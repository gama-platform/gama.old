package msi.gaml.types;

import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;

public interface ITypesManager {

	void alias(String existingTypeName, String otherTypeName);

	IType initType(String keyword, IType typeInstance, int id, int varKind, Class... wraps);

	// TypeDescription getSpecies(String name);

	boolean containsType(String s);

	// List<String> getTypeNames();

	// Collection<TypeDescription> getAllSpecies();

	IType get(String type);

	void dispose();

	IType addSpeciesType(SpeciesDescription species);

	void init(ModelDescription model);

}