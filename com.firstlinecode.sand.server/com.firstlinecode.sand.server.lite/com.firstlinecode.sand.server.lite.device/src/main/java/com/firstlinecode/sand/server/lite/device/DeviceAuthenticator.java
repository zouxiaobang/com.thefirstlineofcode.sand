package com.firstlinecode.sand.server.lite.device;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.firstlinecode.granite.framework.core.auth.IAuthenticator;
import com.firstlinecode.sand.server.framework.things.Device;

@Transactional
@Component
public class DeviceAuthenticator implements IAuthenticator {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public Object getCredentials(Object principal) {
		Device device = getDeviceMapper().selectByDeviceName((String)principal);
		if (device != null)
			return device.getIdentity().getCredentials();
		
		return null;
	}

	@Override
	public boolean exists(Object principal) {
		return getDeviceMapper().selectCountByDeviceName((String)principal) != 0;
	}
	
	private DeviceMapper getDeviceMapper() {
		return sqlSession.getMapper(DeviceMapper.class);
	}

}
