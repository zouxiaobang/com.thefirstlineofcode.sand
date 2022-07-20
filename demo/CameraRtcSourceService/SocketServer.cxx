#include <iostream>
#include <thread>

#include "SocketServer.h"

SocketServer::SocketServer(unique_ptr<CameraRtcSourcePeer> crsPeer) {
	started = false;
	this->crsPeer = move(crsPeer);
}

void SocketServer::start() {
	sockpp::socket_initializer sockInitializer;
	sockpp::tcp_acceptor sockAccptor(9000);

	if (!sockAccptor) {
		cerr << "Error creating the acceptor: " << sockAccptor.last_error_str() << endl;
		return;
	}

	bool clientConnected = false;
	while (true) {
		sockpp::inet_address clientAddress;

		sockpp::tcp_socket clientSocket = sockAccptor.accept(&clientAddress);

		if (clientConnected) {
			const char* errorInfo = "Error Client has already connected.";
			clientSocket.write_n(errorInfo, sizeof(errorInfo));
			clientSocket.close();

			continue;
		}

		if (!clientSocket) {
			cerr << "Error accepting incoming connection: " << sockAccptor.last_error_str() << endl;
			return;
		} else {
			clientConnected = true;

			thread processClientSocketThread(&SocketServer::processClientSocket, this, move(clientSocket));
			processClientSocketThread.detach();
		}
	}
}

void SocketServer::processClientSocket(sockpp::tcp_socket socket) {

}

void SocketServer::stop() {
}