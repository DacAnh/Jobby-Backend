package vn.hoidanit.jobhunter.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.Collections;

@Component("userDetailsService")
// Ghi đè gián tiếp vào bean của lớp UserDetailsService
// Thay vì phải ghi đè, khai báo trực tiếp thông qua lớp trong securityconfiguration như trong mvc
// Theo mặc định lớp nào được gán là Bean thì spring boot sẽ chuyển chữ cái đầu tiên từ hoa thành thường
// VD: UserDetailsService thì bean của nó sẽ là userDetailsService
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lấy người dùng từ CSDL dựa trên email được cung cấp bởi API POST /login
        vn.hoidanit.jobhunter.domain.User user = this.userService.fetchOneUserByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }

        // Trả về kiểu dữ liệu UserDetails mà hàm loadUserByUsername yêu cầu
        //Vì core.userdetails.User kế thừa từ UserDetails nên có thể trả về core.userdetails.User
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList( new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
