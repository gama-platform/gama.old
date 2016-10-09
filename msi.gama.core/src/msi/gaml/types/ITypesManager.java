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