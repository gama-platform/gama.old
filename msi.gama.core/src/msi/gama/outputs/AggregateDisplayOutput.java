/**
 * Created by drogoul, 22 déc. 2015
 *
 */
package msi.gama.outputs;

import java.util.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Class AggregateDisplayOutput. Represents a collection of outputs that are grouped together in a unique view. This output cannot be created from GAML, but is instead built dynamically during
 * simulations.
 *
 * TODO Finish this class and use it to implement a unique output per view
 * WARNING: Not functional (and actually not used) at the moment
 *
 *
 *
 * @author Alexis Drogoul, IRD
 * @since 22 déc. 2015
 *
 */
public class AggregateDisplayOutput implements IOutput, IDisplayOutput {

	/**
	 * The collection of outputs present in this aggregate
	 */
	final Set<IDisplayOutput> outputs = new LinkedHashSet();
	/**
	 * The name of this aggregate (usually displayed in the view title)
	 */
	String name = null;

	/**
	 * The id of the view that has created this output
	 */
	final String viewId;

	/**
	 * The scope is determined by the first output added to this aggregate. When it is empty, the scope is null to reflect this.
	 */

	IScope scope;

	/**
	 * Aggregate outputs are normally created by views, which are supposed to pass them their id
	 */
	public AggregateDisplayOutput(final String id) {
		viewId = id;
	}

	/**
	 * Method dispose()
	 * @see msi.gaml.compilation.ISymbol#dispose()
	 */
	@Override
	public void dispose() {
		for ( IDisplayOutput output : outputs ) {
			output.dispose();
		}
		outputs.clear();
	}

	/**
	 * Method getDescription()
	 * Returns null as this aggregate has no description in GAML.
	 * @see msi.gaml.compilation.ISymbol#getDescription()
	 */
	@Override
	public IDescription getDescription() {
		return null;
	}

	/**
	 * Method getFacet()
	 * Returns null as this aggregate has no description in GAML.
	 * @see msi.gaml.compilation.ISymbol#getFacet(java.lang.String[])
	 */
	@Override
	public IExpression getFacet(final String ... keys) {
		return null;
	}

	/**
	 * Method hasFacet().
	 * Returns false as this aggregate has no description in GAML.
	 * @see msi.gaml.compilation.ISymbol#hasFacet(java.lang.String)
	 */
	@Override
	public boolean hasFacet(final String key) {
		return false;
	}

	/**
	 * Method setChildren()
	 * Nothing to do as this aggregate has no description in GAML.
	 * @see msi.gaml.compilation.ISymbol#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final List<? extends ISymbol> children) {}

	/**
	 * Method getName()
	 * Returns the name of the aggregate
	 * @see msi.gama.common.interfaces.INamed#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Method setName()
	 * Sets a new name for this aggregate
	 * @see msi.gama.common.interfaces.INamed#setName(java.lang.String)
	 */
	@Override
	public void setName(final String newName) {
		name = newName;
	}

	/**
	 * Method serialize()
	 * Returns an empty as this aggregate has no description in GAML.
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "";
	}

	/**
	 * Method init()
	 * If one of the outputs cannot be initialized, returns false
	 * @see msi.gama.common.interfaces.IStepable#init(msi.gama.runtime.IScope)
	 */
	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		for ( IDisplayOutput output : outputs ) {
			if ( !output.init(scope) ) { return false; }
		}
		return true;
	}

	/**
	 * Method step()
	 * If one of the outputs cannot be stepped, returns false
	 * @see msi.gama.common.interfaces.IStepable#step(msi.gama.runtime.IScope)
	 */
	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		for ( IDisplayOutput output : outputs ) {
			if ( !output.step(scope) ) { return false; }
		}
		return true;
	}

	/**
	 * Method isSynchronized()
	 * @see msi.gama.outputs.IDisplayOutput#isSynchronized()
	 */
	@Override
	public boolean isSynchronized() {
		for ( IDisplayOutput output : outputs ) {
			if ( !output.isSynchronized() ) { return false; }
		}
		return true;
	}

	/**
	 * Method setSynchronized()
	 * @see msi.gama.outputs.IDisplayOutput#setSynchronized(boolean)
	 */
	@Override
	public void setSynchronized(final boolean sync) {
		for ( IDisplayOutput output : outputs ) {
			output.setSynchronized(sync);
		}
	}

	/**
	 * Method isUnique()
	 * An aggregate corresponds to a unique view, and hence returns
	 * @see msi.gama.outputs.IDisplayOutput#isUnique()
	 */
	@Override
	public boolean isUnique() {
		return true;
	}

	/**
	 * Method getViewId()
	 * @see msi.gama.outputs.IDisplayOutput#getViewId()
	 */
	@Override
	public String getViewId() {
		return viewId;
	}

	/**
	 * Method setPaused()
	 * @see msi.gama.outputs.IOutput#setPaused(boolean)
	 */
	@Override
	public void setPaused(final boolean paused) {
		for ( IDisplayOutput output : outputs ) {
			output.setPaused(paused);
		}
	}

	/**
	 * Method isPaused()
	 * @see msi.gama.outputs.IOutput#isPaused()
	 */
	@Override
	public boolean isPaused() {
		for ( IDisplayOutput output : outputs ) {
			if ( !output.isPaused() ) { return false; }
		}
		return true;
	}

	/**
	 * Method open()
	 * @see msi.gama.outputs.IOutput#open()
	 */
	@Override
	public void open() {}

	/**
	 * Method close()
	 * @see msi.gama.outputs.IOutput#close()
	 */
	@Override
	public void close() {}

	/**
	 * Method getRefreshRate()
	 * @see msi.gama.outputs.IOutput#getRefreshRate()
	 */
	@Override
	public int getRefreshRate() {
		return 0;
	}

	/**
	 * Method setRefreshRate()
	 * @see msi.gama.outputs.IOutput#setRefreshRate(int)
	 */
	@Override
	public void setRefreshRate(final int rate) {}

	/**
	 * Method update()
	 * @see msi.gama.outputs.IOutput#update()
	 */
	@Override
	public void update() throws GamaRuntimeException {}

	/**
	 * Method getScope()
	 * @see msi.gama.outputs.IOutput#getScope()
	 */
	@Override
	public IScope getScope() {
		return scope;
	}

	/**
	 * Method getOriginalName()
	 * @see msi.gama.outputs.IOutput#getOriginalName()
	 */
	@Override
	public String getOriginalName() {
		return getName();
	}

}
