/*******************************************************************************************************
 *
 * GamaPairReducer.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaPair;
import msi.gama.util.IReference;
import msi.gaml.types.IType;
import ummisco.gama.serializer.gamaType.reference.ReferencePair;

/**
 * The Class GamaPairReducer.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaPairReducer {
	
	/** The key pair type. */
	private final IType keyPairType;
	
	/** The data pair type. */
	private final IType dataPairType;
	
	/** The key. */
	private Object key;
	
	/** The value. */
	private Object value;

	/**
	 * Instantiates a new gama pair reducer.
	 *
	 * @param m the m
	 */
	public GamaPairReducer(final GamaPair m) {
		keyPairType = m.getGamlType().getKeyType();
		dataPairType = m.getGamlType().getContentType();
		key = m.getKey();
		value = m.getValue();
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the key type.
	 *
	 * @return the key type
	 */
	public IType getKeyType() {
		return keyPairType;
	}

	/**
	 * Gets the value type.
	 *
	 * @return the value type
	 */
	public IType getValueType() {
		return dataPairType;
	}

	/**
	 * Sets the key.
	 *
	 * @param k the new key
	 */
	public void setKey(final Object k) {
		key = k;
	}

	/**
	 * Sets the value.
	 *
	 * @param v the new value
	 */
	public void setValue(final Object v) {
		value = v;
	}

	/**
	 * Construct object.
	 *
	 * @return the gama pair
	 */
	public GamaPair constructObject() {
		final boolean isReference = IReference.isReference(key) || IReference.isReference(value);

		return isReference ? new ReferencePair(this) : new GamaPair(key, value, keyPairType, dataPairType);

		// return new GamaPair(key, value, keyPairType, dataPairType);
	}

	/**
	 * Unreference reducer.
	 *
	 * @param sim the sim
	 */
	public void unreferenceReducer(final SimulationAgent sim) {
		key = IReference.getObjectWithoutReference(key, sim);
		value = IReference.getObjectWithoutReference(value, sim);
	}
}
