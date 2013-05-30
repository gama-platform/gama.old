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
 * GAML statement to define the properties of a layer in a display
 * 
 * @todo Description
 * 
 */
@inside(symbols = IKeyword.DISPLAY)
public abstract class AbstractLayerStatement extends Symbol implements ILayerStatement {

	private IDisplayLayerBox box;
	IDisplayOutput output;

	public AbstractLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setBox(new LayerBox(getFacet(IKeyword.TRANSPARENCY), getFacet(IKeyword.POSITION), getFacet(IKeyword.SIZE),
			getFacet(IKeyword.Z), getFacet(IKeyword.REFRESH)));
		final IExpression title = getFacet(IKeyword.NAME);
		if ( title != null && title.isConst() ) {
			setName(title.literalValue());
		}
	}

	@Override
	public final void init(final IScope scope) throws GamaRuntimeException {
		getBox().compute(scope);
		_init(scope);
	}

	protected abstract void _init(IScope scope);

	@Override
	public void setDisplayOutput(final IDisplayOutput out) {
		output = out;
	}

	@Override
	public abstract short getType();

	public String serializeToGaml() {
		return "";
	}

	@Override
	public final void step(final IScope scope) throws GamaRuntimeException {
		getBox().compute(scope);
		_step(scope);
	}

	protected abstract void _step(IScope scope);

	@Override
	public final Double getTransparency() {
		return getBox().getTransparency();
	}

	@Override
	public final Double getElevation() {
		return getBox().getElevation();
	}

	@Override
	public final Boolean getRefresh() {
		return getBox().getRefresh();
	}

	@Override
	public Rectangle2D.Double getBoundingBox() {
		return getBox().getBoundingBox();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {}

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

	@Override
	public void setElevation(final Double elevation) {
		getBox().setElevation(elevation);
	}

	@Override
	public void setRefresh(final Boolean refresh) {
		getBox().setRefresh(refresh);
	}

}
