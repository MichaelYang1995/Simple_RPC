package com.yqy.rpc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: RPCServerApplication
 * @Description: Hello Word!
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@SpringBootApplication
public class RPCServerApplication implements CommandLineRunner {
    public static void main( String[] args ) throws InterruptedException{
        SpringApplication app = new SpringApplication(RPCServerApplication.class);
        app.run(args);
        new CountDownLatch(1).await();
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
