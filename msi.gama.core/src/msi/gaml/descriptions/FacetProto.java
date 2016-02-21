/*********************************************************************************************
 *
 *
 * 'FacetProto.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.*;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.JavaWriter;
import msi.gaml.types.*;

public class FacetProto implements IGamlDescription, Comparable<FacetProto>, IGamlable {

	public final String name;
	public String deprecated = null;
	public final int[] types;
	public final int contentType;
	public final int keyType;
	public final boolean optional;
	public final boolean internal;
	private final boolean isLabel;
	private final boolean isId;
	public final boolean isType;
	public final Set<String> values;
	public String doc = "No documentation yet";
	// private SymbolProto owner;
	static FacetProto KEYWORD = KEYWORD();
	static FacetProto DEPENDS_ON = DEPENDS_ON();
	static FacetProto NAME = NAME();

	public FacetProto(final String name, final int[] types, final int ct, final int kt, final String[] values,
		final boolean optional, final boolean internal, final String doc) {
		this.name = name;
		this.types = types;
		this.contentType = ct;
		this.keyType = kt;
		this.optional = optional;
		this.internal = internal;
		isLabel = SymbolProto.ids.contains(types[0]);
		isId = isLabel && types[0] != IType.LABEL;
		isType = types[0] == IType.TYPE_ID;
		this.values = new THashSet(Arrays.asList(values));
		if ( doc != null ) {
			String[] strings = doc.split(JavaWriter.DOC_SEP, -1);
			this.doc = strings[0];
			if ( strings.length > 1 ) {
				this.deprecated = strings[1];
				if ( deprecated.length() == 0 ) {
					deprecated = null;
				}
			}
		}
	}

	boolean isLabel() {
		return isLabel;
	}

	@Override
	public String getDefiningPlugin() {
		// returns null as facets cannot be defined alone (the symbol already carries this information)
		return null;
	}

	public boolean isId() {
		return isId;
	}

	// public void setOwner(final SymbolProto symbol) {
	// owner = symbol;
	// }

	static FacetProto DEPENDS_ON() {
		return new FacetProto(IKeyword.DEPENDS_ON, new int[] { IType.LIST }, IType.STRING, IType.INT, new String[0],
			true, true, "the dependencies of expressions (internal)");
	}

	static FacetProto KEYWORD() {
		return new FacetProto(IKeyword.KEYWORD, new int[] { IType.ID }, IType.NONE, IType.NONE, new String[0], true,
			true, "the declared keyword (internal)");
	}

	static FacetProto NAME() {
		return new FacetProto(IKeyword.NAME, new int[] { IType.LABEL }, IType.NONE, IType.NONE, new String[0], true,
			true, "the declared name (internal)");
	}

	/**
	 * Method getTitle()
	 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		// String p = owner == null ? "" : " of statement " + owner.getName();
		return "Facet " + name /* + p */;
	}

	public String typesToString() {
		StringBuilder s = new StringBuilder(30);
		s.append(types.length < 2 ? " " : " any type in [");
		for ( int i = 0; i < types.length; i++ ) {
			switch (types[i]) {
				case IType.ID:
					s.append("an identifier");
					break;
				case IType.LABEL:
					s.append("a label");
					break;
				case IType.NEW_TEMP_ID:
					s.append("a new identifier");
					break;
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
					// TODO AD 2/16 Document the types with the new possibility to include of and index
					s.append(Types.get(types[i]).toString());
			}
			if ( i != types.length - 1 ) {
				s.append(", ");
			}
		}
		if ( types.length >= 2 ) {
			s.append("]");
		}
		return s.toString();
	}

	/**
	 * Method getDocumentation()
	 * @see msi.gaml.descriptions.IGamlDescription#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("<b>").append(name).append("</b>, ")
			.append(deprecated != null ? "deprecated" : optional ? "optional" : "required").append("")
			.append(", expects ").append(typesToString());
		if ( values.size() > 0 ) {
			sb.append(", takes values in ").append(values).append(". ");
		}
		if ( doc != null && doc.length() > 0 ) {
			sb.append(" - ").append(doc);
		}
		if ( deprecated != null ) {
			sb.append(" <b>[");
			sb.append(deprecated);
			sb.append("]</b>");
		}
		return sb.toString();
	}

	/**
	 * Method getName()
	 * @see msi.gaml.descriptions.IGamlDescription#getName()
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
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final FacetProto o) {
		return getName().compareTo(o.getName());
	}

	/**
	 * Method serialize()
	 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		if ( deprecated != null ) { return ""; }
		if ( SymbolSerializer.uselessFacets.contains(name) ) { return ""; }
		return name + (optional ? ": optional" : ": required") + " (" +
			(types.length < 2 ? typesToString().substring(1) : typesToString()) + ")";
	}

	/**
	 * Method collectPlugins()
	 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
	 */
	@Override
	public void collectPlugins(final Set<String> plugins) {}

	/**
	 * @return
	 */

}