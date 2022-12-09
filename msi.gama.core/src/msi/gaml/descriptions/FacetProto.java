/*******************************************************************************************************
 *
 * FacetProto.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class FacetProto.
 */
public class FacetProto implements IGamlDescription, Comparable<FacetProto> {

	/** The name. */
	public final String name;

	/** The deprecated. */
	public String deprecated = null;

	/** The types. */
	public final IType<?>[] types;

	/** The types describers. */
	public final int[] typesDescribers;

	/** The content type. */
	public final IType<?> contentType;

	/** The key type. */
	public final IType<?> keyType;

	/** The optional. */
	public final boolean optional;

	/** The internal. */
	public final boolean internal;

	/** The is label. */
	private final boolean isLabel;

	/** The is id. */
	private final boolean isId;

	/** The is remote. */
	final boolean isRemote;

	/** The is new temp. */
	final boolean isNewTemp;

	/** The doc built. */
	boolean docBuilt;

	/** The is type. */
	public final boolean isType;

	/** The values. */
	public final Set<String> values;

	/** The doc. */
	public String doc = "No documentation yet";

	/** The owner. */
	public String owner;

	/** The support. */
	public Class<?> support;

	/**
	 * Instantiates a new facet proto.
	 *
	 * @param name
	 *            the name
	 * @param types
	 *            the types
	 * @param ct
	 *            the ct
	 * @param kt
	 *            the kt
	 * @param values
	 *            the values
	 * @param optional
	 *            the optional
	 * @param internal
	 *            the internal
	 * @param isRemote
	 *            the is remote
	 */
	public FacetProto(final String name, final int[] types, final int ct, final int kt, final String[] values,
			final boolean optional, final boolean internal, final boolean isRemote) {
		this.name = name;
		this.typesDescribers = types;
		isNewTemp = typesDescribers[0] == IType.NEW_TEMP_ID;
		this.types = new IType[types.length];
		for (int i = 0; i < types.length; i++) { this.types[i] = Types.get(types[i]); }
		this.contentType = Types.get(ct);
		this.keyType = Types.get(kt);
		this.optional = optional;
		this.internal = internal;
		this.isRemote = isRemote;
		isLabel = SymbolProto.ids.contains(types[0]);
		isId = isLabel && types[0] != IType.LABEL;
		isType = types[0] == IType.TYPE_ID;
		this.values = values.length == 0 ? null : ImmutableSet.copyOf(values);
	}

	/**
	 * Checks if is label.
	 *
	 * @return true, if is label
	 */
	boolean isLabel() { return isLabel; }

	/**
	 * Sets the owner.
	 *
	 * @param s
	 *            the new owner
	 */
	public void setOwner(final String s) { owner = s; }

	@Override
	public String getDefiningPlugin() {
		// returns null as facets cannot be defined alone (the symbol already
		// carries this information)
		return null;
	}

	/**
	 * Checks if is id.
	 *
	 * @return true, if is id
	 */
	public boolean isId() { return isId; }

	/**
	 * Method getTitle()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		final String p = owner == null ? "" : " of " + owner;
		return "Facet " + name + p;
	}

	/**
	 * Types to string.
	 *
	 * @return the string
	 */
	public String typesToString() {
		final StringBuilder s = new StringBuilder(30);
		s.append(types.length < 2 ? " " : " any type in [");
		for (int i = 0; i < types.length; i++) {
			switch (typesDescribers[i]) {
				case IType.ID:
					s.append("an identifier");
					break;
				case IType.LABEL:
					s.append("a label");
					break;
				case IType.NEW_TEMP_ID:
				case IType.NEW_VAR_ID:
					s.append("a new identifier");
					break;
				case IType.TYPE_ID:
					s.append("a datatype identifier");
					break;
				case IType.NONE:
					s.append("any type");
					break;
				default:
					// TODO AD 2/16 Document the types with the new possibility to
					// include of and index
					s.append(types[i].toString());
			}
			if (i != types.length - 1) { s.append(", "); }
		}
		if (types.length >= 2) { s.append("]"); }
		return s.toString();
	}

	/**
	 * Method getDocumentation()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getDocumentation()
	 */
	@Override
	public Doc getDocumentation() {
		final Doc sb = new RegularDoc();
		sb.append(getDeprecated() != null ? "Deprecated" : optional ? "Optional" : "Required").append(", expects ")
				.append(typesToString());
		if (values != null && values.size() > 0) {
			sb.append(", takes values in ").append(values.toString()).append(". ");
		}
		if (getDoc() != null && getDoc().length() > 0) { sb.append(" - ").append(getDoc()); }
		if (getDeprecated() != null) {
			sb.append(" <b>[");
			sb.append(getDeprecated());
			sb.append("]</b>");
		}
		return sb;
	}

	/**
	 * Method getName()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getName()
	 */
	@Override
	public String getName() { return name; }

	@Override
	public void setName(final String name) {
		// Nothing
	}

	/**
	 * Method compareTo()
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final FacetProto o) {
		return getName().compareTo(o.getName());
	}

	/**
	 * Method serialize()
	 *
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (getDeprecated() != null || SymbolSerializer.uselessFacets.contains(name)) return "";
		return name + (optional ? ": optional" : ": required") + " ("
				+ (types.length < 2 ? typesToString().substring(1) : typesToString()) + ")";
	}

	/**
	 * Builds the doc.
	 */
	public void buildDoc() {
		if (docBuilt) return;
		docBuilt = true;
		final facets facets = support.getAnnotation(facets.class);
		if (facets != null) {
			final facet[] array = facets.value();
			for (final facet f : array) {
				if (name.equals(f.name())) {
					final doc[] docs = f.doc();
					if (docs != null && docs.length > 0) {
						final doc d = docs[0];
						doc = d.value();
						deprecated = d.deprecated();
						if (deprecated != null && deprecated.length() == 0) { deprecated = null; }
					}
				}
			}
		}
		if (doc == null) { doc = "Not documented"; }

	}

	/**
	 * Sets the class.
	 *
	 * @param c
	 *            the new class
	 */
	public void setClass(final Class c) { support = c; }

	/**
	 * Gets the doc.
	 *
	 * @return the doc
	 */
	public String getDoc() {
		buildDoc();
		return doc;
	}

	/**
	 * Gets the deprecated.
	 *
	 * @return the deprecated
	 */
	public String getDeprecated() {
		buildDoc();
		return deprecated;
	}

	/**
	 * @return
	 */

}