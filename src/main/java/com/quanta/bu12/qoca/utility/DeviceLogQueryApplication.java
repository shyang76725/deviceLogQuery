package com.quanta.bu12.qoca.utility;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
@ComponentScan
@PropertySources(value = { @PropertySource("classpath:application.properties") })
public class DeviceLogQueryApplication {
	@Value("${server.port}")
	private String serverPort;
	@Value("${host.user}")
	private String hostUser;
	@Value("${host.pw}")
	private String hostPw;
	@Value("${node.user}")
	private String nodeUser;
	@Value("${node.pw}")
	private String nodepw;
	@Value("${node.ip}")
	private String nodeIp;
	@Value("${device.log.location}")
	private String deviceLogLocation;
	@Value("${OPENAM_INTERNAL_URI}")
	private String oPENAM_INTERNAL_URI;
	@Value("${OPENAM_ADMIN_USERNAME}")
	private String oPENAM_ADMIN_USERNAME;
	@Value("${OPENAM_ADMIN_PASSWORD}")
	private String oPENAM_ADMIN_PASSWORD;
	@Value("${db.ip}")
	private String dbIp;
	@Value("${db.user}")
	private String dbUser;
	@Value("${db.pw}")
	private String dbPw;
	@PostConstruct
	public void setProperty(){
		Property.setServerPort(serverPort);
		Property.setHostUser(hostUser);
		Property.setHostPw(hostPw);
		Property.setNodeUser(nodeUser);
		Property.setNodepw(nodepw);
		Property.setNodeIp(nodeIp);
		Property.setDeviceLogLocation(deviceLogLocation);
		Property.setOPENAM_ADMIN_PASSWORD(oPENAM_ADMIN_PASSWORD);
		Property.setOPENAM_ADMIN_USERNAME(oPENAM_ADMIN_USERNAME);
		Property.setOPENAM_INTERNAL_URI(oPENAM_INTERNAL_URI);
		Property.setDbIp(dbIp);
		Property.setDbUser(dbUser);
		Property.setDbPw(dbPw);
	}
	public static void main(String[] args) {
		SpringApplication.run(DeviceLogQueryApplication.class, args);
		System.out.println("=====Welcome to use tool: Device log query =====");
		System.out.println("1. Please start up your browser.");
		System.out.println("2. input url: http://127.0.0.1:"+Property.getServerPort()+"/");
		System.out.println("==========");
		
	}
	@Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
