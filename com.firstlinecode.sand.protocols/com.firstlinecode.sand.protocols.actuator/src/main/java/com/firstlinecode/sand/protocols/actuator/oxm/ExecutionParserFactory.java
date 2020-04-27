package com.firstlinecode.sand.protocols.actuator.oxm;

import com.firstlinecode.basalt.oxm.parsing.IElementParser;
import com.firstlinecode.basalt.oxm.parsing.IParser;
import com.firstlinecode.basalt.oxm.parsing.IParserFactory;
import com.firstlinecode.basalt.oxm.parsing.IParsingContext;
import com.firstlinecode.basalt.oxm.parsing.IParsingPath;
import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.sand.protocols.actuator.Execute;

public class ExecutionParserFactory implements IParserFactory<Execute> {

	@Override
	public Protocol getProtocol() {
		return Execute.PROTOCOL;
	}

	@Override
	public IParser<Execute> create() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class ExecutionParser implements IParser<Execute> {
		
		@Override
		public Execute createObject() {
			return new Execute();
		}

		@Override
		public IElementParser<Execute> getElementParser(IParsingPath parsingPath) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void processEmbeddedObject(IParsingContext<Execute> context, Protocol protocol, Object embedded) {
			context.getObject().setAction(embedded);
		}
		
	}

}
