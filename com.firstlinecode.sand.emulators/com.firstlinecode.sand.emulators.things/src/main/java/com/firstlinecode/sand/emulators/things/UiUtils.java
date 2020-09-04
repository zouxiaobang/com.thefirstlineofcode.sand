package com.firstlinecode.sand.emulators.things;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class UiUtils {
	private static final int DEFAULT_NOTIFICATION_DELAY_TIME = 1000 * 2;
	
	public static void showNotification(Window window, String title, String message) {
		final JDialog dialog = new JDialog(window, title, ModalityType.MODELESS);
		dialog.setBounds(getParentCenterBounds(window, 400, 160));
		dialog.add(new JLabel(message));
		dialog.setVisible(true);
		
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				dialog.setVisible(false);
				dialog.dispose();
				
				timer.cancel();
			}
		}, DEFAULT_NOTIFICATION_DELAY_TIME);
	}
	
	private static Rectangle getParentCenterBounds(Window window, int width, int height) {
		int parentX = window.getX();
		int parentY = window.getY();
		int parentWidth = window.getWidth();
		int parentHeight = window.getHeight();
		
		if (width > parentWidth || height > parentHeight)
			return new Rectangle(parentX, parentY, width, height);
		
		return new Rectangle((parentX + (parentWidth - width) / 2), (parentY + (parentHeight - height) / 2), width, height);
	}
	
	public static void showDialog(Window parent, JDialog dialog) {
		Dimension size = dialog.getPreferredSize();
		Rectangle bounds = parent.getBounds();
		int x = (int)(bounds.x + (bounds.getWidth() - size.width) / 2);
		int y = (int)(bounds.y + (bounds.getHeight() - size.height) / 2);
		
		dialog.setBounds(x, y, size.width, size.height);
		dialog.setVisible(true);
	}
}
