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

import java.awt.geom.Rectangle2D;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@inside(symbols = IKeyword.DISPLAY)
public abstract class AbstractDisplayLayer extends Symbol implements IDisplayLayer {

	private IDisplayLayerBox box;
	IExpression title;
	String constantTitle;
	IDisplayOutput output;

	// protected IDisplay physicalLayer;

	public AbstractDisplayLayer(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		verifyFacetType(IKeyword.TRANSPARENCY);
		verifyFacetType(IKeyword.POSITION);
		verifyFacetType(IKeyword.SIZE);
		setBox(new LayerBox(getFacet(IKeyword.TRANSPARENCY), getFacet(IKeyword.POSITION),
			getFacet(IKeyword.SIZE)));
		title = getFacet(IKeyword.NAME);
		if ( title.isConst() ) {
			setName(title.literalValue());
			constantTitle = title.literalValue();
			// currentTitle = constantTitle;
		}

	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		output = out;
	}

	@Override
	public abstract short getType();

	public String serializeToGaml() {
		return "";
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		getBox().compute(scope);
	}

	@Override
	public final Double getTransparency() {
		return getBox().getTransparency();
	}

	@Override
	public Rectangle2D.Double getBoundingBox() {
		return getBox().getBoundingBox();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	public void setBox(final IDisplayLayerBox box) {
		this.box = box;
	}

	@Override
	public IDisplayLayerBox getBox() {
		return box;
	}

	@Override
	public void setOpacity(final Double opacity) {
		getBox().setTransparency(opacity);
	}

	/**
	 * @param abstractDisplay
	 */
	// @Override
	// public void setPhysicalLayer(final IDisplay abstractDisplay) {
	// physicalLayer = abstractDisplay;
	// }
}
