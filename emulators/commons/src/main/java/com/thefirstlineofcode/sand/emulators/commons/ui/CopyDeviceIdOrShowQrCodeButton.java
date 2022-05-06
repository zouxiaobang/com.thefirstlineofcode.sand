package com.thefirstlineofcode.sand.emulators.commons.ui;

import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.alexandriasoftware.swing.JSplitButton;
import com.alexandriasoftware.swing.action.ButtonClickedActionListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thefirstlineofcode.sand.client.things.IDevice;

public class CopyDeviceIdOrShowQrCodeButton extends JSplitButton {
	private static final long serialVersionUID = -2163217242326846012L;
	
	private static final String TEXT_COPY_DEVICE_ID = "Copy Device ID";
	private static final String TEXT_SHOW_QR_CODE = "Show QR Code";
	
	private static final String DEFAULT_QR_CODE_IMAGE_FORMAT = "PNG";
	
	private IDevice device;

	public CopyDeviceIdOrShowQrCodeButton(IDevice device) {
		super(TEXT_COPY_DEVICE_ID);
		
		this.device = device;
		
		JPopupMenu popup = new JPopupMenu();
		JMenuItem copyDeviceId = new JMenuItem(TEXT_COPY_DEVICE_ID);
		
		copyDeviceId.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyDeviceId();
			}
		});
		popup.add(copyDeviceId);
		
		JMenuItem showQrCode = new JMenuItem(TEXT_SHOW_QR_CODE);
		showQrCode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showQrCode();
			}
		});
		popup.add(showQrCode);
		
		addButtonClickedActionListener(new ButtonClickedActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyDeviceId();
			}
		});
		
		setPopupMenu(popup);
	}
	
	protected void showQrCode() {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			BitMatrix matrix = qrCodeWriter.encode(device.getDeviceId(), BarcodeFormat.QR_CODE, 200, 200);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(matrix, DEFAULT_QR_CODE_IMAGE_FORMAT, output);
			Image image = Toolkit.getDefaultToolkit().createImage(output.toByteArray());
			JLabel imageLabel = new JLabel(new ImageIcon(image));
			
			JDialog dialog = new JDialog(getWindow(), "QR Code");
			dialog.getRootPane().setContentPane(imageLabel);
			dialog.pack();
			
			UiUtils.showDialog(getWindow(), dialog);
		} catch (Exception e) {
			throw new RuntimeException("Can't generate QR code image.", e);
		}
	}
	
	protected void copyDeviceId() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(device.getDeviceId()), null);
		
		UiUtils.showNotification(getWindow(), "Message", "Device ID has copied to clipboard.");
	}
	
	private Window getWindow() {
		Container current = this;
		Container parent = null;
		while(true) {
			parent = current.getParent();
			if (parent != null) {
				current = parent;
				continue;
			}
			
			if (current instanceof Window)
				return (Window)current;
		}
	}
}
