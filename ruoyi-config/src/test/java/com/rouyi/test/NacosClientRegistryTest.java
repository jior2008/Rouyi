package com.rouyi.test;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.AbstractHealthChecker;
import com.alibaba.nacos.api.naming.pojo.Cluster;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NacosClientRegistryTest {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "127.0.0.1");
        properties.setProperty("namespace", "16006");


        NamingService naming = NamingFactory.createNamingService(properties);

        Instance instance = new Instance();
        instance.setIp("127.0.0.1");
        instance.setPort(8848);
        instance.setEnabled(true);
        instance.setEphemeral(false);
        instance.setHealthy(true);
        instance.setWeight(5.0);
        instance.setClusterName("test-cluster");
        instance.setServiceName("test-service");
        Map<String, String> instanceMeta = new HashMap<>();
        instanceMeta.put("site", "et2");
        instance.setMetadata(instanceMeta);

        Service service = new Service("test-service");
        service.setAppName("nacos-naming");
        service.setGroupName("CNCF");
        service.setName("test-service");
        service.setProtectThreshold(0.8F);

        Map<String, String> serviceMeta = new HashMap<>();
        serviceMeta.put("symmetricCall", "true");
        service.setMetadata(serviceMeta);

        Cluster cluster = new Cluster();
        cluster.setName("test-cluster");
        cluster.setServiceName("test-service");
        AbstractHealthChecker.Http healthChecker = new AbstractHealthChecker.Http();
        healthChecker.setExpectedResponseCode(400);
        cluster.setHealthChecker(healthChecker);
        Map<String, String> clusterMeta = new HashMap<>();
        clusterMeta.put("xxx", "yyyy");
        cluster.setMetadata(clusterMeta);

        naming.registerInstance("test-service", instance);
        
        naming.registerInstance("nacos.test.3", "127.0.0.1", 8848, "TEST1");

        naming.registerInstance("nacos.test.4", "127.0.0.1", 8848, "DEFAULT");

        System.out.println(">>" + naming.getAllInstances("nacos.test.4"));

        naming.deregisterInstance("nacos.test.3", "127.0.0.1", 8848, "DEFAULT");

        System.out.println("##" + naming.getAllInstances("test-service"));

        naming.subscribe("test-service", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent) event).getServiceName());
                System.out.println(((NamingEvent) event).getInstances());
            }
        });
    }
}