/**
 * Created by drogoul, 19 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 19 avr. 2012
 * 
 */
public class GamlResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

	/**
	 * @see org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy#createEObjectDescriptions(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.xtext.util.IAcceptor)
	 */

	static Set<String> composed =
		new HashSet(Arrays.asList(GLOBAL, SPECIES, ENTITIES, ENVIRONMENT));

	@Override
	public boolean createEObjectDescriptions(final EObject eObject,
		final IAcceptor<IEObjectDescription> acceptor) {
		if ( eObject instanceof GamlVarRef ) {
			String s = ((GamlVarRef) eObject).getName();
			if ( s != null && !s.isEmpty() ) {
				acceptor.accept(EObjectDescription.create(QualifiedName.create(s), eObject));
			}
			return eObject instanceof Definition ? composed.contains(((Definition) eObject)
				.getKey()) : false;
		}

		if ( eObject instanceof GamlLangDef || eObject instanceof Block || eObject instanceof Model ) { return true; }
		return false;
	}
}
