package msi.gaml.batch;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;


@facets(value = { @facet(name = IKeyword.NAME, type = IType.LABEL, optional = true),
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
		@facet(name = IKeyword.FRAMERATE, type = IType.INT_STR, optional = true) }, omissible = IKeyword.VAR)
	@symbol(name = { IKeyword.EXPORT }, kind = ISymbolKind.PARAMETER, with_sequence = false)
	@inside(kinds = { ISymbolKind.EXPERIMENT })
public class ExportSymbol extends Symbol {

	public ExportSymbol(IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChildren(List<? extends ISymbol> children) {
		// TODO Auto-generated method stub
		
	}

}
