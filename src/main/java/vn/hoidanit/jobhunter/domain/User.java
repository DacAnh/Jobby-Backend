package vn.hoidanit.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Password không được để trống")
    private String password;

    private int age;

//  Lưu giá trị của gender xuống Db dưới dạng String
//  Vì Db không hiểu được kiểu giá trị GenderEnum
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company  company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void beforeCreateUser(){
        this.setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void beforeUpdateUser(){
        this.setUpdatedAt(Instant.now());
    }
}
