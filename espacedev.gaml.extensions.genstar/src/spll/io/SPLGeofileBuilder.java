/*******************************************************************************************************
 *
 * SPLGeofileBuilder.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package spll.io;

import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.media.jai.RasterFactory;

import org.apache.commons.io.FilenameUtils;
import org.geotools.coverage.Category;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridCoverageWriter;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
// import org.geotools.factory.Hints;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.i18n.Vocabulary;
import org.geotools.renderer.i18n.VocabularyKeys;
// import org.geotools.resources.i18n.Vocabulary;
// import org.geotools.resources.i18n.VocabularyKeys;
import org.geotools.util.NumberRange;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.operation.TransformException;

import au.com.bytecode.opencsv.CSVReader;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;
import core.util.exception.GSIllegalRangedData;
import core.util.exception.GenstarException;
import core.util.stats.GSBasicStats;
import core.util.stats.GSEnumStats;
import spll.SpllEntity;
import spll.SpllPopulation;
import spll.entity.GeoEntityFactory;
import spll.entity.SpllFeature;
import spll.io.exception.InvalidGeoFormatException;

/**
 * Builder that makes it possible to create GIS based file. General underlying contract is defined by GeoTools </br>
 * Can be used as a builder (setup all options and then build your object) or a regular factory
 *
 * @author Kevin Chapuis
 *
 */
public class SPLGeofileBuilder {

	/**
	 * The Enum SPLGisFileExtension.
	 */
	// FILE EXTENSION
	public enum SPLGisFileExtension {

		/** The shp. */
		shp,
		/** The asc. */
		asc,
		/** The tif. */
		tif
	}

	/** The bands. */
	// RASTER FILE BUILD ATTRIBUTE
	private List<float[][]> bands;

	/** The envelope. */
	private ReferencedEnvelope envelope;

	/** The no data. */
	private double noData = SPLRasterFile.DEF_NODATA.doubleValue();

	/** The palette. */
	private Color[] palette = { new Color(254, 240, 217), new Color(253, 204, 138), new Color(252, 141, 89),
			new Color(227, 74, 51), new Color(179, 0, 0) };

	/** The no data color. */
	private final Color noDataColor = new Color(0, 0, 0, 0);

	/** The population. */
	// SHAPE FILE BUILD ATTRIBUTE
	private SpllPopulation population;

	/** The features. */
	private Collection<SpllFeature> features;

	/** The charset. */
	private static final Charset charset = null;

	/** The gis file. */
	// UNSPECIFIC BUILD
	private File gisFile;

	// ----------------------- BUILD ----------------------- //

	/**
	 * Add the GIS file to be build
	 *
	 * @param gisFile
	 * @return
	 * @throws InvalidGeoFormatException
	 * @throws FileNotFoundException
	 */
	public SPLGeofileBuilder setFile(final File gisFile) {
		File parentFile = gisFile.getParentFile();
		if (!parentFile.exists()) { parentFile.mkdirs(); }
		this.gisFile = gisFile;
		// chekFile(SPLGisFileExtension.values());
		return this;
	}

	// ----------------------- RASTER ----------------------- //

	/**
	 * Add a specified palette
	 *
	 * @param palette
	 * @return
	 */
	public SPLGeofileBuilder setPalette(final Color[] palette) {
		this.palette = palette;
		return this;
	}

	/**
	 * Define the noData value for rasterfile
	 *
	 * @param noData
	 * @return
	 */
	public SPLGeofileBuilder setNoData(final double noData) {
		this.noData = noData;
		return this;
	}

	/**
	 * Define the general referenced envelope to build raster file with
	 *
	 * @param envelope
	 * @return
	 */
	public SPLGeofileBuilder setReferenceEnvelope(final ReferencedEnvelope envelope) {
		this.envelope = envelope;
		return this;
	}

