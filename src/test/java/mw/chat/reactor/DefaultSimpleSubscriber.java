package mw.chat.reactor;

import lombok.NoArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DefaultSimpleSubscriber implements Subscriber<Object> {

    private String name="" ;

    public DefaultSimpleSubscriber() {
    }

    public DefaultSimpleSubscriber(String name) {
        this.name ="=> "+ name;
    }

    public static DefaultSimpleSubscriber create() {
       return new DefaultSimpleSubscriber(DefaultSimpleSubscriber.class.getSimpleName());
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        System.out.println(String.format("%s Received : %s",name,o));
    }

    @Override
    public void onError(Throwable t) {
        System.out.println(String.format("%s Error : %s",name, t));
    }

    @Override
    public void onComplete() {
        System.out.println(String.format("%s Completed", name));
    }
}
