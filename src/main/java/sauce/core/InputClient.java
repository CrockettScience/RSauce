package sauce.core;

/**
 * Created by John Crockett.
 */
public interface InputClient {

    default void receivedKeyEvent(InputEvent event){

    }

    default void receivedTextEvent(char character){

    }

    default void receivedMouseButtonEvent(InputEvent event){

    }

    default void mouseScrolled(double x, double y){

    }

    default void cursorPosChanged(double x, double y){

    }

    default void joystickConnected(int joyID){

    }

    default void joystickDisconnected(int joyID){

    }


}
