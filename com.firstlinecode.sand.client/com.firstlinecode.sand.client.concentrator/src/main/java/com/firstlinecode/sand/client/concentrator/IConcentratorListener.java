package com.firstlinecode.sand.client.concentrator;

public interface IConcentratorListener {
	public enum Error {
		ALREADY_EXISTED,
		OVERFLOW_SIZE
	}
	
	void created(Node node);
	void removed(Node node);
	void occurred(Error error, Node source);
}
