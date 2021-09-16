package com.firstlinecode.sand.server.ibdr;

import java.util.List;

import com.firstlinecode.basalt.oxm.OxmService;
import com.firstlinecode.basalt.oxm.parsers.core.stanza.IqParserFactory;
import com.firstlinecode.basalt.oxm.parsing.IParsingFactory;
import com.firstlinecode.basalt.oxm.translating.ITranslatingFactory;
import com.firstlinecode.basalt.oxm.translators.core.stanza.IqTranslatorFactory;
import com.firstlinecode.basalt.oxm.translators.core.stream.StreamTranslatorFactory;
import com.firstlinecode.basalt.oxm.translators.error.StanzaErrorTranslatorFactory;
import com.firstlinecode.basalt.protocol.core.IqProtocolChain;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.basalt.protocol.core.stanza.error.InternalServerError;
import com.firstlinecode.basalt.protocol.core.stanza.error.StanzaError;
import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.basalt.protocol.core.stream.Stream;
import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.granite.framework.core.pipeline.IMessage;
import com.firstlinecode.granite.framework.core.pipeline.IMessageChannel;
import com.firstlinecode.granite.framework.core.pipeline.SimpleMessage;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.InitialStreamNegotiant;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;
import com.firstlinecode.sand.protocols.ibdr.oxm.DeviceRegisterParserFactory;
import com.firstlinecode.sand.protocols.ibdr.oxm.DeviceRegisterTranslatorFactory;

public class IbdrNegotiant extends InitialStreamNegotiant {
	public static final Object KEY_IBDR_REGISTERED = new Object();
	
	private static final IParsingFactory parsingFactory = OxmService.createParsingFactory();
	private static final ITranslatingFactory translatingFactory = OxmService.createTranslatingFactory();
	
	static {
		parsingFactory.register(
				new IqProtocolChain(),
				new IqParserFactory()
		);
		parsingFactory.register(
				new IqProtocolChain(DeviceRegister.PROTOCOL),
				new DeviceRegisterParserFactory()
		);
		
		translatingFactory.register(
				Iq.class,
				new IqTranslatorFactory()
		);
		translatingFactory.register(
				DeviceRegister.class,
				new DeviceRegisterTranslatorFactory()
		);
		translatingFactory.register(StanzaError.class, new StanzaErrorTranslatorFactory());
		translatingFactory.register(Stream.class, new StreamTranslatorFactory());
	}
	
	private IDeviceRegistrar registrar;
	private IMessageChannel eventMessageChannel;
	
	public IbdrNegotiant(String domainName, List<Feature> features, IDeviceRegistrar registrar, IMessageChannel eventMessageChannel) {
		super(domainName, features);
		
		this.registrar = registrar;
		this.eventMessageChannel = eventMessageChannel;
	}
	
	protected boolean doNegotiate(IClientConnectionContext context, IMessage message) {
		if (context.getAttribute(IbdrNegotiant.KEY_IBDR_REGISTERED) != null) {
			if (next != null) {
				done = true;
				return next.negotiate(context, message);
			}
			
			throw new ProtocolException(new BadRequest("Stream has estabilished."));
		}
		
		Iq iq = null;
		try {
			iq = (Iq)parsingFactory.parse((String)message.getPayload());
		} catch (Exception e) {
			// ignore
		}
		
		if (iq == null) {
			if (next != null) {
				done = true;
				return next.negotiate(context, message);
			}
			
			throw new ProtocolException(new BadRequest("Stream has estabilished."));
		}
		
		try {
			negotiateIbdr(context, message);
			return true;
		} catch (RuntimeException e) {
			throw e;
		}
		
	}

	private void negotiateIbdr(final IClientConnectionContext context, IMessage message) {
		Iq iq = (Iq)parsingFactory.parse((String)message.getPayload());
		DeviceRegister deviceRegister = iq.getObject();
		
		if (deviceRegister == null) {
			throw new ProtocolException(new BadRequest("Null register object."));
		}
		
		try {
			Object register = deviceRegister.getRegister();
			if (register == null || !(register instanceof String))
				throw new ProtocolException(new BadRequest("Register object isn't a string."));
			
			DeviceRegistrationEvent registrationEvent = registrar.register((String)register);
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			result.setObject(new DeviceRegister(registrationEvent.getIdentity()));
			
			context.write(translatingFactory.translate(result));
			
			eventMessageChannel.send(new SimpleMessage(registrationEvent));
		} catch (RuntimeException e) {
			// Standard client message processor doesn't support processing stanza error in normal situation.
			// So we process the exception by self.
			processException(iq, e);
		}
	}

	private void processException(Iq iq, RuntimeException e) {
		ProtocolException pe = null;
		
		if (e instanceof ProtocolException) {
			pe = (ProtocolException)e;
		} else {
			pe = findProtocolException(e);
		}
		
		if (pe != null) {
			if (pe.getError() instanceof StanzaError) {
				StanzaError error = (StanzaError)pe.getError();
				error.setId(iq.getId());
			}
			
			throw pe;
		}
		
		StanzaError error = new InternalServerError("Unexpected error. Error message: " + e.getMessage());
		error.setId(iq.getId());
		
		throw new ProtocolException(error);
	}

	private ProtocolException findProtocolException(Throwable t) {
		while (t.getCause() != null) {
			t = t.getCause();
			
			if (t instanceof ProtocolException)
				return (ProtocolException)t;
		}
		
		return null;
	}
}
