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
		return new ExecutionParser();
	}
	
	private class ExecutionParser implements IParser<Execute> {
		
		private static final String ATTRIBUTE_NAME_LAN_TRACEABLE = "lan-traceable";

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
						} else if (attributes.size() == 1 && ATTRIBUTE_NAME_LAN_TRACEABLE.equals(attributes.get(0).getName())) {							
							boolean lanTraceable = Boolean.valueOf(attributes.get(0).getValue().stringIt().get());								
							context.getObject().setLanTraceable(lanTraceable);
						} else {
							throw new ProtocolException(new BadRequest("Only an optional attribute 'lan-traceable' is allowed in Execute."));						
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
