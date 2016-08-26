/*********************************************************************************************
 * 
 * 
 * 'GamlResourceDescriptionStrategy.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;

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

	@Override
	public boolean createEObjectDescriptions(final EObject o, final IAcceptor<IEObjectDescription> acceptor) {
		if (o instanceof ActionArguments) {
			return true;
		} else if (o instanceof ArgumentDefinition) {
			super.createEObjectDescriptions(o, acceptor);
		} else if (o instanceof Statement) {
			try {
				super.createEObjectDescriptions(o, acceptor);
			} catch (final IllegalArgumentException e) {
				return false;
			}
			return ((Statement) o).getBlock() != null; //
		}
		return o instanceof Block || o instanceof Model;
	}
}
