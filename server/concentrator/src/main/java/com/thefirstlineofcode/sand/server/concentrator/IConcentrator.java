package com.thefirstlineofcode.sand.server.concentrator;

public interface IConcentrator {
	public static final String LAN_ID_CONCENTRATOR = "00";
	
	boolean containsNode(String nodeDeviceId);
	boolean containsLanId(String lanId);
	void requestToConfirm(NodeConfirmation confirmation);
	void cancelConfirmation(String nodeDeviceId);
	NodeConfirmed confirm(String nodeDeviceId, String confirmer);
	Node getNode(String lanId);
	Node[] getNodes();
}
