/*********************************************************************************************
 *
 * 'WorkaroundForIssue2476.java, in plugin ummisco.gama.java2d, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.java2d;

import java.awt.event.MouseMotionListener;

import javax.swing.JApplet;

import org.eclipse.swt.SWT;

import msi.gama.common.interfaces.IDisplaySurface;

public class WorkaroundForIssue2476 {

	public static void installOn(final JApplet applet, final IDisplaySurface surface) {
		// Install only on Linux
		if (!ummisco.gama.ui.utils.PlatformHelper.isLinux()) { return; }
		applet.addMouseWheelListener(e -> {
			if (e.getPreciseWheelRotation() > 0) {
				surface.zoomOut();
			} else {
				surface.zoomIn();
			}
		});
		applet.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(final java.awt.event.MouseEvent e) {
				surface.setMousePosition(e.getX(), e.getY());
			}

			@Override
			public void mouseDragged(final java.awt.event.MouseEvent e) {
				surface.draggedTo(e.getX(), e.getY());
			}
		});
		applet.addMouseListener(new java.awt.event.MouseListener() {

			volatile boolean inMenu;

			@Override
			public void mouseReleased(final java.awt.event.MouseEvent e) {
				surface.setMousePosition(e.getX(), e.getY());
				surface.dispatchMouseEvent(SWT.MouseUp);
			}

			@Override
			public void mousePressed(final java.awt.event.MouseEvent e) {
				surface.setMousePosition(e.getX(), e.getY());
				if (inMenu) {
					inMenu = false;
					return;
				}
				surface.dispatchMouseEvent(SWT.MouseDown);
				
			}

			@Override
			public void mouseExited(final java.awt.event.MouseEvent e) {}

			@Override
			public void mouseEntered(final java.awt.event.MouseEvent e) {}

			@Override
			public void mouseClicked(final java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					surface.zoomFit();
				}
				if (e.getButton() == 3 && !inMenu) {
					inMenu = true;
					surface.setMousePosition(e.getX(), e.getY());
					surface.selectAgentsAroundMouse();
				}
			}
		});

	}

}
