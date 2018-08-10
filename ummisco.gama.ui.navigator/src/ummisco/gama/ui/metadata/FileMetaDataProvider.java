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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
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
import msi.gama.util.GAML;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.GamaCSVFile.CSVInfo;
import msi.gama.util.file.GamaFileMetaData;
import msi.gama.util.file.GamaImageFile.ImageInfo;
import msi.gama.util.file.GamaOsmFile;
import msi.gama.util.file.GamaOsmFile.OSMInfo;
import msi.gama.util.file.GamaSavedSimulationFile;
import msi.gama.util.file.GamaSavedSimulationFile.SavedSimulationInfo;
import msi.gama.util.file.GamaShapeFile;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gama.util.file.IGamaFileMetaData;

/**
 * Class FileMetaDataProvider.
 *
 * @author drogoul
 * @since 11 févr. 2015
 *
 */
public class FileMetaDataProvider implements IFileMetaDataProvider {

	// static Gzip GZIP = new Gzip();

	// public static class Gzip {
	//
	// public String compress(final String data) throws IOException {
	// if (data == null) { return null; }
	// final ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
	// final GZIPOutputStream gzip = new GZIPOutputStream(bos);
	// gzip.write(data.getBytes());
	// gzip.close();
	// final byte[] compressed = bos.toByteArray();
	// bos.close();
	// final StringBuffer retString = new StringBuffer();
	// for (final byte element : compressed) {
	// retString.append(Integer.toHexString(0x0100 + (element & 0x00FF)).substring(1));
	// }
	// return retString.toString();
	// }
	//
	// public String decompress(final String hex) throws IOException {
	// final byte[] bts = new byte[hex.length() / 2];
	// for (int i = 0; i < bts.length; i++) {
	// bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	// }
	// final ByteArrayInputStream bis = new ByteArrayInputStream(bts);
	// final GZIPInputStream gis = new GZIPInputStream(bis);
	// final BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
	// final StringBuilder sb = new StringBuilder();
	// String line;
	// while ((line = br.readLine()) != null) {
	// sb.append(line);
	// }
	// br.close();
	// gis.close();
	// bis.close();
	// return sb.toString();
	// }
	// }

	private static volatile Set<Object> processing = Collections.<Object> synchronizedSet(new HashSet<>());

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
	private <T> T adaptTo(final Object o, final Class<T> actualType, final Class<?> adapterType) {
		if (actualType.isInstance(o)) {
			return actualType.cast(o);
		} else if (o instanceof IAdaptable) {
			final Object o2 = ((IAdaptable) o).getAdapter(adapterType);
			if (actualType.isInstance(o2)) { return actualType.cast(o2); }
		}
		return null;
	}

