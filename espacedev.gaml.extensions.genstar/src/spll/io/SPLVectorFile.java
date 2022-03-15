package spll.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.type.BasicFeatureTypes;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.index.quadtree.Quadtree;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import core.util.excpetion.GSIllegalRangedData;
import core.util.random.GenstarRandom;
import spll.entity.GeoEntityFactory;
import spll.entity.SpllFeature;
import spll.entity.iterator.GSFeatureIterator;
import spll.io.exception.InvalidGeoFormatException;
import spll.util.SpllGeotoolsAdapter;
import spll.util.SpllUtil;

/**
 * The higher order implementation of geographic vector file in the SPLL library
 * 
 * WARNING: purpose of this file is to cover the wider number of template for geographic vector files,
 * <i>but</i> in practice it can only stand for standard shapefile
 * 
 * @author kevinchapuis
 *
 */
public class SPLVectorFile implements IGSGeofile<SpllFeature, IValue> {

	private Set<SpllFeature> features = null;

	private final DataStore dataStore;
	private final CoordinateReferenceSystem crs;

	/**
	 * Maps features with SPL features created from them. 
	 * Avoids to recreate a novel one. 
	 * Also makes users able to get the same object again for the same 
	 * query.
	 */
	private Map<Feature, SpllFeature> feature2SPLFeature = new HashMap<>(10000);
	
	
	/**
	 * In this constructor {@link SpllFeature} and {@code dataStore} provide the side of the
	 * same coin: {@link SpllFeature} set must contains all {@link Feature} of the {@code dataStore}
	 * 
	 * @param dataStore
	 * @param features
	 * @throws IOException
	 */
	protected SPLVectorFile(DataStore dataStore, Set<SpllFeature> features) throws IOException {
		this.dataStore = dataStore;
		this.features = features;
		SimpleFeatureType schema = dataStore.getSchema(dataStore.getTypeNames()[0]);
		this.crs = schema.getCoordinateReferenceSystem();
		
	}

	/**
	 * In this constructor {@link SpllFeature} are build from the {@link Feature} contains in the {@code dataStore}
	 * 
	 * @param dataStore
	 * @param attributes
	 * @throws IOException
	 * @throws GSIllegalRangedData 
	 */
	protected SPLVectorFile(DataStore dataStore, List<String> attributes) throws IOException, GSIllegalRangedData {
		this.dataStore = dataStore;
		this.crs = dataStore.getSchema(dataStore.getTypeNames()[0]).getCoordinateReferenceSystem();
		FeatureSource<SimpleFeatureType,SimpleFeature> fSource = dataStore
				.getFeatureSource(dataStore.getTypeNames()[0]);
		features = new HashSet<>();
		FeatureIterator<SimpleFeature> fItt = DataUtilities.collection(fSource.getFeatures(Filter.INCLUDE)).features();
		GeoEntityFactory gef = new GeoEntityFactory(feature2SPLFeature);
		while (fItt.hasNext()) {
			SimpleFeature f = fItt.next();
			SpllFeature sf = gef.createGeoEntity(f, attributes);
			features.add(sf);
			feature2SPLFeature.put(f, sf);
		}
	}

	protected SPLVectorFile(File file, Charset charset, List<String> attributes) throws IOException, GSIllegalRangedData{
		this(readDataStoreFromFile(file, charset), attributes);
	}

	protected SPLVectorFile(File file, Charset charset) throws IOException, GSIllegalRangedData{
		this(readDataStoreFromFile(file, charset), Collections.emptyList());
	}
	 
	private static DataStore readDataStoreFromFile(File file, Charset charset) throws IOException {
		
		Map<String,Object> parameters = new HashMap<>();
		parameters.put("url", file.toURI().toURL());
		DataStore datastore = DataStoreFinder.getDataStore(parameters);
		
		// set the charset (if possible)
		if (charset != null 
			&& datastore instanceof ShapefileDataStore) {
			((ShapefileDataStore)datastore).setCharset(charset);
		}
		return datastore;
	}
	
	// ------------------- GENERAL CONTRACT ------------------- //

	@Override
	public GeoGSFileType getGeoGSFileType() {
		return GeoGSFileType.VECTOR;
	}

