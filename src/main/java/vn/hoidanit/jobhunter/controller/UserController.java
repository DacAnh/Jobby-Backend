package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreatedUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdatedUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService
    ,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch one users by id")
    public ResponseEntity<ResUserDTO> getOneUser(@PathVariable long id){
        User fetchUser = this.userService.fetchOneUserById(id);
        if(fetchUser == null){
            throw new EntityNotFoundException("User not found");
        }
        return ResponseEntity.ok(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> specification,
//            @RequestParam("current") Optional<String> currentOptional,
//            @RequestParam("pageSize") Optional<String> pageSizeOptional){
            Pageable pageable
    ){
//        String sCurrent= currentOptional.isPresent() ? currentOptional.get() : "";
//        String sPageSize= pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";
//        int current = Integer.parseInt(sCurrent);
//        int pageSize = Integer.parseInt(sPageSize);
//        Pageable pageable = PageRequest.of(current-1,pageSize);
        return ResponseEntity.ok(this.userService.fetchAllUser(specification, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreatedUserDTO> createNewUser(@Valid @RequestBody User user){
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist){
            throw new EntityExistsException("Email already exists");
        }
        //  Sử dụng passwordEncoder đã được ghi đè thuật toán trước đó để mã hóa
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        User createdUser= this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreatedUserDTO(createdUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdatedUserDTO> updateUser(@RequestBody User user){
        User updatedUser = this.userService.handleUpdateUser(user);
        if(updatedUser == null){
            throw new EntityNotFoundException("User not found");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable long id){
        this.userService.deletedUser(id);
        return ResponseEntity.ok(null);
    }
}
