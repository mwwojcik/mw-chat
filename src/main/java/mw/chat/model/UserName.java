package mw.chat.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "username")
public class UserName {

    private String username;

    public static UserName from(String username) {
        return new UserName(username);
    }


}
