package com.sauce.asset.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;

public class Effect extends Audio {

    public Effect(String fileSource) {
        super(fileSource);
    }

    @Override
    protected boolean update() {
        int processed = alGetSourcei(getSource(), AL_BUFFERS_PROCESSED);

        for (int i = 0; i < processed; i++) {
            int buffer = alSourceUnqueueBuffers(getSource());

            if (!stream(buffer)) {
                removeMe();
            }
        }

        if (processed == 2) {
            alSourcePlay(getSource());
        }

        return true;
    }
}
