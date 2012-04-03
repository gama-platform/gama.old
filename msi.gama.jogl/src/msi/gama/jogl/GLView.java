package msi.gama.jogl;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.util.GuiUtils;


import msi.gama.gui.views.GamaViewPart;


public class GLView extends GamaViewPart {

	public static final String ID = GuiUtils.GL_VIEW_ID;

	Composite myComposite = null;

	public GLView() {

	}

	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO
		return new Integer[] {
		// PAUSE, REFRESH, SYNCHRONIZE, SEPARATOR,
		// TODO SNAPSHOT,
		// SEPARATOR, ZOOM_IN, ZOOM_OUT, ZOOM_FIT
		};
	}

	@Override
	public void ownCreatePartControl(Composite parent) {

		myComposite = new Composite(parent, SWT.EMBEDDED);
		myComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		myComposite.setLayout(new FillLayout());

		final Frame frame = SWT_AWT.new_Frame(myComposite);

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				try {

					final int WINDOW_WIDTH = 640;
					final int WINDOW_HEIGHT = 480;
					final String WINDOW_TITLE = "Gama OpenGL 3D GIS";

					// Create A 3D World environment.
					NeheJOGL02Basics world = null;
					world = new NeheJOGL02Basics();
					frame.add(world);

					// frame.setContentPane(world);
					final NeheJOGL02Basics world2 = world;
					
					frame.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosing(WindowEvent e) {
							// Use a dedicate thread to run the stop() to ensure that the
							// animator stops before program exits.
							new Thread() {

								@Override
								public void run() {
									world2.animator.stop(); // stop the animator loop
								}
							}.start();
						}
					});

					frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
					frame.setTitle(WINDOW_TITLE);
					frame.setVisible(true);
					world.animator.start(); // start the animation loop
				} catch (RuntimeException e) {
					e.printStackTrace();

				}

			}

		});

		// fill in the blanks !

	}

}
