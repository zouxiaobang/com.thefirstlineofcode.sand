package com.thefirstlineofcode.sand.protocols.actuator.oxm;

import com.thefirstlineofcode.basalt.oxm.Attribute;
import com.thefirstlineofcode.basalt.oxm.Attributes;
import com.thefirstlineofcode.basalt.oxm.binary.BinaryUtils;
import com.thefirstlineofcode.basalt.oxm.translating.IProtocolWriter;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatingFactory;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslator;
import com.thefirstlineofcode.basalt.oxm.translating.ITranslatorFactory;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.sand.protocols.actuator.LanExecute;

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
		private static final String ATTRIBUTE_NAME_TRACE_ID = "trace-id";

		@Override
		public Protocol getProtocol() {
			return LanExecute.PROTOCOL;
		}

		@Override
		public String translate(LanExecute lanExecute, IProtocolWriter writer, ITranslatingFactory translatingFactory) {
			writer.writeProtocolBegin(LanExecute.PROTOCOL);
			writer.writeAttributes(new Attributes().add(new Attribute(ATTRIBUTE_NAME_TRACE_ID,
					BinaryUtils.encodeToBase64(lanExecute.getTraceId().getBytes()))).get());
			writer.writeString(translatingFactory.translate(lanExecute.getLanActionObj()));
			writer.writeProtocolEnd();
			
			return writer.getDocument();
		}
		
	}

}
