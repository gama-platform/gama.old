package msi.gaml.descriptions;

import java.util.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.factories.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.ecore.EObject;

public class PrimitiveDescription extends StatementDescription {

	private GamaHelper helper;

	// TODO Voir si on ne peut pas simplifier un peu l'instatiation et la copie

	public PrimitiveDescription(final String keyword, final IDescription superDesc, final IChildrenProvider cp,
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
		List<IDescription> children = new ArrayList();
		for ( IDescription child : getChildren() ) {
			children.add(child.copy(into));
		}
		if ( args != null ) {
			for ( IDescription child : args.values() ) {
				children.add(child.copy(into));
			}
		}
		PrimitiveDescription desc =
			new PrimitiveDescription(getKeyword(), into, new ChildrenProvider(children), false, args != null, element,
				facets);
		desc.originName = originName;
		desc.setHelper(helper);
		return desc;
	}

}
