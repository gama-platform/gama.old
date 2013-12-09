/**
 * Created by drogoul, 8 déc. 2013
 * 
 */
package msi.gama.metamodel.population;

import java.util.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.species.ISpecies;
import com.google.common.collect.Iterables;

/**
 * Class MetaPopulation. A list of IPopulation, ISpecies or MetaPopulation that behaves like a list of agents (also
 * to filter them).
 * 
 * @author drogoul
 * @since 8 déc. 2013
 * 
 */
public class MetaPopulation implements IList<IAgent>, IAgentFilter, IPopulationSet {

	protected final List<IPopulationSet> populationSets;
	// We cache the value in case.
	protected Map<String, IPopulation> setOfPopulations;

	public MetaPopulation(final IPopulationSet ... pop) {
		populationSets = Arrays.asList(pop);
	}

	/**
	 * Method getAgents()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getAgents()
	 */
	@Override
	public IContainer<?, ? extends IShape> getAgents() {
		return new GamaList(Iterables.concat(populationSets));
	}

	/**
	 * Method accept()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.runtime.IScope,
	 *      msi.gama.metamodel.shape.IShape, msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		final IAgent agent = a.getAgent();
		if ( agent == source.getAgent() ) { return false; }
		return contains(scope, agent);
	}

	/**
	 * Method filter()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.runtime.IScope,
	 *      msi.gama.metamodel.shape.IShape, java.util.Collection)
	 */
	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		IAgent sourceAgent = source == null ? null : source.getAgent();
		results.remove(sourceAgent);
		Iterator<? extends IShape> it = results.iterator();
		while (it.hasNext()) {
			if ( !contains(scope, it.next().getAgent()) ) {
				it.remove();
			}

		}

	}

	/**
	 * Method stringValue()
	 * @see msi.gama.common.interfaces.IValue#stringValue(msi.gama.runtime.IScope)
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return toGaml();
	}

	/**
	 * Method copy()
	 * @see msi.gama.common.interfaces.IValue#copy(msi.gama.runtime.IScope)
	 */
	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new MetaPopulation(populationSets.toArray(new IPopulationSet[populationSets.size()]));
	}

	/**
	 * Method toGaml()
	 * @see msi.gama.common.interfaces.IGamlable#toGaml()
	 */
	@Override
	public String toGaml() {
		final StringBuilder sb = new StringBuilder(populationSets.size() * 10);
		sb.append('[');
		for ( int i = 0; i < populationSets.size(); i++ ) {
			if ( i != 0 ) {
				sb.append(',');
			}
			sb.append(StringUtils.toGaml(populationSets.get(i)));
		}
		sb.append(']');
		return sb.toString();

	}

	/**
	 * Method get()
	 * @see msi.gama.util.IContainer#get(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return listValue(scope).get(scope, index);
	}

	/**
	 * Method getFromIndicesList()
	 * @see msi.gama.util.IContainer#getFromIndicesList(msi.gama.runtime.IScope, msi.gama.util.IList)
	 */
	@Override
	public Object getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return listValue(scope).getFromIndicesList(scope, indices);
	}

	/**
	 * Method contains()
	 * @see msi.gama.util.IContainer#contains(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		if ( !(o instanceof IAgent) ) { return false; }
		for ( IPopulationSet pop : populationSets ) {
			if ( pop.contains(scope, o) ) { return true; }
		}
		return false;
	}

	/**
	 * Method first()
	 * @see msi.gama.util.IContainer#first(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent first(final IScope scope) throws GamaRuntimeException {
		if ( populationSets.size() == 0 ) { return null; }
		return populationSets.get(0).first(scope);
	}

	/**
	 * Method last()
	 * @see msi.gama.util.IContainer#last(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent last(final IScope scope) throws GamaRuntimeException {
		if ( populationSets.size() == 0 ) { return null; }
		return populationSets.get(populationSets.size() - 1).last(scope);
	}

	/**
	 * Method length()
	 * @see msi.gama.util.IContainer#length(msi.gama.runtime.IScope)
	 */
	@Override
	public int length(final IScope scope) {
		int result = 0;
		for ( IPopulationSet p : populationSets ) {
			result += p.length(scope);
		}
		return result;
	}

	/**
	 * Method isEmpty()
	 * @see msi.gama.util.IContainer#isEmpty(msi.gama.runtime.IScope)
	 */
	@Override
	public boolean isEmpty(final IScope scope) {
		for ( IPopulationSet p : populationSets ) {
			if ( !p.isEmpty(scope) ) { return false; }
		}
		return true;
	}

	/**
	 * Method reverse()
	 * @see msi.gama.util.IContainer#reverse(msi.gama.runtime.IScope)
	 */
	@Override
	public IContainer<Integer, IAgent> reverse(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).reverse(scope);
	}

	/**
	 * Method any()
	 * @see msi.gama.util.IContainer#any(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent any(final IScope scope) {
		if ( populationSets.size() == 0 ) { return null; }
		RandomUtils r = scope.getSimulationScope().getExperiment().getRandomGenerator();
		final int i = r.between(0, populationSets.size() - 1);
		return populationSets.get(i).any(scope);
	}

	/**
	 * Method checkBounds()
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return false;
	}

	/**
	 * Method add()
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object, java.lang.Object,
	 *      boolean, boolean)
	 */
	@Override
	public void add(final IScope scope, final Integer index, final Object value, final Object parameter,
		final boolean all, final boolean add) {
		// Not allowed
	}

	/**
	 * Method remove()
	 * @see msi.gama.util.IContainer#remove(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object, boolean)
	 */
	@Override
	public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
		// Not allowed
	}

	/**
	 * Method listValue()
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IList<IAgent> listValue(final IScope scope) throws GamaRuntimeException {
		return new GamaList(iterable(scope));
	}

	/**
	 * Method matrixValue()
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).matrixValue(scope);
	}

	/**
	 * Method matrixValue()
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope, msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
		return listValue(scope).matrixValue(scope, preferredSize);
	}

	/**
	 * Method mapValue()
	 * @see msi.gama.util.IContainer#mapValue(msi.gama.runtime.IScope)
	 */
	@Override
	public GamaMap mapValue(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).mapValue(scope);
	}

	/**
	 * Method iterable()
	 * @see msi.gama.util.IContainer#iterable(msi.gama.runtime.IScope)
	 */
	@Override
	public Iterable<? extends IAgent> iterable(final IScope scope) {
		List<Iterable<? extends IAgent>> result = new ArrayList();
		for ( IPopulationSet p : populationSets ) {
			result.add(p.iterable(scope));
		}
		return Iterables.concat(result);
	}

	/**
	 * Method getSpecies()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getSpecies()
	 */
	@Override
	public ISpecies getSpecies() {
		return null; // We dont know what to return here.
	}

	/**
	 * Method size()
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {
		return length(null);
	}

	/**
	 * Method isEmpty()
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return isEmpty(null);
	}

	/**
	 * Method contains()
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		return contains(null, o);
	}

	/**
	 * Method iterator()
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<IAgent> iterator() {
		return listValue(null).iterator();
	}

	/**
	 * Method toArray()
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray() {
		return listValue(null).toArray();
	}

	/**
	 * Method toArray()
	 * @see java.util.List#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return listValue(null).toArray(a);
	}

	/**
	 * Method add()
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add(final IAgent e) {
		return false; // Not allowed
	}

	/**
	 * Method remove()
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		return false; // Not allowed
	}

	/**
	 * Method containsAll()
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return listValue(null).containsAll(c);
	}

	/**
	 * Method addAll()
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends IAgent> c) {
		return false; // Not allowed
	}

	/**
	 * Method addAll()
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(final int index, final Collection<? extends IAgent> c) {
		return false; // Not allowed
	}

	/**
	 * Method removeAll()
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		return false; // Not allowed
	}

	/**
	 * Method retainAll()
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		return false; // Not allowed
	}

	/**
	 * Method clear()
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear() {
		// Not allowed
	}

	/**
	 * Method get()
	 * @see java.util.List#get(int)
	 */
	@Override
	public IAgent get(final int index) {
		return listValue(null).get(index);
	}

	/**
	 * Method set()
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public IAgent set(final int index, final IAgent element) {
		return null; // not allowed
	}

	/**
	 * Method add()
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add(final int index, final IAgent element) {
		// not allowed
	}

	/**
	 * Method remove()
	 * @see java.util.List#remove(int)
	 */
	@Override
	public IAgent remove(final int index) {
		return null; // not allowed
	}

	/**
	 * Method indexOf()
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(final Object o) {
		return listValue(null).indexOf(o);
	}

	/**
	 * Method lastIndexOf()
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf(final Object o) {
		return listValue(null).lastIndexOf(o);
	}

	/**
	 * Method listIterator()
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<IAgent> listIterator() {
		return listValue(null).listIterator();
	}

	/**
	 * Method listIterator()
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<IAgent> listIterator(final int index) {
		return listValue(null).listIterator(index);
	}

	/**
	 * Method subList()
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<IAgent> subList(final int fromIndex, final int toIndex) {
		return listValue(null).subList(fromIndex, toIndex);
	}

	private Map<String, IPopulation> getMapOfPopulations(final IScope scope) {
		if ( setOfPopulations == null ) {
			setOfPopulations = new LinkedHashMap();
			for ( IPopulationSet pop : populationSets ) {
				if ( pop instanceof MetaPopulation ) {
					setOfPopulations.putAll(((MetaPopulation) pop).getMapOfPopulations(scope));
				} else {
					Collection<? extends IPopulation> pops = pop.getPopulations(scope);
					for ( IPopulation p : pops ) {
						setOfPopulations.put(p.getName(), p);
					}
				}
			}
		}
		return setOfPopulations;
	}

	/**
	 * Method getPopulations()
	 * @see msi.gama.metamodel.population.IPopulationSet#getPopulations(msi.gama.runtime.IScope)
	 */
	@Override
	public Collection<? extends IPopulation> getPopulations(final IScope scope) {
		return getMapOfPopulations(scope).values();
	}

	/**
	 * Method getPopulation()
	 * @see msi.gama.metamodel.population.IPopulationSet#getPopulation(msi.gama.runtime.IScope, java.lang.String)
	 */
	// @Override
	// public IPopulation getPopulation(final IScope scope, final String name) {
	// return getMapOfPopulations(scope).get(name);
	// }

}
