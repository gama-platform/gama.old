/*********************************************************************************************
 *
 * 'FileOpener.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Utility methods related to open file from different type of locations.
 * 
 * @author alruiz@google.com (Alex Ruiz), Alexis Drogoul (2018)
 */
public class FileOpener {

	static final IWorkbenchPage PAGE = WorkbenchHelper.getPage();
	static final URI WORKSPACE = URI.createURI(ResourcesPlugin.getWorkspace().getRoot().getLocationURI().toString(),
			false);

	/**
	 * Returns a best guess URI based on the target string and an optional URI
	 * specifying from where the relative URI should be run. If existingResource is
	 * null, then the root of the workspace is used as the relative URI
	 * 
	 * @param target
	 *            a String giving the path
	 * @param existingResource
	 *            the URI of the resource from which relative URIs should be
	 *            interpreted
	 * @author Alexis Drogoul, July 2018
	 * @return an URI or null if it cannot be determined.
	 */
	public static URI getURI(final String target, final URI existingResource) {
		if (target == null) {
			return null;
		}
		final IPath path = Path.fromOSString(target);
		final IFileStore file = EFS.getLocalFileSystem().getStore(path);
		final IFileInfo info = file.fetchInfo();
		if (info.exists()) {
			// We have an absolute file
			final URI fileURI = URI.createFileURI(target);
			return fileURI;
		} else {
			final URI first = URI.createURI(target, false);
			URI root;
			if (!existingResource.isPlatformResource()) {
				root = URI.createPlatformResourceURI(existingResource.toString(), false);
			} else {
				root = existingResource;
			}
			if (root == null) {
				root = WORKSPACE;
			}
			final URI iu = first.resolve(root);
			if (isFileExistingInWorkspace(iu)) {
				return iu;
			}
			return null;
		}
	}

	public static boolean isFileExistingInWorkspace(final URI uri) {
		if (uri == null) {
			return false;
		}
		final IFile file = getWorkspaceFile(uri);
		if (file != null) {
			return file.exists();
		}
		return false;
	}

	public static IFile getFile(final String path, final URI root) {
		final URI uri = getURI(path, root);
		if (uri != null) {
			if (uri.isPlatformResource()) {
				return getWorkspaceFile(uri);
			}
			if (uri.isFile()) {
				return getFileSystemFile(uri, root);
			}
		}
		return null;
	}

	public static IFile getFileSystemFile(final URI uri, final URI workspaceResource) {
		if (workspaceResource == null || !isFileExistingInWorkspace(workspaceResource)) {
			return null;
		}
		final IFile root = getWorkspaceFile(workspaceResource);
		final IProject project = root.getProject();
		if (!project.exists()) {
			return null;
		}
		final IFolder folder = project.getFolder(new Path("external"));
		if (!folder.exists()) {
			try {
				folder.create(true, true, null);
			} catch (final CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		final String uriString = uri.toFileString();
		// We try to find an existing file linking to this uri (in case it has been
		// renamed, for instance)
		final IFile[] result = new IFile[1];
		try {
			folder.accept((IResourceVisitor) resource -> {
				if (resource.isLinked()) {
					String p = resource.getLocation().toString();
					if (PlatformHelper.isWin32()) {
						// Bug in getLocation().toString() under Windows. The documentation states that
						// the returned string is platform independant, but it is not
						p = p.replace('/', '\\');
					}
					//System.out.println("URI : " + uriString + " | IPath : " + p);
					if (p.equals(uriString)) {
						result[0] = (IFile) resource;
						return false;
					}
				}
				return true;

			}, IResource.DEPTH_INFINITE, IResource.FILE);
		} catch (final CoreException e1) {
			e1.printStackTrace();
		}
		IFile file = result[0];
		if (file != null) {
			return file;
		}
		// We get the file with the same last name
		// If it already exists, we need to find it a new name as it doesnt point to the
		// same absolute file
		String fileName = URI.decode(uri.lastSegment());
		do {
			file = folder.getFile(fileName);
			fileName = "copy of " + fileName;
		} while (file.exists());

		final java.net.URI javaURI = java.net.URI.create(uri.toString());
		try {
			file.createLink(javaURI, IResource.NONE, null);
		} catch (final CoreException e) {
			return null;
		}
		return file;
	}

	public static IFile getWorkspaceFile(final URI uri) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IPath uriAsPath = new Path(URI.decode(uri.toString()));
		IFile file = root.getFile(uriAsPath);
		if (file != null && file.exists()) {
			return file;
		}

		final String uriAsText = uri.toPlatformString(true);
		final IPath path = uriAsText != null ? new Path(uriAsText) : null;
		if (path == null) {
			return null;
		}
		file = root.getFile(path);
		if (file != null && file.exists()) {
			return file;
		}
		return null;
	}

	public static IEditorPart openFile(final String path) {
		return openFile(path, null);
	}

	public static IEditorPart openFile(final String path, final URI root) {
		final URI uri = getURI(path, root);
		if (uri != null) {
			return openFile(uri);
		}
		return null;
	}

	public static IEditorPart openFile(final URI uri) {
		try {
			if (uri.isPlatformResource()) {
				return FileOpener.openFileInWorkspace(uri);
			}
			if (uri.isFile()) {
				return FileOpener.openFileInFileSystem(uri);
			}
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IEditorPart openFileInWorkspace(final URI uri) throws PartInitException {
		final IFile file = getWorkspaceFile(uri);
		if (file == null) {
			return null;
		}
		return IDE.openEditor(PAGE, file);
	}

	public static IEditorPart openFileInFileSystem(final URI uri) throws PartInitException {
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore(Path.fromOSString(uri.toFileString()));
		return IDE.openInternalEditorOnFileStore(PAGE, fileStore);
	}

}
