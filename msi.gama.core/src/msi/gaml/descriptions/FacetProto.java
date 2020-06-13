/*******************************************************************************************************
 *
 * msi.gaml.descriptions.FacetProto.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

public class FacetProto implements IGamlDescription, Comparable<FacetProto> {

	public final String name;
	public String deprecated = null;
	public final IType<?>[] types;
	public final int[] typesDescribers;
	public final IType<?> contentType;
	public final IType<?> keyType;
	public final boolean optional;
	public final boolean internal;
	private final boolean isLabel;
	private final boolean isId;
	final boolean isRemote;
	final boolean isNewTemp;
	public final boolean isType;
	public final Set<String> values;
	public String doc = "No documentation yet";
	public String owner;

	public FacetProto(final String name, final int[] types, final int ct, final int kt, final String[] values,
			final boolean optional, final boolean internal, final boolean isRemote) {
		this.name = name;
		this.typesDescribers = types;
		isNewTemp = typesDescribers[0] == IType.NEW_TEMP_ID;
		this.types = new IType[types.length];
		for (int i = 0; i < types.length; i++) {
			this.types[i] = Types.get(types[i]);
		}
		this.contentType = Types.get(ct);
		this.keyType = Types.get(kt);
		this.optional = optional;
		this.internal = internal;
		this.isRemote = isRemote;
		isLabel = SymbolProto.ids.contains(types[0]);
		isId = isLabel && types[0] != IType.LABEL;
		isType = types[0] == IType.TYPE_ID;
		this.values = values.length == 0 ? null : ImmutableSet.copyOf(values);
		// if (doc != null) {
		// final String[] strings = doc.split(GamlProperties.SEPARATOR, -1);
		// this.doc = strings[0];
		// if (strings.length > 1) {
		// this.deprecated = strings[1];
		// if (deprecated.length() == 0) {
		// deprecated = null;
		// }
		// }
		// }
	}

	boolean isLabel() {
		return isLabel;
	}

	public void setOwner(final String s) {
		owner = s;
	}

	@Override
	public String getDefiningPlugin() {
		// returns null as facets cannot be defined alone (the symbol already
		// carries this information)
		return null;
	}

	public boolean isId() {
		return isId;
	}

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
			if (i != types.length - 1) {
				s.append(", ");
			}
		}
		if (types.length >= 2) {
			s.append("]");
		}
		return s.toString();
	}

	/**
	 * Method getDocumentation()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(100);
		sb.append("<b>").append(name).append("</b>, ")
				.append(deprecated != null ? "deprecated" : optional ? "optional" : "required").append("")
				.append(", expects ").append(typesToString());
		if (values != null && values.size() > 0) {
			sb.append(", takes values in ").append(values).append(". ");
		}
		if (doc != null && doc.length() > 0) {
			sb.append(" - ").append(doc);
		}
		if (deprecated != null) {
			sb.append(" <b>[");
			sb.append(deprecated);
			sb.append("]</b>");
		}
		return sb.toString();
	}

	/**
	 * Method getName()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

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
		if (deprecated != null) { return ""; }
		if (SymbolSerializer.uselessFacets.contains(name)) { return ""; }
		return name + (optional ? ": optional" : ": required") + " ("
				+ (types.length < 2 ? typesToString().substring(1) : typesToString()) + ")";
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {}

	public void buildDoc(final Class<?> c) {
		final facets facets = c.getAnnotation(facets.class);
		if (facets != null) {
			final facet[] array = facets.value();
			for (final facet f : array) {
				if (name.equals(f.name())) {
					final doc[] docs = f.doc();
					if (docs != null && docs.length > 0) {
						final doc d = docs[0];
						doc = d.value();
						deprecated = d.deprecated();
						if (deprecated.length() == 0) {
							deprecated = null;
						}
					}
				}
			}
		}

	}

	/**
	 * @return
	 */

}