package spll.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.BasicFeatureTypes;
import org.geotools.geometry.Envelope2D;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.util.data.GSDataParser;
import core.util.exception.GSIllegalRangedData;
import spll.io.SPLRasterFile;
import spll.util.SpllGeotoolsAdapter;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The factory to safely create Spll geographical entity
 * <p>
 * TODO: no need now to store all possible attribute to avoid duplicate
 *
 * @author kevinchapuis
 *
 */
public class GeoEntityFactory {

	public static String ATTRIBUTE_PIXEL_BAND = "Band";
	public static String ATTRIBUTE_FEATURE_POP = "Population";

	private final Map<String, Attribute<? extends IValue>> featureAttributes;

	private SimpleFeatureBuilder contingencyFeatureBuilder;

	private Map<Feature, SpllFeature> feature2splFeature;

	public GeoEntityFactory(final Map<Feature, SpllFeature> feature2splFeature) {
		this.featureAttributes = new HashMap<>();
		this.feature2splFeature = feature2splFeature;
	}

	/**
	 * Defines the set of attributes for entities to be created. This set will be the support to add new value and
	 * recall them to avoid duplicates
	 *
	 * WARNING: this constructor can only create {@link SpllPixel} TODO: make a unique constructor OR switch to a
	 * builder
	 *
	 * @param attributes
	 */
	public GeoEntityFactory(final Set<Attribute<? extends IValue>> attributes) {
		this.featureAttributes =
				attributes.stream().collect(Collectors.toMap(Attribute::getAttributeName, Function.identity()));
	}

	/**
	 * In addition to set of attribute, also defines a way to create Geotools SimpleFeature to facilitate the creation
	 * of vector style geo entity
	 *
	 * @param attributes
	 * @param featureTypeName
	 * @param crs
	 * @param geomClazz
	 */
	public GeoEntityFactory(final Set<Attribute<? extends IValue>> attributes, final SimpleFeatureType schema) {
		this(attributes);
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("SimpleFeatureTypeBuilder");
		builder.setCRS(schema.getCoordinateReferenceSystem()); // <- Coordinate reference system
		builder.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, schema.getGeometryDescriptor().getType().getBinding());
		DEBUG.OUT("Setup a builder ({}) with {} geometry and [{}] attribute list" + builder.getName() + ","
				+ schema.getGeometryDescriptor().getType().getBinding().getSimpleName() + ","
				+ attributes.stream().map(Attribute::getAttributeName).collect(Collectors.joining(", ")));
		for (Attribute<? extends IValue> attribute : attributes) {
			builder.add(attribute.getAttributeName(),
					attribute.getValueSpace().getValues().stream().allMatch(value -> value.getType().isNumericValue())
							? Number.class : String.class);
		}

		this.contingencyFeatureBuilder = new SimpleFeatureBuilder(builder.buildFeatureType());
	}

	// ---------------------------------------------------------- //

	/**
	 * Create a vector style entity
	 *
	 * @param feature
	 * @return
	 * @throws GSIllegalRangedData
	 */
	public SpllFeature createGeoEntity(final Feature feature, final List<String> attList) throws GSIllegalRangedData {

		SpllFeature cached = feature2splFeature.get(feature);
		if (cached != null) return cached;

		Map<Attribute<? extends IValue>, IValue> values = new HashMap<>();

		Collection<Property> propertyList = feature.getProperties().stream()
				.filter(property -> !BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME.equals(property.getName().getLocalPart())
						&& (attList.isEmpty() || attList.contains(property.getName().getLocalPart())))
				.collect(Collectors.toSet());
		for (Property property : propertyList) {
			String name = property.getName().getLocalPart();
			Attribute<? extends IValue> attribute = null;
			attribute = featureAttributes.get(name);
			if (attribute == null) {
				// if the corresponding attribute does not yet exist, we create it on the fly
				attribute = SpllGeotoolsAdapter.getInstance().getAttribute(property);
				featureAttributes.put(name, attribute);
			}
			if (attribute == null)
				throw new NullPointerException("the attribute for " + property.getName() + " is null");

			if (attribute.getValueSpace() == null)
				throw new NullPointerException("the value space of attribute " + attribute + " is null");

			Object v = property.getValue();
			if (v == null) {
				values.put(attribute, attribute.getValueSpace().getEmptyValue());
			} else if (attribute.getValueSpace().isValidCandidate(v.toString())) {
				try {
					IValue val = attribute.getValueSpace().getValue(v.toString());
					if (val == null) {
						values.put(attribute, attribute.getValueSpace().addValue(v.toString()));
					} else {
						values.put(attribute, val);
					}
				} catch (NullPointerException e) {
					values.put(attribute, attribute.getValueSpace().addValue(v.toString()));
				}
			} else
				throw new IllegalArgumentException("Cannot add value " + v.toString() + " to attribute " + attribute
						+ " because it is not valide data [ValueSpace =  " + attribute.getValueSpace());
		}

		cached = new SpllFeature(values, feature);
		feature2splFeature.put(feature, cached);
		return cached;
	}

	/**
	 * Create a vector style entity based on a geometry (as defined by JTS and a list of pair attribute::value defined
	 * by Gen*)
	 *
	 * @param the_geom
	 * @param featureValues
	 * @return
	 */
	public SpllFeature createGeoEntity(final Geometry the_geom,
			final Map<Attribute<? extends IValue>, IValue> featureValues) {
		GSDataParser gsdp = new GSDataParser();
		// Use factory defined feature constructor to build the inner feature
		contingencyFeatureBuilder.add(the_geom);
		for (Attribute<? extends IValue> att : featureValues.keySet()) {
			String name = att.getAttributeName();
			IValue val = featureValues.get(att);
			if (val == null) { continue; }
			Object valObj =
					att.getValueSpace().getType().isNumericValue() ? gsdp.getNumber(val.toString()) : val.toString();
			contingencyFeatureBuilder.set(name, valObj);
		}
		Feature feat = contingencyFeatureBuilder.buildFeature(null);

		// Add non previously encountered attribute to attributes set
		for (Attribute<? extends IValue> att : featureValues.keySet())
			if (!featureAttributes.containsValue(att)) { featureAttributes.put(att.getAttributeName(), att); }

		// Return created GSFeature
		return new SpllFeature(featureValues, feat);
	}

	/**
	 * Create a raster style entity
	 *
	 * @param pixelBands
	 * @param pixel
	 * @param gridX
	 * @param gridY
	 * @return
	 */
	public SpllPixel createGeoEntity(final Number[] pixelBands, final Envelope2D pixel, final int gridX,
			final int gridY) {
		Map<Attribute<? extends ContinuousValue>, ContinuousValue> values = new HashMap<>();
		Set<Attribute<ContinuousValue>> pixelAttributes = new HashSet<>();
		for (int i = 0; i < pixelBands.length; i++) {
			String bandsName = ATTRIBUTE_PIXEL_BAND + i;
			Attribute<ContinuousValue> attribute = null;
			Optional<Attribute<ContinuousValue>> opAtt =
					pixelAttributes.stream().filter(att -> att.getAttributeName().equals(bandsName)).findAny();

			if (opAtt.isPresent()) {
				attribute = opAtt.get();
			} else {
				attribute = AttributeFactory.getFactory().createContinueAttribute(bandsName);
				attribute.getValueSpace().addExceludedValue(SPLRasterFile.DEF_NODATA.toString());
				pixelAttributes.add(attribute);
			}

			values.put(attribute, attribute.getValueSpace().getInstanceValue(pixelBands[i].toString()));
		}
		return new SpllPixel(values, pixel, gridX, gridY);
	}

}
