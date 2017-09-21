package com.sauce.asset.audio;

import static com.sauce.asset.audio.AudioThread.getSource;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;

public class Music extends Audio {

    public Music(String fileSource) {
        super(fileSource);
    }

    @Override
    protected boolean update() {
        int processed = alGetSourcei(getSource(), AL_BUFFERS_PROCESSED);

        for (int i = 0; i < processed; i++) {
            int buffer = alSourceUnqueueBuffers(getSource());

            if (!stream(buffer)) {
                rewind();

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
}
