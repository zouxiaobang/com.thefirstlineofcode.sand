package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.Attributes;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.convention.annotations.ProtocolObject;
import com.thefirstlineofcode.basalt.oxm.translating.IProtocolWriter;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslator;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;
import com.thefirstlineofcode.sand.protocols.core.ITraceId;

public class LanExecuteTranslatorFactory implements ITranslatorFactory<LanExecute> {

	@Override
	public Class<LanExecute> getType() {
		return LanExecute.class;
	}

	@Override
	public ITranslator<LanExecute> create() {
		return new LanExecutionTranslator();
	}
	
	private class LanExecutionTranslator implements ITranslator<LanExecute> {
		private static final String ELEMENT_NAME_LAN_ACTION_OBJ = "lan-action-obj";
		private static final String ATTRIBUTE_NAME_TRACE_ID = "trace-id";

		@Override
		public Protocol getProtocol() {
			return LanExecute.PROTOCOL;
		}

		@Override
		public String translate(LanExecute lanExecute, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			if (lanExecute.getTraceId() == null)
				throw new IllegalArgumentException("Null trace ID.");
			
			if (lanExecute.getLanActionObj() == null &&
					(lanExecute.getTraceId().getType() != ITraceId.Type.RESPONSE)) {
				throw new IllegalArgumentException("Null LAN execute object not be allowed when LAN execute is request type or error type.");
			}
			
			writer.writeProtocolBegin(LanExecute.PROTOCOL);
			writer.writeAttributes(new Attributes().add(new Attribute(ATTRIBUTE_NAME_TRACE_ID,
					BinaryUtils.encodeToBase64(BinaryUtils.getBytesWithBase64DecodedFlag(
							lanExecute.getTraceId().getBytes())))).get());
			
			ITraceId.Type type = lanExecute.getTraceId().getType();
			if (type == ITraceId.Type.REQUEST) {
				ProtocolObject protocolObject = lanExecute.getLanActionObj().getClass().getAnnotation(ProtocolObject.class);
				if (protocolObject == null)
					throw new IllegalArgumentException("LAN action object must be an protocol object when LAN execute is request type.");
				
				writer.writeString(translatingFactory.translate(lanExecute.getLanActionObj()));
			} else if (type == ITraceId.Type.ERROR) {
				if (!(lanExecute.getLanActionObj() instanceof String)) {
					throw new IllegalArgumentException("LAN action object must be a string when LAN execute is error type.");
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
