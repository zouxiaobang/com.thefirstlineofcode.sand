package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import java.util.Arrays;
import java.util.List;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.Value;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.parsing.ElementParserAdaptor;
import com.thefirstlineofcode.basalt.oxm.parsing.IElementParser;
import com.thefirstlineofcode.basalt.oxm.parsing.IParser;
import com.thefirstlineofcode.basalt.oxm.parsing.IParserFactory;
import com.thefirstlineofcode.basalt.oxm.parsing.IParsingContext;
import com.thefirstlineofcode.basalt.oxm.parsing.IParsingPath;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.BadRequest;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecution;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class LanExecutionParserFactory implements IParserFactory<LanExecution> {
	private ITraceIdFactory traceIdFactory;
	
	public LanExecutionParserFactory(ITraceIdFactory traceIdFactory) {
		this.traceIdFactory = traceIdFactory;
	}
	
	@Override
	public Protocol getProtocol() {
		return LanExecution.PROTOCOL;
	}

	@Override
	public IParser<LanExecution> create() {
		return new LanExecutionParser();
	}
	
	private class LanExecutionParser implements IParser<LanExecution> {
		private static final String PARSING_PATH_LAN_ACTION_OBJ = "/lan-action-obj";
		private static final String ATTRIBUTE_NAME_TRACE_ID = "trace-id";

		@Override
		public LanExecution createObject() {
			return new LanExecution();
		}

		@Override
		public IElementParser<LanExecution> getElementParser(IParsingPath parsingPath) {
			if (parsingPath.match("/")) {
				return new ElementParserAdaptor<LanExecution>() {
					@Override
					public void processAttributes(IParsingContext<LanExecution> context, List<Attribute> attributes) {
						if (attributes.size() != 1 || !ATTRIBUTE_NAME_TRACE_ID.equals(attributes.get(0).getLocalName())) {
							throw new ProtocolException(new BadRequest("No trace ID found."));
						}
						
						String sTraceId = attributes.get(0).getValue().getString();
						if (!BinaryUtils.isBase64Encoded(sTraceId))
							throw new RuntimeException("Attribute value of trace ID must be Base64 encoded string.");
						
						byte[] bytes = BinaryUtils.decodeFromBase64(sTraceId);
						context.getObject().setTraceId(traceIdFactory.create(Arrays.copyOfRange(bytes, 2, bytes.length)));
					}
				};
			} else if (parsingPath.match(PARSING_PATH_LAN_ACTION_OBJ)) {
				return new ElementParserAdaptor<LanExecution>() {
					public void processText(IParsingContext<LanExecution> context, Value<?> text) {
						if (context.getObject().getTraceId().getType() != ITraceId.Type.ERROR) {
							throw new ProtocolException(new BadRequest("Text for element lan-action-obj mustn't be allowed because the type of LAN execute object isn't error."));
						}
						
						context.getObject().setLanActionObj(text.getString());
					}
				};
			} else {
				throw new ProtocolException(new BadRequest(String.format("An invalid element found: '%s'.", parsingPath.toString())));
			}
		}

		@Override
		public void processEmbeddedObject(IParsingContext<LanExecution> context, Protocol protocol, Object embedded) {
			if (context.getObject().getTraceId().getType() != ITraceId.Type.REQUEST)
				throw new ProtocolException(new BadRequest("Embedded object not be allowed bacause the type of LAN execution object isn't request."));
				
			context.getObject().setLanActionObj(embedded);
		}
	}

}
