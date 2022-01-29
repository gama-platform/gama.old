/*******************************************************************************************************
 *
 * DXFDocument.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.objects.DXFDictionary;
import msi.gama.ext.kabeja.dxf.objects.DXFObject;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 */
public class DXFDocument {

	/** The property encoding. */
	public static String PROPERTY_ENCODING = "encoding";

	/** The Constant DEFAULT_MARGIN. */
	public static final double DEFAULT_MARGIN = 5;

	/** The layers. */
	private final Hashtable<String, DXFLayer> layers = new Hashtable<>();

	/** The blocks. */
	private final Hashtable<String, DXFBlock> blocks = new Hashtable<>();

	/** The line types. */
	private final HashMap<String, DXFLineType> lineTypes = new HashMap<>();

	/** The dimension styles. */
	private final HashMap<String, DXFDimensionStyle> dimensionStyles = new HashMap<>();

	/** The text styles. */
	private final HashMap<String, DXFStyle> textStyles = new HashMap<>();

	// the user coordinate systems
	/** The properties. */
	// private Hashtable ucs = new Hashtable();
	private final Hashtable<String, String> properties = new Hashtable<>();

	/** The viewports. */
	private final List<DXFViewport> viewports = new ArrayList<>();

	/** The bounds. */
	private Bounds bounds = new Bounds();

	/** The header. */
	// private double margin;
	private DXFHeader header = new DXFHeader();

	/** The objects. */
	private final HashMap<String, Object> objects = new HashMap<>();

	/** The patterns. */
	private final HashMap<String, DXFHatchPattern> patterns = new HashMap<>();

	/** The views. */
	private final List<DXFView> views = new ArrayList<>();

	/** The root dictionary. */
	private DXFDictionary rootDictionary = new DXFDictionary();

	/**
	 * Instantiates a new DXF document.
	 */
	public DXFDocument() {
		// the defalut layer
		DXFLayer defaultLayer = new DXFLayer();
		defaultLayer.setDXFDocument(this);
		defaultLayer.setName(DXFConstants.DEFAULT_LAYER);
		this.layers.put(DXFConstants.DEFAULT_LAYER, defaultLayer);

		// setup the margin
		// this.margin = DEFAULT_MARGIN;

		// setup the root Dictionary
		this.rootDictionary = new DXFDictionary();
		this.rootDictionary.setDXFDocument(this);
	}

	/**
	 * Adds the DXF layer.
	 *
	 * @param layer
	 *            the layer
	 */
	public void addDXFLayer(final DXFLayer layer) {
		layer.setDXFDocument(this);
		layers.put(layer.getName(), layer);
	}

	/**
	 *
	 * Returns the specified layer.
	 *
	 * @param key
	 *            The layer id
	 * @return the layer or if not found the default layer (layer "0")
	 */
	public DXFLayer getDXFLayer(final String key) {
		if (this.layers.containsKey(key)) return layers.get(key);

		// retun the default layer
		if (this.layers.containsKey(DXFConstants.DEFAULT_LAYER)) return layers.get(DXFConstants.DEFAULT_LAYER);
		DXFLayer layer = new DXFLayer();
		layer.setName(DXFConstants.DEFAULT_LAYER);
		this.addDXFLayer(layer);

		return layer;
	}

	/**
	 * Returns true if the document contains the specified layer.
	 *
	 * @param layerName
	 *            the layer name
	 * @return true - if the document contains the layer, otherwise false
	 */
	public boolean containsDXFLayer(final String layerName) {
		return this.layers.containsKey(layerName);
	}

	/**
	 *
	 * @return the iterator over all DXFLayer of this document
	 */
	public Iterator getDXFLayerIterator() { return layers.values().iterator(); }

	/**
	 * Adds the DXF line type.
	 *
	 * @param ltype
	 *            the ltype
	 */
	public void addDXFLineType(final DXFLineType ltype) {
		lineTypes.put(ltype.getName(), ltype);
	}

	/**
	 * Gets the DXF line type.
	 *
	 * @param name
	 *            the name
	 * @return the DXF line type
	 */
	public DXFLineType getDXFLineType(final String name) {
		return lineTypes.get(name);
	}

	/**
	 *
	 * @return the iterator over all DXFLineTypes
	 */
	public Iterator getDXFLineTypeIterator() { return lineTypes.values().iterator(); }

