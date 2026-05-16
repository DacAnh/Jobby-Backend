package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository,
                      SkillRepository skillRepository,
                      CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO create(Job newJob){
        if(newJob.getSkills()!=null){
            List<Long> reqSkills = newJob.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> skills = this.skillRepository.findAllById(reqSkills);
            newJob.setSkills(skills);
        }

//      Kiểm tra cty
        if(newJob.getCompany()!=null){
            Optional<Company> companyOptional = this.companyRepository.findById(newJob.getCompany().getId());
            if(companyOptional.isPresent()){
                newJob.setCompany(companyOptional.get());
            }
        }
        Job job = this.jobRepository.save(newJob);

        return this.convertToResCreateJobDTO(job);
    }

    public ResUpdateJobDTO update(Job job, Job jobInDb){
        if(job.getSkills()!=null){
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findAllById(reqSkills);
            jobInDb.setSkills(skills);
        }

//      Kiểm tra cty
        if(job.getCompany()!=null){
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if(companyOptional.isPresent()){
                jobInDb.setCompany(companyOptional.get());
            }
        }

//      Cập nhật thông tin
        jobInDb.setName(job.getName());
        jobInDb.setDescription(job.getDescription());
        jobInDb.setStartDate(job.getStartDate());
        jobInDb.setEndDate(job.getEndDate());
        jobInDb.setSalary(job.getSalary());
        jobInDb.setQuantity(job.getQuantity());
        jobInDb.setLocation(job.getLocation());
        jobInDb.setLevel(job.getLevel());
        jobInDb.setActive(job.isActive());


        Job currentJob = this.jobRepository.save(jobInDb);
        return this.convertToResUpdateJobDTO(currentJob);
    }

    public void delete(long id){
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable){
        Page<Job> pageUser = this.jobRepository.findAll(spec,pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageUser.getNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageUser.getContent());

        return resultPaginationDTO;
    }

    private ResCreateJobDTO convertToResCreateJobDTO(Job job){
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(job.getId());
        resCreateJobDTO.setName(job.getName());
        resCreateJobDTO.setActive(job.isActive());
        resCreateJobDTO.setDescription(job.getDescription());
        resCreateJobDTO.setLocation(job.getLocation());
        resCreateJobDTO.setSalary(job.getSalary());
        resCreateJobDTO.setQuantity(job.getQuantity());
        resCreateJobDTO.setLevel(job.getLevel().toString());
        resCreateJobDTO.setStartDate(job.getStartDate());
        resCreateJobDTO.setEndDate(job.getEndDate());

//      Thêm list Skill
        List<String> skills = job.getSkills()
                .stream().map(item -> item.getName())
                .collect(Collectors.toList());
        resCreateJobDTO.setSkill(skills);
        return resCreateJobDTO;
    }
    private ResUpdateJobDTO convertToResUpdateJobDTO(Job job){
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(job.getId());
        resUpdateJobDTO.setName(job.getName());
        resUpdateJobDTO.setActive(job.isActive());
        resUpdateJobDTO.setDescription(job.getDescription());
        resUpdateJobDTO.setLocation(job.getLocation());
        resUpdateJobDTO.setSalary(job.getSalary());
        resUpdateJobDTO.setQuantity(job.getQuantity());
        resUpdateJobDTO.setLevel(job.getLevel().toString());
        resUpdateJobDTO.setStartDate(job.getStartDate());
        resUpdateJobDTO.setEndDate(job.getEndDate());

//      Thêm list Skill
        List<String> skills = job.getSkills()
                .stream().map(item -> item.getName())
                .collect(Collectors.toList());
        resUpdateJobDTO.setSkill(skills);
        return resUpdateJobDTO;
    }
}
