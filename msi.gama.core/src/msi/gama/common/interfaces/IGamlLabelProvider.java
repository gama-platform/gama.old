/*********************************************************************************************
 *
 * 'IGamlLabelProvider.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gaml.compilation.ast.ISyntacticElement;

public interface IGamlLabelProvider {

	String getText(ISyntacticElement element);

	Object getImage(ISyntacticElement element);

}
