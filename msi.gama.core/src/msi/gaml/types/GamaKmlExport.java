/*******************************************************************************************************
 *
 * msi.gaml.types.GamaKmlExport.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import msi.gama.ext.kml.AltitudeMode;
import msi.gama.ext.kml.ColorMode;
import msi.gama.ext.kml.Document;
import msi.gama.ext.kml.Feature;
import msi.gama.ext.kml.Folder;
import msi.gama.ext.kml.IconStyle;
import msi.gama.ext.kml.Kml;
import msi.gama.ext.kml.Link;
import msi.gama.ext.kml.Location;
import msi.gama.ext.kml.Model;
import msi.gama.ext.kml.MultiGeometry;
import msi.gama.ext.kml.Orientation;
import msi.gama.ext.kml.Placemark;
import msi.gama.ext.kml.Scale;
import msi.gama.ext.kml.Style;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaDate;
import msi.gaml.operators.Spatial;

public class GamaKmlExport {
	private final Kml kml;
	private final Document doc;
	private KmlFolder defolder; // Default folder in case we need one.
	private final HashMap<String, KmlFolder> folders;

	public GamaKmlExport() {
		kml = new Kml();
		doc = kml.createAndSetDocument();
		folders = new HashMap<>();
	}

	public KmlFolder addFolder(final String label, final GamaDate beginDate, final GamaDate endDate) {
		final KmlFolder kf = new KmlFolder(doc, label, dateToKml(beginDate), dateToKml(endDate));
		folders.put(label, kf);
		return kf;
	}

	public void add3DModel(final IScope scope, final GamaPoint loc, final double orientation, final double scale,
			final GamaDate beginDate, final GamaDate endDate, final String daefile) {
		getDefaultFolder().add3DModel(scope, loc, orientation, scale, dateToKml(beginDate), dateToKml(endDate),
				daefile);
	}

	public void addGeometry(final IScope scope, final String label, final GamaDate beginDate, final GamaDate endDate,
			final IShape geom, final String styleName, final double height) {
		getDefaultFolder().addGeometry(scope, label, dateToKml(beginDate), dateToKml(endDate), geom, styleName, height);
	}

	/**
	 * Defines a new style to be used with addStyledRecord
	 *
	 * @param name
	 *            Style name. Must be unique.
	 * @param lineWidth
	 *            Width of the line. A thin line should have a value 1.0.
	 * @param lineColor
	 *            Color of the line.
	 * @param fillColor
	 *            Polygon fill color.
	 */
	public void defStyle(final String name, final double lineWidth, final GamaColor lineColor,
			final GamaColor fillColor) {
		final Style style = doc.createAndAddStyle().withId(name);
		style.createAndSetLineStyle().withColor(kmlColor(lineColor)).withWidth(lineWidth);
		style.createAndSetPolyStyle().withColor(kmlColor(fillColor)).withColorMode(ColorMode.NORMAL);
	}

	private static String toHex2Digit(final int a) {
		String prefix = "";
		if (a % 256 < 16) {
			prefix = "0";
		}
		return prefix + Integer.toHexString(a % 256);
	}

	/**
	 * Produces a Kml String representation of the Color given in argument
	 *
	 * @param c
	 *            A Color
	 * @return A String in ABGR Hex text format
	 */
	public String kmlColor(final GamaColor c) {
		return toHex2Digit(c.alpha()) + toHex2Digit(c.blue()) + toHex2Digit(c.green()) + toHex2Digit(c.red());
	}

	/**
	 * Defines a new IconStyle to be used with addStyledRecord
	 *
	 * @param name
	 *            Style name. Must be unique.
	 * @param iconFile
	 *            Name of an image (png) file. The path is relative to the location where the KML file will be saved.
	 * @param scale
	 *            Scale factor applied to the image. Choose 1.0 if you don't know.
	 * @param heading
	 *            Orientation heading of the icon. Should be a value between 0.0 and 360.0. 0.0 is the normal
	 *            orientation of the icon. Higher numbers apply a clockwise rotation of the icon.
	 */

	public void defIconStyle(final String name, final String iconFile, final double scale, final double heading) {
		final Style style = doc.createAndAddStyle().withId(name);
		final IconStyle iconstyle = style.createAndSetIconStyle().withScale(scale).withHeading(heading);
		iconstyle.createAndSetIcon().withHref(iconFile);
	}

	public void saveAsKml(final IScope scope, final String filename) {
		try {
			if (!filename.isEmpty()) {
				kml.marshal(new File(filename));
			} else {
				System.out.println("Failed to save the kml file : no valid file name was provided.");
			}
		} catch (final FileNotFoundException e) {
			GamaRuntimeFileException.error("Failed to open " + filename + " for saving to KML.", scope);
		}
	}

	public void saveAsKmz(final IScope scope, final String filename) {
		try {
			if (!filename.isEmpty()) {
				kml.marshalAsKmz(filename);
			} else {
				System.out.println("Failed to save the kmz file : no valid file name was provided.");
			}
		} catch (final IOException e) {
			GamaRuntimeFileException.error("Failed to open " + filename + " for saving to KMZ.", scope);
		}
	}

	public void hideFolder(final String folname) {
		final KmlFolder kf = getFolder(folname);
		kf.setVisibility(false);
	}

	public void showFolder(final String folname) {
		final KmlFolder kf = getFolder(folname);
		kf.setVisibility(true);
	}

	protected KmlFolder getDefaultFolder() {
		if (defolder == null) {
			defolder = new KmlFolder(doc, "kml");
			folders.put("kml", defolder);
		}
		return defolder;
	}

	protected KmlFolder getFolder(final String folname) {
		KmlFolder kf = folders.get(folname);
		if (kf == null) {
			kf = new KmlFolder(doc, folname);
			folders.put(folname, kf);
		}
		return kf;

	}

	protected String dateToKml(final GamaDate d) {
		return d.toISOString();
		// ("yyyy-MM-dd") + "T" + d.toString("HH:mm:ss");
	}

	public void addLabel(final IScope scope, final GamaPoint loc, final GamaDate beginDate, final GamaDate endDate,
			final String name, final String description, final String styleName) {
		getDefaultFolder().addLabel(scope, loc, dateToKml(beginDate), dateToKml(endDate), name, description, styleName);
	}

	public void addLabel(final IScope scope, final String foldname, final GamaPoint loc, final GamaDate beginDate,
			final GamaDate endDate, final String name, final String description, final String styleName) {
		getFolder(foldname).addLabel(scope, loc, dateToKml(beginDate), dateToKml(endDate), name, description,
				styleName);
	}

	public class KmlFolder {
		public Folder fold;
		static final String ERR_HEADER = "Kml Export: ";

		public KmlFolder(final Document doc, final String label, final String beginDate, final String endDate) {
			this.fold = doc.createAndAddFolder();
			fold.withName(label).createAndSetTimeSpan().withBegin(beginDate).withEnd(endDate);
		}

		public KmlFolder(final Document doc, final String label) {
			this.fold = doc.createAndAddFolder();
			fold.withName(label);
		}

		/**
		 * Adds a KML Extruded Label (see https://kml-samples.googlecode.com/svn/trunk
		 * /interactive/index.html#./Point_Placemarks/Point_Placemarks.Extruded.kml)
		 *
		 * @param xpos
		 *            Latitude or X position, units depending on the CRS used in the model
		 * @param ypos
		 *            Longitude or Y position, units depending on the CRS used in the model
		 * @param height
		 *            Height at which the label should be displayed
		 * @param beginDate
		 *            Begining date of the timespan
		 * @param endDate
		 *            End date of the timespan
		 * @param name
		 *            Title (always displayed)
		 * @param description
		 *            Description (only displayed on mouse click on the label)
		 */
		public void addLabel(final IScope scope, final ILocation loc, final String beginDate, final String endDate,
				final String name, final String description, final String styleName) {
			final Placemark placemark = fold.createAndAddPlacemark().withStyleUrl("#" + styleName);
			placemark.createAndSetTimeSpan().withBegin(beginDate).withEnd(endDate);
			final msi.gama.ext.kml.Point ls = placemark.createAndSetPoint();
			ls.setExtrude(true);
			ls.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
			final GamaPoint locTM = Spatial.Projections.transform_CRS(scope, loc, "EPSG:4326").getCentroid();

			ls.addToCoordinates(locTM.x, locTM.y, locTM.z);
			placemark.setName(name);
			placemark.setDescription(description);
		}

		public void add3DModel(final IScope scope, final ILocation loc, final double orientation, final double scale,
				final String beginDate, final String endDate, final String daefile) {

			final Placemark placemark = fold.createAndAddPlacemark();
			placemark.createAndSetTimeSpan().withBegin(beginDate).withEnd(endDate);
			final Model model = placemark.createAndSetModel();
			model.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
			final GamaPoint locTM = Spatial.Projections.transform_CRS(scope, loc, "EPSG:4326").getCentroid();
			final Location locKML = new Location();
			locKML.setLongitude(locTM.x);
			locKML.setLatitude(locTM.y);
			locKML.setAltitude(locTM.z);
			model.setLocation(locKML);
			model.setScale(new Scale().withX(scale).withY(scale).withZ(scale));
			model.setLink(new Link().withHref(daefile));
			model.setOrientation(new Orientation().withHeading(orientation));
		}

		/**
		 * Add a placemark with a geometry object.The geometry can be a Point, a Line, a Polygon or any Multi-geometry.
		 * Points will be represented by an icon and linear or surface objects will be drawn.
		 *
		 * @param label
		 *            The title of the folder that will be created for this ShpRecord
		 * @param beginDate
		 *            Begining date of the timespan
		 * @param endDate
		 *            End date of the timespan
		 * @param geom
		 *            Geometry object to be drawn
		 * @param height
		 *            Height of the feature to draw. If > 0 the feature will be shown extruded to the given height
		 *            (relative to the ground level). If <= 0 the feature will be drawn flat on the ground.
		 */
		public void addGeometry(final IScope scope, final String label, final String beginDate, final String endDate,
				final IShape shape, final String styleName, final double height) {
			final Placemark placemark = fold.createAndAddPlacemark().withStyleUrl("#" + styleName);
			placemark.setName(label);
			placemark.createAndSetTimeSpan().withBegin(beginDate).withEnd(endDate);

			final IShape shapeTM = Spatial.Projections.transform_CRS(scope, shape, "EPSG:4326");
			final Geometry geom = shapeTM.getInnerGeometry();

			if (geom instanceof Point) {
				addPoint(placemark, (Point) geom, height);
			} else if (geom instanceof LineString) {
				addLine(placemark, (LineString) geom, height);
			} else if (geom instanceof Polygon) {
				addPolygon(placemark, (Polygon) geom, height);
			} else if (geom instanceof MultiPoint) {
				addMultiPoint(placemark, (MultiPoint) geom, height);
			} else if (geom instanceof MultiLineString) {
				addMultiLine(placemark, (MultiLineString) geom, height);
			} else if (geom instanceof MultiPolygon) {
				addMultiPolygon(placemark, (MultiPolygon) geom, height);
			}
		}

		public void setVisibility(final boolean value) {
			this.fold.setVisibility(value);
			for (final Feature f : this.fold.getFeature()) {
				f.setVisibility(value);
			}
		}

		public void addPoint(final Placemark pm, final Point point, final double height) {
			final msi.gama.ext.kml.Point kmlpoint = pm.createAndSetPoint();
			fillPoint(kmlpoint, point, height);
		}

		public void fillPoint(final msi.gama.ext.kml.Point kmlpoint, final Point point,
				final double height) {
			final Coordinate pos = point.getCoordinate();
			if (height > 0.0) {
				kmlpoint.setExtrude(true);
				kmlpoint.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
				kmlpoint.addToCoordinates(pos.x, pos.y, height);
			} else {
				kmlpoint.addToCoordinates(pos.x, pos.y);
			}
		}

		public void addLine(final Placemark pm, final LineString line, final double height) {
			final msi.gama.ext.kml.LineString kmlline = pm.createAndSetLineString();
			fillLine(kmlline, line, height);
		}

		public void fillLine(final msi.gama.ext.kml.LineString kmlline, final LineString line,
				final double height) {
			if (height > 0.0) {
				kmlline.setExtrude(true);
				kmlline.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
				for (final Coordinate pos : line.getCoordinates()) {
					kmlline.addToCoordinates(pos.x, pos.y, height);
				}
			} else {
				for (final Coordinate pos : line.getCoordinates()) {
					kmlline.addToCoordinates(pos.x, pos.y);
				}
			}
		}

		public void addPolygon(final Placemark pm, final Polygon poly, final double height) {
			final msi.gama.ext.kml.Polygon kmlpoly = pm.createAndSetPolygon();
			fillPolygon(kmlpoly, poly, height);
		}

		public void fillPolygon(final msi.gama.ext.kml.Polygon kmlpoly, final Polygon poly,
				final double height) {

			// Shell ring
			final msi.gama.ext.kml.LinearRing kmlring =
					kmlpoly.createAndSetOuterBoundaryIs().createAndSetLinearRing();
			if (height > 0.0) {
				kmlpoly.setExtrude(true);
				kmlpoly.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
				for (final Coordinate pos : poly.getExteriorRing().getCoordinates()) {
					kmlring.addToCoordinates(pos.x, pos.y, height);
				}
			} else {
				kmlpoly.setTessellate(true);
				for (final Coordinate pos : poly.getExteriorRing().getCoordinates()) {
					kmlring.addToCoordinates(pos.x, pos.y);
				}
			}

			// Holes
			for (int hi = 0; hi < poly.getNumInteriorRing(); hi++) {
				final msi.gama.ext.kml.LinearRing kmlhole =
						kmlpoly.createAndAddInnerBoundaryIs().createAndSetLinearRing();
				if (height > 0.0) {
					kmlpoly.setExtrude(true);
					kmlpoly.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
					for (final Coordinate pos : poly.getInteriorRingN(hi).getCoordinates()) {
						kmlhole.addToCoordinates(pos.x, pos.y, height);
					}
				} else {
					kmlpoly.setTessellate(true);
					for (final Coordinate pos : poly.getInteriorRingN(hi).getCoordinates()) {
						kmlhole.addToCoordinates(pos.x, pos.y);
					}
					;
				}
			}
		}

		public void addMultiPoint(final Placemark pm, final MultiPoint mpoint, final double height) {
			final int ng = mpoint.getNumGeometries();
			final MultiGeometry mg = pm.createAndSetMultiGeometry();
			for (int gx = 0; gx < ng; gx++) {
				final msi.gama.ext.kml.Point kmlpoint = mg.createAndAddPoint();
				fillPoint(kmlpoint, (Point) mpoint.getGeometryN(gx), height);
			}
		}

		public void addMultiLine(final Placemark pm, final MultiLineString mline, final double height) {
			final int ng = mline.getNumGeometries();
			final MultiGeometry mg = pm.createAndSetMultiGeometry();
			for (int gx = 0; gx < ng; gx++) {
				final msi.gama.ext.kml.LineString kmlline = mg.createAndAddLineString();
				fillLine(kmlline, (LineString) mline.getGeometryN(gx), height);
			}
		}

		public void addMultiPolygon(final Placemark pm, final MultiPolygon mpoly, final double height) {
			final int ng = mpoly.getNumGeometries();
			final MultiGeometry mg = pm.createAndSetMultiGeometry();
			for (int gx = 0; gx < ng; gx++) {
				final msi.gama.ext.kml.Polygon kmlpoly = mg.createAndAddPolygon();
				fillPolygon(kmlpoly, (Polygon) mpoly.getGeometryN(gx), height);
			}
		}
	}
}