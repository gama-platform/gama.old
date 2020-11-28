/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Scaling3D.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;

@SuppressWarnings ("unchecked")
public abstract class Scaling3D implements Transformation3D {

	public final static Scaling3D IDENTITY = new Uniform(1);

	public static Scaling3D of(final double x, final double y, final double z) {
		if (x == y && y == z) { return of(x); }
		return new Heterogeneous(x, y, z);
	}

	public static Scaling3D of(final GamaPoint p) {
		if (p == null) { return null; }
		return of(p.x, p.y, p.z);
	}

	public static Scaling3D of(final ILocation p) {
		if (p == null) { return null; }
		return of(p.getX(), p.getY(), p.getZ());
	}

	public static Scaling3D of(final double factor) {
		if (factor == 1d) { return IDENTITY; }
		return new Uniform(factor);
	}

	public static class Uniform extends Scaling3D {
		final double factor;

		public Uniform(final double f) {
			factor = f;
		}

		@Override
		public void filter(final Coordinate coord) {
			((GamaPoint) coord).multiplyBy(factor);

		}

		@Override
		public Scaling3D asBoundingBoxIn(final Envelope3D env) {
			return of(factor / env.getWidth(), factor / env.getHeight(), env.isFlat() ? 1.0 : factor / env.getDepth());
		}

		@Override
		public double getZ() {
			return factor;
		}

		@Override
		public double getY() {
			return factor;
		}

		@Override
		public double getX() {
			return factor;
		}

		@Override
		public Scaling3D dividedBy(final double i) {
			return Scaling3D.of(factor / i);
		}
	}

	public static class Heterogeneous extends Scaling3D {
		public double x, y, z;

		public Heterogeneous(final double i, final double j, final double k) {
			x = i;
			y = j;
			z = k;
		}

		public Scaling3D setTo(final double i, final double j, final double k) {
			x = i;
			y = j;
			z = k;
			return this;
		}

		public Scaling3D setTo(final double i) {
			x = y = z = i;
			return this;
		}

		@Override
		public void filter(final Coordinate coord) {
			coord.x *= x;
			coord.y *= y;
			coord.z *= z;
		}

		@Override
		public Scaling3D asBoundingBoxIn(final Envelope3D env) {
			x /= env.getWidth();
			y /= env.getHeight();
			if (!env.isFlat()) {
				z /= env.getDepth();
			} else {
				z = 1.0;
			}
			return this;
		}

		@Override
		public double getZ() {
			return z;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public Scaling3D dividedBy(final double i) {
			return Scaling3D.of(x / i, y / i, z / i);
		}
	}

	public abstract Scaling3D asBoundingBoxIn(final Envelope3D env);

	public abstract double getZ();

	public abstract double getY();

	public abstract double getX();

	public abstract Scaling3D dividedBy(double i);

	public GamaPoint toGamaPoint() {
		return new GamaPoint(getX(), getY(), getZ());
	}

}