	/**
	 * Adds the DXF entity.
	 *
	 * @param entity
	 *            the entity
	 */
	public void addDXFEntity(final DXFEntity entity) {
		entity.setDXFDocument(this);

		DXFLayer layer = this.getDXFLayer(entity.getLayerName());
		layer.addDXFEntity(entity);
	}

	/**
	 * Adds the DXF block.
	 *
	 * @param block
	 *            the block
	 */
	public void addDXFBlock(final DXFBlock block) {
		block.setDXFDocument(this);
		this.blocks.put(block.getName(), block);
	}

	/**
	 * Gets the DXF block.
	 *
	 * @param name
	 *            the name
	 * @return the DXF block
	 */
	public DXFBlock getDXFBlock(final String name) {
		return blocks.get(name);
	}

	/**
	 *
	 * @return the iterator over all DXFBlocks
	 */
	public Iterator getDXFBlockIterator() { return blocks.values().iterator(); }

	/**
	 * Sets the property.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setProperty(final String key, final String value) {
		this.properties.put(key, value);
	}

	/**
	 * Gets the property.
	 *
	 * @param key
	 *            the key
	 * @return the property
	 */
	public String getProperty(final String key) {
		if (properties.contains(key)) return properties.get(key);

		return null;
	}

	/**
	 * Checks for property.
	 *
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public boolean hasProperty(final String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * Returns the bounds of this document
	 *
	 * @return
	 */
	public Bounds getBounds() {
		this.bounds = new Bounds();

		Enumeration e = this.layers.elements();

		while (e.hasMoreElements()) {
			DXFLayer layer = (DXFLayer) e.nextElement();

			if (!layer.isFrozen()) {
				Bounds b = layer.getBounds();

				if (b.isValid()) { this.bounds.addToBounds(b); }
			}
		}

		return bounds;
	}

	/**
	 * Returns the bounds of this document
	 *
	 * @return
	 */
	public Bounds getBounds(final boolean onModelspace) {
		Bounds bounds = new Bounds();

		Enumeration e = this.layers.elements();

		while (e.hasMoreElements()) {
			DXFLayer layer = (DXFLayer) e.nextElement();

			if (!layer.isFrozen()) {
				Bounds b = layer.getBounds(onModelspace);

				if (b.isValid()) { bounds.addToBounds(b); }
			}
		}

		return bounds;
	}

	/**
	 * @deprecated use getBounds().getHeight() instead
	 * @return
	 */
	@Deprecated
	public double getHeight() { return this.bounds.getHeight(); }

	/**
	 * @deprecated use getBounds().getWidth() instead
	 * @return
	 */
	@Deprecated
	public double getWidth() { return this.bounds.getWidth(); }

	/**
	 * Gets the DXF header.
	 *
	 * @return the DXF header
	 */
	public DXFHeader getDXFHeader() { return this.header; }

	/**
	 * Sets the DXF header.
	 *
	 * @param header
	 *            the new DXF header
	 */
	public void setDXFHeader(final DXFHeader header) { this.header = header; }

	/**
	 * Adds the DXF dimension style.
	 *
	 * @param style
	 *            the style
	 */
	public void addDXFDimensionStyle(final DXFDimensionStyle style) {
		this.dimensionStyles.put(style.getName(), style);
	}

	/**
	 * Gets the DXF dimension style.
	 *
	 * @param name
	 *            the name
	 * @return the DXF dimension style
	 */
	public DXFDimensionStyle getDXFDimensionStyle(final String name) {
		return this.dimensionStyles.get(name);
	}

	/**
	 * Gets the DXF dimension style iterator.
	 *
	 * @return the DXF dimension style iterator
	 */
	public Iterator getDXFDimensionStyleIterator() { return this.dimensionStyles.values().iterator(); }

	/**
	 * Adds the DX style.
	 *
	 * @param style
	 *            the style
	 */
	public void addDXStyle(final DXFStyle style) {
		this.textStyles.put(style.getName(), style);
	}

	/**
	 * Gets the DXF style.
	 *
	 * @param name
	 *            the name
	 * @return the DXF style
	 */
	public DXFStyle getDXFStyle(final String name) {
		return this.textStyles.get(name);
	}

	/**
	 * Gets the DXF style iterator.
	 *
	 * @return the DXF style iterator
	 */
	public Iterator getDXFStyleIterator() { return this.textStyles.values().iterator(); }

	/**
	 * Removes the DXF layer.
	 *
	 * @param id
	 *            the id
	 */
	public void removeDXFLayer(final String id) {
		this.layers.remove(id);
	}

