/*********************************************************************************************
 *
 *
 * 'DrawStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import static msi.gama.common.interfaces.IKeyword.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.*;
import msi.gaml.statements.DrawStatement.DrawValidator;
import msi.gaml.types.*;

// A command that is used to draw shapes, figures, text on the display

@symbol(name = DRAW, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@facets(
	value = {
		// Allows to pass any arbitrary geometry to the drawing command
		@facet(name = IKeyword.GEOMETRY,
			type = IType.NONE,
			optional = true,
			doc = @doc("any type of data (it can be geometry, image, text)") ),
		// AD 18/01/13: geometry is now accepting any type of data
		@facet(name = SHAPE,
			type = IType.NONE,
			optional = true,
			doc = @doc(value = "the shape to display", deprecated = "Please  use 'draw the_shape_to_draw' instead") ),
		@facet(name = TEXT,
			type = IType.STRING,
			optional = true,
			doc = @doc(value = "the text to draw", deprecated = "Please  use 'draw the_text_to_draw' instead") ),
		@facet(name = IMAGE,
			type = IType.STRING,
			optional = true,
			doc = @doc(value = "path of the icon to draw (JPEG, PNG, GIF)",
				deprecated = "Please use 'draw the_image_file_to_draw' instead") ),
		@facet(name = TEXTURE,
			type = { IType.STRING, IType.LIST },
			optional = true,
			doc = @doc("the texture that should be applied to the geometry") ),
		@facet(name = EMPTY,
			type = IType.BOOL,
			optional = true,
			doc = @doc("a condition specifying whether the geometry is empty or full") ),
		@facet(name = BORDER,
			type = { IType.COLOR, IType.BOOL },
			optional = true,
			doc = @doc("if used with a color, represents the color of the geometry border. If set to false, expresses that no border should be drawn. If not set, the borders will be drawn using the color of the geometry.") ),
		@facet(name = ROUNDED,
			type = IType.BOOL,
			optional = true,
			doc = @doc(value = "specify whether the geometry have to be rounded (e.g. for squares)",
				deprecated = "Use the squircle operator to draw rounded squares") ),
		@facet(name = AT,
			type = IType.POINT,
			optional = true,
			doc = @doc("location where the shape/text/icon is drawn") ),
		@facet(name = SIZE,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("size of the text/icon (not used in the context of the drawing of a geometry)") ),
		@facet(name = TO, type = IType.POINT, optional = true, doc = @doc("") , internal = true),
		@facet(name = COLOR,
			type = IType.COLOR,
			optional = true,
			doc = @doc("the color to use to display the text/icon/geometry") ),
		@facet(name = SCALE, type = IType.FLOAT, optional = true, doc = @doc("") ),
		@facet(name = ROTATE,
			type = { IType.FLOAT, IType.INT },
			optional = true,
			doc = @doc("orientation of the shape/text/icon") ),
		@facet(name = ROTATE3D,
			type = { IType.PAIR },
			optional = true,
			doc = @doc("orientation of the shape/text/icon") ),
		@facet(name = FONT,
			type = { IType.FONT, IType.STRING },
			optional = true,
			doc = @doc("the font used to draw the text") ),
		@facet(name = BITMAP, type = IType.BOOL, optional = true, doc = @doc("") ),
		@facet(name = DEPTH,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("(only if the display type is opengl) Add a depth to the geometry previously defined (a line becomes a plan, a circle becomes a cylinder, a square becomes a cube, a polygon becomes a polyhedron with height equal to the depth value). Note: This only works if a the agent has not a point geometry") ),
		@facet(name = DrawStatement.BEGIN_ARROW,
			type = { IType.INT, IType.FLOAT },
			optional = true,
			doc = @doc("the size of the arrow, located at the beginning of the drawn geometry") ),
		@facet(name = DrawStatement.END_ARROW,
			type = { IType.INT, IType.FLOAT },
			optional = true,
			doc = @doc("the size of the arrow, located at the end of the drawn geometry") ),
		@facet(name = STYLE,
			type = IType.ID,
			values = { "plain", "bold", "italic" },
			optional = true,
			doc = @doc(value = "the style used to display text",
				deprecated = "Use the font operator with constants #bold, #italic, #plain instead") ) },
	omissible = IKeyword.GEOMETRY)
@inside(symbols = { ASPECT }, kinds = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@doc(
	value = "`" + DRAW +
		"` is used in an aspect block to expresse how agents of the species will be drawn. It is evaluated each time the agent has to be drawn. It can also be used in the graphics block.",
	usages = {
		@usage(
			value = "Any kind of geometry as any location can be drawn when displaying an agent (independently of his shape)",
			examples = { @example(value = "aspect geometryAspect {", isExecutable = false),
				@example(value = "	draw circle(1.0) empty: !hasFood color: #orange ;", isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(value = "Image or text can also be drawn",
			examples = { @example(value = "aspect arrowAspect {", isExecutable = false),
				@example(
					value = "	draw \"Current state= \"+state at: location + {-3,1.5} color: #white font: font('Default', 12, #bold) ;",
					isExecutable = false),
				@example(value = "	draw file(ant_shape_full) rotate: heading at: location size: 5",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(
			value = "Arrows can be drawn with any kind of geometry, using " + DrawStatement.BEGIN_ARROW + " and " +
				DrawStatement.END_ARROW +
				" facets, combined with the empty: facet to specify whether it is plain or empty",
			examples = { @example(value = "aspect arrowAspect {", isExecutable = false),
				@example(value = "	draw line([{20, 20}, {40, 40}]) color: #black begin_arrow:5;",
					isExecutable = false),
				@example(
					value = "	draw line([{10, 10},{20, 50}, {40, 70}]) color: #green end_arrow: 2 begin_arrow: 2 empty: true;",
					isExecutable = false),
				@example(value = "	draw square(10) at: {80,20} color: #purple begin_arrow: 2 empty: true;",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }) })
@validator(DrawValidator.class)
public class DrawStatement extends AbstractStatementSequence {

	public static class DrawValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			IExpressionDescription geom = description.getFacets().get(GEOMETRY);
			if ( geom != null ) {
				for ( String s : Arrays.asList(TEXT, SHAPE, IMAGE) ) {
					IExpressionDescription other = description.getFacets().getDescr(s);
					if ( other != null ) {
						description.warning("'" + s + "' has no effect here", IGamlIssue.CONFLICTING_FACETS, s);
					}
				}

			}
		}

	}

	public static final String END_ARROW = "end_arrow";
	public static final String BEGIN_ARROW = "begin_arrow";

	static final GamaPoint LOC = new GamaPoint(1.0, 1.0);
	public static final Map<String, Integer> CONSTANTS = new HashMap();
	public static final Map<String, Integer> SHAPES = new HashMap();

	static {
		CONSTANTS.put("plain", 0);
		CONSTANTS.put("bold", 1);
		CONSTANTS.put("italic", 2);
		SHAPES.put("geometry", 0);
		SHAPES.put("square", 1);
		SHAPES.put("circle", 2);
		SHAPES.put("triangle", 3);
		SHAPES.put("rectangle", 1);
		SHAPES.put("disc", 2);
		SHAPES.put("line", 4);
	}

	IExpression color, item;

	private final DrawExecuter executer;

	private final IExpression getShapeExpression(final IDescription desc) {
		return GAML.getExpressionFactory().createVar(SHAPE, Types.GEOMETRY, false, IVarExpression.AGENT, desc);
	}

	public DrawStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		item = getFacet(IKeyword.GEOMETRY, SHAPE, IMAGE, TEXT);
		color = getFacet(IKeyword.COLOR);
		if ( item == null ) {
			executer = null;
			return;
		}
		// Compatibility with the old 'draw + shape' statement
		item = patchForCompatibility(item, desc);
		//
		if ( item.getType().id() == IType.GEOMETRY ) {
			executer = new ShapeExecuter(desc);
		} else if ( item.getType().id() == IType.FILE ) {
			executer = new ImageExecuter(desc);
		} else if ( item.getType().id() == IType.STRING ) {
			executer = new TextExecuter(desc);
		} else if ( item.getType().id() == IType.COLOR ) {
			color = item;
			item = getShapeExpression(desc);
			executer = new ShapeExecuter(desc);
		} else {
			// item is supposed to be castable into a geometry
			executer = new ShapeExecuter(desc);
		}
	}

	/**
	 * Various patches to keep the compatibility with GAMA 1.5 and previous versions, where symbols
	 * could be used to draw shapes
	 * @param exp, the expression representing what is to be drawn
	 * @param desc, the description of the statement (used as a context for creating new
	 * expressions)
	 * @return the new expression, patched for compatibility
	 */
	private IExpression patchForCompatibility(final IExpression exp, final IDescription desc) {
		IExpression newExpr = exp;
		if ( exp.getType().id() == IType.STRING && exp.isConst() ) {
			String old = Cast.asString(null, exp.value(null));
			if ( old.contains("deprecated") ) {
				old = old.split("__")[0];
				if ( old.equals("disc") || old.equals("circle") ) {
					IExpression sizeExp = getFacet(SIZE);
					if ( sizeExp == null ) {
						sizeExp = GAML.getExpressionFactory().createConst(1, Types.INT);
					}
					newExpr = GAML.getExpressionFactory().createOperator("circle", desc, null, sizeExp);
				} else if ( old.equals("rectangle") || old.equals("square") ) {
					IExpression sizeExp = getFacet(SIZE);
					if ( sizeExp == null ) {
						sizeExp = GAML.getExpressionFactory().createConst(1, Types.INT);
					}

					newExpr = GAML.getExpressionFactory().createOperator("square", desc, null, sizeExp);
				} else if ( old.equals("geometry") ) {
					newExpr = getShapeExpression(desc);
				} else if ( old.equals("line") ) {
					IExpression at = getFacet(AT);
					final IExpression to = getFacet(TO);
					if ( at == null ) {
						at = GAML.getExpressionFactory().createVar("location", Types.POINT, false, IVarExpression.AGENT,
							desc);
					}
					final List<IExpression> elements = new ArrayList();
					elements.add(at);
					elements.add(to);
					final IExpression list = GAML.getExpressionFactory().createList(elements);
					newExpr = GAML.getExpressionFactory().createOperator("line", desc, null, list);
				}
			} else {
				if ( GamaFileType.verifyExtension("image", old) ) {
					newExpr = GAML.getExpressionFactory().createOperator("file", desc, null, exp);
				}
			}
			// if ( newExpr == null ) {
			// newExpr = exp;
			// }
			// if ( newExpr != null ) {
			desc.getFacets().put(IKeyword.GEOMETRY, newExpr);
			// } else {
			// If no operator has been found, we throw an exception
			// desc.error("Impossible to patch the expression for compatibility", IGamlIssue.UNKNOWN_UNARY,
			// desc.getUnderlyingElement(null), "");

			// }
		}
		return newExpr;
	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IGraphics g = scope.getGraphics();
		if ( g == null ) { return null; }
		try {
			return executer.executeOn(scope, g);
		} catch (GamaRuntimeException e) {
			throw e;
		} catch (Exception e) {
			java.lang.System.err.println("Error when drawing in a display : " + e.getMessage());
		}
		return null;
	}

	private abstract class DrawExecuter {

		private IExpression size;

		IExpression loc;

		IExpression bord;

		IExpression rot;

		IExpression rot3D;

		IExpression depth;

		IExpression empty;

		IExpression rounded;

		IExpression textures;

		Color constCol;
		private final Color constBord;
		private final boolean hasBord;
		private final ILocation constSize;
		private final Double constRot;
		private final GamaPair<Double, GamaPoint> constRot3D;
		private final Boolean constEmpty;
		private final Boolean constRounded;
		protected final ILocation constLoc;

		DrawExecuter(final IDescription desc) throws GamaRuntimeException {
			final IScope scope = GAMA.obtainNewScope();
			empty = getFacet(EMPTY);
			if ( empty == null ) {
				constEmpty = false;
			} else if ( empty.isConst() ) {
				constEmpty = Cast.asBool(scope, empty.value(scope));
			} else {
				constEmpty = null;
			}

			depth = getFacet(DEPTH);
			setSize(getFacet(SIZE));
			loc = getFacet(AT);
			bord = getFacet(BORDER);
			rot = getFacet(ROTATE);
			rot3D = getFacet(ROTATE3D);
			rounded = getFacet(ROUNDED);
			textures = getFacet(TEXTURE);

			constSize = getSizeExp() == null ? LOC
				: getSizeExp().isConst() ? Cast.asPoint(scope, getSizeExp().value(scope), false) : null;
			constCol = color != null && color.isConst() ? Cast.asColor(scope, color.value(scope), false) : null;
			if ( bord != null && bord.isConst() ) {
				IType type = bord.getType();
				if ( type == Types.BOOL ) {
					hasBord = Cast.asBool(scope, bord.value(scope));
					constBord = null;
				} else {
					constBord = bord != null && bord.isConst() ? Cast.asColor(scope, bord.value(scope), false) : null;
					hasBord = true;
				}
			} else if ( bord == null ) {
				hasBord = true;
				constBord = Color.black;
			} else {
				hasBord = true;
				constBord = null;
			}

			constRot = rot != null && rot.isConst() ? Cast.asFloat(scope, rot.value(scope)) : null;
			constRot3D = rot3D != null && rot3D.isConst() ? Cast.asPair(scope, rot3D.value(scope), false) : null;
			constLoc = loc != null && loc.isConst() ? Cast.asPoint(scope, loc.value(scope), false) : null;
			constRounded =
				rounded != null && rounded.isConst() ? Cast.asBool(scope, rounded.value(scope), false) : null;
			GAMA.releaseScope(scope);
		}

		Double getRotation(final IScope scope) throws GamaRuntimeException {
			return constRot == null ? rot == null ? null : Cast.asFloat(scope, rot.value(scope)) : constRot;
		}

		ILocation getSize(final IScope scope) {
			return constSize == null ? Cast.asPoint(scope, size.value(scope)) : constSize;
		}

		Color getColor(final IScope scope) {
			return constCol == null
				? color != null ? Cast.asColor(scope, color.value(scope))
					: scope.getAgentScope().getSpecies().hasVar(COLOR)
						? Cast.asColor(scope, scope.getAgentVarValue(scope.getAgentScope(), COLOR)) : Color.yellow
				: constCol;
		}

		Color getBorder(final IScope scope) {
			if ( !hasBord ) { return null; }
			if ( constBord != null ) { return constBord; }

			return bord != null ? Cast.asColor(scope, bord.value(scope))
				: scope.getAgentScope().getSpecies().hasVar(BORDER)
					? Cast.asColor(scope, scope.getAgentVarValue(scope.getAgentScope(), BORDER)) : Color.black;
		}

		Boolean getRounded(final IScope scope) {
			return constRounded == null ? rounded == null ? false : Cast.asBool(scope, rounded.value(scope))
				: constRounded;
		}

		Boolean getEmpty(final IScope scope) {
			return constEmpty == null ? empty == null ? false : Cast.asBool(scope, empty.value(scope)) : constEmpty;
		}

		ILocation getLocation(final IScope scope) {
			return constLoc == null
				? loc != null ? Cast.asPoint(scope, loc.value(scope)) : scope.getAgentScope().getLocation() : constLoc;
		}

		public IList getTextures(final IScope scope) throws GamaRuntimeException {
			if ( textures == null ) { return null; }
			Object o = textures.value(scope);
			if ( o instanceof GamaList ) { return (GamaList) o; }
			return GamaListFactory.createWithoutCasting(Types.NO_TYPE, o);
		}

		abstract Rectangle2D executeOn(IScope agent, IGraphics g) throws GamaRuntimeException;

		IExpression getSizeExp() {
			return size;
		}

		void setSize(final IExpression size) {
			this.size = size;
		}

	}

	private class ShapeExecuter extends DrawExecuter {

		final IExpression endArrow, beginArrow;

		private ShapeExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			endArrow = desc.getFacets().getExpr(END_ARROW);
			beginArrow = desc.getFacets().getExpr(BEGIN_ARROW);
		}

		ILocation getLocation(final IScope scope, final IShape shape) {
			return constLoc == null ? loc != null ? Cast.asPoint(scope, loc.value(scope)) : null : constLoc;
		}

		@Override
			Rectangle2D executeOn(final IScope scope, final IGraphics gr) throws GamaRuntimeException {
			final IShape g1 = Cast.asGeometry(scope, item.value(scope), false); // WARNING Verify no side effect
			if ( g1 == null ) { return null; }
			IShape g2 = new GamaShape(g1, null, getRotation(scope), getLocation(scope, g1));
			if ( depth != null ) {
				g2.setAttribute(IShape.DEPTH_ATTRIBUTE, depth.value(scope));
			}
			if ( rot3D != null ) {
				g2.setAttribute(IShape.ROTATE_ATTRIBUTE, rot3D.value(scope));
			}

			IList textures = getTextures(scope);
			if ( textures != null ) {
				g2.setAttribute(IShape.TEXTURE_ATTRIBUTE, textures);
			}
			Color color = getColor(scope);
			Color border = getBorder(scope);
			Boolean fill = !getEmpty(scope);

			drawArrows(scope, gr, g2, color, border, fill);
			return gr.drawGamaShape(scope, g2, color, fill, border, getRounded(scope));

		}

		/**
		 * @param g2
		 */
		private void drawArrows(final IScope scope, final IGraphics gr, final IShape g2, final Color color,
			final Color border, final Boolean fill) {

			if ( endArrow != null ) {
				IList<? extends ILocation> points = g2.getPoints();
				int size = points.size();
				if ( size < 2 ) { return; }
				double width = Cast.asFloat(scope, endArrow.value(scope));
				IShape geometry = GamaGeometryType.buildArrow(new GamaPoint(points.get(size - 2)),
					new GamaPoint(points.get(size - 1)), width, width + width / 3, fill);
				gr.drawGamaShape(scope, geometry, color, fill, border, false);
			}
			if ( beginArrow != null ) {
				IList<? extends ILocation> points = g2.getPoints();
				int size = points.size();
				if ( size < 2 ) { return; }
				double width = Cast.asFloat(scope, beginArrow.value(scope));
				IShape geometry = GamaGeometryType.buildArrow(new GamaPoint(points.get(1)),
					new GamaPoint(points.get(0)), width, width + width / 3, fill);
				gr.drawGamaShape(scope, geometry, color, fill, border, false);
			}
		}
	}

	private class ImageExecuter extends DrawExecuter {

		private final GamaImageFile constImg;
		private BufferedImage workImage;
		Graphics2D g2d = null;

		private ImageExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			constImg = (GamaImageFile) (item.isConst() ? Cast.as(item, IGamaFile.class, false) : null);
		}

		// FIXME : Penser � placer des exceptions
		// FIXME Optimiser tout �a
		@Override
			Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			// final IAgent agent = scope.getAgentScope();
			final ILocation from = getLocation(scope);
			final Double displayWidth = getSize(scope).getX();
			final GamaImageFile file = constImg == null ? (GamaImageFile) item.value(scope) : constImg;
			final BufferedImage img = file.getImage(scope);
			final int image_width = img.getWidth();
			final int image_height = img.getHeight();
			final double ratio = image_width / (double) image_height;
			final int displayHeight = Maths.round(displayWidth / ratio);
			final int x = (int) (from.getX() - displayWidth / 2);
			final int y = (int) (from.getY() - displayHeight / 2d);

			final Color c = getColor(scope);
			if ( color != null ) {
				if ( workImage == null || workImage.getWidth() != image_width ||
					workImage.getHeight() != image_height ) {
					if ( workImage != null ) {
						workImage.flush();
					}
					workImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);
					if ( g2d != null ) {
						g2d.dispose();
					}
					g2d = workImage.createGraphics();
					g2d.drawImage(img, 0, 0, null);
				} else if ( constImg == null ) {
					g2d.drawImage(img, 0, 0, null);
				}
				g2d.setPaint(c);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
				g2d.fillRect(0, 0, image_width, image_height);

				final Rectangle2D result = g.drawImage(scope, workImage, new GamaPoint(x, y, from.getZ()),
					new GamaPoint(displayWidth, displayHeight), null, getRotation(scope), false, null);
				workImage.flush();
				return result;
			}
			return g.drawImage(scope, img, new GamaPoint(x, y, from.getZ()), new GamaPoint(displayWidth, displayHeight),
				null, getRotation(scope), false, null);
		}

	}

	private class TextExecuter extends DrawExecuter {

		private final String constText;
		private final IExpression font;
		private final GamaFont constFont;
		private final IExpression style;
		private final Integer constStyle;
		private final IExpression bitmap;
		private final Boolean constBitmap;

		private TextExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			final IScope scope = GAMA.obtainNewScope();
			constText = item.isConst() ? Cast.asString(scope, item.value(scope)) : null;
			font = getFacet(FONT);
			constFont = font == null ? (GamaFont) Types.FONT.getDefault()
				: font.isConst() ? GamaFontType.staticCast(scope, font.value(scope), false) : null;
			style = getFacet(STYLE);
			constStyle = style == null ? Font.PLAIN
				: style.isConst() ? CONSTANTS.get(Cast.asString(scope, style.value(scope))) : null;
			bitmap = getFacet(BITMAP);
			constBitmap = bitmap != null && bitmap.isConst() ? Cast.asBool(scope, bitmap.value(scope)) : null;
			GAMA.releaseScope(scope);

		}

		@Override
			Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			// final IAgent agent = scope.getAgentScope();
			final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
			if ( info == null || info.length() == 0 ) { return null; }
			Font fName = constFont == null ? GamaFontType.staticCast(scope, font.value(scope), false) : constFont;
			final int fStyle = constStyle == null ? CONSTANTS.get(style.value(scope)) : constStyle;
			if ( fName.getStyle() != fStyle && style != null ) {
				fName = new GamaFont(fName.deriveFont(fStyle));
			}
			final Boolean fBitmap = constBitmap == null ? true : constBitmap;
			double fSize;
			// if ( size != null && fName.getSize() != fSize ) {
			// fName = new GamaFont(fName.deriveFont(fSize));
			// }
			if ( getSizeExp() == null ) {
				fSize = fName.getSize() * new PixelUnitExpression("pixels", null).value(scope);
			} else {
				fSize = getSize(scope).getY();
			}
			return g.drawString(info, getColor(scope), getLocation(scope), fSize, fName, getRotation(scope), fBitmap);

		}
	}

}