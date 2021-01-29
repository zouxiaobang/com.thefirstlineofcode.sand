package com.firstlinecode.sand.protocols.actuator.oxm;

import java.util.List;

import com.firstlinecode.basalt.oxm.Attribute;
import com.firstlinecode.basalt.oxm.parsing.ElementParserAdaptor;
import com.firstlinecode.basalt.oxm.parsing.IElementParser;
import com.firstlinecode.basalt.oxm.parsing.IParser;
import com.firstlinecode.basalt.oxm.parsing.IParserFactory;
import com.firstlinecode.basalt.oxm.parsing.IParsingContext;
import com.firstlinecode.basalt.oxm.parsing.IParsingPath;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionParserFactory implements IParserFactory<Execute> {
	@Override
	public Protocol getProtocol() {
		return Execute.PROTOCOL;
	}

	@Override
	public IParser<Execute> create() {
		return new ExecutionParser();
	}
	
	private class ExecutionParser implements IParser<Execute> {
		
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
						if (attributes.size() != 0) {
							throw new ProtocolException(new BadRequest("Execute object mustn't has any attributes."));
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
