/*******************************************************************************************************
 *
 * IImageProvider.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.image.BufferedImage;

import javax.annotation.Nonnegative;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

/**
 * The Interface IImageProvider. An abstraction to represent objects that can provide an image (a BufferedImage) and a
 * name associated to it (hence the inheritance from INamed).
 */
public interface IImageProvider extends IEnvelopeProvider, IAsset {

	/**
	 * Returns the number of rows (height) of the receiver
	 *
	 * @param scope
	 * @return
	 */
	@Nonnegative
	int getRows(IScope scope);

	/**
	 * Returns the number of columns (width) of the receiver
	 *
	 * @param scope
	 * @return
	 */
	@Nonnegative
	int getCols(IScope scope);

	/**
	 * Compute envelope.
	 *
	 * @param scope
	 *            the scope
	 * @return the envelope 3 D
	 */
	@Override
	default Envelope3D computeEnvelope(final IScope scope) {
		return Envelope3D.of(0, getCols(scope), 0, getRows(scope), 0, 0);
	}

	/**
	 * Returns the image provided by this provider, specifying whether to compute or load it again or to use a cache. It
	 * is assumed (by default) that the image produced is not for OpenGL
	 *
	 * @param scope
	 *            the scope
	 * @return the image
	 */
	BufferedImage getImage(final IScope scope, final boolean useCache);

	/**
	 * Gets the image. Default is to use whatever cache is provided
	 *
	 * @param scope
	 *            the scope
	 * @return the image
	 */
	default BufferedImage getImage(final IScope scope) {
		return getImage(scope, true);
	}

	/**
	 * Checks if is animated.
	 *
	 * @return true, if is animated
	 */
	default boolean isAnimated() { return false; }

}
