/**
 * Created by drogoul, 7 mai 2016
 * 
 */
package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy;
import org.eclipse.xtext.ui.editor.reconciler.XtextReconciler;

import com.google.inject.Inject;

import msi.gama.lang.gaml.resource.GamlResourceDocManager;

/**
 * The class GamlReconciler.
 *
 * @author drogoul
 * @since 7 mai 2016
 *
 */
public class GamlReconciler extends XtextReconciler {

	/**
	 * @param strategy
	 */
	@Inject
	public GamlReconciler(final XtextDocumentReconcileStrategy strategy) {
		super(strategy);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		System.out.println("Reconcile job beginning");
		final IStatus status = super.run(monitor);
		if (status == Status.CANCEL_STATUS)
			return status;
		try {
			System.out.println("Documenting job beginning");
			GamlResourceDocManager.DocumentationJob.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return status;
	}

}
