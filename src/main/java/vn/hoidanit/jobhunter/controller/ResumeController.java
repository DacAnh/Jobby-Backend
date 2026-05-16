package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService, FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if(!isIdExist){
            throw new IdInvalidException("User id/job id không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if(reqResumeOptional.isEmpty()){
            throw new IdInvalidException("Resume với id = "+ resume.getId() + "không tồn tại");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> delete(@PathVariable Integer id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if(reqResumeOptional.isEmpty()){
            throw new IdInvalidException("Resume với id = " + id +" không tồn tại");
        }
        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvalidException{
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if(reqResumeOptional.isEmpty()){
            throw new IdInvalidException("Resume với id = " + id +" không tồn tại");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(reqResumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
//  Cần filter theo role của user nên cần thêm điều kiện khi getAll, check xem user đó thuộc c.ty nào
//  thì chỉ query data theo c.ty đó
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> specification,
            Pageable pageable){

        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.fetchOneUserByEmail(email);
        if(currentUser != null){
            Company userCompany = currentUser.getCompany();
            if(userCompany != null){
                List<Job> companyJobs = userCompany.getJobs();
                if(companyJobs != null && companyJobs.size() > 0){
                    arrJobIds = companyJobs.stream().map(x -> x.getId()).collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());
        Specification<Resume> finalSpec = jobInSpec.and(specification);
        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec,pageable));
    }
    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable){
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
