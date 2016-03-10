/**
 * Created by drogoul, 11 févr. 2015
 *
 */
package msi.gama.gui.navigator;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;
import gnu.trove.map.hash.THashMap;
import msi.gama.gui.navigator.images.ImageDataLoader;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.*;
import msi.gama.util.file.GAMLFile.GamlInfo;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.GamaImageFile.ImageInfo;
import msi.gama.util.file.GamaOsmFile.OSMInfo;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import msi.gaml.factories.DescriptionFactory;

/**
 * Class FileMetaDataProvider.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class FileMetaDataProvider implements IFileMetaDataProvider {

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
	public static final THashMap<String, String> longNames = new THashMap() {

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
			String[] segments = split(propertiesString);
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
			IProjectDescription desc = project.getDescription();
			comment = desc.getComment();
		}

		public ProjectInfo(final String propertiesString) {
			super(propertiesString);
			String[] segments = split(propertiesString);
			comment = segments[1];
		}

		@Override
		public String getSuffix() {
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

	public static final Map<String, Class> CLASSES = new HashMap() {

		{
			put(CSV_CT_ID, CSVInfo.class);
			put(IMAGE_CT_ID, ImageInfo.class);
			put(GAML_CT_ID, GamlInfo.class);
			put(SHAPEFILE_CT_ID, ShapeInfo.class);
			put(OSM_CT_ID, OSMInfo.class);
			put(SHAPEFILE_SUPPORT_CT_ID, GenericFileInfo.class);
			put("project", ProjectInfo.class);
		}
	};

	public static FileMetaDataProvider getInstance() {
		return instance;
	}

	ExecutorService executor = Executors.newCachedThreadPool();

	private FileMetaDataProvider() {
		ResourcesPlugin.getWorkspace().getSynchronizer().add(CACHE_KEY);
	};

	@Override
	public String getDecoratorSuffix(final Object element) {
		IGamaFileMetaData data = getMetaData(element, false, true);
		if ( data == null ) { return ""; }
		return data.getSuffix();
	}

	private static IGamaFileMetaData getMetaData(final IProject project, final boolean includeOutdated) {
		if ( !project.isAccessible() ) { return null; }
		String ct = "project";
		Class infoClass = CLASSES.get(ct);
		if ( infoClass == null ) { return null; }
		IGamaFileMetaData data = readMetadata(project, infoClass, includeOutdated);
		if ( data == null ) {
			try {
				storeMetadata(project, new ProjectInfo(project), false);
			} catch (CoreException e) {
				e.printStackTrace();
				return null;
			}
		}
		return data;
	}

	/**
	 * Method getMetaData()
	 * @see msi.gama.gui.navigator.IFileMetaDataProvider#getMetaData(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IGamaFileMetaData getMetaData(final Object element, final boolean includeOutdated,
		final boolean immediately) {
		if ( element instanceof IProject ) { return getMetaData((IProject) element, includeOutdated); }
		IFile file = SwtGui.adaptTo(element, IFile.class, IFile.class);

		if ( file == null ) {
			if ( element instanceof java.io.File ) {
				IPath p = Path.fromOSString(((java.io.File) element).getAbsolutePath());
				file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(p);
			}
			if ( file == null || !file.exists() ) { return null; }
		} else if ( !file.isAccessible() ) { return null; }
		final String ct = getContentTypeId(file);
		Class infoClass = CLASSES.get(ct);
		if ( infoClass == null ) { return null; }
		final IGamaFileMetaData[] data = new IGamaFileMetaData[] { readMetadata(file, infoClass, includeOutdated) };
		if ( data[0] == null ) {

			final IFile theFile = file;
			Runnable create = new Runnable() {

				@Override
				public void run() {
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
					if ( data[0] == null ) {
						data[0] = createGenericFileMetaData(theFile);
					}

					// System.out
					// .println("Storing the metadata just created (or recreated) while reading it for " + theFile);
					storeMetadata(theFile, data[0], false);
					try {

						theFile.refreshLocal(IResource.DEPTH_ZERO, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					GAMA.getGui().asyncRun(new Runnable() {

						@Override
						public void run() {
							// Fire a LabelProviderChangedEvent to notify eclipse views
							// that label provider has been changed for the resources
							// System.out.println("Finally: updating the decorator manager");
							PlatformUI.getWorkbench().getDecoratorManager().update("msi.gama.application.decorator");
						}
					});

				}

			};

			if ( immediately ) {
				create.run();
			} else {
				executor.submit(create);
			}

		}
		return data[0];
	}

	private static <T extends IGamaFileMetaData> T readMetadata(final IResource file, final Class<T> clazz,
		final boolean includeOutdated) {
		IGamaFileMetaData result = null;
		long modificationStamp = file.getModificationStamp();
		try {
			byte[] b = ResourcesPlugin.getWorkspace().getSynchronizer().getSyncInfo(CACHE_KEY, file);
			if ( b != null ) {
				String s = new String(b, "UTF-8");
				// String s = file.getPersistentProperty(CACHE_KEY);
				result = GamaFileMetaData.from(s, modificationStamp, clazz, includeOutdated);
				if ( !clazz.isInstance(result) ) { return null; }
			}
		} catch (Exception ignore) {
			System.err.println("Error loading metadata for " + file.getName() + " : " + ignore.getMessage());
		}
		return (T) result;
	}

	@Override
	public void storeMetadata(final File f, final IGamaFileMetaData data) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(f.getAbsolutePath());
		IFile file = workspace.getRoot().getFileForLocation(location);
		storeMetadata(file, data, false);

	}

	public static void storeMetadata(final IResource file, final IGamaFileMetaData data, final boolean immediately) {
		try {
			// System.out.println("Writing back metadata to " + file);
			if ( ResourcesPlugin.getWorkspace().isTreeLocked() ) {
				// System.out.println("Canceled: Resources are locked");
				return;
			}
			if ( data != null ) {
				data.setModificationStamp(file.getModificationStamp());
			}

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					try {
						ResourcesPlugin.getWorkspace().getSynchronizer().setSyncInfo(CACHE_KEY, file,
							data == null ? null : data.toPropertyString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException | CoreException e) {
						e.printStackTrace();
					}
					// System.out.println("Success: sync info written");
				}
			};
			// WorkspaceModifyOperation
			if ( !immediately ) {
				GAMA.getGui().asyncRun(runnable);
			} else {
				GAMA.getGui().run(runnable);
				// file.setPersistentProperty(CACHE_KEY, data == null ? null : data.toPropertyString());
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

		} catch (Exception ignore) {
			ignore.printStackTrace();
			System.err.println("Error storing metadata for " + file.getName() + " : " + ignore.getMessage());
		}
	}

	/**
	 * @param file
	 */
	static GamlInfo createGamlFileMetaData(final IFile file) {
		return DescriptionFactory.getModelFactory().getInfo(URI.createFileURI(file.getLocation().toOSString()),
			file.getModificationStamp());
	}

	static GamaCSVFile.CSVInfo createCSVFileMetaData(final IFile file) {
		return new CSVInfo(file.getLocation().toOSString(), file.getModificationStamp(), null);
	}

	/**
	 * PROBLEM : Stores separately the thumbnail and the image metadata. The former cannot be saved on
	 * disk and is not read
	 * @param file
	 * @return
	 */
	static ImageInfo createImageFileMetaData(final IFile file) {
		ImageInfo imageInfo = null;
		ImageData imageData = null;

		int type = -1, width = -1, height = -1;
		imageData = ImageDataLoader.getImageData(file);
		if ( imageData != null ) {
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
			info = new ShapeInfo(file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	static GamaOsmFile.OSMInfo createOSMMetaData(final IFile file) {
		OSMInfo info = null;
		try {
			info = new OSMInfo(file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	static GenericFileInfo createShapeFileSupportMetaData(final IFile file) {
		GenericFileInfo info = null;
		IResource r = shapeFileSupportedBy(file);
		if ( r == null ) { return null; }
		String ext = file.getFileExtension();
		String type = longNames.containsKey(ext) ? longNames.get(ext) : "Data";
		info = new GenericFileInfo(file.getModificationStamp(), "" + type + " for '" + r.getName() + "'");
		return info;

	}

	static GenericFileInfo createGenericFileMetaData(final IFile file) {
		String ext = file.getFileExtension();
		ext = ext.toUpperCase();
		return new GenericFileInfo(file.getModificationStamp(), "Generic " + ext + " file");
	}

	public static boolean isGAML(final IFile p) {
		return p != null && "gaml".equals(p.getFileExtension())/* GAML_CT_ID.equals(getContentTypeId(p)) */;
	}

	public static String getContentTypeIdOld(final IFile p) {
		IContentDescription desc;
		try {
			desc = p.getContentDescription();
			if ( desc != null ) {
				IContentType type = desc.getContentType();
				if ( type != null ) { return type.getId(); }
			}
		} catch (CoreException e) {}
		String ext = p.getFileExtension();
		if ( "gaml".equals(ext) ) { return GAML_CT_ID; }
		if ( "shp".equals(ext) ) { return SHAPEFILE_CT_ID; }
		if ( OSMExt.contains(ext) ) { return OSM_CT_ID; }
		if ( longNames.contains(ext) ) { return SHAPEFILE_SUPPORT_CT_ID; }
		return "";
	}

	public static String getContentTypeId(final IFile p) {
		IContentType ct = Platform.getContentTypeManager().findContentTypeFor(p.getFullPath().toOSString());
		if ( ct != null ) { return ct.getId(); }
		String ext = p.getFileExtension();
		if ( "gaml".equals(ext) ) { return GAML_CT_ID; }
		if ( "shp".equals(ext) ) { return SHAPEFILE_CT_ID; }
		if ( OSMExt.contains(ext) ) { return OSM_CT_ID; }
		if ( longNames.contains(ext) ) { return SHAPEFILE_SUPPORT_CT_ID; }
		return "";
	}

	public static boolean isShapeFileSupport(final IFile p) {
		String ext = p.getFileExtension();
		return longNames.contains(ext);
	}

	public static IResource shapeFileSupportedBy(final IFile r) {
		String fileName = r.getName();
		// Special case for these odd files
		if ( fileName.endsWith(".shp.xml") ) {
			fileName = fileName.replace(".xml", "");
		} else {
			String extension = r.getFileExtension();
			if ( !longNames.contains(extension) ) { return null; }
			fileName = fileName.replace(extension, "shp");
		}
		return r.getParent().findMember(fileName);
	}

	public static boolean isSupport(final IFile shapefile, final IFile other) {
		IResource r = shapeFileSupportedBy(other);
		return shapefile.equals(r);
	}

}
