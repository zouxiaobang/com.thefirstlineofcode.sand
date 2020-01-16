package com.firstlinecode.sand.server.lite.auth;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.granite.framework.core.auth.IAuthenticator;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;

@Transactional
@Component
public class DeviceAuthenticator implements IAuthenticator {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public Object getCredentials(Object principal) {
		DeviceIdentityMapper mapper = sqlSession.getMapper(DeviceIdentityMapper.class);
		
		DeviceIdentity deviceIdentity = mapper.selectByJid((JabberId)principal);
		if (deviceIdentity != null)
			return deviceIdentity.getCredentials();
		
		return null;
	}

	@Override
	public boolean exists(Object principal) {
		DeviceIdentityMapper mapper = sqlSession.getMapper(DeviceIdentityMapper.class);
		int count = mapper.selectCountByJid((JabberId)principal);
		
		return count != 0;
	}

}
