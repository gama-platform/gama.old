package spll.util;

import java.util.Set;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.BasicFeatureTypes;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;
import core.util.exception.GSIllegalRangedData;

/**
 * A simple utility class that enable to transpose geotools object and variable into Gen* object
 * 
 * @author kevinchapuis
 *
 */
public class SpllGeotoolsAdapter {

	private static SpllGeotoolsAdapter gaw = new SpllGeotoolsAdapter();
	
	private SpllGeotoolsAdapter() {};
	
	public static SpllGeotoolsAdapter getInstance() {
		return gaw;
	}
	
	/**
	 * Establish a Geotools feature type from a set of Genstar attribute 
	 * <p>
	 * From a set of {@link Attribute} to a {@link SimpleFeatureType}
	 * 
	 * @param attributes
	 * @return
	 */
	public SimpleFeatureType getGeotoolsFeatureType(String name,
			Set<Attribute<? extends IValue>> attributes, CoordinateReferenceSystem crs,
			GeometryDescriptor geometry) {
		SimpleFeatureTypeBuilder stb = new SimpleFeatureTypeBuilder();
		stb.setName(name);
		stb.setCRS(crs);
		stb.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, geometry.getType().getBinding());
		attributes.stream().forEach(att -> stb.add(att.getAttributeName(), IValue.class));
		return stb.buildFeatureType();
	}
	
	/**
	 * Transpose a Geotools property into a Genstar attribute
	 * <p>
	 * From {@link Property} to {@link Attribute} with basic value transposition that can be of type
	 * {@link GSEnumDataType#Boolean} {@link GSEnumDataType#Continue} {@link GSEnumDataType#Integer} {@link GSEnumDataType#Nominal}
	 * 
	 * @param property
	 * @return
	 * @throws GSIllegalRangedData 
	 */
	public Attribute<? extends IValue> getAttribute(Property property) throws GSIllegalRangedData {
		
		return AttributeFactory.getFactory().createAttribute(
					property.getName().getLocalPart(), 
					GSEnumDataType.getTypeForJavaType(property.getType().getBinding())
					);
	}
	
}
