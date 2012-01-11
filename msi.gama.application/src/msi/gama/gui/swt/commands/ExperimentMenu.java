/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.commands;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.descript.GamlDescriptIO;
import msi.gama.lang.gaml.ui.GamlResourceSet;
import msi.gama.lang.utils.Convert;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlException;
import msi.gaml.factories.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;

public class ExperimentMenu extends org.eclipse.jface.action.ContributionItem {

	public ExperimentMenu() {}

	public ExperimentMenu(final String id) {
		super(id);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	FileIdentifier file;

	// private void cleanUpInterface() {
	// GUI.getPage().saveAllEditors(true);
	// if ( GUI.isSimulationPerspective() ) {
	// GUI.hideParameterView();
	// GUI.eraseConsole();
	// } else if ( GUI.isBatchPerspective() ) {
	// GUI.hideView("msi.gama.application.view.BatchSummaryView");
	// }
	// /* Clear the list of views */
	// ContributionItem.clearDynamicDisplayList();
	// }

	@Override
	public void fill(final Menu menu, final int index) {

		try {
			waitForBuilder();
			// cleanUpInterface();
			identifyFile();
			// Convert.getDocMap(file.fileToRun, GamlResourceSet.get(file.fileToRun.getProject()));

			final IModel[] model = new IModel[] { identifyModel() };
			if ( file.isNull() && model[0] == null ) { return; }
			if ( model[0] == null ) {
				GuiUtils.waitStatus("Compiling model");

				try {
					model[0] =
						(IModel) DescriptionFactory.getModelFactory().compile(
							new ModelStructure(file.filePath, Convert.getDocMap(file.resource)));
				} catch (final GamlException e1) {
					e1.printStackTrace();
					MenuItem experiments = new MenuItem(menu, SWT.NONE);
					experiments.setText("No experiment available (select to see the errors)");
					experiments.setImage(SwtGui.noExperimentImage);
					experiments.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent evt) {
							GuiUtils.raise(e1);
						}

					});

					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
					MenuItem experiments = new MenuItem(menu, SWT.NONE);
					experiments.setText("No experiment available (interrupted)");
					experiments.setImage(SwtGui.noExperimentImage);
					return;
				} catch (final Exception e) {
					e.printStackTrace();
					MenuItem experiments = new MenuItem(menu, SWT.NONE);
					experiments.setText("No experiment available (errors)");
					experiments.setImage(SwtGui.noExperimentImage);
					experiments.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent evt) {
							GuiUtils.raise(e);
						}

					});
					return;

				}
			}
			int i = 0;
			MenuItem title = new MenuItem(menu, SWT.BOLD, i++);
			IExperiment current = GAMA.getExperiment();
			title.setText("Experiments defined for model " + model[0].getName());
			title.setEnabled(false);
			for ( IExperiment e : model[0].getExperiments() ) {
				final MenuItem exp = new MenuItem(menu, SWT.CHECK, i++);
				exp.setImage(SwtGui.experimentMenuImage);
				if ( current != null && current.getName().equals(e.getName()) &&
					current.getModel().getFileName().equals(e.getModel().getFileName()) ) {
					exp.setText("Reload '" + e.getName() + "'");
					exp.setSelection(true);
				} else {
					exp.setText("Load '" + e.getName() + "'");
				}
				exp.setData("EXP", e.getName());

				exp.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent evt) {
						String name = (String) exp.getData("EXP");
						GuiUtils.openSimulationPerspective();
						GAMA.newExperiment(name, model[0]);
					}

				});
			}
			if ( i < menu.getItemCount() && (menu.getItem(i).getStyle() & SWT.SEPARATOR) == 0 ) {
				new MenuItem(menu, SWT.SEPARATOR, i);
			}
			// experiments.setMenu(expMenu);
			GuiUtils.informStatus("Ready");
		} finally {
			GamlDescriptIO.getInstance().canRun(true);
		}
	}

	/**
	 * 
	 */
	private static void waitForBuilder() {
		// synchronize with GamlCompilCallback in case it is running

		GamlDescriptIO.getInstance().canRun(false);
		while (GamlDescriptIO.getInstance().isBuilding()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	private void identifyFile() {
		file = new FileIdentifier();
		ISelectionService selectionService = SwtGui.getWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if ( selection instanceof TextSelection ) {
			IEditorPart editorPart = SwtGui.getPage().getActiveEditor();
			if ( editorPart == null ) { return; }
			IEditorInput ed = editorPart.getEditorInput();
			if ( ed instanceof IFileEditorInput ) {
				IFileEditorInput input = (IFileEditorInput) ed;
				file.setFileToRun(input.getFile());
			} else if ( ed instanceof FileStoreEditorInput ) {
				file.filePath = ((FileStoreEditorInput) ed).getURI().getPath();
			}
		} else if ( selection instanceof IStructuredSelection ) {
			if ( selection.isEmpty() ) { return; }
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if ( o instanceof IFile ) {
				file.setFileToRun((IFile) o);
				return;
			}
		}
	}

	private IModel identifyModel() {
		if ( SwtGui.getPage().getActiveEditor() != null ) { return null; }
		if ( GuiUtils.isSimulationPerspective() ) {
			if ( GAMA.getExperiment() != null ) { return GAMA.getExperiment().getModel(); }
		}
		return null;
	}

	private static class FileIdentifier {

		String filePath;
		IFile fileToRun;
		Resource resource;

		void setFileToRun(final IFile fileToRun) {
			this.fileToRun = fileToRun;
			if ( fileToRun != null && filePath == null ) {
				filePath = GamlDescriptIO.getPath(fileToRun);
			}
			if ( fileToRun != null ) {
				ResourceSet rs = GamlResourceSet.get(fileToRun.getProject());
				String p = fileToRun.getFullPath().toString();
				URI u = URI.createPlatformResourceURI(p, true);
				resource = rs.getResource(u, true);
			}
		}

		boolean isNull() {
			return fileToRun == null && (filePath == null || filePath.trim().length() == 0);
		}

	}

}
