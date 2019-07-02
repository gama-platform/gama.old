/*********************************************************************************************
 *
 * 'SLDs.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling;

import java.awt.Color;
import java.util.Set;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.Filters;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.SemanticType;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Utility class for working with Geotools SLD objects.
 * <p>
 * This class assumes a subset of the SLD specification:
 * <ul>
 * <li>Single Rule - matching Filter.INCLUDE
 * <li>Symbolizer lookup by name
 * </ul>
 * </p>
 * <p>
 * When you start to branch out to SLD information that contains multiple rules you will need to modify this class.
 * </p>
 *
 * @author Jody Garnett, Refractions Research.
 * @since 0.7.0
 *
 *
 *
 * @source $URL$
 */
public class SLDs extends SLD {
	private static FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);

	public static final double ALIGN_LEFT = 1.0;
	public static final double ALIGN_CENTER = 0.5;
	public static final double ALIGN_RIGHT = 0.0;
	public static final double ALIGN_BOTTOM = 1.0;
	public static final double ALIGN_MIDDLE = 0.5;
	public static final double ALIGN_TOP = 0.0;

	public static int size(final Graphic graphic) {
		if (graphic == null) { return NOTFOUND; }
		return Filters.asInt(graphic.getSize());
	}

	public static Color polyFill(final PolygonSymbolizer symbolizer) {
		if (symbolizer == null) { return null; }

		final Fill fill = symbolizer.getFill();

		if (fill == null) { return null; }

		final Expression color = fill.getColor();
		return color(color);
	}

	public static Color color(final Expression expr) {
		if (expr == null) { return null; }
		try {
			return expr.evaluate(null, Color.class);
		} catch (final Throwable t) {
			class ColorVisitor implements ExpressionVisitor {
				Color found;

				@Override
				public Object visit(final Literal expr, final Object data) {
					if (found != null) { return null; }
					try {
						final Color color = expr.evaluate(expr, Color.class);
						if (color != null) {
							found = color;
						}
					} catch (final Throwable t) {
						// not a color
					}
					return data;
				}

				@Override
				public Object visit(final NilExpression arg0, final Object data) {
					return data;
				}

				@Override
				public Object visit(final Add arg0, final Object data) {
					return data;
				}

				@Override
				public Object visit(final Divide arg0, final Object data) {
					return null;
				}

				@Override
				public Object visit(final Function function, final Object data) {
					for (final Expression param : function.getParameters()) {
						param.accept(this, data);
					}
					return data;
				}

				@Override
				public Object visit(final Multiply arg0, final Object data) {
					return data;
				}

				@Override
				public Object visit(final PropertyName arg0, final Object data) {
					return data;
				}

				@Override
				public Object visit(final Subtract arg0, final Object data) {
					return data;
				}
			}
			final ColorVisitor search = new ColorVisitor();
			expr.accept(search, null);

			return search.found;
		}
	}

	/**
	 * Extracts the fill color with a given opacity from the PointSymbolizer.
	 *
	 * @param symbolizer
	 *            the point symbolizer from which to get the color.
	 * @return the Color with transparency if available. Returns null if no color is available.
	 */
	public static Color pointFillWithAlpha(final PointSymbolizer symbolizer) {
		if (symbolizer == null) { return null; }

		final Graphic graphic = symbolizer.getGraphic();
		if (graphic == null) { return null; }

		for (final GraphicalSymbol gs : graphic.graphicalSymbols()) {
			if (gs != null && gs instanceof Mark) {
				final Mark mark = (Mark) gs;
				final Fill fill = mark.getFill();
				if (fill != null) {
					Color colour = color(fill.getColor());
					if (colour == null) { return null; }
					Expression opacity = fill.getOpacity();
					if (opacity == null) {
						opacity = ff.literal(1.0);
					}
					final float alpha = (float) Filters.asDouble(opacity);
					colour = new Color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f,
							alpha);
					return colour;
				}
			}
		}

		return null;
	}

	public static boolean isSemanticTypeMatch(final FeatureTypeStyle fts, final String regex) {
		final Set<SemanticType> identifiers = fts.semanticTypeIdentifiers();
		for (final SemanticType semanticType : identifiers) {
			if (semanticType.matches(regex)) { return true; }
		}
		return false;
	}

	/**
	 * Returns the min scale of the default rule, or 0 if none is set
	 */
	public static double minScale(final FeatureTypeStyle fts) {
		if (fts == null || fts.rules().size() == 0) { return 0.0; }

		final Rule r = fts.rules().get(0);
		return r.getMinScaleDenominator();
	}

	/**
	 * Returns the max scale of the default rule, or Double#NaN if none is set
	 */
	public static double maxScale(final FeatureTypeStyle fts) {
		if (fts == null || fts.rules().size() == 0) { return Double.NaN; }

		final Rule r = fts.rules().get(0);
		return r.getMaxScaleDenominator();
	}

	/**
	 * The type name that can be used in an SLD in the featuretypestyle that matches all feature types.
	 */
	public static final String GENERIC_FEATURE_TYPENAME = "Feature";

	public static final boolean isPolygon(final SimpleFeatureType featureType) {
		if (featureType == null) { return false; }
		final GeometryDescriptor geometryType = featureType.getGeometryDescriptor();
		if (geometryType == null) { return false; }
		final Class<?> type = geometryType.getType().getBinding();
		return Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
	}

	public static final boolean isLine(final SimpleFeatureType featureType) {
		if (featureType == null) { return false; }
		final GeometryDescriptor geometryType = featureType.getGeometryDescriptor();
		if (geometryType == null) { return false; }
		final Class<?> type = geometryType.getType().getBinding();
		return LineString.class.isAssignableFrom(type) || MultiLineString.class.isAssignableFrom(type);
	}

	public static final boolean isPoint(final SimpleFeatureType featureType) {
		if (featureType == null) { return false; }
		final GeometryDescriptor geometryType = featureType.getGeometryDescriptor();
		if (geometryType == null) { return false; }
		final Class<?> type = geometryType.getType().getBinding();
		return Point.class.isAssignableFrom(type) || MultiPoint.class.isAssignableFrom(type);
	}
}
