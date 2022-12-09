/*******************************************************************************************************
 *
 * GosplSubPopulation.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * A subpopulation is a view of an actual multitypePopulation. Any change to this subpopulation is reflected to the
 * original MultitypePopulation (and reciprocally).
 *
 * @author Samuel Thiriot
 */
public final class GosplSubPopulation<E extends ADemoEntity> implements IPopulation<E, Attribute<? extends IValue>> {

	/** The multi pop. */
	protected final GosplMultitypePopulation<E> multiPop;

	/** The type. */
	protected final String type;

	/** The set. */
	protected final Set<E> set;

	/**
	 * Instantiates a new gospl sub population.
	 *
	 * @param p
	 *            the p
	 * @param type
	 *            the type
	 */
	protected GosplSubPopulation(final GosplMultitypePopulation<E> p, final String type) {
		this.multiPop = p;
		this.type = type;
		this.set = multiPop.getSetForType(type);
	}

	@Override
	public Set<Attribute<? extends IValue>> getPopulationAttributes() {
		return this.multiPop.getAttributesForType(this.type);
	}

	@Override
	public boolean add(final E e) {
		if (e.hasEntityType() && !type.equals(e.getEntityType())) throw GamaRuntimeException
				.error("cannot add this agent of type " + e.getEntityType() + " to a subpopulation of " + type, null);
		return multiPop.add(e);
	}

	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return multiPop.addAll(type, c);
	}

	@Override
	public void clear() {
		multiPop.clear(type);
	}

	@Override
	public boolean contains(final Object o) {
		return set.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public boolean isEmpty() { return set.isEmpty(); }

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public boolean remove(final Object o) {
		if (!set.contains(o)) return false;
		multiPop.remove(o);
		return true;
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		boolean anyChange = false;
		for (Object e : c) { anyChange = remove(e) || anyChange; }
		return anyChange;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		boolean anyChange = set.retainAll(c);
		if (anyChange) { multiPop.recomputeSize(); }
		return anyChange;
	}

	@Override
	public GosplSubPopulation<E> clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return set.toArray(a);
	}

	@Override
	public boolean isAllPopulationOfType(final String thisType) {
		return Objects.equals(type, thisType);
	}

	@Override
	public Attribute<? extends IValue> getPopulationAttributeNamed(final String name) {
		Set<Attribute<? extends IValue>> attributes = getPopulationAttributes();
		if (attributes == null) return null;
		for (Attribute<? extends IValue> a : attributes) { if (a.getAttributeName().equals(name)) return a; }
		return null;
	}

}
