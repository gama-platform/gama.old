/*******************************************************************************************************
 *
 * FileUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
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

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.ext.webb.Webb;
import msi.gama.ext.webb.WebbException;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.CacheLocationProvider;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The class FileUtils.
 *
 * @author drogoul
 * @since 20 dec. 2011
 *
 */
@SuppressWarnings ("deprecation")
public class FileUtils {

	/** The web. */
	public static ThreadLocal<Webb> WEB = ThreadLocal.withInitial(Webb::create);

	/** The Constant URL_SEPARATOR_REPLACEMENT. */
	public static final String URL_SEPARATOR_REPLACEMENT = "+_+";

	/** The Constant COPY_OF. */
	public static final String COPY_OF = "copy of ";

	/** The Constant HOME. */
	public static final String HOME = "~";

	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "/";

	/** The Constant CACHE_FOLDER_PATH. */
	public static final IPath CACHE_FOLDER_PATH = new Path(".cache");

	/** The Constant EXTERNAL_FOLDER_PATH. */
	public static final IPath EXTERNAL_FOLDER_PATH = new Path("external");

	/** The root. */
	static IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();

	/** The file system. */
	static IFileSystem FILE_SYSTEM = EFS.getLocalFileSystem();

	/** The user home. */
	static String USER_HOME = System.getProperty("user.home");

	/** The Constant WORKSPACE_URI. */
	static final URI WORKSPACE_URI = URI.createURI(ROOT.getLocationURI().toString(), false);

	/** The Constant CACHE. */
	public static final File CACHE;

