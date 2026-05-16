package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;


import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;

    private String name;
    private String location;
    private double salary;
    private int quantity;
    private String level;
    private String description;

    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private List<String> skill;

}
