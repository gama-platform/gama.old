/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import msi.gama.gui.graphics.IGraphics;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.operators.Maths;

// A command that is used to draw shapes, figures, text on the display

@symbol(name = ISymbol.DRAW, kind = ISymbolKind.SINGLE_COMMAND)
@facets(value = {
	@facet(name = ISymbol.SHAPE, type = IType.ID, optional = true),
	@facet(name = ISymbol.TEXT, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.IMAGE, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.EMPTY, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.AT, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.SIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.TO, type = IType.POINT_STR, optional = true),
	@facet(name = ISymbol.COLOR, type = IType.COLOR_STR, optional = true),
	@facet(name = ISymbol.SCALE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.ROTATE, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.FONT, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.STYLE, type = IType.ID, values = { "plain", "bold", "italic" }, optional = true) },

combinations = {
	@combination({ ISymbol.SHAPE, ISymbol.COLOR, ISymbol.SIZE, ISymbol.AT, ISymbol.EMPTY,
		ISymbol.ROTATE }),
	@combination({ ISymbol.TO, ISymbol.SHAPE, ISymbol.COLOR, ISymbol.SIZE, ISymbol.EMPTY }),
	@combination({ ISymbol.SHAPE, ISymbol.COLOR, ISymbol.SIZE, ISymbol.EMPTY, ISymbol.ROTATE }),
	@combination({ ISymbol.TEXT, ISymbol.SIZE, ISymbol.COLOR, ISymbol.AT, ISymbol.ROTATE }),
	@combination({ ISymbol.IMAGE, ISymbol.SIZE, ISymbol.AT, ISymbol.SCALE, ISymbol.ROTATE,
		ISymbol.COLOR }) })
@inside(symbols = { ISymbol.ASPECT }, kinds = { ISymbolKind.SEQUENCE_COMMAND })
public class DrawCommand extends AbstractCommandSequence {

	private static ImageCache cachedImages = new ImageCache();
	private static final Map<String, Integer> STYLES = new HashMap();
	private static final Map<String, Integer> SHAPES = new HashMap();
	static {
		STYLES.put("plain", 0);
		STYLES.put("bold", 1);
		STYLES.put("italic", 2);
		SHAPES.put("geometry", 0);
		SHAPES.put("square", 1);
		SHAPES.put("circle", 2);
		SHAPES.put("triangle", 3);
		SHAPES.put("rectangle", 1);
		SHAPES.put("disc", 2);
		SHAPES.put("line", 4);
	}

	private final DrawExecuter executer;

	public DrawCommand(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		final boolean isShape = getFacet(ISymbol.SHAPE) != null;
		final boolean isText = getFacet(ISymbol.TEXT) != null;
		final boolean isImage = getFacet(ISymbol.IMAGE) != null;
		executer =
			isShape ? new ShapeExecuter(desc) : isText ? new TextExecuter(desc) : isImage
				? new ImageExecuter(desc) : new ShapeExecuter(desc);

	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		IGraphics g = (IGraphics) stack.getContext();
		if ( g == null ) { return null; }
		return executer.executeOn(stack, g);
	}

	private abstract class DrawExecuter {

		IExpression size, location, color, rotate;

		Color constantColor = null;
		GamaPoint constantSize = null;
		Integer constantRotate = null;

		DrawExecuter(final IDescription desc) throws GamaRuntimeException {
			size = getFacet(ISymbol.SIZE);
			if ( size == null ) {
				constantSize = new GamaPoint(1.0, 1.0);
			} else if ( size.isConst() ) {
				constantSize = Cast.asPoint(size.value(GAMA.getDefaultScope()));
			}
			location = getFacet(ISymbol.AT);
			color = getFacet(COLOR);
			if ( color != null && color.isConst() ) {
				constantColor = Cast.asColor(color.value(GAMA.getDefaultScope()));
			}
			rotate = getFacet(ISymbol.ROTATE);
			if ( rotate != null && rotate.isConst() ) {
				constantRotate = Cast.asInt(rotate.value(GAMA.getDefaultScope()));
			}
		}

		double scale(final double val, final IGraphics g) {
			return val * g.getXScale();
		}

		GamaPoint scale(final GamaPoint p, final IGraphics g) {
			return new GamaPoint(p.x * g.getXScale(), p.y * g.getYScale());
		}

		Integer getRotation(final IScope scope) throws GamaRuntimeException {
			return constantRotate == null ? rotate == null ? null : Cast.asInt(scope,
				rotate.value(scope)) : constantRotate;
		}

		abstract Rectangle2D executeOn(IScope agent, IGraphics g) throws GamaRuntimeException;

	}

	private class ShapeExecuter extends DrawExecuter {

		private final IExpression shape, empty, toExpr;

