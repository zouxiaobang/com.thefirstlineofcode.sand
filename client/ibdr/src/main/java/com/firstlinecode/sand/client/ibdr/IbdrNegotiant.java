package com.firstlinecode.sand.client.ibdr;

import java.util.List;

import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.chalk.core.stream.AbstractStreamer;
import com.firstlinecode.chalk.core.stream.INegotiationContext;
import com.firstlinecode.chalk.core.stream.NegotiationException;
import com.firstlinecode.chalk.core.stream.negotiants.AbstractStreamNegotiant;
import com.firstlinecode.chalk.network.ConnectionException;
import com.firstlinecode.sand.protocols.ibdr.Register;

public class IbdrNegotiant extends AbstractStreamNegotiant {
	
	
	@Override
	protected void doNegotiate(INegotiationContext context) throws ConnectionException, NegotiationException {
		@SuppressWarnings("unchecked")
		List<Feature> features = (List<Feature>)context.getAttribute(AbstractStreamer.NEGOTIATION_KEY_FEATURES);
		
		for (Feature feature : features) {
			if (feature instanceof Register) {
				return;
			}
		}
		
		throw new NegotiationException(this, IbdrError.NOT_SUPPORTED);
	}

}
