/**
 * Created by drogoul, 11 févr. 2015
 *
 */
package msi.gama.gui.navigator;

import java.net.MalformedURLException;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import gnu.trove.map.hash.THashMap;
import msi.gama.gui.navigator.images.ImageDataLoader;
import msi.gama.gui.swt.SwtGui;
import msi.gama.util.file.*;
import msi.gama.util.file.GAMLFile.GamlInfo;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.GamaImageFile.ImageInfo;
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
	public static final String SHAPEFILE_SUPPORT_CT_ID = "msi.gama.gui.shapefile.support.type";

	private final static FileMetaDataProvider instance = new FileMetaDataProvider();

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
			put(SHAPEFILE_SUPPORT_CT_ID, GenericFileInfo.class);
			put("project", ProjectInfo.class);
		}
	};

	public static FileMetaDataProvider getInstance() {
		return instance;
	}

	private FileMetaDataProvider() {
		ResourcesPlugin.getWorkspace().getSynchronizer().add(CACHE_KEY);
	};

	@Override
	public String getDecoratorSuffix(final Object element) {
		IGamaFileMetaData data = getMetaData(element, false);
		if ( data == null ) { return ""; }
		return data.getSuffix();
	}

	private IGamaFileMetaData getMetaData(final IProject project, final boolean includeOutdated) {
		if ( !project.isAccessible() ) { return null; }
		String ct = "project";
		Class infoClass = CLASSES.get(ct);
		if ( infoClass == null ) { return null; }
		IGamaFileMetaData data = readMetadata(project, infoClass, includeOutdated);
		if ( data == null ) {
			try {
				data = new ProjectInfo(project);
				storeMetadata(project, data);
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
	public IGamaFileMetaData getMetaData(final Object element, final boolean includeOutdated) {
		if ( element instanceof IProject ) { return getMetaData((IProject) element, includeOutdated); }
		IFile file = SwtGui.adaptTo(element, IFile.class, IFile.class);

		if ( file == null ) {
			if ( element instanceof java.io.File ) {
				IPath p = Path.fromOSString(((java.io.File) element).getAbsolutePath());
				file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(p);
				if ( file == null || !file.exists() ) { return null; }
			}
		} else if ( !file.isAccessible() ) { return null; }
		String ct = getContentTypeId(file);
		Class infoClass = CLASSES.get(ct);
		if ( infoClass == null ) { return null; }
		IGamaFileMetaData data = readMetadata(file, infoClass, includeOutdated);
		if ( data == null ) {
			if ( SHAPEFILE_CT_ID.equals(ct) ) {
				data = createShapeFileMetaData(file);
			} else if ( IMAGE_CT_ID.equals(ct) ) {
				data = createImageFileMetaData(file);
			} else if ( CSV_CT_ID.equals(ct) ) {
				data = createCSVFileMetaData(file);
			} else if ( GAML_CT_ID.equals(ct) ) {
				data = createGamlFileMetaData(file);
			} else if ( SHAPEFILE_SUPPORT_CT_ID.equals(ct) ) {
				data = createShapeFileSupportMetaData(file);
			}
			// Last chance: we generate a generic info
			if ( data == null ) {
				data = createGenericFileMetaData(file);
			}
			System.out.println("Storing the metadata just created (or recreated) while reading it for " + file);
			storeMetadata(file, data);
		}
		return data;
	}

	private <T extends IGamaFileMetaData> T readMetadata(final IResource file, final Class<T> clazz,
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

	public void storeMetadata(final IResource file, final IGamaFileMetaData data) {
		try {
			System.out.println("Writing back metadata to " + file);
			if ( ResourcesPlugin.getWorkspace().isTreeLocked() ) {
				System.out.println("Canceled: Resources are locked");
				return;
			}
			if ( data != null ) {
				data.setModificationStamp(file.getModificationStamp());
			}
			ResourcesPlugin.getWorkspace().getSynchronizer().setSyncInfo(CACHE_KEY, file,
				data == null ? null : data.toPropertyString().getBytes("UTF-8"));
			System.out.println("Success: sync info written");
			// file.setPersistentProperty(CACHE_KEY, data == null ? null : data.toPropertyString());

			// Decorate using current UI thread
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					// Fire a LabelProviderChangedEvent to notify eclipse views
					// that label provider has been changed for the resources
					System.out.println("Finally: updating the decorator manager");
					PlatformUI.getWorkbench().getDecoratorManager().update("msi.gama.application.decorator");
				}
			});

		} catch (Exception ignore) {
			ignore.printStackTrace();
			System.err.println("Error storing metadata for " + file.getName() + " : " + ignore.getMessage());
		}
	}

	/**
	 * @param file
	 */
	private GamlInfo createGamlFileMetaData(final IFile file) {
		return DescriptionFactory.getModelFactory().getInfo(URI.createFileURI(file.getLocation().toOSString()),
			file.getModificationStamp());
	}

	private GamaCSVFile.CSVInfo createCSVFileMetaData(final IFile file) {
		return new CSVInfo(file.getLocation().toOSString(), file.getModificationStamp());
	}

	/**
	 * PROBLEM : Stores separately the thumbnail and the image metadata. The former cannot be saved on
	 * disk and is not read
	 * @param file
	 * @return
	 */
	private ImageInfo createImageFileMetaData(final IFile file) {
		// ImageDescriptor descriptor = null;
		ImageInfo imageInfo = null;
		// try {
		// Object o = file.getSessionProperty(CACHE_KEY);
		// if ( o instanceof ImageDescriptor ) {
		// descriptor = (ImageDescriptor) o;
		// }
		// } catch (CoreException e) {}

		// if ( descriptor == null ) {
		ImageData imageData = null;
		// Image image = null;

		int type = -1, width = -1, height = -1;
		try {
			imageData = ImageDataLoader.getImageData(file);
			// Display display = SwtGui.getDisplay();
			// image = new Image(display, imageData);
		} catch (Exception ex) {
			System.out.println("Error in loading " + file.getLocation().toString());
		}
		if ( imageData != null ) {
			// try {
			width = imageData.width;
			height = imageData.height;
			type = imageData.type;
			// if ( image != null ) {
			// image = GamaIcons.scaleImage(image.getDevice(), image, 16, 16);
			// descriptor = ImageDescriptor.createFromImageData(image.getImageData());
			// }
			// } catch (Exception ex) {
			// System.out.println("Error in loading " + file.getLocation().toString());
			// } finally {
			// if ( image != null ) {
			// image.dispose();
			// }
			// }
			// }

			imageInfo = new ImageInfo(file.getModificationStamp(), /* descriptor, */type, width, height);
			// try {
			// file.setSessionProperty(CACHE_KEY, descriptor);
			// } catch (CoreException e) {}

		}
		return imageInfo;

	}

	/**
	 * @param file
	 * @return
	 */
	private GamaShapeFile.ShapeInfo createShapeFileMetaData(final IFile file) {
		ShapeInfo info = null;
		try {
			info = new ShapeInfo(file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	private GenericFileInfo createShapeFileSupportMetaData(final IFile file) {
		GenericFileInfo info = null;
		IResource r = shapeFileSupportedBy(file);
		if ( r == null ) { return null; }
		String ext = file.getFileExtension();
		String type = longNames.containsKey(ext) ? longNames.get(ext) : "Data";
		info = new GenericFileInfo(file.getModificationStamp(), "" + type + " for '" + r.getName() + "'");
		return info;

	}

	private GenericFileInfo createGenericFileMetaData(final IFile file) {
		String ext = file.getFileExtension();
		ext = ext.toUpperCase();
		return new GenericFileInfo(file.getModificationStamp(), "Generic " + ext + " file");
	}

	public static boolean isGAML(final IFile p) {
		return p != null && "gaml".equals(p.getFileExtension())/* GAML_CT_ID.equals(getContentTypeId(p)) */;
	}

	public static String getContentTypeId(final IFile p) {
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
		if ( longNames.contains(ext) ) { return SHAPEFILE_SUPPORT_CT_ID; }
		return "";
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
