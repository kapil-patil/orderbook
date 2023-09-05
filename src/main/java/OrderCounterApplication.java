import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ubs.*")
public class OrderCounterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCounterApplication.class, args);
    }

}