		private final Boolean constantEmpty;
		private final Integer constantShape;

		private ShapeExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			shape = getFacet(ISymbol.SHAPE);
			if ( shape == null ) {
				constantShape = SHAPES.get("geometry");
			} else if ( shape.isConst() ) {
				constantShape = SHAPES.get(shape.literalValue());
			} else {
				constantShape = null;
			}

			empty = getFacet(ISymbol.EMPTY);
			if ( empty == null ) {
				constantEmpty = false;
			} else if ( empty.isConst() ) {
				constantEmpty = Cast.asBool(empty.value(GAMA.getDefaultScope()));
			} else {
				constantEmpty = null;
			}
			toExpr = getFacet(ISymbol.TO);

		}

		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			final IAgent agent = scope.getAgentScope();

			GamaPoint loc;
			if ( location == null ) {
				loc = agent.getLocation();
			} else {
				loc = Cast.asPoint(scope, location.value(scope));
				loc = agent.getLocation();
			}

			if ( loc == null ) {
				scope.setStatus(ExecutionStatus.skipped);
				return null;
			}
			final GamaPoint from = scale(loc, g);
			Integer shapeIndex =
				constantShape == null ? SHAPES.get(Cast.asString(shape.value(scope)))
					: constantShape;
			if ( shapeIndex == null ) {
				shapeIndex = 0;
			}
			Color c;
			if ( constantColor == null ) {
				if ( color != null ) {
					c = Cast.asColor(scope, color.value(scope));
				} else {
					Object o = scope.getAgentVarValue(agent, ISymbol.COLOR);
					if ( o != null ) {
						c = Cast.asColor(o);
					} else {
						c = Color.yellow;
					}
				}
			} else {
				c = constantColor;
			}

