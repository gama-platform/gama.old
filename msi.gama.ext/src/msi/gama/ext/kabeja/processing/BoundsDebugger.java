/*******************************************************************************************************
 *
 * BoundsDebugger.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.processing;

import java.util.Iterator;
import java.util.Map;

import msi.gama.ext.kabeja.dxf.Bounds;
import msi.gama.ext.kabeja.dxf.DXF3DFace;
import msi.gama.ext.kabeja.dxf.DXFBlock;
import msi.gama.ext.kabeja.dxf.DXFDocument;
import msi.gama.ext.kabeja.dxf.DXFEntity;
import msi.gama.ext.kabeja.dxf.DXFLayer;
import msi.gama.ext.kabeja.dxf.DXFText;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class BoundsDebugger extends AbstractPostProcessor {

	/** The Constant LAYER_NAME. */
	public static final String LAYER_NAME = "kabeja_bounds_debug";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.tools.PostProcessor#process(de.miethxml.kabeja.dxf.DXFDocument)
	 */
	@Override
	public void process(final DXFDocument doc, final Map context) throws ProcessorException {
		// set all blocks to color gray
		Iterator i = doc.getDXFBlockIterator();

		while (i.hasNext()) {
			DXFBlock b = (DXFBlock) i.next();
			Iterator ie = b.getDXFEntitiesIterator();

			while (ie.hasNext()) {
				DXFEntity entity = (DXFEntity) ie.next();

				// set to gray
				entity.setColor(9);
			}
		}

		DXFEntity left = null;
		DXFEntity top = null;
		DXFEntity right = null;
		DXFEntity bottom = null;

		Bounds b = doc.getBounds();
		double x = b.getMinimumX() + b.getWidth() / 2;
		double y = b.getMinimumY() + b.getHeight() / 2;

		// starting at the center point of the draft
		Bounds lBounds = new Bounds(x, x, y, y);
		Bounds rBounds = new Bounds(x, x, y, y);
		Bounds tBounds = new Bounds(x, x, y, y);
		Bounds bBounds = new Bounds(x, x, y, y);

		i = doc.getDXFLayerIterator();

		while (i.hasNext()) {
			DXFLayer l = (DXFLayer) i.next();

			// set color to gray
			l.setColor(8);

			Iterator ti = l.getDXFEntityTypeIterator();

			while (ti.hasNext()) {
				String type = (String) ti.next();
				for (DXFEntity entity : l.getDXFEntities(type)) {
					// set to gray
					entity.setColor(8);

					Bounds currentBounds = entity.getBounds();

					if (currentBounds.isValid()) {
						if (currentBounds.getMinimumX() <= lBounds.getMinimumX()) {
							lBounds = currentBounds;
							left = entity;
						}

						if (currentBounds.getMinimumY() <= bBounds.getMinimumY()) {
							bBounds = currentBounds;
							bottom = entity;
						}

						if (currentBounds.getMaximumX() >= rBounds.getMaximumX()) {
							rBounds = currentBounds;
							right = entity;
						}

						if (currentBounds.getMaximumY() >= tBounds.getMaximumY()) {
							tBounds = currentBounds;
							top = entity;
						}
					}
				}
			}
		}

		// left -> red
		if (left != null) {
			left.setColor(0);
			addBounds(lBounds, doc, 0, left.getType() + "=" + left.getID());
		}

		// right -> green
		if (right != null) {
			right.setColor(2);
			addBounds(rBounds, doc, 2, right.getType() + "=" + right.getID());
		}

		// bottom blue
		if (bottom != null) {
			bottom.setColor(4);
			addBounds(bBounds, doc, 4, bottom.getType() + "=" + bottom.getID());
		}

		// top color -> magenta
		if (top != null) {
			top.setColor(5);
			addBounds(tBounds, doc, 5, top.getType() + "=" + top.getID());

			// the color -> magenta
			top.setColor(5);
			addBounds(b, doc, 6, "ALL");
		}
	}

	/**
	 * Adds the bounds.
	 *
	 * @param bounds
	 *            the bounds
	 * @param doc
	 *            the doc
	 * @param color
	 *            the color
	 * @param type
	 *            the type
	 */
	protected void addBounds(final Bounds bounds, final DXFDocument doc, final int color, final String type) {
		DXF3DFace face = new DXF3DFace();
		face.getPoint1().setX(bounds.getMinimumX());
		face.getPoint1().setY(bounds.getMinimumY());

		face.getPoint2().setX(bounds.getMinimumX());
		face.getPoint2().setY(bounds.getMaximumY());

		face.getPoint3().setX(bounds.getMaximumX());
		face.getPoint3().setY(bounds.getMaximumY());

		face.getPoint4().setX(bounds.getMaximumX());
		face.getPoint4().setY(bounds.getMinimumY());

		face.setColor(color);
		face.setLayerName(LAYER_NAME);

		doc.addDXFEntity(face);

		DXFText t = new DXFText();
		t.setDXFDocument(doc);
		t.setText("DEBUG-" + type);
		t.getInsertPoint().setX(bounds.getMinimumX());
		t.getInsertPoint().setY(bounds.getMaximumY());
		t.setColor(color);
		t.setLayerName(LAYER_NAME);
		doc.addDXFEntity(t);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.tools.PostProcessor#setProperties(java.util.Map)
	 */
	@Override
	public void setProperties(final Map properties) {}
}
