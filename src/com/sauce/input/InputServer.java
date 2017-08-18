package com.sauce.input;

import com.sauce.core.Main;
import com.structures.nonsaveable.ArrayList;

/**
 * Created by John Crockett.
 */
public class InputServer {

    private static ArrayList<InputClient> subscribers = new ArrayList<>();

    public static void recieveRawInputEvent(Main.RawInputEvent event){
        for(int i = 0; i < subscribers.size(); i++){
            subscribers.get(i).receivedInputEvent(new InputEvent(event.key(), event.action()));
        }
    }

    public static void bind(InputClient sub){
        subscribers.add(sub);
    }
}
