#ifndef SOCKET_SERVER_H
#define SOCKET_SERVER_H

#include "sockpp/tcp_acceptor.h"

#include "CameraRtcSourcePeer.h"

using namespace std;


class SocketServer {
public:
	SocketServer() {}
	SocketServer(unique_ptr<CameraRtcSourcePeer> crsPeer);
	void start();
	void stop();

private:
	void processClientSocket(sockpp::tcp_socket socket);
	void processClientMessage(sockpp::tcp_socket &socket, string &message, bool *stop);

private:
	unique_ptr<CameraRtcSourcePeer> crsPeer;
	bool started;
};

#endif