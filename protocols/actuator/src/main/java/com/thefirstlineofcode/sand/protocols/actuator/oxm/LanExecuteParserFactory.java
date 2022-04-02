package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import java.util.Arrays;
import java.util.List;

import com.thefirstlineofcode.basalt.oxm.Attribute;
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
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;
import com.thefirstlineofcode.sand.protocols.core.ITraceIdFactory;

public class LanExecuteParserFactory implements IParserFactory<LanExecute> {
	private ITraceIdFactory traceIdFactory;
	
	public LanExecuteParserFactory(ITraceIdFactory traceIdFactory) {
		this.traceIdFactory = traceIdFactory;
	}
	
	@Override
	public Protocol getProtocol() {
		return LanExecute.PROTOCOL;
	}

	@Override
	public IParser<LanExecute> create() {
		return new LanExecutionParser();
	}
	
	private class LanExecutionParser implements IParser<LanExecute> {
		private static final String ATTRIBUTE_NAME_TRACE_ID = "trace-id";

		@Override
		public LanExecute createObject() {
			return new LanExecute();
		}

		@Override
		public IElementParser<LanExecute> getElementParser(IParsingPath parsingPath) {
			if (parsingPath.match("/")) {
				return new ElementParserAdaptor<LanExecute>() {
					@Override
					public void processAttributes(IParsingContext<LanExecute> context, List<Attribute> attributes) {
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
			} else {
				throw new ProtocolException(new BadRequest(String.format("An invalid element found: '%s'.", parsingPath.toString())));
			}
		}

		@Override
		public void processEmbeddedObject(IParsingContext<LanExecute> context, Protocol protocol, Object embedded) {
			context.getObject().setLanActionObj(embedded);
		}	
	}

}
