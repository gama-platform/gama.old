/*********************************************************************************************
 * 
 * 
 * 'AbstractLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

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
			getFacet(IKeyword.REFRESH), getFacet(IKeyword.TRACE), getFacet(IKeyword.FADING)));
		final IExpression title = getFacet(IKeyword.NAME);
		if ( title != null && title.isConst() ) {
			setName(title.literalValue());
		}
	}

	@Override
	public final boolean init(final IScope scope) {
		// getBox().compute(scope);
		return _init(scope);
	}

	protected abstract boolean _init(IScope scope);

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
	public final boolean step(final IScope scope) throws GamaRuntimeException {
		return _step(scope);
	}

	protected abstract boolean _step(IScope scope);

	@Override
	public final Double getTransparency() {
		return getBox().getTransparency();
	}

	//
	// @Override
	// public final Double getElevation() {
	// return getBox().getElevation();
	// }

	@Override
	public final Boolean getRefresh() {
		return getBox().getRefresh();
	}

	@Override
	public final Integer getTrace() {
		return getBox().getTrace();
	}

	@Override
	public final Boolean getFading() {
		return getBox().getFading();
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
	public void setTransparency(final Double transparency) {
		getBox().setTransparency(transparency);
	}

	//
	// @Override
	// public void setElevation(final Double elevation) {
	// getBox().setElevation(elevation);
	// }

	@Override
	public void setRefresh(final Boolean refresh) {
		getBox().setRefresh(refresh);
	}

}
