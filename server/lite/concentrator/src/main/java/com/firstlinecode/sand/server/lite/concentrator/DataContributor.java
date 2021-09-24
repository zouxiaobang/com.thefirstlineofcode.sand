package com.firstlinecode.sand.server.lite.concentrator;

import org.pf4j.Extension;

import com.firstlinecode.granite.framework.adf.mybatis.DataContributorAdapter;

@Extension
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
			"ConcentrationMapper.xml",
			"NodeConfirmationMapper.xml"
		};
	}
}
