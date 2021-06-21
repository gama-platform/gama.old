package msi.gama.common.util;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.file.IFieldMatrixProvider;
import msi.gaml.species.ISpecies;

public class FieldUtils {

	private static final double[] DNULL = new double[0];

	public static double[] buildDoubleArrayFrom(final IScope scope, final Object object, final GamaPoint dim) {
		if (object instanceof IFieldMatrixProvider) {
			IFieldMatrixProvider provider = (IFieldMatrixProvider) object;
			dim.x = provider.getCols(scope);
			dim.y = provider.getRows(scope);
			return provider.getFieldData(scope);
		}
		// Special case for grid species
		if (object instanceof ISpecies) {
			ISpecies species = (ISpecies) object;
			if (species.isGrid())
				return buildDoubleArrayFrom(scope, species.getPopulation(scope).getTopology().getPlaces(), dim);
		}
		return DNULL;
	}

	public static float[] buildFloatArrayFrom(final IScope scope, final Object object, final GamaPoint dim) {
		if (object instanceof GamaImageFile) return buildFloatArrayFrom(scope, (GamaImageFile) object, dim);
		return toFloats(buildDoubleArrayFrom(scope, object, dim));
	}

	private static float[] buildFloatArrayFrom(final IScope scope, final GamaImageFile file, final GamaPoint dim) {
		BufferedImage image = file.getImage(scope, true);
		dim.x = image.getWidth();
		dim.y = image.getHeight();
		final float[] values = new float[(int) (dim.x * dim.y)];
		int[] pixels = new int[values.length];
		PixelGrabber pgb = new PixelGrabber(image, 0, 0, (int) dim.x, (int) dim.y, pixels, 0, (int) dim.x);
		try {
			pgb.grabPixels();
		} catch (InterruptedException e) {}
		for (int i = 0; i < values.length; ++i) {
			values[i] = pixels[i] & 255;
		}
		return values;
	}

	public static float[] toFloats(final double[] array) {
		float[] result = new float[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = (float) array[i];
		}
		return result;
	}

}