	/**
	 * Add bands to build raster file from
	 */
	public SPLGeofileBuilder setRasterBands(final float[][]... bands) {
		int[] xy = null;
		for (float[][] band : bands) {
			if (xy == null) {
				xy = new int[2];
				xy[0] = band.length;
				xy[1] = band[0].length;
			} else if (xy[0] != band.length || xy[1] != band[0].length) throw new IllegalArgumentException(
					"All bands should have same width x height: \n" + IntStream.range(0, bands.length)
							.mapToObj(i -> "b" + i + " [" + bands[i].length + "x" + bands[i][0].length + "]")
							.collect(Collectors.joining(" / ")));
		}
		this.bands = new ArrayList<>(Arrays.asList(bands));
		return this;
	}

	// ----------------------- VECTOR ----------------------- //

	/**
	 * Add a localized population to build shapefile from
	 *
	 * @param population
	 * @return
	 */
	public SPLGeofileBuilder setPopulation(final SpllPopulation population) {
		this.population = population;
		return this;
	}

	/**
	 * Add a collection of feature ({@link SpllFeature}) to build shapefile from
	 *
	 * @param features
	 * @return
	 */
	public SPLGeofileBuilder setFeatures(final Collection<SpllFeature> features) {
		this.features = features;
		return this;
	}

