package dynamicqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DynamicqrBackendApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("org.springframework.boot.logging.LoggingSystem",
                "org.springframework.boot.logging.java.JavaLoggingSystem");
        return application.sources(DynamicqrBackendApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DynamicqrBackendApplication.class, args);
    }
}
