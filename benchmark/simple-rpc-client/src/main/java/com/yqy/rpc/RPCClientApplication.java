package com.yqy.rpc;

import com.yqy.rpc.client.call.AsyncCallService;
import com.yqy.rpc.client.call.SyncCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName: RPCClientApplication
 * @Description: Hello Word！
 * @Author: YangQingyuan
 * @Data: 2020/02/03
 * @Version: V1.0
 **/
@SpringBootApplication
public class RPCClientApplication implements CommandLineRunner {

    @Autowired
    private SyncCallService syncCallService;

    @Autowired
    private AsyncCallService asyncCallService;

    public static void main( String[] args )
    {
        SpringApplication app = new SpringApplication(RPCClientApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        asyncCallService.asyncCall();
        //syncCallService.syncCallTest();
    }
}
