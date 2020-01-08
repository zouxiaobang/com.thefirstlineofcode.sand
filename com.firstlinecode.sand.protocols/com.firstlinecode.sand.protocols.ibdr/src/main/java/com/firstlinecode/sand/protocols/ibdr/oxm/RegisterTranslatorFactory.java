package com.firstlinecode.sand.protocols.ibdr.oxm;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.oxm.translating.IProtocolWriter;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslatingFactory;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslator;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslatorFactory;
import com.firstlinecode.sand.protocols.ibdr.Register;

public class RegisterTranslatorFactory implements ITranslatorFactory<Register> {
	private static final ITranslator<Register> translator = new RegisterTranslator();

	@Override
	public Class<Register> getType() {
		return Register.class;
	}

	@Override
	public ITranslator<Register> create() {
		return translator;
	}
	
	private static class RegisterTranslator implements ITranslator<Register> {
		

		@Override
		public Protocol getProtocol() {
			return Register.PROTOCOL;
		}

		@Override
		public String translate(Register register, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			writer.writeProtocolBegin(Register.PROTOCOL);
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}
		
	}
}
