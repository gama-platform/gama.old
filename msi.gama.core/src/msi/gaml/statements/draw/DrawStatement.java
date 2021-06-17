/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.DrawStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import static msi.gama.common.interfaces.IKeyword.ANCHOR;
import static msi.gama.common.interfaces.IKeyword.ASPECT;
import static msi.gama.common.interfaces.IKeyword.AT;
import static msi.gama.common.interfaces.IKeyword.BORDER;
import static msi.gama.common.interfaces.IKeyword.COLOR;
import static msi.gama.common.interfaces.IKeyword.DEPTH;
import static msi.gama.common.interfaces.IKeyword.DRAW;
import static msi.gama.common.interfaces.IKeyword.EMPTY;
import static msi.gama.common.interfaces.IKeyword.FONT;
import static msi.gama.common.interfaces.IKeyword.PERSPECTIVE;
import static msi.gama.common.interfaces.IKeyword.ROTATE;
import static msi.gama.common.interfaces.IKeyword.ROUNDED;
import static msi.gama.common.interfaces.IKeyword.SIZE;
import static msi.gama.common.interfaces.IKeyword.TEXTURE;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.draw.DrawStatement.DrawValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

// A command that is used to draw shapes, figures, text on the display

@symbol (
		name = DRAW,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.DISPLAY })
@facets (
		value = {
				// Allows to pass any arbitrary geometry to the drawing command
				@facet (
						name = IKeyword.GEOMETRY,
						type = IType.NONE,
						optional = true,
						doc = @doc ("any type of data (it can be geometry, image, text)")),
				// AD 18/01/13: geometry is now accepting any type of data
				@facet (
						name = TEXTURE,
						type = { IType.STRING, IType.LIST, IType.FILE },
						optional = true,
						doc = @doc ("the texture(s) that should be applied to the geometry. Either a path to a file or a list of paths")),
				@facet (
						name = EMPTY,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								deprecated = "Use 'wireframe' instead",
								value = "a condition specifying whether the geometry is empty or full")),
				@facet (
						name = IKeyword.WIREFRAME,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a condition specifying whether to draw the geometry in wireframe or not")),
				@facet (
						name = BORDER,
						type = { IType.COLOR, IType.BOOL },
						optional = true,
						doc = @doc ("if used with a color, represents the color of the geometry border. If set to false, expresses that no border should be drawn. If not set, the borders will be drawn using the color of the geometry.")),
				@facet (
						name = ROUNDED,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "specify whether the geometry have to be rounded (e.g. for squares)",
								deprecated = "Use the squircle operator to draw rounded squares")),
				@facet (
						name = AT,
						type = IType.POINT,
						optional = true,
						doc = @doc ("location where the shape/text/icon is drawn")),
				@facet (
						name = ANCHOR,
						type = IType.POINT,
						optional = true,
						doc = @doc ("Only used when perspective: true in OpenGL. The anchor point of the location with respect to the envelope of the text to draw, can take one of the following values: #center, #top_left, #left_center, #bottom_left, #bottom_center, #bottom_right, #right_center, #top_right, #top_center; or any point between {0,0} (#bottom_left) and {1,1} (#top_right)")),
				@facet (
						name = SIZE,
						type = { IType.FLOAT, IType.POINT },
						optional = true,
						doc = @doc ("Size of the shape/icon/image to draw, expressed as a bounding box (width, height, depth; if expressed as a float, represents the box as a cube). Does not apply to texts: use a font with the required size instead")),
				@facet (
						name = COLOR,
						type = { IType.COLOR, IType.CONTAINER },
						optional = true,
						doc = @doc ("the color to use to display the object. In case of images, will try to colorize it. You can also pass a list of colors : in that case, each color will be matched to its corresponding vertex.")),
				@facet (
						name = ROTATE,
						type = { IType.FLOAT, IType.INT, IType.PAIR },
						index = IType.FLOAT,
						of = IType.POINT,
						optional = true,
						doc = @doc ("orientation of the shape/text/icon; can be either an int/float (angle) or a pair float::point (angle::rotation axis). The rotation axis, when expressed as an angle, is by defaut {0,0,1}")),
				@facet (
						name = FONT,
						type = { IType.FONT, IType.STRING },
						optional = true,
						doc = @doc ("the font used to draw the text, if any. Applying this facet to geometries or images has no effect. You can construct here your font with the operator \"font\". ex : font:font(\"Helvetica\", 20 , #plain)")),
				@facet (
						name = DEPTH,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("(only if the display type is opengl) Add an artificial depth to the geometry previously defined (a line becomes a plan, a circle becomes a cylinder, a square becomes a cube, a polygon becomes a polyhedron with height equal to the depth value). Note: This only works if the geometry is not a point ")),

				@facet (
						name = "precision",
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("(only if the display type is opengl and only for text drawing) controls the accuracy with which curves are rendered in glyphs. Between 0 and 1, the default is 0.1. "
								+ "Smaller values will output much more faithful curves but can be considerably slower, "
								+ "so it is better if they concern text that does not change and can be drawn inside layers marked as 'refresh: false'")),
				@facet (
						name = DrawStatement.BEGIN_ARROW,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("the size of the arrow, located at the beginning of the drawn geometry")),
				@facet (
						name = DrawStatement.END_ARROW,
						type = { IType.INT, IType.FLOAT },
						optional = true,
						doc = @doc ("the size of the arrow, located at the end of the drawn geometry")),
				@facet (
						name = IKeyword.LIGHTED,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "Whether the object should be lighted or not (only applicable in the context of opengl displays)")),
				@facet (
						name = PERSPECTIVE,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "Whether to render the text in perspective or facing the user. Default is true.")),
				@facet (
						name = IKeyword.MATERIAL,
						type = IType.MATERIAL,
						optional = true,
						doc = @doc (
								value = "Set a particular material to the object (only if you use it in an \"opengl2\" display).")),
				@facet (
						name = IKeyword.WIDTH,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								value = "The line width to use for drawing this object")),
				@facet (
						name = "bitmap",
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								deprecated = "use 'perspective' instead.",
								value = "Whether to render the text in 3D or not")) },

		omissible = IKeyword.GEOMETRY)
