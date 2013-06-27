/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import static msi.gama.common.interfaces.IKeyword.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.file.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.*;
import msi.gaml.types.*;

// A command that is used to draw shapes, figures, text on the display

@symbol(name = DRAW, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@facets(value = {
	// Allows to pass any arbitrary geometry to the drawing command
	@facet(name = IKeyword.GEOMETRY, type = IType.NONE, optional = true),
	// AD 18/01/13: geometry is now accepting an y type of data
	@facet(name = SHAPE, type = IType.NONE, optional = true),
	@facet(name = TEXT, type = IType.STRING, optional = true),
	@facet(name = IMAGE, type = IType.STRING, optional = true),
	@facet(name = EMPTY, type = IType.BOOL, optional = true),
	@facet(name = BORDER, type = IType.COLOR, optional = true),
	@facet(name = ROUNDED, type = IType.BOOL, optional = true), @facet(name = AT, type = IType.POINT, optional = true),
	@facet(name = SIZE, type = IType.FLOAT, optional = true), @facet(name = TO, type = IType.POINT, optional = true),
	@facet(name = COLOR, type = IType.COLOR, optional = true),
	@facet(name = SCALE, type = IType.FLOAT, optional = true),
	@facet(name = ROTATE, type = IType.INT, optional = true),
	@facet(name = FONT, type = IType.STRING, optional = true),
	@facet(name = DEPTH, type = IType.FLOAT, optional = true),
	@facet(name = STYLE, type = IType.ID, values = { "plain", "bold", "italic" }, optional = true) },

combinations = { @combination({ IKeyword.GEOMETRY, EMPTY, BORDER, ROUNDED, COLOR, DEPTH }),
	@combination({ SHAPE, COLOR, SIZE, AT, EMPTY, BORDER, ROUNDED, ROTATE, DEPTH }),
	@combination({ TO, SHAPE, COLOR, SIZE, EMPTY, BORDER, ROUNDED }),
	@combination({ SHAPE, COLOR, SIZE, EMPTY, BORDER, ROUNDED, ROTATE }),
	@combination({ TEXT, SIZE, COLOR, AT, ROTATE }), @combination({ IMAGE, SIZE, AT, SCALE, ROTATE, COLOR }) }, omissible = IKeyword.GEOMETRY)
@inside(symbols = { ASPECT }, kinds = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
public class DrawStatement extends AbstractStatementSequence {

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
		return GAML.getExpressionFactory().createVar(SHAPE, Types.get(IType.GEOMETRY), Types.get(IType.NONE),
			Types.get(IType.STRING), false, IVarExpression.AGENT, desc);
	}

	public DrawStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		item = getFacet(IKeyword.GEOMETRY, getFacet(SHAPE, getFacet(IMAGE, getFacet(TEXT))));
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
	 *            expressions)
	 * @return the new expression, patched for compatibility
	 */
	private IExpression patchForCompatibility(final IExpression exp, final IDescription desc) {
		IExpression newExpr = null;
		if ( exp.getType().id() == IType.STRING && exp.isConst() ) {
			String old = Cast.asString(null, exp.value(null));
			if ( old.contains("deprecated") ) {
				old = old.split("__")[0];
				if ( old.equals("disc") || old.equals("circle") ) {
					IExpression sizeExp = getFacet(SIZE);
					if ( sizeExp == null ) {
						sizeExp = GAML.getExpressionFactory().createConst(1, Types.get(IType.INT));
					}
					newExpr = GAML.getExpressionFactory().createOperator("circle", desc, sizeExp);
				} else if ( old.equals("rectangle") || old.equals("square") ) {
					IExpression sizeExp = getFacet(SIZE);
					if ( sizeExp == null ) {
						sizeExp = GAML.getExpressionFactory().createConst(1, Types.get(IType.INT));
					}

					newExpr = GAML.getExpressionFactory().createOperator("square", desc, sizeExp);
				} else if ( old.equals("geometry") ) {
					newExpr = getShapeExpression(desc);
				} else if ( old.equals("line") ) {
					IExpression at = getFacet(AT);
					final IExpression to = getFacet(TO);
					if ( at == null ) {
						at =
							GAML.getExpressionFactory().createVar("location", Types.get(IType.POINT),
								Types.get(IType.FLOAT), Types.get(IType.INT), false, IVarExpression.AGENT, desc);
					}
					final List<IExpression> elements = new ArrayList();
					elements.add(at);
					elements.add(to);
					final IExpression list = GAML.getExpressionFactory().createList(elements);
					newExpr = GAML.getExpressionFactory().createOperator("line", desc, list);
				}
			} else {
				if ( GamaFileType.isImageFile(old) ) {
					newExpr = GAML.getExpressionFactory().createOperator("file", desc, exp);
				}
			}
			if ( newExpr != null ) {
				desc.getFacets().put(IKeyword.GEOMETRY, exp);
			} else {
				// If no operator has been found, we throw an exception
				desc.error("Impossible to patch the expression for compatibility", IGamlIssue.UNKNOWN_UNARY,
					desc.getUnderlyingElement(null), "");

			}
		}
		return exp;
	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		final IGraphics g = stack.getGraphics();
		if ( g == null ) { return null; }
		return executer.executeOn(stack, g);
	}

	private abstract class DrawExecuter {

		IExpression size, loc, bord, rot, depth, empty, rounded;

		Color constCol;
		private final Color constBord;
		private final ILocation constSize;
		private final Integer constRot;
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
			size = getFacet(SIZE);
			loc = getFacet(AT);
			bord = getFacet(BORDER);
			rot = getFacet(ROTATE);
			rounded = getFacet(ROUNDED);

			constSize = size == null ? LOC : size.isConst() ? Cast.asPoint(scope, size.value(scope)) : null;
			constCol = color != null && color.isConst() ? Cast.asColor(scope, color.value(scope)) : null;
			constBord = bord != null && bord.isConst() ? Cast.asColor(scope, bord.value(scope)) : null;
			constRot = rot != null && rot.isConst() ? Cast.asInt(scope, rot.value(scope)) : null;
			constLoc = loc != null && loc.isConst() ? Cast.asPoint(scope, loc.value(scope)) : null;
			constRounded = rounded != null && rounded.isConst() ? Cast.asBool(scope, rounded.value(scope)) : null;
			GAMA.releaseScope(scope);
		}

		Integer getRotation(final IScope scope) throws GamaRuntimeException {
			return constRot == null ? rot == null ? null : Cast.asInt(scope, rot.value(scope)) : constRot;
		}

		ILocation getSize(final IScope scope) {
			return constSize == null ? Cast.asPoint(scope, size.value(scope)) : constSize;
		}

		Color getColor(final IScope scope) {
			return constCol == null ? color != null ? Cast.asColor(scope, color.value(scope)) : scope.getAgentScope()
				.getSpecies().hasVar(COLOR) ? Cast.asColor(scope, scope.getAgentVarValue(scope.getAgentScope(), COLOR))
				: Color.yellow : constCol;
		}

		Color getBorder(final IScope scope) {
			return constBord == null ? bord != null ? Cast.asColor(scope, bord.value(scope)) : scope.getAgentScope()
				.getSpecies().hasVar(BORDER) ? Cast.asColor(scope,
				scope.getAgentVarValue(scope.getAgentScope(), BORDER)) : Color.black : constBord;
		}

		Boolean getRounded(final IScope scope) {
			return constRounded == null ? rounded == null ? false : Cast.asBool(scope, rounded.value(scope))
				: constRounded;
		}

		Boolean getEmpty(final IScope scope) {
			return constEmpty == null ? empty == null ? false : Cast.asBool(scope, empty.value(scope)) : constEmpty;
		}

		ILocation getLocation(final IScope scope) {
			return constLoc == null ? loc != null ? Cast.asPoint(scope, loc.value(scope)) : scope.getAgentScope()
				.getLocation() : constLoc;
		}

		abstract Rectangle2D executeOn(IScope agent, IGraphics g) throws GamaRuntimeException;

	}

	private class ShapeExecuter extends DrawExecuter {

		private ShapeExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
		}

		ILocation getLocation(final IScope scope, final IShape shape) {
			return constLoc == null ? loc != null ? Cast.asPoint(scope, loc.value(scope)) : shape.getLocation()
				: constLoc;
		}

		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics gr) throws GamaRuntimeException {
			final IShape g1 = Cast.asGeometry(scope, item.value(scope));
			if ( g1 == null ) {
				// GuiUtils.debug("DrawStatement.ShapeExecuter.executeOn : null shape");
				return null;
			}
			final IShape g2 = Spatial.Transformations.at_location(scope, g1, getLocation(scope, g1));
			if ( depth != null ) {
				g2.setAttribute("depth", depth.value(scope));
			}
			return gr.drawGamaShape(scope, g2, getColor(scope), !getEmpty(scope), getBorder(scope), getRotation(scope),
				getRounded(scope));

		}
	}

	private class ImageExecuter extends DrawExecuter {

		private final GamaImageFile constImg;
		private BufferedImage workImage;
		Graphics2D g2d = null;

		private ImageExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			constImg = (GamaImageFile) (item.isConst() ? Cast.as(item, IGamaFile.class) : null);
		}

		// FIXME : Penser à placer des exceptions
		// FIXME Optimiser tout ça
		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			final IAgent agent = scope.getAgentScope();
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
				if ( workImage == null || workImage.getWidth() != image_width || workImage.getHeight() != image_height ) {
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

				final Rectangle2D result =
					g.drawImage(scope, workImage, new GamaPoint(x, y), new GamaPoint(displayWidth, displayHeight),
						null, getRotation(scope), agent.getLocation().getZ(), false);
				workImage.flush();
				return result;
			}
			return g.drawImage(scope, img, new GamaPoint(x, y), new GamaPoint(displayWidth, displayHeight), null,
				getRotation(scope), agent.getLocation().getZ(), false);
		}

	}

	private class TextExecuter extends DrawExecuter {

		private final String constText;
		private final IExpression font;
		private final String constFont;
		private final IExpression style;
		private final Integer constStyle;

		private TextExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			final IScope scope = GAMA.obtainNewScope();
			constText = item.isConst() ? Cast.asString(scope, item.value(scope)) : null;
			font = getFacet(FONT);
			constFont =
				font == null ? Font.SANS_SERIF : font.isConst() ? Cast.asString(scope, font.value(scope)) : null;
			style = getFacet(STYLE);
			constStyle =
				style == null ? Font.PLAIN : style.isConst() ? CONSTANTS.get(Cast.asString(scope, style.value(scope)))
					: null;
			GAMA.releaseScope(scope);

		}

		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			final IAgent agent = scope.getAgentScope();
			final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
			if ( info == null || info.length() == 0 ) { return null; }
			final String fName = constFont == null ? Cast.asString(scope, font.value(scope)) : constFont;
			final int fStyle = constStyle == null ? CONSTANTS.get(style.value(scope)) : constStyle;

			return g.drawString(info, getColor(scope), getLocation(scope), getSize(scope).getY(), fName, fStyle,
				getRotation(scope), agent.getLocation().getZ());

		}
	}

}