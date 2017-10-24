package sauce.core.scene;

import sauce.core.engine.Camera;
import util.Vector2D;

public interface CameraChangeSubscriber {
    void cameraResized(Vector2D newSize);
    void cameraChanged(Camera newCamera);
    void cameraMovedPosition(Vector2D deltaPosition);
}