	/**
	 * Adds the DXF viewport.
	 *
	 * @param viewport
	 *            the viewport
	 */
	public void addDXFViewport(final DXFViewport viewport) {
		this.viewports.add(viewport);
	}

	/**
	 * Gets the DXF viewport iterator.
	 *
	 * @return the DXF viewport iterator
	 */
	public Iterator getDXFViewportIterator() { return this.viewports.iterator(); }

	/**
	 * Removes the DXF viewport.
	 *
	 * @param viewport
	 *            the viewport
	 */
	public void removeDXFViewport(final DXFViewport viewport) {
		this.viewports.remove(viewport);
	}

	/**
	 * Removes the DXF viewport.
	 *
	 * @param index
	 *            the index
	 */
	public void removeDXFViewport(final int index) {
		this.viewports.remove(index);
	}

	/**
	 * Adds the DXF view.
	 *
	 * @param view
	 *            the view
	 */
	public void addDXFView(final DXFView view) {
		this.views.add(view);
	}

	/**
	 * Gets the DXF view iterator.
	 *
	 * @return the DXF view iterator
	 */
	public Iterator getDXFViewIterator() { return this.views.iterator(); }

	/**
	 * Adds the DXF object.
	 *
	 * @param obj
	 *            the obj
	 */
	@SuppressWarnings ("unchecked")
	public void addDXFObject(final DXFObject obj) {
		// look if the object goes in a dictionary
		DXFDictionary d = this.rootDictionary.getDXFDictionaryForID(obj.getID());

		if (d != null) {
			d.putDXFObject(obj);
		} else {
			// is not bound to a dictionary
			HashMap<String, DXFObject> type = null;

			if (this.objects.containsKey(obj.getObjectType())) {
				type = (HashMap<String, DXFObject>) objects.get(obj.getObjectType());
			} else {
				type = new HashMap<>();
				this.objects.put(obj.getObjectType(), type);
			}

			type.put(obj.getID(), obj);
		}
	}

	/**
	 * Returns the root dictionary.
	 *
	 * @return the root DXFDictionray
	 */
	public DXFDictionary getRootDXFDictionary() { return this.rootDictionary; }

	/**
	 * Sets the root DXF dictionary.
	 *
	 * @param root
	 *            the new root DXF dictionary
	 */
	public void setRootDXFDictionary(final DXFDictionary root) { this.rootDictionary = root; }

	/**
	 * Gets the DXF objects by type.
	 *
	 * @param type
	 *            the type
	 * @return the DXF objects by type
	 */
	public List getDXFObjectsByType(final String type) {
		@SuppressWarnings ("unchecked") HashMap<String, DXFObject> objecttypes =
				(HashMap<String, DXFObject>) this.objects.get(type);
		return new ArrayList<>(objecttypes.values());
	}

	/**
	 *
	 * @param id,
	 *            the ID of the object
	 * @return the object
	 */
	public DXFObject getDXFObjectByID(final String id) {
		Iterator i = this.objects.values().iterator();

		while (i.hasNext()) {
			HashMap map = (HashMap) i.next();
			Object obj;

			if ((obj = map.get(id)) != null) return (DXFObject) obj;
		}

		// Nothing found --> search in the dictionaries
		return this.rootDictionary.getDXFObjectByID(id);
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
		Iterator i = this.getDXFLayerIterator();

		while (i.hasNext()) {
			DXFLayer layer = (DXFLayer) i.next();

			if ((entity = layer.getDXFEntityByID(id)) != null) return entity;
		}

		i = this.getDXFBlockIterator();

		while (i.hasNext()) {
			DXFBlock block = (DXFBlock) i.next();

			if ((entity = block.getDXFEntityByID(id)) != null) return entity;
		}

		return entity;
	}

	/**
	 * Adds a DXFHatchPattern to the document.
	 *
	 * @param pattern
	 */
	public void addDXFHatchPattern(final DXFHatchPattern pattern) {
		this.patterns.put(pattern.getID(), pattern);
	}

	/**
	 *
	 * @return java.util.Iterator over all DXFHatchPattern of the document
	 */
	public Iterator getDXFHatchPatternIterator() { return this.patterns.values().iterator(); }

	/**
	 *
	 * @param ID
	 *            of the pattern (also called pattern name)
	 * @return the DXFHatchPattern or null
	 */
	public DXFHatchPattern getDXFHatchPattern(final String id) {
		return this.patterns.get(id);
	}
}
