/**
 * Created by drogoul, 13 févr. 2012
 * 
 */
package msi.gama.metamodel.population;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;

/**
 * The class EntitiesPlaceHolder.
 * 
 * @author drogoul
 * @since 13 févr. 2012
 * 
 */
@symbol(kind = ISymbolKind.ABSTRACT_SECTION, name = { IKeyword.ENTITIES })
@inside(kinds = ISymbolKind.MODEL)
@with_sequence
public class EntitiesPlaceHolder extends Symbol {

	public EntitiesPlaceHolder(final IDescription desc) {
		super(desc);
	}

	/**
	 * @see msi.gaml.compilation.Symbol#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

}
