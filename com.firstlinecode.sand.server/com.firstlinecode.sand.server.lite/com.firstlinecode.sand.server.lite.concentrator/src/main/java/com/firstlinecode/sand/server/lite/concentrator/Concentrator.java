package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;

import com.firstlinecode.sand.server.framework.things.concentrator.IConcentrator;
import com.firstlinecode.sand.server.framework.things.concentrator.Node;
import com.firstlinecode.sand.server.framework.things.concentrator.NodeConfirmationRequest;

public class Concentrator implements IConcentrator {
	private String deviceId;
	private SqlSession sqlSession;
	
	public Concentrator(String deviceId, SqlSession sqlSession) {
		this.deviceId = deviceId;
		this.sqlSession = sqlSession;
	}

	@Override
	public Node[] getNodes(String deviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNode(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirm(String deviceId) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean containsNode(String deviceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestConfirmation(NodeConfirmationRequest request) {
		// TODO Auto-generated method stub
		
	}

}
