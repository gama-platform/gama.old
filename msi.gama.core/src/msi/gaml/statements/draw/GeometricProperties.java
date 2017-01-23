package msi.gaml.statements.draw;

import msi.gama.metamodel.shape.GamaPoint;

public class GeometricProperties {

	GamaPoint location;
	GamaPoint size;

	public static GeometricProperties create() {
		return new GeometricProperties(null, null);
	}

	GeometricProperties(final GamaPoint location, final GamaPoint size) {
		this.location = location;
		this.size = size;
	}

	GamaPoint getLocation() {
		return location;
	}

	GamaPoint getSize() {
		return size;
	}

	GamaPoint getAxis() {
		return null;
	}

	Double getAngle() {
		return null;
	}

	Double getDepth() {
		return null;
	}

	GeometricProperties withLocation(final GamaPoint loc) {
		location = loc;
		return this;
	}

	GeometricProperties withSize(final GamaPoint size) {
		this.size = size;
		return this;
	}

	GeometricProperties withRotation(final Double angle, final GamaPoint axis) {
		if (angle == null || axis == null)
			return this;
		return new WithRotation(location, size, angle, axis);
	}

	GeometricProperties withDepth(final Double depth) {
		if (depth == null)
			return this;
		return new WithDepth(location, size, depth);
	}

	static class WithRotation extends GeometricProperties {
		private WithRotation(final GamaPoint location, final GamaPoint size, final Double angle, final GamaPoint axis) {
			super(location, size);
			this.angle = angle;
			this.axis = axis;
		}

		Double angle;
		GamaPoint axis;

		@Override
		GeometricProperties withRotation(final Double angle, final GamaPoint axis) {
			if (angle == null || axis == null)
				return new GeometricProperties(location, size);
			this.angle = angle;
			this.axis = axis;
			return this;
		}

		@Override
		GeometricProperties withDepth(final Double depth) {
			if (depth == null)
				return this;
			return new WithRotationAndDepth(location, size, angle, axis, depth);
		}

		@Override
		Double getAngle() {
			return angle;
		}

		@Override
		GamaPoint getAxis() {
			return axis;
		}
	}

	static class WithDepth extends GeometricProperties {

		Double depth;

		private WithDepth(final GamaPoint location, final GamaPoint size, final Double depth) {
			super(location, size);
			this.depth = depth;
		}

		@Override
		GeometricProperties withRotation(final Double angle, final GamaPoint axis) {
			if (angle == null || axis == null)
				return this;
			return new WithRotationAndDepth(location, size, angle, axis, depth);
		}

		@Override
		GeometricProperties withDepth(final Double depth) {
			if (depth == null)
				return new GeometricProperties(location, size);
			this.depth = depth;
			return this;
		}

		@Override
		Double getDepth() {
			return depth;
		}
	}

	static class WithRotationAndDepth extends WithRotation {
		private WithRotationAndDepth(final GamaPoint location, final GamaPoint size, final Double angle,
				final GamaPoint axis, final Double depth) {
			super(location, size, angle, axis);
			this.depth = depth;
		}

		Double depth;

		@Override
		GeometricProperties withRotation(final Double angle, final GamaPoint axis) {
			if (angle == null || axis == null)
				return new WithDepth(location, size, depth);
			return super.withRotation(angle, axis);
		}

		@Override
		GeometricProperties withDepth(final Double depth) {
			if (depth == null)
				return new WithRotation(location, size, angle, axis);
			this.depth = depth;
			return this;
		}

		@Override
		Double getDepth() {
			return depth;
		}
	}

}