	public static final QualifiedName CACHE_KEY = new QualifiedName("msi.gama.application", "metadata");
	public static final QualifiedName CHANGE_KEY = new QualifiedName("msi.gama.application", "changed");
	public static final String CSV_CT_ID = "msi.gama.gui.csv.type";
	public static final String IMAGE_CT_ID = "msi.gama.gui.images.type";
	public static final String GAML_CT_ID = "msi.gama.gui.gaml.type";
	public static final String SHAPEFILE_CT_ID = "msi.gama.gui.shapefile.type";
	public static final String OSM_CT_ID = "msi.gama.gui.osm.type";
	public static final String SHAPEFILE_SUPPORT_CT_ID = "msi.gama.gui.shapefile.support.type";
	public static final String GSIM_CT_ID = "msi.gama.gui.gsim.type";
	

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
		public void appendSuffix(final StringBuilder sb) {
			if (suffix != null) {
				sb.append(suffix);
			}
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
			if (comment == null || comment.isEmpty()) { return ""; }
			return comment;
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			if (comment != null && !comment.isEmpty()) {
				sb.append(comment);
			}
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
					put(GSIM_CT_ID, SavedSimulationInfo.class);
				}
			};

	ExecutorService executor = Executors.newCachedThreadPool();
	volatile boolean started;

	private FileMetaDataProvider() {
		ResourcesPlugin.getWorkspace().getSynchronizer().add(CACHE_KEY);
	}

	private IGamaFileMetaData getMetaData(final IProject project, final boolean includeOutdated) {
		if (!project.isAccessible()) { return null; }
		final String ct = "project";
		final Class<? extends GamaFileMetaData> infoClass = CLASSES.get(ct);
		if (infoClass == null) { return null; }
		final IGamaFileMetaData data = readMetadata(project, infoClass, includeOutdated);
		if (data == null) {
			try {
				storeMetaData(project, new ProjectInfo(project), false);
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
		startup();
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
							case GSIM_CT_ID:
								data[0] = createSacedSimulationFileMetaData(theFile);
								break;								
						}
						// Last chance: we generate a generic info
						if (data[0] == null) {
							data[0] = createGenericFileMetaData(theFile);
						}

						// System.out
						// .println("Storing the metadata just created (or
						// recreated) while reading it for " + theFile);
						storeMetaData(theFile, data[0], immediately);
						try {

							theFile.refreshLocal(IResource.DEPTH_ZERO, null);
						} catch (final CoreException e) {
							e.printStackTrace();
						}
						// GAMA.getGui().updateDecorator("msi.gama.application.decorator");
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

	private <T extends IGamaFileMetaData> T readMetadata(final IResource file, final Class<T> clazz,
			final boolean includeOutdated) {
		T result = null;
		final long modificationStamp = file.getModificationStamp();
		try {
			final String s = (String) file.getSessionProperty(CACHE_KEY);
			if (s != null) {
				// s = GZIP.decompress(s);
				result = GamaFileMetaData.from(s, modificationStamp, clazz, includeOutdated);
			}
			if (!clazz.isInstance(result)) { return null; }
		} catch (final Exception ignore) {
			System.err.println("Error loading metadata for " + file.getName() + " : " + ignore.getMessage());
		}
		return result;
	}

	@Override
	public void storeMetaData(final IResource file, final IGamaFileMetaData data, final boolean immediately) {
		startup();
		if (!file.isAccessible()) { return; }
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
					file.setSessionProperty(CACHE_KEY, data == null ? null : (data.toPropertyString()));
					file.setSessionProperty(CHANGE_KEY, true);
				} catch (final Exception e) {
					e.printStackTrace();
				}
				// System.out.println("Success: sync info written");
			};
			// WorkspaceModifyOperation
			if (!immediately) {
				executor.submit(runnable);
			} else {
				runnable.run();
			}

		} catch (final Exception ignore) {
			ignore.printStackTrace();
			System.err.println("Error storing metadata for " + file.getName() + " : " + ignore.getMessage());
		}
	}

	/**
	 * @param file
	 */
	private GamlFileInfo createGamlFileMetaData(final IFile file) {
		return GAML.getInfo(URI.createPlatformResourceURI(file.getFullPath().toOSString(), true),
				file.getModificationStamp());
	}

	private GamaCSVFile.CSVInfo createCSVFileMetaData(final IFile file) {
		return new CSVInfo(file.getLocation().toOSString(), file.getModificationStamp(), null);
	}

	private ImageInfo createImageFileMetaData(final IFile file) {
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
	private GamaShapeFile.ShapeInfo createShapeFileMetaData(final IFile file) {
		ShapeInfo info = null;
		try {
			info = new ShapeInfo(null, file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	private GamaOsmFile.OSMInfo createOSMMetaData(final IFile file) {
		OSMInfo info = null;
		try {
			info = new OSMInfo(file.getLocationURI().toURL(), file.getModificationStamp());
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return info;

	}

	private GenericFileInfo createShapeFileSupportMetaData(final IFile file) {
		GenericFileInfo info = null;
		final IResource r = shapeFileSupportedBy(file);
		if (r == null) { return null; }
		final String ext = file.getFileExtension();
		final String type = longNames.containsKey(ext) ? longNames.get(ext) : "Data";
		info = new GenericFileInfo(file.getModificationStamp(), "" + type + " for '" + r.getName() + "'");
		return info;

	}

	private GenericFileInfo createGenericFileMetaData(final IFile file) {
		String ext = file.getFileExtension();
		if (ext == null) { return new GenericFileInfo(file.getModificationStamp(), "Generic file"); }
		ext = ext.toUpperCase();
		return new GenericFileInfo(file.getModificationStamp(), "Generic " + ext + " file");
	}

	private GamaSavedSimulationFile.SavedSimulationInfo createSacedSimulationFileMetaData(final IFile file) {
		return new SavedSimulationInfo(file.getLocation().toOSString(), file.getModificationStamp());
	}	
	
	public static String getContentTypeId(final IFile p) {
		final IContentType ct = Platform.getContentTypeManager().findContentTypeFor(p.getFullPath().toOSString());
		if (ct != null) { return ct.getId(); }
		if (GamlFileExtension.isAny(p.getName())) { return GAML_CT_ID; }
		final String ext = p.getFileExtension();
		if ("shp".equals(ext)) { return SHAPEFILE_CT_ID; }
		if (OSMExt.contains(ext)) { return OSM_CT_ID; }
		if (longNames.containsKey(ext)) { return SHAPEFILE_SUPPORT_CT_ID; }
		if("gsim".equals(ext)) { return GSIM_CT_ID; }
		return "";
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

	private void startup() {
		if (started) { return; }
		started = true;
		System.out.print("Reading workspace metadata ");
		final long ms = System.currentTimeMillis();
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(resource -> {
				if (resource.isAccessible()) {
					resource.setSessionProperty(CACHE_KEY, resource.getPersistentProperty(CACHE_KEY));
				}
				return true;
			});
		} catch (final CoreException e) {
			// Nothing
		} finally {
			System.out.println("in " + (System.currentTimeMillis() - ms) + "ms");
		}
		try {
			ResourcesPlugin.getWorkspace().addSaveParticipant("ummisco.gama.ui.modeling", getSaveParticipant());
		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	private ISaveParticipant getSaveParticipant() {
		return new ISaveParticipant() {

			@Override
			public void saving(final ISaveContext context) throws CoreException {
				if (context.getKind() != ISaveContext.FULL_SAVE) { return; }
				System.out.print("Saving workspace metadata ");
				final long ms = System.currentTimeMillis();
				final String[] toSave = new String[1];
				try {
					ResourcesPlugin.getWorkspace().getRoot().accept(resource -> {

						try {
							if (resource.isAccessible()) {
								toSave[0] = (String) resource.getSessionProperty(CACHE_KEY);
								resource.setPersistentProperty(CACHE_KEY, (toSave[0]));
							}
							return true;
						} catch (final Exception e) {
							System.out.println("Error for resource " + resource.getName());
							if (toSave[0] != null)
							System.out.println("Trying to save " + toSave[0].length() + " bytes ");
							return true;
						}

					});
				} finally {
					System.out.println("in " + (System.currentTimeMillis() - ms) + "ms");
				}

			}

			@Override
			public void rollback(final ISaveContext context) {}

			@Override
			public void prepareToSave(final ISaveContext context) throws CoreException {}

			@Override
			public void doneSaving(final ISaveContext context) {}
		};
	}

	public List<IFile> getSupportFilesOf(final IFile f) {
		if (f == null) { return Collections.EMPTY_LIST; }
		if (!getContentTypeId(f).equals(SHAPEFILE_CT_ID)) { return Collections.EMPTY_LIST; }
		final IContainer c = f.getParent();
		final List<IFile> result = new ArrayList<>();
		try {
			for (final IResource r : c.members()) {
				if (r instanceof IFile && isSupport(f, (IFile) r)) {
					result.add((IFile) r);
				}
			}
		} catch (final CoreException e) {}
		return result;
	}

}
