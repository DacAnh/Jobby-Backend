package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private  final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) throws IdInvalidException {

//      Kiểm tra tồn tại theo ID
        if (this.permissionService.fetchById(permission.getId())==null){
            throw new IdInvalidException("Permission với id= "+ permission.getId() + "không tồn tại");
        }

//      Kiểm tra tồn tại theo module, apiPath, và method
//      (không thích! vì vậy mới comment đoạn code này)
//        if (this.permissionService.isPermissionExist(permission)){
//            if(this.permissionService.isSameName(permission)){
//                throw new IdInvalidException("Permission đã tồn tại");
//            }
//        }

        return ResponseEntity.ok().body(this.permissionService.update(permission));
    }

    @DeleteMapping("/permissions")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if (this.permissionService.fetchById(id) == null){
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại");
        }

        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> spec, Pageable pageable)
    {
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
}
