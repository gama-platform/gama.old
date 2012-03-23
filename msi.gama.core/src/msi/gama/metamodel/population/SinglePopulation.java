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

	IList<IAgent> agents = new GamaList();
	
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
	public IAgent get(final Integer index) throws GamaRuntimeException {
		return agents.get(index);
	}

	/**
	 * @see msi.gama.util.IContainer#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) throws GamaRuntimeException {
		return agents.contains(o);
	}

	/**
	 * @see msi.gama.util.IContainer#first()
	 */
	@Override
	public IAgent first() throws GamaRuntimeException {
		return agents.first();
	}

	/**
	 * @see msi.gama.util.IContainer#last()
	 */
	@Override
	public IAgent last() throws GamaRuntimeException {
		return agents.last();
	}

	/**
	 * @see msi.gama.util.IContainer#length()
	 */
	@Override
	public int length() {
		return agents.length();
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
	public boolean isEmpty() {
		return agents.isEmpty();
	}

	/**
	 * @see msi.gama.util.IContainer#reverse()
	 */
	@Override
	public IContainer<Integer, IAgent> reverse() throws GamaRuntimeException {
		return agents.reverse();
	}

	/**
	 * @see msi.gama.util.IContainer#any()
	 */
	@Override
	public IAgent any() {
		return agents.any();
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
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		super.addAll(value, param);
		agents.addAll(value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#addAll(java.lang.Object, msi.gama.util.IContainer,
	 *      java.lang.Object)
	 */
	@Override
	public void addAll(final Integer index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		super.addAll(index, value, param);
		agents.addAll(index, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(final IAgent value, final Object param) throws GamaRuntimeException {
		super.add(value, param);
		agents.add(value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#add(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void add(final Integer index, final IAgent value, final Object param)
		throws GamaRuntimeException {
		super.add(index, value, param);
		agents.add(index, value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#removeAll(msi.gama.util.IContainer)
	 */
	@Override
	public boolean removeAll(final IContainer<?, IAgent> value) throws GamaRuntimeException {
		super.removeAll(value);
		return agents.removeAll(value);
	}

	/**
	 * @see msi.gama.util.IContainer#removeAt(java.lang.Object)
	 */
	@Override
	public Object removeAt(final Integer index) throws GamaRuntimeException {
		super.removeAt(index);
		return agents.removeAt(index);
	}

	/**
	 * @see msi.gama.util.IContainer#putAll(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void putAll(final IAgent value, final Object param) throws GamaRuntimeException {
		agents.putAll(value, param);
	}

	/**
	 * @see msi.gama.util.IContainer#put(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(final Integer index, final IAgent value, final Object param)
		throws GamaRuntimeException {
		agents.put(index, value, param);
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
	public String stringValue() throws GamaRuntimeException {
		return null;
	}

	/**
	 * @see msi.gama.common.interfaces.IValue#copy()
	 */
	@Override
	public IValue copy() throws GamaRuntimeException {
		return null;
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
	public boolean removeFirst(final IAgent a) {
		agents.remove(a);
		return super.removeFirst(a);
	}

	@Override
	public GamaList<IAgent> getAgentsList() {
		return new GamaList(agents);
	}

}
