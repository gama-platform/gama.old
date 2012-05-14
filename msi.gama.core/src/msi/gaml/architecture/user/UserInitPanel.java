package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.IDescription;

@symbol(name = IKeyword.USER_INIT, kind = ISymbolKind.BEHAVIOR)
@inside(kinds = { ISymbolKind.SPECIES })
public class UserInitPanel extends UserPanel {

	public UserInitPanel(final IDescription desc) {
		super(desc);
	}

}
