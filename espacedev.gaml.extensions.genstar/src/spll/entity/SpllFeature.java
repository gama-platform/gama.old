package spll.entity;

import java.util.Map;

import org.opengis.feature.Feature;

import org.locationtech.jts.geom.Geometry;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

/**
 * A Feature is an entity read from a vector style GIS file.
 * It might have attributes which are less detailed or qualified than DemoAttributes. 
 * It is not generated, but more read-only.
 * 
 * @author Kevin Chapuis
 *
 */
public class SpllFeature extends AGeoEntity<IValue> {
 
	private final Feature innerFeature;
	
	protected SpllFeature(Map<Attribute<? extends IValue>, IValue> values, Feature innerFeature) {
		super(values, innerFeature.getIdentifier().getID());
		this.innerFeature = innerFeature;
	}
	
	public Feature getInnerFeature(){
		return innerFeature;
	}
	
	@Override
	public double getArea() {
		return this.getProxyGeometry().getArea();
	}
	
	@Override
	public Geometry getGeometry() {
		return (Geometry) innerFeature.getDefaultGeometryProperty().getValue();
	}
	
	@Override
	public String getGenstarName(){
		return innerFeature.getIdentifier().getID();
	}
	
	@Override
	public String toString() {
		return "Feature of type: "+innerFeature.getType()+" with default geometry being "+this.getGeometry().getGeometryType();
	}


}
