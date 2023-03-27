/*******************************************************************************************************
 *
 * SnapshotMaker.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.io.File;

import javax.imageio.ImageIO;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.ISnapshotMaker;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SnapshotMaker.
 */
public class SnapshotMaker implements ISnapshotMaker {

	static {
		DEBUG.ON();
	}

	/** The robot. */
	final Robot robot;

	/**
	 * Instantiates a new snapshot maker.
	 *
	 * @throws AWTException
	 */
	SnapshotMaker() throws AWTException {
		robot = new Robot();
	}

	/**
	 * Do snapshot.
	 *
	 * @param surface
	 *            the surface
	 * @param composite
	 *            the composite
	 */
	@Override
	public void takeAndSaveSnapshot(final IDisplaySurface surface) {
		if (surface == null) return;
		final IScope scope = surface.getScope();
		final String autosavePath = surface.getData().getAutosavePath();
		String fileName;
		if (autosavePath == null || autosavePath.isBlank()) {
			final String snapshotFile = FileUtils.constructAbsoluteFilePath(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME
					+ "/" + GAMA.getModel().getName() + "_display_" + surface.getOutput().getName(), false);
			fileName = snapshotFile + "_cycle_" + scope.getClock().getCycle() + "_time_"
					+ java.lang.System.currentTimeMillis() + ".png";
		} else {
			fileName = FileUtils.constructAbsoluteFilePath(scope,
					IDisplaySurface.SNAPSHOT_FOLDER_NAME + "/" + autosavePath, false);
			if (!fileName.endsWith(".png")) { fileName += ".png"; }
		}
		GamaImage image = captureImage(surface);
		if (scope.interrupted() || image == null) return;

		try {
			Files.newFolder(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME);
		} catch (final GamaRuntimeException e1) {
			// Intentionnaly passing GAMA.getRuntimeScope() to errors in order to
			// prevent the exceptions from being masked.
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}

		try {
			if (!ImageIO.write(image, "png", new File(fileName)))
				throw new RuntimeException("Impossible to write image");
			image.flush();
		} catch (final Exception ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		}
	}

	/**
	 * Capture image.
	 *
	 * @param surface
	 *            the surface
	 * @return the buffered image
	 */
	@Override
	public GamaImage captureImage(final IDisplaySurface surface) {
		final LayeredDisplayData data = surface.getData();
		GamaImage image = null;
		GamaPoint p = data.getImageDimension();
		Rectangle composite = surface.getBoundsForSnapshot();
		final int width = p == null || p.x <= 0 ? composite.width : (int) p.x;
		final int height = p == null || p.y <= 0 ? composite.height : (int) p.y;

		if (GamaPreferences.Displays.DISPLAY_FAST_SNAPSHOT.getValue()) {
			try {
				DEBUG.OUT("Snapshot with dimensions " + composite);
				Image im = robot.createScreenCapture(composite);
				image = ImageOperators.scaleImage(im, width, height);
				im.flush();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		// in case it has not worked, snapshot is still null
		if (image == null) {
			DEBUG.OUT("Trying to snapshot with dimensions " + width + " " + height);
			image = (GamaImage) surface.getImage(width, height);
		}
		return image;
	}

	/**
	 * Do snapshot.
	 *
	 * @param scope
	 *            the scope
	 * @param autosavePath
	 *            the autosave path
	 */
	@Override
	public void takeAndSaveScreenshot(final IScope scope, final String autosavePath) {
		if (scope == null) return;

		String fileName;
		if (autosavePath == null || autosavePath.isBlank()) {
			final String snapshotFile = FileUtils.constructAbsoluteFilePath(scope,
					IDisplaySurface.SNAPSHOT_FOLDER_NAME + "/" + GAMA.getModel().getName() + "_screen", false);
			fileName = snapshotFile + "_cycle_" + scope.getClock().getCycle() + "_time_"
					+ java.lang.System.currentTimeMillis() + ".png";
		} else {
			fileName = FileUtils.constructAbsoluteFilePath(scope,
					IDisplaySurface.SNAPSHOT_FOLDER_NAME + "/" + autosavePath, false);
			if (!fileName.endsWith(".png")) { fileName += ".png"; }
		}

		GamaImage image = null;
		try {
			Rectangle r = getScreenTotalArea();
			Image im = robot.createScreenCapture(r);
			image = GamaImage.from(im, false);
			im.flush();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (image == null) return;
		try {
			Files.newFolder(scope, IDisplaySurface.SNAPSHOT_FOLDER_NAME);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + IDisplaySurface.SNAPSHOT_FOLDER_NAME);
			GAMA.reportError(GAMA.getRuntimeScope(), e1, false);
			e1.printStackTrace();
			return;
		}

		try {
			ImageIO.write(image, "png", new File(fileName));
			image.flush();
		} catch (final java.io.IOException ex) {
			final GamaRuntimeException e = GamaRuntimeException.create(ex, scope);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(GAMA.getRuntimeScope(), e, false);
		}
	}

	/** The instance. */

	private static SnapshotMaker instance;

	/**
	 * Gets the single instance of SnapshotMaker.
	 *
	 * @return single instance of SnapshotMaker
	 */
	public static SnapshotMaker getInstance() {
		if (instance == null) {
			try {
				instance = new SnapshotMaker();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	/**
	 * Gets the screen total area.
	 *
	 * @param windowOrNull
	 *            the window or null
	 * @return the screen total area
	 */
	static public Rectangle getScreenTotalArea() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
	}

}
