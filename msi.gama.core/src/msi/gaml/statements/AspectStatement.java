/*******************************************************************************************************
 *
 * msi.gaml.statements.AspectStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.ShapeDrawingAttributes;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;

@symbol (
		name = { IKeyword.ASPECT },
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		unique_name = true,
		concept = { IConcept.DISPLAY })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = true,
				doc = @doc ("identifier of the aspect (it can be used in a display to identify which aspect should be used for the given species). Two special names can also be used: 'default' will allow this aspect to be used as a replacement for the default aspect defined in preferences; 'highlighted' will allow the aspect to be used when the agent is highlighted as a replacement for the default (application of a color)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "Aspect statement is used to define a way to draw the current agent. Several aspects can be defined in one species. It can use attributes to customize each agent's aspect. The aspect is evaluate for each agent each time it has to be displayed.",
		usages = { @usage (
				value = "An example of use of the aspect statement:",
				examples = { @example (
						value = "species one_species {",
						isExecutable = false),
						@example (
								value = "	int a <- rnd(10);",
								isExecutable = false),
						@example (
								value = "	aspect aspect1 {",
								isExecutable = false),
						@example (
								value = "		if(a mod 2 = 0) { draw circle(a);}",
								isExecutable = false),
						@example (
								value = "		else {draw square(a);}",
								isExecutable = false),
						@example (
								value = "		draw text: \"a= \" + a color: #black size: 5;",
								isExecutable = false),
						@example (
								value = "	}",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) })
public class AspectStatement extends AbstractStatementSequence {

	boolean isHighlightAspect;

	static final Map<String, Integer> SHAPES = new HashMap<String, Integer>() {

		{
			put("circle", 1);
			put("square", 2);
			put("triangle", 3);
			put("sphere", 4);
			put("cube", 5);
			put("point", 6);
		}
	};

	public static GamaColor borderColor = GamaColor.getInt(Color.black.getRGB());
	public static IExecutable DEFAULT_ASPECT = scope -> {
		final IAgent agent = scope.getAgent();
		if (agent != null && !agent.dead()) {
			final IGraphics g = scope.getGraphics();
			if (g == null) { return null; }
			try {
				if (agent == scope.getGui().getHighlightedAgent()) {
					g.beginHighlight();
				}
				final boolean hasColor = agent.getSpecies().hasVar(IKeyword.COLOR);
				GamaColor color;
				if (hasColor) {
					final Object value = agent.getDirectVarValue(scope, IKeyword.COLOR);
					color = Cast.asColor(scope, value);
				} else {
					color = GamaColor.getInt(GamaPreferences.Displays.CORE_COLOR.getValue().getRGB());
				}
				final String defaultShape = GamaPreferences.Displays.CORE_SHAPE.getValue();
				final Integer index = SHAPES.get(defaultShape);
				IShape ag;

				if (index != null) {
					final Double defaultSize = GamaPreferences.Displays.CORE_SIZE.getValue();
					final ILocation point = agent.getLocation();

					switch (SHAPES.get(defaultShape)) {
						case 1:
							ag = GamaGeometryType.buildCircle(defaultSize, point);
							break;
						case 2:
							ag = GamaGeometryType.buildSquare(defaultSize, point);
							break;
						case 3:
							ag = GamaGeometryType.buildTriangle(defaultSize, point);
							break;
						case 4:
							ag = GamaGeometryType.buildSphere(defaultSize, point);
							break;
						case 5:
							ag = GamaGeometryType.buildCube(defaultSize, point);
							break;
						case 6:
							ag = GamaGeometryType.createPoint(point);
							break;
						default:
							ag = agent.getGeometry();
					}
				} else {
					ag = agent.getGeometry();
				}

				final IShape ag2 = ag.copy(scope);
				final DrawingAttributes attributes = new ShapeDrawingAttributes(ag2, agent, color, borderColor);
				return g.drawShape(ag2.getInnerGeometry(), attributes);
			} catch (final GamaRuntimeException e) {
				// cf. Issue 1052: exceptions are not thrown, just displayed
				e.printStackTrace();
			} finally {
				g.endHighlight();
			}
		}
		return null;
	};

	public AspectStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME, IKeyword.DEFAULT));
		isHighlightAspect = getName().equals("highlighted");
	}

	@Override
	public Rectangle2D executeOn(final IScope scope) {
		final IAgent agent = scope.getAgent();
		final boolean shouldHighlight = agent == scope.getGui().getHighlightedAgent() && !isHighlightAspect;
		if (agent != null && !agent.dead()) {
			IGraphics g = scope.getGraphics();
			// hqnghi: try to find scope from experiment
			if (g == null) {
				g = GAMA.getExperiment().getAgent().getSimulation().getScope().getGraphics();
			}
			// end-hqnghi
			if (g == null) { return null; }
			try {
				if (scope.interrupted()) { return null; }
				if (shouldHighlight) {
					g.beginHighlight();
				}
				return (Rectangle2D) super.executeOn(scope);
			} catch (final GamaRuntimeException e) {
				// cf. Issue 1052: exceptions are not thrown, just displayed
				e.printStackTrace();
			} finally {
				if (shouldHighlight) {
					g.endHighlight();
				}
				// agent.releaseLock();
			}

		}
		return null;

	}

	@Override
	public Rectangle2D privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IGraphics g = scope.getGraphics();
		if (g == null) { return null; }
		super.privateExecuteIn(scope);
		return g.getAndWipeTemporaryEnvelope();
	}
}
