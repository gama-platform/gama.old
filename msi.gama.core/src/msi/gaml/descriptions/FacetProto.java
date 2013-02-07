package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.types.IType;

public class FacetProto {

	public final String name;
	public final List<String> types;
	public final boolean optional;
	public final boolean isLabel;
	public final String[] values;
	static FacetProto KEYWORD = KEYWORD();
	static FacetProto DEPENDS_ON = DEPENDS_ON();

	public FacetProto(final String name, final String[] types, final String[] values,
		final boolean optional) {
		this.name = name;
		this.types = Arrays.asList(types);
		this.optional = optional;
		isLabel = SymbolProto.ids.contains(types[0]);
		this.values = values;
	}

	static FacetProto DEPENDS_ON() {
		return new FacetProto(IKeyword.DEPENDS_ON, new String[] { IType.LABEL }, new String[0],
			true);
	}

	static FacetProto KEYWORD() {
		return new FacetProto(IKeyword.KEYWORD, new String[] { IType.ID }, new String[0], true);
	}
}