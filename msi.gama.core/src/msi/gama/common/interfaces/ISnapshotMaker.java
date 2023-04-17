/*******************************************************************************************************
 *
 * ISnapshotMaker.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.image.BufferedImage;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;

/**
 * The Interface ISnapshotMaker.
 */
public interface ISnapshotMaker {

	/**
	 * Do snapshot.
	 *
	 * @param surface
	 *            the surface
	 * @param composite
	 *            the composite
	 */
	default void takeAndSaveSnapshot(final IDisplaySurface surface, GamaPoint desiredDimensions) {}

	/**
	 * Capture image.
	 *
	 * @param surface
	 *            the surface
	 * @return the buffered image
	 */
	default BufferedImage captureImage(final IDisplaySurface surface, GamaPoint desiredDimensions) {
		return null;
	}

	/**
	 * Do snapshot.
	 *
	 * @param scope
	 *            the scope
	 * @param autosavePath
	 *            the autosave path
	 */
	default void takeAndSaveScreenshot(final IScope scope, final String autosavePath) {}

}