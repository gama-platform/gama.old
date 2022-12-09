/*******************************************************************************************************
 *
 * IDComparator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.entity.comparator;

import java.util.Comparator;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * The Class IDComparator.
 */
public class IDComparator implements Comparator<IEntity<? extends IAttribute<? extends IValue>>> {

	/** The Constant SELF. */
	public static final String SELF = "DEFAULT COMPARATOR";

	/** The instance. */
	private static IDComparator INSTANCE = new IDComparator();

	/**
	 * Instantiates a new ID comparator.
	 */
	private IDComparator() {}

	/**
	 * Gets the single instance of IDComparator.
	 *
	 * @return single instance of IDComparator
	 */
	protected static IDComparator getInstance() { return INSTANCE; }

	@Override
	public int compare(final IEntity<? extends IAttribute<? extends IValue>> o1,
			final IEntity<? extends IAttribute<? extends IValue>> o2) {
		if (o1.hasChildren() && !o2.hasChildren()) return 1;
		if (!o1.hasChildren() && o2.hasChildren()) return -1;
		if (o1.hasChildren() && o2.hasChildren()) {
			if (o1.getChildren().size() > o2.getChildren().size()) return 1;
			if (o1.getChildren().size() < o2.getChildren().size()) return -1;
		}
		return o1.getEntityId().compareTo(o2.getEntityId());
	}

}
