/*********************************************************************************************
 *
 *
 * 'TypesManager.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.util.Collection;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TypesManager extends IDescription.DescriptionVisitor<SpeciesDescription> implements ITypesManager {

	public static int CURRENT_INDEX = IType.SPECIES_TYPES;

	private TypesManager parent;

	private final THashMap<String, IType> types = new THashMap(10, 0.95f);

	public TypesManager(final TypesManager parent) {
		setParent(parent);
	}

	@Override
	public void setParent(final ITypesManager parent) {
		this.parent = (TypesManager) parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#alias(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void alias(final String existingTypeName, final String otherTypeName) {
		final IType t = types.get(existingTypeName);
		if (t != null) {
			types.put(otherTypeName, t);
		}
	}

	@Override
	public boolean visit(final SpeciesDescription species) {
		addSpeciesType(species);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#addSpeciesType(msi.gaml.descriptions.
	 * TypeDescription)
	 */
	@Override
	public IType addSpeciesType(final SpeciesDescription species) {
		final String name = species.getName();
		if (!name.equals(IKeyword.AGENT)) {
			if (get(name) != Types.NO_TYPE) {
				species.error("Species " + name + " already declared. Species name must be unique",
						IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(null), name);
			}
			return addType(new GamaAgentType(species, species.getName(), ++CURRENT_INDEX, species.getJavaBase()),
					species.getJavaBase());
		}
		return get(IKeyword.AGENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#initType(java.lang.String,
	 * msi.gaml.types.IType, int, int, java.lang.Class)
	 */
	@Override
	public IType initType(final String keyword, IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		if (keyword.equals(IKeyword.UNKNOWN)) {
			typeInstance = Types.NO_TYPE;
		}
		typeInstance.init(varKind, id, keyword, wraps);
		return addType(typeInstance, wraps);
	}

	private IType addType(final IType t, final Class... wraps) {
		final int i = t.id();
		final String name = t.toString();
		types.put(name, t);
		// Hack to allow types to be declared with their id as string
		types.put(String.valueOf(i), t);
		for (final Class cc : wraps) {
			Types.CLASSES_TYPES_CORRESPONDANCE.put(cc, name);
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#init()
	 */
	@Override
	public void init(final ModelDescription model) {
		// We first add the species as types
		model.visitAllSpecies(this);
		// Then we parent the types
		model.visitAllSpecies(new DescriptionVisitor<SpeciesDescription>() {

			@Override
			public boolean visit(final SpeciesDescription entry) {
				final IType type = get(entry.getName());
				if (!type.isParented() && !type.getName().equals(IKeyword.AGENT)) {
					final TypeDescription parent = entry.getParent();
					// Takes care of invalid species (see Issue 711)
					type.setParent(parent == null || parent == entry ? get(IKeyword.AGENT) : get(parent.getName()));
				}
				return true;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#containsType(java.lang.String)
	 */
	@Override
	public boolean containsType(final String s) {
		final IType t = types.get(s);
		if (t != null) {
			return true;
		}
		if (parent == null) {
			return false;
		}
		return parent.containsType(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#getTypeNames()
	 */
	// @Override
	// public List<String> getTypeNames() {
	// final List<String> result = parent == null ? new ArrayList() :
	// parent.getTypeNames();
	// for (final String s : types.keySet()) {
	// if (!Strings.isGamaNumber(s)) {
	// result.add(s);
	// }
	// }
	// return result;
	// }

	Collection<IType> getTypes() {
		return types.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#get(java.lang.String)
	 */
	@Override
	public IType get(final String type) {
		final IType t = types.get(type);
		if (t != null)
			return t;
		if (parent == null)
			return Types.NO_TYPE;
		return parent.get(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.types.ITypesManager#dispose()
	 */
	@Override
	public void dispose() {
		types.clear();
	}

}
