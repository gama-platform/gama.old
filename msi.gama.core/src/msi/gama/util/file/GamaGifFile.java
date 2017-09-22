package msi.gama.util.file;

import msi.gama.common.util.ImageUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.IType;

@file (
		name = "gif",
		extensions = { "gif" },
		buffer_type = IType.MATRIX,
		buffer_content = IType.INT,
		buffer_index = IType.POINT,
		concept = { IConcept.IMAGE, IConcept.FILE },
		doc = @doc ("GIF files represent a particular type of image files, which can be animated"))
public class GamaGifFile extends GamaImageFile {

	private int averageDelay;
	private int frameCount;

	public GamaGifFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaGifFile(final IScope scope, final String pathName, final IMatrix<Integer> image) {
		super(scope, pathName, image);

	}

	@Override
	public boolean isAnimated() {
		return getFrameCount() > 0;
	}

	public int getAverageDelay() {
		return ImageUtils.getInstance().getDuration(path) / getFrameCount();
	}

	public int getFrameCount() {
		return ImageUtils.getInstance().getFrameCount(path);
	}

}
