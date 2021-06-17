/*******************************************************************************************************
 *
 * msi.gama.outputs.LayeredDisplayOutput.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.outputs.LayeredDisplayOutput.DisplaySerializer;
import msi.gama.outputs.LayeredDisplayOutput.InfoValidator;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.OverlayStatement;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
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
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * The Class LayerDisplayOutput.
 *
 * @author drogoul
 */
@symbol (
		name = { IKeyword.DISPLAY },
		kind = ISymbolKind.OUTPUT,
		with_sequence = true,
		concept = { IConcept.DISPLAY })
@facets (
		value = { @facet (
				name = IKeyword.VIRTUAL,
				type = IType.BOOL,
				optional = true,
				doc = @doc ("Declaring a display as virtual makes it invisible on screen, and only usable for display inheritance")),
				@facet (
						name = IKeyword.PARENT,
						type = IType.ID,
						optional = true,
						doc = @doc ("Declares that this display inherits its layers and attributes from the parent display named as the argument. Expects the identifier of the parent display or a string if the name of the parent contains spaces")),
				@facet (
						name = IKeyword.BACKGROUND,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Allows to fill the background of the display with a specific color")),
				@facet (
						name = IKeyword.NAME,
						type = IType.LABEL,
						optional = false,
						doc = @doc ("the identifier of the display")),
				@facet (
						name = IKeyword.FOCUS,
						type = IType.GEOMETRY,
						optional = true,
						doc = @doc ("the geometry (or agent) on which the display will (dynamically) focus")),
				// WARNING VALIDER EN VERIFIANT LE TYPE DU DISPLAY
				@facet (
						name = IKeyword.TYPE,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Allows to use either Java2D (for planar models) or OpenGL (for 3D models) as the rendering subsystem")),
				@facet (
						name = IKeyword.REFRESH_EVERY,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "Allows to refresh the display every n time steps (default is 1)",
								deprecated = "Use refresh: every(n) instead")),
				@facet (
						name = "synchronized",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates whether the display should be directly synchronized with the simulation")),
				@facet (
						name = "antialias",
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates whether to use advanced antialiasing for the display or not. The default value is the one indicated in the preferences of GAMA ('false' is its factory default). Antialising produces smoother outputs, but comes with a cost in terms of speed and memory used. ")),

				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates the condition under which this output should be refreshed (default is true)")),
				@facet (
						name = IKeyword.TOOLBAR,
						type = { IType.BOOL, IType.COLOR },
						optional = true,
						doc = @doc ("Indicates whether the top toolbar of the display view should be initially visible or not. If a color is passed, then the background of the toolbar takes this color")),
				@facet (
						name = IKeyword.FULLSCREEN,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("Indicates, when using a boolean value, whether or not the display should cover the whole screen (default is false). If an integer is passed, specifies also the screen to use: 0 for the primary monitor, 1 for the secondary one, and so on and so forth. If the monitor is not available, the first one is used")),

				@facet (
						name = IKeyword.ZFIGHTING,
						internal = true,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								deprecated = "now done automatically by default",
								value = "Allows to alleviate a problem where agents at the same z would overlap each other in random ways")),
				@facet (
						name = IKeyword.SCALE,
						type = { IType.BOOL, IType.FLOAT },
						optional = true,
						doc = @doc (
								value = "Allows to display a scale bar in the overlay. Accepts true/false or an unit name",
								deprecated = "Not functional anymore. Scale is now displayed by default")),
				@facet (
						name = IKeyword.SHOWFPS,
						internal = true,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to enable/disable the drawing of the number of frames per second")),
				@facet (
						name = IKeyword.DRAWENV,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to enable/disable the drawing of the world shape and the ordinate axes. Default can be configured in Preferences")),
				@facet (
						name = IKeyword.ORTHOGRAPHIC_PROJECTION,
						internal = true,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to enable/disable the orthographic projection. Default can be configured in Preferences")),
				@facet (
						name = IKeyword.AMBIENT_LIGHT,
						type = { IType.INT, IType.COLOR },
						optional = true,
						doc = @doc ("Allows to define the value of the ambient light either using an int (ambient_light:(125)) or a rgb color ((ambient_light:rgb(255,255,255)). default is rgb(127,127,127,255)")),
				@facet (
						name = IKeyword.DIFFUSE_LIGHT,
						type = { IType.INT, IType.COLOR },
						optional = true,
						doc = @doc (
								value = "Allows to define the value of the diffuse light either using an int (diffuse_light:(125)) or a rgb color ((diffuse_light:rgb(255,255,255)). default is (127,127,127,255)",
								deprecated = "Use statement \"light\" instead")),
				@facet (
						name = IKeyword.DIFFUSE_LIGHT_POS,
						type = IType.POINT,
						optional = true,
						doc = @doc (
								value = "Allows to define the position of the diffuse light either using an point (diffuse_light_pos:{x,y,z}). default is {world.shape.width/2,world.shape.height/2,world.shape.width`*`2}",
								deprecated = "Use statement \"light\" instead")),
				@facet (
						name = IKeyword.IS_LIGHT_ON,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to enable/disable the light. Default is true")),
				@facet (
						name = IKeyword.DRAW_DIFFUSE_LIGHT,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "Allows to show/hide a representation of the lights. Default is false.")),
				@facet (
						name = "camera_pos",
						type = { IType.POINT, IType.AGENT },
						optional = true,
						doc = @doc (
								deprecated = "Use 'camera_location' instead",
								value = "Allows to define the position of the camera")),
				@facet (
						name = IKeyword.CAMERA_LOCATION,
						type = { IType.POINT, IType.AGENT },
						optional = true,
						doc = @doc ("Allows to define the location of the camera, the origin being the center of the model")),
				@facet (
						name = IKeyword.CAMERA_TARGET,
						type = IType.POINT,
						optional = true,
						doc = @doc ("Allows to define the target of the camera (what does it look at)")),
				@facet (
						name = "camera_look_pos",
						type = IType.POINT,
						optional = true,
						doc = @doc (
								deprecated = "Use 'camera_target' instead",
								value = "Allows to define the direction of the camera")),
				@facet (
						name = IKeyword.CAMERA_ORIENTATION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("Allows to define the orientation of the 'up-vector' of the camera")),
				@facet (
						name = "camera_up_vector",
						type = IType.POINT,
						optional = true,
						doc = @doc (
								deprecated = "Use 'camera_orientation' instead",
								value = "Allows to define the orientation of the 'up-vector' of the camera")),
				@facet (
						name = IKeyword.CAMERA_LENS,
						internal = true,
						type = IType.INT,
						optional = true,
						doc = @doc ("Allows to define the lens of the camera")),
				@facet (
						name = IKeyword.CAMERA_INTERACTION,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If false, the user will not be able to modify the position and the orientation of the camera, and neither using the ROI. Default is true.")),
				@facet (
						name = "use_shader",
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "Used to invoke the new OpenGL architecture",
								deprecated = "If the coresponding plugin has been installed, use 'display type: opengl2' instead")),
				@facet (
						name = IKeyword.KEYSTONE,
						type = IType.CONTAINER,
						optional = true,
						doc = @doc ("Set the position of the 4 corners of your screen ([topLeft,topRight,botLeft,botRight]), in (x,y) coordinate ( the (0,0) position is the top left corner, while the (1,1) position is the bottom right corner). The default value is : [{0,0},{1,0},{0,1},{1,1}]")),
				@facet (
						name = IKeyword.ROTATE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Set the angle for the rotation around the Z axis")),
				@facet (
						name = "z_near",
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Set the distances to the near depth clipping planes. Must be positive.")),
				@facet (
						name = "z_far",
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Set the distances to the far depth clipping planes. Must be positive.")),
				@facet (
						name = IKeyword.AUTOSAVE,
						type = { IType.BOOL, IType.POINT, IType.STRING },
						optional = true,
						doc = @doc ("Allows to save this display on disk. A value of true/false will save it at a resolution of 500x500. A point can be passed to personalize these dimensions. Note that setting autosave to true (or to any other value than false) in a display will synchronize all the displays defined in the experiment")), },
		omissible = IKeyword.NAME)
@inside (
		symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@validator (InfoValidator.class)
@serializer (DisplaySerializer.class)
@doc (
		value = "A display refers to a independent and mobile part of the interface that can display species, images, texts or charts.",
		usages = { @usage (
				value = "The general syntax is:",
				examples = @example (
						value = "display my_display [additional options] { ... }",
						isExecutable = false)),
				@usage (
						value = "Each display can include different layers (like in a GIS).",
						examples = { @example (
								value = "display gridWithElevationTriangulated type: opengl ambient_light: 100 {",
								isExecutable = false),
								@example (
										value = "	grid cell elevation: true triangulation: true;",
										isExecutable = false),
								@example (
										value = "	species people aspect: base;",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) })
public class LayeredDisplayOutput extends AbstractDisplayOutput {

	private final List<AbstractLayerStatement> layers;
	protected IDisplaySurface surface;
	private int index;

	final LayeredDisplayData data = new LayeredDisplayData();
	// Specific to overlays
	OverlayStatement overlayInfo;

	public static class DisplaySerializer extends SymbolSerializer<SymbolDescription> {

		/**
		 * Method collectPluginsInFacetValue()
		 *
		 * @see msi.gaml.descriptions.SymbolSerializer#collectPluginsInFacetValue(msi.gaml.descriptions.SymbolDescription,
		 *      java.lang.String, java.util.Set)
		 */
		// @Override
		// protected void collectMetaInformationInFacetValue(final SymbolDescription desc, final String key,
		// final GamlProperties plugins) {
		// super.collectMetaInformationInFacetValue(desc, key, plugins);
		// if (key.equals(TYPE)) {
		// final IExpressionDescription exp = desc.getFacet(TYPE);
		// if (exp.getExpression() != null) {
		// final String type = exp.getExpression().literalValue();
		// final DisplayDescription dd = msi.gama.runtime.GAMA.getGui().getDisplayDescriptionFor(type);
		// if (dd != null) {
		// plugins.put(GamlProperties.PLUGINS, dd.getDefiningPlugin());
		// }
		// }
		// }
		// }

	}

	public static class InfoValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {

			final IExpressionDescription parent = d.getFacet(PARENT);
			if (parent != null) { handleInheritance(d, parent.toString()); }

			final IExpressionDescription auto = d.getFacet(AUTOSAVE);
			if (auto != null && auto.getExpression().isConst() && auto.getExpression().literalValue().equals(TRUE)) {
				d.info("With autosave enabled, GAMA must remain the frontmost window and the display must not be covered or obscured by other windows",
						IGamlIssue.GENERAL, auto.getTarget(), AUTOSAVE);
			}
			// Are we in OpenGL world ?
			final IExpressionDescription type = d.getFacet(TYPE);
			final Boolean isOpenGLDefault = !GamaPreferences.Displays.CORE_DISPLAY.getValue().equals("Java2D");
			final Boolean isOpenGLWanted = type == null ? isOpenGLDefault
					: type.getExpression().literalValue().equals(LayeredDisplayData.OPENGL);
			if (type != null) {
				// Addresses and fixes Issue 833.
				final String s = type.getExpression().literalValue();
				if (!IGui.DISPLAYS.containsKey(s) && !msi.gama.runtime.GAMA.isInHeadLessMode()) {
					// In headless mode, all displays should be accepted
					d.error(s + " is not a valid display type. Valid types are:" + IGui.DISPLAYS.keySet(),
							IGamlIssue.UNKNOWN_KEYWORD, TYPE);
					return;
				}
			} else {
				if (isOpenGLDefault) { d.setFacet(TYPE, LabelExpressionDescription.create(LayeredDisplayData.OPENGL)); }
			}

			final String camera = d.firstFacetFoundAmong(CAMERA_LOCATION, CAMERA_TARGET, CAMERA_ORIENTATION,
					CAMERA_LENS, "z_near", "z_far");
			if (!isOpenGLWanted && camera != null) {
				d.warning(
						"camera-related facets will have no effect on 2D displays. Use 'focus:' instead if you want to change the default zoom and position.",
						IGamlIssue.UNUSED, camera);
			}

			// AD: addressing the deprecation of the "trace:" facet
			final IExpressionDescription trace = d.getFacet(TRACE);
			if (trace != null) {
				d.visitChildren(layer -> {
					if (!layer.hasFacet(TRACE)) { layer.setFacet(TRACE, trace); }
					return true;
				});

			}
			// AD: addressing the deprecation of camera_up_vector, camera_look_pos and camera_pos
			IExpressionDescription c = d.getFacet("camera_up_vector");
			if (c != null) { d.setFacet(CAMERA_ORIENTATION, c); }
			c = d.getFacet("camera_look_pos");
			if (c != null) { d.setFacet(CAMERA_TARGET, c); }
			c = d.getFacet("camera_pos");
			if (c != null) { d.setFacet(CAMERA_LOCATION, c); }
			d.removeFacets("camera_pos", "camera_look_pos", "camera_up_vector");
		}

		private void handleInheritance(final IDescription d, final String string) {
			final IDescription output = d.getEnclosingDescription();
			for (final IDescription display : output.getChildrenWithKeyword(DISPLAY)) {
				if (display.getName().equals(string)) {
					handleInheritance(d, display);
					return;
				}
			}
			d.error("No parent display named '" + string + "' found");
		}

		private void handleInheritance(final IDescription child, final IDescription parent) {
			final Facets childFacets = child.getFacets();
			final boolean hasVirtual = childFacets.containsKey(VIRTUAL);
			final Facets parentFacets = parent.getFacets();
			childFacets.complementWith(parentFacets);
			if (!hasVirtual) { childFacets.remove(VIRTUAL); }
			child.replaceChildrenWith(Iterables.concat(parent.getOwnChildren(), child.getOwnChildren()));

		}

	}

	public LayeredDisplayOutput(final IDescription desc) {
		super(desc);

		if (hasFacet(IKeyword.TYPE)) { data.setDisplayType(getLiteral(IKeyword.TYPE)); }
		layers = new ArrayList<>();
	}

	public IOverlayProvider<OverlayInfo> getOverlayProvider() {
		return overlayInfo;
	}

	@Override
	public boolean shouldOpenAsynchronously() {
		// OpenGL views need to be opened synchronously
		return !data.isOpenGL();
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		final boolean result = super.init(scope);
		if (!result) return false;
		data.initWith(getScope(), description);

		for (final ILayerStatement layer : getLayers()) {
			layer.setDisplayOutput(this);
			if (!getScope().init(layer).passed()) return false;
		}

		final IExpression sync = getFacet("synchronized");
		if (sync != null) { setSynchronized(Cast.asBool(getScope(), sync.value(getScope()))); }

		createSurface(getScope());
		return true;
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
		if (surface == null) return;
		data.update(getScope(), description.getFacets());

		if (overlayInfo != null) { getScope().step(overlayInfo); }

		super.update();

	}

	@Override
	public void dispose() {
		if (disposed) return;
		setSynchronized(false);
		super.dispose();
		if (surface != null) { surface.dispose(); }
		surface = null;
		getLayers().clear();
		data.dispose();
	}

	protected void createSurface(final IScope scope) {
		if (surface != null) {
			surface.outputReloaded();
			return;
		}
		if (scope.getExperiment().isHeadless()) {
			// If in headless mode, we need to get the 'image' surface
			data.setDisplayType(IKeyword.IMAGE);
		} else if (data.isOpenGL()) // The surface will be crezated later
			return;
		surface = scope.getGui().createDisplaySurfaceFor(this);
	}

	@Override
	public String getViewId() {
		if (data.isWeb()) return IGui.GL_LAYER_VIEW_ID3;
		if (data.isOpenGL2()) return IGui.GL_LAYER_VIEW_ID2;
		if (data.isOpenGL()) return IGui.GL_LAYER_VIEW_ID;
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
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		final List<AbstractLayerStatement> list = new ArrayList<>();
		for (final ISymbol s : commands) {
			if (s instanceof OverlayStatement && ((OverlayStatement) s).hasInfo()) {
				overlayInfo = (OverlayStatement) s;
			}
			list.add((AbstractLayerStatement) s);

		}
		setLayers(list);

	}

	public void setSurface(final IDisplaySurface surface) {
		this.surface = surface;
		if (surface == null) { view = null; }
	}

	public BufferedImage getImage() {
		return surface == null ? null : surface.getImage(surface.getWidth(), surface.getHeight());
	}

	public BufferedImage getImage(final int w, final int h) {
		return surface == null ? null : surface.getImage(w, h);
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
		if (surface == null) return;
		if (data.isOpenGL()) { ((IDisplaySurface.OpenGL) surface).setPaused(paused); }
		if (wasPaused && !paused) { surface.updateDisplay(false); }
	}

	public LayeredDisplayData getData() {
		return data;
	}

	// Keeping in sync the two implementations of synchronized, so that OpenGL
	// objects can have an easy access to the value (and modify it). Also allows
	// modelers to declare this property directly in the model.

	@Override
	public void setSynchronized(final boolean sync) {
		data.setSynchronized(sync);
		super.setSynchronized(sync);

	}

	@Override
	public boolean isSynchronized() {
		return super.isSynchronized() && data.isSynchronized();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	@Override
	public boolean isAutoSave() {
		final IExpression e = getFacet(IKeyword.AUTOSAVE);
		if (e == null) return false;
		if (e == IExpressionFactory.FALSE_EXPR) return false;
		return true;
	}

}
