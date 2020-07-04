package com.firstlinecode.sand.demo.server;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.Iq;
import com.firstlinecode.basalt.protocol.core.stanza.error.BadRequest;
import com.firstlinecode.granite.framework.processing.IProcessingContext;
import com.firstlinecode.granite.framework.processing.IXepProcessor;
import com.firstlinecode.sand.demo.protocols.AccessControlList;

public class AccessControlListProcessor implements IXepProcessor<Iq, AccessControlList> {

	@Override
	public void process(IProcessingContext context, Iq iq, AccessControlList xep) {
		// TODO Auto-generated method stub
		if (iq.getType() != Iq.Type.GET)
			throw new ProtocolException(new BadRequest("Attribute 'type' should be set to 'get'."));
		
		if (xep.getEntries() != null)
			throw new ProtocolException(new BadRequest("Access control list entries must be null."));
		
		
	}

}
