#ifndef UNICODE
#define UNICODE
#endif 

#include <iostream>
#include <string>
#include <thread>

#include "api/peer_connection_interface.h"

#include "SocketServer.h"
#include "WebcamWebrtcPeer.h"

#include <windows.h>

SocketServer *socketServer;

void startNativeService() {
    rtc::scoped_refptr<WebcamWebrtcPeer> wwPeer =
        new rtc::RefCountedObject<WebcamWebrtcPeer>();
    if (wwPeer->getVideoCaptureDeviceName())
        cout << "Find a video capture device which's name is : " << wwPeer->getVideoCaptureDeviceName() << endl;

    socketServer = new SocketServer(wwPeer);
    socketServer->start();

    cout << "Webcam WebRTC native service has stopped.";
}

void stopNativeService() {
    if (socketServer) {
        socketServer->stop();
        socketServer = nullptr;
    }
}

LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE, PWSTR pCmdLine, int nCmdShow)
{
    std::thread nativeServiceThread(startNativeService);
    nativeServiceThread.detach();

    // Register the window class.
    const wchar_t CLASS_NAME[]  = L"Sample Window Class";

    WNDCLASS wc ={ };

    wc.lpfnWndProc   = WindowProc;
    wc.hInstance     = hInstance;
    wc.lpszClassName = CLASS_NAME;

    RegisterClass(&wc);

    // Create the window.

    HWND hwnd = CreateWindowEx(
        0,                              // Optional window styles.
        CLASS_NAME,                     // Window class
        L"Learn to Program Windows",    // Window text
        WS_OVERLAPPEDWINDOW,            // Window style

        // Size and position
        CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,

        NULL,       // Parent window    
        NULL,       // Menu
        hInstance,  // Instance handle
        NULL        // Additional application data
    );

    if (hwnd == NULL)
    {
        return 0;
    }

    ShowWindow(hwnd, nCmdShow);

    // Run the message loop.
    MSG msg ={ };
    while (GetMessage(&msg, NULL, 0, 0))
    {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }

    return 0;
}

LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam) {
	switch (uMsg) {
		case WM_DESTROY:
			stopNativeService();
			PostQuitMessage(0);
			return 0;

		return 0;
    }

	return DefWindowProc(hwnd, uMsg, wParam, lParam);
}
