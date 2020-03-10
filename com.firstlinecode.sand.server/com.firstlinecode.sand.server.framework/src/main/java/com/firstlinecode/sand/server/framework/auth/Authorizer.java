package com.firstlinecode.sand.server.framework.auth;

import com.firstlinecode.granite.framework.core.auth.IAuthenticator;
import com.firstlinecode.granite.framework.core.auth.PrincipalNotFoundException;

public class Authorizer implements IAuthenticator {

	@Override
	public Object getCredentials(Object principal) throws PrincipalNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Object principal) {
		// TODO Auto-generated method stub
		return false;
	}

}
