package vn.hoidanit.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Ghi đè thêm cấu hình path tĩnh từ biến môi trường trong application.properties
// Tức cứ mỗi request /storage/** thì sẽ tìm trong basePath ( hoidanit.upload-file.base-path )
@Configuration
public class StaticResourcesWebConfiguration
        implements WebMvcConfigurer {

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(baseURI);
    }
}