	@Override
	public boolean isCoordinateCompliant(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> file) {
		CoordinateReferenceSystem thisCRS = null, fileCRS = null;
		thisCRS = SpllUtil.getCRSfromWKT(this.getWKTCoordinateReferentSystem());
		fileCRS = SpllUtil.getCRSfromWKT(file.getWKTCoordinateReferentSystem());
		if (thisCRS == null && fileCRS == null) return false;
		if (thisCRS.equals(fileCRS)) return true;
		Integer codeThis = null;
		Integer codeFile = null;
		try {
			codeThis = CRS.lookupEpsgCode(thisCRS, true);
			codeFile = CRS.lookupEpsgCode(fileCRS, true);
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		return codeThis == null && codeFile == null ? false : codeFile.equals(codeThis) ;
	}

	@Override
	public String getWKTCoordinateReferentSystem() {
		return crs.toWKT();
	}

	@Override
	public Envelope getEnvelope() throws IOException {
		return new ReferencedEnvelope(dataStore.getFeatureSource(dataStore.getTypeNames()[0]).getBounds());
	}
	
	@Override
	public IGSGeofile<SpllFeature, IValue> transferTo(File destination,
			Map<? extends AGeoEntity<? extends IValue>,Number> transfer,
			Attribute<? extends IValue> attribute) throws IllegalArgumentException, IOException {
		if(features.stream().anyMatch(feat -> !transfer.containsKey(feat)))
			throw new IllegalArgumentException("There is a mismatch between provided set of geographical entity and "
					+ "geographic entity of this SPLVector file "+this.toString());
		
		Set<Attribute<? extends IValue>> attrSet = new HashSet<Attribute<? extends IValue>>();
		attrSet.add(attribute);
		GeoEntityFactory gef = new GeoEntityFactory(
				attrSet, 
				SpllGeotoolsAdapter.getInstance().getGeotoolsFeatureType(
						attribute.toString(), 
						attrSet, 
						this.crs, 
						this.dataStore.getFeatureSource(dataStore.getTypeNames()[0]).getSchema().getGeometryDescriptor()));
		
		Collection<SpllFeature> newFeatures = new HashSet<>();
		for(AGeoEntity<? extends IValue> entity : this.features) {
			Map<Attribute<? extends IValue>, IValue> theMap = new HashMap<>();
			theMap.put(attribute, attribute.getValueSpace().getInstanceValue(transfer.get(entity).toString()));
			newFeatures.add(gef.createGeoEntity(entity.getGeometry(), theMap));
		}
		
		IGSGeofile<SpllFeature, IValue> res = null;
		try {
			res =  new SPLGeofileBuilder().setFeatures(newFeatures).setFile(destination).buildShapeFile();
		} catch (SchemaException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InvalidGeoFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
		return res;
	}

	// ---------------------------------------------------------------- //
	// ----------------------- ACCESS TO VALUES ----------------------- //
	// ---------------------------------------------------------------- //


	@Override
	public Collection<SpllFeature> getGeoEntity() {
		return Collections.unmodifiableSet(features);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * TODO: leave parallel processing option open
	 * 
	 * @return
	 */
	@Override
	public Collection<Attribute<? extends IValue>> getGeoAttributes() {
		return features.stream().flatMap(f -> f.getAttributes().stream())
				.collect(Collectors.toSet());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * TODO: leave parallel processing option open
	 * 
	 * @return
	 */
	@Override
	public Collection<IValue> getGeoValues() {
		return features.stream().flatMap(f -> f.getValues().stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Iterator<SpllFeature> getGeoEntityIterator() {
		return new GSFeatureIterator(dataStore, feature2SPLFeature);
	}

	@Override
	public Iterator<SpllFeature> getGeoEntityIteratorWithin(Geometry geom) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
		Filter filter = ff.within(ff.property( BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME), ff.literal( geom ));
		return new GSFeatureIterator(dataStore, filter, feature2SPLFeature);
	}


	@Override
	public Collection<SpllFeature> getGeoEntityWithin(Geometry geom) {
		Set<SpllFeature> collection = new HashSet<>(); 
		getGeoEntityIteratorWithin(geom).forEachRemaining(collection::add);
		return collection;
	}

	@Override
	public Iterator<SpllFeature> getGeoEntityIteratorIntersect(Geometry geom) {
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
		Filter filter = ff.intersects(ff.property( BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME), ff.literal( geom ));
		return new GSFeatureIterator(dataStore, filter, feature2SPLFeature);
	}

	@Override
	public Collection<SpllFeature> getGeoEntityIntersect(Geometry geom) {
		Set<SpllFeature> collection = new HashSet<>(); 
		getGeoEntityIteratorIntersect(geom).forEachRemaining(collection::add);
		return collection;
	}

	public DataStore getStore() {
		return dataStore;
	}
	
	/**
	 * Associate a proxy geometry to each spatial entity (#SpllFeature) that correspond to the area
	 * at minDist and maxDist from the original geometry
	 * 
	 * @param minDist
	 * @param maxDist
	 * @param avoidOverlapping
	 */
	public void minMaxDistance(Double minDist, Double maxDist, Boolean avoidOverlapping)  {
		Quadtree quadTreeMin = null;
		if (minDist != null && minDist > 0) {
			quadTreeMin = new Quadtree();
			for (SpllFeature ft : features) {
				Geometry g = ft.getGeometry().buffer(minDist);	
				try {
					quadTreeMin.insert(g.getEnvelopeInternal(), g);
				} catch (Exception e){ quadTreeMin = null;}
				
			}
		}
		Quadtree quadTreeOverlap = null;
		if (avoidOverlapping) quadTreeOverlap = new Quadtree();
		for (SpllFeature ft : features) {
			 Geometry newGeom = ft.getGeometry().buffer(maxDist);
			 if (quadTreeMin != null && ! quadTreeMin.isEmpty()) {
				 @SuppressWarnings("unchecked")
				List<Geometry> intersection = quadTreeMin.query(newGeom.getEnvelopeInternal());
				 for (Geometry g : intersection) {
					 if (g.isEmpty()) continue;
					 newGeom =  SpllUtil.difference(newGeom, g);
					 if (newGeom == null) break;
					 newGeom = manageGeometryCollection(newGeom);
					 
				 }
				 if (avoidOverlapping) {
					 try {
						 quadTreeOverlap.insert( newGeom.getEnvelopeInternal(), newGeom);
						} catch (Exception e){ quadTreeOverlap = null;}
					}
			 } else if (minDist > 0) {
				 for (SpllFeature ft2 : features) {
					if (ft == ft2) continue;
					Geometry newGeom2 = ft2.getProxyGeometry();
					if (newGeom2.isEmpty()) continue;
					newGeom =  SpllUtil.difference(newGeom, newGeom2);
					if (newGeom == null) break;
					newGeom = manageGeometryCollection(newGeom);
					 
				 }
				 if (avoidOverlapping) {
					 try {
						 quadTreeOverlap.insert( newGeom.getEnvelopeInternal(), newGeom);
						} catch (Exception e){ quadTreeOverlap = null;}
				 }
			 }
			ft.setProxyGeometry(newGeom);
		}
		
		if (avoidOverlapping) {
			List<SpllFeature> fts_overlap = new ArrayList<>(features);
			Collections.shuffle(fts_overlap, GenstarRandom.getInstance());
			
			for (SpllFeature ft : fts_overlap) {
				Geometry newGeom = ft.getProxyGeometry();
				 if (quadTreeOverlap != null && ! quadTreeOverlap.isEmpty()) {
					@SuppressWarnings("unchecked")
					List<Geometry> intersection =  quadTreeOverlap.query(newGeom.getEnvelopeInternal());
					 for (Geometry g : intersection) {
						if (g.isEmpty() ||  (g == ft.getGeometry())) continue;
						 newGeom =  SpllUtil.difference(newGeom, g);
						 if (newGeom == null) break;
						 newGeom = manageGeometryCollection(newGeom);
						
					 }
				 } else {
					 for (SpllFeature ft2 : fts_overlap) {
							if (ft == ft2) continue;
							Geometry newGeom2 = ft2.getProxyGeometry();
							if (newGeom2.isEmpty()) continue;
							newGeom =  SpllUtil.difference(newGeom, newGeom2);
							if (newGeom == null) break;
							newGeom = manageGeometryCollection(newGeom);
							 
						 }
				 }
				 ft.setProxyGeometry(newGeom);
			}
		}
	}
	
	private Geometry manageGeometryCollection(Geometry geom) {
		geom.buffer(0.0);
		if (geom.getArea() == 0) {
			if (geom.getLength() == 0) return geom.getFactory().createPoint(geom.getCoordinate());
			return geom.getFactory().createLineString(geom.getCoordinates());
		}
		if (geom instanceof GeometryCollection) {
			 List<Geometry> toKeep = new ArrayList<Geometry>();
			 for (int i = 0; i < geom.getNumGeometries(); i++) {
				 Geometry newGeom = geom.getGeometryN(i);
				 if (newGeom instanceof Polygon) {
					 toKeep.add(newGeom);
				 }
			 }
			 if (toKeep.size() == 1)
				 geom = toKeep.get(0);
			 else if (toKeep.size() > 1) {
				 Polygon[] polys = new Polygon[toKeep.size()];
				 for (int i = 0; i < toKeep.size(); i++) polys[i] = (Polygon) toKeep.get(i);
				 geom = geom.getFactory().createMultiPolygon(polys);
			 }
			 else {
				 return geom.getFactory().createPoint(geom.getCoordinate());
			 }
		 }
		return geom;
	}

	@Override
	public String toString() {
		String s = "";
		try {
			s = "Shapefile containing "+features.size()+" features of geometry type "+dataStore
					.getSchema(dataStore.getTypeNames()[0]).getGeometryDescriptor().getType();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

}
