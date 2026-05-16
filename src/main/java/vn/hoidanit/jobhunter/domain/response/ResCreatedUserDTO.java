package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResCreatedUserDTO {

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private int age;
    private String address;
    private Instant createdAt;
    private CompanyUser company;
}
