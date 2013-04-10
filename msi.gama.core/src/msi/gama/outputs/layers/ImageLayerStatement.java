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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.outputs.IDisplayOutput;
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
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.IMAGE, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.FILE, type = IType.STRING, optional = true),
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.STRING, optional = true),
	@facet(name = IKeyword.GIS, type = IType.STRING, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.Z, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
public class ImageLayerStatement extends AbstractLayerStatement {

	public ImageLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
	}

	IExpression imageFileExpression = null;
	String constantImage = null;
	String currentImage = null;

	private GisLayer gisLayer = null;

	public GisLayer getGisLayer() {
		return gisLayer;
	}

	@Override
	public short getType() {
		if ( getFacet(IKeyword.GIS) == null ) { return ILayerStatement.IMAGE; }
		return ILayerStatement.GIS;
	}

	public String getImageFileName() {
		return currentImage;
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		if ( getFacet(IKeyword.GIS) != null ) {
			buildGisLayer(scope);
		} else {
			if ( constantImage == null ) {
				// Redefined to allow replacing the "name" attribute by "file"
				IExpression tag = getFacet(IKeyword.NAME);
				if ( tag == null ) {
					tag = getFacet(IKeyword.FILE);
				}
				if ( tag == null ) { throw new GamaRuntimeException("Missing properties " +
					IKeyword.NAME + " and " + IKeyword.FILE); }
				if ( tag.isConst() ) {
					setName(Cast.asString(scope, tag.value(scope)));
				} else {
					setName(tag.toGaml());
				}
				imageFileExpression = getFacet(IKeyword.FILE);
				if ( imageFileExpression == null ) {
					imageFileExpression = getFacet(IKeyword.NAME);
				}
				if ( imageFileExpression == null ) { throw new GamaRuntimeException(
					"Image file not defined"); }
				// setFacet(IKeyword.FILE, imageFileExpression);
				if ( imageFileExpression.isConst() ) {
					constantImage = Cast.asString(scope, imageFileExpression.value(scope));
					currentImage = constantImage;
					try {
						ImageUtils.getInstance().getImageFromFile(constantImage);
					} catch (final Exception ex) {
						constantImage = null;
						throw new GamaRuntimeException(ex);
					}
				}
			}
		}
	}

	public void buildGisLayer(final IScope scope) throws GamaRuntimeException {
		String fileName =
			getFacet(IKeyword.GIS) != null ? Cast.asString(scope,
				getFacet(IKeyword.GIS).value(scope)) : name;
		GamaShapeFile file = new GamaShapeFile(scope, fileName);
		GamaColor c = null;
		IExpression colorExpr = getFacet(IKeyword.COLOR);
		if ( colorExpr != null ) {
			c = Cast.asColor(scope, getFacet(IKeyword.COLOR).value(scope));
		}
		gisLayer = new GisLayer(file.getContents(scope), c, "");
	}

	@Override
	public void dispose() {
		super.dispose();
		gisLayer = null;
	}

	public static class GisLayer {

		private final IContainer<Integer, GamaShape> objects;
		private String type;
		private Color color = Color.black;

		public GisLayer(final IContainer<Integer, GamaShape> objects, final Color color,
			final String type) {
			super();
			this.objects = objects;
			if ( color != null ) {
				this.color = color;
			}
			this.type = type;
		}

		public IContainer<Integer, GamaShape> getObjects() {
			return objects;
		}

		public void dipose() {
			type = null;
		}

		public String getType() {
			return type;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public Color getColor() {
			return color;
		}
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);
		if ( gisLayer == null ) {
			currentImage =
				constantImage != null ? constantImage : Cast.asString(scope,
					imageFileExpression.value(scope));
		}
	}

	/**
	 * @throws GamlException
	 * @throws GamaRuntimeException
	 * @param newValue
	 */
	public void setGisLayerName(final String newValue) throws GamaRuntimeException {
		setName(newValue);
		IScope scope = GAMA.obtainNewScope();
		if ( scope == null ) { throw new GamaRuntimeException("No simulation running"); }
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

}
