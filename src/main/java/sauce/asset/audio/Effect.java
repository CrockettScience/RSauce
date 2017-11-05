package sauce.asset.audio;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.openal.AL10.*;

public class Effect extends Audio {

    private AtomicBoolean stopped = new AtomicBoolean();

    public Effect(String fileSource) {
        super(fileSource);

        stopped.set(false);
    }

    @Override
    protected boolean update() {
        checkDisposed();
        int processed = alGetSourcei(getSource(), AL_BUFFERS_PROCESSED);

        for (int i = 0; i < processed; i++) {
            int buffer = alSourceUnqueueBuffers(getSource());

            if (!stream(buffer) || stopped.get()) {
                removeMe();
            }
        }

        if (processed == 2 && !stopped.get()) {
            alSourcePlay(getSource());
        }

        return true;
    }

    public void stop(){
        stopped.set(true);
    }
}
