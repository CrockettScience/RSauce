package com.sauce.input;

/**
 * Created by John Crockett.
 */
public interface InputClient {

    void receivedKeyEvent(InputEvent event);
    void receivedTextEvent(char character);
    void receivedMouseButtonEvent(InputEvent event);
    void mouseScrolled(double x, double y);
    void cursorPosChanged(double x, double y);
    void joystickConnected(int joyID);
    void joystickDisconnected(int joyID);

}
