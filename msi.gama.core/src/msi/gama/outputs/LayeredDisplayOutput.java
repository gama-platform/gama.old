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
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.LayeredDisplayOutput.InfoValidator;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The Class LayerDisplayOutput.
 * 
 * @author drogoul
 */
@symbol(name = { IKeyword.DISPLAY }, kind = ISymbolKind.OUTPUT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.BACKGROUND,
		type = IType.COLOR,
		optional = true,
		doc = @doc("Allows to fill the background of the display with a specific color")),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the display")),
	@facet(name = IKeyword.FOCUS,
		type = IType.GEOMETRY,
		optional = true,
		doc = @doc("the geometry (or agent) on which the displau will (dynamically) focus")),
	// WARNING VALIDER EN VERIFIANT LE TYPE DU DISPLAY
	@facet(name = IKeyword.TYPE,
		type = IType.LABEL,
		optional = true,
		doc = @doc("Allows to use either Java2D (for planar models) or OpenGL (for 3D models) as the rendering subsystem")),
	@facet(name = IKeyword.REFRESH_EVERY,
		type = IType.INT,
		optional = true,
		doc = @doc(value = "Allows to refresh the display every n time steps (default is 1)",
			deprecated = "Use refresh: every(n) instead")),
	@facet(name = IKeyword.REFRESH,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Indicates the condition under which this output should be refreshed (default is true)")),
	@facet(name = IKeyword.TESSELATION, type = IType.BOOL, optional = true, doc = @doc("")),
	@facet(name = IKeyword.ZFIGHTING,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to alleviate a problem where agents at the same z would overlap each other in random ways")),
	@facet(name = IKeyword.TRACE,
		type = { IType.BOOL, IType.INT },
		optional = true,
		doc = @doc("Allows to aggregate the visualization of agents at each timestep on the display. Default is false. If set to an int value, only the last n-th steps will be visualized. If set to true, no limit of timesteps is applied. This facet can also be applied to individual layers")),
	@facet(name = IKeyword.SCALE,
		type = { IType.BOOL, IType.FLOAT },
		optional = true,
		doc = @doc("Allows to display a scale bar in the overlay. Accepts true/false or an unit name")),
	@facet(name = IKeyword.SHOWFPS,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to enable/disable the drawing of the number of frames per second")),
	@facet(name = IKeyword.DRAWENV,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to enable/disable the drawing of the world shape and the ordinate axes. Default can be configured in Preferences")),
	@facet(name = IKeyword.ORTHOGRAPHIC_PROJECTION,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to enable/disable the orthographic projection. Default can be configured in Preferences")),
	@facet(name = IKeyword.AMBIENT_LIGHT,
		type = { IType.INT, IType.COLOR },
		optional = true,
		doc = @doc("Allows to define the value of the ambient light either using an int (ambient_light:(125)) or a rgb color ((ambient_light:rgb(255,255,255)). default is rgb(125,125,125)")),
	@facet(name = IKeyword.DIFFUSE_LIGHT,
		type = { IType.INT, IType.COLOR },
		optional = true,
		doc = @doc("Allows to define the value of the diffuse light either using an int (diffuse_light:(125)) or a rgb color ((diffuse_light:rgb(255,255,255)). default is rgb(125,125,125)")),
	@facet(name = IKeyword.DIFFUSE_LIGHT_POS,
		type = IType.POINT,
		optional = true,
		doc = @doc("Allows to define the position of the diffuse light either using an point (diffuse_light_pos:{x,y,z}). default is {world.shape.width/2,world.shape.height/2,world.shape.width`*`2}")),
	@facet(name = IKeyword.IS_LIGHT_ON,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to enable/disable the light. Default is true")),
	@facet(name = IKeyword.DRAW_DIFFUSE_LIGHT,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows to enable/disable the drawing of the diffuse light. Default is false")),
	@facet(name = IKeyword.CAMERA_POS,
		type = { IType.POINT, IType.AGENT },
		optional = true,
		doc = @doc("Allows to define the position of the camera")),
	@facet(name = IKeyword.CAMERA_LOOK_POS,
		type = IType.POINT,
		optional = true,
		doc = @doc("Allows to define the direction of the camera")),
	@facet(name = IKeyword.CAMERA_UP_VECTOR,
		type = IType.POINT,
		optional = true,
		doc = @doc("Allows to define the orientation of the camera")),
	@facet(name = IKeyword.POLYGONMODE, type = IType.BOOL, optional = true, doc = @doc("")),
	@facet(name = IKeyword.AUTOSAVE,
		type = { IType.BOOL, IType.POINT },
		optional = true,
		doc = @doc("Allows to save this display on disk. A value of true/false will save it at a resolution of 500x500. A point can be passed to personalize these dimensions")),
	@facet(name = IKeyword.OUTPUT3D, type = { IType.BOOL, IType.POINT }, optional = true) },
	omissible = IKeyword.NAME)
@inside(symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@validator(InfoValidator.class)
@doc(value = "A display refers to a independent and mobile part of the interface that can display species, images, texts or charts.",
	usages = {
		@usage(value = "The general syntax is:",
			examples = @example(value = "display my_display [additional options] { ... }", isExecutable = false)),
		@usage(value = "Each display can include different layers (like in a GIS).", examples = {
			@example(value = "display gridWithElevationTriangulated type: opengl ambient_light: 100 {",
				isExecutable = false),
			@example(value = "	grid cell elevation: true triangulation: true;", isExecutable = false),
			@example(value = "	species people aspect: base;", isExecutable = false),
			@example(value = "}", isExecutable = false) }) })
public class LayeredDisplayOutput extends AbstractDisplayOutput {

	public static class InfoValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {

			IExpressionDescription auto = d.getFacets().get(AUTOSAVE);
			if ( auto != null && auto.getExpression().isConst() && auto.getExpression().literalValue().equals(TRUE) ) {
				d.info(
					"With autosave enabled, GAMA must remain the frontmost window and the display must not be covered or obscured by other windows",
					IGamlIssue.GENERAL, auto.getTarget(), AUTOSAVE);
			}
			// Are we in OpenGL world ?
			IExpressionDescription type = d.getFacets().get(TYPE);
			if ( type != null ) {
				// Addresses and fixes Issue 833.
				String s = type.getExpression().literalValue();
				if ( !IGui.DISPLAYS.containsKey(s) ) {
					d.error(s + " is not a valid display type. Valid types are:" + IGui.DISPLAYS.keySet(),
						IGamlIssue.UNKNOWN_KEYWORD, TYPE);
					return;
				};
			}
			Boolean isOpenGLDefault = !GamaPreferences.CORE_DISPLAY.getValue().equals("Java2D");
			Boolean isOpenGLWanted =
				type == null ? isOpenGLDefault : type.getExpression().literalValue()
					.equals(LayeredDisplayOutput.OPENGL);

			if ( !isOpenGLWanted ) { return; }

			// Do we display a grid ?

			Boolean gridDisplayed = false;
			for ( IDescription desc : d.getChildren() ) {
				if ( desc.getKeyword().equals(GRID_POPULATION) ) {
					gridDisplayed = true;
					break;
				}
			}
			if ( !gridDisplayed ) { return; }
			IExpressionDescription zfight = d.getFacets().get(ZFIGHTING);
			Boolean zFightDefault = GamaPreferences.CORE_Z_FIGHTING.getValue();
			Boolean zFightWanted =
				zfight == null ? zFightDefault : zfight.getExpression().literalValue().equals(IKeyword.TRUE);
			if ( zFightWanted ) {
				String prefs = zFightDefault ? "(enabled by default in the Preferences)" : "";
				d.info("z_fighting " + prefs +
					" improves the rendering, but disables the selection of a single cell in a grid layer",
					IGamlIssue.GENERAL, zfight == null ? null : zfight.getTarget(), IKeyword.AUTOSAVE);
			}
		}
	}

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";
	public static final String WEB = "web";
	public static final String THREED = "3D";

	private List<AbstractLayerStatement> layers;
	protected IDisplaySurface surface;
	private boolean constantBackground = true;
	private boolean constantAmbientLight = true;
	private boolean constantDiffuseLight = true;
	private boolean constantDiffusePos = true;
	private boolean constantCamera = true;
	private boolean constantCameraLook = true;
	final LayeredDisplayData data = new LayeredDisplayData(GamaPreferences.CORE_BACKGROUND.getValue(), false, false,
		true, 0, GamaPreferences.CORE_Z_FIGHTING.getValue(), GamaPreferences.CORE_DRAW_NORM.getValue(),
		GamaPreferences.CORE_CUBEDISPLAY.getValue(), false, GamaPreferences.CORE_SCALE.getValue(),
		GamaPreferences.CORE_SHOW_FPS.getValue(), GamaPreferences.CORE_DRAW_ENV.getValue(),
		GamaPreferences.CORE_IS_LIGHT_ON.getValue(), false, new GamaColor(100, 100, 100, 255), new GamaColor(10, 10,
			10, 255), new GamaPoint(-1, -1, -1), new GamaPoint(-1, -1, -1), new GamaPoint(-1, -1, -1), new GamaPoint(0,
			1, 0), true, GamaPreferences.CORE_DISPLAY.getValue().equalsIgnoreCase(JAVA2D) ? JAVA2D : OPENGL,
		new GamaPoint(-1, -1), new GamaPoint(0, 0), GamaPreferences.CORE_HIGHLIGHT.getValue());
	// Specific to overlays
	OverlayStatement overlayInfo;

	public LayeredDisplayOutput(final IDescription desc) {
		super(desc);

		if ( hasFacet(IKeyword.TYPE) ) {
			data.setDisplayType(getLiteral(IKeyword.TYPE));
		}
		layers = new ArrayList<AbstractLayerStatement>();
	}

	public IOverlayProvider getOverlayProvider() {
		return overlayInfo;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		boolean result = super.init(scope);
		if ( !result ) { return false; }
		final IExpression color = getFacet(IKeyword.BACKGROUND);
		if ( color != null ) {
			setBackgroundColor(Cast.asColor(getScope(), color.value(getScope())));

			if ( color.isConst() ) {
				constantBackground = true;
			} else {
				constantBackground = false;
			}
		}

		final IExpression auto = getFacet(IKeyword.AUTOSAVE);
		if ( auto != null ) {
			if ( auto.getType().equals(Types.POINT) ) {
				data.setAutosave(true);
				data.setImageDimension(Cast.asPoint(getScope(), auto.value(getScope())));
			} else {
				data.setAutosave(Cast.asBool(getScope(), auto.value(getScope())));
			}
		}
		for ( final ILayerStatement layer : getLayers() ) {
			// try {
			layer.setDisplayOutput(this);
			if ( !getScope().init(layer) ) { return false; }
			// } catch (final GamaRuntimeException e) {
			// GAMA.reportError(e, true);
			// return false;
			// }
		}

		// OpenGL parameter initialization
		final IExpression tess = getFacet(IKeyword.TESSELATION);
		if ( tess != null ) {
			setTesselation(Cast.asBool(getScope(), tess.value(getScope())));
		}

		final IExpression z = getFacet(IKeyword.ZFIGHTING);
		if ( z != null ) {
			setZFighting(Cast.asBool(getScope(), z.value(getScope())));
		}

		final IExpression scale = getFacet(IKeyword.SCALE);
		if ( scale != null ) {
			if ( scale.getType().equals(Types.BOOL) ) {
				data.setDisplayScale(Cast.asBool(getScope(), scale.value(getScope())));
			} else {
				data.setDisplayScale(true);
			}
		}

		final IExpression fps = getFacet(IKeyword.SHOWFPS);
		if ( fps != null ) {
			setShowFPS(Cast.asBool(getScope(), fps.value(getScope())));
		}

		computeTrace(getScope());

		final IExpression denv = getFacet(IKeyword.DRAWENV);
		if ( denv != null ) {
			setDrawEnv(Cast.asBool(getScope(), denv.value(getScope())));
		}

		final IExpression ortho = getFacet(IKeyword.ORTHOGRAPHIC_PROJECTION);
		if ( ortho != null ) {
			setOrtho(Cast.asBool(getScope(), ortho.value(getScope())));
		}

		final IExpression ddiff = getFacet(IKeyword.DRAW_DIFFUSE_LIGHT);
		if ( ddiff != null ) {
			setDrawDiffuseLight(Cast.asBool(getScope(), ddiff.value(getScope())));
		}

		final IExpression lightOn = getFacet(IKeyword.IS_LIGHT_ON);
		if ( lightOn != null ) {
			setIsLightOn(Cast.asBool(getScope(), lightOn.value(getScope())));
		}

		final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
		if ( light != null ) {

			if ( light.getType().equals(Types.COLOR) ) {
				setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
				setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
			}

			if ( light.isConst() ) {
				constantAmbientLight = true;
			} else {
				constantAmbientLight = false;
			}

		}

		final IExpression light2 = getFacet(IKeyword.DIFFUSE_LIGHT);
		if ( light2 != null ) {

			if ( light2.getType().equals(Types.COLOR) ) {
				setDiffuseLightColor(Cast.asColor(getScope(), light2.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light2.value(getScope()));
				setDiffuseLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
			}

			if ( light2.isConst() ) {
				constantDiffuseLight = true;
			} else {
				constantDiffuseLight = false;
			}

		}

		final IExpression light3 = getFacet(IKeyword.DIFFUSE_LIGHT_POS);
		if ( light3 != null ) {
			setDiffuseLightPosition((GamaPoint) Cast.asPoint(getScope(), light3.value(getScope())));
			if ( light3.isConst() ) {
				constantDiffusePos = true;
			} else {
				constantDiffusePos = false;
			}
		}

		final IExpression camera = getFacet(IKeyword.CAMERA_POS);
		if ( camera != null ) {

			setCameraPos(Cast.asPoint(getScope(), camera.value(getScope())));

			if ( camera.isConst() ) {
				constantCamera = true;
			} else {
				constantCamera = false;
			}

		}

		final IExpression cameraLook = getFacet(IKeyword.CAMERA_LOOK_POS);
		if ( cameraLook != null ) {
			setCameraLookPos(Cast.asPoint(getScope(), cameraLook.value(getScope())));

			if ( cameraLook.isConst() ) {
				constantCameraLook = true;
			} else {
				constantCameraLook = false;
			}

		}
		// Set the up vector of the opengl Camera (see gluPerspective)
		final IExpression cameraUp = getFacet(IKeyword.CAMERA_UP_VECTOR);
		if ( cameraUp != null ) {
			setCameraUpVector(Cast.asPoint(getScope(), cameraUp.value(getScope())));
		}

		final IExpression poly = getFacet(IKeyword.POLYGONMODE);
		if ( poly != null ) {
			setPolygonMode(Cast.asBool(getScope(), poly.value(getScope())));
		}

		final IExpression out3D = getFacet(IKeyword.OUTPUT3D);
		if ( out3D != null ) {
			if ( out3D.getType().equals(Types.POINT) ) {
				setOutput3D(true);
				setOutput3DNbCycles(Cast.asPoint(getScope(), out3D.value(getScope())));
			} else {
				setOutput3D(Cast.asBool(getScope(), out3D.value(getScope())));
			}
		}
		SimulationAgent sim = getScope().getSimulationScope();
		// hqnghi if layer come from micro-model
		ModelDescription micro = this.getDescription().getModelDescription();
		ModelDescription main = (ModelDescription) scope.getModel().getDescription();
		Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
		if ( fromMicroModel ) {
			ExperimentAgent exp =
				(ExperimentAgent) scope.getAgentScope()
					.getExternMicroPopulationFor(this.getDescription().getOriginName()).getAgent(0);
			sim = (SimulationAgent) exp.getSimulation();
		}
		// end-hqnghi
		Envelope env = null;
		if ( sim != null ) {
			env = sim.getEnvelope();
		} else {
			env = new Envelope3D(0, 100, 0, 100, 0, 0);
		}
		this.data.setEnvWidth(env.getWidth());
		this.data.setEnvHeight(env.getHeight());
		createSurface(getScope());
		return true;
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		for ( final ILayerStatement layer : getLayers() ) {
			getScope().step(layer);
		}
		return true;
	}

	@Override
	public void update() throws GamaRuntimeException {
		if ( surface == null ) { return; }
		// /////////////// dynamic Lighting ///////////////////

		if ( !constantBackground ) {

			final IExpression color = getFacet(IKeyword.BACKGROUND);
			if ( color != null ) {
				setBackgroundColor(Cast.asColor(getScope(), color.value(getScope())));
			}

		}

		if ( !constantAmbientLight ) {
			final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
			if ( light != null ) {
				if ( light.getType().equals(Types.COLOR) ) {
					setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
				} else {
					final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
					setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
				}
			}
		}

		if ( !constantDiffuseLight ) {
			final IExpression light2 = getFacet(IKeyword.DIFFUSE_LIGHT);
			if ( light2 != null ) {
				if ( light2.getType().equals(Types.COLOR) ) {
					setDiffuseLightColor(Cast.asColor(getScope(), light2.value(getScope())));
				} else {
					final int meanValue = Cast.asInt(getScope(), light2.value(getScope()));
					setDiffuseLightColor(new GamaColor(meanValue, meanValue, meanValue, 255));
				}
			}
		}

		if ( !constantDiffusePos ) {
			final IExpression light3 = getFacet(IKeyword.DIFFUSE_LIGHT_POS);
			if ( light3 != null ) {
				setDiffuseLightPosition((GamaPoint) Cast.asPoint(getScope(), light3.value(getScope())));

			}
		}

		// /////////////////// dynamic camera ///////////////////
		if ( !constantCamera ) {
			final IExpression camera = getFacet(IKeyword.CAMERA_POS);
			if ( camera != null ) {
				setCameraPos(Cast.asPoint(getScope(), camera.value(getScope())));
			}
			// graphics.setCameraPosition(getCameraPos());
		}

		if ( !constantCameraLook ) {
			final IExpression cameraLook = getFacet(IKeyword.CAMERA_LOOK_POS);
			if ( cameraLook != null ) {
				setCameraLookPos(Cast.asPoint(getScope(), cameraLook.value(getScope())));
			}
		}
		computeTrace(getScope());

		if ( overlayInfo != null ) {
			getScope().step(overlayInfo);
		}

		if ( isSynchronized() ) {
			surface.updateDisplay(false);
		} else {
			super.update();
		}

	}

	private void computeTrace(final IScope scope) {
		final IExpression agg = getFacet(IKeyword.TRACE);
		if ( agg != null ) {
			int limit = 0;
			if ( agg.getType().id() == IType.BOOL && Cast.asBool(scope, agg.value(scope)) ) {
				limit = Integer.MAX_VALUE;
			} else {
				limit = Cast.asInt(scope, agg.value(scope));
			}
			setTraceDisplay(limit);
		}
	}

	public boolean shouldDisplayScale() {
		return data.isDisplayScale();
	}

	public void toogleScaleDisplay() {
		data.setDisplayScale(!data.isDisplayScale());
	}

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		setSynchronized(false);
		super.dispose();
		if ( surface != null ) {
			surface.dispose();
		}
		surface = null;
		getLayers().clear();
	}

	protected void createSurface(final IScope scope) {
		if ( surface != null ) {
			surface.initialize(scope, this);
		} else {
			surface = GuiUtils.getDisplaySurfaceFor(scope, this);
		}
	}

	public double getEnvWidth() {
		return data.getEnvWidth();
	}

	public double getEnvHeight() {
		return data.getEnvHeight();
	}

	@Override
	public String getViewId() {
		return GuiUtils.LAYER_VIEW_ID;
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
		List<AbstractLayerStatement> list = new ArrayList();
		for ( ISymbol s : commands ) {
			if ( s instanceof OverlayStatement ) {
				overlayInfo = (OverlayStatement) s;
			} else if ( s instanceof AbstractLayerStatement ) {
				list.add((AbstractLayerStatement) s);
			}
		}
		setLayers(list);
	}

	public Color getBackgroundColor() {
		return data.getBackgroundColor();
	}

	public BufferedImage getImage() {
		return surface.getImage();
	}

	public void setBackgroundColor(final Color background) {
		this.data.setBackgroundColor(background);
		if ( surface != null ) {
			surface.setBackground(background);
		}
	}

	public void setLayers(final List<AbstractLayerStatement> layers) {
		this.layers = layers;
	}

	public List<AbstractLayerStatement> getLayers() {
		return layers;
	}

	@Override
	public void setPaused(final boolean paused) {
		boolean wasPaused = isPaused();
		super.setPaused(paused);
		if ( isOpenGL() ) {
			((IDisplaySurface.OpenGL) surface).setPaused(paused);
		}
		if ( wasPaused && !paused ) {
			surface.updateDisplay(false);
		}
	}

	public boolean isOpenGL() {
		return data.getDisplayType().equals(OPENGL) || data.getDisplayType().equals(THREED);
	}

	public boolean getTesselation() {
		return data.isTesselation();
	}

	private void setTesselation(final boolean tesselation) {
		this.data.setTesselation(tesselation);
	}

	public boolean getZFighting() {
		return data.isZ_fighting();
	}

	private void setZFighting(final boolean z) {
		this.data.setZ_fighting(z);
	}

	public boolean getDrawNorm() {
		return data.isDraw_norm();
	}

	public boolean getCubeDisplay() {
		return data.isCubeDisplay();
	}

	public boolean getOrtho() {
		return data.isOrtho();
	}

	private void setOrtho(final boolean o) {
		this.data.setOrtho(o);
	}

	public boolean getShowFPS() {
		return data.isShowfps();
	}

	private void setShowFPS(final boolean fps) {
		this.data.setShowfps(fps);
	}

	public int getTraceDisplay() {
		return data.getTraceDisplay();
	}

	private void setTraceDisplay(final int agg) {
		this.data.setTraceDisplay(agg);
	}

	public boolean getDrawEnv() {
		return data.isDrawEnv();
	}

	private void setDrawEnv(final boolean drawEnv) {
		this.data.setDrawEnv(drawEnv);
	}

	public boolean getDrawDiffuseLight() {
		return data.isDrawDiffLight();
	}

	private void setDrawDiffuseLight(final boolean drawDiff) {
		this.data.setDrawDiffLight(drawDiff);
	}

	public boolean getIsLightOn() {
		return data.isLightOn();
	}

	private void setIsLightOn(final boolean islight) {
		this.data.setLightOn(islight);
	}

	public boolean getOutput3D() {
		return data.isOutput3D();
	}

	private void setOutput3D(final boolean output3D) {
		this.data.setOutput3D(output3D);
	}

	public ILocation getCameraPos() {
		return data.getCameraPos();
	}

	private void setCameraPos(final ILocation cameraPos) {
		this.data.setCameraPos(cameraPos);
	}

	public ILocation getCameraLookPos() {
		return data.getCameraLookPos();
	}

	private void setCameraLookPos(final ILocation cameraLookPos) {
		this.data.setCameraLookPos(cameraLookPos);
	}

	public ILocation getCameraUpVector() {
		return data.getCameraUpVector();
	}

	private void setCameraUpVector(final ILocation cameraUpVector) {
		this.data.setCameraUpVector(cameraUpVector);
	}

	public Color getAmbientLightColor() {
		return data.getAmbientLightColor();
	}

	private void setAmbientLightColor(final Color ambientLightColor) {
		this.data.setAmbientLightColor(ambientLightColor);
	}

	public Color getDiffuseLightColor() {
		return data.getDiffuseLightColor();
	}

	private void setDiffuseLightColor(final Color diffuseLightColor) {
		this.data.setDiffuseLightColor(diffuseLightColor);
	}

	public GamaPoint getDiffuseLightPosition() {
		return data.getDiffuseLightPosition();
	}

	private void setDiffuseLightPosition(final GamaPoint diffuseLightPosition) {
		this.data.setDiffuseLightPosition(diffuseLightPosition);
	}

	public boolean getPolygonMode() {
		return data.isPolygonMode();
	}

	private void setPolygonMode(final boolean polygonMode) {
		this.data.setPolygonMode(polygonMode);
	}

	public ILocation getOutput3DNbCycles() {
		return data.getOutput3DNbCycles();
	}

	private void setOutput3DNbCycles(final ILocation output3DNbCycles) {
		this.data.setOutput3DNbCycles(output3DNbCycles);
	}

	public LayeredDisplayData getData() {
		return data;
	}

}
