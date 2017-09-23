package com.sauce.asset.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;

public class Music extends Audio {

    private int loopTagSample;
    private float loopTagTime;

    public Music(String fileSource, float loopTagSeconds) {
        super(fileSource);

        loopTagTime = loopTagSeconds;
    }

    @Override
    protected void loadAudio() {
        super.loadAudio();

        int samples = getIOAudio().getAudioInfo().getLengthSamples();
        float seconds = getIOAudio().getAudioInfo().getLengthSeconds();

        loopTagSample = (int)Math.floor((loopTagTime / seconds) * samples);
    }

    @Override
    protected boolean update() {
        int processed = alGetSourcei(getSource(), AL_BUFFERS_PROCESSED);

        for (int i = 0; i < processed; i++) {
            int buffer = alSourceUnqueueBuffers(getSource());

            if (!stream(buffer)) {
                seek(loopTagSample);

                if (!stream(buffer)) {
                    return false;
                }
            }
            alSourceQueueBuffers(getSource(), buffer);
        }

        if (processed == 2) {
            alSourcePlay(getSource());
        }

        return true;
    }

    private float getProgress() {
        return 1.0f - getSamplesLeft() / (float) (getIOAudio().getAudioInfo().getLengthSamples());
    }

    private float getProgressTime(float progress) {
        return progress * getIOAudio().getAudioInfo().getLengthSeconds();
    }

    public float getElapsedTime() {
        return getProgressTime(getProgress());
    }

    public void rewind(){
        super.rewind();
    }

    public void seek(float seekTime){
        int samples = getIOAudio().getAudioInfo().getLengthSamples();
        float seconds = getIOAudio().getAudioInfo().getLengthSeconds();

        seek((int)Math.floor((seekTime / seconds) * samples));
    }
}
