/*********************************************************************************************
 *
 * 'IBoxDecorator.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
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

import org.eclipse.swt.custom.StyledText;

public interface IBoxDecorator {

	IBoxProvider getProvider();

	void setProvider(IBoxProvider newProvider);

	void setStyledText(StyledText st);

	void setSettings(IBoxSettings settings);

	void enableUpdates(boolean visible);

	void decorate(boolean mouseClickSupport);

	void undecorate();

	void forceUpdate();

	// void selectCurrentBox();
	// void unselectCurrentBox();
}
