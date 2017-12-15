package sauce.core;

import sauce.util.structures.Vector2D;

public interface CameraChangeSubscriber {
    void cameraResized(Vector2D newSize);
    void cameraChanged(Camera newCamera);
    void cameraMovedPosition(Vector2D deltaPosition);
}
