package sauce.concurrent;

import sauce.core.Engine;
import sauce.core.Main;

public class Concurrent {

    public static <R extends Return, A extends Argument> R requestMainThreadAndWait(Script<A, R> request, A a){
        if(Thread.currentThread() == Main.getMainThread())
            return request.execute(a);

        R[] pointer = (R[]) new Return[1];

        R noneYet = (R) new Return();
        pointer[0] = noneYet;

        Object lock = new Object();

        Engine.engueueScript(new Script<A, Return>() {
            @Override
            protected Return scriptMain(Argument args) {
                synchronized (lock) {
                    pointer[0] = request.execute(a);
                    lock.notify();
                    return null;
                }
            }
        }, a);

        synchronized (lock) {
            while (pointer[0] == noneYet) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        return pointer[0];
    }

    public static <A extends Argument> void requestMainThread(Script<A, Return> request, A a){
        if(Thread.currentThread() == Main.getMainThread())
            request.execute(a);

        Engine.engueueScript(request, a);
    }

}
