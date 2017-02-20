/*********************************************************************************************
 *
 * 'AbstractLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * GAML statement to define the properties of a layer in a display
 *
 * @todo Description
 *
 */
@inside (
		symbols = IKeyword.DISPLAY)
public abstract class AbstractLayerStatement extends Symbol implements ILayerStatement {

	private IDisplayLayerBox box;
	LayeredDisplayOutput output;
	private Integer order = 0;
	protected boolean layerToCreate = true;

	public boolean isToCreate() {
		return layerToCreate;
	}

	public AbstractLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setBox(new LayerBox(getFacet(IKeyword.TRANSPARENCY), getFacet(IKeyword.POSITION), getFacet(IKeyword.SIZE),
				getFacet(IKeyword.REFRESH), getFacet(IKeyword.TRACE), getFacet(IKeyword.FADING),
				getFacet(IKeyword.SELECTABLE)));
		setName(desc.getName());
	}

	@Override
	public void setOrder(final Integer o) {
		order = o;
	}

	@Override
	public Integer getOrder() {
		return order;
	}

	@Override
	public int compareTo(final ILayerStatement o) {
		return order.compareTo(o.getOrder());
	}

	@Override
	public final boolean init(final IScope scope) {
		return _init(scope);
	}

	protected abstract boolean _init(IScope scope);

	@Override
	public void setDisplayOutput(final IDisplayOutput out) {
		output = (LayeredDisplayOutput) out;
	}

	public LayeredDisplayOutput getDisplayOutput() {
		return output;
	}

	public LayeredDisplayData getLayeredDisplayData() {
		if (output == null)
			return null;
		return output.getData();
	}

	@Override
	public abstract short getType();

	@Override
	public final boolean step(final IScope scope) throws GamaRuntimeException {
		if (!scope.interrupted()) { return _step(scope); }
		return false;
	}

	protected abstract boolean _step(IScope scope);

	@Override
	public final Double getTransparency() {
		return getBox().getTransparency();
	}

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
	public void setChildren(final Iterable<? extends ISymbol> children) {}

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

	@Override
	public void setRefresh(final Boolean refresh) {
		getBox().setRefresh(refresh);
	}

	@Override
	public void setSelectable(final Boolean s) {
		getBox().setSelectable(s);
	}

}
