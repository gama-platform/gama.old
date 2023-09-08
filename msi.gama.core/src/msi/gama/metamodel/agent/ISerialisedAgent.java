/*******************************************************************************************************
 *
 * ISerialisedAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

/**
 * The Interface ISerialisedAgent.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public interface ISerialisedAgent {

	/**
	 * Gets the index.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the index
	 * @date 8 août 2023
	 */
	int getIndex();

	/**
	 * Gets the attribute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param var
	 *            the var
	 * @return the attribute
	 * @date 8 août 2023
	 */
	Object getAttributeValue(String var);

	/**
	 * Sets the attribute value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param var
	 *            the var
	 * @param val
	 *            the val
	 * @date 8 août 2023
	 */
	void setAttributeValue(String var, Object val);

}
