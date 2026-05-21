package vn.hoidanit.jobhunter.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLoginDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginDTO{
        private long id;
        private String email;
        private String name;
        private Role role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount{
        private UserLoginDTO user;
        private int age;
        private String gender;
        private String address;
    }

    @JsonProperty("access_token")
    private String accessToken;

    private UserLoginDTO user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken{
        private long id;
        private String email;
        private String name;
    }
}
