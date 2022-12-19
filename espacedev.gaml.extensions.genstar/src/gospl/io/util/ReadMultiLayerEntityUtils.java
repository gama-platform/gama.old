/*******************************************************************************************************
 *
 * ReadMultiLayerEntityUtils.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.io.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

	/** The id. */
	private final String id;

	/** The weight. */
	private final String weight;

	/** The layer. */
	private final int layer;

	/** The ids. */
	private Map<Integer, String> ids;

	/** The entity. */
	private Map<Attribute<? extends IValue>, IValue> entity;

	/** The Constant gsdp. */
	public static final GSDataParser gsdp = new GSDataParser();

	/**
	 * Instantiates a new read multi layer entity utils.
	 *
	 * @param layer
	 *            the layer
	 * @param id
	 *            the id
	 * @param weight
	 *            the weight
	 */
	public ReadMultiLayerEntityUtils(final int layer, final String id, final String weight) {
		this.layer = layer;
		this.id = id;
		this.weight = weight;
		this.ids = new HashMap<>();
	}

	/**
	 * Instantiates a new read multi layer entity utils.
	 *
	 * @param layer
	 *            the layer
	 * @param id
	 *            the id
	 * @param weight
	 *            the weight
	 * @param entity
	 *            the entity
	 */
	public ReadMultiLayerEntityUtils(final int layer, final String id, final String weight,
			final Map<Attribute<? extends IValue>, IValue> entity) {
		this(layer, id, weight);
		this.entity = entity;
	}

	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public int getLayer() { return layer; }

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() { return id; }

	/**
	 * Gets the wgt.
	 *
	 * @return the wgt
	 */
	public String getWgt() { return weight; }

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public Map<Attribute<? extends IValue>, IValue> getEntity() { return entity; }

	/**
	 * Sets the entity.
	 *
	 * @param entity
	 *            the entity
	 */
	public void setEntity(final Map<Attribute<? extends IValue>, IValue> entity) { this.entity = entity; }

	/**
	 * Gets the i ds.
	 *
	 * @return the i ds
	 */
	public Map<Integer, String> getIDs() { return Collections.unmodifiableMap(ids); }

	/**
	 * Sets the I ds.
	 *
	 * @param ids
	 *            the ids
	 */
	public void setIDs(final Map<Integer, String> ids) { this.ids = ids; }

	/**
	 * To gospl entity.
	 *
	 * @return the gospl entity
	 */
	public GosplEntity toGosplEntity() {
		return this.toGosplEntity(false);
	}

	/**
	 * To gospl entity.
	 *
	 * @param withID
	 *            the with ID
	 * @return the gospl entity
	 */
	public GosplEntity toGosplEntity(final boolean withID) {
		GosplEntity entity = new GosplEntity(this.entity, gsdp.getDouble(weight));
		if (withID) { entity._setEntityId(id); }
		return entity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entity, id, weight);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass() || layer == 0) return false;
		ReadMultiLayerEntityUtils other = (ReadMultiLayerEntityUtils) obj;
		return Objects.equals(entity, other.entity) && Objects.equals(id, other.id)
				&& Objects.equals(weight, other.weight);

	}

}
