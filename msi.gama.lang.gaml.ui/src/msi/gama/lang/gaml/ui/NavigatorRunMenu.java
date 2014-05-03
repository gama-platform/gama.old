/*********************************************************************************************
 * 
 * 
 * 'NavigatorRunMenu.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui;

import java.util.*;
import java.util.List;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.navigator.VirtualFolder;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.*;
import msi.gama.runtime.GAMA;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ISyntacticElement;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;

public class NavigatorRunMenu extends ContributionItem implements IWorkbenchContribution {

	// private final GamlJavaValidator validator;

	public NavigatorRunMenu() {
		// validator = GamlActivator.getInstance().getInjector("gaml").getInstance(GamlJavaValidator.class);
	}

	public NavigatorRunMenu(final String id) {
		super(id);
		// validator = GamlActivator.getInstance().getInjector("gaml").getInstance(GamlJavaValidator.class);
	}

	private List<URI> getSelection() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if ( window == null ) { return Collections.EMPTY_LIST; }
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		Object firstElement = selection.getFirstElement();
		if ( firstElement == null ) { return getAllGamaModelsIn(ResourcesPlugin.getWorkspace().getRoot()); }
		List<URI> result = getAllGamaModelsIn(firstElement);
		if ( !result.isEmpty() ) {
			if ( firstElement instanceof IResource ) {
				result.add(0, URI.createPlatformResourceURI(((IResource) firstElement).getFullPath().toString(), true));
			} else {
				result.add(0, null);
			}
		}
		return result;
	}

	private static void recursiveFindGamaFiles(final List<URI> uris, final IContainer container) {

		try {
			IResource[] iResources;
			iResources = container.members();
			for ( IResource iR : iResources ) {
				// for gama files
				if ( iR.getType() == IResource.FILE && "gaml".equalsIgnoreCase(iR.getFileExtension()) ) {
					if ( ((IFile) iR).findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO) == IMarker.SEVERITY_ERROR ) {
						continue;
					}

					URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
					uris.add(uri);
				} else if ( iR instanceof IContainer ) {
					recursiveFindGamaFiles(uris, (IContainer) iR);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param workspace
	 * @return
	 */
	private List<URI> getAllGamaModelsIn(final Object container) {
		List<URI> uris = new ArrayList();
		if ( container instanceof IContainer ) {
			recursiveFindGamaFiles(uris, (IContainer) container);
		} else if ( container instanceof VirtualFolder ) {
			Object[] children = ((VirtualFolder) container).getChildren();
			for ( Object c : children ) {
				if ( c instanceof IContainer ) {
					recursiveFindGamaFiles(uris, (IContainer) c);
				}
			}
		} else if ( container instanceof IFile ) {
			try {
				if ( ((IFile) container).findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO) == IMarker.SEVERITY_ERROR ) { return Collections.EMPTY_LIST; }
				URI uri = URI.createPlatformResourceURI(((IFile) container).getFullPath().toString(), true);
				uris.add(uri);
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
		return uris;
	}

	@Override
	public void fill(final Menu parent, final int index) {
		StringBuilder sb = new StringBuilder();
		List<URI> uris = getSelection();
		URI sourceURI = null;
		Map<URI, List<String>> map = Collections.EMPTY_MAP;
		if ( !uris.isEmpty() ) {
			sourceURI = uris.remove(0);
			map = grabExperiments(uris);
		}
		Set<String> sourceSegments = new HashSet();
		if ( sourceURI != null ) {
			String[] segmentsArray = sourceURI.segments();
			for ( int i = 0; i < segmentsArray.length; i++ ) {
				segmentsArray[i] = URI.decode(segmentsArray[i]);
			}
			sourceSegments = new LinkedHashSet(Arrays.asList(segmentsArray));
		}
		if ( map.isEmpty() ) {
			MenuItem nothing = new MenuItem(parent, SWT.PUSH);
			nothing.setText("No experiments defined");
			nothing.setEnabled(false);
			return;
		}
		boolean onlyOneFile = map.size() == 1;
		for ( URI uri : map.keySet() ) {
			List<String> expNames = map.get(uri);
			// if ( expNames.size() == 1 ) { // No need to create a sub-menu
			// MenuItem expItem = new MenuItem(parent, SWT.PUSH);
			// sb.setLength(0);
			// sb.append(expNames.get(0));
			// if ( !onlyOneFile ) {
			// sb.append(" in ");
			// for ( int i = 1; i < uri.segmentCount(); i++ ) {
			// String s = URI.decode(uri.segment(i));
			// if ( !"models".equals(s) && !sourceSegments.contains(s) ) {
			// sb.append(s);
			// sb.append('>');
			// }
			// }
			// sb.setLength(sb.length() - 1);
			// }
			// expItem.setText(sb.toString());
			// expItem.setData("uri", uri);
			// expItem.setData("exp", expNames.get(0));
			// expItem.setImage(IGamaIcons.NAVIGATOR_RUN.image());
			// expItem.addSelectionListener(adapter);
			// } else {
			MenuItem modelItem = new MenuItem(parent, SWT.CASCADE);

			sb.setLength(0);
			for ( int i = 1; i < uri.segmentCount() - 1; i++ ) {
				String s = URI.decode(uri.segment(i));
				if ( !"models".equals(s) && !sourceSegments.contains(s) ) {
					sb.append(s);
					sb.append(" > ");
				}
			}
			modelItem.setText(URI.decode(sb.toString() + uri.lastSegment()));
			// modelItem.setImage(IGamaIcons.FILE_ICON.image());
			// modelItem.setEnabled(false);
			Menu expMenu = new Menu(modelItem);
			modelItem.setMenu(expMenu);

			for ( String name : expNames ) {
				MenuItem expItem = new MenuItem(expMenu, SWT.PUSH);
				expItem.setText(name);
				expItem.setData("uri", uri);
				expItem.setData("exp", name);
				expItem.setImage(IGamaIcons.NAVIGATOR_RUN.image());
				expItem.addSelectionListener(adapter);
			}
			// }

		}
	}

	private final SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			String exp = (String) mi.getData("exp");
			if ( uri != null && exp != null ) {
				ResourceSet rs = new SynchronizedXtextResourceSet();
				GamlResource resource = (GamlResource) rs.getResource(uri, true);
				IModel model = GamlModelBuilder.getInstance().compile(resource);
				if ( model == null ) { return; }
				GuiUtils.openSimulationPerspective();
				GAMA.controller.newExperiment(exp, model);
			}
		}
	};

	private Map<URI, List<String>> grabExperiments(final List<URI> uris) {
		final Map<URI, List<String>> map = new TOrderedHashMap();
		ResourceSet rs = new SynchronizedXtextResourceSet();
		for ( URI uri : uris ) {
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

		return map;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void initialize(final IServiceLocator serviceLocator) {}

}
