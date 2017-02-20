package msi.gaml.statements.draw;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;

public class GeometricProperties {

	GamaPoint location;
	Scaling3D size;

	public static GeometricProperties create() {
		return new GeometricProperties(null, null);
	}

	GeometricProperties(final GamaPoint location, final Scaling3D size) {
		this.location = location;
		this.size = size;
	}

	GamaPoint getLocation() {
		return location;
	}

	Scaling3D getSize() {
		return size;
	}

	public AxisAngle getRotation() {
		return null;
	}

	Double getHeight() {
		return null;
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
		if (rotation == null)
			return this;
		return new WithRotation(location, size, rotation);
	}

	GeometricProperties withHeight(final Double depth) {
		if (depth == null)
			return this;
		return new WithDepth(location, size, depth);
	}

	static class WithRotation extends GeometricProperties {
		private WithRotation(final GamaPoint location, final Scaling3D size, final AxisAngle rotation) {
			super(location, size);
			this.rotation = rotation;
		}

		AxisAngle rotation;

		@Override
		GeometricProperties withRotation(final AxisAngle rotation) {
			if (rotation == null)
				return new GeometricProperties(location, size);
			this.rotation = rotation;
			return this;
		}

		@Override
		GeometricProperties withHeight(final Double depth) {
			if (depth == null)
				return this;
			return new WithRotationAndDepth(location, size, rotation, depth);
		}

		@Override
		public AxisAngle getRotation() {
			return rotation;
		}

	}

	static class WithDepth extends GeometricProperties {

		Double depth;

		private WithDepth(final GamaPoint location, final Scaling3D size, final Double depth) {
			super(location, size);
			this.depth = depth;
		}

		@Override
		GeometricProperties withRotation(final AxisAngle rotation) {
			if (rotation == null)
				return this;
			return new WithRotationAndDepth(location, size, rotation, depth);
		}

		@Override
		GeometricProperties withHeight(final Double depth) {
			if (depth == null)
				return new GeometricProperties(location, size);
			this.depth = depth;
			return this;
		}

		@Override
		Double getHeight() {
			return depth;
		}
	}

	static class WithRotationAndDepth extends WithRotation {
		private WithRotationAndDepth(final GamaPoint location, final Scaling3D size, final AxisAngle rotation,
				final Double depth) {
			super(location, size, rotation);
			this.depth = depth;
		}

		Double depth;

		@Override
		GeometricProperties withRotation(final AxisAngle rotation) {
			if (rotation == null)
				return new WithDepth(location, size, depth);
			return super.withRotation(rotation);
		}

		@Override
		GeometricProperties withHeight(final Double depth) {
			if (depth == null)
				return new WithRotation(location, size, rotation);
			this.depth = depth;
			return this;
		}

		@Override
		Double getHeight() {
			return depth;
		}
	}

}
