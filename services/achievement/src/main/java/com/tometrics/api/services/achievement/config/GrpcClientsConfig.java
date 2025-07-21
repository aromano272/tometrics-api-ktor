package com.tometrics.api.services.achievement.config;

import com.tometrics.api.services.protos.ServiceDiscoveryGrpcServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientsConfig {

    private final GrpcChannelFactory channelFactory;

    public GrpcClientsConfig(GrpcChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    // TODO(aromano): check if this is resilient to disconnects etc..
    @Bean
    public ServiceDiscoveryGrpcServiceGrpc.ServiceDiscoveryGrpcServiceBlockingV2Stub serviceDiscoveryChannel() {
        var channel = channelFactory.createChannel("localhost", 9083);
        return ServiceDiscoveryGrpcServiceGrpc.newBlockingV2Stub(channel);
    }

}
