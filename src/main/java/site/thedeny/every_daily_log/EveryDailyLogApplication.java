package site.thedeny.every_daily_log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class EveryDailyLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveryDailyLogApplication.class, args);
	}

}
