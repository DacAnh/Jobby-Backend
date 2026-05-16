package vn.hoidanit.jobhunter.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vn.hoidanit.jobhunter.domain.response.RestResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<RestResponse<Object>> handleLoginException(Exception ex){
        RestResponse<Object> res= new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Exception...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex){
        RestResponse<Object> res= new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Không tìm thấy Id");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Object>> handleCreateUserError(IllegalArgumentException ex){
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Exception...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex){
//      Lấy ra message lỗi dựa vào bindingresult
        BindingResult result = ex.getBindingResult();

//      Chuyển message qua dạng list để format nhiều lỗi trong 1 list
        final List<FieldError> fieldErrors = result.getFieldErrors();

//      Format về định dạng API custom
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());

//      Hàm ex.getBody().getDetail() lấy ra message lỗi mặc định của MethodArgumentNotValidException
        res.setError(ex.getBody().getDetail());

//      Chuyển list fieldErrors từ dạng  FieldError thành String
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());

//      Nếu errors >1 thì trả ra dưới dạng Array, còn errors =1 thì trả ra dưới dạng String
        res.setMessage(errors.size() >1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public  ResponseEntity<RestResponse<Object>> handleUrlApi(NoResourceFoundException ex){
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Không tìm thấy API hợp lệ");
        res.setMessage("API");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex){
        RestResponse<Object> res= new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Exception upload file...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
//    @ExceptionHandler(value = {
//            NoResourceFoundException.class,
//    })
//    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
//        RestResponse<Object> res = new RestResponse<Object>();
//        res.setStatusCode(HttpStatus.NOT_FOUND.value());
//        res.setError(ex.getMessage());
//        res.setMessage("404 Not Found. URL may not exist...");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//    }
}
