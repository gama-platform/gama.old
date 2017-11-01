/*********************************************************************************************
 *
 * 'FileMetaDataProvider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.metadata;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.ImageData;

import msi.gama.common.GamlFileExtension;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.GamaFileMetaData;
import msi.gama.util.file.GamaImageFile.ImageInfo;
import msi.gama.util.file.GamaOsmFile;
import msi.gama.util.file.GamaOsmFile.OSMInfo;
import msi.gama.util.file.GamaShapeFile;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gama.util.file.IGamaFileMetaData;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * Class FileMetaDataProvider.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class FileMetaDataProvider implements IFileMetaDataProvider {

	private static volatile Set<Object> processing = Collections.<Object> synchronizedSet(new HashSet<>());

	/**
	 * Adapt the specific object to the specified class, supporting the IAdaptable interface as well.
	 */
	public static <T> T adaptTo(final Object o, final Class<T> cl) {
		return adaptTo(o, cl, cl);
	}

	/**
	 * Adapt the specific object to the specified classes, supporting the IAdaptable interface as well.
	 *
	 * @param o
	 *            the object.
	 * @param actualType
	 *            the actual type that must be returned.
	 * @param adapterType
	 *            the adapter type to check for.
	 */
	public static <T> T adaptTo(Object o, final Class<T> actualType, final Class<?> adapterType) {
		if (actualType.isInstance(o)) {
			return actualType.cast(o);
		} else if (o instanceof IAdaptable) {
			o = ((IAdaptable) o).getAdapter(adapterType);
			if (actualType.isInstance(o)) { return actualType.cast(o); }
		}
		return null;
	}

	public static final QualifiedName CACHE_KEY = new QualifiedName("msi.gama.application", "metadata");
	public static final String CSV_CT_ID = "msi.gama.gui.csv.type";
	public static final String IMAGE_CT_ID = "msi.gama.gui.images.type";
	public static final String GAML_CT_ID = "msi.gama.gui.gaml.type";
	public static final String SHAPEFILE_CT_ID = "msi.gama.gui.shapefile.type";
	public static final String OSM_CT_ID = "msi.gama.gui.osm.type";
	public static final String SHAPEFILE_SUPPORT_CT_ID = "msi.gama.gui.shapefile.support.type";

	private final static FileMetaDataProvider instance = new FileMetaDataProvider();

	public static final ArrayList<String> OSMExt = new ArrayList<String>() {

		{
			add("osm");
			add("gz");
			add("pbf");
			add("bz2");
		}
	};
	public static final HashMap<String, String> longNames = new HashMap<String, String>() {

		{
			put("prj", "Projection data");
			put("shx", "Index data");
			put("dbf", "Attribute data");
			put("xml", "Metadata");
			put("sbn", "Query data");
			put("sbx", "Query data");
			put("qix", "Query data");
			put("qpj", "QGis project");
			put("fix", "Feature index");
			put("cpg", "Character set codepage");
			put("qml", "Style information");
		}
	};

	public static class GenericFileInfo extends GamaFileMetaData {

		final String suffix;

		public GenericFileInfo(final long stamp, final String suffix) {
			super(stamp);
			this.suffix = suffix;
		}

		public GenericFileInfo(final String propertiesString) {
			super(propertiesString);
			final String[] segments = split(propertiesString);
			suffix = segments[1];
		}

		@Override
		public String getSuffix() {
			return suffix;
		}

		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + suffix;
		}

		@Override
		public String getDocumentation() {
			return suffix;
		}
	}

	public static class ProjectInfo extends GamaFileMetaData {

		final String comment;

		public ProjectInfo(final IProject project) throws CoreException {
			super(project.getModificationStamp());
			final IProjectDescription desc = project.getDescription();
			comment = desc.getComment();
		}

		public ProjectInfo(final String propertiesString) {
			super(propertiesString);
			final String[] segments = split(propertiesString);
			comment = segments[1];
		}

		@Override
		public String getSuffix() {
			if (comment == null || comment.isEmpty())
				return "";
			return comment;
		}

		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + comment;
		}

		@Override
		public String getDocumentation() {
			return comment;
		}
	}

	public static final Map<String, Class<? extends GamaFileMetaData>> CLASSES =
			new HashMap<String, Class<? extends GamaFileMetaData>>() {

				{
					put(CSV_CT_ID, CSVInfo.class);
					put(IMAGE_CT_ID, ImageInfo.class);
					put(GAML_CT_ID, GamlFileInfo.class);
					put(SHAPEFILE_CT_ID, ShapeInfo.class);
					put(OSM_CT_ID, OSMInfo.class);
					put(SHAPEFILE_SUPPORT_CT_ID, GenericFileInfo.class);
					put("project", ProjectInfo.class);
				}
			};

	ExecutorService executor = Executors.newCachedThreadPool();

	private FileMetaDataProvider() {
		ResourcesPlugin.getWorkspace().getSynchronizer().add(CACHE_KEY);
	}

	@Override
	public String getDecoratorSuffix(final Object element) {
		final IGamaFileMetaData data = getMetaData(element, false, true);
		if (data == null) { return ""; }
		return data.getSuffix();
	}

	private IGamaFileMetaData getMetaData(final IProject project, final boolean includeOutdated) {
		if (!project.isAccessible()) { return null; }
		final String ct = "project";
		final Class<? extends GamaFileMetaData> infoClass = CLASSES.get(ct);
		if (infoClass == null) { return null; }
		final IGamaFileMetaData data = readMetadata(project, infoClass, includeOutdated);
		if (data == null) {
			try {
				storeMetadata(project, new ProjectInfo(project), false);
			} catch (final CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		return data;
	}

	/**
	 * Method getMetaData()
	 * 
	 * @see msi.gama.gui.navigator.IFileMetaDataProvider#getMetaData(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IGamaFileMetaData getMetaData(final Object element, final boolean includeOutdated,
			final boolean immediately) {
		if (processing.contains(element)) {

			while (processing.contains(element)) {
				try {
					Thread.currentThread().sleep(100);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			return getMetaData(element, includeOutdated, immediately);

		}

		try {
			if (element instanceof IProject) { return getMetaData((IProject) element, includeOutdated); }
			IFile file = adaptTo(element, IFile.class, IFile.class);

			if (file == null) {
				if (element instanceof java.io.File) {
					final IPath p = Path.fromOSString(((java.io.File) element).getAbsolutePath());
					file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(p);
				}
				if (file == null || !file.exists()) { return null; }
			} else if (!file.isAccessible()) { return null; }
			final String ct = getContentTypeId(file);
			final Class<? extends GamaFileMetaData> infoClass = CLASSES.get(ct);
			if (infoClass == null) { return null; }
			final IGamaFileMetaData[] data = new IGamaFileMetaData[] { readMetadata(file, infoClass, includeOutdated) };
			if (data[0] == null) {
				processing.add(element);
				final IFile theFile = file;
				final Runnable create = () -> {
					try {
						switch (ct) {
							case SHAPEFILE_CT_ID:
								data[0] = createShapeFileMetaData(theFile);
								break;
							case OSM_CT_ID:
								data[0] = createOSMMetaData(theFile);
								break;
							case IMAGE_CT_ID:
								data[0] = createImageFileMetaData(theFile);
								break;
							case CSV_CT_ID:
								data[0] = createCSVFileMetaData(theFile);
								break;
							case GAML_CT_ID:
								data[0] = createGamlFileMetaData(theFile);
								break;
							case SHAPEFILE_SUPPORT_CT_ID:
								data[0] = createShapeFileSupportMetaData(theFile);
								break;
						}
						// Last chance: we generate a generic info
						if (data[0] == null) {
							data[0] = createGenericFileMetaData(theFile);
						}

						// System.out
						// .println("Storing the metadata just created (or
						// recreated) while reading it for " + theFile);
						storeMetadata(theFile, data[0], false);
						try {

							theFile.refreshLocal(IResource.DEPTH_ZERO, null);
						} catch (final CoreException e) {
							e.printStackTrace();
						}
						GAMA.getGui().updateDecorator("msi.gama.application.decorator");
					} finally {
						processing.remove(element);
					}

				};

				if (immediately) {
					create.run();

				} else {
					executor.submit(create);
				}

			}
			return data[0];
		} finally {

		}
	}

	private static <T extends IGamaFileMetaData> T readMetadata(final IResource file, final Class<T> clazz,
			final boolean includeOutdated) {
		T result = null;
		final long modificationStamp = file.getModificationStamp();
		try {
			final byte[] b = ResourcesPlugin.getWorkspace().getSynchronizer().getSyncInfo(CACHE_KEY, file);
			if (b != null) {
				final String s = new String(b, "UTF-8");
				// String s = file.getPersistentProperty(CACHE_KEY);
				result = GamaFileMetaData.from(s, modificationStamp, clazz, includeOutdated);
				if (!clazz.isInstance(result)) { return null; }
			}
		} catch (final Exception ignore) {
			System.err.println("Error loading metadata for " + file.getName() + " : " + ignore.getMessage());
		}
		return result;
	}

	@Override
	public void storeMetadata(final File f, final IGamaFileMetaData data) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = Path.fromOSString(f.getAbsolutePath());
		final IFile file = workspace.getRoot().getFileForLocation(location);
		storeMetadata(file, data, false);

	}

	@Override
	public void storeMetadata(final IResource file, final IGamaFileMetaData data, final boolean immediately) {
		try {
			// System.out.println("Writing back metadata to " + file);
			if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
				// System.out.println("Canceled: Resources are locked");
				return;
			}

			if (data != null) {
				data.setModificationStamp(file.getModificationStamp());
			}

			final Runnable runnable = () -> {
				try {
					ResourcesPlugin.getWorkspace().getSynchronizer().setSyncInfo(CACHE_KEY, file,
							data == null ? null : data.toPropertyString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException | CoreException e) {
					e.printStackTrace();
				}
				// System.out.println("Success: sync info written");
			};
			// WorkspaceModifyOperation
			if (!immediately) {
				WorkbenchHelper.asyncRun(runnable);
			} else {
				WorkbenchHelper.run(runnable);
				// file.setPersistentProperty(CACHE_KEY, data == null ? null :
				// data.toPropertyString());
			}

			// Decorate using current UI thread
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			// // Fire a LabelProviderChangedEvent to notify eclipse views
			// // that label provider has been changed for the resources
			// System.out.println("Finally: updating the decorator manager");
			// PlatformUI.getWorkbench().getDecoratorManager().update("msi.gama.application.decorator");
			// }
			// });

		} catch (final Exception ignore) {
			ignore.printStackTrace();
			System.err.println("Error storing metadata for " + file.getName() + " : " + ignore.getMessage());
		}
	}

	/**
	 * @param file
	 */
	static GamlFileInfo createGamlFileMetaData(final IFile file) {
		return GAML.getInfo(URI.createPlatformResourceURI(file.getFullPath().toOSString(), true),
				file.getModificationStamp());
	}

	static GamaCSVFile.CSVInfo createCSVFileMetaData(final IFile file) {
		return new CSVInfo(file.getLocation().toOSString(), file.getModificationStamp(), null);
	}

	static ImageInfo createImageFileMetaData(final IFile file) {
		ImageInfo imageInfo = null;
		ImageData imageData = null;

		int type = -1, width = -1, height = -1;
		imageData = ImageDataLoader.getImageData(file);
		if (imageData != null) {
			width = imageData.width;
			height = imageData.height;
			type = imageData.type;
		} else {
			width = -1;
			height = -1;
			type = -1;
		}
		imageInfo = new ImageInfo(file.getModificationStamp(), type, width, height);
		return imageInfo;

	}

	/**
	 * @param file
	 * @return
	 */
	static GamaShapeFile.ShapeInfo createShapeFileMetaData(final IFile file) {
		ShapeInfo info = null;
		try {
			info = new ShapeInfo(GAMA.getRuntimeScope(), file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	static GamaOsmFile.OSMInfo createOSMMetaData(final IFile file) {
		OSMInfo info = null;
		try {
			info = new OSMInfo(file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	static GenericFileInfo createShapeFileSupportMetaData(final IFile file) {
		GenericFileInfo info = null;
		final IResource r = shapeFileSupportedBy(file);
		if (r == null) { return null; }
		final String ext = file.getFileExtension();
		final String type = longNames.containsKey(ext) ? longNames.get(ext) : "Data";
		info = new GenericFileInfo(file.getModificationStamp(), "" + type + " for '" + r.getName() + "'");
		return info;

	}

	static GenericFileInfo createGenericFileMetaData(final IFile file) {
		String ext = file.getFileExtension();
		if (ext == null)
			return new GenericFileInfo(file.getModificationStamp(), "Generic file");
		ext = ext.toUpperCase();
		return new GenericFileInfo(file.getModificationStamp(), "Generic " + ext + " file");
	}

	@Override
	public boolean isGAML(final IFile p) {
		return p != null && GamlFileExtension.isAny(p.getName());
	}

	public static String getContentTypeId(final IFile p) {
		final IContentType ct = Platform.getContentTypeManager().findContentTypeFor(p.getFullPath().toOSString());
		if (ct != null) { return ct.getId(); }
		if (GamlFileExtension.isAny(p.getName())) { return GAML_CT_ID; }
		final String ext = p.getFileExtension();
		if ("shp".equals(ext)) { return SHAPEFILE_CT_ID; }
		if (OSMExt.contains(ext)) { return OSM_CT_ID; }
		if (longNames.containsKey(ext)) { return SHAPEFILE_SUPPORT_CT_ID; }
		return "";
	}

	public static boolean isShapeFileSupport(final IFile p) {
		final String ext = p.getFileExtension();
		return longNames.containsKey(ext);
	}

	public static IResource shapeFileSupportedBy(final IFile r) {
		String fileName = r.getName();
		// Special case for these odd files
		if (fileName.endsWith(".shp.xml")) {
			fileName = fileName.replace(".xml", "");
		} else {
			final String extension = r.getFileExtension();
			if (!longNames.containsKey(extension)) { return null; }
			fileName = fileName.replace(extension, "shp");
		}
		return r.getParent().findMember(fileName);
	}

	public static boolean isSupport(final IFile shapefile, final IFile other) {
		final IResource r = shapeFileSupportedBy(other);
		return shapefile.equals(r);
	}

	public static FileMetaDataProvider getInstance() {
		return instance;
	}

}
