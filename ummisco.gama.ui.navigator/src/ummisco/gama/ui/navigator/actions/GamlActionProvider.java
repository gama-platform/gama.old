package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.navigator.contents.WrappedExperimentContent;
import ummisco.gama.ui.navigator.contents.WrappedSyntacticContent;

public class GamlActionProvider extends CommonActionProvider {

	WrappedSyntacticContent selection;
	SelectionListenerAction runAction, revealAction;

	public GamlActionProvider() {}

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		makeActions();
	}

	private void makeActions() {
		runAction = new SelectionListenerAction("Run...") {
			@Override
			public void run() {
				selection.handleDoubleClick();
			}

		};
		runAction.setId("run.experiment");
		runAction.setEnabled(true);
		revealAction = new SelectionListenerAction("Reveal...") {
			@Override
			public void run() {
				GAMA.getGui().editModel(null, selection.getElement().getElement());
			}
		};
		revealAction.setId("reveal.item");
		revealAction.setEnabled(true);
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		super.fillContextMenu(menu);
		if (selection == null)
			return;
		menu.add(new Separator());
		if (selection instanceof WrappedExperimentContent) {
			menu.appendToGroup("group.copy", runAction);
		}
		menu.appendToGroup("group.copy", revealAction);
	}

	@Override
	public void updateActionBars() {
		final StructuredSelection s = (StructuredSelection) getContext().getSelection();
		if (s.isEmpty()) {
			selection = null;
			return;
		}
		final Object o = s.getFirstElement();
		if (!(o instanceof WrappedSyntacticContent)) {
			selection = null;
			return;
		}
		selection = (WrappedSyntacticContent) o;
		runAction.selectionChanged(s);
		revealAction.selectionChanged(s);
	}

}
