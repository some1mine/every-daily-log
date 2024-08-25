package site.thedeny.every_daily_log;

import org.h2.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EveryDailyLogApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(StringUtils.isNumber(null));
	}

}
