package org.raj.quickfixj.springboot.server;

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJServer;
import io.allune.quickfixj.spring.boot.starter.template.QuickFixJTemplate;
import lombok.extern.slf4j.Slf4j;
import org.raj.quickfixj.springboot.server.component.CustomProperties;
import org.raj.quickfixj.springboot.server.service.MarketDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import quickfix.*;

import java.util.concurrent.TimeUnit;

@EnableQuickFixJServer
@SpringBootApplication
@Slf4j
@EnableConfigurationProperties(CustomProperties.class)
public class BootifiedServer {
    public static void main(String[] args) {
        SpringApplication.run(BootifiedServer.class, args);
    }


//    @Bean
//    public CommandLineRunner quickFixRunner(MarketDataRequestService application) {
//        return args -> {
//            SessionSettings settings = new SessionSettings("/Users/i339662/Documents/spring-demo/Examples/spring-boot-server/src/main/resources/quickfixj-server.cfg");
//            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
//            LogFactory logFactory = new FileLogFactory(settings);
//            MessageFactory messageFactory = new DefaultMessageFactory();
//
//            Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
//            acceptor.start();
//
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                if (acceptor != null) {
//                    acceptor.stop();
//                }
//            }));
//        };
//    }



    @Bean
    @Primary
    public MarketDataService serverApplication(QuickFixJTemplate quickFixJTemplate, Acceptor serverAcceptor, @Value("${quickfixj.server.publisher.schedule.period}") final long period,
                                               @Value("${quickfixj.server.publisher.schedule.time-unit}") final TimeUnit timeUnit) {
        return new MarketDataService(quickFixJTemplate, serverAcceptor,period,timeUnit) ;
    }

}
