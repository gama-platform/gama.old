/*********************************************************************************************
 *
 * 'Utils.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling;

import java.awt.Color;

import org.eclipse.swt.graphics.Rectangle;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

/**
 * Utilities class.
 *
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class Utils {
	/**
	 * The default StyleFactory to use.
	 */
	public static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

	/**
	 * The default FilterFactory to use.
	 */
	public static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

	/**
	 * Transform a swt Rectangle instance into an awt one.
	 *
	 * @param rect
	 *            the swt <code>Rectangle</code>
	 * @return a java.awt.Rectangle instance with the appropriate location and size.
	 */
	public static java.awt.Rectangle toAwtRectangle(final Rectangle rect) {
		final java.awt.Rectangle rect2d = new java.awt.Rectangle();
		rect2d.setRect(rect.x, rect.y, rect.width, rect.height);
		return rect2d;
	}

	/**
	 * Create a default Style ofr the featureSource.
	 *
	 * @param featureSource
	 *            the source on which to create the style.
	 * @return the style created.
	 */
	public static Style createStyle2(final SimpleFeatureSource featureSource) {
		final SimpleFeatureType schema = featureSource.getSchema();
		final Class<?> geomType = schema.getGeometryDescriptor().getType().getBinding();

		if (Polygon.class.isAssignableFrom(geomType) || MultiPolygon.class.isAssignableFrom(geomType)) {
			return createPolygonStyle();

		} else if (LineString.class.isAssignableFrom(geomType) || MultiLineString.class.isAssignableFrom(geomType)) {
			return createLineStyle();

		} else {
			return createPointStyle();
		}
	}

	/**
	 * Create a default polygon style.
	 *
	 * @return the created style.
	 */
	public static Style createPolygonStyle() {

		// create a partially opaque outline stroke
		final Stroke stroke = styleFactory.createStroke(filterFactory.literal(Color.BLUE), filterFactory.literal(1),
				filterFactory.literal(0.5));

		// create a partial opaque fill
		final Fill fill = styleFactory.createFill(filterFactory.literal(Color.CYAN), filterFactory.literal(0.5));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
		 */
		final PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	/**
	 * Create a default line style.
	 *
	 * @return the created style.
	 */
	public static Style createLineStyle() {
		final Stroke stroke = styleFactory.createStroke(filterFactory.literal(Color.BLUE), filterFactory.literal(1));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
		 */
		final LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	/**
	 * Create a default point style.
	 *
	 * @return the created style.
	 */
	public static Style createPointStyle() {
		final Graphic gr = styleFactory.createDefaultGraphic();
		final Mark mark = styleFactory.getCircleMark();
		mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLUE), filterFactory.literal(1)));
		mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));
		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(filterFactory.literal(5));
		/*
		 * Setting the geometryPropertyName arg to null signals that we want to draw the default geomettry of features
		 */
		final PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

		final Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		final FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		final Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

}
