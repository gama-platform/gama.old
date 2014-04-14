/*********************************************************************************************
 * 
 *
 * 'GamlOutlineTreeProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.outline;

import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

/**
 * customization of the default outline structure
 * 
 */
public class GamlOutlineTreeProvider extends DefaultOutlineTreeProvider {

	protected void _createChildren(final IOutlineNode parentNode, final Statement stm) {
		Block block = stm.getBlock();
		if ( block != null ) {
			for ( Statement substm : EGaml.getStatementsOf(block) ) {
				createNode(parentNode, substm);
			}
		}
	}

	protected boolean _isLeaf(final Statement s) {
		return s.getBlock() == null;
	}
}
