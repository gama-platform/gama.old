/*********************************************************************************************
 *
 * 'ITypesManager.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.metamodel.agent.IAgent;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;

public interface ITypesManager {

	void alias(String existingTypeName, String otherTypeName);

	boolean containsType(String s);

	IType<?> get(String type);

	void dispose();

	IType<? extends IAgent> addSpeciesType(SpeciesDescription species);

	void init(ModelDescription model);

	void setParent(ITypesManager typesManager);

	<Support> IType<Support> initType(String keyword, IType<Support> typeInstance, int id, int varKind,
			Class<Support> support);

}