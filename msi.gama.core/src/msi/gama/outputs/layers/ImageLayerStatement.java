/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
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
@facets(value = { @facet(name = IKeyword.FILE, type = { IType.STRING, IType.FILE }, optional = true),
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.STRING, optional = true),
	@facet(name = IKeyword.GIS, type = { IType.FILE, IType.STRING }, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
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
				if ( tag == null ) { throw GamaRuntimeException.error("Missing properties " + IKeyword.NAME + " and " +
					IKeyword.FILE); }
				if ( tag.isConst() ) {
					setName(Cast.asString(scope, tag.value(scope)));
				} else {
					setName(tag.toGaml());
				}
				if ( imageFileExpression == null ) { throw GamaRuntimeException.error("Image file not defined"); }
				if ( imageFileExpression.isConst() ) {
					constantImage = Cast.asString(scope, imageFileExpression.value(scope));
					currentImage = constantImage;
					try {
						ImageUtils.getInstance().getImageFromFile(constantImage);
					} catch (final Exception ex) {
						constantImage = null;
						throw GamaRuntimeException.create(ex);
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
	public void setGisLayerName(final String newValue) throws GamaRuntimeException {
		gisExpression = GAML.getExpressionFactory().createConst(newValue, Types.get(IType.STRING));
		IScope scope = GAMA.obtainNewScope();
		try {
			buildGisLayer(scope);
		} finally {
			GAMA.releaseScope(scope);
		}

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
