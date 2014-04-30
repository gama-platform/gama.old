package ummisco.gaml.editbox.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import ummisco.gaml.editbox.*;
import ummisco.gaml.editbox.impl.BoxProviderRegistry;

//keep it stateless since called from commands
public class EnableEditBox extends AbstractHandler implements IWorkbenchWindowActionDelegate {

	public static final String COMMAND_ID = "ummisco.gaml.editbox.actions.EnableEditBoxCmd";
	
	private IWorkbenchWindow win;
	private BoxProviderRegistry registry; //only caching

	public void init(IWorkbenchWindow window) {
		win = window;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		runCommand(!EditBox.getDefault().isEnabled());
		return null;
	}
	
	public void run(IAction action) {
		boolean checked = !EditBox.getDefault().isEnabled();
		runCommand(checked);
		action.setChecked(checked);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void runCommand(boolean isChecked) {
		if (win == null)
			win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (!isChecked)
			releaseDecorators();
		else {
			getRegistry().setPartListener(win.getPartService(),new BoxDecoratorPartListener());
			IWorkbenchPart part = win.getActivePage().getActiveEditor();
			if (part != null)
				setVisible(part, true);
		}
		EditBox.getDefault().setEnabled(isChecked);
	}

	public void dispose() {
		releaseDecorators();
	}

	private void releaseDecorators() {
		if (win != null)
			getRegistry().removePartListener(win.getPartService());
		getRegistry().releaseDecorators();
	}

	protected BoxProviderRegistry getRegistry() {
		if (registry == null)
			registry = EditBox.getDefault().getProviderRegistry();
		return registry;
	}

	protected void setVisible(IWorkbenchPartReference partRef, boolean visible) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part != null) 
			setVisible(part, visible);
	}

	protected void setVisible(IWorkbenchPart part, boolean visible) {
		IBoxDecorator decorator = getRegistry().getDecorator(part);
		if (decorator == null && !visible)
			return;
		if (decorator == null) 
			decorator = decorate(part);
		if (decorator != null)
			decorator.enableUpdates(visible);
	}

	protected IBoxDecorator decorate(IWorkbenchPart part) {
		IBoxDecorator result = null;
		IBoxProvider provider = getRegistry().getBoxProvider(part);
		if (provider != null)
			result = provider.decorate(part);
		if (result != null)
			getRegistry().addDecorator(result, part);
		return result;
	}

	protected void undecorate(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part != null) {
			IBoxDecorator decorator = getRegistry().removeDecorator(part);
			if (decorator != null) {
				decorator.getProvider().releaseDecorator(decorator);
			}
		}
	}

	class BoxDecoratorPartListener implements IPartListener2 {

		public void partActivated(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			undecorate(partRef);
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
			setVisible(partRef, false);
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			setVisible(partRef, false);
			setVisible(partRef, true);
		}

		public void partOpened(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			setVisible(partRef, true);
		}
	}
}
