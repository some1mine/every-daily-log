package site.thedeny.every_daily_log;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcAuditing
@EnableR2dbcRepositories
@SpringBootApplication
@EnableReactiveMongoRepositories
@Import(value={H2ConsoleAutoConfiguration.class})
public class EveryDailyLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveryDailyLogApplication.class, args);
	}

}
