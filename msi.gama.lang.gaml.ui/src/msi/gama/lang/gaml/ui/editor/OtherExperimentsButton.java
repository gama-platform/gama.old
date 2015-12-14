/**
 * Created by drogoul, 4 déc. 2014
 *
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.*;
import java.util.List;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.*;
import msi.gama.lang.gaml.ui.XtextGui;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISyntacticElement;

/**
 * The class OtherExperimentsButton.
 *
 * @author drogoul
 * @since 4 déc. 2014
 *
 */
public class OtherExperimentsButton {

	GamlEditor editor;
	GamaToolbar2 parent;
	ToolItem menu;

	public OtherExperimentsButton(final GamlEditor editor, final GamaToolbar2 toolbar) {
		this.editor = editor;
		this.parent = toolbar;
		if ( XtextGui.EDITOR_SHOW_OTHER.getValue() ) {
			createButton();
		}
	}

	private void createButton() {

		// parent.sep(5, SWT.RIGHT);
		menu = parent.menu(IGamaColors.BLUE, "Other...", SWT.RIGHT);
		menu.getControl().setToolTipText("Run other experiments defined in models belonging to the same project");
		((FlatButton) menu.getControl()).addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Menu old = (Menu) menu.getData();
				menu.setData(null);
				if ( old != null ) {
					old.dispose();
				}
				Menu dropMenu = createExperimentsSubMenu();
				menu.setData(dropMenu);
				Rectangle rect = menu.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = menu.getControl().toDisplay(pt);
				dropMenu.setLocation(pt.x, pt.y);
				dropMenu.setVisible(true);
			}

		});
		// setVisible(XtextGui.EDITOR_SHOW_OTHER.getValue());
	}

	private final SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			String exp = (String) mi.getData("exp");
			if ( uri != null && exp != null ) {
				IModel model = editor.getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						ResourceSet rs = state.getResourceSet();
						GamlResource resource = (GamlResource) rs.getResource(uri, true);
						return new GamlModelBuilder() /* GamlModelBuilder.getInstance() */.compile(resource);
					}

				});
				if ( model == null ) { return; }
				GuiUtils.openSimulationPerspective();
				GAMA.runGuiExperiment(exp, model);
			}
		}
	};

	public Menu createExperimentsSubMenu() {
		Menu parentMenu = new Menu(this.parent);
		Map<URI, List<String>> map = grabProjectModelsAndExperiments();
		if ( map.isEmpty() ) {
			MenuItem nothing = new MenuItem(parentMenu, SWT.PUSH);
			nothing.setText("No experiments defined");
			nothing.setEnabled(false);
			return parentMenu;
		}
		for ( URI uri : map.keySet() ) {
			MenuItem modelItem = new MenuItem(parentMenu, SWT.CASCADE);
			modelItem.setText("Model " + URI.decode(uri.lastSegment()));
			modelItem.setImage(IGamaIcons.FILE_ICON.image());
			Menu expMenu = new Menu(modelItem);
			modelItem.setMenu(expMenu);
			List<String> expNames = map.get(uri);
			for ( String name : expNames ) {
				MenuItem expItem = new MenuItem(expMenu, SWT.PUSH);
				expItem.setText(name);
				expItem.setData("uri", uri);
				expItem.setData("exp", name);
				expItem.setImage(IGamaIcons.BUTTON_GUI.image());
				expItem.addSelectionListener(adapter);
			}
		}
		return parentMenu;
	}

	private Map<URI, List<String>> grabProjectModelsAndExperiments() {
		final Map<URI, List<String>> map = new LinkedHashMap();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				String platformString = resource.getURI().toPlatformString(true);
				IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
				IProject proj = myFile.getProject();
				// AD Addresses Issue 796 by passing null to the "without" parameter
				List<URI> resources = getAllGamaFilesInProject(proj, /* resource.getURI() */null);
				ResourceSet rs = editor.resourceSetProvider.get(proj);
				for ( URI uri : resources ) {
					GamlResource xr = (GamlResource) rs.getResource(uri, true);
					if ( xr.getErrors().isEmpty() ) {
						ISyntacticElement el = xr.getSyntacticContents();
						for ( ISyntacticElement ch : el.getChildren() ) {
							if ( ch.isExperiment() ) {
								if ( !map.containsKey(uri) ) {
									map.put(uri, new ArrayList());
								}
								map.get(uri).add(ch.getName());
							}
						}
					}
				}

			}
		});
		return map;
	}

	public static ArrayList<URI> getAllGamaFilesInProject(final IProject project, final URI without) {
		ArrayList<URI> allGamaFiles = new ArrayList();
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = project.getLocation();
		recursiveFindGamaFiles(allGamaFiles, path, myWorkspaceRoot, without);
		return allGamaFiles;
	}

	private static void recursiveFindGamaFiles(final ArrayList<URI> allGamaFiles, final IPath path,
		final IWorkspaceRoot myWorkspaceRoot, final URI without) {
		IContainer container = myWorkspaceRoot.getContainerForLocation(path);

		try {
			IResource[] iResources;
			iResources = container.members();
			for ( IResource iR : iResources ) {
				// for gama files
				if ( "gaml".equalsIgnoreCase(iR.getFileExtension()) ) {
					URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
					if ( !uri.equals(without) ) {
						allGamaFiles.add(uri);
					}
				}
				if ( iR.getType() == IResource.FOLDER ) {
					IPath tempPath = iR.getLocation();
					recursiveFindGamaFiles(allGamaFiles, tempPath, myWorkspaceRoot, without);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param showOtherEnabled
	 */
	public void setVisible(final boolean enabled) {
		if ( enabled ) {
			if ( menu != null ) { return; }
			createButton();
		} else {
			if ( menu == null ) { return; }
			menu.dispose();
			menu = null;
		}
		// menu.getControl().setVisible(enabled);
	}

}
