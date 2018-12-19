package com.rookiefly.springboot.quickstart.consul;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.rookiefly.springboot.quickstart.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class PrometheusConsulRegister {

    private static Logger logger = LoggerFactory.getLogger(PrometheusConsulRegister.class);

    public static final String TAG = "prometheus-springboot";

    @Value("${server.port}")
    private int port = 9000;

    public static final String SERVICE_NAME = "springboot-quickstart-web";

    public static final String SERVICE_ID = "springboot-quickstart-web-";

    @PostConstruct
    private void init() {
        try {
            String localHost = NetUtils.getLocalHost();
            String metrics = String.format("http://%s:%s/actuator/prometheus", localHost, port);
            Consul client = Consul.builder().withHostAndPort(HostAndPort.fromParts("127.0.0.1", 8500)).build();
            Registration.RegCheck single = Registration.RegCheck.http(metrics, 20);
            Registration reg = ImmutableRegistration.builder()
                    .check(single)
                    .addTags(TAG)
                    .address(localHost)
                    .port(port)
                    .name(SERVICE_NAME)
                    .id(SERVICE_ID + port)
                    .build();
            AgentClient agentClient = client.agentClient();
            agentClient.register(reg);
        } catch (Exception e) {
            logger.error("consul register error", e);
        }
    }

    @PreDestroy
    public void onDestroy() {
        logger.debug("Spring Container is destroyed!");
        try {
            Consul client = Consul.builder().withHostAndPort(HostAndPort.fromParts("127.0.0.1", 8500)).build();
            AgentClient agentClient = client.agentClient();
            agentClient.deregister(SERVICE_ID + port);
        } catch (Exception e) {
            logger.error("consul deregister error", e);
        }
    }
}
