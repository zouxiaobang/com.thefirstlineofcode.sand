#include <iostream>
#include <string>

#include "api/peer_connection_interface.h"

#include "SocketServer.h"
#include "WebcamWebrtcPeer.h"

void printHelp() {
	cout << "Usage: WebcamWebrtcNativeService [OPTIONS]\n";
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

	unique_ptr<WebcamWebrtcPeer> wwPeer = make_unique<WebcamWebrtcPeer>();
	if (wwPeer->getVideoCaptureDeviceName())
		cout << "Find a video capture device which's name is : " << wwPeer->getVideoCaptureDeviceName() << endl;

	SocketServer socketServer(std::move(wwPeer));
	socketServer.start();

	cout << "Webcam WebRTC native service has stopped.";

	return 0;
}
