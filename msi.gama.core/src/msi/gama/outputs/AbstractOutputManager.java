/*******************************************************************************************************
 *
 * AbstractOutputManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

/**
 * Class AbstractOutputManager.
 *
 * @author drogoul
 * @since 9 juin 2013
 *
 */
public abstract class AbstractOutputManager extends Symbol implements IOutputManager {

	/** The autosave. */
	final IExpression autosave;

	/**
	 * Properties
	 */

	/** The layout. */
	LayoutStatement layout;

	/** The outputs. */
	protected final IMap<String, IOutput> outputs = GamaMapFactory.create();

	/** The virtual outputs. */
	protected final IMap<String, IOutput> virtualOutputs = GamaMapFactory.create();

	/** The display index. */
	protected int displayIndex;

	/**
	 * Instantiates a new abstract output manager.
	 *
	 * @param desc
	 *            the desc
	 */
	public AbstractOutputManager(final IDescription desc) {
		super(desc);
		autosave = desc.getFacetExpr(IKeyword.AUTOSAVE);
	}

	@Override
	public Map<String, ? extends IOutput> getOutputs() { return outputs; }

	@Override
	public Iterator<IOutput> iterator() {
		return Iterators.unmodifiableIterator(outputs.values().iterator());
	}

	@Override
	public IOutput getOutputWithId(final String id) {
		return outputs.get(id);
	}

	@Override
	public void putAll(final Map<String, IOutput> mm) {
		outputs.putAll(mm);
	}

	@Override
	public IOutput getOutputWithOriginalName(final String s) {
		return Iterables.find(this, each -> each.getOriginalName().equals(s), null);
	}

	@Override
	public void add(final IOutput output) {
		if (output instanceof IDisplayOutput && ((IDisplayOutput) output).isVirtual()) {
			virtualOutputs.put(output.getId(), output);
		} else {
			outputs.put(output.getId(), output);
		}
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
		try {
			// AD: explicit addition of an ArrayList to prevent dispose errors
			// (when outputs remove themselves from the list)
			for (final IOutput output : new ArrayList<>(outputs.values())) { output.dispose(); }
			clear();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	// hqnghi
	// for instant, multi-simulation cannot have their owns outputs display at
	// same time.
	public void clear() {
		outputs.clear();
	}

	@Override
	public void remove(final IOutput o) {
		if (!(o instanceof AbstractOutput)) return;
		if (((AbstractOutput) o).isUserCreated()) {
			o.dispose();
			outputs.values().remove(o);
		} else {
			o.setPaused(true);
		}
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		for (final ISymbol s : commands) {
			if (s instanceof LayoutStatement) {
				layout = (LayoutStatement) s;
			} else if (s instanceof IOutput o) {
				add(o);
				o.setUserCreated(false);
				if (o instanceof LayeredDisplayOutput) { ((LayeredDisplayOutput) o).setIndex(displayIndex++); }
			}
		}
	}

	@Override
	public void forceUpdateOutputs() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.update(); }
	}

	@Override
	public void pause() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setPaused(true); }
	}

	@Override
	public void resume() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setPaused(false); }
	}

	@Override
	public void synchronize() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setSynchronized(true); }
	}

	@Override
	public void unSynchronize() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.setSynchronized(false); }
	}

	@Override
	public void close() {
		for (final IDisplayOutput o : getDisplayOutputs()) { o.close(); }
	}

	@Override
	public Iterable<IDisplayOutput> getDisplayOutputs() {
		return Iterables.filter(outputs.values(), IDisplayOutput.class);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean init(final IScope scope) {
		name = scope.getRoot().getName();
		boolean atLeastOneOutputAutosaving = false;
		for (final IOutput output : ImmutableList.copyOf(this)) {
			if (!open(scope, output)) return false;
			if (output instanceof IDisplayOutput && ((IDisplayOutput) output).isAutoSave()) {
				atLeastOneOutputAutosaving = true;
			}
		}
		atLeastOneOutputAutosaving |= evaluateAutoSave(scope);
		if (atLeastOneOutputAutosaving) { synchronize(); }

		return true;
	}

	/**
	 * Evaluate auto save.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private boolean evaluateAutoSave(final IScope scope) throws GamaRuntimeException {
		boolean isAutosaving = false;
		if (autosave != null) {
			String path = null;
			if (autosave.getGamlType().equals(Types.STRING)) {
				path = Cast.asString(scope, autosave.value(scope));
				isAutosaving = path != null && !path.isBlank();
			} else {
				isAutosaving = Cast.asBool(scope, autosave.value(scope));
			}
			if (isAutosaving) { SnapshotMaker.getInstance().doSnapshot(scope, path); }
		}
		return isAutosaving;
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout
	 *            the new layout
	 */
	public void setLayout(final LayoutStatement layout) { this.layout = layout; }

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public LayoutStatement getLayout() { return layout; }

	@Override
	public boolean open(final IScope scope, final IOutput output) {

		if (scope.init(output).passed()) {
			output.setPaused(false);
			if (initialStep(scope, output)) {
				try {
					output.open();
					output.update();
				} catch (final RuntimeException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * Initial step.
	 *
	 * @param scope
	 *            the scope
	 * @param output
	 *            the output
	 * @return true, if successful
	 */
	protected boolean initialStep(final IScope scope, final IOutput output) {
		return scope.step(output).passed();
	}

	@Override
	public boolean step(final IScope scope) {
		outputs.forEach((name, each) -> {
			if (each.isRefreshable() && each.getScope().step(each).passed()) { each.update(); }
		});
		if (autosave != null) {
			boolean isAutosaving = false;
			String autosavingPath = null;
			if (autosave.getGamlType().equals(Types.STRING)) {
				isAutosaving = true;
				autosavingPath = Cast.asString(scope, autosave.value(scope));
			} else {
				isAutosaving = Cast.asBool(scope, autosave.value(scope));
			}
			if (isAutosaving) { SnapshotMaker.getInstance().doSnapshot(scope, autosavingPath); }
		}

		return true;
	}

}