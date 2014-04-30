package ummisco.gaml.editbox.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import ummisco.gaml.editbox.*;


public class SelectBox extends AbstractHandler {

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor!=null){
			IBoxDecorator decorator = EditBox.getDefault().getProviderRegistry().getDecorator(activeEditor);
			if (decorator!=null)
				decorator.selectCurrentBox();
		}
		return null;
	}

}
