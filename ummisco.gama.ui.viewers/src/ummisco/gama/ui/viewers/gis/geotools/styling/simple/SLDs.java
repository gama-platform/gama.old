/*********************************************************************************************
 *
 * 'SLDs.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling.simple;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.FontData;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.filter.Filters;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
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

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
 * When you start to branch out to SLD information that contains multiple rules
 * you will need to modify this class.
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
		if (graphic == null) {
			return NOTFOUND;
		}
		return Filters.asInt(graphic.getSize());
	}

	public static Color polyFill(final PolygonSymbolizer symbolizer) {
		if (symbolizer == null) {
			return null;
		}

		final Fill fill = symbolizer.getFill();

		if (fill == null) {
			return null;
		}

		final Expression color = fill.getColor();
		return color(color);
	}

	public static Color color(final Expression expr) {
		if (expr == null) {
			return null;
		}
		try {
			return expr.evaluate(null, Color.class);
		} catch (final Throwable t) {
			class ColorVisitor implements ExpressionVisitor {
				Color found;

				@Override
				public Object visit(final Literal expr, final Object data) {
					if (found != null)
						return null;
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
	 * Grabs the font from the first TextSymbolizer.
	 * <p>
	 * If you are using something fun like symbols you will need to do your own
	 * thing.
	 * </p>
	 * 
	 * @param symbolizer
	 *            Text symbolizer information.
	 * @return FontData[] of the font's fill, or null if unavailable.
	 */
	public static FontData[] textFont(final TextSymbolizer symbolizer) {

		final Font font = font(symbolizer);
		if (font == null)
			return null;

		final FontData[] tempFD = new FontData[1];
		final Expression fontFamilyExpression = font.getFamily().get(0);
		final Expression sizeExpression = font.getSize();
		if (sizeExpression == null || fontFamilyExpression == null)
			return null;

		final Double size = sizeExpression.evaluate(null, Double.class);

		try {
			final String fontFamily = fontFamilyExpression.evaluate(null, String.class);
			tempFD[0] = new FontData(fontFamily, size.intValue(), 1);
		} catch (final NullPointerException ignore) {
			return null;
		}
		if (tempFD[0] != null)
			return tempFD;
		return null;
	}

	/**
	 * Retrieves all colour names defined in a rule
	 * 
	 * @param rule
	 *            the rule
	 * @return an array of unique colour names
	 */
	public static String[] colors(final Rule rule) {
		final Set<String> colorSet = new HashSet<String>();

		Color color = null;
		for (final Symbolizer sym : rule.symbolizers()) {
			if (sym instanceof PolygonSymbolizer) {
				final PolygonSymbolizer symb = (PolygonSymbolizer) sym;
				color = polyFill(symb);

			} else if (sym instanceof LineSymbolizer) {
				final LineSymbolizer symb = (LineSymbolizer) sym;
				color = color(symb);

			} else if (sym instanceof PointSymbolizer) {
				final PointSymbolizer symb = (PointSymbolizer) sym;
				color = pointFillWithAlpha(symb);
			}

			if (color != null) {
				colorSet.add(SLD.colorToHex(color));
			}
		}

		if (colorSet.size() > 0) {
			return colorSet.toArray(new String[0]);
		} else {
			return new String[0];
		}
	}

	/**
	 * Extracts the fill color with a given opacity from the
	 * {@link PointSymbolizer}.
	 * 
	 * @param symbolizer
	 *            the point symbolizer from which to get the color.
	 * @return the {@link Color} with transparency if available. Returns null if
	 *         no color is available.
	 */
	public static Color pointFillWithAlpha(final PointSymbolizer symbolizer) {
		if (symbolizer == null) {
			return null;
		}

		final Graphic graphic = symbolizer.getGraphic();
		if (graphic == null) {
			return null;
		}

		for (final GraphicalSymbol gs : graphic.graphicalSymbols()) {
			if (gs != null && gs instanceof Mark) {
				final Mark mark = (Mark) gs;
				final Fill fill = mark.getFill();
				if (fill != null) {
					Color colour = color(fill.getColor());
					if (colour == null) {
						return null;
					}
					Expression opacity = fill.getOpacity();
					if (opacity == null)
						opacity = ff.literal(1.0);
					final float alpha = (float) Filters.asDouble(opacity);
					colour = new Color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f,
							alpha);
					return colour;
				}
			}
		}

		return null;
	}

	/**
	 * Extracts the stroke color with a given opacity from the
	 * {@link PointSymbolizer}.
	 * 
	 * @param symbolizer
	 *            the point symbolizer from which to get the color.
	 * @return the {@link Color} with transparency if available. Returns null if
	 *         no color is available.
	 */
	public static Color pointStrokeColorWithAlpha(final PointSymbolizer symbolizer) {
		if (symbolizer == null) {
			return null;
		}

		final Graphic graphic = symbolizer.getGraphic();
		if (graphic == null) {
			return null;
		}

		for (final GraphicalSymbol gs : graphic.graphicalSymbols()) {
			if (gs != null && gs instanceof Mark) {
				final Mark mark = (Mark) gs;
				final Stroke stroke = mark.getStroke();
				if (stroke != null) {
					Color colour = color(stroke);
					if (colour == null) {
						return null;
					}
					Expression opacity = stroke.getOpacity();
					if (opacity == null)
						opacity = ff.literal(1.0);
					final float alpha = (float) Filters.asDouble(opacity);
					colour = new Color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f,
							alpha);
					return colour;
				}
			}
		}

		return null;
	}

	public static Font font(final TextSymbolizer symbolizer) {
		if (symbolizer == null)
			return null;
		final Font font = symbolizer.getFont();
		return font;
	}

	public static Style getDefaultStyle(final StyledLayerDescriptor sld) {
		final Style[] styles = styles(sld);
		for (int i = 0; i < styles.length; i++) {
			final Style style = styles[i];
			final List<FeatureTypeStyle> ftStyles = style.featureTypeStyles();
			genericizeftStyles(ftStyles);
			if (style.isDefault()) {
				return style;
			}
		}
		// no default, so just grab the first one
		return styles[0];
	}

	/**
	 * Converts the type name of all FeatureTypeStyles to Feature so that the
	 * all apply to any feature type. This is admittedly dangerous but is
	 * extremely useful because it means that the style can be used with any
	 * feature type.
	 *
	 * @param ftStyles
	 */
	private static void genericizeftStyles(final List<FeatureTypeStyle> ftStyles) {
		for (final FeatureTypeStyle featureTypeStyle : ftStyles) {
			featureTypeStyle.featureTypeNames().clear();
			featureTypeStyle.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
		}
	}

	public static boolean isSemanticTypeMatch(final FeatureTypeStyle fts, final String regex) {
		final Set<SemanticType> identifiers = fts.semanticTypeIdentifiers();
		for (final SemanticType semanticType : identifiers) {
			if (semanticType.matches(regex))
				return true;
		}
		return false;
	}

	/**
	 * Returns the min scale of the default rule, or 0 if none is set
	 */
	public static double minScale(final FeatureTypeStyle fts) {
		if (fts == null || fts.rules().size() == 0)
			return 0.0;

		final Rule r = fts.rules().get(0);
		return r.getMinScaleDenominator();
	}

	/**
	 * Returns the max scale of the default rule, or {@linkplain Double#NaN} if
	 * none is set
	 */
	public static double maxScale(final FeatureTypeStyle fts) {
		if (fts == null || fts.rules().size() == 0)
			return Double.NaN;

		final Rule r = fts.rules().get(0);
		return r.getMaxScaleDenominator();
	}

	/**
	 * gets the first FeatureTypeStyle
	 */
	public static FeatureTypeStyle getFeatureTypeStyle(final Style s) {
		final List<FeatureTypeStyle> fts = s.featureTypeStyles();
		if (fts.size() > 0) {
			return fts.get(0);
		}
		return null;
	}

	/**
	 * Find the first rule which contains a rastersymbolizer, and return it
	 *
	 * @param s
	 *            A style to search in
	 * @return a rule, or null if no raster symbolizers are found.
	 */
	public static Rule getRasterSymbolizerRule(final Style s) {
		final List<FeatureTypeStyle> fts = s.featureTypeStyles();
		for (int i = 0; i < fts.size(); i++) {
			final FeatureTypeStyle featureTypeStyle = fts.get(i);
			final List<Rule> rules = featureTypeStyle.rules();
			for (int j = 0; j < rules.size(); j++) {
				final Rule rule = rules.get(j);
				final Symbolizer[] symbolizers = rule.getSymbolizers();
				for (int k = 0; k < symbolizers.length; k++) {
					final Symbolizer symbolizer = symbolizers[k];
					if (symbolizer instanceof RasterSymbolizer) {
						return rule;
					}
				}

			}
		}
		return null;
	}

	/**
	 * The type name that can be used in an SLD in the featuretypestyle that
	 * matches all feature types.
	 */
	public static final String GENERIC_FEATURE_TYPENAME = "Feature";

	public static final boolean isPolygon(final SimpleFeatureType featureType) {
		if (featureType == null)
			return false;
		return isPolygon(featureType.getGeometryDescriptor());
	}

	/* This needed to be a function as it was being written poorly everywhere */
	public static final boolean isPolygon(final GeometryDescriptor geometryType) {
		if (geometryType == null)
			return false;
		final Class<?> type = geometryType.getType().getBinding();
		return Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
	}

	public static final boolean isLine(final SimpleFeatureType featureType) {
		if (featureType == null)
			return false;
		return isLine(featureType.getGeometryDescriptor());
	}

	/* This needed to be a function as it was being written poorly everywhere */
	public static final boolean isLine(final GeometryDescriptor geometryType) {
		if (geometryType == null)
			return false;
		final Class<?> type = geometryType.getType().getBinding();
		return LineString.class.isAssignableFrom(type) || MultiLineString.class.isAssignableFrom(type);
	}

	public static final boolean isPoint(final SimpleFeatureType featureType) {
		if (featureType == null)
			return false;
		return isPoint(featureType.getGeometryDescriptor());
	}

	/* This needed to be a function as it was being writen poorly everywhere */
	public static final boolean isPoint(final GeometryDescriptor geometryType) {
		if (geometryType == null)
			return false;
		final Class<?> type = geometryType.getType().getBinding();
		return Point.class.isAssignableFrom(type) || MultiPoint.class.isAssignableFrom(type);
	}
}
