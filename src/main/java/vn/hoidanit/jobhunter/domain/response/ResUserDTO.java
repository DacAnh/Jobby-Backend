package vn.hoidanit.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private CompanyUser company;
    private RoleUser role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser{
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser{
        private long id;
        private String name;
    }

}
