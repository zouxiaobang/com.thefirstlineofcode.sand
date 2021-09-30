package com.firstlinecode.sand.protocols.ibdr.oxm;

import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;
import com.thefirstlineofcode.basalt.oxm.Value;
import com.thefirstlineofcode.basalt.oxm.translating.IProtocolWriter;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslator;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;

public class DeviceRegisterTranslatorFactory implements ITranslatorFactory<DeviceRegister> {
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
			} else if (register instanceof DeviceIdentity) {
				DeviceIdentity deviceIdentity = (DeviceIdentity)register;
				writer.writeElementBegin("device-identity").
					writeTextOnly("device-name", deviceIdentity.getDeviceName()).
					writeTextOnly("credentials", deviceIdentity.getCredentials()).
				writeElementEnd();
			} else {
				throw new RuntimeException("Unknown register object.");
			}
			
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}
		
	}

}
