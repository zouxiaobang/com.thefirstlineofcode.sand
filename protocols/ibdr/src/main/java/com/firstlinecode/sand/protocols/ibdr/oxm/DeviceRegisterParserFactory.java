package com.firstlinecode.sand.protocols.ibdr.oxm;

import java.util.List;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.basalt.oxm.Attribute;
import com.firstlinecode.basalt.oxm.Value;
import com.firstlinecode.basalt.oxm.parsing.ElementParserAdaptor;
import com.firstlinecode.basalt.oxm.parsing.IElementParser;
import com.firstlinecode.basalt.oxm.parsing.IParser;
import com.firstlinecode.basalt.oxm.parsing.IParserFactory;
import com.firstlinecode.basalt.oxm.parsing.IParsingContext;
import com.firstlinecode.basalt.oxm.parsing.IParsingPath;
import com.firstlinecode.basalt.oxm.parsing.ParserAdaptor;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
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
			} else if (parsingPath.match("device-id")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					public void processText(IParsingContext<DeviceRegister> context, Value<?> text) {
						if (context.getObject().getRegister() != null)
							throw new ProtocolException(new BadRequest("Device registration document allows only one subelement."));
						
						context.getObject().setRegister(text.getString());
					};
				};
			} else if (parsingPath.match("/device-identity")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					@Override
					public void processAttributes(IParsingContext<DeviceRegister> context, List<Attribute> attributes) {
						if (context.getObject().getRegister() != null)
							throw new ProtocolException(new BadRequest("Device registration document allows only one subelement."));
						
						context.getObject().setRegister(new DeviceIdentity());
					}
				};
			} else if (parsingPath.match("/device-identity/device-name")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					@Override
					public void processText(IParsingContext<DeviceRegister> context, Value<?> text) {
						DeviceIdentity identity = (DeviceIdentity)context.getObject().getRegister();
						identity.setDeviceName(text.getString());
					}
				};
			} else if (parsingPath.match("/device-identity/credentials")) {
				return new ElementParserAdaptor<DeviceRegister>() {
					@Override
					public void processText(IParsingContext<DeviceRegister> context, Value<?> text) {
						DeviceIdentity identity = (DeviceIdentity)context.getObject().getRegister();
						identity.setCredentials(text.getString());
					}
				};
			} else {
				return super.getElementParser(parsingPath);
			}
		}
	}
}
