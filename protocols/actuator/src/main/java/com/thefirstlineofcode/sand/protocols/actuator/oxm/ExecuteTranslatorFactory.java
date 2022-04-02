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
import com.thefirstlineofcode.sand.protocols.actuator.Execute;

public class ExecuteTranslatorFactory implements ITranslatorFactory<Execute> {
	private static final ITranslator<Execute> translator = new ExecuteTranslator();

	@Override
	public Class<Execute> getType() {
		return Execute.class;
	}

	@Override
	public ITranslator<Execute> create() {
		return translator;
	}
	
	private static class ExecuteTranslator implements ITranslator<Execute> {

		private static final String ATTRIBUTE_NAME_LAN_TRACEABLE = "lan-traceable";

		@Override
		public Protocol getProtocol() {
			return Execute.PROTOCOL;
		}

		@Override
		public String translate(Execute execute, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			if (execute.getAction() == null) {
				throw new ProtocolException(new BadRequest("Null action."));
			}
			
			writer.writeProtocolBegin(Execute.PROTOCOL);
			if (execute.isLanTraceable()) {				
				writer.writeAttributes(new Attributes().add(new Attribute(ATTRIBUTE_NAME_LAN_TRACEABLE, execute.isLanTraceable())).get());
			}
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
