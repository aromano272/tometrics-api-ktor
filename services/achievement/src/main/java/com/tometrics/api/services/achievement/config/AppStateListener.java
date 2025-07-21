package com.tometrics.api.services.achievement.config;

import com.tometrics.api.common.domain.models.ServiceInfo;
import com.tometrics.api.services.commongrpc.models.servicediscovery.ServiceInfoKt;
import com.tometrics.api.services.protos.ServiceDiscoveryGrpcServiceGrpc;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppStateListener {

    private final ServiceInfo serviceInfo;
    private final ServiceDiscoveryGrpcServiceGrpc.ServiceDiscoveryGrpcServiceBlockingV2Stub serviceDiscoveryGrpcClient;

    public AppStateListener(
            ServiceInfo serviceInfo,
            ServiceDiscoveryGrpcServiceGrpc.ServiceDiscoveryGrpcServiceBlockingV2Stub serviceDiscoveryGrpcClient
    ) {
        this.serviceInfo = serviceInfo;
        this.serviceDiscoveryGrpcClient = serviceDiscoveryGrpcClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(
    ) {
        try {
            // This will be called after the application has started
            // We need to use a separate thread to call the suspend function
            new Thread(() -> {
                try {
                    serviceDiscoveryGrpcClient.register(ServiceInfoKt.toNetwork(serviceInfo));

                    System.out.println("Service registered with service discovery: " + serviceInfo);
                } catch (Exception e) {
                    System.err.println("Failed to register service with service discovery: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Failed to register service with service discovery: " + e.getMessage());
            e.printStackTrace();
        }
    }
}