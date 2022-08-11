#define WINAPI_FAMILY WINAPI_FAMILY_DESKTOP_APP

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

/*    rtc::scoped_refptr<WebcamWebrtcPeer> wwPeer =
        new rtc::RefCountedObject<WebcamWebrtcPeer>();
    if (wwPeer->getVideoCaptureDeviceName())
        cout << "Find a video capture device which's name is : " << wwPeer->getVideoCaptureDeviceName() << endl;

    SocketServer socketServer(wwPeer);
    socketServer.start();*/

    cout << "Webcam WebRTC native service has stopped.";

    return 0;
}
