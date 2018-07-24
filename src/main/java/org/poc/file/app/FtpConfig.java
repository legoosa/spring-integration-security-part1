package org.poc.file.app;

import java.io.File;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ftp.outbound.FtpMessageHandler;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableConfigurationProperties(FtpProperties.class)
public class FtpConfig {

	private final FtpProperties ftpProperties;
	
	public FtpConfig(FtpProperties ftpProperties) {
		this.ftpProperties = ftpProperties;
	}

	@Bean
	DefaultFtpSessionFactory ftpSessionFactory(){
		DefaultFtpSessionFactory ftpSessionFactory = new DefaultFtpSessionFactory();
		ftpSessionFactory.setHost(ftpProperties.getHost());
		ftpSessionFactory.setPort(ftpProperties.getPort());
		ftpSessionFactory.setUsername(ftpProperties.getUsername());
		ftpSessionFactory.setPassword(ftpProperties.getPassword());
		return ftpSessionFactory;
	}

	@Bean
    @ServiceActivator(inputChannel = "toFtpChannel")
    public MessageHandler handler() {
        FtpMessageHandler handler = new FtpMessageHandler(ftpSessionFactory());
        handler.setRemoteDirectoryExpression(new LiteralExpression(ftpProperties.getDirectory()));
        handler.setFileNameGenerator(message -> {
            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else {
                throw new IllegalArgumentException("File expected as payload.");
            }
        });
        return handler;
    }
	
	@MessagingGateway
	public interface FtpGateway {

		@Gateway(requestChannel = "toFtpChannel")
	    void send(File file);
	}
	
	@Bean
	public FtpRemoteFileTemplate template(){
		return new FtpRemoteFileTemplate(ftpSessionFactory());
	}
}
