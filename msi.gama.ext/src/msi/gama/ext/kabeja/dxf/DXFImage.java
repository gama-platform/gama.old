/*******************************************************************************************************
 *
 * DXFImage.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;

import msi.gama.ext.kabeja.dxf.helpers.Point;
import msi.gama.ext.kabeja.dxf.objects.DXFImageDefObject;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFImage extends DXFEntity {
	
	/** The insert point. */
	protected Point insertPoint = new Point();
	
	/** The vector V. */
	protected Point vectorV = new Point();
	
	/** The vector U. */
	protected Point vectorU = new Point();
	
	/** The image size along U. */
	protected double imageSizeAlongU;
	
	/** The image size along V. */
	protected double imageSizeAlongV;
	
	/** The image def ID. */
	protected String imageDefID = "";
	
	/** The brightness. */
	protected double brightness;
	
	/** The contrast. */
	protected double contrast;
	
	/** The fade. */
	protected double fade;
	
	/** The clip boundary. */
	protected ArrayList<Point> clipBoundary = new ArrayList<>();
	
	/** The clipping. */
	protected boolean clipping = false;
	
	/** The rectangular clipping. */
	protected boolean rectangularClipping = false;
	
	/** The polygonal clipping. */
	protected boolean polygonalClipping = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		Bounds b = new Bounds();
		DXFImageDefObject imageDef = (DXFImageDefObject) this.doc.getDXFObjectByID(this.getImageDefObjectID());

		if (imageDef != null) {
			b.addToBounds(this.insertPoint);
			b.addToBounds(insertPoint.getX() + imageSizeAlongU, insertPoint.getY() + imageSizeAlongV,
					this.insertPoint.getZ());
		} else {
			b.setValid(false);
		}

		return b;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_IMAGE; }

	/**
	 * Gets the insert point.
	 *
	 * @return the insert point
	 */
	public Point getInsertPoint() { return insertPoint; }

	/**
	 * Sets the insert point.
	 *
	 * @param p the new insert point
	 */
	public void setInsertPoint(final Point p) { this.insertPoint = p; }

	/**
	 * Sets the image def object ID.
	 *
	 * @param id the new image def object ID
	 */
	public void setImageDefObjectID(final String id) { this.imageDefID = id; }

	/**
	 * Gets the image def object ID.
	 *
	 * @return the image def object ID
	 */
	public String getImageDefObjectID() { return this.imageDefID; }

	/**
	 * @return Returns the imageSizeAlongU.
	 */
	public double getImageSizeAlongU() { return imageSizeAlongU; }

	/**
	 * @param imageSizeAlongU
	 *            The imageSizeAlongU to set.
	 */
	public void setImageSizeAlongU(final double imageSizeAlongU) { this.imageSizeAlongU = imageSizeAlongU; }

	/**
	 * @return Returns the imageSizeAlongV.
	 */
	public double getImageSizeAlongV() { return imageSizeAlongV; }

	/**
	 * @param imageSizeAlongV
	 *            The imageSizeAlongV to set.
	 */
	public void setImageSizeAlongV(final double imageSizeAlongV) { this.imageSizeAlongV = imageSizeAlongV; }

	/**
	 * @return Returns the vectorU.
	 */
	public Point getVectorU() { return vectorU; }

	/**
	 * @param vectorU
	 *            The vectorU to set.
	 */
	public void setVectorU(final Point vectorU) { this.vectorU = vectorU; }

	/**
	 * @return Returns the vectorV.
	 */
	public Point getVectorV() { return vectorV; }

	/**
	 * @param vectorV
	 *            The vectorV to set.
	 */
	public void setVectorV(final Point vectorV) { this.vectorV = vectorV; }

	/**
	 * @return Returns the brightness.
	 */
	public double getBrightness() { return brightness; }

	/**
	 * @param brightness
	 *            The brightness to set.
	 */
	public void setBrightness(final double brightness) { this.brightness = brightness; }

	/**
	 * @return Returns the clipping.
	 */
	public boolean isClipping() { return clipping; }

	/**
	 * @param clipping
	 *            The clipping to set.
	 */
	public void setClipping(final boolean clipping) { this.clipping = clipping; }

	/**
	 * @return Returns the contrast.
	 */
	public double getContrast() { return contrast; }

	/**
	 * @param contrast
	 *            The contrast to set.
	 */
	public void setContrast(final double contrast) { this.contrast = contrast; }

	/**
	 * @return Returns the fade.
	 */
	public double getFade() { return fade; }

	/**
	 * @param fade
	 *            The fade to set.
	 */
	public void setFade(final double fade) { this.fade = fade; }

	/**
	 * @return Returns the clipBoundary.
	 */
	public ArrayList getClipBoundary() { return clipBoundary; }

	/**
	 * Adds the clipping point.
	 *
	 * @param p the p
	 */
	public void addClippingPoint(final Point p) {
		clipBoundary.add(p);
	}

	/**
	 * @return Returns the polygonalClipping.
	 */
	public boolean isPolygonalClipping() { return polygonalClipping; }

	/**
	 * @param polygonalClipping
	 *            The polygonalClipping to set.
	 */
	public void setPolygonalClipping(final boolean polygonalClipping) {
		this.polygonalClipping = polygonalClipping;
		this.rectangularClipping = !polygonalClipping;
	}

	/**
	 * @return Returns the rectangularClipping.
	 */
	public boolean isRectangularClipping() { return rectangularClipping; }

	/**
	 * @param rectangularClipping
	 *            The rectangularClipping to set.
	 */
	public void setRectangularClipping(final boolean rectangularClipping) {
		this.rectangularClipping = rectangularClipping;
		this.polygonalClipping = !rectangularClipping;
	}

	@Override
	public double getLength() { return 0; }
}
