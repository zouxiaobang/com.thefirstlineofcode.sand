package com.thefirstlineofcode.sand.server.ibdr;

import java.util.List;

import com.thefirstlineofcode.basalt.protocol.core.stream.Feature;
import com.thefirstlineofcode.basalt.protocol.core.stream.Features;
import com.thefirstlineofcode.granite.framework.core.adf.IApplicationComponentService;
import com.thefirstlineofcode.granite.framework.core.annotations.Component;
import com.thefirstlineofcode.granite.framework.core.connection.IClientConnectionContext;
import com.thefirstlineofcode.granite.pipeline.stages.stream.IStreamNegotiant;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.InitialStreamNegotiant;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.ResourceBindingNegotiant;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.SaslNegotiant;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.SessionEstablishmentNegotiant;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.TlsNegotiant;
import com.thefirstlineofcode.sand.protocols.ibdr.Register;
import com.thefirstlineofcode.sand.protocols.ibdr.oxm.RegisterTranslatorFactory;
import com.thefirstlineofcode.sand.server.stream.DeviceClientMessageProcessor;

@Component("ibdr.supported.client.message.processor")
public class IbdrSupportedClientMessageProcessor extends DeviceClientMessageProcessor {
	private static final String APP_COMPONENT_NAME_DEVICE_REGISTRAR = "device.registrar";
	
	private IDeviceRegistrar registrar;
	
	@Override
	protected IStreamNegotiant createNegotiant() {
		if (tlsRequired) {
			IStreamNegotiant initialStream = new InitialStreamNegotiant(hostName,
					getInitialStreamNegotiantAdvertisements());
			
			IStreamNegotiant tls = new IbdrSupportedTlsNegotiant(hostName, tlsRequired,
					getTlsNegotiantAdvertisements());
			
			IStreamNegotiant ibdrAfterTls = new IbdrNegotiant(hostName, getTlsNegotiantAdvertisements(),
					registrar, eventMessageChannel);
			
			IStreamNegotiant sasl = new SaslNegotiant(hostName,
					saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
					getSaslNegotiantFeatures(), authenticator);
			
			IStreamNegotiant resourceBinding = new ResourceBindingNegotiant(
					hostName, sessionManager);
			IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
					router, sessionManager, eventMessageChannel, sessionListenerDelegate);
			
			resourceBinding.setNext(sessionEstablishment);
			sasl.setNext(resourceBinding);
			ibdrAfterTls.setNext(sasl);
			tls.setNext(ibdrAfterTls);
			initialStream.setNext(tls);
			
			return initialStream;
		} else {
			IStreamNegotiant initialStream = new IbrSupportedInitialStreamNegotiant(hostName,
					getInitialStreamNegotiantAdvertisements());
			
			IStreamNegotiant ibdrBeforeTls = new IbdrNegotiant(hostName, getInitialStreamNegotiantAdvertisements(),
					registrar, eventMessageChannel);
			
			IStreamNegotiant tls = new IbdrSupportedTlsNegotiant(hostName, tlsRequired,
					getTlsNegotiantAdvertisements());
			
			IStreamNegotiant ibdrAfterTls = new IbdrNegotiant(hostName, getTlsNegotiantAdvertisements(),
					registrar, eventMessageChannel);
			
			IStreamNegotiant sasl = new SaslNegotiant(hostName,
					saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
					getSaslNegotiantFeatures(), authenticator);
			
			IStreamNegotiant resourceBinding = new ResourceBindingNegotiant(
					hostName, sessionManager);
			IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
					router, sessionManager, eventMessageChannel, sessionListenerDelegate);
			
			resourceBinding.setNext(sessionEstablishment);
			sasl.setNext(resourceBinding);
			ibdrAfterTls.setNext(sasl);
			tls.setNext(ibdrAfterTls);
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
	
	@Override
	public void setApplicationComponentService(IApplicationComponentService appComponentService) {
		super.setApplicationComponentService(appComponentService);
		
		registrar = appComponentService.getAppComponent(APP_COMPONENT_NAME_DEVICE_REGISTRAR, IDeviceRegistrar.class);
	}
}
