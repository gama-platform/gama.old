/*******************************************************************************************************
 *
 * GamaListReducer.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IReference;
import msi.gaml.types.IType;
import ummisco.gama.serializer.gamaType.reference.ReferenceList;

/**
 * The Class GamaListReducer.
 */
@SuppressWarnings ({ "rawtypes" })
public class GamaListReducer {

	/** The values list reducer. */
	private ArrayList<Object> valuesListReducer = new ArrayList<>();

	/** The content type list reducer. */
	private final IType contentTypeListReducer;

	/**
	 * Instantiates a new gama list reducer.
	 *
	 * @param l
	 *            the l
	 */
	public GamaListReducer(final IList l) {
		contentTypeListReducer = l.getGamlType().getContentType();
		valuesListReducer.addAll(l);
	}

	/**
	 * Construct object.
	 *
	 * @param scope
	 *            the scope
	 * @return the i list
	 */
	public IList constructObject(final IScope scope) {

		boolean isReference = false;
		int i = 0;
		while (!isReference && i < valuesListReducer.size()) {
			isReference = IReference.isReference(valuesListReducer.get(i));
			i++;
		}

		return isReference ? new ReferenceList(this)
				: GamaListFactory.create(scope, contentTypeListReducer, valuesListReducer);
	}

	/**
	 * Gets the values list reducer.
	 *
	 * @return the values list reducer
	 */
	public ArrayList<Object> getValuesListReducer() { return valuesListReducer; }

	/**
	 * Gets the content type list reducer.
	 *
	 * @return the content type list reducer
	 */
	public IType getContentTypeListReducer() { return contentTypeListReducer; }

	/**
	 * Unreference reducer.
	 *
	 * @param sim
	 *            the sim
	 */
	public void unreferenceReducer(final SimulationAgent sim) {
		final ArrayList<Object> listWithoutRef = new ArrayList<>();

		for (final Object elt : valuesListReducer) {
			listWithoutRef.add(IReference.getObjectWithoutReference(elt, sim));
		}

		valuesListReducer = listWithoutRef;
	}
}
