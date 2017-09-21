package com.sauce.asset.audio;

import com.sauce.util.io.AudioUtil;
import com.util.RSauceLogger;
import com.util.structures.nonsaveable.Queue;
import com.util.structures.nonsaveable.Set;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioThread extends Thread {

    private static long audioDevice;
    private static long audioContext;

    private static AudioThread singletonAudioThread;
    private static Queue<OpenALPlayEntry> audioQueue = new Queue<>();
    private static boolean audioQueueIsBeingAccessed = false;
    private static boolean audioRunning = true;
    private static boolean clearAudio = false;
    private static Set<OpenALPlayEntry> loadedAudio = new Set<>();

    private AudioThread(){}

    public static AudioThread getAudioThread(){
        if(singletonAudioThread == null){
            singletonAudioThread = new AudioThread();
        }

        return singletonAudioThread;
    }

    @Override
    public void run() {
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

        try {
            while (audioRunning) {
                // update all loaded audio
                for (OpenALPlayEntry entry : loadedAudio) {
                    if (!entry.audio.update() || entry.audio.shouldBeRemoved()) {
                        loadedAudio.remove(entry);
                        entry.dispose();
                    }
                }

                if (clearAudio) {
                    loadedAudio.clear();
                    clearAudio = false;
                }

                // check the audio queue
                if (!audioQueue.isEmpty()) {
                    getQueueToken();

                    OpenALPlayEntry entry = audioQueue.dequeue();
                    entry.audio.loadAudio();
                    if (entry.play())
                        loadedAudio.add(entry);
                    else
                        RSauceLogger.printErrorln("Audio Playback Failed");


                    returnQueueToken();
                }
            }
        } finally {
            alcSetThreadContext(NULL);
            alcDestroyContext(audioContext);
            alcCloseDevice(audioDevice);
        }
    }

    public static void enqueue(Audio a){
        a.addme();
        getQueueToken();

        audioQueue.enqueue(new OpenALPlayEntry(a));

        returnQueueToken();
    }

    public static void remove(Audio a){
        a.removeMe();
    }

    public static void clear(){
        clearAudio = true;
    }

    private static synchronized void getQueueToken(){
        if(!audioQueueIsBeingAccessed)
            audioQueueIsBeingAccessed = true;
        else {
            try {
                singletonAudioThread.waitThreads();
                audioQueueIsBeingAccessed = true;
            } catch (InterruptedException e) {
                throw new RuntimeException("Audio Thread was interrupted");
            }
        }
    }

    private static void returnQueueToken(){
        audioQueueIsBeingAccessed = false;
        singletonAudioThread.notifyThreads();
    }

    private synchronized void notifyThreads(){
        notify();
    }

    private synchronized void waitThreads() throws InterruptedException {
        wait();
    }

    public static void killAudioThread(){
        audioRunning = false;
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
            alDeleteBuffers(buffer);
        }
    }
}
