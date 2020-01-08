package com.firstlinecode.sand.protocols.ibdr.oxm;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.oxm.Value;
import com.firstlinecode.basalt.protocol.oxm.translating.IProtocolWriter;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslatingFactory;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslator;
import com.firstlinecode.basalt.protocol.oxm.translating.ITranslatorFactory;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;

public class IqRegisterTranslatorFactory implements ITranslatorFactory<DeviceRegister> {
	private ITranslator<DeviceRegister> translator = new DeviceRegisterTranslator();

	@Override
	public Class<DeviceRegister> getType() {
		return DeviceRegister.class;
	}

	@Override
	public ITranslator<DeviceRegister> create() {
		return translator;
	}
	
	private class DeviceRegisterTranslator implements ITranslator<DeviceRegister> {

		@Override
		public Protocol getProtocol() {
			return DeviceRegister.PROTOCOL;
		}

		@Override
		public String translate(DeviceRegister iqRegister, IProtocolWriter writer,
				ITranslatingFactory translatingFactory) {
			writer.writeProtocolBegin(DeviceRegister.PROTOCOL);
			
			Object register = iqRegister.getRegister();
			
			if (register == null) {
				throw new RuntimeException("Null register object.");
			}
			
			if (register instanceof String) {
				writer.writeElementBegin("device-id").writeText(Value.create((String)register)).writeElementEnd();
			} else if (register instanceof JabberId) {
				writer.writeElementBegin("jid").writeText(Value.create(((JabberId)register).toString())).writeElementEnd();
			} else {
				throw new RuntimeException("Unknown register object.");
			}
			
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}
		
	}

}
