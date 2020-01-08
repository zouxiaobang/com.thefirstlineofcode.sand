package com.firstlinecode.sand.protocols.ibdr.oxm;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.oxm.Value;
import com.firstlinecode.basalt.protocol.oxm.parsing.ElementParserAdaptor;
import com.firstlinecode.basalt.protocol.oxm.parsing.IElementParser;
import com.firstlinecode.basalt.protocol.oxm.parsing.IParser;
import com.firstlinecode.basalt.protocol.oxm.parsing.IParserFactory;
import com.firstlinecode.basalt.protocol.oxm.parsing.IParsingContext;
import com.firstlinecode.basalt.protocol.oxm.parsing.IParsingPath;
import com.firstlinecode.basalt.protocol.oxm.parsing.ParserAdaptor;
import com.firstlinecode.sand.protocols.ibdr.DeviceRegister;

public class DeviceRegisterParserFactory implements IParserFactory<DeviceRegister> {
	@Override
	public Protocol getProtocol() {
		return DeviceRegister.PROTOCOL;
	}

	@Override
	public IParser<DeviceRegister> create() {
		return new DeviceRegisterParser();
	}
	
	private static class DeviceRegisterParser extends ParserAdaptor<DeviceRegister> {
		public DeviceRegisterParser() {
			super(DeviceRegister.class);
		}
		
		@Override
		public IElementParser<DeviceRegister> getElementParser(IParsingPath parsingPath) {
			if (parsingPath.match("/")) {
				return new ElementParserAdaptor<>();
			} else if (parsingPath.match("/device-id")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					@Override
					public void processText(IParsingContext<DeviceRegister> context, Value<?> text) {
						context.getObject().setRegister(text.getString());
					}
				};
			} else if (parsingPath.match("/jid")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					@Override
					public void processText(IParsingContext<DeviceRegister> context, Value<?> text) {
						context.getObject().setRegister(JabberId.parse(text.getString()));
					}
				};
			} else {
				return super.getElementParser(parsingPath);
			}
		}
	}
}
