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
