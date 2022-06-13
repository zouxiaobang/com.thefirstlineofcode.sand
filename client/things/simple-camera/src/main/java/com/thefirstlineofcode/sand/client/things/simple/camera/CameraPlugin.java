package com.thefirstlineofcode.sand.client.things.simple.camera;

import java.util.Properties;

import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionParserFactory;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.IqProtocolChain;
import com.thefirstlineofcode.chalk.core.IChatSystem;
import com.thefirstlineofcode.chalk.core.IPlugin;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.OpenLiveStreaming;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakePhoto;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakeVideo;

public class CameraPlugin implements IPlugin {

	@Override
	public void init(IChatSystem chatSystem, Properties properties) {
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(TakePhoto.PROTOCOL),
				new NamingConventionParserFactory<TakePhoto>(TakePhoto.class));
		chatSystem.registerTranslator(TakePhoto.class,
				new NamingConventionTranslatorFactory<TakePhoto>(TakePhoto.class));
		
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(TakeVideo.PROTOCOL),
				new NamingConventionParserFactory<TakeVideo>(TakeVideo.class));
		chatSystem.registerTranslator(TakeVideo.class,
				new NamingConventionTranslatorFactory<TakeVideo>(TakeVideo.class));
		
		chatSystem.registerParser(new IqProtocolChain(Execution.PROTOCOL).next(OpenLiveStreaming.PROTOCOL),
				new NamingConventionParserFactory<OpenLiveStreaming>(OpenLiveStreaming.class));
		chatSystem.registerTranslator(OpenLiveStreaming.class,
				new NamingConventionTranslatorFactory<OpenLiveStreaming>(OpenLiveStreaming.class));
	}

	@Override
	public void destroy(IChatSystem chatSystem) {
		chatSystem.unregisterTranslator(OpenLiveStreaming.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(OpenLiveStreaming.PROTOCOL));
		
		chatSystem.unregisterTranslator(TakeVideo.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(TakeVideo.PROTOCOL));
				
		chatSystem.unregisterTranslator(TakePhoto.class);
		chatSystem.unregisterParser(new IqProtocolChain(Execution.PROTOCOL).next(TakePhoto.PROTOCOL));
	}

}
