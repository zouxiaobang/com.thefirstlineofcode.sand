package com.firstlinecode.sand.server.lite.devices;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.adf.mybatis.DataContributorAdapter;

@Extension
public class DataContributor extends DataContributorAdapter {
	@Override
	protected Class<?>[] getDataObjects() {
		return new Class<?>[] {
			D_Device.class,
			D_DeviceAuthorization.class,
			D_DeviceIdentity.class
		};		
	}
	
	@Override
	protected String[] getInitScriptFileNames() {
		return new String[] {"device.sql"};
	}
	
	@Override
	protected String[] getMapperFileNames() {
		return new String[] {
			"DeviceAuthorizationMapper.xml",
			"DeviceIdentityMapper.xml",
			"DeviceMapper.xml"
		};
	}
}
