#include <iostream>
#include <thread>

#include "SocketServer.h"

SocketServer::SocketServer(unique_ptr<WebcamWebrtcPeer> wwPeer) {
	started = false;
	this->wwPeer = move(wwPeer);
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
	ssize_t n;
	char buf[1024];

	bool stop = false;
	while ((n = socket.read(buf, sizeof(buf))) > 0) {
		string message(buf, n);
		processClientMessage(socket, message, &stop);
		if (stop)
			break;
	}

	cout << "Connection closed from " << socket.peer_address() << endl;
}

void SocketServer::processClientMessage(sockpp::tcp_socket &socket, string &message, bool *stop) {
	cout << "Received client message: " << message << endl;

	if (message.compare("exit") == 0) {
		cout << "Exit command received. The program will exit." << endl;
		socket.close();
		*stop = true;
	}

	if (socket.write_n(message.c_str(), message.size()) == -1) {
		cout << "Failed to write message to client. Message is: " << message << endl;
	}
}

void SocketServer::stop() {
}