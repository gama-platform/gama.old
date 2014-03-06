package msi.gaml.descriptions;

import msi.gaml.compilation.GamaHelper;
import msi.gaml.descriptions.StatementDescription.StatementWithChildrenDescription;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

public class PrimitiveDescription extends StatementWithChildrenDescription {

	private GamaHelper helper;

	// TODO Voir si on ne peut pas simplifier un peu l'instatiation et la copie

	public PrimitiveDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final boolean hasScope, final boolean hasArgs, final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, hasScope, hasArgs, source, facets);
	}

	public GamaHelper getHelper() {
		return helper;
	}

	public void setHelper(final GamaHelper helper) {
		this.helper = helper;
	}

	@Override
	public PrimitiveDescription copy(final IDescription into) {
		PrimitiveDescription desc =
			new PrimitiveDescription(getKeyword(), into, ChildrenProvider.NONE, false, args != null, element, facets);
		if ( args != null ) {
			desc.args.putAll(args);
		}
		desc.originName = originName;
		desc.setHelper(helper);
		return desc;
	}

}
