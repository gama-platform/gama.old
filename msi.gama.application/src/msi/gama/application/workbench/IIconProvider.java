/*******************************************************************************************************
 *
 * IIconProvider.java, in msi.gama.application, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.application.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * The Interface IIconProvider.
 */
public interface IIconProvider {

	/**
	 * Desc.
	 *
	 * @param name the name
	 * @return the image descriptor
	 */
	ImageDescriptor desc(String name);

	/**
	 * Image.
	 *
	 * @param name the name
	 * @return the image
	 */
	Image image(String name);

}
