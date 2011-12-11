/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.splashHandlers;

import msi.gama.gui.application.GUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.DisplayAccess;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * @author Vincent Zurczak - EBM WebSourcing
 */
public class FadingSplashHandler extends BasicSplashHandler {

	private int alphaValue = 255;
	private Image img;

	@Override
	public void init(final Shell splash) {
		super.init(splash);
		System.out.println("Passage par le splash handler");
		ImageDescriptor desc = ImageDescriptor.createFromFile(GUI.class, "splash.png");
		final ImageData imgData = desc.getImageData();

		getContent().addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				imgData.alpha = FadingSplashHandler.this.alphaValue;
				Image previousImg = FadingSplashHandler.this.img;
				FadingSplashHandler.this.img = new Image(e.display, imgData);

				if ( previousImg != null ) {
					previousImg.dispose();
					previousImg = null;
				}

				e.gc.drawImage(FadingSplashHandler.this.img, 0, 0);
			}
		});

		Thread worker = new Thread() {

			@Override
			public void run() {
				DisplayAccess.accessDisplayDuringStartup();
				getContent().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						while (FadingSplashHandler.this.alphaValue > 20) {
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								// nothing
							}
							FadingSplashHandler.this.alphaValue -= 20;
							getContent().redraw();
							getContent().update();
						}
					}
				});
			}
		};
		worker.start();
	}
}