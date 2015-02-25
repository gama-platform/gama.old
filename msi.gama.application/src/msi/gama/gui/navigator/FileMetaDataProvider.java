/**
 * Created by drogoul, 11 févr. 2015
 * 
 */
package msi.gama.gui.navigator;

import java.net.URL;
import java.util.*;
import msi.gama.gui.navigator.images.ImageDataLoader;
import msi.gama.gui.swt.*;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.util.file.*;
import msi.gama.util.file.CsvReader.Stats;
import msi.gama.util.file.GAMLFile.GamlInfo;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.GamaImageFile.ImageInfo;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.*;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

	private final static FileMetaDataProvider instance = new FileMetaDataProvider();

	public static final Map<String, Class> CLASSES = new HashMap() {

		{
			put(CSV_CT_ID, CSVInfo.class);
			put(IMAGE_CT_ID, ImageInfo.class);
			put(GAML_CT_ID, GamlInfo.class);
			put(SHAPEFILE_CT_ID, ShapeInfo.class);
		}
	};

	public static FileMetaDataProvider getInstance() {
		return instance;
	}

	private FileMetaDataProvider() {};

	@Override
	public String getDecoratorSuffix(final Object element) {
		IGamaFileMetaData data = getMetaData(element);
		if ( data == null ) { return ""; }
		return data.getSuffix();
	}

	/**
	 * Method getMetaData()
	 * @see msi.gama.gui.navigator.IFileMetaDataProvider#getMetaData(org.eclipse.core.resources.IFile)
	 */
	@Override
	public IGamaFileMetaData getMetaData(final Object element) {
		IFile file = SwtGui.adaptTo(element, IFile.class, IFile.class);
		if ( file == null ) { return null; }
		String ct = getContentTypeId(file);
		Class infoClass = CLASSES.get(ct);
		if ( infoClass == null ) { return null; }
		IGamaFileMetaData data = readMetadata(file, infoClass);
		if ( data == null ) {
			if ( SHAPEFILE_CT_ID.equals(ct) ) {
				data = createShapeFileMetaData(file);
			} else if ( IMAGE_CT_ID.equals(ct) ) {
				data = createImageFileMetaData(file);
			} else if ( CSV_CT_ID.equals(ct) ) {
				data = createCSVFileMetaData(file);
			} else if ( GAML_CT_ID.equals(ct) ) {
				data = createGamlFileMetaData(file);
			}
			storeMetadata(file, data);
		}
		return data;
	}

	private <T extends IGamaFileMetaData> T readMetadata(final IFile file, final Class<T> clazz) {
		IGamaFileMetaData result = null;
		long modificationStamp = file.getModificationStamp();
		try {
			String s = file.getPersistentProperty(CACHE_KEY);
			if ( s != null ) {
				result = GamaFileMetaData.from(s, modificationStamp, clazz);
				if ( !clazz.isInstance(result) ) { return null; }
			}
		} catch (Exception ignore) {
			System.err.println("Error loading metadata for " + file.getName() + " : " + ignore.getMessage());
		}
		return (T) result;
	}

	private void storeMetadata(final IFile file, final IGamaFileMetaData data) {
		try {
			file.setPersistentProperty(CACHE_KEY, data.toPropertyString());
		} catch (Exception ignore) {
			System.err.println("Error storing metadata for " + file.getName() + " : " + ignore.getMessage());
		}
	}

	/**
	 * @param file
	 */
	private GamlInfo createGamlFileMetaData(final IFile file) {
		URI uri = URI.createFileURI(file.getLocation().toOSString());
		return DescriptionFactory.getModelFactory().getInfo(uri, file.getModificationStamp());
	}

	private GamaCSVFile.CSVInfo createCSVFileMetaData(final IFile file) {
		CSVInfo info = null;
		try {
			Stats s = CsvReader.getStats(file.getContents());
			if ( s != null ) {
				info = new CSVInfo(file.getModificationStamp(), s.cols, s.rows, s.header, s.delimiter, s.type);
			}
		} catch (CoreException e) {
			System.out.println("Error in loading " + file.getLocation().toString());
		}
		return info;
	}

	/**
	 * PROBLEM : Stores separately the thumbnail and the image metadata. The former cannot be saved on
	 * disk and is not read
	 * @param file
	 * @return
	 */
	private ImageInfo createImageFileMetaData(final IFile file) {
		ImageDescriptor descriptor = null;
		ImageInfo imageInfo = null;
		try {
			Object o = file.getSessionProperty(CACHE_KEY);
			if ( o instanceof ImageDescriptor ) {
				descriptor = (ImageDescriptor) o;
			}
		} catch (CoreException e) {}

		if ( descriptor == null ) {
			ImageData imageData = null;
			Image image = null;

			int type = -1, width = -1, height = -1;
			try {
				imageData = ImageDataLoader.getImageData(file);
				Display display = SwtGui.getDisplay();
				image = new Image(display, imageData);
			} catch (Exception ex) {
				System.out.println("Error in loading " + file.getLocation().toString());
			}
			if ( imageData != null ) {
				try {
					width = imageData.width;
					height = imageData.height;
					type = imageData.type;
					if ( image != null ) {
						image = GamaIcons.scaleImage(image.getDevice(), image, 16, 16);
						descriptor = ImageDescriptor.createFromImageData(image.getImageData());
					}
				} catch (Exception ex) {
					System.out.println("Error in loading " + file.getLocation().toString());
				} finally {
					if ( image != null ) {
						image.dispose();
					}
				}
			}

			imageInfo = new ImageInfo(file.getModificationStamp(), descriptor, type, width, height);
			try {
				file.setSessionProperty(CACHE_KEY, descriptor);
			} catch (CoreException e) {}

		}
		return imageInfo;

	}

	/**
	 * @param file
	 * @return
	 */
	private GamaShapeFile.ShapeInfo createShapeFileMetaData(final IFile file) {
		ShapeInfo info = null;
		URL url = null;
		ShapefileDataStore store = null;
		try {
			url = file.getLocationURI().toURL();
			store = new ShapefileDataStore(url);
			SimpleFeatureSource source = store.getFeatureSource();
			SimpleFeatureCollection features = source.getFeatures();
			boolean noCRS = false;
			CoordinateReferenceSystem crs = null;
			try {
				crs = source.getInfo().getCRS();
			} catch (Exception e) {}
			noCRS = crs == null;
			ReferencedEnvelope env = source.getBounds();
			if ( env != null ) {
				if ( !noCRS ) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(), true);
					} catch (Exception e) {}
				}
			} else {
				env = new ReferencedEnvelope();
			}
			info = new ShapeInfo(file.getModificationStamp(), features.size(), crs, env.getWidth(), env.getHeight());
		} catch (Exception e) {
			return null;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		return info;

	}

	public static boolean isShapefile(final IFile p) {
		return p != null && SHAPEFILE_CT_ID.equals(getContentTypeId(p));
	}

	public static boolean isImage(final IFile p) {
		return p != null && IMAGE_CT_ID.equals(getContentTypeId(p));
	}

	public static boolean isCSV(final IFile p) {
		return p != null && CSV_CT_ID.equals(getContentTypeId(p));
	}

	public static boolean isGAML(final IFile p) {
		return p != null && GAML_CT_ID.equals(getContentTypeId(p));
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
		IPath path = p.getLocation();
		String ext = path.getFileExtension();
		if ( "gaml".equals(ext) ) { return GAML_CT_ID; }
		if ( "shp".equals(ext) ) { return SHAPEFILE_CT_ID; }
		return "";
	}

}
