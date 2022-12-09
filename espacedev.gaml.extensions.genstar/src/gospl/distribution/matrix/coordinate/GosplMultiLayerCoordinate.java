/*******************************************************************************************************
 *
 * GosplMultiLayerCoordinate.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.distribution.matrix.coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

/**
 * Mulit-level coordinate to represent coordinate of attribute / value for several (probabilist) universe
 *
 * @author kevinchapuis
 *
 */
public class GosplMultiLayerCoordinate extends ACoordinate<Attribute<? extends IValue>, IValue> {

	/** The childs. */
	private final Set<GosplMultiLayerCoordinate> childs;

	/**
	 * Instantiates a new gospl multi layer coordinate.
	 *
	 * @param coordinate
	 *            the coordinate
	 */
	public GosplMultiLayerCoordinate(final ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		super(coordinate.getMap());
		childs = new HashSet<>();
	}

	/**
	 * Instantiates a new gospl multi layer coordinate.
	 *
	 * @param self
	 *            the self
	 */
	public GosplMultiLayerCoordinate(final Map<Attribute<? extends IValue>, IValue> self) {
		super(self);
		childs = new HashSet<>();
	}

	/**
	 * Whether this multi layer coordinate have child or not
	 *
	 * @return
	 */
	public boolean hasChild() {
		return childs == null || childs.isEmpty();
	}

	/**
	 * Return the set of all child coordinate
	 *
	 * @return
	 */
	public Set<GosplMultiLayerCoordinate> getChilds() { return Collections.unmodifiableSet(childs); }

	/**
	 * Add new child coordinate
	 *
	 * @param coordinate
	 */
	public void addChild(final GosplMultiLayerCoordinate coordinate) {
		this.childs.add(coordinate);
	}

	@Override
	protected boolean isCoordinateSetComplient(final Map<Attribute<? extends IValue>, IValue> coordinateSet) {
		return coordinateSet.entrySet().stream()
				.allMatch(e -> e.getValue().getValueSpace().getAttribute().equals(e.getKey()));
	}

}
