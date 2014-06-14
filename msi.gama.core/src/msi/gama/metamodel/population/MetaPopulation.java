/*********************************************************************************************
 * 
 * 
 * 'MetaPopulation.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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
import msi.gaml.types.*;
import com.google.common.collect.Iterables;

/**
 * Class MetaPopulation. A list of IPopulation, ISpecies or MetaPopulation that behaves like a list of agents (also
 * to filter them).
 * 
 * @author drogoul
 * @since 8 déc. 2013
 * 
 */
public class MetaPopulation implements IContainer<Integer, IAgent>, IContainer.Addressable<Integer, IAgent>, IAgentFilter, IPopulationSet {

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
	public IContainer<?, ? extends IShape> getAgents(final IScope scope) {
		List<java.lang.Iterable<? extends IAgent>> result = new ArrayList();
		for ( IPopulationSet p : populationSets ) {
			result.add(p.iterable(scope));
		}
		return GamaList.from(Iterables.concat(result));
	}

	/**
	 * Method accept()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		final IAgent agent = a.getAgent();
		if ( agent == source.getAgent() ) { return false; }
		return contains(scope, agent);
	}

	/**
	 * Method filter()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, java.util.Collection)
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
		return listValue(scope, Types.NO_TYPE).get(scope, index);
	}

	/**
	 * Method getFromIndicesList()
	 * @see msi.gama.util.IContainer#getFromIndicesList(msi.gama.runtime.IScope, msi.gama.util.IList)
	 */
	@Override
	public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return listValue(scope, Types.NO_TYPE).getFromIndicesList(scope, indices);
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
	public IAgent firstValue(final IScope scope) throws GamaRuntimeException {
		if ( populationSets.size() == 0 ) { return null; }
		return populationSets.get(0).firstValue(scope);
	}

	/**
	 * Method last()
	 * @see msi.gama.util.IContainer#last(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent lastValue(final IScope scope) throws GamaRuntimeException {
		if ( populationSets.size() == 0 ) { return null; }
		return populationSets.get(populationSets.size() - 1).lastValue(scope);
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
		return listValue(scope, Types.NO_TYPE).reverse(scope);
	}

	/**
	 * Method any()
	 * @see msi.gama.util.IContainer#any(msi.gama.runtime.IScope)
	 */
	@Override
	public IAgent anyValue(final IScope scope) {
		if ( populationSets.size() == 0 ) { return null; }
		RandomUtils r = scope.getRandom();
		final int i = r.between(0, populationSets.size() - 1);
		return populationSets.get(i).anyValue(scope);
	}

	/**
	 * Method checkBounds()
	 * @see msi.gama.util.IContainer#checkBounds(java.lang.Object, boolean)
	 */
	// @Override
	// public boolean checkBounds(final Integer index, final boolean forAdding) {
	// return false;
	// }

	/**
	 * Method add()
	 * @see msi.gama.util.IContainer#add(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object, java.lang.Object, boolean, boolean)
	 */
	// @Override
	// public void add(final IScope scope, final Integer index, final Object value, final Object parameter,
	// final boolean all, final boolean add) {
	// // Not allowed
	// }

	/**
	 * Method remove()
	 * @see msi.gama.util.IContainer#remove(msi.gama.runtime.IScope, java.lang.Object, java.lang.Object, boolean)
	 */
	// @Override
	// public void remove(final IScope scope, final Object index, final Object value, final boolean all) {
	// // Not allowed
	// }

	/**
	 * Method listValue()
	 * @see msi.gama.util.IContainer#listValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IList<IAgent> listValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		// WARNING: Double copy of the list
		return GamaList.from(iterable(scope)).listValue(scope, contentsType);
	}

	/**
	 * Method matrixValue()
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType) throws GamaRuntimeException {
		return listValue(scope, contentsType).matrixValue(scope, contentsType);
	}

	/**
	 * Method matrixValue()
	 * @see msi.gama.util.IContainer#matrixValue(msi.gama.runtime.IScope, msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize)
		throws GamaRuntimeException {
		return listValue(scope, contentsType).matrixValue(scope, contentsType, preferredSize);
	}

	/**
	 * Method mapValue()
	 * @see msi.gama.util.IContainer#mapValue(msi.gama.runtime.IScope)
	 */
	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType)
		throws GamaRuntimeException {
		return listValue(scope, contentsType).mapValue(scope, keyType, contentsType);
	}

	/**
	 * Method iterable()
	 * @see msi.gama.util.IContainer#iterable(msi.gama.runtime.IScope)
	 */
	@Override
	public java.lang.Iterable<? extends IAgent> iterable(final IScope scope) {
		List<java.lang.Iterable<? extends IAgent>> result = new ArrayList();
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

	private Map<String, IPopulation> getMapOfPopulations(final IScope scope) {
		if ( setOfPopulations == null ) {
			setOfPopulations = new TOrderedHashMap();
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
	 * Method iterator()
	 * @see java.lang.Iterable#iterator()
	 */
	// @Override
	// public Iterator<IAgent> iterator() {
	// List<Iterator<? extends IAgent>> iterators = new ArrayList();
	// for ( IPopulationSet p : populationSets ) {
	// iterators.add(p.iterator());
	// }
	// return Iterators.concat(iterators.iterator());
	// }

}
