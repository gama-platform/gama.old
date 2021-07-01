/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.GridLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.MESH,
		kind = ISymbolKind.LAYER,
		with_sequence = false,
		concept = { IConcept.GRID, IConcept.DISPLAY, IConcept.LAYER })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.POSITION,
				type = IType.POINT,
				optional = true,
				doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = "no_data",
						type = { IType.FLOAT },
						optional = true,
						doc = @doc ("Can be used to specify a 'no_data' value, forcing the renderer to not render the cells with this value. If not specified, that value will be searched in the field to display")),
				@facet (
						name = IKeyword.SCALE,
						type = { IType.FLOAT },
						optional = true,
						doc = @doc ("Represents the z-scaling factor, which allows to scale all values of the field. ")),
				@facet (
						name = IKeyword.SIZE,
						type = { IType.POINT, IType.FLOAT },
						optional = true,
						doc = @doc ("Represents the extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions. This latter possibility allows to limit the height of the field. If only a flat value is provided, it is considered implicitly as the z maximal amplitude (or z scaling factor if < 1)")),
				@facet (
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency level of the layer (between 0 -- opaque -- and 1 -- fully transparent)")),
				@facet (
						name = IKeyword.BORDER,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the color to draw lines (borders of cells)")),
				@facet (
						name = IKeyword.WIREFRAME,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true displays the field in wireframe using the lines color")),
				@facet (
						name = IKeyword.SMOOTH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Applies a simple convolution (box filter) to smooth out the terrain produced by this field. Does not change the values of course.")),
				@facet (
						name = IKeyword.SOURCE,
						type = { IType.FILE, IType.MATRIX, IType.SPECIES },
						optional = false,
						doc = @doc ("Allows to specify the elevation of each cell by passing a grid, a raster, image or csv file or directly a matrix of int/float. The dimensions of the field are those of the file or matrix.")),
				@facet (
						name = IKeyword.TEXTURE,
						type = { IType.FILE },
						optional = true,
						doc = @doc ("A file  containing the texture image to be applied to the field. If not specified, the field will be displayed either in color or grayscale, depending on the other facets")),
				@facet (
						name = IKeyword.GRAYSCALE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true, gives a grey color to each polygon depending on its elevation (false by default). Supersedes 'color' if it is defined.")),
				@facet (
						name = IKeyword.COLOR,
						type = { IType.COLOR, IType.LIST, IType.MAP },
						optional = true,
						doc = @doc ("if true, and if neither 'grayscale' or 'texture' are specified, displays the field using the given color or colors. List of colors, palettes (with interpolation), gradients and scales are supported")),
				@facet (
						name = IKeyword.TRIANGULATION,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("specifies wether the cells of th field will be triangulated: if it is false, they will be displayed as horizontal squares at a given elevation, whereas if it is true, cells will be triangulated and linked to neighbors in order to have a continuous surface (false by default)")),
				@facet (
						name = IKeyword.TEXT,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("specify whether the value that represents the elevation is displayed on each cell (false by default)")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("(openGL only) specify whether the display of the species is refreshed. (true by default, but should be deactivated if the field is static)")) },
		omissible = IKeyword.SOURCE)
@doc (
		value = "Allows the modeler to display in an optimized way a field of values, optionally using elevation. Useful for displaying DEMs, for instance, without having to load them into a grid. Can be fed with a matrix of int/float, a grid, a csv/raster/image file and supports many visualisation options",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display my_display {",
						isExecutable = false),
						@example (
								value = "   field a_filename lines: #black position: { 0.5, 0 } size: {0.5,0.5} triangulated: true texture: anothe_file;",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }), },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.GRID, IKeyword.EVENT, "graphics", IKeyword.IMAGE,
				IKeyword.OVERLAY, IKeyword.POPULATION })
public class MeshLayerStatement extends AbstractLayerStatement {

	public MeshLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SOURCE).literalValue());
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput out) {
		return LayerType.MESH;
	}

	@Override
	public boolean _step(final IScope sim) throws GamaRuntimeException {
		return true;
	}

}
