/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.demos.applet;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.BulletStats;
import com.bulletphysics.demos.opengl.DemoApplication;
import com.bulletphysics.demos.opengl.GLDebugDrawer;
import com.bulletphysics.demos.opengl.IGL;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author jezek2
 */
public class DemoPanel extends JPanel {

	private DemoApplication demoApp;
	private boolean inited = false;
	private BufferedImage img;
	private SoftwareGL sgl;
	private Timer timer;
	private Font font = new Font("DialogInput", Font.PLAIN, 10);
	private AlphaComposite overlayComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	private Color overlayColor = new Color(0.6f, 0.6f, 0.6f, 1f);

	public DemoPanel() {
		sgl = new SoftwareGL();
		
		img = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
		sgl.init(img);
		
		setFocusable(true);
		requestFocusInWindow();
		
		MouseHandler mh = new MouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);
		
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
					if (demoApp != null) demoApp.keyboardCallback(e.getKeyChar(), 0, 0, e.getModifiersEx());
				}
				
				repaint();
			}

			public void keyPressed(KeyEvent e) {
				if (demoApp != null) demoApp.specialKeyboard(convertKey(e.getKeyCode()), 0, 0, e.getModifiersEx());
				repaint();
			}

			public void keyReleased(KeyEvent e) {
				if (demoApp != null) demoApp.specialKeyboardUp(convertKey(e.getKeyCode()), 0, 0, e.getModifiersEx());
				repaint();
			}
			
			private int convertKey(int code) {
				int key = 0;
				switch (code) {
					case KeyEvent.VK_LEFT: key = Keyboard.KEY_LEFT; break;
					case KeyEvent.VK_RIGHT: key = Keyboard.KEY_RIGHT; break;
					case KeyEvent.VK_UP: key = Keyboard.KEY_UP; break;
					case KeyEvent.VK_DOWN: key = Keyboard.KEY_DOWN; break;
					case KeyEvent.VK_F5: key = Keyboard.KEY_F5; break;
				}
				return key;
			}
		});
		
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				if (img != null) {
					img.flush();
				}
				
				img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				sgl.init(img);
				if (demoApp != null) demoApp.reshape(getWidth(), getHeight());
				repaint();
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});
		
		timer = new Timer(20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
	}

	public IGL getGL() {
		return sgl;
	}
	
	public void runDemo(DemoApplication app) {
		if (demoApp != null) {
			demoApp.destroy();
		}
		
		if (app == null) {
			timer.stop();
		}
		
		demoApp = app;
		demoApp.getDynamicsWorld().setDebugDrawer(new GLDebugDrawer(sgl));
		inited = false;
		timer.start();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		long t0 = System.nanoTime();
		if (demoApp != null) {
			if (!inited) {
				demoApp.myinit();
				demoApp.reshape(img.getWidth(), img.getHeight());
			}
			inited = true;
			
			BulletStats.updateTime = 0;
			demoApp.clientMoveAndDisplay();
		}
		
		g.drawImage(img, 0, 0, null);
		
		if (demoApp != null) {
			long time = (System.nanoTime() - t0) / 1000000;

			long physicsTime = BulletStats.stepSimulationTime;
			long updateTime = BulletStats.updateTime;
			long renderTime = time - physicsTime - updateTime;

			Composite comp = g2.getComposite();
			g2.setComposite(overlayComposite);
			g.setColor(overlayColor);
			g.fillRect(getWidth()-135, getHeight()-53, 130, 50);
			g2.setComposite(comp);
			
			g.setFont(font);
			g.setColor(Color.BLACK);

			g.drawString(" Render time: "+renderTime+" ms", getWidth()-130, getHeight()-40);
			g.drawString("Physics time: "+physicsTime+" ms", getWidth()-130, getHeight()-25);
			g.drawString(" Update time: "+updateTime+" ms", getWidth()-130, getHeight()-10);
		}
	}
	
	private class MouseHandler implements MouseListener, MouseMotionListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (demoApp != null) demoApp.mouseFunc(e.getButton()-1, 0, e.getX(), /*img.getHeight() - 1 -*/ e.getY());
			repaint();
			
			if (!hasFocus()) {
				requestFocusInWindow();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (demoApp != null) demoApp.mouseFunc(e.getButton()-1, 1, e.getX(), /*img.getHeight() - 1 -*/ e.getY());
			repaint();
		}

		public void mouseDragged(MouseEvent e) {
			if (demoApp != null) demoApp.mouseMotionFunc(e.getX(), /*img.getHeight() - 1 -*/ e.getY());
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
		}
	}

}
