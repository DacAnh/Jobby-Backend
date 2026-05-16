package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission){
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
    }

    public Permission fetchById(long id){
        Optional<Permission> permission = permissionRepository.findById(id);
        if(permission.isPresent()){
            return permission.get();
        }
        return null;
    }

    public Permission create(Permission permission){
        return permissionRepository.save(permission);
    }

    public Permission update(Permission permission){
        Permission permissionDb = this.fetchById(permission.getId());
        if(permissionDb != null){
            permissionDb.setName(permission.getName());
            permissionDb.setApiPath(permission.getApiPath());
            permissionDb.setMethod(permission.getMethod());
            permissionDb.setModule(permission.getModule());

//          Cập nhật
            permissionDb = this.permissionRepository.save(permissionDb);
            return permissionDb;
        }
        return null;
    }

    public Void delete(long id){

//      Xóa quyền tại bảng permission_role
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission currentPermission = permission.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

//      Xóa quyền
        permissionRepository.deleteById(id);
        return null;
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable){
        Page<Permission> permissions = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(permissions.getTotalPages());
        meta.setTotal(permissions.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(permissions.getContent());

        return dto;
    }
}
