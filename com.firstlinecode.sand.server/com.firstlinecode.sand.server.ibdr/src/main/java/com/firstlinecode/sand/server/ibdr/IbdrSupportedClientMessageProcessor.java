package com.firstlinecode.sand.server.ibdr;

import java.util.List;

import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.basalt.protocol.core.stream.Features;
import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.granite.framework.stream.IStreamNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.InitialStreamNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.ResourceBindingNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.SaslNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.SessionEstablishmentNegotiant;
import com.firstlinecode.granite.framework.stream.negotiants.TlsNegotiant;
import com.firstlinecode.granite.stream.standard.StandardClientMessageProcessor;
import com.firstlinecode.sand.protocols.ibdr.Register;
import com.firstlinecode.sand.protocols.ibdr.oxm.RegisterTranslatorFactory;

@Component("ibdr.supported.client.message.processor")
public class IbdrSupportedClientMessageProcessor extends StandardClientMessageProcessor {
	@Dependency("registrar")
	private IDeviceRegistrar registrar;
	
	@Override
	protected IStreamNegotiant createNegotiant() {
		if (tlsRequired) {
			IStreamNegotiant initialStream = new InitialStreamNegotiant(hostName,
					getInitialStreamNegotiantAdvertisements());
			
			IStreamNegotiant tls = new IbdrSupportedTlsNegotiant(hostName, tlsRequired,
					getTlsNegotiantAdvertisements());
			
			IStreamNegotiant ibrAfterTls = new IbdrNegotiant(hostName,
					getTlsNegotiantAdvertisements(), registrar);
			
			IStreamNegotiant sasl = new SaslNegotiant(hostName,
					saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
					getSaslNegotiantFeatures(), authenticator);
			
			IStreamNegotiant resourceBinding = new ResourceBindingNegotiant(
					hostName, sessionManager);
			IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
					router, sessionManager, eventMessageChannel, sessionListenerDelegate);
			
			resourceBinding.setNext(sessionEstablishment);
			sasl.setNext(resourceBinding);
			ibrAfterTls.setNext(sasl);
			tls.setNext(ibrAfterTls);
			initialStream.setNext(tls);
			
			return initialStream;
		} else {
			IStreamNegotiant initialStream = new IbrSupportedInitialStreamNegotiant(hostName,
					getInitialStreamNegotiantAdvertisements());
			
			IStreamNegotiant ibdrBeforeTls = new IbdrNegotiant(hostName,
					getInitialStreamNegotiantAdvertisements(), registrar);
			
			IStreamNegotiant tls = new IbdrSupportedTlsNegotiant(hostName, tlsRequired,
					getTlsNegotiantAdvertisements());
			
			IStreamNegotiant ibrAfterTls = new IbdrNegotiant(hostName,
					getTlsNegotiantAdvertisements(), registrar);
			
			IStreamNegotiant sasl = new SaslNegotiant(hostName,
					saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
					getSaslNegotiantFeatures(), authenticator);
			
			IStreamNegotiant resourceBinding = new ResourceBindingNegotiant(
					hostName, sessionManager);
			IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
					router, sessionManager, eventMessageChannel, sessionListenerDelegate);
			
			resourceBinding.setNext(sessionEstablishment);
			sasl.setNext(resourceBinding);
			ibrAfterTls.setNext(sasl);
			tls.setNext(ibrAfterTls);
			ibdrBeforeTls.setNext(tls);
			initialStream.setNext(ibdrBeforeTls);
			
			return initialStream;
		}
		
	}
	
	private static class IbrSupportedInitialStreamNegotiant extends InitialStreamNegotiant {
		static {
			oxmFactory.register(Register.class, new RegisterTranslatorFactory());
		}
		
		public IbrSupportedInitialStreamNegotiant(String domainName, List<Feature> features) {
			super(domainName, features);
			features.add(new Register());
		}
	}
	
	private static class IbdrSupportedTlsNegotiant extends TlsNegotiant {
		static {
			oxmFactory.register(Register.class, new RegisterTranslatorFactory());
		}

		public IbdrSupportedTlsNegotiant(String domainName, boolean tlsRequired, List<Feature> features) {
			super(domainName, tlsRequired, features);
		}
		
		@Override
		protected Features getAvailableFeatures(IClientConnectionContext context) {
			Features features = super.getAvailableFeatures(context);
			
			if (context.getAttribute(IbdrNegotiant.KEY_IBDR_REGISTERED) == null) {
				features.getFeatures().add(new Register());
			}
			
			return features;
		}
	}
}
