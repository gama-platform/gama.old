/*******************************************************************************************************
 *
 * IBoxProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.Collection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The Interface IBoxProvider.
 */
public interface IBoxProvider {

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Supports.
	 *
	 * @param editorPart the editor part
	 * @return true, if successful
	 */
	boolean supports(IWorkbenchPart editorPart);

	/**
	 * Decorate.
	 *
	 * @param editorPart the editor part
	 * @return the i box decorator
	 */
	IBoxDecorator decorate(IWorkbenchPart editorPart);

	/**
	 * Gets the editors box settings.
	 *
	 * @return the editors box settings
	 */
	IBoxSettings getEditorsBoxSettings();

	/**
	 * Gets the settings store.
	 *
	 * @return the settings store
	 */
	IBoxSettingsStore getSettingsStore();

	/**
	 * Creates the settings.
	 *
	 * @return the i box settings
	 */
	IBoxSettings createSettings();

	/**
	 * Creates the decorator.
	 *
	 * @return the i box decorator
	 */
	IBoxDecorator createDecorator();

	/**
	 * Gets the builders.
	 *
	 * @return the builders
	 */
	Collection<String> getBuilders();

	/**
	 * Creates the box builder.
	 *
	 * @param name the name
	 * @return the i box builder
	 */
	IBoxBuilder createBoxBuilder(String name);

}
