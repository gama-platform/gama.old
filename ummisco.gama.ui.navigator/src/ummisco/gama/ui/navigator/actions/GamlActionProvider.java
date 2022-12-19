/*******************************************************************************************************
 *
 * GamlActionProvider.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.GenericFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import ummisco.gama.ui.navigator.contents.WrappedExperimentContent;
import ummisco.gama.ui.navigator.contents.WrappedSyntacticContent;

/**
 * The Class GamlActionProvider.
 */
public class GamlActionProvider extends CommonActionProvider {

	/** The selection. */
	WrappedSyntacticContent selection;
	
	/** The reveal action. */
	SelectionListenerAction runAction, revealAction, setStartupAction;

	/**
	 * Instantiates a new gaml action provider.
	 */
	public GamlActionProvider() {}

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		makeActions();
	}

	/**
	 * Make actions.
	 */
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
		
		setStartupAction = new SelectionListenerAction("Set as startup...") {
			
			@Override
			protected boolean updateSelection(IStructuredSelection selection) {
				if (!super.updateSelection(selection)) return false;
				setText("Set as startup...");
				if (GamaPreferences.Interface.CORE_STARTUP_MODEL.getValue() && selection.getFirstElement() instanceof WrappedSyntacticContent) {
					String path = FileUtils.constructAbsoluteFilePath(null,
							((WrappedSyntacticContent) selection.getFirstElement()).getFile().getResource()
									.getLocation().toOSString(),
							true);
					if (path != null) {
						IGamaFile file_old = GamaPreferences.Interface.CORE_DEFAULT_MODEL.getValue();
						if (file_old != null && file_old.exists(null) && file_old.getOriginalPath().equals(path)) {
							setText("UnSet as startup...");
						}
					}
				}
				return true;
			}

			@Override
			public void run() {
				String path = FileUtils.constructAbsoluteFilePath(null,
						selection.getFile().getResource().getLocation().toOSString(), true);
				if (path != null) {

					IGamaFile file_old = GamaPreferences.Interface.CORE_DEFAULT_MODEL.getValue();
					if (GamaPreferences.Interface.CORE_STARTUP_MODEL.getValue() && file_old != null && file_old.exists(null) && file_old.getOriginalPath().equals(path)) { 
							GamaPreferences.Interface.CORE_STARTUP_MODEL.set(false).save(); 
					}else {
						GamaPreferences.Interface.CORE_STARTUP_MODEL.set(true).save();						
						IGamaFile file = Files.from(null, path);
						GamaPreferences.Interface.CORE_DEFAULT_MODEL.setValue(null, file);
						GamaPreferences.Interface.CORE_DEFAULT_MODEL.save();
						GamaPreferences.Interface.CORE_DEFAULT_EXPERIMENT.set(selection.getElement().getName()).save();
					}
				}
			}

		};
		setStartupAction.setId("startup.experiment");
		setStartupAction.setEnabled(true);
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		super.fillContextMenu(menu);
		if (selection == null)
			return;
		menu.add(new Separator());
		if (selection instanceof WrappedExperimentContent) {
			menu.appendToGroup("group.copy", runAction);
			menu.appendToGroup("group.copy", setStartupAction);
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
		setStartupAction.selectionChanged(s);
		revealAction.selectionChanged(s);
	}

}
