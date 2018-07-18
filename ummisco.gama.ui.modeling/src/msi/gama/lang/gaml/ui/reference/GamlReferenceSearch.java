/*********************************************************************************************
 *
 * 'GamlReferenceSearch.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.reference;

import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import ummisco.gama.ui.access.GamlSearchField;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class GamlReferenceTools.
 *
 * @author drogoul
 * @since 11 nov. 2016
 *
 */
public class GamlReferenceSearch {

	static private GamlReferenceSearch INSTANCE = new GamlReferenceSearch();

	public static void install() {
		WorkbenchHelper.runInUI("Install GAML Search", 0, m -> {
			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window instanceof WorkbenchWindow) {
				final MTrimBar topTrim = ((WorkbenchWindow) window).getTopTrim();
				for (final MTrimElement element : topTrim.getChildren()) {
					if ("SearchField".equals(element.getElementId())) {
						final Composite parent = ((Control) element.getWidget()).getParent();
						final Control old = (Control) element.getWidget();
						WorkbenchHelper.runInUI("Disposing old search control", 500, m2 -> old.dispose());
						element.setWidget(GamlSearchField.installOn(parent));
						parent.layout(true, true);
						parent.update();
						break;
					}
				}
			}
		});
	}

}
