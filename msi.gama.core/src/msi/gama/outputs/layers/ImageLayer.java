/*******************************************************************************************************
 *
 * ImageLayer.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.runtime.exceptions.GamaRuntimeException.error;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.FileDrawingAttributes;
import msi.gaml.types.GamaFileType;
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
	GamaImageFile cachedFile;
	
	/** The file. */
	IExpression file;
	
	/** The is potentially variable. */
	boolean isPotentiallyVariable;
	
	/** The is file. */
	boolean isFile;

	/**
	 * Instantiates a new image layer.
	 *
	 * @param scope the scope
	 * @param layer the layer
	 */
	public ImageLayer(final IScope scope, final ILayerStatement layer) {
		super(layer);
		file = ((ImageLayerStatement) definition).file;
		isFile = file.getGamlType().getGamlType().equals(Types.FILE);
		isPotentiallyVariable = !file.isContextIndependant();
		if (!isFile) {
			if (file.isConst() || !isPotentiallyVariable) {
				final String constantFilePath = Cast.asString(scope, file.value(scope));
				cachedFile = createFileFromString(scope, constantFilePath);
				isFile = true;
			}
		} else if (!isPotentiallyVariable) {
			cachedFile = createFileFromFileExpression(scope);
			isFile = true;
		}
	}

	@Override
	protected ILayerData createData() {
		return new ImageLayerData(definition);
	}

	/**
	 * Creates the file from file expression.
	 *
	 * @param scope the scope
	 * @return the gama image file
	 */
	private GamaImageFile createFileFromFileExpression(final IScope scope) {
		final GamaFile<?, ?> result = (GamaFile<?, ?>) file.value(scope);
		return verifyFile(scope, result);
	}

	/**
	 * Creates the file from string.
	 *
	 * @param scope the scope
	 * @param imageFileName the image file name
	 * @return the gama image file
	 */
	private GamaImageFile createFileFromString(final IScope scope, final String imageFileName) {
		final GamaImageFile result = GamaFileType.createImageFile(scope, imageFileName, null);
		return verifyFile(scope, result);
	}

	/**
	 * Verify file.
	 *
	 * @param scope the scope
	 * @param input the input
	 * @return the gama image file
	 */
	private GamaImageFile verifyFile(final IScope scope, final GamaFile<?, ?> input) {
		if (input == cachedFile) return cachedFile;
		if (input == null) throw error("Not a file: " + file.serialize(false), scope);
		if (!(input instanceof GamaImageFile)) throw error("Not an image:" + input.getPath(scope), scope);
		final GamaImageFile result = (GamaImageFile) input;
		try {
			result.getImage(scope, !getData().getRefresh());
		} catch (final GamaRuntimeFileException ex) {
			throw ex;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
		cachedFile = result;
		env = computeEnvelope(scope, result);
		return result;
	}

	/**
	 * Compute envelope.
	 *
	 * @param scope the scope
	 * @param file the file
	 * @return the envelope 3 D
	 */
	private Envelope3D computeEnvelope(final IScope scope, final GamaImageFile file) {
		return file.getGeoDataFile(scope) != null ? file.computeEnvelope(scope) : scope.getSimulation().getEnvelope();
	}

	/**
	 * Builds the image.
	 *
	 * @param scope the scope
	 * @return the gama image file
	 */
	protected GamaImageFile buildImage(final IScope scope) {
		if (!isPotentiallyVariable) return cachedFile;
		return isFile ? createFileFromFileExpression(scope)
				: createFileFromString(scope, Cast.asString(scope, file.value(scope)));
	}

	@Override
	public void privateDraw(final IGraphicsScope scope, final IGraphics dg) {
		final GamaImageFile file = buildImage(scope);
		if (file == null) return;
		final FileDrawingAttributes attributes = new FileDrawingAttributes(null, true);
		attributes.setUseCache(!getData().getRefresh());
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
		dg.drawFile(file, attributes);
	}

	@Override
	public void dispose() {
		super.dispose();
		cachedFile = null;
		env = null;
	}

	@Override
	public String getType() { return "Image layer"; }

	/**
	 * @param newValue
	 */
	public void setImageFileName(final IScope scope, final String newValue) {
		createFileFromString(scope, newValue);
		isFile = true;
		isPotentiallyVariable = false;
	}

	/**
	 * Gets the image file name.
	 *
	 * @param scope the scope
	 * @return the image file name
	 */
	public String getImageFileName(final IScope scope) {
		if (cachedFile != null && !isPotentiallyVariable) return cachedFile.getPath(scope);
		return "Unknown";
	}

}