@inside (
		symbols = { ASPECT },
		kinds = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@doc (
		value = "`" + DRAW
				+ "` is used in an aspect block to express how agents of the species will be drawn. It is evaluated each time the agent has to be drawn. It can also be used in the graphics block.",
		usages = { @usage (
				value = "Any kind of geometry as any location can be drawn when displaying an agent (independently of his shape)",
				examples = { @example (
						value = "aspect geometryAspect {",
						isExecutable = false),
						@example (
								value = "	draw circle(1.0) empty: !hasFood color: #orange ;",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "Image or text can also be drawn",
						examples = { @example (
								value = "aspect arrowAspect {",
								isExecutable = false),
								@example (
										value = "	draw \"Current state= \"+state at: location + {-3,1.5} color: #white font: font('Default', 12, #bold) ;",
										isExecutable = false),
								@example (
										value = "	draw file(ant_shape_full) rotate: heading at: location size: 5",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "Arrows can be drawn with any kind of geometry, using " + DrawStatement.BEGIN_ARROW
								+ " and " + DrawStatement.END_ARROW
								+ " facets, combined with the empty: facet to specify whether it is plain or empty",
						examples = { @example (
								value = "aspect arrowAspect {",
								isExecutable = false),
								@example (
										value = "	draw line([{20, 20}, {40, 40}]) color: #black begin_arrow:5;",
										isExecutable = false),
								@example (
										value = "	draw line([{10, 10},{20, 50}, {40, 70}]) color: #green end_arrow: 2 begin_arrow: 2 empty: true;",
										isExecutable = false),
								@example (
										value = "	draw square(10) at: {80,20} color: #purple begin_arrow: 2 empty: true;",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) })
@validator (DrawValidator.class)
public class DrawStatement extends AbstractStatementSequence {

	public static class DrawValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {
			final IExpressionDescription empty = description.getFacet(IKeyword.EMPTY);
			if (empty != null) {
				description.setFacet(IKeyword.WIREFRAME, empty);
				description.removeFacets(EMPTY);
			}
			final IExpressionDescription persp = description.getFacet("bitmap");
			if (persp != null) {
				if (description.getFacet(PERSPECTIVE) != null) {
					description.removeFacets("bitmap");
				} else {
					final IExpression e = persp.getExpression();
					final IExpression newExp =
							GAML.getExpressionFactory().createOperator("not", description, persp.getTarget(), e);
					description.setFacet(PERSPECTIVE, newExp);
				}
			}

			final IExpressionDescription geom = description.getFacet(GEOMETRY);
			if (geom != null) {
				for (final String s : Arrays.asList(TEXT, SHAPE, IMAGE)) {
					final IExpressionDescription other = description.getFacet(s);
					if (other != null) {
						description.warning("'" + s + "' has no effect here", IGamlIssue.CONFLICTING_FACETS, s);
					}
				}
				final IExpression exp = geom.getExpression();
				final IType<?> type = exp == null ? Types.NO_TYPE : exp.getGamlType();
				if (exp == null || !canDraw(exp)) {
					description.error("'draw' cannot draw objects of type " + type, IGamlIssue.WRONG_TYPE, GEOMETRY);
					return;
				}
				if (type.equals(Types.STRING)) {
					final IExpressionDescription rot = description.getFacet(ROTATE);

					if (rot != null) {
						final IExpressionDescription per = description.getFacet(PERSPECTIVE);
						if (per != null) {
							if (per.isConst() && per.equalsString(FALSE)) {
								description.warning("Rotations cannot be applied when perspective is false",
										IGamlIssue.CONFLICTING_FACETS, ROTATE);
							}
						}
					}
				}

			}

		}

		private boolean canDraw(final IExpression exp) {
			IType<?> type = exp.getGamlType();
			if (type.isDrawable()) return true;
			// In case we have a generic file operator, for instance
			type = type.typeIfCasting(exp);
			return type.isDrawable();
		}

	}

	public static final String END_ARROW = "end_arrow";
	public static final String BEGIN_ARROW = "begin_arrow";

	private final DrawExecuter executer;

	private final ThreadLocal<DrawingData> data;

	public DrawStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		final IExpression item = getFacet(IKeyword.GEOMETRY);
		data = ThreadLocal.withInitial(() -> new DrawingData(this));
		if (item == null) {
			executer = null;
		} else {
			if (item.getGamlType().getGamlType().id() == IType.FILE) {
				executer = new FileExecuter(item);
			} else if (item.getGamlType().id() == IType.STRING) {
				executer = new TextExecuter(item);
			} else {
				// item is supposed to be castable into a geometry
				executer = new ShapeExecuter(item, getFacet(BEGIN_ARROW), getFacet(END_ARROW));
			}
		}
	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (executer == null) return null;
		final IGraphics g = scope.getGraphics();
		if (g == null) return null;
		try {
			final DrawingData d = data.get();
			d.refresh(scope);
			final Rectangle2D result = executer.executeOn(scope, g, d);
			if (result != null) { g.accumulateTemporaryEnvelope(result); }
			return result;
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			java.lang.System.err.println("Error when drawing in a display : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}