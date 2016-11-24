/*********************************************************************************************
 *
 * 'IBoxEnabledEditor.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

/**
 * Class IBoxEnabledEditor.
 * 
 * @author drogoul
 * @since 16 nov. 2014
 * 
 */
public interface IBoxEnabledEditor {

	IBoxDecorator getDecorator();

	boolean isDecorationEnabled();

	void createDecorator();

	void decorate(boolean doIt);

	void enableUpdates(boolean visible);

}
