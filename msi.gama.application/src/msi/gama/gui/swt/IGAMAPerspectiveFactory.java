package msi.gama.gui.swt;

import msi.gama.runtime.IScope;
import org.eclipse.ui.IPerspectiveFactory;


public interface IGAMAPerspectiveFactory extends IPerspectiveFactory {
	public void initialise(IScope gamaScope);
}
