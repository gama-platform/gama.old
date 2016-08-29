/**
 * Created by drogoul, 4 déc. 2014
 *
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlModelBuilder;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.ISyntacticElement.SyntacticVisitor;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

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
		if (AutoStartup.EDITOR_SHOW_OTHER.getValue()) {
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
				final Menu old = (Menu) menu.getData();
				menu.setData(null);
				if (old != null) {
					old.dispose();
				}
				final Menu dropMenu = createExperimentsSubMenu();
				menu.setData(dropMenu);
				final Rectangle rect = menu.getBounds();
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
			final MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			final String exp = (String) mi.getData("exp");
			if (uri != null && exp != null) {
				final IModel model = editor.getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						final ResourceSet rs = state.getResourceSet();
						final GamlResource resource = (GamlResource) rs.getResource(uri, true);
						return new GamlModelBuilder()
								/* GamlModelBuilder.getInstance() */.compile(resource);
					}

				});
				if (model == null) {
					return;
				}
				// if (GAMA.getGui().openSimulationPerspective(model, exp,
				// true))
				GAMA.runGuiExperiment(exp, model);
			}
		}
	};

	public Menu createExperimentsSubMenu() {
		final Menu parentMenu = new Menu(this.parent);
		final Map<URI, List<String>> map = grabProjectModelsAndExperiments();
		if (map.isEmpty()) {
			final MenuItem nothing = new MenuItem(parentMenu, SWT.PUSH);
			nothing.setText("No experiments defined");
			nothing.setEnabled(false);
			return parentMenu;
		}
		for (final URI uri : map.keySet()) {
			final MenuItem modelItem = new MenuItem(parentMenu, SWT.CASCADE);
			modelItem.setText("Model " + URI.decode(uri.lastSegment()));
			modelItem.setImage(IGamaIcons.FILE_ICON.image());
			final Menu expMenu = new Menu(modelItem);
			modelItem.setMenu(expMenu);
			final List<String> expNames = map.get(uri);
			for (final String name : expNames) {
				final MenuItem expItem = new MenuItem(expMenu, SWT.PUSH);
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
				final String platformString = resource.getURI().toPlatformString(true);
				final IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
				final IProject proj = myFile.getProject();
				// AD Addresses Issue 796 by passing null to the "without"
				// parameter
				final List<URI> resources = getAllGamaFilesInProject(proj, null);
				final ResourceSet rs = editor.resourceSetProvider.get(proj);
				for (final URI uri : resources) {
					final GamlResource xr = (GamlResource) rs.getResource(uri, true);
					if (xr.getErrors().isEmpty()) {
						final ISyntacticElement el = xr.getSyntacticContents();
						if (el != null)
							el.visitExperiments(new SyntacticVisitor() {

								@Override
								public void visit(final ISyntacticElement element) {

									if (!map.containsKey(uri)) {
										map.put(uri, new ArrayList());
									}
									map.get(uri).add(element.getName());

								}
							});
					}
				}

			}
		});
		return map;
	}

	public static ArrayList<URI> getAllGamaFilesInProject(final IProject project, final URI without) {
		final ArrayList<URI> allGamaFiles = new ArrayList();
		final IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IPath path = project.getLocation();
		recursiveFindGamaFiles(allGamaFiles, path, myWorkspaceRoot, without);
		return allGamaFiles;
	}

	private static void recursiveFindGamaFiles(final ArrayList<URI> allGamaFiles, final IPath path,
			final IWorkspaceRoot myWorkspaceRoot, final URI without) {
		final IContainer container = myWorkspaceRoot.getContainerForLocation(path);

		try {
			final IResource[] iResources = container.members();
			if (iResources != null)
				for (final IResource iR : iResources) {
					// for gama files
					if ("gaml".equalsIgnoreCase(iR.getFileExtension())) {
						final URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
						if (!uri.equals(without)) {
							allGamaFiles.add(uri);
						}
					}
					if (iR.getType() == IResource.FOLDER) {
						final IPath tempPath = iR.getLocation();
						recursiveFindGamaFiles(allGamaFiles, tempPath, myWorkspaceRoot, without);
					}
				}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param showOtherEnabled
	 */
	public void setVisible(final boolean enabled) {
		if (enabled) {
			if (menu != null) {
				return;
			}
			createButton();
		} else {
			if (menu == null) {
				return;
			}
			menu.dispose();
			menu = null;
		}
		// menu.getControl().setVisible(enabled);
	}

}
