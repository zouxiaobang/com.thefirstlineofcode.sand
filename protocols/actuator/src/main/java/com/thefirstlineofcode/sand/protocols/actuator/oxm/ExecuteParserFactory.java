package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import java.util.List;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.parsing.ElementParserAdaptor;
import com.thefirstlineofcode.basalt.oxm.parsing.IElementParser;
import com.thefirstlineofcode.basalt.oxm.parsing.IParser;
import com.thefirstlineofcode.basalt.oxm.parsing.IParserFactory;
import com.thefirstlineofcode.basalt.oxm.parsing.IParsingContext;
import com.thefirstlineofcode.basalt.oxm.parsing.IParsingPath;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;

public class ExecuteParserFactory implements IParserFactory<Execute> {
	@Override
	public Protocol getProtocol() {
		return Execute.PROTOCOL;
	}

	@Override
	public IParser<Execute> create() {
		return new ExecuteParser();
	}
	
	private class ExecuteParser implements IParser<Execute> {
		@Override
		public Execute createObject() {
			return new Execute();
		}

		@Override
		public IElementParser<Execute> getElementParser(IParsingPath parsingPath) {
			if (parsingPath.match("/")) {
				return new ElementParserAdaptor<Execute>() {
					@Override
					public void processAttributes(IParsingContext<Execute> context, List<Attribute> attributes) {
						if (attributes.size() == 0) {
							return;
						}
						
						for (Attribute attribute : attributes) {
							if (Execute.ATTRIBUTE_NAME_LAN_TRACEABLE.equals(attribute.getName())) {							
								boolean lanTraceable = Boolean.valueOf(attribute.getValue().stringIt().get());								
								context.getObject().setLanTraceable(lanTraceable);
							} else if (Execute.ATTRIBUTE_NAME_LAN_TIMEOUT.equals(attribute.getName())) {
								Integer timeout = Integer.valueOf(attribute.getValue().stringIt().get());
								context.getObject().setLanTimeout(timeout);
							} else {
								throw new ProtocolException(new BadRequest("Only optional attributes 'lan-traceable' and 'lan-timeout' are allowed in Execute."));						
							}
						}
					}
				};
			} else {
				throw new ProtocolException(new BadRequest(String.format("An invalid element found: '%s'.", parsingPath.toString())));
			}
		}

		@Override
		public void processEmbeddedObject(IParsingContext<Execute> context, Protocol protocol, Object embedded) {
			context.getObject().setAction(embedded);
		}
		
	}

}
