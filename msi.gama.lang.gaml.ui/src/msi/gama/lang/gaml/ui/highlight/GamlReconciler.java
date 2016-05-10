/**
 * Created by drogoul, 7 mai 2016
 * 
 */
package msi.gama.lang.gaml.ui.highlight;

import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy;
import org.eclipse.xtext.ui.editor.reconciler.XtextReconciler;

import com.google.inject.Inject;

/**
 * The class GamlReconciler.
 *
 * @author drogoul
 * @since 7 mai 2016
 *
 */
public class GamlReconciler extends XtextReconciler {

	String title;

	/**
	 * @param strategy
	 */
	@Inject
	public GamlReconciler(final XtextDocumentReconcileStrategy strategy) {
		super(strategy);
		setDelay(500);
		// pause();
	}

	// @Override
	// public void resume() {
	// }
	// @Override
	// protected IStatus run(final IProgressMonitor monitor) {
	// // System.out.println("Reconcile job beginning");
	// // final long begin = System.nanoTime();
	// // final IStatus status = super.run(monitor);
	// // System.out.println("'" + title + "' reconciled in " +
	// // (System.nanoTime() - begin) / 1000000d + " ms");
	// //
	// System.out.println("****************************************************");
	//
	// return Status.CANCEL_STATUS;
	// }

	// @Override
	// protected void handleInputDocumentChanged(final IDocument oldInput, final
	// IDocument newInput) {
	// super.handleInputDocumentChanged(oldInput, newInput);
	// }
	//
	// @Override
	// public void setEditor(final XtextEditor editor) {
	// final GamlEditor ed = (GamlEditor) editor;
	// title = ed.getPartName();
	// super.setEditor(editor);
	// }

}
