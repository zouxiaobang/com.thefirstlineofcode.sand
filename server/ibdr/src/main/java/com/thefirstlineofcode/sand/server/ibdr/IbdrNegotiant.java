package com.thefirstlineofcode.sand.server.ibdr;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.thefirstlineofcode.basalt.oxm.OxmService;
import com.thefirstlineofcode.basalt.oxm.parsers.core.stanza.IqParserFactory;
import com.thefirstlineofcode.basalt.oxm.parsing.IParsingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translators.core.stanza.IqTranslatorFactory;
import com.thefirstlineofcode.basalt.oxm.translators.core.stream.StreamTranslatorFactory;
import com.thefirstlineofcode.basalt.oxm.translators.error.StanzaErrorTranslatorFactory;
import com.thefirstlineofcode.basalt.xmpp.core.IqProtocolChain;
import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.Iq;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.BadRequest;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.InternalServerError;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.StanzaError;
import com.thefirstlineofcode.basalt.xmpp.core.stream.Feature;
import com.thefirstlineofcode.basalt.xmpp.core.stream.Stream;
import com.thefirstlineofcode.granite.framework.core.connection.IClientConnectionContext;
import com.thefirstlineofcode.granite.framework.core.pipeline.IMessage;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirer;
import com.thefirstlineofcode.granite.pipeline.stages.stream.negotiants.InitialStreamNegotiant;
import com.thefirstlineofcode.sand.protocols.ibdr.DeviceRegister;
import com.thefirstlineofcode.sand.protocols.ibdr.oxm.DeviceRegisterParserFactory;
import com.thefirstlineofcode.sand.protocols.ibdr.oxm.DeviceRegisterTranslatorFactory;
import com.thefirstlineofcode.sand.server.devices.DeviceRegistered;

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
	private IEventFirer eventFirer;
	
	public IbdrNegotiant(String domainName, List<Feature> features, IDeviceRegistrar registrar, IEventFirer eventFirer) {
		super(domainName, features);
		
		this.registrar = registrar;
		this.eventFirer = eventFirer;
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
			
			String deviceId = (String)register;
			DeviceRegistered registered = registrar.register(deviceId);
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			result.setObject(new DeviceRegister(registered.deviceIdentity));
			
			context.write(translatingFactory.translate(result));
			
			eventFirer.fire(new DeviceRegistrationEvent(deviceId, registered.deviceIdentity.getDeviceName(),
					registered.authorizer, registered.registrationTime));
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
			
			if (t instanceof InvocationTargetException) {
				t = ((InvocationTargetException)t).getTargetException();
				if (t instanceof ProtocolException)
					return (ProtocolException)t;
				
				return new ProtocolException(new InternalServerError(t.getMessage()));
			}
		}
		
		return null;
	}
}
