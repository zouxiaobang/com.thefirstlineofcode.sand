package com.thefirstlineofcode.sand.server.lite.concentrator;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;

import com.thefirstlinelinecode.sand.protocols.concentrator.NodeCreated;
import com.thefirstlineofcode.basalt.protocol.core.ProtocolException;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.Conflict;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactory;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.sand.server.concentrator.Confirmed;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.concentrator.NodeConfirmation;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

public class Concentrator implements IConcentrator, IDataObjectFactoryAware {
	private String deviceId;
	private SqlSession sqlSession;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	private IDataObjectFactory dataObjectFactory;
	
	public Concentrator(String deviceId, SqlSession sqlSession) {
		this.deviceId = deviceId;
		this.sqlSession = sqlSession;
	}
	
	@Override
	public Node getNode(String lanId) {
		return getConcentrationMapper().selectNodeByLanId(deviceId, lanId);
	}

	@Override
	public Node[] getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Confirmed confirm(String nodeDeviceId, String confirmer) {
		if (!deviceManager.isValid(nodeDeviceId))
			throw new RuntimeException(String.format("Invalid node device ID '%s'.", nodeDeviceId));
		
		if (containsNode(nodeDeviceId))
			throw new ProtocolException(new Conflict(String.format("Duplicated node which's ID is '%s'.", nodeDeviceId)));
		
		D_NodeConfirmation confirmation = getNodeConfirmation(deviceId, nodeDeviceId);
		if (confirmation == null) {
			throw new ProtocolException(new NotAcceptable("No node confirmation found."));
		}
		
		String model = deviceManager.getModel(nodeDeviceId);
		if (model == null) {
			throw new ProtocolException(new NotAcceptable(String.format("Unsupported model '%s'.", model)));
		}
		
		if (containsLanId(nodeDeviceId))
			throw new ProtocolException(new Conflict(String.format("Duplicated LAN ID('%'). The node's ID is '%s'.",
					confirmation.getNode().getLanId(), nodeDeviceId)));
		
		Device device = dataObjectFactory.create(Device.class);
		device.setDeviceId(nodeDeviceId);
		device.setModel(deviceManager.getModel(nodeDeviceId));
		device.setRegistrationTime(Calendar.getInstance().getTime());
		deviceManager.create(device);
		
		Concentration concentration = new Concentration();
		concentration.setId(UUID.randomUUID().toString());
		concentration.setConcentrator(confirmation.getConcentrator());
		concentration.setNode(confirmation.getNode().getDeviceId());
		concentration.setLanId(confirmation.getNode().getLanId());
		concentration.setCommunicationNet(confirmation.getNode().getCommunicationNet());
		concentration.setAddress(confirmation.getNode().getAddress());
		concentration.setConfirmationTime(Calendar.getInstance().getTime());
		getConcentrationMapper().insert(concentration);
		
		getNodeComfirmationMapper().updateConfirmed(confirmation.getId(), confirmer, concentration.getConfirmationTime());
		
		return new Confirmed(confirmation.getRequestId(), new NodeCreated(deviceId, nodeDeviceId,
				confirmation.getNode().getLanId(), model));
	}
	
	private D_NodeConfirmation getNodeConfirmation(String concentrator, String node) {
		NodeConfirmation[] confirmations = getNodeComfirmationMapper().selectByConcentratorAndNode(concentrator, node);
		if (confirmations == null || confirmations.length == 0)
			return null;
		
		Date currentTime = Calendar.getInstance().getTime();
		for (NodeConfirmation confirmation : confirmations) {
			if (confirmation.getExpiredTime().after(currentTime) &&
					!confirmation.isCanceled() &&
					confirmation.getConfirmedTime() == null) {
				return (D_NodeConfirmation)confirmation;
			}
		}
		
		return null;
	}

	@Override
	public boolean containsNode(String nodeDeviceId) {
		return getConcentrationMapper().selectCountByConcentratorAndNode(deviceId, nodeDeviceId) != 0;
	}

	@Override
	public void requestToConfirm(NodeConfirmation confirmation) {
		if (!deviceId.equals(confirmation.getConcentrator())) {
			throw new ProtocolException(new NotAcceptable("Wrong device ID of concentrator. Your program maybe has a bug."));
		}
		
		if (containsNode(confirmation.getNode().getDeviceId())) {
			throw new ProtocolException(new Conflict(String.format("Duplicated node which's ID is '%s'.", confirmation.getNode().getDeviceId())));
		}
		
		if (containsLanId(confirmation.getNode().getLanId()))
			throw new ProtocolException(new Conflict(String.format("Duplicated land ID '%s'.", confirmation.getNode().getLanId())));
		
		getNodeComfirmationMapper().insert(confirmation);
	}
	
	@Override
	public void cancelConfirmation(String nodeDeviceId) {
		// TODO Auto-generated method stub
		
	}
	
	private NodeConfirmationMapper getNodeComfirmationMapper() {
		return sqlSession.getMapper(NodeConfirmationMapper.class);
	}
	
	private ConcentrationMapper getConcentrationMapper() {
		return sqlSession.getMapper(ConcentrationMapper.class);
	}

	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}

	@Override
	public boolean containsLanId(String lanId) {
		return getConcentrationMapper().selectCountByConcentratorAndLanId(deviceId, lanId) != 0;
	}
	
}
