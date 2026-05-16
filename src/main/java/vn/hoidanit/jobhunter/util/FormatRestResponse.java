package vn.hoidanit.jobhunter.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.util.anotaion.ApiMessage;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
        // true: Bất cứ response nào cũng cần ghi đè để format response trả về
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> res= new RestResponse<Object>();
        res.setStatusCode(status);

//      Không format nếu data thuộc dạng String
        if(body instanceof String){
            return body;
        }
        if(status >= 400 ){
            // case error
            return body;
        } else {
            // case success
            res.setData(body);
//          Lấy đối tượng ApiMessage tại returnType ( sau khi luồng hoạt động đã đi qua Controller )
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
//          Kiểm tra giá trị message, nếu message==null thì nó sẽ bị lỗi nếu cố lấy message.value()
            res.setMessage(message!=null ? message.value() : "CALL API SUCCESS");
        }

        return res;
    }
}
