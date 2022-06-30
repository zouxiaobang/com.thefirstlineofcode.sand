package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.Attributes;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.translating.IProtocolWriter;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslator;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatorFactory;
import com.thefirstlineofcode.basalt.xmpp.core.Protocol;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecution;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;

public class LanExecutionTranslatorFactory implements ITranslatorFactory<LanExecution> {

	@Override
	public Class<LanExecution> getType() {
		return LanExecution.class;
	}

	@Override
	public ITranslator<LanExecution> create() {
		return new LanExecutionTranslator();
	}
	
	private class LanExecutionTranslator implements ITranslator<LanExecution> {
		private static final String ELEMENT_NAME_LAN_ACTION_OBJ = "lan-action-obj";
		private static final String ATTRIBUTE_NAME_TRACE_ID = "trace-id";

		@Override
		public Protocol getProtocol() {
			return LanExecution.PROTOCOL;
		}

		@Override
		public String translate(LanExecution lanExecute, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			if (lanExecute.getTraceId() == null)
				throw new IllegalArgumentException("Null trace ID.");
			
			if (lanExecute.getLanActionObj() == null &&
					(lanExecute.getTraceId().getType() != ITraceId.Type.RESPONSE)) {
				throw new IllegalArgumentException("Null LAN action object not be allowed when LAN execution is request type or error type.");
			}
			
			writer.writeProtocolBegin(LanExecution.PROTOCOL);
			writer.writeAttributes(new Attributes().add(new Attribute(ATTRIBUTE_NAME_TRACE_ID,
					BinaryUtils.encodeToBase64(BinaryUtils.getBytesWithBase64DecodedFlag(
							lanExecute.getTraceId().getBytes())))).get());
			
			ITraceId.Type type = lanExecute.getTraceId().getType();
			if (type == ITraceId.Type.REQUEST) {
				ProtocolObject protocolObject = lanExecute.getLanActionObj().getClass().getAnnotation(ProtocolObject.class);
				if (protocolObject == null)
					throw new IllegalArgumentException("LAN action object must be an protocol object when LAN execution is request type.");
				
				writer.writeString(translatingFactory.translate(lanExecute.getLanActionObj()));
			} else if (type == ITraceId.Type.ERROR) {
				if (!(lanExecute.getLanActionObj() instanceof String)) {
					throw new IllegalArgumentException("LAN action object must be a string when LAN execution is error type.");
				}
				
				writer.writeElementBegin(ELEMENT_NAME_LAN_ACTION_OBJ);
				writer.writeText((String)lanExecute.getLanActionObj());
				writer.writeElementEnd();
			} else {
				// NO-OP
			}
			
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}
	}

}
