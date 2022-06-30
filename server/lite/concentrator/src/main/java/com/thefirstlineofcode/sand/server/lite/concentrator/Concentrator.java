package com.thefirstlineofcode.sand.server.lite.concentrator;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thefirstlineofcode.basalt.xmpp.core.ProtocolException;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.Conflict;
import com.thefirstlineofcode.basalt.xmpp.core.stanza.error.NotAcceptable;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactory;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.thefirstlineofcode.sand.server.concentrator.Concentration;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.concentrator.NodeConfirmation;
import com.thefirstlineofcode.sand.server.concentrator.NodeConfirmed;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Component
@Transactional
@Scope("prototype")
public class Concentrator implements IConcentrator, IDataObjectFactoryAware {
	private String deviceName;
	private SqlSession sqlSession;
	
	@Autowired
	private IDeviceManager deviceManager;
	
	private IDataObjectFactory dataObjectFactory;
	
	public Concentrator(String deviceName, SqlSession sqlSession) {
		this.deviceName = deviceName;
		this.sqlSession = sqlSession;
	}
	
	@Override
	public Node getNodeByLanId(String lanId) {
		return getConcentrationMapper().selectNodeByConcentratorAndLanId(deviceName, lanId);
	}
	
	@Override
	public Node getNodeByDeviceId(String nodeDeviceId) {
		return getConcentrationMapper().selectNodeByConcentratorAndNode(deviceName, nodeDeviceId);
	}

	@Override
	public Node[] getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeConfirmed confirm(String nodeDeviceId, String confirmer) {
		if (!deviceManager.isValid(nodeDeviceId))
			throw new RuntimeException(String.format("Invalid node device ID '%s'.", nodeDeviceId));
		
		if (containsNode(nodeDeviceId))
			throw new ProtocolException(new Conflict(String.format("Reduplicate node which's ID is '%s'.", nodeDeviceId)));
		
		D_NodeConfirmation confirmation = getNodeConfirmation(deviceName, nodeDeviceId);
		if (confirmation == null) {
			throw new ProtocolException(new NotAcceptable("No node confirmation found."));
		}
		
		String model = deviceManager.getModel(nodeDeviceId);
		if (model == null) {
			throw new ProtocolException(new NotAcceptable(String.format("Unsupported model '%s'.", model)));
		}
		
		if (containsLanId(nodeDeviceId))
			throw new ProtocolException(new Conflict(String.format("Reduplicate LAN ID('%'). The node's ID is '%s'.",
					confirmation.getNode().getLanId(), nodeDeviceId)));
		
		Device node = dataObjectFactory.create(Device.class);
		node.setDeviceId(nodeDeviceId);
		node.setModel(model);
		node.setRegistrationTime(Calendar.getInstance().getTime());
		deviceManager.create(node);
		
		D_Concentration concentration = dataObjectFactory.create(Concentration.class);
		concentration.setId(UUID.randomUUID().toString());
		concentration.setConcentratorDeviceName(confirmation.getConcentratorDeviceName());
		concentration.setNodeDeviceId(confirmation.getNode().getDeviceId());
		concentration.setLanId(confirmation.getNode().getLanId());
		concentration.setCommunicationNet(confirmation.getNode().getCommunicationNet());
		concentration.setAddress(confirmation.getNode().getAddress());
		Date creationTime = Calendar.getInstance().getTime();
		concentration.setCreationTime(creationTime);
		getConcentrationMapper().insert(concentration);
		
		Date confirmedTime = Calendar.getInstance().getTime();
		getNodeConfirmationMapper().updateConfirmed(confirmation.getId(), confirmer, confirmedTime);
		
		return createNodeConfirmed(confirmation, node, concentration);
	}

	private NodeConfirmed createNodeConfirmed(NodeConfirmation confirmation, Device node,
			D_Concentration concentration) {
		return new NodeConfirmed(confirmation.getRequestId(), confirmation.getConcentratorDeviceName(),
				node.getDeviceId(), confirmation.getNode().getLanId(), node.getModel(),
				confirmation.getConfirmer(), concentration.getCreationTime(), confirmation.getConfirmedTime());
	}
	
	private D_NodeConfirmation getNodeConfirmation(String concentrator, String node) {
		NodeConfirmation[] confirmations = getNodeConfirmationMapper().selectByConcentratorAndNode(concentrator, node);
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
		return getConcentrationMapper().selectCountByConcentratorAndNode(deviceName, nodeDeviceId) != 0;
	}

	@Override
	public void requestToConfirm(NodeConfirmation confirmation) {
		if (!deviceName.equals(confirmation.getConcentratorDeviceName())) {
			throw new ProtocolException(new NotAcceptable("Wrong device ID of concentrator. Your program maybe has a bug."));
		}
		
		if (containsNode(confirmation.getNode().getDeviceId())) {
			throw new ProtocolException(new Conflict(String.format("Reduplicate node which's ID is '%s'.", confirmation.getNode().getDeviceId())));
		}
		
		if (containsLanId(confirmation.getNode().getLanId()))
			throw new ProtocolException(new Conflict(String.format("Reduplicate land ID '%s'.", confirmation.getNode().getLanId())));
		
		getNodeConfirmationMapper().insert(confirmation);
	}
	
	@Override
	public void cancelConfirmation(String nodeDeviceId) {
		// TODO Auto-generated method stub
		
	}
	
	private NodeConfirmationMapper getNodeConfirmationMapper() {
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
		return getConcentrationMapper().selectCountByConcentratorAndLanId(deviceName, lanId) != 0;
	}
	
}
