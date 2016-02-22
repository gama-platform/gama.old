/*********************************************************************************************
 *
 *
 * 'WaterLevelUtils.java', in plugin 'cenres.gaml.extensions.hydro', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package cenres.gaml.extensions.hydro.utils;

import java.util.*;
import com.vividsolutions.jts.geom.Coordinate;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.Types;

/**
 * The class GamaGeometryUtils.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public class WaterLevelUtils {

	/**
	 * This class allows to sort coordinates in increasing order according to the x value.
	 * @author Philippe Caillou
	 *
	 */
	public static class XCoordinatesComparator implements Comparator<Coordinate> {

		@Override
		public int compare(final Coordinate arg0, final Coordinate arg1) {
			return arg0.x > arg1.x ? 1 : arg0.x < arg1.x ? -1 : 0;
		}

	}

	/**
	 * This class allows to sort coordinates in decreasing order according to the y value.
	 * @author Philippe
	 *
	 */
	public static class YCoordinatesComparator implements Comparator<Coordinate> {

		@Override
		public int compare(final Coordinate arg0, final Coordinate arg1) {
			return arg0.y > arg1.y ? -1 : arg0.y < arg1.y ? 1 : 0;
		}

	}

	public static double heigth(final IScope scope, final List<Coordinate> points, final double targetsurface) {
		// double totalsurface;
		double currentheight;
		double previousheight;
		// double previoussurface;
		double currentsurface;

		// double tempsurf;
		boolean trouve = false;
		double res = -1;

		int nbtrap = points.size() - 1;

		double[] prevtrapsurf = new double[nbtrap];
		double[] nexttrapsurf = new double[nbtrap];
		double[] trapwidth = new double[nbtrap];
		double[] prevtrapwidth = new double[nbtrap];
		double[] leftheight = new double[nbtrap + 1];
		double[] leftheightprec = new double[nbtrap + 1];

		List<Coordinate> sortedpointsy = new ArrayList<Coordinate>();
		sortedpointsy.addAll(points);
		Collections.sort(sortedpointsy, new YCoordinatesComparator()); // max to min

		List<Coordinate> sortedpointsx = new ArrayList<Coordinate>();
		sortedpointsx.addAll(points);
		Collections.sort(sortedpointsx, new XCoordinatesComparator()); // min to max

		currentheight = sortedpointsy.get(0).y;

		boolean finished = false;
		int nextpoint = -1;

		for ( int i = 0; i < nbtrap + 1; i++ ) {
			leftheight[i] = currentheight - sortedpointsx.get(i).y;
		}
		currentsurface = 0;
		for ( int i = 0; i < nbtrap; i++ ) {
			trapwidth[i] = sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x;
			nexttrapsurf[i] = trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
			currentsurface = currentsurface + nexttrapsurf[i];
		}

		while (!finished) {
			nextpoint++;
			// previoussurface=currentsurface;
			currentsurface = 0;

			prevtrapsurf = nexttrapsurf;
			nexttrapsurf = new double[nbtrap];

			leftheightprec = leftheight;
			leftheight = new double[nbtrap + 1];

			prevtrapwidth = trapwidth;
			trapwidth = new double[nbtrap];

			previousheight = currentheight;
			currentheight = sortedpointsy.get(nextpoint).y;
			for ( int i = 0; i < nbtrap + 1; i++ ) {
				leftheight[i] = currentheight - sortedpointsx.get(i).y;
			}

			for ( int i = 0; i < nbtrap; i++ ) {
				if ( prevtrapwidth[i] <= 0 ) {
					trapwidth[i] = 0;
				}
			}
			for ( int i = 0; i < nbtrap; i++ ) {
				if ( prevtrapwidth[i] > 0 ) {
					trapwidth[i] = prevtrapwidth[i];
					if ( leftheight[i] <= 0 & leftheight[i + 1] > 0 ) {
						trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
							(currentheight - sortedpointsx.get(i + 1).y) /
							(sortedpointsx.get(i).y - sortedpointsx.get(i + 1).y);
						leftheight[i] = 0;
					}
					if ( leftheight[i] > 0 & leftheight[i + 1] <= 0 ) {
						trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
							(currentheight - sortedpointsx.get(i).y) /
							(sortedpointsx.get(i + 1).y - sortedpointsx.get(i).y);
						leftheight[i + 1] = 0;
					}
					if ( leftheight[i] <= 0 & leftheight[i + 1] <= 0 ) {
						trapwidth[i] = 0;
					}
					nexttrapsurf[i] = trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
					currentsurface = currentsurface + trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
				}
			}
			if ( currentsurface < targetsurface ) {
				finished = true;

				// STOT=somme(triangles,h2*dx/dy)+somme(rect,h*dx)
				// STOT=h2*somm(dxtri/dytri/2)+h*somme(dxrect)
				// STOT=A*h2+Bh
				// A=somme(dxtri/dytri/2) B=somme(dxrect)
				// hsol=(-b +- sqrt(b2-4ac) )/2a with c=-stot
				double A = 0;
				double B = 0;
				double stot = targetsurface - currentsurface;
				double C = -stot;
				for ( int i = 0; i < nbtrap; i++ ) {
					if ( prevtrapsurf[i] != 0 ) {
						if ( leftheight[i] >= 0 & leftheight[i + 1] >= 0 ) {
							B = B + (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x);
						} else {
							if ( sortedpointsx.get(i + 1).y < sortedpointsx.get(i).y ) {
								A = A + 0.5 * prevtrapwidth[i] / leftheightprec[i + 1];
								B = B + 0.5 * leftheight[i + 1] * prevtrapwidth[i] / leftheightprec[i + 1];
								C = C - leftheight[i + 1] * trapwidth[i];
							} else {
								A = A + 0.5 * prevtrapwidth[i] / leftheightprec[i];
								B = B + 0.5 * leftheight[i] * prevtrapwidth[i] / leftheightprec[i];
								C = C - leftheight[i] * trapwidth[i];
							}
						}
					}
				}
				double sol1 = (-B + FastMath.sqrt(B * B - 4.0 * A * C)) / (2.0 * A);
				double sol2 = (-B - FastMath.sqrt(B * B - 4.0 * A * C)) / (2.0 * A);
				if ( A == 0 & B > 0 ) {
					sol1 = -C / B;
				}
				if ( A == 0 & B > 0 ) {
					sol2 = -1;
				}

				if ( sol1 >= 0 & sol1 <= previousheight - currentheight ) {
					trouve = true;
					res = currentheight + sol1;
					if ( sol2 > 0 & sol2 < previousheight - currentheight ) { throw GamaRuntimeException
						.error("2 possible water level, pb...", scope);

					}

				}
				if ( sol2 >= 0 & sol2 <= previousheight - currentheight ) {
					trouve = true;
					res = currentheight + sol2;
					if ( sol1 > 0 & sol1 < previousheight - currentheight ) { throw GamaRuntimeException
						.error("2 possible water level, pb...", scope);

					}

				}

			}
			if ( nextpoint == nbtrap ) {
				finished = true;
			}
		}
		if ( !trouve ) { throw GamaRuntimeException.error("no possible water level, pb...", scope); }
		return res;
	}

	public static double area(final List<Coordinate> points, final double targetheight) {
		int nbtrap = points.size() - 1;

		double[] nexttrapsurf = new double[nbtrap];
		double[] trapwidth = new double[nbtrap];
		double[] leftheight = new double[nbtrap + 1];

		double currentsurface = 0;

		List<Coordinate> sortedpointsx = new ArrayList<Coordinate>();
		sortedpointsx.addAll(points);
		Collections.sort(sortedpointsx, new XCoordinatesComparator()); // min to max

		for ( int i = 0; i < nbtrap + 1; i++ ) {
			leftheight[i] = targetheight - sortedpointsx.get(i).y;
		}
		for ( int i = 0; i < nbtrap; i++ ) {
			trapwidth[i] = sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x;
			if ( leftheight[i] <= 0 & leftheight[i + 1] > 0 ) {
				trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
					(targetheight - sortedpointsx.get(i + 1).y) / (sortedpointsx.get(i).y - sortedpointsx.get(i + 1).y);
				leftheight[i] = 0;
			}
			if ( leftheight[i] > 0 & leftheight[i + 1] <= 0 ) {
				trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
					(targetheight - sortedpointsx.get(i).y) / (sortedpointsx.get(i + 1).y - sortedpointsx.get(i).y);
				leftheight[i + 1] = 0;
			}
			if ( leftheight[i] <= 0 & leftheight[i + 1] <= 0 ) {
				trapwidth[i] = 0;
			}
			nexttrapsurf[i] = trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
			currentsurface = currentsurface + trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
		}
		return currentsurface;

	}

	public static IList<IList<GamaPoint>> areaPolylines(final List<Coordinate> points, final double targetheight) {
		int nbtrap = points.size() - 1;

		double[] nexttrapsurf = new double[nbtrap];
		double[] trapwidth = new double[nbtrap];
		double[] leftheight = new double[nbtrap + 1];

		double currentsurface = 0;
		boolean inthewater = false;

		GamaPoint currentstartpoint = new GamaPoint(0, 0);
		IList<IList<GamaPoint>> listoflist = GamaListFactory.create(Types.LIST.of(Types.POINT));
		IList<GamaPoint> currentlist = GamaListFactory.create(Types.POINT);

		List<Coordinate> sortedpointsx = new ArrayList<Coordinate>();
		sortedpointsx.addAll(points);
		Collections.sort(sortedpointsx, new XCoordinatesComparator()); // min to max

		for ( int i = 0; i < nbtrap + 1; i++ ) {
			leftheight[i] = targetheight - sortedpointsx.get(i).y;
		}
		for ( int i = 0; i < nbtrap; i++ ) {
			if ( leftheight[i] > 0 & leftheight[i + 1] > 0 ) {
				if ( !inthewater ) {
					currentlist = GamaListFactory.create(Types.POINT);
					currentstartpoint = new GamaPoint(sortedpointsx.get(i).x, targetheight);
					currentlist.add(new GamaPoint(sortedpointsx.get(i).x, targetheight));
					currentlist.add(new GamaPoint(sortedpointsx.get(i).x, sortedpointsx.get(i).y));
				}
				inthewater = true;

				currentlist.add(new GamaPoint(sortedpointsx.get(i + 1).x, sortedpointsx.get(i + 1).y));
				trapwidth[i] = sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x;
			}
			if ( leftheight[i] <= 0 & leftheight[i + 1] > 0 ) {
				trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
					(targetheight - sortedpointsx.get(i + 1).y) / (sortedpointsx.get(i).y - sortedpointsx.get(i + 1).y);
				leftheight[i] = 0;
				if ( !inthewater ) {
					currentlist = GamaListFactory.create(Types.POINT);
					currentstartpoint = new GamaPoint(sortedpointsx.get(i).x + trapwidth[i], targetheight);
					currentlist.add(new GamaPoint(sortedpointsx.get(i).x + trapwidth[i], targetheight));
					inthewater = true;
				}
				currentlist.add(new GamaPoint(sortedpointsx.get(i + 1).x + trapwidth[i], sortedpointsx.get(i + 1).y));
			}
			if ( leftheight[i] > 0 & leftheight[i + 1] <= 0 ) {
				trapwidth[i] = (sortedpointsx.get(i + 1).x - sortedpointsx.get(i).x) *
					(targetheight - sortedpointsx.get(i).y) / (sortedpointsx.get(i + 1).y - sortedpointsx.get(i).y);
				leftheight[i + 1] = 0;
				if ( inthewater ) {
					currentlist.add(new GamaPoint(sortedpointsx.get(i).x + trapwidth[i], targetheight));
					currentlist.add(currentstartpoint);
					listoflist.add(currentlist);
					inthewater = false;
				}
			}
			if ( leftheight[i] <= 0 & leftheight[i + 1] <= 0 ) {
				trapwidth[i] = 0;
				if ( inthewater ) {
					currentlist.add(new GamaPoint(sortedpointsx.get(i).x, targetheight));
					currentlist.add(currentstartpoint);
					listoflist.add(currentlist);
					inthewater = false;
				}
			}
			nexttrapsurf[i] = trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
			currentsurface = currentsurface + trapwidth[i] * (leftheight[i] + leftheight[i + 1]) / 2;
		}
		if ( inthewater ) {
			currentlist.add(currentstartpoint);
			listoflist.add(currentlist);
			inthewater = false;
		}
		return listoflist;

	}

}
