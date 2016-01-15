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

import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.descriptions.StatementDescription.StatementWithChildrenDescription;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

public class PrimitiveDescription extends StatementWithChildrenDescription {

	private GamaHelper helper;
	private String plugin;

	// TODO Voir si on ne peut pas simplifier un peu l'instatiation et la copie

	public PrimitiveDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
		final boolean hasScope, final boolean hasArgs, final EObject source, final Facets facets, final String plugin) {
		super(keyword, superDesc, cp, hasScope, hasArgs, source, facets);
		this.plugin = plugin;
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
			args != null, element, facets.cleanCopy(), plugin);
		if ( args != null ) {
			desc.args.putAll(args);
		}
		desc.originName = originName;
		desc.setHelper(helper);
		return desc;
	}

	/**
	 * @param plugin name
	 */
	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	@Override
	public void collectPlugins(final Set<String> plugins) {
		plugins.add(plugin);
	}

}
