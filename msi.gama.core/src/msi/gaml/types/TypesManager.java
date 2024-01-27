/*******************************************************************************************************
 *
 * TypesManager.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class TypesManager.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class TypesManager implements ITypesManager {

	static {
		DEBUG.ON();
	}

	/** The current index. */
	public static int CURRENT_INDEX = IType.SPECIES_TYPES;

	/** The name. */
	private final String name;

	/** The parent. */
	private volatile TypesManager parent;

	/** The types. */
	private final ConcurrentHashMap<String, IType<?>> types = new ConcurrentHashMap(5, 0.75f);

	/**
	 * Instantiates a new types manager.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name.
	 * @date 21 janv. 2024
	 */
	public TypesManager(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Types manager for " + name;
	}

	/**
	 * Instantiates a new types manager.
	 *
	 * @param types2
	 *            the types 2
	 */
	// public TypesManager(final ITypesManager types2) {
	// setParent(types2);
	// }

	@Override
	public Set<IType<?>> getAllTypes() { return new HashSet(types.values()); }

	@Override
	public void setParent(final ITypesManager newParent) {
		// DEBUG.OUT("Trying to parent " + this + " with " + newParent);
		ITypesManager oldParent = parent;
		if (newParent == this || newParent == oldParent) return;
		if (oldParent == null || oldParent == Types.getBuiltInTypes()) {
			this.parent = (TypesManager) newParent;
			return;
		}
		if (newParent != Types.getBuiltInTypes()) { newParent.setParent(oldParent); }
		this.parent = (TypesManager) newParent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.types.ITypesManager#alias(java.lang.String, java.lang.String)
	 */
	@Override
	public void alias(final String existingTypeName, final String otherTypeName) {
		final IType t = get(existingTypeName, null);
		if (t != null) { types.put(otherTypeName, t); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gaml.types.ITypesManager#addSpeciesType(msi.gaml.descriptions. TypeDescription)
	 */
	@Override
	public IType<? extends IAgent> addSpeciesType(final SpeciesDescription species) {
		final String name = species.getName();
		if (!IKeyword.AGENT.equals(name) && !containsType(name)) // if (containsType(name)) {
			// species.error("Species " + name + " already declared. Species name must be unique",
			// IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(), name);
			// }
			return addSpeciesType(new GamaAgentType(species, species.getName(), ++CURRENT_INDEX,
					(Class<IAgent>) species.getJavaBase()), species.getJavaBase());
		return (IType<? extends IAgent>) get(IKeyword.AGENT);
	}

	@Override
	public <Support> IType<Support> initType(final String keyword, final IType<Support> originalType, final int id,
			final int varKind, final Class<Support> support, final String plugin) {
		IType<Support> typeInstance = originalType;
		if (IKeyword.UNKNOWN.equals(keyword)) { typeInstance = Types.NO_TYPE; }
		typeInstance.init(varKind, id, keyword, support);
		typeInstance.setDefiningPlugin(plugin);
		return addType(typeInstance, support);
	}

	/**
	 * Adds the species type.
	 *
	 * @param t
	 *            the t
	 * @param clazz
	 *            the clazz
	 * @return the i type<? extends I agent>
	 */
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

	/**
	 * Adds the type.
	 *
	 * @param t
	 *            the t
	 * @param support
	 *            the support
	 * @return the i type
	 */
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
		model.visitAllSpecies(entry -> {
			addSpeciesType(entry);
			return true;
		});
		// Then we parent the types
		model.visitAllSpecies(entry -> {
			final IType type = get(entry.getName());
			if (!IKeyword.AGENT.equals(type.getName())) {
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
		if (t != null) return true;
		if (getParent() == null) return false;
		return getParent().containsType(s);
	}

	@Override
	public IType get(final String type, final IType defaultValue) {
		if (type == null) return defaultValue;
		final IType t = types.get(type);
		if (t != null) return t;
		if (getParent() == null) return defaultValue;
		return getParent().get(type, defaultValue);
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

	@Override
	public IType decodeType(final String s) {
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
		try {
			tokenizer.nextToken(); // Skip "BOF" token
			return decode(tokenizer);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Parses the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param tokenizer
	 *            the tokenizer
	 * @return the i type
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @date 4 nov. 2023
	 */
	IType decode(final StreamTokenizer tokenizer) throws IOException {
		String baseName = tokenizer.sval;
		IType result = get(baseName);
		tokenizer.nextToken();
		if (!(result instanceof IContainerType ic)) return result;

		IType param1 = Types.NO_TYPE;
		IType param2 = Types.NO_TYPE;
		boolean first = true;
		if (tokenizer.ttype == '<') {
			do {
				tokenizer.nextToken(); // Skip '<' or ','
				if (first) {
					param1 = decode(tokenizer);
					first = false;
				} else {
					param2 = decode(tokenizer);
					first = true;
				}
			} while (tokenizer.ttype == ',');
			tokenizer.nextToken(); // skip '>'
		}
		if (first) return ic.of(param1, param2);
		return ic.of(param1);
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	private TypesManager getParent() { return parent; }

}
