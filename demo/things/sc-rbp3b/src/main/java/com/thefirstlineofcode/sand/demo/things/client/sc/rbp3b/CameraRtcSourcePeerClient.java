package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CameraRtcSourcePeerClient {
	private CameraRtcSourcePeerClient.Listener listener;
	
	private Thread receivingThread;
	private Thread sendingThread;
	
	private BlockingQueue<byte[]> sendingQueue;
	private BlockingQueue<byte[]> receivingQueue;
	
	private volatile boolean stopThreadsFlag;
	
	private Socket socket;
	
	private ICameraRtcSourcePeer cameraRtcSourcePeer;
	
	public CameraRtcSourcePeerClient(CameraRtcSourcePeerClient.Listener listener) {
		this.listener = listener;
	}
	
	public ICameraRtcSourcePeer connect(ICameraRtcSourcePeer.Listener listener) throws IOException {
		InetSocketAddress address = new InetSocketAddress("localhost", 9000);
		if (address.isUnresolved()) {
			throw new RuntimeException("Inet socket address is unresolved.");
		}
		
		if (socket == null) {
			socket = createSocket();
		}
		
		socket.connect(address, 4000);
		startThreads();
		
		return new CameraRtcSourcePeer(this, listener);
	}
	
	protected Socket createSocket() throws IOException {
		Socket socket = new Socket();
		socket.setSoTimeout(1000);
		socket.setTcpNoDelay(true);
		
		return socket;
	}
	
	private void startThreads() {
		stopThreadsFlag = false;
		
		sendingThread = new SendingThread();
		sendingThread.start();
		
		receivingThread = new ReceivingThread();
		receivingThread.start();
	}
	
	public void close() {
		if (socket == null)
			return;
		
		write("exit");
		
		if (socket.isConnected())
			try {
				socket.close();
			} catch (IOException e) {}
	}
	
	public void write(String message) {
		try {
			sendingQueue.put(message.getBytes("UTF8"));
		} catch (Exception e) {
			listener.processException(new CameraRtcSourcePeerException("Failed to send message.", e));
		}
	}
	
	boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	private class ReceivingThread extends Thread {
		private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
		private InputStream input;
		
		public ReceivingThread() {
			super("Camera RTC Source Peer Receiving Thread");
		}
		
		@Override
		public void run() {
			try {
				input = new BufferedInputStream(socket.getInputStream());
			} catch (IOException e) {
				listener.processException(new CameraRtcSourcePeerException("Receiving thread can't be created", e));
				return;
			}
			
			byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			while (true) {
				try {
					if (stopThreadsFlag) {
						break;
					}
					
					int num = input.read(buf);	
					if (num == -1) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							throw new RuntimeException("Unexpected exception.", e);
						}
					} else {
						processSourcePeerMessage(new String(buf, 0, num));
					}
				} catch (IOException e) {
					listener.processException(new CameraRtcSourcePeerException("Failed to read socket.", e));
					break;
				}
			}
		}
	}
	
	private void processSourcePeerMessage(String message) {
		// TODO
	}
	
	private class SendingThread extends Thread {
		private OutputStream output;
		
		public SendingThread() {
			super("Camera RTC Source Peer Sending Thread");
		}
		
		@Override
		public void run() {
			try {
				output = socket.getOutputStream();
			} catch (IOException e) {
				listener.processException(new CameraRtcSourcePeerException("Sending thread can't be created", e));
				return;
			}
			
			byte[] bytes = null;
			while (true) {
				try {
					bytes = sendingQueue.poll(128, TimeUnit.MILLISECONDS);
					
					if (stopThreadsFlag) {
						break;
					}
					
					if (bytes == null)
						continue;
					
					output.write(bytes);
					output.flush();
				} catch (InterruptedException e) {
					break;
				} catch (IOException e) {
					listener.processException(new CameraRtcSourcePeerException("Failed to write socket.", e));
					break;
				}
			}
		}
	}
	
	public interface Listener {
		void processException(CameraRtcSourcePeerException e);
	}
}
