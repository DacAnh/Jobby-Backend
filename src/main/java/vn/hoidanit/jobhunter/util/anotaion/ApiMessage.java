package vn.hoidanit.jobhunter.util.anotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Custom message trả về cho client thay vì message cố định "API CALL SUCCESS" bằng cách dùng Anotation

// Cho phép hoạt động trong quá trình chạy project
@Retention(RetentionPolicy.RUNTIME)

// Phạm vi hoạt động là tại các method
@Target(ElementType.METHOD)
public @interface ApiMessage {
    String value();
}
