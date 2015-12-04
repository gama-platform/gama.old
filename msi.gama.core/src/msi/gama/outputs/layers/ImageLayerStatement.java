/*********************************************************************************************
 *
 *
 * 'ImageLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaShapeFile;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol(name = IKeyword.IMAGE, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(
	value = {
		@facet(name = IKeyword.FILE,
			type = { IType.STRING, IType.FILE },
			optional = true,
			doc = @doc("the name/path of the image (in the case of a raster image)") ),
		@facet(name = IKeyword.POSITION,
			type = IType.POINT,
			optional = true,
			doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.") ),
		@facet(name = IKeyword.SIZE,
			type = IType.POINT,
			optional = true,
			doc = @doc("the layer resize factor: {1,1} refers to the original size whereas {0.5,0.5} divides by 2 the height and the width of the layer. In case of a 3D layer, a 3D point can be used (note that {1,1} is equivalent to {1,1,0}, so a resize of a layer containing 3D objects with a 2D points will remove the elevation)") ),
		@facet(name = IKeyword.TRANSPARENCY,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the transparency rate of the agents (between 0 and 1, 1 means no transparency)") ),
		@facet(name = IKeyword.NAME,
			type = IType.STRING,
			optional = true,
			doc = @doc("Human readable title of the image layer") ),
		@facet(name = IKeyword.GIS,
			type = { IType.FILE, IType.STRING },
			optional = true,
			doc = @doc("the name/path of the shape file (to display a shapefile as background, without creating agents from it)") ),
		@facet(name = IKeyword.COLOR,
			type = IType.COLOR,
			optional = true,
			doc = @doc("in the case of a shapefile, this the color used to fill in geometries of the shapefile") ),
		@facet(name = IKeyword.REFRESH,
			type = IType.BOOL,
			optional = true,
			doc = @doc("(openGL only) specify whether the image display is refreshed. (true by default, usefull in case of images that is not modified over the simulation)") ) },
	omissible = IKeyword.NAME)
@doc(value = "`" + IKeyword.IMAGE + "` allows modeler to display an image (e.g. as background of a simulation).",
	usages = {
		@usage(value = "The general syntax is:",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "   image layer_name file: image_file [additional options];", isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(value = "For instance, in the case of a bitmap image",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "   image background file:\"../images/my_backgound.jpg\";", isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(value = "Or in the case of a shapefile:",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "   image testGIS gis: \"../includes/building.shp\" color: rgb('blue');",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(
			value = "It is also possible to superpose images on different layers in the same way as for species using opengl display:",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "  image image1 file:\"../images/image1.jpg\";", isExecutable = false),
				@example(value = "  image image2 file:\"../images/image2.jpg\";", isExecutable = false),
				@example(value = "  image image3 file:\"../images/image3.jpg\" position: {0,0,0.5};",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
		IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION, IKeyword.TEXT })
public class ImageLayerStatement extends AbstractLayerStatement {

	public ImageLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		imageFileExpression = getFacet(IKeyword.FILE, IKeyword.NAME);
		gisExpression = getFacet(IKeyword.GIS);
		colorExpression = getFacet(IKeyword.COLOR);
	}

	final IExpression imageFileExpression;
	IExpression gisExpression;
	final IExpression colorExpression;
	String constantImage = null;
	String currentImage = null;
	Color color = null;

	private IList<IShape> shapes = null;

	public IList<IShape> getShapes() {
		return shapes;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public short getType() {
		if ( gisExpression == null ) { return ILayerStatement.IMAGE; }
		return ILayerStatement.GIS;
	}

	public String getImageFileName() {
		return currentImage;
	}

	// FIXME Use GamaImageFile
	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		if ( gisExpression != null ) {
			buildGisLayer(scope);
		} else {
			if ( constantImage == null ) {
				// Redefined to allow replacing the "name" attribute by "file"
				IExpression tag = getFacet(IKeyword.NAME);
				if ( tag == null ) {
					tag = getFacet(IKeyword.FILE);
				}
				if ( tag == null ) { throw GamaRuntimeException
					.error("Missing properties " + IKeyword.NAME + " and " + IKeyword.FILE, scope); }
				if ( tag.isConst() ) {
					setName(Cast.asString(scope, tag.value(scope)));
				} else {
					setName(tag.serialize(false));
				}
				if ( imageFileExpression == null ) { throw GamaRuntimeException.error("Image file not defined",
					scope); }
				if ( imageFileExpression.isConst() ) {
					constantImage = Cast.asString(scope, imageFileExpression.value(scope));
					currentImage = constantImage;
					try {
						ImageUtils.getInstance().getImageFromFile(scope, constantImage);
					} catch (final Exception ex) {
						constantImage = null;
						throw GamaRuntimeException.create(ex, scope);
					}
				}
			}
		}
		return true;
	}

	private GamaShapeFile getShapeFile(final IScope scope) {
		if ( gisExpression == null ) { return null; }
		if ( gisExpression.getType().id() == IType.STRING ) {
			String fileName = Cast.asString(scope, gisExpression.value(scope));
			return new GamaShapeFile(scope, fileName);
		}
		Object o = gisExpression.value(scope);
		if ( o instanceof GamaShapeFile ) { return (GamaShapeFile) o; }
		return null;
	}

	public void buildGisLayer(final IScope scope) throws GamaRuntimeException {
		GamaShapeFile file = getShapeFile(scope);
		if ( colorExpression != null ) {
			color = Cast.asColor(scope, colorExpression.value(scope));
		}
		shapes = file.getContents(scope);
	}

	@Override
	public void dispose() {
		super.dispose();
		shapes = null;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		if ( gisExpression == null ) {
			currentImage =
				constantImage != null ? constantImage : Cast.asString(scope, imageFileExpression.value(scope));
		} else {
			if ( shapes == null ) {
				buildGisLayer(scope);
			}
		}
		return true;
	}

	/**
	 * @throws GamlException
	 * @throws GamaRuntimeException
	 * @param newValue
	 */
	public void setGisLayerName(final IScope scope, final String newValue) throws GamaRuntimeException {
		gisExpression = GAML.getExpressionFactory().createConst(newValue, Types.STRING);
		buildGisLayer(scope);
	}

	/**
	 * @param newValue
	 */
	public void setImageFileName(final String newValue) {
		constantImage = newValue;
	}

	/**
	 *
	 */
	public void resetShapes() {
		shapes = null;
	}

}
