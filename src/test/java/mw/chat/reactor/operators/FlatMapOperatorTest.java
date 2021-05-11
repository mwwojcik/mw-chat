package mw.chat.reactor.operators;

import com.github.javafaker.Faker;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FlatMapOperatorTest {

    @DisplayName("Should get users and theirs orders")
    @Test
    void shouldGetUsersAndTheirsOrders() {
        UserService.getUsers().flatMap(user -> OrderService.getOrders(user.getId())).subscribe(DefaultSimpleSubscriber.create());
    }


}

@Getter
@ToString
class User {

    private String name;
    private int id;

    public User(int id) {
        this.id = id;
        this.name = new Faker().name().fullName();
    }

}

@Getter
@ToString
class PurchaseOrder {

    private String item;
    private int userId;
    private String price;

    public PurchaseOrder(int userId) {
        var faker = new Faker();
        this.userId = userId;
        this.item = faker.commerce().productName();
        this.price = faker.commerce().price();
    }

}

class OrderService {

    private static Map<Integer, List<PurchaseOrder>> db = new HashMap<>();

    static {
        List<PurchaseOrder> orders1 = Arrays.asList(new PurchaseOrder(1), new PurchaseOrder(1), new PurchaseOrder(1));
        List<PurchaseOrder> orders2 = Arrays.asList(new PurchaseOrder(2), new PurchaseOrder(2), new PurchaseOrder(2));
        List<PurchaseOrder> orders3 = Arrays.asList(new PurchaseOrder(3), new PurchaseOrder(3), new PurchaseOrder(3));
        db.put(1, orders1);
        db.put(2, orders2);
        db.put(3, orders3);
    }

    public static Flux<PurchaseOrder> getOrders(int userId) {
        return Flux.create(purchaseOrderFluxSink -> {
            db.get(userId).forEach(purchaseOrderFluxSink::next);
            purchaseOrderFluxSink.complete();
        });
    }

}

class UserService {

    public static Flux<User> getUsers() {
        return Flux.range(1, 2).map(i -> new User(i));
    }
}