	/**
	 * Add new feature to build shapefile with
	 * <p>
	 * because {@link Feature} are immutable object, we must erase former {@link SpllFeature} with new one that
	 * encapsulate updated {@link SimpleFeature} build from former one plus {@link Attribute} created from information
	 * from the csv
	 *
	 * @param csvFile
	 * @param seperator
	 * @param keyAttribute
	 * @param keyCSV
	 * @param newAttributes
	 * @return
	 * @throws IOException
	 * @throws GSIllegalRangedData
	 */
	public SPLGeofileBuilder addAttributeToFeature(final File csvFile, final char seperator, final String keyAttribute,
			final String keyCSV, final Map<String, GSEnumDataType> newAttributes)
			throws IOException, GSIllegalRangedData {
		if (features == null || features.isEmpty()) throw new IllegalStateException(this.getClass().getCanonicalName()
				+ " is not able to add attribute if not any feature have been added before");
		if (!csvFile.exists())
			throw new FileNotFoundException("File " + csvFile + " cannot be resolve to a valid file");

		try (CSVReader reader = new CSVReader(new FileReader(csvFile), seperator)) {
			List<String[]> dataTable = reader.readAll();
			Map<String, Map<String, String>> values = new HashMap<>();
			List<String> header = Arrays.asList(dataTable.get(0));

			if (!header.contains(keyCSV)) throw new IllegalArgumentException(
					"The given key to retrieve attribute from csv is not related to any csv column");
			int keyCSVIndex = header.indexOf(keyCSV);

			Map<String, Integer> attIndex = newAttributes.keySet().stream().filter(header::contains)
					.collect(Collectors.toMap(s -> s, header::indexOf));
			Map<String, Attribute<? extends IValue>> attAGeo = new HashMap<>();

			for (String attName : attIndex.keySet()) {
				attAGeo.put(attName,
						AttributeFactory.getFactory().createAttribute(attName, newAttributes.get(attName)));
			}

			for (String[] line : dataTable) {
				String id = line[keyCSVIndex];
				Map<String, String> vals = new HashMap<>();
				for (String name : attIndex.keySet()) {
					int idat = attIndex.get(name);
					vals.put(name, line[idat]);
				}
				values.put(id, vals);
			}
			// Create the new type using the former as a template
			Set<FeatureType> fTypes =
					features.stream().map(feat -> feat.getInnerFeature().getType()).collect(Collectors.toSet());
			if (fTypes.size() > 1)
				throw new IllegalStateException("There is more than one feature type in feature collection");
			SimpleFeatureTypeBuilder stb = new SimpleFeatureTypeBuilder();
			stb.init((SimpleFeatureType) fTypes.iterator().next());
			stb.setName("augmented feature type");
			// add new attributes
			newAttributes.keySet().stream().forEach(attName -> stb.add(attName, IValue.class));
			GeoEntityFactory gef = new GeoEntityFactory(new HashSet<>(attAGeo.values()), stb.buildFeatureType());

			Set<SpllFeature> newSpllFeatures = new HashSet<>();
			for (SpllFeature ft : features) {
				Collection<String> properties = ft.getPropertiesAttribute();
				if (!properties.contains(keyAttribute)) { continue; }
				String objid = ft.getValueForAttribute(keyAttribute).getStringValue();

				Map<String, String> vals = values.get(objid);
				if (vals == null) { continue; }

				// New Spll feature attribute setup
				Map<Attribute<? extends IValue>, IValue> newValues = new HashMap<>(ft.getAttributeMap());
				newValues.putAll(vals.keySet().stream().collect(Collectors.toMap(vn -> attAGeo.get(vn),
						vn -> attAGeo.get(vn).getValueSpace().addValue(vals.get(vn)))));
				newSpllFeatures.add(gef.createGeoEntity(ft.getGeometry(), newValues));
			}
			this.features = newSpllFeatures;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Add new feature to build shapefile with
	 * <p>
	 * because {@link Feature} are immutable object, we must erase former {@link SpllFeature} with new one that
	 * encapsulate updated {@link SimpleFeature} build from former one plus {@link Attribute} created from information
	 * from the csv
	 *
	 * @param csvFile
	 * @param seperator
	 * @param keyAttribute
	 * @param keyCSV
	 * @param newAttributes
	 * @return
	 * @throws IOException
	 * @throws GSIllegalRangedData
	 */
	public SPLGeofileBuilder addIdToFeature() throws GSIllegalRangedData {
		if (features == null || features.isEmpty()) throw new IllegalStateException(this.getClass().getCanonicalName()
				+ " is not able to add the id if not any feature have been added before");
		Map<String, Attribute<? extends IValue>> attAGeo = new HashMap<>();

		String attName = "id";
		attAGeo.put(attName, AttributeFactory.getFactory().createAttribute(attName, GSEnumDataType.Nominal));

		// Create the new type using the former as a template
		Set<FeatureType> fTypes =
				features.stream().map(feat -> feat.getInnerFeature().getType()).collect(Collectors.toSet());
		if (fTypes.size() > 1)
			throw new IllegalStateException("There is more than one feature type in feature collection");
		SimpleFeatureTypeBuilder stb = new SimpleFeatureTypeBuilder();
		stb.init((SimpleFeatureType) fTypes.iterator().next());
		stb.setName("augmented feature type");
		// add new attributes
		GeoEntityFactory gef = new GeoEntityFactory(new HashSet<>(attAGeo.values()), stb.buildFeatureType());

		Set<SpllFeature> newSpllFeatures = new HashSet<>();
		for (SpllFeature ft : features) {

			Map<String, String> vals = new HashMap<>();
			vals.put(attName, ft.getGenstarName());

			// New Spll feature attribute setup
			Map<Attribute<? extends IValue>, IValue> newValues = new HashMap<>(ft.getAttributeMap());
			newValues.putAll(vals.keySet().stream().collect(
					Collectors.toMap(attAGeo::get, vn -> attAGeo.get(vn).getValueSpace().addValue(vals.get(vn)))));
			newSpllFeatures.add(gef.createGeoEntity(ft.getGeometry(), newValues));
		}
		this.features = newSpllFeatures;

		return this;
	}

	// ------------------------------------------------------------ //
	// CREATE GEOFILE //
	// ------------------------------------------------------------ //

	/**
	 * Create a geo referenced file
	 *
	 * @param geofile
	 * @return
	 * @throws IllegalArgumentException
	 * @throws TransformException
	 * @throws IOException
	 * @throws InvalidGeoFormatException
	 * @throws GSIllegalRangedData
	 */
	public IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> buildGeofile() throws IOException,
			IllegalArgumentException, TransformException, InvalidGeoFormatException, GSIllegalRangedData {
		if (FilenameUtils.getExtension(this.gisFile.getName()).equals(SPLGisFileExtension.shp.toString()))
			return new SPLVectorFile(this.gisFile, SPLGeofileBuilder.charset);
		if (FilenameUtils.getExtension(this.gisFile.getName()).equals(SPLGisFileExtension.asc.toString())
				|| FilenameUtils.getExtension(this.gisFile.getName()).equals(SPLGisFileExtension.tif.toString()))
			return new SPLRasterFile(this.gisFile);
		chekFile(SPLGisFileExtension.values());
		throw new GenstarException("GIS file " + gisFile + " cannot be init.");
	}

	/**
	 * : test : change float pixel value type to double (possible through JAI)
	 *
	 * Build a raster file from a list of pixel band.
	 *
	 * @param rasterfile
	 * @param pixels
	 * @param crs
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws TransformException
	 */
	public SPLRasterFile buildRasterfile() throws IOException, IllegalArgumentException, TransformException {

		if (bands.isEmpty()) throw new IllegalStateException(
				this.getClass().getCanonicalName() + " should contain bands data to build raster file");

		List<GSBasicStats<Double>> stats = bands.stream()
				.map(pix -> new GSBasicStats<>(GSBasicStats.transpose(pix), Arrays.asList(this.noData * 1d))).toList();
		float min = (float) stats.stream().mapToDouble(stat -> stat.getStat(GSEnumStats.min)[0]).min().getAsDouble();
		float max = (float) stats.stream().mapToDouble(stat -> stat.getStat(GSEnumStats.max)[0]).min().getAsDouble();

		Category nan = new Category(Vocabulary.formatInternational(VocabularyKeys.UNTITLED),
				new Color[] { noDataColor }, NumberRange.create(this.noData, this.noData));
		Category values = new Category("values", palette, NumberRange.create(min, max));

		GridSampleDimension[] bandsDimension =
				{ new GridSampleDimension("Dimension", new Category[] { nan, values }, null) };

		WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, this.bands.get(0).length,
				this.bands.get(0)[0].length, this.bands.size(), null);
		int bandNb = -1;
		for (float[][] pixels : this.bands) {
			bandNb++;
			for (int y = 0; y < pixels[0].length; y++) {
				for (int x = 0; x < pixels.length; x++) { raster.setSample(x, y, bandNb, pixels[x][y]); }
			}
		}

		return writeRasterFile(this.gisFile,
				new GridCoverageFactory().create(this.gisFile.getName(), raster, this.envelope, bandsDimension));
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 * @throws SchemaException
	 */
	public SPLVectorFile buildShapeFile() throws IOException, SchemaException {
		if (!chekAndRenameFile(SPLGisFileExtension.shp))
			throw new GenstarException("Not able to build requested " + this.gisFile + " shape file");

		if (population == null && (features == null || features.isEmpty())) throw new IllegalStateException(
				"To build shape file you must first setup a sources, either features or population");

		Map<String, Serializable> key2m = new HashMap<>();
		if (gisFile != null) {
			key2m.put("url", gisFile.toURI().toURL());
		} else {
			key2m.put("url", File.createTempFile("tmp_file", ".shp").toURI().toURL());
		}
		key2m.put("create spatial index", Boolean.TRUE);

		DataStore newDataStore = new ShapefileDataStoreFactory().createNewDataStore(key2m);
		// Three cases:
		// 1 - write down a population to a file
		if (this.population != null) {
			// 1.1 - with no more feature to add to the file
			if (this.features == null || this.features.isEmpty()) {
				this.features = Collections.emptyList();
				Map<ADemoEntity, Geometry> geoms = population.stream().filter(e -> e.getLocation() != null)
						.collect(Collectors.toMap(e -> e, SpllEntity::getLocation));
				final StringBuilder specs = new StringBuilder();
				specs.append("geometry:" + getGeometryType(geoms.values()));
				List<String> atts = new ArrayList<>();
				for (final Attribute<? extends IValue> at : population.getPopulationAttributes()) {
					atts.add(at.getAttributeName());
					String name = at.getAttributeName().replace("\"", "").replace("'", "");
					specs.append(',').append(name).append(':').append("String");

				}
				this.features = Collections.emptyList();
				Set<String> links = new HashSet<>();
				for (SpllEntity ent : population) { links.addAll(ent.getLinkedPlaces().keySet()); }
				for (String link : links) { specs.append(',').append(link).append(':').append("String"); }
				newDataStore.createSchema(DataUtilities.createType("Pop", specs.toString()));
				try (FeatureWriter<SimpleFeatureType, SimpleFeature> fw =
						newDataStore.getFeatureWriter(newDataStore.getTypeNames()[0], Transaction.AUTO_COMMIT)) {

					for (final SpllEntity entity : population) {

						SimpleFeature f = fw.next();
						List<Object> at =
								Stream.concat(Stream.of(geoms.get(entity)), entity.getValues().stream()).toList();
						for (String link : links) {
							AGeoEntity<? extends IValue> lp = entity.getLinkedPlaces().get(link);
							if (lp == null) {
								at.add("?");
							} else {
								at.add(lp.getGenstarName());
							}
						}
						f.setAttributes(at);
						fw.write();
						fw.hasNext();

					}

					if (gisFile != null) {
						try (FileWriter fwz = new FileWriter(this.gisFile.getAbsolutePath().replace(".shp", ".prj"))) {
							fwz.write(population.getCrs().toString());
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				}
				// 1.2 - with extended feature to add to spatial entities
			} else {
				// ?
			}
		}
		// 2 - write down features to a vector style file if C1. features are available C2 no population are available
		else if (this.features != null && !this.features.isEmpty()) {
			Set<FeatureType> featTypeSet =
					this.features.stream().map(feat -> feat.getInnerFeature().getType()).collect(Collectors.toSet());
			if (featTypeSet.size() > 1) throw new SchemaException(
					"Multiple feature type to instantiate schema:\n" + Arrays.toString(featTypeSet.toArray()));

			SimpleFeatureType featureType = (SimpleFeatureType) featTypeSet.iterator().next();
			newDataStore.createSchema(featureType);

			try (Transaction transaction = new DefaultTransaction("create")) {
				SimpleFeatureStore featureStore =
						(SimpleFeatureStore) newDataStore.getFeatureSource(newDataStore.getTypeNames()[0]);

				SimpleFeatureCollection collection = new ListFeatureCollection(featureType,
						this.features.stream().map(f -> (SimpleFeature) f.getInnerFeature()).toList());
				featureStore.setTransaction(transaction);
				try {
					featureStore.addFeatures(collection);
					transaction.commit();
				} catch (Exception problem) {
					problem.printStackTrace();
					transaction.rollback();
				}
			}
		}

		return new SPLVectorFile(newDataStore, new HashSet<>(this.features));
	}

	// -------------------------------------------------------------------------- //
	// ------------------- FACTORY STYLE STATIC GIS FILE INIT ------------------- //
	// -------------------------------------------------------------------------- //

	/**
	 * Create a shapefile from a file
	 *
	 * @param shapefile
	 *            the file to parse
	 * @param charset
	 *            the charset to use for parsing the shapefile (null for default)
	 * @return
	 * @throws IOException,
	 *             InvalidFileTypeException
	 * @throws GSIllegalRangedData
	 */
	public static SPLVectorFile getShapeFile(final File shapefile, final Charset charset)
			throws IOException, InvalidGeoFormatException, GSIllegalRangedData {
		if (FilenameUtils.getExtension(shapefile.getName()).equals(SPLGisFileExtension.shp.toString()))
			return new SPLVectorFile(shapefile, charset);
		String[] pathArray = shapefile.getPath().split(File.separator);
		throw new InvalidGeoFormatException(pathArray[pathArray.length - 1], Arrays.asList(SPLGisFileExtension.shp));
	}

	/**
	 * javadoc
	 *
	 * @param shapefile
	 * @param attributes
	 * @param ownCharset
	 *            charset to use to decode the file (null for default)
	 * @return
	 * @throws IOException
	 * @throws InvalidGeoFormatException
	 * @throws GSIllegalRangedData
	 */
	public static SPLVectorFile getShapeFile(final File shapefile, final List<String> attributes, final Charset ownCharset)
			throws IOException, InvalidGeoFormatException, GSIllegalRangedData {
		if (FilenameUtils.getExtension(shapefile.getName()).equals(SPLGisFileExtension.shp.toString()))
			return new SPLVectorFile(shapefile, ownCharset, attributes);
		String[] pathArray = shapefile.getPath().split(File.separator);
		throw new InvalidGeoFormatException(pathArray[pathArray.length - 1], Arrays.asList(SPLGisFileExtension.shp));
	}

	// ------------------------------------------------------- //
	// ------------------- INNER UTILITIES ------------------- //
	// ------------------------------------------------------- //

	/**
	 * Write raster file.
	 *
	 * @param rasterfile
	 *            the rasterfile
	 * @param coverage
	 *            the coverage
	 * @return the SPL raster file
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws TransformException
	 *             the transform exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Write down either asc or tif file
	 */
	private SPLRasterFile writeRasterFile(final File rasterfile, final GridCoverage2D coverage)
			throws IllegalArgumentException, TransformException, IOException {
		AbstractGridCoverageWriter writer;
		try {
			chekFile(SPLGisFileExtension.asc, SPLGisFileExtension.tif);
		} catch (FileNotFoundException e) {
			this.gisFile.createNewFile();
		} catch (InvalidGeoFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (FilenameUtils.getExtension(rasterfile.getName()).equals(SPLGisFileExtension.asc.toString())) {
			writer = new ArcGridWriter(rasterfile, new Hints(Hints.USE_JAI_IMAGEREAD, true));
		} else {
			writer = new GeoTiffWriter(rasterfile);
		}
		writer.write(coverage);
		return new SPLRasterFile(rasterfile);
	}

	/**
	 * Gets the geometry type.
	 *
	 * @param geoms
	 *            the geoms
	 * @return the geometry type
	 */
	/*
	 * retrieve the string formated type for this geometry
	 */
	private String getGeometryType(final Collection<Geometry> geoms) {
		String geomType = "";
		for (final Geometry geom : geoms) {
			if (geom != null) {
				geomType = geom.getClass().getSimpleName();
				if (geom.getNumGeometries() > 1) {
					if (geom.getGeometryN(0).getClass() == Point.class) {
						geomType = MultiPoint.class.getSimpleName();
					} else if (geom.getGeometryN(0).getClass() == LineString.class) {
						geomType = MultiLineString.class.getSimpleName();
					} else if (geom.getGeometryN(0).getClass() == Polygon.class) {
						geomType = MultiPolygon.class.getSimpleName();
					}
					break;
				}
			}
		}

		if ("DynamicLineString".equals(geomType)) { geomType = LineString.class.getSimpleName(); }
		return geomType;
	}

	/**
	 * Chek file.
	 *
	 * @param expectedExtension
	 *            the expected extension
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InvalidGeoFormatException
	 *             the invalid geo format exception
	 */
	/*
	 * Check if file exists and is consistent with expected extension
	 */
	private void chekFile(final SPLGisFileExtension... expectedExtension)
			throws FileNotFoundException, InvalidGeoFormatException {
		if (this.gisFile == null || !Files.exists(gisFile.toPath()))
			throw new FileNotFoundException("GIS file " + gisFile + " is unidentified");
		if (Stream.of(expectedExtension)
				.noneMatch(ext -> FilenameUtils.getExtension(this.gisFile.getName()).equals(ext.toString()))) {
			String[] pathArray = gisFile.getPath().split(File.separator);
			throw new InvalidGeoFormatException(pathArray[pathArray.length - 1],
					Arrays.asList(SPLGisFileExtension.values()));
		}
	}

	/**
	 * Chek and rename file.
	 *
	 * @param expectedExtension
	 *            the expected extension
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Check if file exists and insure extension compliance
	 */
	private boolean chekAndRenameFile(final SPLGisFileExtension expectedExtension) throws IOException {
		try {
			chekFile(expectedExtension);
			return true;
		} catch (FileNotFoundException e) {
			return this.gisFile.createNewFile();
		} catch (InvalidGeoFormatException e) {
			return gisFile.renameTo(
					new File(FilenameUtils.removeExtension(gisFile.getAbsolutePath()) + "." + expectedExtension));
		}
	}
}
