package com.firstlinecode.sand.server.stream;

import com.firstlinecode.granite.framework.stream.IStreamNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.InitialStreamNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.SaslNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.SessionEstablishmentNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.TlsNegotiant;
import com.firstlinecode.granite.stream.standard.StandardClientMessageProcessor;

public class DeviceClientMessageProcessor extends StandardClientMessageProcessor {
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
}
