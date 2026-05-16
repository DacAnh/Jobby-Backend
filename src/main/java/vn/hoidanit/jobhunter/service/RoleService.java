package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existsByName(String name){
        return this.roleRepository.existsByName(name);
    }

    public Role create(Role role){

//      Kiểm tra các permissions có trong request của role
        if(role.getPermissions() != null){
            List<Long> permissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> permissionsDb = this.permissionRepository.findByIdIn(permissions);
            role.setPermissions(permissionsDb);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id){
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }

    public Role update(Role role){
        Role roleDb = this.fetchById(role.getId());
        if(roleDb.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Permission> permissionsDb = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(permissionsDb);
        }

        roleDb.setName(role.getName());
        roleDb.setDescription(role.getDescription());
        roleDb.setActive(role.isActive());
        roleDb.setPermissions(role.getPermissions());
        roleDb = this.roleRepository.save(roleDb);
        return roleDb;
    }

    public void delete(long id){
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable){
        Page<Role> rolePage = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(rolePage.getTotalPages());
        meta.setTotal(rolePage.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(rolePage.getContent());
        return dto;
    }
}
