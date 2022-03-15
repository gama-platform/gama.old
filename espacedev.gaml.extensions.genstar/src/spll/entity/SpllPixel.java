package spll.entity;

import java.util.Map;

import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.GeometryBuilder;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.numeric.ContinuousValue;

public class SpllPixel extends AGeoEntity<ContinuousValue> {
	
	private Envelope2D pixel;
	
	private int gridX;
	private int gridY;
	
	protected SpllPixel(Map<Attribute<? extends ContinuousValue>, ContinuousValue> bandsData, Envelope2D pixel, int gridX, int gridY) {
		super(bandsData, "px ["+pixel.getCenterX()+";"+pixel.getCenterY()+"]");
		this.gridX = gridX;
		this.gridY = gridY;
		this.pixel = pixel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Get pixel's area in square meter 
	 * 
	 */
	@Override
	public double getArea() {
		return Math.sqrt(pixel.getWidth() * pixel.getHeight());
	}

	@Override
	public Point getLocation() {
		return new GeometryBuilder().point(pixel.getCenterX(), pixel.getCenterY());
	}
	
	@Override
	public Geometry getGeometry() {
		return new GeometryBuilder().polygon(
				pixel.getMinX(), pixel.getMinY(),
				pixel.getMinX(), pixel.getMaxY(),
				pixel.getMaxX(), pixel.getMaxY(),
				pixel.getMaxX(), pixel.getMinY());
	}
	
	public int getGridX() {
		return gridX;
	}
	
	public int getGridY() {
		return gridY;
	}
	
	@Override
	public String toString() {
		return this.getGenstarName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gridX;
		result = prime * result + gridY;
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
		SpllPixel other = (SpllPixel) obj;
		if (gridX != other.gridX)
			return false;
		if (gridY != other.gridY)
			return false;
		return true;
	}

	
}
