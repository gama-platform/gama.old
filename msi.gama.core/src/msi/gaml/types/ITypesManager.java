/*******************************************************************************************************
 *
 * ITypesManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Set;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;

/**
 * The Interface ITypesManager.
 */
public interface ITypesManager extends IDisposable {

	/**
	 * Alias.
	 *
	 * @param existingTypeName
	 *            the existing type name
	 * @param otherTypeName
	 *            the other type name
	 */
	void alias(String existingTypeName, String otherTypeName);

	/**
	 * Contains type.
	 *
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	boolean containsType(String s);

	/**
	 * Gets the.
	 *
	 * @param type
	 *            the type
	 * @return the i type
	 */
	IType<?> get(String type);

	/**
	 * Adds the species type.
	 *
	 * @param species
	 *            the species
	 * @return the i type<? extends I agent>
	 */
	IType<? extends IAgent> addSpeciesType(SpeciesDescription species);

	/**
	 * Inits the.
	 *
	 * @param model
	 *            the model
	 */
	void init(ModelDescription model);

	/**
	 * Sets the parent.
	 *
	 * @param typesManager
	 *            the new parent
	 */
	void setParent(ITypesManager typesManager);

	/**
	 * Inits the type.
	 *
	 * @param <Support>
	 *            the generic type
	 * @param keyword
	 *            the keyword
	 * @param typeInstance
	 *            the type instance
	 * @param id
	 *            the id
	 * @param varKind
	 *            the var kind
	 * @param support
	 *            the support
	 * @param pluginName
	 *            the plugin name
	 * @return the i type
	 */
	<Support> IType<Support> initType(String keyword, IType<Support> typeInstance, int id, int varKind,
			Class<Support> support, String pluginName);

	/**
	 * Gets the all types.
	 *
	 * @return the all types
	 */
	Set<IType<?>> getAllTypes();

	/**
	 * Decode type. Expects either a simple type name, or a parametric one with 1 or 2 type parameters
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param type
	 *            the type
	 * @return the i type
	 * @date 4 nov. 2023
	 */
	IType decodeType(String type);

}