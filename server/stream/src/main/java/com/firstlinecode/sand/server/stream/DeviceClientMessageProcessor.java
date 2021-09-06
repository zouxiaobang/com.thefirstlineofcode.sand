package com.firstlinecode.sand.server.stream;

import com.firstlinecode.granite.framework.adf.spring.AdfComponentService;
import com.firstlinecode.granite.framework.core.adf.IApplicationComponentService;
import com.firstlinecode.granite.framework.core.auth.IAuthenticator;
import com.firstlinecode.granite.pipeline.stages.stream.IStreamNegotiant;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.InitialStreamNegotiant;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.SaslNegotiant;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.SessionEstablishmentNegotiant;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.TlsNegotiant;
import com.firstlinecode.granite.stream.standard.StandardClientMessageProcessor;

public class DeviceClientMessageProcessor extends StandardClientMessageProcessor {
	private static final String BEAN_NAME_DEVICE_AUTHENTICATOR = "deviceAuthenticator";
	
	protected IStreamNegotiant createNegotiant() {
		IStreamNegotiant intialStream = new InitialStreamNegotiant(hostName,
				getInitialStreamNegotiantAdvertisements());
		
		IStreamNegotiant tls = new TlsNegotiant(hostName, tlsRequired,
				getTlsNegotiantAdvertisements());
		
		IStreamNegotiant sasl = new SaslNegotiant(hostName,
				saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
				getSaslNegotiantFeatures(), authenticator);
		
		IStreamNegotiant resourceBinding = new DeviceResourceBindingNegotiant(
				hostName, sessionManager);
		IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
				router, sessionManager, eventMessageChannel, sessionListenerDelegate);
		
		resourceBinding.setNext(sessionEstablishment);
		sasl.setNext(resourceBinding);
		tls.setNext(sasl);
		intialStream.setNext(tls);
		
		return intialStream;
	}
	
	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		this.appComponentService = appComponentService;
		authenticator = ((AdfComponentService)appComponentService).getApplicationContext().
				getBean(BEAN_NAME_DEVICE_AUTHENTICATOR, IAuthenticator.class);
	}
}
