#include <iostream>
#include <string>

#include "api/peer_connection_interface.h"

#include "SocketServer.h"
#include "CameraRtcSourcePeer.h"

void printHelp() {
	cout << "Usage: CameraRtcSourceService [OPTIONS]\n";
	cout << "OPTIONS:" << endl;
	cout << "-help\tDisplay help information." << endl;
};

int main(int argc, char* argv[]) {
	if (argc == 2 && strlen(argv[1]) == 5 && strncmp("-help", argv[1], 5) == 0) {
		printHelp();
		return 0;
	}

	if (argc != 1) {
		printHelp();
		return 1;
	}

	unique_ptr<CameraRtcSourcePeer> crsPeer = make_unique<CameraRtcSourcePeer>();
	if (crsPeer->getVideoCaptureDeviceName())
		cout << "Find a video capture device which's name is : " << crsPeer->getVideoCaptureDeviceName() << endl;

	SocketServer socketServer(std::move(crsPeer));
	socketServer.start();

	cout << "Camera RTC source service has stopped.";

	return 0;
}
