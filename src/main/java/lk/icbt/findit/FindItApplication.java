package lk.icbt.findit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FindItApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindItApplication.class, args);
	}

}
