package msi.gaml.descriptions;

import msi.gama.common.interfaces.IKeyword;
import msi.gaml.types.IType;

public class FacetProto {

	public final String name;
	public final int[] types;
	public final boolean optional;
	public final boolean isLabel;
	public final String[] values;
	public final String doc;
	static FacetProto KEYWORD = KEYWORD();
	static FacetProto DEPENDS_ON = DEPENDS_ON();
	static FacetProto NAME = NAME();

	public FacetProto(final String name, final int[] types, final String[] values,
		final boolean optional, String doc) {
		this.name = name;
		this.types = types;
		this.optional = optional;
		isLabel = SymbolProto.ids.contains(types[0]);
		this.values = values;
		this.doc = doc;
	}

	static FacetProto DEPENDS_ON() {
		return new FacetProto(IKeyword.DEPENDS_ON, new int[] { IType.LIST }, new String[0],
			true, "");
	}

	static FacetProto KEYWORD() {
		return new FacetProto(IKeyword.KEYWORD, new int[] { IType.ID }, new String[0], true, "");
	}

	static FacetProto NAME() {
		return new FacetProto(IKeyword.NAME, new int[] { IType.LABEL }, new String[0], true, "");
	}
}