package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.Attributes;
import com.thefirstlineofcode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.thefirstlineofcode.basalt.oxm.translating.IProtocolWriter;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslator;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.sand.protocols.actuator.Execution;

public class ExecuteTranslatorFactory implements ITranslatorFactory<Execution> {
	private static final ITranslator<Execution> translator = new ExecuteTranslator();

	@Override
	public Class<Execution> getType() {
		return Execution.class;
	}

	@Override
	public ITranslator<Execution> create() {
		return translator;
	}
	
	private static class ExecuteTranslator implements ITranslator<Execution> {
		@Override
		public Protocol getProtocol() {
			return Execution.PROTOCOL;
		}

		@Override
		public String translate(Execution execute, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			if (execute.getAction() == null) {
				throw new ProtocolException(new BadRequest("Null action."));
			}
			
			writer.writeProtocolBegin(Execution.PROTOCOL);
			
			if (execute.isLanTraceable()) {				
				writer.writeAttributes(new Attributes().add(new Attribute(Execution.ATTRIBUTE_NAME_LAN_TRACEABLE,
						execute.isLanTraceable())).get());
			}
			if (execute.getLanTimeout() != null)
				writer.writeAttributes(new Attributes().add(new Attribute(Execution.ATTRIBUTE_NAME_LAN_TIMEOUT,
						execute.getLanTimeout())).get());
				
			
			translateAction(execute.getAction(), writer, translatingFactory);
			
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}

		@SuppressWarnings("unchecked")
		private <T> void translateAction(Object action, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			ITranslatorFactory<T> actionTranslatorFactory = new NamingConventionTranslatorFactory<>((Class<T>)action.getClass());			
			actionTranslatorFactory.create().translate((T)action, writer, translatingFactory);
		}
	}

}
