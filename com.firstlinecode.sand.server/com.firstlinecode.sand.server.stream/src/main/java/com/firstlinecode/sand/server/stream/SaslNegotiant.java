package com.firstlinecode.sand.server.stream;

import java.util.List;

import com.firstlinecode.basalt.protocol.core.stream.Feature;
import com.firstlinecode.granite.framework.core.auth.IAuthenticator;
import com.firstlinecode.granite.framework.core.connection.IClientConnectionContext;
import com.firstlinecode.granite.framework.core.integration.IMessage;
import com.firstlinecode.granite.framework.core.session.ISessionManager;

public class SaslNegotiant extends com.firstlinecode.granite.framework.stream.negotiants.SaslNegotiant {
	private ISessionManager sessionManager;
	
	public SaslNegotiant(String domainName, String[] supportedMechanisms, int abortRetries, int failureRetries,
			List<Feature> features, IAuthenticator autenticator) {
		super(domainName, supportedMechanisms, abortRetries, failureRetries, features, autenticator);
	}
	
	public void setSessionManager(ISessionManager sessionManager) {
		
	}
	
	@Override
	protected boolean doNegotiate(IClientConnectionContext context, IMessage message) {
		if ()
		return super.doNegotiate(context, message);
	}
}
