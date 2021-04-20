package mw.chat.web;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

class MessagesTopicHandlerTest {

    private List<String> messages = new CopyOnWriteArrayList<String>();
    private Flux myFlux=Flux.fromIterable(messages);



    @DisplayName("Should react on insert to messages")
    @Test
    void shouldReactOnInsertToMessages()
        throws InterruptedException {
     myFlux.doOnNext(this::sayHello).subscribe();
     messages.add("hello");
     messages.add("hello");
     messages.add("hello");
     messages.add("hello");
     messages.add("hello");
     messages.add("hello");
     Thread.sleep(1000);

     }

    private void sayHello(Object o) {
        System.out.println("====="+o);
    }


   /* public static class ObservableList<T> {

        protected final List<T> list;
        protected final PublishSubject<T> onAdd;

        public ObservableList() {
            this.list = new ArrayList<T>();
            this.onAdd = PublishSubject.create();
        }
        public void add(T value) {
            list.add(value);
            onAdd.onNext(value);
        }
        public Observable<T> getObservable() {
            return onAdd;
        }
    }*/
}