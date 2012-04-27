/**
 * Created by drogoul, 5 févr. 2012
 * 
 */

package msi.gama.lang.gaml.ui;

import msi.gama.common.util.GuiUtils;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.validation.ValidatingEditorCallback;

/**
 * The class GamlEditorCallback.
 * 
 * @author drogoul
 * @since 5 févr. 2012
 * 
 */
public class GamlValidatingEditorCallback extends ValidatingEditorCallback {

	@Override
	public void afterCreatePartControl(final XtextEditor editor) {
		GuiUtils.debug("After create part control called for " + editor.getPartName());
		super.afterCreatePartControl(editor);
		// ((GamlEditor) editor).installDocumentListener();
	}

	@Override
	public void afterSave(final XtextEditor editor) {
		GuiUtils.debug("After save called for " + editor.getPartName());
		// super.afterSave(editor);
		// editorSupport.markEditorClean(this);
	}

}
