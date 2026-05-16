package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUpdatedUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private int age;
    private String address;
    private Instant updatedAt;
    private CompanyUser company;


    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}
