package sauce.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import sauce.concurrent.Argument;
import sauce.concurrent.Return;
import sauce.concurrent.Script;
import sauce.concurrent.Concurrent;
import sauce.util.RSauceLogger;
import sauce.util.structures.nonsaveable.Map;
import sauce.util.structures.nonsaveable.Queue;
import sauce.util.structures.nonsaveable.Set;
import sauce.util.structures.threadsafe.ThreadSafeQueue;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioManager extends Thread {

    private static long audioDevice;
    private static long audioContext;

    private static AudioManager singletonAudioThread;
    private static boolean audioRunning = false;
    private static Set<OpenALPlayEntry> loadedAudio = new Set<>();
    private static ThreadSafeQueue<Script<?, ?>> scriptQueue = new ThreadSafeQueue<>();
    private static Queue<OpenALPlayEntry> removalQueue = new Queue<>();

    // Gains
    private static float master = 1.0f;
    private static float sfx = 1.0f;
    private static float music = 1.0f;
    private static Set<Music> musicVolSet = new Set<>();

    private AudioManager(){}

    public static AudioManager getAudioThread(){
        if(singletonAudioThread == null){
            singletonAudioThread = new AudioManager();
        }

        return singletonAudioThread;
    }

    @Override
    public void run() {
        if(audioRunning){
            RSauceLogger.printWarningln("AudioThread is already running.");
            return;
        }

        audioRunning = true;

        audioDevice = alcOpenDevice((ByteBuffer) null);
        if (audioDevice == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }

        ALCCapabilities deviceCaps = ALC.createCapabilities(audioDevice);

        audioContext = alcCreateContext(audioDevice, (IntBuffer) null);
        if (audioContext == NULL) {
            throw new IllegalStateException("Failed to create an OpenAL context.");
        }

        alcSetThreadContext(audioContext);
        AL.createCapabilities(deviceCaps);

        alListenerfv(AL_ORIENTATION, (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind());

        try {
            while (audioRunning) {
                // Execute operations
                while(!scriptQueue.isEmpty()){
                    scriptQueue.dequeue().execute(null);
                }

                // Update all loaded audio streams
                for (OpenALPlayEntry entry : loadedAudio) {
                    if (!entry.audio.update() || entry.audio.shouldBeRemoved()) {
                        removalQueue.enqueue(entry);
                    }
                }

                // Remove finished and cleared audio
                while(!removalQueue.isEmpty()) {
                    OpenALPlayEntry entry = removalQueue.dequeue();
                    loadedAudio.remove(entry);
                    entry.dispose();
                }
            }
        } finally {
            alcSetThreadContext(NULL);
            alcDestroyContext(audioContext);
            alcCloseDevice(audioDevice);

            if(audioRunning){
                Concurrent.requestMainThread(new Script<Argument, Return>() {
                    @Override
                    protected Return scriptMain(Argument args) {
                        AudioManager.getAudioThread().start();
                        return null;
                    }
                }, null);
            }
        }
    }

    public static void enqueue(Music m){
        scriptQueue.enqueue(new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                OpenALPlayEntry entry = new OpenALPlayEntry(m);
                AudioCache.getCachedAudio(entry.audio);

                if (entry.play()) {
                    loadedAudio.add(entry);
                    alSourcef(m.getSource(), AL_GAIN, music);
                    musicVolSet.add(m);
                }
                else
                    RSauceLogger.printErrorln("Audio Playback Failed");

                return null;
            }
        });
    }

    public static void enqueue(Noise e){
        scriptQueue.enqueue(new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                OpenALPlayEntry entry = new OpenALPlayEntry(e);
                AudioCache.getCachedAudio(entry.audio);

                if (entry.play()) {
                    loadedAudio.add(entry);
                    alSourcef(e.getSource(), AL_GAIN, sfx);
                }
                else
                    RSauceLogger.printErrorln("Audio Playback Failed");


                return null;
            }
        });
    }

    public static void remove(Audio a){
        a.removeMe();
    }

    public static void clear(){
        scriptQueue.enqueue(new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                for (OpenALPlayEntry entry : loadedAudio)
                    removalQueue.enqueue(entry);

                return null;
            }
        });
    }

    public static void setVolMaster(float vol){
        master = vol >= 0.0f ? vol <= 1.0f ? vol : 1.0f : 0.0f;
        scriptQueue.enqueue(new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                alListenerf(AL_GAIN, master);
                return null;
            }
        });
    }

    public static void setVolSFX(float vol){
        sfx = vol >= 0.0f ? vol <= 1.0f ? vol : 1.0f : 0.0f;
    }

    public static void setVolMusic(float vol){
        music = vol >= 0.0f ? vol <= 1.0f ? vol : 1.0f : 0.0f;
        scriptQueue.enqueue(new Script<Argument, Return>(){

            @Override
            protected Return scriptMain(Argument args) {
                for(Music m : musicVolSet){
                    alSourcef(m.getSource(), AL_GAIN, music);
                }
                return null;
            }
        });
    }

    public static float getVolMaster(){
        return master;
    }

    public static float getVolSFX(){
        return sfx;
    }

    public static float getVolMusic(){
        return music;
    }

    public static void killAudioThread(){
        audioRunning = false;
    }

    public static void clearAudioCache(){
        AudioCache.clear();
    }

    private static class OpenALPlayEntry{
        private IntBuffer buffer;
        private Audio audio;

        private OpenALPlayEntry(Audio a){
            audio = a;
        }

        private boolean play(){
            buffer = BufferUtils.createIntBuffer(audio.getIOAudio().getAudioInfo().getChannels());
            alGenBuffers(buffer);

            return audio.play(buffer);
        }

        private void dispose(){
            audio.dispose();
            alDeleteBuffers(buffer);
            buffer = null;
            audio = null;
        }
    }

    private static class AudioCache{
        private static Map<String, AudioUtil.IOAudio> cache = new Map<>();

        private static void getCachedAudio(Audio a){
            if (cache.containsKey(a.getFileName())) {
                a.loadAudio(cache.get(a.getFileName()));
                a.rewind();
            }
            else{
                a.loadAudio();
                cache.put(a.getFileName(), a.getIOAudio());
            }
        }

        private static void clear(){
            for(AudioUtil.IOAudio audio : cache.valueSet()){
                stb_vorbis_close(audio.getAudioInfo().getHandle());
            }

            cache.clear();
        }
    }
}
