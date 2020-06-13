/*******************************************************************************************************
 *
 * msi.gaml.types.TypesManager.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class TypesManager implements IDescription.DescriptionVisitor<SpeciesDescription>, ITypesManager {

	public static int CURRENT_INDEX = IType.SPECIES_TYPES;
	private TypesManager parent;
	private final ConcurrentHashMap<String, IType<?>> types = new ConcurrentHashMap(5, 0.75f);

	public TypesManager(final ITypesManager types2) {
		setParent(types2);
	}

	@Override
	public Iterable<IType<?>> getAllTypes() {
		return new HashSet(types.values());
	}

	@Override
	public void setParent(final ITypesManager parent) {
		this.parent = (TypesManager) parent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.types.ITypesManager#alias(java.lang.String, java.lang.String)
	 */
	@Override
	public void alias(final String existingTypeName, final String otherTypeName) {
		final IType t = types.get(existingTypeName);
		if (t != null) {
			types.put(otherTypeName, t);
		}
	}

	@Override
	public boolean process(final SpeciesDescription species) {
		addSpeciesType(species);
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.types.ITypesManager#addSpeciesType(msi.gaml.descriptions. TypeDescription)
	 */
	@Override
	public IType<? extends IAgent> addSpeciesType(final SpeciesDescription species) {
		final String name = species.getName();
		if (!name.equals(IKeyword.AGENT)) {
			if (get(name) != Types.NO_TYPE) {
				species.error("Species " + name + " already declared. Species name must be unique",
						IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(), name);
			}
			return addSpeciesType(new GamaAgentType(species, species.getName(), ++CURRENT_INDEX,
					(Class<IAgent>) species.getJavaBase()), species.getJavaBase());
		}
		return get(IKeyword.AGENT);
	}

	@Override
	public <Support> IType<Support> initType(final String keyword, final IType<Support> originalType, final int id,
			final int varKind, final Class<Support> support) {
		IType<Support> typeInstance = originalType;
		if (keyword.equals(IKeyword.UNKNOWN)) {
			typeInstance = Types.NO_TYPE;
		}
		typeInstance.init(varKind, id, keyword, support);
		return addType(typeInstance, support);
	}

	private IType<? extends IAgent> addSpeciesType(final IType<? extends IAgent> t,
			final Class<? extends IAgent> clazz) {
		final int i = t.id();
		final String name = t.toString();
		types.put(name, t);
		// Hack to allow types to be declared with their id as string
		types.put(String.valueOf(i), t);
		// for (final Class cc : wraps) {
		Types.CLASSES_TYPES_CORRESPONDANCE.put(clazz, name);
		// }
		return t;
	}

	private IType addType(final IType t, final Class support) {
		final int i = t.id();
		final String name = t.toString();
		types.put(name, t);
		// Hack to allow types to be declared with their id as string
		types.put(String.valueOf(i), t);
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
		model.visitAllSpecies(entry -> {
			final IType type = get(entry.getName());
			if (!type.isParented() && !type.getName().equals(IKeyword.AGENT)) {
				final TypeDescription parent = entry.getParent();
				// Takes care of invalid species (see Issue 711)
				type.setParent(parent == null || parent == entry ? get(IKeyword.AGENT) : get(parent.getName()));
			}
			return true;
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
		if (t != null) { return true; }
		if (parent == null) { return false; }
		return parent.containsType(s);
	}

	@Override
	public IType get(final String type) {
		if (type == null) { return Types.NO_TYPE; }
		final IType t = types.get(type);
		if (t != null) { return t; }
		if (parent == null) { return Types.NO_TYPE; }
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
