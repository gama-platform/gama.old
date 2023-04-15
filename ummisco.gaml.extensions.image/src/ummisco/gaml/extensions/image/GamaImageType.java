/*******************************************************************************************************
 *
 * GamaImageType.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */

/**
 * The Class GamaImageType.
 */

/**
 * The Class GamaImageType.
 */

@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.IMAGE,
		id = GamaImageType.ID,
		wraps = { GamaImage.class },
		kind = ISymbolKind.Variable.REGULAR,
		doc = { @doc ("Represents ARGB image objects that can be passed directly as arguments to draw statements and other similar functions. "
				+ "An image can be created from many different sources : a field, a grid, a file containing an image, and a number of operators allow to apply filters or to combine them. They can of course be saved on disk") },
		concept = { IConcept.TYPE, IConcept.IMAGE, IConcept.DISPLAY })
public class GamaImageType extends GamaType<GamaImage> {

	/** The Constant ID. */
	public static final int ID = IType.AVAILABLE_TYPES + 30;

	/**
	 * Cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param copy
	 *            the copy
	 * @return the gama font
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "Cast any object to an image",
			usages = { @usage (
					value = "if the operand is a point, returns an empty image of dimensions x and y",
					examples = { @example ("image i <- image({100,100}); // equivalent to image(100, 100)") }),
					@usage (
							value = "if the operand is a grid species, returns an image of the same dimension where each cell gives its color to the corresponding pixel",
							examples = { @example ("image f <- image(my_grid);") }),
					@usage (
							value = "if the operand is a field (or a matrix of float), return an image, where each cell gives a gray value to the corresponding pixel (after normalization)",
							examples = { @example ("image f <- image(my_field);") }),
					@usage (
							value = "if the operand is a string, tries to load the corresponding file (if any) as an image_file and returns its contents, otherwise returns nil if it doesnt exist or if the path does not represent an image file",
							examples = { @example ("image f <- image('/images/image.png');") }),
					@usage (
							value = "in all other cases, return nil",
							examples = { @example ("image f <- image(12); // --> nil") }) })
	@Override
	public GamaImage cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param copy
	 *            the copy
	 * @return the gama font
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static GamaImage staticCast(final IScope scope, final Object obj, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof GamaImage im) {
			if (copy) return GamaImage.from(im, im.getAlpha(scope));
			return im;
		}
		if (obj instanceof GamaIntMatrix mat) return GamaImage.from(scope, mat);
		if (obj instanceof BufferedImage im) return GamaImage.from(im, true);
		if (obj instanceof Image im) return ImageHelper.copyToOptimalImage(im);
		if (obj instanceof GamaImageFile f) return GamaImage.from(f.getImage(scope, true), true, f.getOriginalPath());
		if (obj instanceof GamaPoint p) return ImageOperators.image((int) p.getX(), (int) p.getY());
		if (obj instanceof String s) return staticCast(scope, new GamaImageFile(scope, s), false);
		if (obj instanceof GamaField f) return GamaImage.from(scope, f);
		if (obj instanceof ISpecies s && s.isGrid())
			return GamaImage.from((GamaSpatialMatrix) s.getPopulation(scope).getTopology().getPlaces());
		return null;
	}

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	@Override
	public GamaImage getDefault() { return null; }

	/**
	 * Can cast to const.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isDrawable() { return true; }

	@Override
	public GamaImage copyFromClipboard(final IScope scope) {
		if (ImageConstants.clipboard == null) return null;
		Transferable content = ImageConstants.clipboard.getContents(null);
		if (content == null || !content.isDataFlavorSupported(DataFlavor.imageFlavor)) return null;
		try {
			return staticCast(scope, content.getTransferData(DataFlavor.imageFlavor), false);
		} catch (UnsupportedFlavorException | IOException e) {
			return null;
		}

	}

}
