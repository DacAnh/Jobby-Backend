package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreatedUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdatedUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User fetchOneUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    public User fetchOneUserByEmail(String email){
        return this.userRepository.findOneByEmail(email);
    }

//    public ResultPaginationDTO fetchAllUser(Pageable pageable){
    public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable){
        Page<User> pageUser =this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(meta);

//      Chuyển dữ liệu
        List<ResUserDTO> resUserDTOS = pageUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
//        List<ResUserDTO> resUserDTOS = new ArrayList<>();
//        for(User user : pageUser.getContent()){
//            ResUserDTO resUserDTO = new ResUserDTO();
//            ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
//            resUserDTO.setId(user.getId());
//            resUserDTO.setAddress(user.getAddress());
//            resUserDTO.setName(user.getName());
//            resUserDTO.setEmail(user.getEmail());
//            resUserDTO.setGender(user.getGender());
//            resUserDTO.setAge(user.getAge());
//            resUserDTO.setCreatedAt(user.getCreatedAt());
//            resUserDTO.setUpdatedAt(user.getUpdatedAt());
//
//            company.setId(user.getCompany() !=null ? user.getCompany().getId():0);
//            company.setName(user.getCompany() !=null ? user.getCompany().getName():null);
//            resUserDTO.setCompany(company);
//
//            resUserDTOS.add(resUserDTO);
//        }
//        resultPaginationDTO.setResult(resUserDTOS);

        resultPaginationDTO.setResult(resUserDTOS);
        return resultPaginationDTO;
    }

    public User handleCreateUser(User user){

//      Check company
        if(user.getCompany()!=null){
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent()?companyOptional.get():null);
        }

//      Check role
        if(user.getRole()!=null){
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role !=null? role:null);
        }

        return this.userRepository.save(user);

//        User createdUser = this.userRepository.save(user);
//
//        ResCreatedUserDTO resUserDTO = new ResCreatedUserDTO();
//        resUserDTO.setId(createdUser.getId());
//        resUserDTO.setEmail(createdUser.getEmail());
//        resUserDTO.setName(createdUser.getName());
//        resUserDTO.setAge(createdUser.getAge());
//        resUserDTO.setGender(createdUser.getGender());
//        resUserDTO.setAddress(createdUser.getAddress());
//        resUserDTO.setCreatedAt(createdUser.getCreatedAt());
//        ResCreatedUserDTO.CompanyDTO resCompanyDTO = new ResCreatedUserDTO.CompanyDTO();
//        if(createdUser.getCompany()!=null){
//            resCompanyDTO.setId(createdUser.getCompany().getId());
//        }
//        resUserDTO.setCompany(resCompanyDTO);
//
//        return resUserDTO;
    }

    public User handleUpdateUser(User reqUser){
        User currentUser = this.userRepository.findById(reqUser.getId()).get();
        if(currentUser != null){
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());

//          Check company
            if(reqUser.getCompany()!=null){
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent()?companyOptional.get():null);
            }

//          Check role
            if(reqUser.getRole()!=null){
                Role role = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(role !=null? role:null);
            }

//          Update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }


    public void deletedUser(long id){
        if(this.userRepository.existsById(id)){
            this.userRepository.deleteById(id);
            return;
        }else {
            throw new UsernameNotFoundException("Người dùng không tồn tại");
        }
    }

    public void updateUserToken(String token, String email){
        User currentUser= this.fetchOneUserByEmail(email);
        if(currentUser!=null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByEmailAndRefreshToken(String email, String refreshToken){
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

    public ResCreatedUserDTO convertToResCreatedUserDTO(User user){
        ResCreatedUserDTO res = new ResCreatedUserDTO();
        ResCreatedUserDTO.CompanyUser company = new ResCreatedUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if(user.getCompany()!=null){
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user){
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

//      Chuyển thông tin Company
        if(user.getCompany()!=null){
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

//      Chuyển thông tin Role
        if(user.getRole()!=null){
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public ResUpdatedUserDTO convertToResUpdateUserDTO(User user){
        ResUpdatedUserDTO res = new ResUpdatedUserDTO();
        ResUpdatedUserDTO.CompanyUser company = new ResUpdatedUserDTO.CompanyUser();

        if(user.getCompany()!=null){
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }
}