	static {
		DEBUG.OFF();
		CACHE = new File(ROOT.getLocation().toFile().getAbsolutePath() + SEPARATOR + CACHE_FOLDER_PATH.toString());
		if (!CACHE.exists()) { CACHE.mkdirs(); }
		try {
			ROOT.getPathVariableManager().setValue("CACHE_LOC", ROOT.getLocation().append(CACHE_FOLDER_PATH));
		} catch (final CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if is absolute path.
	 *
	 * @param filePath
	 *            the file path
	 *
	 * @return true, if is absolute path
	 */
	static boolean isAbsolutePath(final String filePath) {
		// Fixes #2456
		return Paths.get(filePath).isAbsolute();
	}

	// Add a thin layer of workspace-based searching in order to resolve linked
	// resources.
	/**
	 * Construct absolute file path.
	 *
	 * @param scope
	 *            the scope
	 * @param filePath
	 *            the file path
	 * @param mustExist
	 *            the must exist
	 * @return the string
	 */
	// Should be able to catch most of the calls to relative resources as well
	static public String constructAbsoluteFilePath(final IScope scope, final String filePath, final boolean mustExist) {
		String fp;
		if (filePath.startsWith(HOME)) {
			fp = filePath.replaceFirst(HOME, USER_HOME);
		} else {
			fp = filePath;
		}
		if (isAbsolutePath(fp)) {
			URI modelBase = null;
			if (scope != null) {
				final IModel m = scope.getModel();
				if (m != null) { modelBase = m.getURI(); }
			}
			final String file = findOutsideWorkspace(fp, modelBase, mustExist);
			if (file != null) // DEBUG.OUT("Hit with EFS-based search: " + file);
				return file;
		}
		if (scope != null) {
			final IExperimentAgent a = scope.getExperiment();
			// No need to search more if the experiment is null
			if (a == null) return fp;
			if (!a.isHeadless()) {
				// Necessary to ask the workspace for the containers as projects might be linked
				final List<IContainer> paths = a.getWorkingPaths().stream()
						.map(s -> ROOT.findContainersForLocation(new Path(s))[0]).collect(toList());
				for (final IContainer folder : paths) {
					final String file = findInWorkspace(fp, folder, mustExist);
					if (file != null) {
						DEBUG.OUT("Hit with workspace-based search: " + file);
						return file;
					}
				}
			}
		}

		DEBUG.OUT("Falling back to the old JavaIO based search");
		return OldFileUtils.constructAbsoluteFilePathAlternate(scope, fp, mustExist);
	}

	/**
	 * Find in workspace.
	 *
	 * @param fp
	 *            the fp
	 * @param container
	 *            the container
	 * @param mustExist
	 *            the must exist
	 * @return the string
	 */
	private static String findInWorkspace(final String fp, final IContainer container, final boolean mustExist) {
		final IPath full = container.getFullPath().append(fp);
		IResource file = ROOT.getFile(full);
		if (!file.exists()) {
			// Might be a folder we're looking for
			file = ROOT.getFolder(full);
		}
		if (!file.exists() && mustExist) return null;
		final IPath loc = file.getLocation();
		// cf. #2997
		if (loc == null) return fp;
		return loc.toString();
		// getLocation() works for regular and linked files
	}

	/**
	 * Find outside workspace.
	 *
	 * @param fp
	 *            the fp
	 * @param modelBase
	 *            the model base
	 * @param mustExist
	 *            the must exist
	 * @return the string
	 */
	private static String findOutsideWorkspace(final String fp, final URI modelBase, final boolean mustExist) {
		if (!mustExist) return fp;
		final IFileStore file = FILE_SYSTEM.getStore(new Path(fp));
		final IFileInfo info = file.fetchInfo();
		if (info.exists()) {
			final IFile linkedFile = createLinkToExternalFile(fp, modelBase);
			if (linkedFile == null) return fp;
			return linkedFile.getLocation().toFile().getAbsolutePath();
		}
		return null;
	}

	/**
	 * Creates the link to external file.
	 *
	 * @param path
	 *            the path
	 * @param workspaceResource
	 *            the workspace resource
	 * @return the i file
	 */
	public static IFile createLinkToExternalFile(final String path, final URI workspaceResource) {
		// Always try to return the full file, without creating a link, if the file
		// happens to be in the workspace
		// (manageable by it)
		final IPath filePath = new Path(path);
		final IFile[] resources = ROOT.findFilesForLocation(filePath);
		if (resources.length > 0) return resources[0];

		final IFolder folder = createExternalFolder(workspaceResource);
		if (folder == null) return null;
		// We try to find an existing file linking to this uri (in case it has been
		// renamed, for instance)
		IFile file = findExistingLinkedFile(folder, path);
		if (file != null) return file;
		// We get the file with the same last name
		// If it already exists, we need to find it a new name as it doesnt point to the
		// same absolute file
		String fileName = new Path(path).lastSegment();
		final int i = fileName.lastIndexOf(URL_SEPARATOR_REPLACEMENT);
		if (i > -1) { fileName = fileName.substring(i + URL_SEPARATOR_REPLACEMENT.length()); }
		file = correctlyNamedFile(folder, fileName);
		return createLinkedFile(path, file);
	}

	/**
	 * Returns a best guess URI based on the target string and an optional URI specifying from where the relative URI
	 * should be run. If existingResource is null, then the root of the workspace is used as the relative URI
	 *
	 * @param target
	 *            a String giving the path
	 * @param existingResource
	 *            the URI of the resource from which relative URIs should be interpreted
	 * @author Alexis Drogoul, July 2018
	 * @return an URI or null if it cannot be determined.
	 */
	public static URI getURI(final String target, final URI existingResource) {
		if (target == null) return null;
		try {
			final IPath path = Path.fromOSString(target);
			final IFileStore file = EFS.getLocalFileSystem().getStore(path);
			final IFileInfo info = file.fetchInfo();
			if (info.exists()) return URI.createFileURI(target);
			final URI first = URI.createURI(target, false);
			URI root;
			if (!existingResource.isPlatformResource()) {
				root = URI.createPlatformResourceURI(existingResource.toString(), false);
			} else {
				root = existingResource;
			}
			if (root == null) { root = WORKSPACE_URI; }
			final URI iu = first.resolve(root);
			if (isFileExistingInWorkspace(iu)) return iu;
			return null;
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Checks if is file existing in workspace.
	 *
	 * @param uri
	 *            the uri
	 * @return true, if is file existing in workspace
	 */
	public static boolean isFileExistingInWorkspace(final URI uri) {
		if (uri == null) return false;
		final IFile file = getWorkspaceFile(uri);
		if (file != null) return file.exists();
		return false;
	}

	/**
	 * Gets the file.
	 *
	 * @param path
	 *            the path
	 * @param root
	 *            the root
	 * @param mustExist
	 *            the must exist
	 * @return the file
	 */
	public static IFile getFile(final String path, final URI root, final boolean mustExist) {
		final URI uri = getURI(path, root);
		if (uri != null) {
			if (uri.isPlatformResource()) return getWorkspaceFile(uri);
			return createLinkToExternalFile(path, root);
		}
		return null;
	}

	// public static IFile linkAndGetExternalFile(final URI uri, final URI
	// workspaceResource) {
	// final String path = URI.decode(uri.isFile() ? uri.toFileString() :
	// uri.toString());
	// return linkAndGetExternalFile(URI.decode(path), workspaceResource);
	// }

	/**
	 * Creates the linked file.
	 *
	 * @param path
	 *            the path
	 * @param file
	 *            the file
	 * @return the i file
	 */
	private static IFile createLinkedFile(final String path, final IFile file) {
		java.net.URI resolvedURI = null;
		final java.net.URI javaURI = URIUtil.toURI(path);// new java.io.File(path).toURI();

		try {
			resolvedURI = ROOT.getPathVariableManager().convertToRelative(javaURI, true, null);
		} catch (final CoreException e1) {
			resolvedURI = javaURI;
		}
		try {
			file.createLink(resolvedURI, IResource.NONE, null);
		} catch (final CoreException e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}

	/**
	 * Correctly named file.
	 *
	 * @param folder
	 *            the folder
	 * @param fileName
	 *            the file name
	 * @return the i file
	 */
	private static IFile correctlyNamedFile(final IFolder folder, final String fileName) {
		IFile file;
		String fn = fileName;
		do {
			file = folder.getFile(fn);
			fn = COPY_OF + fn;
		} while (file.exists());
		return file;
	}

	/**
	 * Find existing linked file.
	 *
	 * @param folder
	 *            the folder
	 * @param name
	 *            the name
	 * @return the i file
	 */
	private static IFile findExistingLinkedFile(final IFolder folder, final String name) {
		final IFile[] result = new IFile[1];
		try {
			folder.accept((IResourceVisitor) resource -> {
				if (resource.isLinked()) {
					final String p = resource.getLocation().toString();
					if (p.equals(name)) {
						result[0] = (IFile) resource;
						return false;
					}
				}
				return true;

			}, IResource.DEPTH_INFINITE, IResource.FILE);
		} catch (final CoreException e1) {
			e1.printStackTrace();
		}
		return result[0];
	}

	/**
	 * Creates the external folder.
	 *
	 * @param workspaceResource
	 *            the workspace resource
	 * @return the i folder
	 */
	private static IFolder createExternalFolder(final URI workspaceResource) {
		if (workspaceResource == null || !isFileExistingInWorkspace(workspaceResource)) return null;
		final IFile root = getWorkspaceFile(workspaceResource);
		final IProject project = root.getProject();
		if (!project.exists()) return null;
		final IFolder folder = project.getFolder(EXTERNAL_FOLDER_PATH);
		if (!folder.exists()) {
			try {
				folder.create(true, true, null);
			} catch (final CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		return folder;
	}

	/**
	 * Gets the workspace file.
	 *
	 * @param uri
	 *            the uri
	 * @return the workspace file
	 */
	public static IFile getWorkspaceFile(final URI uri) {
		final IPath uriAsPath = new Path(URI.decode(uri.toString()));
		IFile file;
		try {
			file = ROOT.getFile(uriAsPath);
		} catch (final Exception e1) {
			return null;
		}
		if (file != null && file.exists()) return file;
		final String uriAsText = uri.toPlatformString(true);
		final IPath path = uriAsText != null ? new Path(uriAsText) : null;
		if (path == null) return null;
		try {
			file = ROOT.getFile(path);
		} catch (final Exception e) {
			return null;
		}
		if (file != null && file.exists()) return file;
		return null;
	}

	/**
	 * Construct absolute temp file path.
	 *
	 * @param scope
	 *            the scope
	 * @param url
	 *            the url
	 * @return the string
	 */
	public static String constructAbsoluteTempFilePath(final IScope scope, final URL url) {
		return CACHE.getAbsolutePath() + SEPARATOR + url.getHost() + URL_SEPARATOR_REPLACEMENT
				+ url.getPath().replace(SEPARATOR, URL_SEPARATOR_REPLACEMENT);

	}

	/**
	 * Construct relative temp file path.
	 *
	 * @param scope
	 *            the scope
	 * @param url
	 *            the url
	 * @return the string
	 */
	private static String constructRelativeTempFilePath(final IScope scope, final URL url) {
		return "" + CacheLocationProvider.NAME + "" + SEPARATOR + url.getHost() + URL_SEPARATOR_REPLACEMENT
				+ url.getPath().replace(SEPARATOR, URL_SEPARATOR_REPLACEMENT);

	}

	/**
	 * Clean cache.
	 */
	public static void cleanCache() {
		if (GamaPreferences.External.CORE_HTTP_EMPTY_CACHE.getValue()) {
			final File[] files = CACHE.listFiles();
			if (files != null) {
				for (final File f : files) {
					if (!f.isDirectory()) {
						try {
							f.delete();
						} catch (final Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if is directory or null external file.
	 *
	 * @param path
	 *            the path
	 * @return true, if is directory or null external file
	 */
	public static boolean isDirectoryOrNullExternalFile(final String path) {
		final IFileStore external = FILE_SYSTEM.getStore(new Path(path));
		final IFileInfo info = external.fetchInfo();
		if (info.isDirectory() || !info.exists()) return true;
		return false;
	}

	/**
	 * Fetch to temp file.
	 *
	 * @param scope
	 *            the scope
	 * @param url
	 *            the url
	 * @return the string
	 */
	@SuppressWarnings ("deprecation")
	public static String fetchToTempFile(final IScope scope, final URL url) {
		String pathName = constructRelativeTempFilePath(scope, url);
		final String urlPath = url.toExternalForm();
		final String status = "Downloading file " + urlPath.substring(urlPath.lastIndexOf(SEPARATOR));
		scope.getGui().getStatus(scope).beginSubStatus(status);
		final Webb web = WEB.get();
		try {
			try (InputStream in = web.get(urlPath).ensureSuccess()
					.connectTimeout(GamaPreferences.External.CORE_HTTP_CONNECT_TIMEOUT.getValue())
					.readTimeout(GamaPreferences.External.CORE_HTTP_READ_TIMEOUT.getValue())
					.retry(GamaPreferences.External.CORE_HTTP_RETRY_NUMBER.getValue(), false).asStream().getBody();) {
				// final java.net.URI uri = URIUtil.toURI(pathName);
				pathName = ROOT.getPathVariableManager().resolvePath(new Path(pathName)).toOSString();
				// pathName = ROOT.getPathVariableManager().resolveURI(uri).getPath();
				final java.nio.file.Path p = new File(pathName).toPath();
				if (Files.exists(p)) { Files.delete(p); }
				Files.copy(in, p);
			}
		} catch (final IOException | WebbException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			scope.getGui().getStatus(scope).endSubStatus(status);
		}
		return pathName;
	}

}
