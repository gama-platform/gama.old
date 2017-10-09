/*********************************************************************************************
 *
 * 'NavigatorContentProvider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.WorkbenchContentProvider;

import msi.gama.common.GamlFileExtension;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.WrappedSyntacticContent.WrappedExperimentContent;
import ummisco.gama.ui.navigator.WrappedSyntacticContent.WrappedModelContent;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class NavigatorContentProvider extends WorkbenchContentProvider {

	private TopLevelFolder[] virtualFolders;

	public NavigatorContentProvider() {

	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getParent(); }
		if (element instanceof IProject) {
			for (final TopLevelFolder folder : virtualFolders) {
				if (folder.accepts((IProject) element)) { return folder; }
			}
		}
		if (element instanceof IFile && FileMetaDataProvider.SHAPEFILE_SUPPORT_CT_ID
				.equals(FileMetaDataProvider.getContentTypeId((IFile) element))) {
			final IResource r = FileMetaDataProvider.shapeFileSupportedBy((IFile) element);
			if (r != null) { return r; }
		}
		return super.getParent(element);
	}

	@Override
	public Object[] getChildren(final Object p) {
		if (p instanceof NavigatorRoot) {
			if (virtualFolders == null) {
				initializeVirtualFolders(p);
			}
			return virtualFolders;
		}
		if (p instanceof VirtualContent) { return ((VirtualContent) p).getNavigatorChildren(); }
		if (p instanceof IFile) {

			final String ctid = FileMetaDataProvider.getContentTypeId((IFile) p);
			if (ctid.equals(FileMetaDataProvider.GAML_CT_ID)) {

				final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(p, false, true);
				if (metaData instanceof GamlFileInfo) {
					final GamlFileInfo info = (GamlFileInfo) metaData;
					final List l = new ArrayList<>();

					final ISyntacticElement element = GAML
							.getContents(URI.createPlatformResourceURI(((IFile) p).getFullPath().toOSString(), true));
					if (element != null) {
						if (!GamlFileExtension.isExperiment(((IFile) p).getFullPath().toOSString())) {
							l.add(new WrappedModelContent((IFile) p, element));
						}
						element.visitExperiments(exp -> l.add(new WrappedExperimentContent((IFile) p, exp)));
					}

					// for (final String s : info.getExperiments()) {
					// l.add(new WrappedExperiment((IFile) p, s));
					// }
					if (!info.getImports().isEmpty()) {
						final WrappedFolder wf = new WrappedFolder((IFile) p, info.getImports(), "Imports");
						if (wf.getNavigatorChildren().length > 0)
							l.add(wf);
					}
					if (!info.getUses().isEmpty()) {
						final WrappedFolder wf = new WrappedFolder((IFile) p, info.getUses(), "Uses");
						if (wf.getNavigatorChildren().length > 0)
							l.add(wf);
					}
					// addPluginsTo((IFile) p, l);
					return l.toArray();
				}
				return VirtualContent.EMPTY;

			} else if (ctid.equals(FileMetaDataProvider.SHAPEFILE_CT_ID)) {
				try {
					final IContainer folder = ((IFile) p).getParent();
					final List<IResource> sub = new ArrayList<>();
					for (final IResource r : folder.members()) {
						if (r instanceof IFile && FileMetaDataProvider.isSupport((IFile) p, (IFile) r)) {
							sub.add(r);
						}
					}
					return sub.toArray();
				} catch (final CoreException e) {
					e.printStackTrace();
					return super.getChildren(p);
				}
			}

		}
		return super.getChildren(p);
	}

	/**
	 * @param p
	 * @param l
	 */
	// private void addPluginsTo(final IFile f, final List l) {
	// final IProject p = f.getProject();
	// IPath path = f.getProjectRelativePath();
	// final String s = ".metadata/" + path.toPortableString() + ".meta";
	// path = Path.fromPortableString(s);
	// final IResource r = p.findMember(path);
	// if (r == null || !(r instanceof IFile)) {
	// return;
	// }
	// final IFile m = (IFile) r;
	// try {
	// final InputStream is = m.getContents();
	// final BufferedReader in = new BufferedReader(new InputStreamReader(is));
	// final GamlProperties props = new GamlProperties(in);
	// final Set<String> contents = props.get(GamlProperties.PLUGINS);
	//
	// if (contents == null || contents.isEmpty()) {
	// return;
	// }
	// l.add(new WrappedPlugins(f, contents, "Requires"));
	// } catch (final CoreException e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).hasChildren(); }
		if (element instanceof NavigatorRoot) { return true; }
		if (element instanceof IFile) {
			final String ext = FileMetaDataProvider.getContentTypeId((IFile) element);
			return (FileMetaDataProvider.GAML_CT_ID.equals(ext) || FileMetaDataProvider.SHAPEFILE_CT_ID.equals(ext))
					&& getChildren(element).length > 0;
		}
		return super.hasChildren(element);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.virtualFolders = null;
	}

	@Override
	protected void processDelta(final IResourceDelta delta) {
		super.processDelta(delta);

	}

	private void initializeVirtualFolders(final Object parentElement) {
		virtualFolders = new TopLevelFolder[] { new TestModelsFolder(parentElement, "Test models"),
				new UserProjectsFolder(parentElement, "User models"),
				new PluginsModelsFolder(parentElement, "Plugin models"),
				new ModelsLibraryFolder(parentElement, "Library models") };
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		super.inputChanged(viewer, null, ResourcesPlugin.getWorkspace());
	}

}
