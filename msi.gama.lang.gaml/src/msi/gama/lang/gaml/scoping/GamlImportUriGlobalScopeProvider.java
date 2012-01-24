/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.scoping;

import java.util.*;
import msi.gaml.compilation.GamlCompiler;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import com.google.common.base.Predicate;

public class GamlImportUriGlobalScopeProvider extends ImportUriGlobalScopeProvider {

	@Override
	protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
		LinkedHashSet<URI> temp = super.getImportedUris(resource);
		Iterator<URI> uriIter = temp.iterator();
		while (uriIter.hasNext()) {
			URI uri = uriIter.next();
			if ( uri == null || uri.isEmpty() ) {
				uriIter.remove();
			}
		}
		for ( URI uri : GamlCompiler.gamlAdditionsURIs ) {
			temp.add(uri);
		}
		return temp;
	}

	@Override
	protected IScope createLazyResourceScope(final IScope parent, final URI uri,
		final IResourceDescriptions descriptions, final EClass type,
		final Predicate<IEObjectDescription> filter, final boolean ignoreCase) {
		if ( uri == null || uri.isEmpty() ) { return parent; }
		return super.createLazyResourceScope(parent, uri, descriptions, type, filter, ignoreCase);
	}

}
