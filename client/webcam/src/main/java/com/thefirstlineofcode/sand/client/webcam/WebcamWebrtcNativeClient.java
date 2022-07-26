package com.thefirstlineofcode.sand.client.webcam;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class WebcamWebrtcNativeClient {
	private static final int DEFAULT_BLOCKING_TIMEOUT = 128;
	
	private WebcamWebrtcNativeClient.Listener listener;
	
	private Thread sendingThread;
	private Thread receivingThread;
	private Thread processingThread;
	
	private BlockingQueue<String> sendingQueue;
	private BlockingQueue<String> receivingQueue;
	
	private volatile boolean stopThreadsFlag;
	
	private Socket socket;
		
	public WebcamWebrtcNativeClient(Listener listener) {
		this.listener = listener;
		
		sendingQueue = new ArrayBlockingQueue<>(16);
		receivingQueue = new ArrayBlockingQueue<>(16);
	}
	
	public void connect() throws IOException {
		InetSocketAddress address = new InetSocketAddress("localhost", 9000);
		if (address.isUnresolved()) {
			throw new RuntimeException("Inet socket address is unresolved.");
		}
		
		if (socket == null) {
			socket = createSocket();
		}
		socket.connect(address, 4000);
		
		startThreads();
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
		
		processingThread = new ProcessingThread();
		processingThread.start();
	}
	
	public void close() {
		stopThreads();
		
		if (socket != null && socket.isConnected()) {
			send("exit");
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (socket != null && socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}
	
	private void stopThreads() {
		stopThreadsFlag = true;
		
		if (processingThread != null) {
			try {
				processingThread.join(DEFAULT_BLOCKING_TIMEOUT * 4, 0);
			} catch (InterruptedException e) {
				// ignore
			}
			processingThread = null;
		}
		
		if (sendingThread != null) {
			try {
				sendingThread.join(DEFAULT_BLOCKING_TIMEOUT * 4, 0);
			} catch (InterruptedException e) {
				// ignore
			}
			sendingThread = null;
		}
		
		if (receivingThread != null) {
			try {
				receivingThread.join(DEFAULT_BLOCKING_TIMEOUT * 4, 0);
			} catch (InterruptedException e) {
				// ignore
			}
			receivingThread = null;
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
				listener.processException(new CameraWebrtcPeerException("Receiving thread can't be created", e));
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
						receivingQueue.put((new String(buf, 0, num)));
					}
				} catch (SocketTimeoutException e) {
					if (stopThreadsFlag)
						break;
				} catch (IOException e) {
					listener.processException(new CameraWebrtcPeerException("Failed to read socket.", e));
					break;
				} catch (InterruptedException e) {
					listener.processException(new CameraWebrtcPeerException("Failed to put message to receiving queue.", e));
				}
			}
		}
	}
	
	private void received(String message) {
		// TODO
		System.out.println("Received message: " + message);
	}
	
	public void send(String message) {
		try {
			sendingQueue.put(message);
		} catch (InterruptedException e) {
			listener.processException(new CameraWebrtcPeerException("Failed to put message to sending queue.", e));
		}
	}
	
	private class ProcessingThread extends Thread {
		public ProcessingThread() {
			super("Camera RTC Source Peer Processing Thread");
		}
		
		public void run() {
			while (true) {
				try {
					String message = null;
					message = receivingQueue.poll(DEFAULT_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);
					
					if (stopThreadsFlag) {
						break;
					}
					
					if (message == null)
						continue;
					
					received(message);
				} catch (InterruptedException e) {
					break;
				}
				
			}
		}
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
				listener.processException(new CameraWebrtcPeerException("Sending thread can't be created", e));
				return;
			}
			
			while (true) {
				try {
					String message = sendingQueue.poll(DEFAULT_BLOCKING_TIMEOUT, TimeUnit.MILLISECONDS);
					
					if (stopThreadsFlag) {
						break;
					}
					
					if (message == null)
						continue;
					
					output.write(message.getBytes("UTF-8"));
					output.flush();
				} catch (InterruptedException e) {
					break;
				} catch (IOException e) {
					listener.processException(new CameraWebrtcPeerException("Failed to write socket.", e));
					break;
				}
			}
		}
	}
	
	public interface Listener {
		void processException(CameraWebrtcPeerException e);
	}
}
