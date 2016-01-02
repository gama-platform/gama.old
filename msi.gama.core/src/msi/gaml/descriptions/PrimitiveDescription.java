/*********************************************************************************************
 *
 *
 * 'PrimitiveDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import org.eclipse.emf.ecore.EObject;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.StatementDescription.StatementWithChildrenDescription;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

public class PrimitiveDescription extends StatementWithChildrenDescription {

	private GamaHelper helper;
	private final String plugin;

	// TODO Voir si on ne peut pas simplifier un peu l'instatiation et la copie

	public PrimitiveDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final boolean hasScope, final boolean hasArgs, final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, hasScope, hasArgs, source, facets);
		plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	public GamaHelper getHelper() {
		return helper;
	}

	public void setHelper(final GamaHelper helper) {
		this.helper = helper;
	}

	@Override
	public PrimitiveDescription copy(final IDescription into) {
		PrimitiveDescription desc = new PrimitiveDescription(getKeyword(), into, ChildrenProvider.NONE, false,
			args != null, element, facets.cleanCopy());
		if ( args != null ) {
			desc.args.putAll(args);
		}
		desc.originName = originName;
		desc.setHelper(helper);
		return desc;
	}

}
