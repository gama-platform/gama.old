/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.GeometricProperties.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;

public class GeometricProperties implements IDisposable {

	GamaPoint location;
	Scaling3D size;
	AxisAngle rotation;
	Double depth, lineWidth;
	IShape.Type type;
	//
	static PoolUtils.ObjectPool<GeometricProperties> POOL =
			PoolUtils.create("GeometricProperties", true, () -> new GeometricProperties(), null);

	public static GeometricProperties create() {
		return POOL.get();
		// return new GeometricProperties();
	}

	@Override
	public void dispose() {
		location = null;
		size = null;
		rotation = null;
		depth = null;
		lineWidth = null;
		type = null;
		POOL.release(this);
	}

	GamaPoint getLocation() {
		return location;
	}

	Scaling3D getSize() {
		return size;
	}

	public AxisAngle getRotation() {
		return rotation;
	}

	public Double getAngle() {
		if (rotation == null) { return null; }
		return rotation.angle;
	}

	public GamaPoint getAxis() {
		if (rotation == null) { return null; }
		return rotation.getAxis();
	}

	public Double getLineWidth() {
		return lineWidth == null ? GamaPreferences.Displays.CORE_LINE_WIDTH.getValue() : lineWidth;
	}

	public void setLineWidth(final Double d) {
		lineWidth = d;
	}

	Double getHeight() {
		return depth;
	}

	GeometricProperties withLocation(final GamaPoint loc) {
		location = loc;
		return this;
	}

	GeometricProperties withSize(final Scaling3D size) {
		this.size = size;
		return this;
	}

	GeometricProperties withRotation(final AxisAngle rotation) {
		this.rotation = rotation;
		return this;
	}

	GeometricProperties withHeight(final Double depth) {
		this.depth = depth;
		return this;
	}

	public IShape.Type getType() {
		return type == null ? Type.POLYGON : type;
	}

	public void setType(final Type type2) {
		type = type2;
	}

}
