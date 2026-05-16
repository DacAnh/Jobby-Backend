package vn.hoidanit.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Tên không được để trống")
    private  String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private  String description;
    private String address;
    private  String logo;

//  format ngày giờ thẳng về client, trong db thì vẫn là GMT+7
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a",timezone = "GMT+7")
    private Instant createdAt;
    //  format ngày giờ thẳng về client, trong db thì vẫn là GMT+7
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a",timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    @OneToMany( mappedBy = "company", fetch =  FetchType.LAZY)
//  Giải quyết vấn đề loop vô hạn bằng cách ko trả ra DS user khi lấy DS cty
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "company", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

//  Cấu hình thực hiện hành động trước khi lưu data xuống Db
//  Vì mỗi lần tạo data là phải thiết lập setCreatedBy,... ở phần service.
//  Nếu entity nào cũng phải set như vậy thì rất cực => Mỗi khi tạo entity thì nên đưa hành động vào
//  Các hành động như PrePersist, PostPersist, PreRemove,.... thì cần phải được
//  "trang trí" tại hàm void, và không có dữ liệu đầu vào
    @PrePersist
    public void beforeCreateCompany(){
        this.setCreatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "");
        this.setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void beforeUpdateCompany(){
        this.setUpdatedBy(SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "");
        this.setUpdatedAt(Instant.now());
    }
}
