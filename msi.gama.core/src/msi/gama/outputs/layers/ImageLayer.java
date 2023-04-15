/*******************************************************************************************************
 *
 * ImageLayer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import java.awt.image.BufferedImage;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.AssetDrawingAttributes;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
public class ImageLayer extends AbstractLayer {

	/** The env. */
	// Cache a copy of both to avoid reloading them each time.
	Envelope3D env;

	/** The cached file. */
	IImageProvider cachedImageProvider;

	/** The file. */
	IExpression provider;

	/** The matrix. */
	IExpression matrix;

	/** The is potentially variable. */
	boolean isFilePotentiallyVariable;

	/** The is potentially variable. */
	boolean isMatrixPotentiallyVariable;

	/** The is file. */
	boolean isImageProvider;

	/** whether it's a matrix or not **/

	/** cached copy to avoid reloading **/
	BufferedImage cachedBufferedImage;

	/**
	 * Instantiates a new image layer.
	 *
	 * @param scope
	 *            the scope
	 * @param layer
	 *            the layer
	 */
	public ImageLayer(final IScope scope, final ILayerStatement layer) {
		super(layer);
		provider = ((ImageLayerStatement) definition).file;
		isImageProvider = isImageProvider();
		isFilePotentiallyVariable = !provider.isContextIndependant();
		matrix = ((ImageLayerStatement) definition).matrix;
		isMatrixPotentiallyVariable = matrix == null ? false : !matrix.isContextIndependant();
		if (matrix == null) {
			if (!isImageProvider) {
				if (provider.isConst() || !isFilePotentiallyVariable) {
					Object value = provider.value(scope);
					if (value instanceof String s) {
						cachedImageProvider = createFileFromString(scope, s);
					} else if (value instanceof IImageProvider p) {
						cachedImageProvider = p;
					} else {
						final String s = Cast.asString(scope, value);
						cachedImageProvider = createFileFromString(scope, s);
					}
					isImageProvider = true;
				}
			} else if (!isFilePotentiallyVariable) {
				cachedImageProvider = createImageProviderFromFileExpression(scope);
				isImageProvider = true;
			}
		} else {
			cachedBufferedImage = GamaIntMatrix.from(scope, Cast.asMatrix(scope, matrix.value(scope))).getImage(scope);
		}

	}

	/**
	 * Checks if is image provider.
	 *
	 * @return true, if is image provider
	 */
	private boolean isImageProvider() {
		IType providerType = provider.getGamlType();
		if (IImageProvider.class.isAssignableFrom(providerType.toClass())
				|| IMatrix.class.isAssignableFrom(providerType.toClass()) && providerType.getContentType() == Types.INT)
			return true;
		return false;
	}

	@Override
	protected ILayerData createData() {
		return new ImageLayerData(definition);
	}

	/**
	 * Creates the file from file expression.
	 *
	 * @param scope
	 *            the scope
	 * @return the gama image file
	 */
	private IImageProvider createImageProviderFromFileExpression(final IScope scope) {
		return verifyFile(scope, provider.value(scope));
	}

	/**
	 * Creates the file from string.
	 *
	 * @param scope
	 *            the scope
	 * @param imageFileName
	 *            the image file name
	 * @return the gama image file
	 */
	private IImageProvider createFileFromString(final IScope scope, final String imageFileName) {
		final GamaImageFile result = GamaFileType.createImageFile(scope, imageFileName, null);
		return verifyFile(scope, result);
	}

	/**
	 * Verify file.
	 *
	 * @param scope
	 *            the scope
	 * @param input
	 *            the input
	 * @return the gama image file
	 */
	private IImageProvider verifyFile(final IScope scope, final Object input) {
		if (input == cachedImageProvider) return cachedImageProvider;
		if (!(input instanceof IImageProvider result))
			throw error("Not a provider of images: " + provider.serialize(false), scope);
		try {
			result.getImage(scope, !getData().getRefresh());
		} catch (final GamaRuntimeFileException ex) {
			throw ex;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
		cachedImageProvider = result;
		env = computeEnvelope(scope, result);
		return result;
	}

	/**
	 * Compute envelope.
	 *
	 * @param scope
	 *            the scope
	 * @param file
	 *            the file
	 * @return the envelope 3 D
	 */
	private Envelope3D computeEnvelope(final IScope scope, final IImageProvider file) {
		if (file instanceof GamaImageFile gif && gif.getGeoDataFile(scope) != null) return file.computeEnvelope(scope);
		return scope.getSimulation().getEnvelope();
	}

	/**
	 * Builds the image.
	 *
	 * @param scope
	 *            the scope
	 * @return the gama image file
	 */
	protected IImageProvider buildImage(final IScope scope) {
		if (!isFilePotentiallyVariable) return cachedImageProvider;
		return isImageProvider ? createImageProviderFromFileExpression(scope)
				: createFileFromString(scope, Cast.asString(scope, provider.value(scope)));
	}

	/**
	 * Builds the image from matrix.
	 *
	 * @param scope
	 *            the scope
	 * @return the buffered image
	 */
	protected BufferedImage buildImageFromMatrix(final IScope scope) {
		if (!isMatrixPotentiallyVariable) return cachedBufferedImage;
		return GamaIntMatrix.from(scope, Cast.asMatrix(scope, matrix.value(scope))).getImage(scope);
	}

	@Override
	public void privateDraw(final IGraphicsScope scope, final IGraphics dg) {

		// getting the drawing attributes
		final AssetDrawingAttributes attributes = new AssetDrawingAttributes(null, true);
		attributes.setUseCache(!getData().getRefresh());

		final IImageProvider file = buildImage(scope);
		if (env != null) {
			final GamaPoint loc;
			if (dg.is2D()) {
				loc = new GamaPoint(env.getMinX(), env.getMinY());
			} else {
				loc = new GamaPoint(env.getWidth() / 2 + env.getMinX(), env.getHeight() / 2 + env.getMinY());
			}
			attributes.setLocation(loc);
			attributes.setSize(Scaling3D.of(env.getWidth(), env.getHeight(), 0));
		}

		if (file != null) {
			dg.drawAsset(file, attributes);
		} else {
			final BufferedImage img = buildImageFromMatrix(scope);
			if (img != null) { dg.drawImage(img, attributes); }
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		cachedImageProvider = null;
		env = null;
	}

	@Override
	public String getType() { return "Image layer"; }

}
