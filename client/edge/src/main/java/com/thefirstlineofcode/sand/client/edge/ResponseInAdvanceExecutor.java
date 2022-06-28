package com.thefirstlineofcode.sand.client.edge;

import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Stanza;
import com.thefirstlineofcode.chalk.core.IChatClient;
import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.client.core.actuator.IExecutor;
import com.thefirstlineofcode.sand.client.core.concentrator.IConcentrator;

public class ResponseInAdvanceExecutor<T> implements IExecutor<T> {
	private IExecutor<T> original;
	private IThing thing;
	
	public ResponseInAdvanceExecutor(IExecutor<T> original, IThing thing) {
		this.original = original;
		this.thing = thing;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(Iq iq, Object action) throws ProtocolException {
		if (thing instanceof IEdgeThing) {
			IEdgeThing edge = (IEdgeThing)thing;
			IChatClient chatClient = edge.getChatClient();
			
			Iq result = new Iq(Iq.Type.RESULT, iq.getId());
			setFromToAddresses(iq.getFrom(), iq.getTo(), result);
			chatClient.getChatServices().getIqService().send(result);
		}
		
		return original.execute(iq, (T)action);
	}
	
	private void setFromToAddresses(JabberId from, JabberId to, Stanza stanza) {
		if (toLanNode(to))
			stanza.setFrom(to.getBareId());
		
		if (from != null && !((IEdgeThing)thing).getStreamConfig().getHost().equals(from.toString())) {
			stanza.setTo(from);
		}
	}
	
	private boolean toLanNode(JabberId to) {
		return to != null && to.getResource() != null &&
				!IConcentrator.LAN_ID_CONCENTRATOR.equals(to.getResource());
	}
	
}

