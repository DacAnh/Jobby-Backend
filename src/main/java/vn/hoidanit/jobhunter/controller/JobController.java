package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(job.getId());
        if(jobOptional.isEmpty()){
            throw new IdInvalidException("Job id =" + job.getId() + " không tồn tại");
        }
        return ResponseEntity.ok()
                .body(this.jobService.update(job, jobOptional.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void>  delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        if(jobOptional.isEmpty()){
            throw new IdInvalidException("Job not found: " + id);
        }

        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }
    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch a job")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        if(jobOptional.isEmpty()){
            throw new IdInvalidException("Job not found: " + id);
        }

        return ResponseEntity.ok(jobOptional.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable
            ){
        return ResponseEntity.ok().body(this.jobService.fetchAll(spec, pageable));
    }
}
