/*********************************************************************************************
 *
 * 'IBoxProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.Collection;
import org.eclipse.ui.IWorkbenchPart;

public interface IBoxProvider {

	String getId();

	String getName();

	boolean supports(IWorkbenchPart editorPart);

	IBoxDecorator decorate(IWorkbenchPart editorPart);

	IBoxSettings getEditorsBoxSettings();

	IBoxSettingsStore getSettingsStore();

	IBoxSettings createSettings();

	IBoxDecorator createDecorator();

	Collection<String> getBuilders();

	IBoxBuilder createBoxBuilder(String name);

}
