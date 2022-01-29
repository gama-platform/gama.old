/*******************************************************************************************************
 *
 * LayerFilter.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.dxf.DXFEntity;
import msi.gama.ext.kabeja.dxf.DXFLayer;

/**
 * The Class LayerFilter.
 */
public class LayerFilter extends AbstractPostProcessor {
	
	/** The Constant PROPERTY_REMOVE_LAYERS. */
	public final static String PROPERTY_REMOVE_LAYERS = "layers.remove";
	
	/** The Constant PROPERTY_MERGE_LAYERS. */
	public final static String PROPERTY_MERGE_LAYERS = "layers.merge";
	
	/** The Constant MERGED_LAYER_NAME. */
	public final static String MERGED_LAYER_NAME = "ALL";
	
	/** The merge. */
	protected boolean merge = false;
	
	/** The removable layers. */
	protected Set<String> removableLayers = new HashSet<>();

	@Override
	public void setProperties(final Map properties) {
		super.setProperties(properties);

		if (properties.containsKey(PROPERTY_MERGE_LAYERS)) {
			this.merge = Boolean.parseBoolean((String) properties.get(PROPERTY_MERGE_LAYERS));
		}

		if (properties.containsKey(PROPERTY_REMOVE_LAYERS)) {
			this.removableLayers.clear();

			StringTokenizer st = new StringTokenizer((String) properties.get(PROPERTY_REMOVE_LAYERS), "|");

			while (st.hasMoreTokens()) { this.removableLayers.add(st.nextToken()); }
		}
	}

	@Override
	public void process(final DXFDocument doc, final Map context) throws ProcessorException {
		DXFLayer mergeLayer = null;

		if (this.merge) {
			if (doc.containsDXFLayer(MERGED_LAYER_NAME)) {
				mergeLayer = doc.getDXFLayer(MERGED_LAYER_NAME);
			} else {
				mergeLayer = new DXFLayer();
				mergeLayer.setName(MERGED_LAYER_NAME);
				doc.addDXFLayer(mergeLayer);
			}
		}

		// iterate over all layers
		Iterator i = doc.getDXFLayerIterator();

		while (i.hasNext()) {
			DXFLayer layer = (DXFLayer) i.next();

			if (this.removableLayers.contains(layer.getName())) {
				i.remove();
			} else if (this.merge && (layer != mergeLayer)) {
				Iterator types = layer.getDXFEntityTypeIterator();

				while (types.hasNext()) {
					String type = (String) types.next();
					Iterator entityIterator = layer.getDXFEntities(type).iterator();

					while (entityIterator.hasNext()) {
						DXFEntity e = (DXFEntity) entityIterator.next();
						// we set all entities to the merged layer
						// and remove them from the last layer
						e.setLayerName(MERGED_LAYER_NAME);

						// set again to the doc, which will
						// place the entity on the right
						// layer -> the LAYER = "ALL"
						doc.addDXFEntity(e);
						entityIterator.remove();
					}
				}

				// remove the layer
				i.remove();
			}
		}
	}
}
