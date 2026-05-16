package vn.hoidanit.jobhunter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.hoidanit.jobhunter.domain.response.RestResponse;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

//      Để spring mặc định thêm thông tin vào header của response
        this.delegate.commence(request, response, authException);

//      Hỗ trợ tiếng việt
//      Giúp phần body hỗ trợ được tiếng Việt để thêm thông tin vào phần body
        response.setContentType("application/json;charset=UTF-8");

//      Tạo chuẩn response trả về đính kèm thông tin ở body
        RestResponse<Object> res= new RestResponse<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

//      Khi client gửi request không có header bear token thì server
//      ném ra exception không có message, nên phải authException.getMessage() khi authException.getCause()==null
        String errorMessage = Optional.ofNullable(authException.getCause())
                        .map(Throwable::getMessage)
                                .orElse(authException.getMessage());
        res.setError(errorMessage);
        res.setMessage("Token không hợp lệ");
        mapper.writeValue(response.getWriter(), res);
    }
}
