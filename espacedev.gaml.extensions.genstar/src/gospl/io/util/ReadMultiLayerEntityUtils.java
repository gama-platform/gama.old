package gospl.io.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.data.GSDataParser;
import gospl.GosplEntity;

/**
 * Utilities to encapsulate information on entities when read from data sources
 * 
 * @author kevinchapuis
 *
 */
public class ReadMultiLayerEntityUtils {

	private String id;
	private String weight;
	
	private int layer;
	
	private Map<Integer,String> ids;
	
	private Map<Attribute<? extends IValue>,IValue> entity;
	
	public static final GSDataParser gsdp = new GSDataParser();
	
	public ReadMultiLayerEntityUtils(int layer, String id, String weight) {
		this.layer = layer;
		this.id = id;
		this.weight = weight;
		this.ids = new HashMap<>();
	}
	
	public ReadMultiLayerEntityUtils(int layer, String id, String weight, Map<Attribute<? extends IValue>,IValue> entity) {
		this(layer,id,weight);
		this.entity = entity;
	}
	
	public int getLayer() {return layer;}
	public String getId() {return id;}
	public String getWgt() {return weight;}
	
	public Map<Attribute<? extends IValue>,IValue> getEntity() {return entity;}
	public void setEntity(Map<Attribute<? extends IValue>,IValue> entity) {this.entity = entity;}
	
	public Map<Integer,String> getIDs() {return Collections.unmodifiableMap(ids);}
	public void setIDs(Map<Integer,String> ids) {this.ids = ids;}
	
	public GosplEntity toGosplEntity() {
		return this.toGosplEntity(false);
	}
	
	public GosplEntity toGosplEntity(boolean withID) {
		GosplEntity entity = new GosplEntity(this.entity, gsdp.getDouble(weight));
		if(withID) { entity._setEntityId(id); }
		return entity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (layer == 0) { return false; } // NASTY TRICKS TO ALLOW FIRST ORDER ENTITY TO NEVER BE EQUAL
		ReadMultiLayerEntityUtils other = (ReadMultiLayerEntityUtils) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	
}
