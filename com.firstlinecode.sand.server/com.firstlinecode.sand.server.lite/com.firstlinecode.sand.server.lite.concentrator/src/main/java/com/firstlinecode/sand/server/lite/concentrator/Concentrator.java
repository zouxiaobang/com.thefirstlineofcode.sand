package com.firstlinecode.sand.server.lite.concentrator;

import org.apache.ibatis.session.SqlSession;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.Conflict;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.concentrator.NodeConfirmation;

public class Concentrator implements IConcentrator {
	private String deviceId;
	private SqlSession sqlSession;
	
	public Concentrator(String deviceId, SqlSession sqlSession) {
		this.deviceId = deviceId;
		this.sqlSession = sqlSession;
	}

	@Override
	public Node[] getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirm(String node) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean containsNode(String node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void requestConfirmation(NodeConfirmation confirmation) {
		if (!deviceId.equals(confirmation.getNode().getConcentrator())) {
			throw new ProtocolException(new NotAcceptable("Wrong device ID of concentrator. Your program maybe has a bug."));
		}
		
		if (containsNode(confirmation.getNode().getNode())) {
			throw new ProtocolException(new Conflict());
		}
		
		getNodeComfirmationMapper().insert(confirmation);;
	}
	
	private NodeConfirmationMapper getNodeComfirmationMapper() {
		return sqlSession.getMapper(NodeConfirmationMapper.class);
	}

}
