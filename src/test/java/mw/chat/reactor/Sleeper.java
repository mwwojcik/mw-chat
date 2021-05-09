package mw.chat.reactor;

public class Sleeper {
public static void sleepSecconds(int sec) {
    try {
        Thread.sleep(sec*1000);
    } catch (InterruptedException e) {
        throw new IllegalArgumentException(e);
    }
}

}
