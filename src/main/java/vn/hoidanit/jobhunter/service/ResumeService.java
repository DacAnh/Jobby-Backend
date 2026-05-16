package vn.hoidanit.jobhunter.service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Autowired
    FilterBuilder filterBuilder;
    @Autowired
    private FilterParser filterParser;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository, UserRepository userRepository, FilterParser filterParser, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }
    public boolean checkResumeExistByUserAndJob(Resume resume){

//      Kiểm tra user thông qua ID
        if(resume.getUser()==null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if(userOptional.isEmpty())
            return false;

//      Kiểm tra job thông qua ID
        if(resume.getJob()==null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if(jobOptional.isEmpty())
            return false;
        return true;
    }

    public ResCreateResumeDTO create(Resume resume){
        resume = this.resumeRepository.save(resume);

        return this.toResCreateResumeDTO(resume);
    }

    public Optional<Resume> fetchById(long id){
        return this.resumeRepository.findById(id);
    }

    public ResUpdateResumeDTO update(Resume  resume){
        resume = this.resumeRepository.save(resume);

        return this.toResUpdateResumeDTO(resume);
    }

    public void delete(long id){
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO getResume(Resume resume){
        return  this.toResFetchResumeDTO(resume);
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> specification, Pageable pageable){
        Page<Resume> page = this.resumeRepository.findAll(specification,pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        List<ResFetchResumeDTO> listResume = page.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listResume);
        return resultPaginationDTO;
    }

    private ResCreateResumeDTO toResCreateResumeDTO(Resume resume){
        ResCreateResumeDTO dto = new ResCreateResumeDTO();
        dto.setId(resume.getId());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        return dto;
    }

    private ResUpdateResumeDTO toResUpdateResumeDTO(Resume resume){
        ResUpdateResumeDTO dto = new ResUpdateResumeDTO();
        dto.setId(resume.getId());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setUpdatedBy(resume.getUpdatedBy());
        return dto;
    }

    private ResFetchResumeDTO toResFetchResumeDTO(Resume resume){
        ResFetchResumeDTO dto = new ResFetchResumeDTO();
        dto.setId(resume.getId());
        dto.setEmail(resume.getEmail());
        dto.setUrl(resume.getUrl());
        dto.setStatus(resume.getStatus());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setUpdatedBy(resume.getUpdatedBy());

        if(resume.getJob()!=null){
            dto.setCompanyName(resume.getJob().getCompany().getName());
        }
        dto.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(),resume.getUser().getName()));
        dto.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(),resume.getJob().getName()));
        return dto;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable){
//      Tự build query thay vì dùng library
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                :"";
        FilterNode node = filterParser.parse("email='"+email+"'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(page.getContent());
        return resultPaginationDTO;
    }
}
