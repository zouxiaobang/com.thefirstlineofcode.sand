package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.granite.framework.adf.mybatis.DataContributorAdapter;

public class DataContributor extends DataContributorAdapter {
	@Override
	protected Class<?>[] getDataObjects() {
		return new Class<?>[] {
			D_NodeConfirmation.class
		};
	}
	
	@Override
	protected String[] getInitScriptFileNames() {
		return new String[] {"concentrator.sql"};
	}
	
	@Override
	protected String[] getMapperFileNames() {
		return new String[] {
			"ConcentratorMapper.xml",
			"ConfirmRequestMapper.xml"
		};
	}
}
