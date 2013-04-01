/**
 * Created by drogoul, 19 avr. 2012
 * 
 */
package msi.gama.lang.gaml.resource;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
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

	static Set<String> composed = new HashSet(Arrays.asList(GLOBAL, SPECIES, ENTITIES, ENVIRONMENT,
		ACTION));

	@Override
	public boolean createEObjectDescriptions(final EObject o,
		final IAcceptor<IEObjectDescription> acceptor) {
		if ( o instanceof ActionArguments ) {
			return true;
		} else if ( o instanceof ArgumentDefinition ) {
			super.createEObjectDescriptions(o, acceptor);
		} else if ( o instanceof Statement ) {
			Statement stm = (Statement) o;
			String n = EGaml.getNameOf(stm);
			if ( n != null ) {
				super.createEObjectDescriptions(stm, acceptor);
			}
			return ((Statement) o).getBlock() != null; // composed.contains(EGaml.getKey.caseStatement(stm));
		}
		return o instanceof Block || o instanceof Model;
	}
}