			int displaySize =
				constantSize == null ? Maths
					.round(scale(Cast.asFloat(scope, size.value(scope)), g)) : Maths.round(scale(
					constantSize.x, g));
			if ( shapeIndex == 4 ) { // line
				if ( toExpr == null ) {
					scope.setStatus(ExecutionStatus.skipped);
					return null;
				}

				GamaPoint target = Cast.asPoint(scope, toExpr.value(scope));
				target = scale(agent.getLocation(), g);
				return drawLine(from, target, displaySize, c, g);

			}
			boolean isEmpty =
				constantEmpty == null ? empty == null ? false : Cast.asBool(scope,
					empty.value(scope)) : constantEmpty;
			return draw(scope, shapeIndex, from, displaySize, c, isEmpty, g);
		}

		private Rectangle2D draw(final IScope scope, final Integer shapeIndex, final GamaPoint at,
			final int displaySize, final Color c, final boolean isEmpty, final IGraphics g)
			throws GamaRuntimeException {
			int x = Maths.round(at.x) - (displaySize >> 1);
			int y = Maths.round(at.y) - (displaySize >> 1);
			g.setDrawingCoordinates(x, y);
			g.setDrawingDimensions(displaySize, displaySize);
			switch (shapeIndex) {
				case 0: {
					return g.drawGeometry(scope.getAgentScope().getInnerGeometry(), c, !isEmpty,
						getRotation(scope));
				}
				case 1: {
					return g.drawRectangle(c, !isEmpty, getRotation(scope));
				}
				case 2: {
					return g.drawCircle(c, !isEmpty, null);
				}
				case 3: {
					return g.drawTriangle(c, !isEmpty, getRotation(scope));
				}
			}
			return null;
		}

		private Rectangle2D drawLine(final GamaPoint from, final GamaPoint target,
			final int displaySize, final Color c, final IGraphics g) {
			int x = Maths.round(from.x);
			int y = Maths.round(from.y);
			g.setDrawingCoordinates(x, y);
			int toX = Maths.round(target.x);
			int toY = Maths.round(target.y);
			// TODO Size ??
			return g.drawLine(c, toX, toY);
		}
	}

	private class ImageExecuter extends DrawExecuter {

		private final IExpression image;
		private final String constantImage;
		private BufferedImage workImage;
		Graphics2D g2d = null;

		private ImageExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			image = getFacet(ISymbol.IMAGE);
			constantImage =
				image.isConst() ? Cast.asString(image.value(GAMA.getDefaultScope())) : null;
		}

		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			IAgent agent = scope.getAgentScope();
			GamaPoint from = null;
			if ( location == null ) {
				from = scale(agent.getLocation(), g);
			} else {
				from = scale(Cast.asPoint(scope, location.value(scope)), g);
			}
			int displayWidth =
				constantSize == null ? Maths
					.round(scale(Cast.asFloat(scope, size.value(scope)), g)) : Maths.round(scale(
					constantSize.x, g));
			String img = constantImage == null ? Cast.asString(image.value(scope)) : constantImage;
			if ( !cachedImages.contains(img) ) {
				loadImage(scope, img);
			}
			BufferedImage image = cachedImages.get(img);
			int image_width = image.getWidth();
			int image_height = image.getHeight();
			double ratio = image_width / (double) image_height;
			int displayHeight = Maths.round(displayWidth / ratio);
			int x = Maths.round(from.x) - displayWidth / 2;
			int y = Maths.round(from.y) - displayHeight / 2;
			g.setDrawingDimensions(displayWidth, displayHeight);
			g.setDrawingCoordinates(x, y);
			Color c = null;
			Integer angle =
				constantRotate == null ? rotate == null ? null : Cast.asInt(scope,
					rotate.value(scope)) : constantRotate;
			if ( color != null ) {
				if ( constantColor == null ) {
					c = Cast.asColor(scope, color.value(scope));
				} else {
					c = constantColor;
				}
				if ( workImage == null || workImage.getWidth() != image.getWidth() ||
					workImage.getHeight() != image.getHeight() ) {
					if ( workImage != null ) {
						workImage.flush();
					}
					workImage =
						new BufferedImage(image.getWidth(), image.getHeight(),
							BufferedImage.TYPE_INT_ARGB);
					if ( g2d != null ) {
						g2d.dispose();
					}
					g2d = workImage.createGraphics();
					if ( constantImage != null ) {
						g2d.drawImage(image, 0, 0, null);
					}
				}
				if ( constantImage == null ) {
					g2d.drawImage(image, 0, 0, null);
				}
				g2d.setPaint(c);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
				// Pourquoi l'alpha ne fonctionne pas ??
				g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
				// g2d.dispose();

				Rectangle2D result = g.drawImage(workImage, angle);
				workImage.flush();
				return result;
			}
			return g.drawImage(image, angle);
		}

		private void loadImage(final IScope scope, final String s) {
			BufferedImage img = null;
			try {
				img =
					ImageIO.read(new File(scope.getSimulationScope().getModel()
						.getRelativeFilePath(s, true)));
				if ( img != null ) {
					cachedImages.add(s, img);
				}

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class TextExecuter extends DrawExecuter {

		private final IExpression text;
		private final String constantText;
		private final IExpression font;
		private final String constantFont;
		private final IExpression style;
		private final Integer constantStyle;

		private TextExecuter(final IDescription desc) throws GamaRuntimeException {
			super(desc);
			text = getFacet(ISymbol.TEXT);
			if ( text.isConst() ) {
				constantText = Cast.asString(text.value(GAMA.getDefaultScope()));
			} else {
				constantText = null;
			}
			font = getFacet(FONT);
			if ( font == null ) {
				constantFont = Font.SANS_SERIF;
			} else if ( font.isConst() ) {
				constantFont = Cast.asString(font.value(GAMA.getDefaultScope()));
			} else {
				constantFont = null;
			}
			style = getFacet(ISymbol.STYLE);
			if ( style == null ) {
				constantStyle = Font.PLAIN;
			} else if ( style.isConst() ) {
				constantStyle = STYLES.get(Cast.asString(style.value(GAMA.getDefaultScope())));
			} else {
				constantStyle = null;
			}

		}

		@Override
		Rectangle2D executeOn(final IScope scope, final IGraphics g) throws GamaRuntimeException {
			IAgent agent = scope.getAgentScope();
			GamaPoint from = null;
			if ( location == null ) {
				from = scale(agent.getLocation(), g);
			} else {
				from = scale(Cast.asPoint(scope, location.value(scope)), g);
			}
			int displaySize =
				constantSize == null ? Maths
					.round(scale(Cast.asFloat(scope, size.value(scope)), g)) : Maths.round(scale(
					constantSize.x, g));
			String info = constantText == null ? Cast.asString(text.value(scope)) : constantText;
			if ( info == null || info.length() == 0 ) {
				scope.setStatus(ExecutionStatus.skipped);
				return null;
			}
			int x = Maths.round(from.x);
			int y = Maths.round(from.y);
			Color c =
				constantColor == null ? color != null ? Cast.asColor(scope, color.value(scope))
					: (Color) agent.getAttribute("color") : constantColor;
			Integer angle =
				constantRotate == null ? rotate == null ? null : Cast.asInt(scope,
					rotate.value(scope)) : constantRotate;
			String fName = constantFont == null ? Cast.asString(font.value(scope)) : constantFont;
			int fStyle = constantStyle == null ? STYLES.get(style.value(scope)) : constantStyle;
			Font f = new Font(fName, fStyle, displaySize);
			g.setFont(f);
			g.setDrawingCoordinates(x, y);
			return g.drawString(info, c, angle); // ??
		}
	}

}