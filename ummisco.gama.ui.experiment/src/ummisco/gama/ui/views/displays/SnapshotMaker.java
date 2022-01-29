/*******************************************************************************************************
 *
 * SnapshotMaker.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Rectangle;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.ImageUtils;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;

/**
 * The Class SnapshotMaker.
 */
public class SnapshotMaker {

	/**
	 * Do snapshot.
	 *
	 * @param surface the surface
	 * @param composite the composite
	 */
	public void doSnapshot(final IDisplaySurface surface, final Rectangle composite) {
		if (surface == null || composite == null) return;
		final IScope scope = surface.getScope();
		final String snapshotFile = FileUtils.constructAbsoluteFilePath(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME
				+ "/" + GAMA.getModel().getName() + "_display_" + surface.getOutput().getName(), false);
		final LayeredDisplayData data = surface.getData();
		final int w = (int) data.getImageDimension().getX();
		final int h = (int) data.getImageDimension().getY();

		final int width = w == -1 ? surface.getWidth() : w;
		final int height = h == -1 ? surface.getHeight() : h;
		final String autosavePath = data.getAutosavePath();
		final String file = autosavePath != null && !autosavePath.isEmpty() ? autosavePath
				: snapshotFile + "_size_" + width + "x" + height + "_cycle_" + scope.getClock().getCycle() + "_time_"
						+ java.lang.System.currentTimeMillis() + ".png";
		// System.out.println("xxx " + file);
		BufferedImage image = null;
		if (GamaPreferences.Displays.DISPLAY_FAST_SNAPSHOT.getValue()) {
			try {
				final Robot robot = new Robot();
				final java.awt.Rectangle bounds =
						new java.awt.Rectangle(composite.x, composite.y, composite.width, composite.height);
				// System.out.println("Bounds of composite " + bounds + " | surface display width "
				// + surface.getDisplayWidth() + "surface display height " + surface.getDisplayHeight() + " "
				// + " | surface height " + height + " surface width " + width);
				image = robot.createScreenCapture(bounds);
				image = ImageUtils.resize(image, width, height);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		// in case it has not worked, snapshot is still null
		if (image == null) { image = surface.getImage(width, height); }
		if (scope.interrupted() || image == null) return;
		// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to
		// prevent the exceptions from being masked.
		try {
			Files.newFolder(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}

		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			ImageIO.write(image, "png", os);
			image.flush();
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		} finally {
			try {
				if (os != null) { os.close(); }
			} catch (final Exception ex) {
				final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(GAMA.getRuntimeScope(), e, false);
			}
		}
	}

	/** The instance. */
	private static SnapshotMaker instance = new SnapshotMaker();

	/**
	 * Gets the single instance of SnapshotMaker.
	 *
	 * @return single instance of SnapshotMaker
	 */
	public static SnapshotMaker getInstance() {
		return instance;
	}

}
