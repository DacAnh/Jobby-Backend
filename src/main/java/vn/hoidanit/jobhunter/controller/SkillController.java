package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if(skill.getName()!=null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill name =" + skill.getName() +" đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
        if(currentSkill==null){
            throw new IdInvalidException("Skill id =" + skill.getId() + " không tồn tại");
        }

        if(skill.getName()!=null && this.skillService.isNameExist(skill.getName())){
            throw new IdInvalidException("Skill id=" + skill.getName() + " đã tồn tại");
        }

        currentSkill.setName(skill.getName());
        return ResponseEntity.ok(this.skillService.updateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Skill> spec,
            Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK).body(
                this.skillService.fetchAllSkills(spec, pageable)
        );
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if(currentSkill==null){
            throw new IdInvalidException("Skill id= "+id + " không tồn tại");
        }

        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().build();
    }

}
