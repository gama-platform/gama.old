/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IGamlLabelProvider.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gaml.compilation.ast.ISyntacticElement;

public interface IGamlLabelProvider {

	String getText(ISyntacticElement element);

	Object getImage(ISyntacticElement element);

}
