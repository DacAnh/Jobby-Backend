package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {

        if(this.roleService.existsByName(role.getName())){
            throw new IdInvalidException("Role với name =" + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInvalidException {

//      Kiểm tra Id
        if(this.roleService.fetchById(role.getId()) ==null){
            throw new IdInvalidException("Role với id =" + role.getId() + "không tồn tại");
        }

//      Kiểm tra name
//        if(this.roleService.existsByName(role.getName())){
//            throw new IdInvalidException("Role với name =" + role.getName() + " đã tồn tại");
//        }

        return ResponseEntity.ok().body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if(this.roleService.fetchById(id) ==null){
            throw new IdInvalidException("Role với id =" + id + "không tồn tại");
        }

        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable){
        return ResponseEntity.ok(this.roleService.getRoles(spec,pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.fetchById(id);
        if(role == null){
            throw new IdInvalidException("Resume với id = "+ id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role);
    }
}
