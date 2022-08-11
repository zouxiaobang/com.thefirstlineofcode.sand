#include <iostream>
#include <thread>

#include "SocketServer.h"

SocketServer::SocketServer(rtc::scoped_refptr<WebcamWebrtcPeer> wwPeer) {
	started = false;
	alreadyConnected = false;
	this->wwPeer = wwPeer;
}

void SocketServer::start() {
	if (started)
		return;
		
	net = new cppnet::CppNet();
	net->Init(2);
	net->ListenAndAccept("127.0.0.1", 9000);

	net->SetAcceptCallback(std::bind(&SocketServer::connected, this, std::placeholders::_1, std::placeholders::_2));
	net->SetReadCallback(std::bind(&SocketServer::messageRead, this, std::placeholders::_1, std::placeholders::_2, std::placeholders::_3));
	net->SetDisconnectionCallback(std::bind(&SocketServer::disconnected, this, std::placeholders::_1, std::placeholders::_2));

	started = true;
	alreadyConnected = false;

	net->Join();
}

void SocketServer::messageRead(cppnet::Handle handle, std::shared_ptr<cppnet::Buffer> data, uint32_t len) {
	const int bufSize = 1024 * 8;

	if (len >= bufSize) {
		cout << "Received a huge message which's size is " << len << ". Ignore to process the message." << endl;
		return;
	}

	char buf[bufSize] = {0};
	int readSize = data->Read(buf, bufSize);

	data->Clear();

	if (readSize > 0) {
		std::string message(buf, readSize);
		cout << "Received client message: " << message << endl;
		processMessage(handle, message);
	} else {
		cout << "Can't read message from data." << endl;
	}
}

void SocketServer::processMessage(cppnet::Handle handle, const std::string &message) {
	if (message.compare("STOP") == 0) {
		cout << "Stop command received. The service will be stopped." << endl;
		stop();

		return;
	} else if (message.compare("OPEN") == 0) {
		static const char openedMsg[] = "OPENED";
		wwPeer->open();
		handle->Write(openedMsg, sizeof(openedMsg));
	} else if (message.compare("CLOSE") == 0) {
		static const char closedMsg[] = "CLOSED";
		wwPeer->close();
		handle->Write(closedMsg, sizeof(closedMsg));
	} else if (message.compare(0, 6, "OFFER ") == 0 && message.size() > 6) {
		std::string offerSdp = message.substr(6, message.size() - 6);
		cout << "Offer command received. SDP is: " + offerSdp << endl;

		wwPeer->offered(handle, offerSdp);
	} else {
		cout << "Unkown command received. Message is: " + message << endl;
	}
}

void SocketServer::connected(cppnet::Handle handle, uint32_t err) {
	static const char conflictMsg[] = "CONFLICT";

	std::string ip;
	uint16_t port;
	if (handle->GetAddress(ip, port)) {
		cout << "A client has connected. Client address: " << ip << ":" << port << "." << endl;
	} else {
		cout << "A client has connected. Failed to get Client addressl" << endl;
	}

	if (alreadyConnected) {
		cout << "Conflict. A client has already connected. The secondary connected client will be closed." << endl;

		handle->Write(conflictMsg, sizeof(conflictMsg));
		handle->Close();

		return;
	}

	alreadyConnected = true;

	if (err) {
		cout << "Connected error: " + err;
	}
}

void SocketServer::disconnected(cppnet::Handle handle, uint32_t err) {
	cout << "A client has disconnected." << endl;

	if (err) {
		cout << "Disconnected error: " + err;
	}
}

void SocketServer::stop() {
	wwPeer->close();

	net->Destory();
	net = nullptr;
}

SocketServer::~SocketServer() {
	if (net)
		stop();
}