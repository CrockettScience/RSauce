package com.sauce.asset.audio;

import com.sauce.core.Project;

import java.io.IOException;
import java.nio.IntBuffer;

import static com.sauce.util.io.ResourceUtil.*;
import static com.sauce.util.io.AudioUtil.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.stb.STBVorbis.*;

public abstract class Audio {

    private String fileName;

    private IOAudio audio;
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

        samplesLeft = audio.getAudioInfo().getLengthSamples();
    }

    protected boolean stream(int buffer) {
        AudioInfo info = audio.getAudioInfo();

        int samples = 0;

        while (samples < Project.AUDIO_BUFFER_SIZE) {
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

    protected float getProgress() {
        return 1.0f - samplesLeft / (float) (audio.getAudioInfo().getLengthSamples());
    }

    protected float getProgressTime(float progress) {
        return progress * audio.getAudioInfo().getLengthSeconds();
    }

    protected void rewind() {
        stb_vorbis_seek_start(audio.getAudioInfo().getHandle());
        samplesLeft = audio.getAudioInfo().getLengthSamples();
    }

    protected void skip(int direction) {
        seek(Math.min(Math.max(0, stb_vorbis_get_sample_offset(audio.getAudioInfo().getHandle()) + direction * audio.getAudioInfo().getSampleRate()), audio.getAudioInfo().getLengthSamples()));
    }

    protected void skipTo(float offset0to1) {
        seek(Math.round(audio.getAudioInfo().getLengthSamples() * offset0to1));
    }

    protected void seek(int sample_number) {
        stb_vorbis_seek(audio.getAudioInfo().getHandle(), sample_number);
        samplesLeft = audio.getAudioInfo().getLengthSamples() - sample_number;
    }

    protected boolean play(int source, IntBuffer buffers) {
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

    public void dispose(){
        stb_vorbis_close(audio.getAudioInfo().getHandle());
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
}
