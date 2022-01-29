/*******************************************************************************************************
 *
 * DXFBlock.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;

import msi.gama.ext.kabeja.dxf.helpers.Point;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFBlock {

	/** The type. */
	public static String TYPE = "BLOCK";

	/** The reference point. */
	private Point referencePoint;

	/** The layer ID. */
	private String layerID = DXFConstants.DEFAULT_LAYER;

	/** The name. */
	private String name = "";

	/** The description. */
	private String description = "";

	/** The entities. */
	private final ArrayList<DXFEntity> entities;

	/** The doc. */
	private DXFDocument doc;

	/**
	 *
	 */
	public DXFBlock() {
		this.entities = new ArrayList<>();
		this.referencePoint = new Point();
	}

	/**
	 * Gets the bounds.
	 *
	 * @return the bounds
	 */
	public Bounds getBounds() {
		// first set the own point
		Bounds bounds = new Bounds();
		Iterator<DXFEntity> i = entities.iterator();

		if (i.hasNext()) {
			while (i.hasNext()) {
				DXFEntity entity = i.next();
				Bounds b = entity.getBounds();

				if (b.isValid()) { bounds.addToBounds(b); }
			}
		} else {
			bounds.setValid(false);
		}

		return bounds;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() { return description; }

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(final String description) { this.description = description; }

	/**
	 * @return Returns the p.
	 */
	public Point getReferencePoint() { return referencePoint; }

	/**
	 * @param p
	 *            The p to set.
	 */
	public void setReferencePoint(final Point p) { this.referencePoint = p; }

	/**
	 * Adds the DXF entity.
	 *
	 * @param entity
	 *            the entity
	 */
	public void addDXFEntity(final DXFEntity entity) {
		entities.add(entity);
	}

	/**
	 *
	 * @return a iterator over all entities of this block
	 */
	public Iterator getDXFEntitiesIterator() { return entities.iterator(); }

	/**
	 * @return Returns the layerID.
	 */
	public String getLayerID() { return layerID; }

	/**
	 * @param layerID
	 *            The layerID to set.
	 */
	public void setLayerID(final String layerID) { this.layerID = layerID; }

	/**
	 * @return Returns the name.
	 */
	public String getName() { return name; }

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(final String name) { this.name = name; }

	/**
	 * @param doc
	 *            The doc to set.
	 */
	public void setDXFDocument(final DXFDocument doc) {
		this.doc = doc;

		Iterator i = entities.iterator();

		while (i.hasNext()) {
			DXFEntity entity = (DXFEntity) i.next();
			entity.setDXFDocument(doc);
		}
	}

	/**
	 *
	 * @return the parent document
	 */
	public DXFDocument getDXFDocument() { return this.doc; }

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public double getLength() {
		double length = 0;
		Iterator i = entities.iterator();

		while (i.hasNext()) {
			DXFEntity entity = (DXFEntity) i.next();
			length += entity.getLength();
		}

		return length;
	}

	/**
	 * Gets the
	 *
	 * @see DXFEntity with the specified ID.
	 * @param id
	 *            of the
	 * @see DXFEntity
	 * @return the
	 * @see DXFEntity with the specified ID or null if there is no
	 * @see DXFEntity with the specified ID
	 */
	public DXFEntity getDXFEntityByID(final String id) {
		DXFEntity entity = null;
		Iterator i = this.entities.iterator();

		while (i.hasNext()) {
			DXFEntity e = (DXFEntity) i.next();

			if (e.getID().equals(id)) return e;
		}

		return entity;
	}
}
