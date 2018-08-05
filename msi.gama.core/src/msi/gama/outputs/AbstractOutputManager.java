/*********************************************************************************************
 *
 * 'AbstractOutputManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import msi.gama.runtime.IScope;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;

/**
 * Class AbstractOutputManager.
 *
 * @author drogoul
 * @since 9 juin 2013
 *
 */
public abstract class AbstractOutputManager extends Symbol implements IOutputManager {

	LayoutStatement layout;
	protected final TOrderedHashMap<String, IOutput> outputs = new TOrderedHashMap<>();
	protected final Map<String, IOutput> virtualOutputs = new TOrderedHashMap<>();

	protected int displayIndex;

	public AbstractOutputManager(final IDescription desc) {
		super(desc);
	}

	@Override
	public Map<String, ? extends IOutput> getOutputs() {
		return outputs;
	}

	@Override
	public Iterator<IOutput> iterator() {
		return Iterators.unmodifiableIterator(outputs.values().iterator());
	}

	@Override
	public IOutput get(final String id) {
		return outputs.get(id);
	}

	@Override
	public IOutput getOutputWithName(final String name) {
		return Iterables.find(this, each -> each.getName().equals(name), null);
	}

	@Override
	public void putAll(final Map<String, IOutput> mm) {
		outputs.putAll(mm);
	}

	@Override
	public IOutput getOutputWithOriginalName(final String name) {
		return Iterables.find(this, each -> each.getOriginalName().equals(name), null);
	}

	@Override
	public void add(final IOutput output) {
		if (output instanceof IDisplayOutput && ((IDisplayOutput) output).isVirtual()) {
			virtualOutputs.put(output.getId(), output);
		} else {
			outputs.put(output.getId(), output);
		}
	}

	// hqnghi add output with alias name from micro-model
	@Override
	public void put(final String name, final IOutput output) {
		outputs.put(name, output);
	}

	// end-hqnghi
	@Override
	public synchronized void dispose() {
		super.dispose();
		try {
			// AD: explicit addition of an ArrayList to prevent dispose errors
			// (when outputs remove themselves from the list)
			for (final IOutput output : new ArrayList<>(outputs.values())) {
				output.dispose();
			}
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
		if (!(o instanceof AbstractOutput)) { return; }
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
			} else if (s instanceof IOutput) {
				final IOutput o = (IOutput) s;
				add(o);
				o.setUserCreated(false);
				if (o instanceof LayeredDisplayOutput) {
					((LayeredDisplayOutput) o).setIndex(displayIndex++);
				}
			}
		}
	}

	@Override
	public void forceUpdateOutputs() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.update();
		}
	}

	@Override
	public void pause() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.setPaused(true);
		}
	}

	@Override
	public void resume() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.setPaused(false);
		}
	}

	@Override
	public void synchronize() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.setSynchronized(true);
		}
	}

	@Override
	public void unSynchronize() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.setSynchronized(false);
		}
	}

	@Override
	public void close() {
		for (final IDisplayOutput o : getDisplayOutputs()) {
			o.close();
		}
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
		for (final IOutput output : ImmutableList.copyOf(this)) {
			if (!open(scope, output)) { return false; }
		}

		return true;
	}

	public void setLayout(final LayoutStatement layout) {
		this.layout = layout;
	}

	public LayoutStatement getLayout() {
		return layout;
	}

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

	protected boolean initialStep(final IScope scope, final IOutput output) {
		return scope.step(output).passed();
	}

	@Override
	public boolean step(final IScope scope) {
		final List<IOutput> copy = new ArrayList<>(outputs.values());
		final Iterable<IOutput> filtered =
				Iterables.filter(copy, each -> each.isRefreshable() && each.getScope().step(each).passed());
		for (final IOutput o : filtered) {
			o.update();
		}
		return true;
	}

}