package com.firstlinecode.sand.server.concentrator;

public interface IConcentrator {
	boolean containsNode(String node);
	void requestConfirmation(NodeConfirmation confirmation);
	void confirm(String node);
	Node[] getNodes();
}
