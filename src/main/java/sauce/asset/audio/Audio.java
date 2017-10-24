package sauce.asset.audio;

import sauce.core.Preferences;
import sauce.util.misc.AssetDisposedException;
import sauce.util.misc.Disposable;

import java.io.IOException;
import java.nio.IntBuffer;

import static sauce.util.io.ResourceUtil.*;
import static sauce.util.io.AudioUtil.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.stb.STBVorbis.*;

public abstract class Audio implements Disposable{
    private boolean disposed = false;

    private String fileName;

    private IOAudio audio;
    private int source;
    private int samplesLeft;
    private boolean removeMe = false;

    public Audio(String fileSource){
        fileName = fileSource;
    }

    protected void loadAudio(){
        IOResource resource;
        try {
            resource = loadResource(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Ogg Vorbis file.");
        }

        audio = ioResourceToAudio(resource, getAudioInfo(resource));
        source = alGenSources();

        samplesLeft = audio.getAudioInfo().getLengthSamples();
    }

    protected void loadAudio(IOAudio cachedAudio){
        audio = cachedAudio;

        source = alGenSources();

        samplesLeft = audio.getAudioInfo().getLengthSamples();
    }

    public String getFileName(){
        return fileName;
    }

    protected boolean stream(int buffer) {
        if(disposed)
            throw new AssetDisposedException(this);

        AudioInfo info = audio.getAudioInfo();

        int samples = 0;

        while (samples < Preferences.AUDIO_BUFFER_SIZE) {
            info.getPcm().position(samples);
            int samplesPerChannel = stb_vorbis_get_samples_short_interleaved(info.getHandle(), info.getChannels(), info.getPcm());
            if (samplesPerChannel == 0) {
                break;
            }

            samples += samplesPerChannel * audio.getAudioInfo().getChannels();
        }

        if (samples == 0) {
            return false;
        }

        info.getPcm().position(0);
        alBufferData(buffer, info.getFormat(), info.getPcm(), info.getSampleRate());
        samplesLeft -= samples / info.getChannels();

        return true;
    }

    protected void rewind() {
        if(disposed)
            throw new AssetDisposedException(this);

        stb_vorbis_seek_start(audio.getAudioInfo().getHandle());
        samplesLeft = audio.getAudioInfo().getLengthSamples();
    }

    protected void seek(int sample_number) {
        if(disposed)
            throw new AssetDisposedException(this);

        stb_vorbis_seek(audio.getAudioInfo().getHandle(), sample_number);
        samplesLeft = audio.getAudioInfo().getLengthSamples() - sample_number;
    }

    protected boolean play(IntBuffer buffers) {
        if(disposed)
            throw new AssetDisposedException(this);

        for (int i = 0; i < buffers.limit(); i++) {
            if (!stream(buffers.get(i))) {
                return false;
            }
        }

        alSourceQueueBuffers(source, buffers);
        alSourcePlay(source);

        return true;
    }

    protected abstract boolean update();

    protected int getSource(){
        return source;
    }

    public void dispose(){
        alDeleteSources(source);
        disposed = true;
    }

    boolean shouldBeRemoved() {
        return removeMe;
    }

    void removeMe() {
        removeMe = true;
    }

    void addme(){
        removeMe = false;
    }

    IOAudio getIOAudio(){
        return audio;
    }

    protected int getSamplesLeft() {
        return samplesLeft;
    }

    protected void checkDisposed(){
        if(disposed)
            throw new AssetDisposedException(this);

    }
}
