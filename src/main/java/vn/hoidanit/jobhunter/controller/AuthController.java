package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.controller.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreatedUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResponseLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO){
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

//      xác thực người dùng
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//      Vì mặc định spring security thực hiện xác thực qua RAM,
//     nên cần ghi đè xác thực để điều hướng spring security xác thực qua CSDL (MySQL) -> cần viết hàm loadUserByUsername

//nạp thông tin (nếu xử lý thành công) vào SecurityContext
//        SecurityContextHolder.getContext().setAuthentication(authentication);



//      Đưa thông tin của authentication vào context để dùng cho những request đã đăng nhập tiếp theo (nếu chưa sử
//      dụng JWT thì sẽ cần)
//      Thiếp lập thông tin user đăng nhập vào context để có thể sử dụng sau này nếu muốn gọi user đăng nhập từ
//      sevice
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseLoginDTO res = new ResponseLoginDTO();
        User currentUser = this.userService.fetchOneUserByEmail(reqLoginDTO.getUsername());
        ResponseLoginDTO.UserLoginDTO userLoginDTO = new ResponseLoginDTO.UserLoginDTO(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getRole()
        );

        res.setUser(userLoginDTO);

        //      Bắt đầu tạo Token
//      Truyền thông tin định danh người dùng mặc định (authentication) cho việc tạo token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(),res);

        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(res);
//      Cập nhật refresh_token cho user
        this.userService.updateUserToken(refresh_token, reqLoginDTO.getUsername());

//      Thiết lập cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResponseLoginDTO.UserGetAccount> getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.fetchOneUserByEmail(email);
        ResponseLoginDTO.UserLoginDTO userLoginDTO = new ResponseLoginDTO.UserLoginDTO();
        ResponseLoginDTO.UserGetAccount userGetAccount = new ResponseLoginDTO.UserGetAccount();

        userLoginDTO.setId(currentUser.getId());
        userLoginDTO.setEmail(currentUser.getEmail());
        userLoginDTO.setName(currentUser.getName());
        userLoginDTO.setRole(currentUser.getRole());

        userGetAccount.setUser(userLoginDTO);
        userGetAccount.setAge(currentUser.getAge());
        userGetAccount.setGender(String.valueOf(currentUser.getGender()));
        userGetAccount.setAddress(currentUser.getAddress());
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResponseLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refreshToken
    ){
//      Kiểm tra refreshToken có hợp lệ hay ko. Tránh bị hacker spoofing token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User currentUser = this.userService.getUserByEmailAndRefreshToken(email, refreshToken);
        if(currentUser == null){
            throw new UsernameNotFoundException("Refresh token not found");
        }


        ResponseLoginDTO res = new ResponseLoginDTO();
        ResponseLoginDTO.UserLoginDTO userLoginDTO = new ResponseLoginDTO.UserLoginDTO(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getRole()
        );

        res.setUser(userLoginDTO);

//      Bắt đầu tạo Token
//      Truyền thông tin định danh người dùng mặc định (authentication) cho việc tạo token
        String access_token = this.securityUtil.createAccessToken(email,res);

        res.setAccessToken(access_token);

        String newRefreshToken = this.securityUtil.createRefreshToken(res);
//      Cập nhật refresh_token cho user
        this.userService.updateUserToken(newRefreshToken, email);

//      Thiết lập cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.userService.updateUserToken(null, email);

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreatedUserDTO> register(@Valid @RequestBody User newUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(newUser.getEmail());
        if(isEmailExist){
            throw new IdInvalidException(
                    "Email "+ newUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác"
            );
        }

        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        User currentUser = this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreatedUserDTO(currentUser));
    }
}
