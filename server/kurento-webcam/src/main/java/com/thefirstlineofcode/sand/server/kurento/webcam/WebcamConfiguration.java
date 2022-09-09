package com.thefirstlineofcode.sand.server.kurento.webcam;

import org.kurento.client.KurentoClient;
import org.pf4j.Extension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thefirstlineofcode.granite.framework.adf.spring.ISpringConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;

@Extension
@Configuration
public class WebcamConfiguration implements ISpringConfiguration, IServerConfigurationAware, IConfigurationAware {
	private static final String KEY_KURENTO_WEBSOCKET_URL = "kurento.websockt.url";
	private String domainName;
	private String kurentoWebsocketUrl;
	
	@Bean
	public KurentoClient kurentoClient() {
		if (kurentoWebsocketUrl != null)
			return KurentoClient.create(kurentoWebsocketUrl);
		else
			return KurentoClient.create(String.format("ws://%s:8888/kurento", domainName));
	}

	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		kurentoWebsocketUrl = configuration.getString(KEY_KURENTO_WEBSOCKET_URL);
	}
}
