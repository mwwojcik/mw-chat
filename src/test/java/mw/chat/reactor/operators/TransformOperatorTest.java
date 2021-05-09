package mw.chat.reactor.operators;

import com.github.javafaker.Faker;
import java.util.Locale;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class TransformOperatorTest {

    /*
    * Transform could be treated as a custom operator. It can transform item in custom way.
    * */

    @DisplayName("Should transform stream signal one to another")
    @Test
    void shouldTransformStreamSignalOneToAnother() {
        persons().transform(applyTransformOnlyForSomePersons()).subscribe(DefaultSimpleSubscriber.create());
     }

    private Function<Flux<Person>,Flux<Person>> applyTransformOnlyForSomePersons(){
        return flux ->
            flux.filter(it->it.getAge()>10).doOnNext(it->it.setName(it.getName().toUpperCase())).doOnDiscard(Person.class,
                                                                                                             p-> System.out.println(
                                                                                                                 String.format(
                                                                                                                     "Discarded"
                                                                                                                         + "=>%s", p)));
    }

    private Flux<Person> persons(){
        return Flux.range(1, 20).map(it -> Person.create());
    }
}

@Setter
class Person{
    private static Faker faker=new Faker();

    private String name ;
    private int age;

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person create(){
            return new Person(faker.name().firstName(),faker.random().nextInt(1,30));
        }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("%s=>%s", name,age);
    }
}