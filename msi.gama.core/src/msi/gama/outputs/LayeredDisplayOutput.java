/*********************************************************************************************
 *
 *
 * 'LayeredDisplayOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGamaView.Display;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.LayeredDisplayOutput.DisplaySerializer;
import msi.gama.outputs.LayeredDisplayOutput.InfoValidator;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.OverlayStatement;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class LayerDisplayOutput.
 *
 * @author drogoul
 */
@symbol(name = { IKeyword.DISPLAY }, kind = ISymbolKind.OUTPUT, with_sequence = true, concept = { IConcept.DISPLAY })
@facets(value = {
		@facet(name = IKeyword.BACKGROUND, type = IType.COLOR, optional = true, doc = @doc("Allows to fill the background of the display with a specific color")),
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the display")),
		@facet(name = IKeyword.FOCUS, type = IType.GEOMETRY, optional = true, doc = @doc("the geometry (or agent) on which the display will (dynamically) focus")),
		// WARNING VALIDER EN VERIFIANT LE TYPE DU DISPLAY
		@facet(name = IKeyword.TYPE, type = IType.LABEL, optional = true, doc = @doc("Allows to use either Java2D (for planar models) or OpenGL (for 3D models) as the rendering subsystem")),
		@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true, doc = @doc(value = "Allows to refresh the display every n time steps (default is 1)", deprecated = "Use refresh: every(n) instead")),
		@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true, doc = @doc("Indicates the condition under which this output should be refreshed (default is true)")),
		@facet(name = IKeyword.FULLSCREEN, type = IType.BOOL, optional = true, doc = @doc("Indicates whether or not the display should cover the whole screen (default is false")),
		@facet(name = IKeyword.TESSELATION, internal = true, type = IType.BOOL, optional = true, doc = @doc("")),
		@facet(name = IKeyword.ZFIGHTING, internal = true, type = IType.BOOL, optional = true, doc = @doc("Allows to alleviate a problem where agents at the same z would overlap each other in random ways")),
		@facet(name = IKeyword.TRACE, type = { IType.BOOL,
				IType.INT }, optional = true, doc = @doc(deprecated = "The value of the trace must instead be defined in each layer's definition now.", value = "Allows to aggregate the visualization of agents at each timestep on the display. Default is false. If set to an int value, only the last n-th steps will be visualized. If set to true, no limit of timesteps is applied. This facet can also be applied to individual layers")),
		@facet(name = IKeyword.SCALE, type = { IType.BOOL,
				IType.FLOAT }, optional = true, doc = @doc("Allows to display a scale bar in the overlay. Accepts true/false or an unit name")),
		@facet(name = IKeyword.SHOWFPS, internal = true, type = IType.BOOL, optional = true, doc = @doc("Allows to enable/disable the drawing of the number of frames per second")),
		@facet(name = IKeyword.DRAWENV, type = IType.BOOL, optional = true, doc = @doc("Allows to enable/disable the drawing of the world shape and the ordinate axes. Default can be configured in Preferences")),
		@facet(name = IKeyword.ORTHOGRAPHIC_PROJECTION, internal = true, type = IType.BOOL, optional = true, doc = @doc("Allows to enable/disable the orthographic projection. Default can be configured in Preferences")),
		@facet(name = IKeyword.AMBIENT_LIGHT, type = { IType.INT,
				IType.COLOR }, optional = true, doc = @doc("Allows to define the value of the ambient light either using an int (ambient_light:(125)) or a rgb color ((ambient_light:rgb(255,255,255)). default is rgb(127,127,127,255)")),
		@facet(name = IKeyword.DIFFUSE_LIGHT, type = { IType.INT,
				IType.COLOR }, optional = true, doc = @doc(value = "Allows to define the value of the diffuse light either using an int (diffuse_light:(125)) or a rgb color ((diffuse_light:rgb(255,255,255)). default is (127,127,127,255)", deprecated = "Use statement \"light\" instead")),
		@facet(name = IKeyword.DIFFUSE_LIGHT_POS, type = IType.POINT, optional = true, doc = @doc(value = "Allows to define the position of the diffuse light either using an point (diffuse_light_pos:{x,y,z}). default is {world.shape.width/2,world.shape.height/2,world.shape.width`*`2}", deprecated = "Use statement \"light\" instead")),
		@facet(name = IKeyword.IS_LIGHT_ON, type = IType.BOOL, optional = true, doc = @doc("Allows to enable/disable the light. Default is true")),
		@facet(name = IKeyword.DRAW_DIFFUSE_LIGHT, type = IType.BOOL, optional = true, doc = @doc(value = "Allows to show/hide a representation of the lights. Default is false.")),
		@facet(name = IKeyword.CAMERA_POS, type = { IType.POINT,
				IType.AGENT }, optional = true, doc = @doc("Allows to define the position of the camera")),
		@facet(name = IKeyword.CAMERA_LOOK_POS, type = IType.POINT, optional = true, doc = @doc("Allows to define the direction of the camera")),
		@facet(name = IKeyword.CAMERA_UP_VECTOR, type = IType.POINT, optional = true, doc = @doc("Allows to define the orientation of the camera")),
		@facet(name = IKeyword.CAMERA_LENS, internal = true, type = IType.INT, optional = true, doc = @doc("Allows to define the lens of the camera")),
		@facet(name = IKeyword.CAMERA_INTERACTION, type = IType.BOOL, optional = true, doc = @doc("If false, the user will not be able to modify the position and the orientation of the camera, and neither using the ROI. Default is true.")),
		@facet(name = "use_shader", type = IType.BOOL, optional = true, doc = @doc("Under construction...")),
		@facet(name = "keystone", type = IType.CONTAINER, optional = true, doc = @doc("Set the position of the 4 corners of your screen ([topLeft,topRight,botLeft,botRight]), in (x,y) coordinate ( the (0,0) position is the top left corner, while the (1,1) position is the bottom right corner). The default value is : [{0,0},{1,0},{0,1},{1,1}]. Note that this statement can only work with the \"use_shader\" facet set to true.")),
		@facet(name = IKeyword.POLYGONMODE, internal = true, type = IType.BOOL, optional = true, doc = @doc("")),
		@facet(name = IKeyword.AUTOSAVE, type = { IType.BOOL,
				IType.POINT }, optional = true, doc = @doc("Allows to save this display on disk. A value of true/false will save it at a resolution of 500x500. A point can be passed to personalize these dimensions")),
		@facet(name = IKeyword.OUTPUT3D, internal = true, type = { IType.BOOL,
				IType.POINT }, optional = true) }, omissible = IKeyword.NAME)
