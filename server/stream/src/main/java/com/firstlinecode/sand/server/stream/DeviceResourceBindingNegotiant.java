package com.firstlinecode.sand.server.stream;

import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.granite.framework.core.pipeline.IMessage;
import com.firstlinecode.granite.framework.core.session.ISessionManager;
import com.firstlinecode.granite.pipeline.stages.stream.StreamConstants;
import com.firstlinecode.granite.pipeline.stages.stream.negotiants.ResourceBindingNegotiant;
import com.thefirstlineofcode.basalt.protocol.core.JabberId;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAuthorized;
import com.thefirstlineofcode.basalt.protocol.core.stream.Bind;
import com.thefirstlineofcode.basalt.protocol.core.stream.Stream;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.Conflict;
import com.thefirstlineofcode.basalt.protocol.core.stream.error.InternalServerError;

public class DeviceResourceBindingNegotiant extends ResourceBindingNegotiant {
	private static final String RESOURCE_DEVICE = "device";

	public DeviceResourceBindingNegotiant(String domainName, ISessionManager sessionManager) {
		super(domainName, sessionManager);
	}
	
	@Override
	protected boolean doNegotiate(IClientConnectionContext context, IMessage message) {
		Object request = oxmFactory.parse((String)message.getPayload());
		if (request instanceof Iq) {
			Iq iq = (Iq)request;
			if (iq.getObject() instanceof Bind) {
				Bind bind = iq.getObject();
				
				String authorizationId = context.removeAttribute(StreamConstants.KEY_AUTHORIZATION_ID);
				JabberId jid = generatedDeviceJidByServer(context, authorizationId);
				
				if (sessionManager.exists(jid)) {
					context.write(new Conflict());
					context.write(new Stream(true));
					context.close(true);
					
					return true;
				}
				
				Iq response = new Iq(Iq.Type.RESULT);
				response.setId(iq.getId());
				bind = new Bind(jid);
				response.setObject(bind);
				
				context.setAttribute(StreamConstants.KEY_BINDED_JID, jid);
				context.write(oxmFactory.translate(response));
			} else {
				throw new ProtocolException(new NotAuthorized("Not a resource binding request."));
			}
		} else {
			throw new ProtocolException(new NotAuthorized("Not a resource binding request."));
		}
		
		return true;
	}
	
	protected JabberId generatedDeviceJidByServer(IClientConnectionContext context, String authorizationId) {
		JabberId jid = JabberId.parse(String.format("%s@%s/%s", authorizationId, domainName, RESOURCE_DEVICE));
			
		if (sessionManager.exists(jid)) {
			// TODO. Should close existed resource.
			throw new ProtocolException(new Conflict(String.format("Conflict JID: %s", jid)));
		}
		
		if (jid == null) {
			throw new ProtocolException(new InternalServerError("Failed to generate a resource for binding."));
		}
		
		return jid;
	}

}
