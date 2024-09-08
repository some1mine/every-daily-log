package site.thedeny.every_daily_log.common.config.db.h2db;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class H2ConsoleConfig {
    private Server webServer;

    @Value("${spring.h2.console.port}")
    private String port;

    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {

        this.webServer = org.h2.tools.Server.createWebServer("-webPort", port, "-tcpAllowOthers").start();
    }

    @EventListener(org.springframework.context.event.ContextClosedEvent.class)
    public void stop() {
        this.webServer.stop();
    }

}
