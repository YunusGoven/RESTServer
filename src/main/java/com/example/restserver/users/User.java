package com.example.restserver.users;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Builder
public class User {

    @NotNull(message = "Login must not be null")
    @NotBlank(message="Login must not be blank")
    @Size(min=3, max=10, message="Login will have this number of characters [3-10]")
    private String login;

    @NotNull(message = "Password must not be null")
    @NotBlank(message="Password must not be blank")
    @Size(min=6, max=999, message="Password will have this number of characters : min 6")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}