@inside(symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@validator(InfoValidator.class)
@serializer(DisplaySerializer.class)
@doc(value = "A display refers to a independent and mobile part of the interface that can display species, images, texts or charts.", usages = {
		@usage(value = "The general syntax is:", examples = @example(value = "display my_display [additional options] { ... }", isExecutable = false)),
		@usage(value = "Each display can include different layers (like in a GIS).", examples = {
				@example(value = "display gridWithElevationTriangulated type: opengl ambient_light: 100 {", isExecutable = false),
				@example(value = "	grid cell elevation: true triangulation: true;", isExecutable = false),
				@example(value = "	species people aspect: base;", isExecutable = false),
				@example(value = "}", isExecutable = false) }) })
public class LayeredDisplayOutput extends AbstractDisplayOutput {

	public static class DisplaySerializer extends SymbolSerializer {

		/**
		 * Method collectPluginsInFacetValue()
		 * 
		 * @see msi.gaml.descriptions.SymbolSerializer#collectPluginsInFacetValue(msi.gaml.descriptions.SymbolDescription,
		 *      java.lang.String, java.util.Set)
		 */
		@Override
		protected void collectMetaInformationInFacetValue(final SymbolDescription desc, final String key,
				final GamlProperties plugins) {
			super.collectMetaInformationInFacetValue(desc, key, plugins);
			if (key.equals(TYPE)) {
				final IExpressionDescription exp = desc.getFacet(TYPE);
				if (exp.getExpression() != null) {
					final String type = exp.getExpression().literalValue();
					final DisplayDescription dd = GAMA.getGui().getDisplayDescriptionFor(type);
					if (dd != null) {
						plugins.put(GamlProperties.PLUGINS, dd.getDefiningPlugin());
					}
				}
			}
		}

	}

	public static class InfoValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {

			final IExpressionDescription auto = d.getFacet(AUTOSAVE);
			if (auto != null && auto.getExpression().isConst() && auto.getExpression().literalValue().equals(TRUE)) {
				d.info("With autosave enabled, GAMA must remain the frontmost window and the display must not be covered or obscured by other windows",
						IGamlIssue.GENERAL, auto.getTarget(), AUTOSAVE);
			}
			// Are we in OpenGL world ?
			final IExpressionDescription type = d.getFacet(TYPE);
			if (type != null) {
				// Addresses and fixes Issue 833.
				final String s = type.getExpression().literalValue();
				if (!IGui.DISPLAYS.containsKey(s) && !GAMA.isInHeadLessMode()) {
					// In headless mode, all displays should be accepted
					d.error(s + " is not a valid display type. Valid types are:" + IGui.DISPLAYS.keySet(),
							IGamlIssue.UNKNOWN_KEYWORD, TYPE);
					return;
				}
				;
			}
			final Boolean isOpenGLDefault = !GamaPreferences.CORE_DISPLAY.getValue().equals("Java2D");
			final Boolean isOpenGLWanted = type == null ? isOpenGLDefault
					: type.getExpression().literalValue().equals(LayeredDisplayData.OPENGL);

			final IExpressionDescription camera = d.getFacet(CAMERA_POS, CAMERA_LOOK_POS, CAMERA_UP_VECTOR,
					CAMERA_LENS);
			if (!isOpenGLWanted && camera != null) {
				d.warning(
						"camera-related facets will have no effect on 2D displays. Use 'focus:' instead if you want to change the default zoom and position.",
						IGamlIssue.UNUSED, CAMERA_POS);
			}

			// AD: addressing the deprecation of the "trace:" facet
			final IExpressionDescription trace = d.getFacet(TRACE);
			if (trace != null) {
				d.visitChildren(new DescriptionVisitor<IDescription>() {

					@Override
					public boolean visit(final IDescription layer) {
						if (!layer.hasFacet(TRACE)) {
							layer.setFacet(TRACE, trace);
						}
						return true;
					}
				});

			}
		}

	}

	private final List<AbstractLayerStatement> layers;
	protected IDisplaySurface surface;
	private boolean useShader = false;
	private boolean constantBackground = true;
	private boolean constantAmbientLight = true;
	private boolean constantCamera = true;
	private boolean constantCameraLook = true;
	public volatile boolean cameraFix = false; // Means that the camera has been
												// set by the modeler.
	final LayeredDisplayData data = new LayeredDisplayData();
	// Specific to overlays
	OverlayStatement overlayInfo;

	public LayeredDisplayOutput(final IDescription desc) {
		super(desc);

		if (hasFacet(IKeyword.TYPE)) {
			data.setDisplayType(getLiteral(IKeyword.TYPE));
		}
		layers = new ArrayList<>();
	}

	public IOverlayProvider getOverlayProvider() {
		return overlayInfo;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		final boolean result = super.init(scope);
		if (!result) {
			return false;
		}
		final IExpression color = getFacet(IKeyword.BACKGROUND);
		if (color != null) {
			setBackgroundColor(Cast.asColor(getScope(), color.value(getScope())));
			constantBackground = color.isConst();
		}

		final IExpression auto = getFacet(IKeyword.AUTOSAVE);
		if (auto != null) {
			if (auto.getType().equals(Types.POINT)) {
				data.setAutosave(true);
				data.setImageDimension(Cast.asPoint(getScope(), auto.value(getScope())));
			} else {
				data.setAutosave(Cast.asBool(getScope(), auto.value(getScope())));
			}
		}
		for (final ILayerStatement layer : getLayers()) {
			// try {
			layer.setDisplayOutput(this);
			if (!getScope().init(layer)) {
				return false;
			}
			// } catch (final GamaRuntimeException e) {
			// GAMA.reportError(e, true);
			// return false;
			// }
		}

		// OpenGL parameter initialization
		final IExpression tess = getFacet(IKeyword.TESSELATION);
		if (tess != null) {
			this.data.setTesselation(Cast.asBool(getScope(), tess.value(getScope())));
		}

		final IExpression z = getFacet(IKeyword.ZFIGHTING);
		if (z != null) {
			this.data.setZ_fighting(Cast.asBool(getScope(), z.value(getScope())));
		}

		final IExpression scale = getFacet(IKeyword.SCALE);
		if (scale != null) {
			if (scale.getType().equals(Types.BOOL)) {
				data.setDisplayScale(Cast.asBool(getScope(), scale.value(getScope())));
			} else {
				data.setDisplayScale(true);
			}
		}

		final IExpression fps = getFacet(IKeyword.SHOWFPS);
		if (fps != null) {
			this.data.setShowfps(Cast.asBool(getScope(), fps.value(getScope())));
		}

		// computeTrace(getScope());

		final IExpression denv = getFacet(IKeyword.DRAWENV);
		if (denv != null) {
			this.data.setDrawEnv(Cast.asBool(getScope(), denv.value(getScope())));
		}

		final IExpression ortho = getFacet(IKeyword.ORTHOGRAPHIC_PROJECTION);
		if (ortho != null) {
			this.data.setOrtho(Cast.asBool(getScope(), ortho.value(getScope())));
		}

		final IExpression fixed_cam = getFacet(IKeyword.CAMERA_INTERACTION);
		if (fixed_cam != null) {
			this.data.disableCameraInteractions(!Cast.asBool(getScope(), fixed_cam.value(getScope())));
		}

		final IExpression use_shader = getFacet("use_shader");
		if (use_shader != null) {
			this.useShader = Cast.asBool(getScope(), use_shader.value(getScope()));
		}
		
		final IExpression keystone_exp = getFacet("keystone");
		if (keystone_exp != null) {
			List<ILocation> val = Cast.asList(getScope(), keystone_exp.value(getScope()));
			if (val.size() == 4) {
				data.setKeystone(val);
			}
		}

		final IExpression lightOn = getFacet(IKeyword.IS_LIGHT_ON);
		if (lightOn != null) {
			this.data.setLightOn(Cast.asBool(getScope(), lightOn.value(getScope())));
		}

		final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
		if (light != null) {
			if (light.getType().equals(Types.COLOR)) {
				this.data.setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
				this.data.setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
			}
			constantAmbientLight = light.isConst();
		}

		final IExpression light2 = getFacet(IKeyword.DIFFUSE_LIGHT);
		// this facet is deprecated...
		if (light2 != null) {
			this.data.setLightActive(1, true);
			if (light2.getType().equals(Types.COLOR)) {
				this.data.setDiffuseLightColor(1, Cast.asColor(getScope(), light2.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light2.value(getScope()));
				this.data.setDiffuseLightColor(1, new GamaColor(meanValue, meanValue, meanValue, 255));
			}
		}

		final IExpression light3 = getFacet(IKeyword.DIFFUSE_LIGHT_POS);
		// this facet is deprecated...
		if (light3 != null) {
			this.data.setLightActive(1, true);
			this.data.setLightDirection(1, (GamaPoint) Cast.asPoint(getScope(), light3.value(getScope())));
		}

		final IExpression drawLights = getFacet(IKeyword.DRAW_DIFFUSE_LIGHT);
		if (drawLights != null) {
			if (Cast.asBool(getScope(), drawLights.value(getScope())) == true) {
				// set the drawLight attribute to true for all the already
				// existing light
				for (int i = 0; i < 8; i++) {
					boolean lightAlreadyCreated = false;
					for (final LightPropertiesStructure lightProp : this.data.getDiffuseLights()) {
						if (lightProp.id == i) {
							lightProp.drawLight = true;
							lightAlreadyCreated = true;
						}
					}
					// if the light does not exist yet, create it by using the
					// method "setLightActive", and set the drawLight attr to
					// true.
					if (!lightAlreadyCreated) {
						if (i < 2) {
							this.data.setLightActive(i, true);
						} else {
							this.data.setLightActive(i, false);
						}
						this.data.setDrawLight(i, true);
					}
					lightAlreadyCreated = false;
				}
			}
		}

		final IExpression camera = getFacet(IKeyword.CAMERA_POS);
		if (camera != null) {
			final ILocation location = Cast.asPoint(getScope(), camera.value(getScope()));
			location.setY(-location.getY()); // y component need to be reverted
			this.data.setCameraPos(location);
			constantCamera = camera.isConst();
			cameraFix = true;
		}

		final IExpression cameraLook = getFacet(IKeyword.CAMERA_LOOK_POS);
		if (cameraLook != null) {
			final ILocation location = Cast.asPoint(getScope(), cameraLook.value(getScope()));
			location.setY(-location.getY()); // y component need to be reverted
			this.data.setCameraLookPos(location);
			constantCameraLook = cameraLook.isConst();
			cameraFix = true;
		}
		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraUp = getFacet(IKeyword.CAMERA_UP_VECTOR);
		if (cameraUp != null) {
			final ILocation location = Cast.asPoint(getScope(), cameraUp.value(getScope()));
			location.setY(-location.getY()); // y component need to be reverted
			this.data.setCameraUpVector(location);
			cameraFix = true;
		}

		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraLens = getFacet(IKeyword.CAMERA_LENS);
		if (cameraLens != null) {
			final int lens = Cast.asInt(getScope(), cameraLens.value(getScope()));
			this.data.setCameraLens(lens);
		}

		final IExpression poly = getFacet(IKeyword.POLYGONMODE);
		if (poly != null) {
			this.data.setPolygonMode(Cast.asBool(getScope(), poly.value(getScope())));
		}

		final IExpression out3D = getFacet(IKeyword.OUTPUT3D);
		if (out3D != null) {
			if (out3D.getType().equals(Types.POINT)) {
				this.data.setOutput3D(true);
				// this.data.setOutput3DNbCycles(Cast.asPoint(getScope(),
				// out3D.value(getScope())));
			} else {
				this.data.setOutput3D(Cast.asBool(getScope(), out3D.value(getScope())));
			}
		}

		final IExpression fs = getFacet(IKeyword.FULLSCREEN);
		if (fs != null) {
			this.data.setFullScreen(Cast.asBool(scope, fs.value(scope)));
		}

		SimulationAgent sim = getScope().getSimulation();
		// hqnghi if layer come from micro-model
		final ModelDescription micro = this.getDescription().getModelDescription();
		final ModelDescription main = (ModelDescription) scope.getModel().getDescription();
		final Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
		if (fromMicroModel) {
			final ExperimentAgent exp = (ExperimentAgent) scope.getRoot()
					.getExternMicroPopulationFor(micro.getAlias() + "." + this.getDescription().getOriginName())
					.getAgent(0);
			sim = exp.getSimulation();
		}
		// end-hqnghi
		Envelope env = null;
		if (sim != null) {
			env = sim.getEnvelope();
		} else {
			env = new Envelope3D(0, 100, 0, 100, 0, 0);
		}
		this.data.setEnvWidth(env.getWidth());
		this.data.setEnvHeight(env.getHeight());
		// We reinit the surface if it is present, but we dont create it anymore
		// in the first init.
		if (surface != null) {
			surface.outputReloaded();
		}
		createSurface(getScope());
		return true;
	}

	protected IGamaView.Display getView() {
		return (Display) view;
	}

	@Override
	public void open() {
		super.open();
		if (getView() != null)
			getView().waitToBeRealized();
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		for (final ILayerStatement layer : getLayers()) {
			getScope().setCurrentSymbol(layer);
			getScope().step(layer);
		}
		return true;
	}

	@Override
	public void update() throws GamaRuntimeException {
		if (surface == null) {
			return;
		}
		
		final IExpression auto = getFacet(IKeyword.AUTOSAVE);
		if (auto != null) {
			if (auto.getType().equals(Types.POINT)) {
				data.setAutosave(true);
				data.setImageDimension(Cast.asPoint(getScope(), auto.value(getScope())));
			} else {
				data.setAutosave(Cast.asBool(getScope(), auto.value(getScope())));
			}
		}
		// /////////////// dynamic Lighting ///////////////////

		if (!constantBackground) {

			final IExpression color = getFacet(IKeyword.BACKGROUND);
			if (color != null) {
				setBackgroundColor(Cast.asColor(getScope(), color.value(getScope())));
			}

		}

		if (!constantAmbientLight) {
			final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
			if (light != null) {
				if (light.getType().equals(Types.COLOR)) {
					this.data.setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
				} else {
					final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
					this.data.setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
				}
			}
		}

		// /////////////////// dynamic camera ///////////////////
		if (!constantCamera) {
			final IExpression camera = getFacet(IKeyword.CAMERA_POS);
			if (camera != null) {
				final ILocation location = Cast.asPoint(getScope(), camera.value(getScope()));
				if (location != null)
					location.setY(-location.getY()); // y component need to be
														// reverted
				this.data.setCameraPos(location);
			}
			// graphics.setCameraPosition(getCameraPos());
		}

		if (!constantCameraLook) {
			final IExpression cameraLook = getFacet(IKeyword.CAMERA_LOOK_POS);
			if (cameraLook != null) {
				final ILocation location = Cast.asPoint(getScope(), cameraLook.value(getScope()));
				if (location != null)
					location.setY(-location.getY()); // y component need to be
														// reverted
				this.data.setCameraLookPos(location);
			}
		}

		if (overlayInfo != null) {
			getScope().step(overlayInfo);
		}

		// if ( isSynchronized() ) {
		// surface.updateDisplay(false);
		// } else {
		super.update();
		// }

	}

	public boolean shouldDisplayScale() {
		return data.isDisplayScale();
	}

	public void toogleScaleDisplay() {
		data.setDisplayScale(!data.isDisplayScale());
	}

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		setSynchronized(false);
		super.dispose();
		if (surface != null) {
			surface.dispose();
		}
		surface = null;
		getLayers().clear();
		data.dispose();
	}

	protected void createSurface(final IScope scope) {
		if (surface != null) {
			surface.outputReloaded();
			return;
		}
		if (GAMA.isInHeadLessMode())
			data.setDisplayType(IKeyword.IMAGE);
		else if (isOpenGL()) {
			// If in headless mode, we need to get the 'image' surface
			return;
		}
		surface = scope.getGui().getDisplaySurfaceFor(this);
	}

	@Override
	public String getViewId() {
		if (isOpenGL()) {
			return IGui.GL_LAYER_VIEW_ID;
		}
		return IGui.LAYER_VIEW_ID;
	}

	public IDisplaySurface getSurface() {
		return surface;
	}

	@Override
	public List<? extends ISymbol> getChildren() {
		return getLayers();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		final List<AbstractLayerStatement> list = new ArrayList();
		for (final ISymbol s : commands) {
			if (s instanceof OverlayStatement && ((OverlayStatement) s).hasInfo()) {
				overlayInfo = (OverlayStatement) s;
			}
			list.add((AbstractLayerStatement) s);

		}
		setLayers(list);
		//
		// final List<LightStatement> lightList = new ArrayList();
		// for (final ISymbol s : commands) {
		// if (s instanceof OverlayStatement && ((OverlayStatement)
		// s).hasInfo()) {
		// overlayInfo = (OverlayStatement) s;
		// }
		// lightList.add((LightStatement) s);
		//
		// }
	}

	public void setSurface(final IDisplaySurface surface) {
		this.surface = surface;
	}

	public BufferedImage getImage() {
		return surface == null ? null : surface.getImage(surface.getWidth(), surface.getHeight());
	}

	public BufferedImage getImage(final int w, final int h) {
		return surface == null ? null : surface.getImage(w, h);
	}

	private void setBackgroundColor(final Color background) {
		data.setBackgroundColor(background);
	}

	public void setLayers(final List<AbstractLayerStatement> layers) {
		this.layers.clear();
		this.layers.addAll(layers);
	}

	public List<AbstractLayerStatement> getLayers() {
		return layers;
	}

	@Override
	public void setPaused(final boolean paused) {
		final boolean wasPaused = isPaused();
		super.setPaused(paused);
		if (surface == null) {
			return;
		}
		if (isOpenGL()) {
			((IDisplaySurface.OpenGL) surface).setPaused(paused);
		}
		if (wasPaused && !paused) {
			surface.updateDisplay(false);
		}
	}

	public boolean isOpenGL() {
		return data.getDisplayType().equals(LayeredDisplayData.OPENGL)
				|| data.getDisplayType().equals(LayeredDisplayData.THREED);
	}

	public LayeredDisplayData getData() {
		return data;
	}

	public boolean useShader() {
		if (data.getKeystone() != null) {
			return true;
		}
		return useShader;
	}

	// Keeping in sync the two implementations of synchronized, so that OpenGL
	// objects can have an easy access to the value (and modify it). Also allows
	// modelers to declare this property directly in the model.

	@Override
	public void setSynchronized(final boolean sync) {
		super.setSynchronized(sync);
		data.setSynchronized(sync);
	}

	@Override
	public boolean isSynchronized() {
		return super.isSynchronized() && data.isSynchronized();
	}

}
