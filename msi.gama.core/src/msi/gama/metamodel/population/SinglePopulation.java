package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

public class SinglePopulation extends AbstractPopulation {

	protected IList<IAgent> agents = new GamaList();

	public SinglePopulation(final IAgent host, final ISpecies species) {
		super(host, species);
	}

	@Override
	public void computeAgentsToSchedule(final IScope scope, final IList list)
		throws GamaRuntimeException {}

	@Override
	public void dispose() {
		IAgent[] ags = agents.toArray(new IAgent[0]);
		for ( int i = 0, n = ags.length; i < n; i++ ) {
			ags[i].dispose();
		}
		agents.clear();
		super.dispose();
	}

	/**
	 * @see msi.gama.metamodel.population.IPopulation#size()
	 */
	@Override
	public int size() {
		return agents.size();
	}

	/**
	 * @see msi.gama.util.IContainer#get(java.lang.Object)
	 */
	@Override
	public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return agents.get(scope, index);
	}

	/**
	 * @see msi.gama.util.IContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return agents.contains(scope, o);
	}

	/**
	 * @see msi.gama.util.IContainer#first()
	 */
	@Override
	public IAgent first(final IScope scope) throws GamaRuntimeException {
		return agents.first(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#last()
	 */
	@Override
	public IAgent last(final IScope scope) throws GamaRuntimeException {
		return agents.last(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#length()
	 */
	@Override
	public int length(final IScope scope) {
		return agents.length(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#max(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent max(final IScope scope) throws GamaRuntimeException {
		return agents.max(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#min(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent min(final IScope scope) throws GamaRuntimeException {
		return agents.min(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#product(msi.gama.runtime.IScope)
	 */
	@Override
	public Object product(final IScope scope) throws GamaRuntimeException {
		return agents.product(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#sum(msi.gama.runtime.IScope)
	 */
	@Override
	public Object sum(final IScope scope) throws GamaRuntimeException {
		return agents.sum(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#isEmpty()
	 */
	@Override
	public boolean isEmpty(final IScope scope) {
		return agents.isEmpty(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#reverse()
	 */
	@Override
	public IContainer<Integer, IAgent> reverse(final IScope scope) throws GamaRuntimeException {
		return agents.reverse(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#any()
	 */
	@Override
	public IAgent any(final IScope scope) {
		return agents.any(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return false;
	}

	/**
	 * @see msi.gama.util.IContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return agents.checkIndex(index);
	}

	/**
	 * @see msi.gama.util.IContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return agents.checkValue(value);
	}

	/**
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return agents.checkBounds(index, forAdding);
	}

	/**
	 * @see msi.gama.util.IContainer#addAll(msi.gama.util.IContainer, java.lang.Object)
	 */
	@Override
	public void addAll(IScope scope, final IContainer value, final Object param)
		throws GamaRuntimeException {
		super.addAll(scope, value, param);
		agents.addAll(scope, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#addAll(java.lang.Object, msi.gama.util.IContainer,
	 *      java.lang.Object)
	 */
	@Override
	public void addAll(IScope scope, final Integer index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		super.addAll(scope, index, value, param);
		agents.addAll(scope, index, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(IScope scope, final IAgent value, final Object param)
		throws GamaRuntimeException {
		super.add(scope, value, param);
		agents.add(scope, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#add(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(IScope scope, final Integer index, final IAgent value, final Object param)
		throws GamaRuntimeException {
		super.add(scope, index, value, param);
		agents.add(scope, index, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.util.IContainer)
	 */
	@Override
	public boolean removeAll(IScope scope, final IContainer<?, IAgent> value)
		throws GamaRuntimeException {
		super.removeAll(scope, value);
		return agents.removeAll(scope, value);
	}

	/**
	 * @see msi.gama.util.IContainer#removeAt(java.lang.Object)
	 */
	@Override
	public Object removeAt(IScope scope, final Integer index) throws GamaRuntimeException {
		super.removeAt(scope, index);
		return agents.removeAt(scope, index);
	}

	/**
	 * @see msi.gama.util.IContainer#putAll(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putAll(IScope scope, final IAgent value, final Object param)
		throws GamaRuntimeException {
		agents.putAll(scope, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#put(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(IScope scope, final Integer index, final IAgent value, final Object param)
		throws GamaRuntimeException {
		agents.put(scope, index, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#clear()
	 */
	@Override
	public void clear() throws GamaRuntimeException {
		super.clear();
		agents.clear();
	}

	/**
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IList listValue(final IScope scope) throws GamaRuntimeException {
		return agents.listValue(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return agents.matrixValue(scope);
	}

	/**
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope,
	 *      msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		return agents.matrixValue(scope, preferredSize);
	}

	/**
	 * @see msi.gama.util.IContainer#mapValue(msi.gama.runtime.IScope)
	 */
	@Override
	public Map mapValue(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#type()
	 */
	@Override
	public IType type() {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#copy()
	 */
	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return listValue(scope);
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlable#toGaml()
	 */
	@Override
	public String toGaml() {
		return null;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<IAgent> iterator() {
		return agents.iterator();
	}

	@Override
	public Iterable<IAgent> iterable(final IScope scope) {
		return agents;
	}

	@Override
	public boolean removeFirst(IScope scope, final IAgent a) {
		agents.remove(a);
		return super.removeFirst(scope, a);
	}

	@Override
	public GamaList<IAgent> getAgentsList() {
		return new GamaList(agents);
	}

	@Override
	public IAgent getFromIndicesList(final IScope scope, final IList indices)
		throws GamaRuntimeException {
		return (IAgent) agents.getFromIndicesList(scope, indices);
	}

}
