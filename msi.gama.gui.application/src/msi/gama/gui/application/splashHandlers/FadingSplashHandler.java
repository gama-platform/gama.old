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