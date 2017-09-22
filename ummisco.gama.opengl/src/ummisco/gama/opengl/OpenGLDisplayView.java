/*********************************************************************************************
 *
 * 'OpenGLDisplayView.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import ummisco.gama.ui.views.displays.SWTDisplayView;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public class OpenGLDisplayView extends SWTDisplayView {

	boolean isOverlayTemporaryVisible;

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	public SWTOpenGLDisplaySurface getDisplaySurface() {
		return (SWTOpenGLDisplaySurface) super.getDisplaySurface();
	}

	@Override
	protected Composite createSurfaceComposite(final Composite parent) {
		final SWTOpenGLDisplaySurface surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	protected void updateOverlay() {
		final SWTOpenGLDisplaySurface surface = getDisplaySurface();
		if (surface == null)
			return;
		if (surface.getROIDimensions() != null) {
			if (!overlay.isVisible()) {
				isOverlayTemporaryVisible = true;
				overlay.setVisible(true);
			}
		} else {
			if (isOverlayTemporaryVisible) {
				isOverlayTemporaryVisible = false;
				overlay.setVisible(false);
			}
		}
		overlay.update();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		new OpenGLToolbarMenu().createItem(tb, this);
	}

	@Override
	protected List<String> getCameraNames() {
		return new ArrayList<String>(getDisplaySurface().renderer.camera.PRESETS.keySet());
	}
}
