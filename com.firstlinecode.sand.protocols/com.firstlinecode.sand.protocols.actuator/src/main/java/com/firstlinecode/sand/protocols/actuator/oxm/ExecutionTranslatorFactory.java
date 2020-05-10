package com.firstlinecode.sand.protocols.actuator.oxm;

import com.firstlinecode.basalt.oxm.convention.NamingConventionTranslatorFactory;
import com.firstlinecode.basalt.oxm.translating.IProtocolWriter;
import com.firstlinecode.basalt.oxm.translating.ITranslatingFactory;
import com.firstlinecode.basalt.oxm.translating.ITranslator;
import com.firstlinecode.basalt.oxm.translating.ITranslatorFactory;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionTranslatorFactory implements ITranslatorFactory<Execute> {
	private static final ITranslator<Execute> translator = new ExecutionTranslator();

	@Override
	public Class<Execute> getType() {
		return Execute.class;
	}

	@Override
	public ITranslator<Execute> create() {
		return translator;
	}
	
	private static class ExecutionTranslator implements ITranslator<Execute> {

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
			translate(execute.getAction(), writer, translatingFactory);
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}

		@SuppressWarnings("unchecked")
		private <T> void translate(Object action, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			Class<T> actionType = (Class<T>)action.getClass();
			ITranslatorFactory<T> actionTranslatorFactory = createCustomActionTranslatorFactory(actionType);
			if (actionTranslatorFactory == null) {				
				actionTranslatorFactory = new NamingConventionTranslatorFactory<>(actionType);
			}
			
			actionTranslatorFactory.create().translate((T)action, writer, translatingFactory);
		}

		@SuppressWarnings("unchecked")
		private <T> ITranslatorFactory<T> createCustomActionTranslatorFactory(Class<T> actionType) {
			String customActionTranslatorFactoryName = String.format("%s.%s.%s", actionType.getPackage().getName(),
					actionType.getSimpleName(), "TranslatorFactory");
			try {
				Class<ITranslatorFactory<T>> customActionTranslatorFactoryType = (Class<ITranslatorFactory<T>>)Class.forName(customActionTranslatorFactoryName);
				return customActionTranslatorFactoryType.newInstance();
			} catch (Exception e) {
				// Ignore
			}
			
			return null;
		}
	}

}
