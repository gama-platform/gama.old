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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import msi.gama.lang.gaml.ui.editor.toolbar.EditorToolbar;
import ummisco.gama.ui.access.GamlSearchField;

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
		final UIJob job = new UIJob("Install GAML Search") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					final MTrimBar topTrim = ((WorkbenchWindow) window).getTopTrim();
					for (final MTrimElement element : topTrim.getChildren()) {
						if ("SearchField".equals(element.getElementId())) {
							final Composite parent = ((Control) element.getWidget()).getParent();
							((Control) element.getWidget()).dispose();
							element.setWidget(GamlSearchField.installOn(parent));
							new EditorToolbar().fill(GamlSearchField.INSTANCE.getToolbar());
							parent.layout(true, true);
							parent.update();
							break;
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

}
