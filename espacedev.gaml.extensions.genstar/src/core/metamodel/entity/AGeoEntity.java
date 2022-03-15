package core.metamodel.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.tag.EntityTag;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.IntegerValue;
import core.util.data.GSDataParser;

public abstract class AGeoEntity<V extends IValue> implements IEntity<Attribute<? extends V>> {
 
	/**
	 * The unique identifier of the entity. See {@link EntityUniqueId}
	 */
	private String id = null;
		
	private String gsName;
	
	private Map<Attribute<? extends V>, V> attributes;
	

	/**
	 * The proxy use for the localization of agents and area computation: 
	 * by default, the geometry of the entity
	 */
	protected Geometry proxyGeometry;
	
	/**
	 * The type of the agent (like "household" or "building"), 
	 * or null if undefined
	 */
	private String type;
	
	
	public AGeoEntity(Map<Attribute<? extends V>, V> attributes, String id) {
		this.attributes = attributes;
		this.gsName = id;
	}

	@Override
	public Collection<Attribute<? extends V>> getAttributes() {
		return attributes.keySet();
	}
	
	@Override
	public Map<Attribute<? extends V>, IValue> getAttributeMap() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public boolean hasAttribute(Attribute<? extends V> attribute) {
		return attributes.containsKey(attribute);
	}

	@Override
	public Collection<V> getValues() {
		return attributes.values();
	}

	@Override
	public V getValueForAttribute(Attribute<? extends V> attribute) {
		return attributes.get(attribute);
	}

	@Override
	public V getValueForAttribute(String property) {
		return attributes.get(attributes.keySet().stream()
				.filter(att -> att.getAttributeName().equals(property)).findAny().get());
	}
	
	// ---------------------- Geo-location contract for GeoEntities ---------------------- //
	
	/**
	 * Translate the given value into numeric data. If this value
	 * is not of number type would return NaN
	 * 
	 * @param attribute
	 * @return
	 */
	public Number getNumericValueForAttribute(Attribute<? extends V> attribute) {
		if(attribute == null) {throw new IllegalArgumentException("Cannot get numeric value for \""+attribute+"\" attribute");}
		if(attribute.getValueSpace().getType().isNumericValue())
			return new GSDataParser().parseNumbers(this.getValueForAttribute(attribute).getStringValue());
		return Double.NaN;
	}
	
	/**
	 * Based on #getNumericValueForAttribute(Attribute) method, using 
	 * {@link #getValueForAttribute(String)} to retrieve proper attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public Number getNumericValueForAttribute(String attribute) {
		return this.getNumericValueForAttribute(attributes.keySet().stream()
				.filter(att -> att.getAttributeName().equals(attribute)).findAny().get());
	}
	
	/**
	 * The geometry charcaterizes the attribute
	 * 
	 * @return {@link Geometry}
	 */
	public abstract Geometry getGeometry();
	
	/**
	 * Gives the name of this attribute
	 * 
	 * @return
	 */
	public String getGenstarName(){
		return gsName;
	}
	
	/**
	 * A collection of property name for this geographical attribute
	 * 
	 * @return
	 */
	public Collection<String> getPropertiesAttribute(){
		return this.getAttributes().stream()
				.map(Attribute::getAttributeName).collect(Collectors.toList());
	}
	
	/**
	 * Gives the area (metrics refers to geometry's crs) of this attribute
	 * 
	 * @return
	 */
	public double getArea(){
		return getProxyGeometry().getArea();
	}
	
	@Override
	public final void _setEntityId(String novelid) throws IllegalStateException {
		if (this.id != null)
			throw new IllegalArgumentException("cannot change the identifier of an agent; "+
						"this agent already had id "+this.id+" but we were asked "+
					"to change it for "+novelid);
		this.id = novelid;
	}

	@Override
	public final String getEntityId() throws IllegalStateException {
		if (this.id == null)
			throw new IllegalStateException("no id is defined yet for agent "+this.toString());
		return this.id;
	}
	
	@Override
	public final boolean _hasEntityId() {
		return this.id != null;
	}

	/**
	 * The point characterizes the attribute location
	 * 
	 * @return {@link Point}
	 */
	public Point getLocation() {
		return getGeometry().getCentroid();
	}
	
	@Override
	public final boolean hasEntityType() {
		return type != null;
	}

	@Override
	public final String getEntityType() {
		return type;
	}

	@Override
	public void setEntityType(String type) {
		this.type = type;
	}
	

	@Override
	public boolean hasParent() {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public IEntity<? extends IAttribute<? extends IValue>> getParent() {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public void setParent(IEntity<? extends IAttribute<? extends IValue>> e) {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public boolean hasChildren() {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public IntegerValue getCountChildren() {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public Set<IEntity<? extends IAttribute<? extends IValue>>> getChildren() {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}

	@Override
	public void addChild(IEntity<? extends IAttribute<? extends IValue>> e) {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");	}

	@Override
	public void addChildren(Collection<IEntity<? extends IAttribute<? extends IValue>>> e) {
		throw new NotImplementedException("geo entities are not compliant with the multilevel population framework");
	}
	
	@Override
	public boolean hasTags(EntityTag... tags) {
		throw new NotImplementedException("geo entities have no entity tag");
	}
	
	@Override
	public Collection<EntityTag> getTags() {
		throw new NotImplementedException("geo entities have no entity tag (yet)");
	}
	
	public Geometry getProxyGeometry() {
		return proxyGeometry == null ? getGeometry() : proxyGeometry;
	}

	public void setProxyGeometry(Geometry proxyGeometry) {
		this.proxyGeometry = proxyGeometry;
	}


	
}
