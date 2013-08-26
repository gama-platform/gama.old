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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The Class LayerDisplayOutput.
 * 
 * @author drogoul
 */
// FIXME: Why this is not define in gaml/statement
@symbol(name = { IKeyword.DISPLAY }, kind = ISymbolKind.OUTPUT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.BACKGROUND, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { LayeredDisplayOutput.JAVA2D,
		LayeredDisplayOutput.OPENGL, LayeredDisplayOutput.THREED, LayeredDisplayOutput.SWT }, optional = true),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true),
	@facet(name = IKeyword.TESSELATION, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.STENCIL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.ZFIGHTING, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.SHOWFPS, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.DRAWENV, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.AMBIENT_LIGHT, type = { IType.INT, IType.COLOR }, optional = true),
	@facet(name = IKeyword.DIFFUSE_LIGHT, type = { IType.INT, IType.COLOR }, optional = true),
	@facet(name = IKeyword.CAMERA_POS, type = { IType.POINT, IType.AGENT }, optional = true),
	@facet(name = IKeyword.CAMERA_LOOK_POS, type = IType.POINT, optional = true),
	@facet(name = IKeyword.CAMERA_UP_VECTOR, type = IType.POINT, optional = true),
	@facet(name = IKeyword.POLYGONMODE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.AUTOSAVE, type = { IType.BOOL, IType.POINT }, optional = true),
	@facet(name = IKeyword.OUTPUT3D, type = { IType.BOOL, IType.POINT }, optional = true) }, omissible = IKeyword.NAME)
@inside(symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
public class LayeredDisplayOutput extends AbstractDisplayOutput {

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";
	public static final String THREED = "3D";
	public static final String SWT = "swt";

	private List<AbstractLayerStatement> layers;
	private Color backgroundColor;
	protected IDisplaySurface surface;
	String snapshotFileName;
	private boolean autosave = false;
	private boolean output3D = false;
	private boolean tesselation = true;
	private boolean stencil = false;
	private boolean z_fighting = false;
	private boolean showfps = false;
	private boolean drawEnv = true;
	private Color ambientLightColor = new GamaColor(125, 125, 125);
	private Color diffuseLightColor = new GamaColor(125, 125, 125);
	// Set it to (-1,-1,-1) to set the camera with the right value if no value defined.
	private ILocation cameraPos = new GamaPoint(-1, -1, -1);
	private ILocation cameraLookPos = new GamaPoint(-1, -1, -1);
	private ILocation cameraUpVector = new GamaPoint(0, 1, 0);
	private boolean constantAmbientLight = true;
	private boolean constantDiffuseLight = true;
	private boolean constantCamera = true;
	private boolean constantCameraLook = true;
	private boolean polygonMode = true;
	private String displayType = JAVA2D;
	private ILocation imageDimension = new GamaPoint(-1, -1);
	private ILocation output3DNbCycles = new GamaPoint(0, 0);
	private boolean isSwt = false;
	private double envWidth;
	private double envHeight;

	public LayeredDisplayOutput(final IDescription desc) {
		super(desc);

		if ( hasFacet(IKeyword.TYPE) ) {
			displayType = getLiteral(IKeyword.TYPE);
			isSwt = displayType.equals(SWT);
		}
		layers = new GamaList<AbstractLayerStatement>();

	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		boolean result = super.init(scope);
		if ( !result ) { return false; }
		final IExpression color = getFacet(IKeyword.BACKGROUND);
		if ( color != null ) {
			setBackgroundColor(Cast.asColor(getScope(), color.value(getScope())));
		} else {
			if ( getBackgroundColor() == null ) {
				setBackgroundColor(Cast.asColor(getScope(), "white"));
			}
		}

		final IExpression auto = getFacet(IKeyword.AUTOSAVE);
		if ( auto != null ) {
			if ( auto.getType().equals(Types.get(IType.POINT)) ) {
				autosave = true;
				imageDimension = Cast.asPoint(getScope(), auto.value(getScope()));
			} else {
				autosave = Cast.asBool(getScope(), auto.value(getScope()));
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

		final IExpression st = getFacet(IKeyword.STENCIL);
		if ( st != null ) {
			setStencil(Cast.asBool(getScope(), st.value(getScope())));
		}

		final IExpression z = getFacet(IKeyword.ZFIGHTING);
		if ( z != null ) {
			setZFighting(Cast.asBool(getScope(), z.value(getScope())));
		}

		final IExpression fps = getFacet(IKeyword.SHOWFPS);
		if ( fps != null ) {
			setShowFPS(Cast.asBool(getScope(), fps.value(getScope())));
		}

		final IExpression denv = getFacet(IKeyword.DRAWENV);
		if ( denv != null ) {
			setDrawEnv(Cast.asBool(getScope(), denv.value(getScope())));
		}

		final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
		if ( light != null ) {

			if ( light.getType().equals(Types.get(IType.COLOR)) ) {
				setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
				setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue));
			}

			if ( light.isConst() ) {
				constantAmbientLight = true;
			} else {
				constantAmbientLight = false;
			}

		}

		final IExpression light2 = getFacet(IKeyword.DIFFUSE_LIGHT);
		if ( light2 != null ) {

			if ( light2.getType().equals(Types.get(IType.COLOR)) ) {
				setDiffuseLightColor(Cast.asColor(getScope(), light2.value(getScope())));
			} else {
				final int meanValue = Cast.asInt(getScope(), light2.value(getScope()));
				setDiffuseLightColor(new GamaColor(meanValue, meanValue, meanValue));
			}

			if ( light2.isConst() ) {
				constantDiffuseLight = true;
			} else {
				constantDiffuseLight = false;
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
			if ( out3D.getType().equals(Types.get(IType.POINT)) ) {
				setOutput3D(true);
				setOutput3DNbCycles(Cast.asPoint(getScope(), out3D.value(getScope())));
			} else {
				setOutput3D(Cast.asBool(getScope(), out3D.value(getScope())));
			}
		}
		Envelope env = scope.getSimulationScope().getEnvelope();
		this.envWidth = env.getWidth();
		this.envHeight = env.getHeight();
		if ( !isSwt ) {
			createSurface(scope.getSimulationScope());
		}
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
		if ( !constantAmbientLight ) {
			final IExpression light = getFacet(IKeyword.AMBIENT_LIGHT);
			if ( light != null ) {
				if ( light.getType().equals(Types.get(IType.COLOR)) ) {
					setAmbientLightColor(Cast.asColor(getScope(), light.value(getScope())));
				} else {
					final int meanValue = Cast.asInt(getScope(), light.value(getScope()));
					setAmbientLightColor(new GamaColor(meanValue, meanValue, meanValue));
				}
			}
		}

		if ( !constantDiffuseLight ) {
			final IExpression light2 = getFacet(IKeyword.DIFFUSE_LIGHT);
			if ( light2 != null ) {
				if ( light2.getType().equals(Types.get(IType.COLOR)) ) {
					setDiffuseLightColor(Cast.asColor(getScope(), light2.value(getScope())));
				} else {
					final int meanValue = Cast.asInt(getScope(), light2.value(getScope()));
					setDiffuseLightColor(new GamaColor(meanValue, meanValue, meanValue));
				}
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

		// GuiUtils.debug("LayeredDisplayOutput.update");
		surface.updateDisplay();

	}

	@Override
	public void forceUpdate() throws GamaRuntimeException {
		// GUI.debug("Updating output " + getName());
		if ( surface != null /* && surface.canBeUpdated() */) {
			// GUI.debug("Updating the surface of output " + getName());
			surface.forceUpdateDisplay();
		}
	}

	//
	// @Override
	// public void schedule() throws GamaRuntimeException {
	// super.schedule();
	// getScope().step(this);
	// }

	public void setImageFileName(final String fileName) {
		snapshotFileName = fileName;
	}

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		if ( surface != null ) {
			surface.setSynchronized(false);
		}
		super.dispose();
		if ( surface != null ) {
			surface.dispose();
		}
		surface = null;
		getLayers().clear();
	}

	protected void createSurface(final IAgent sim) {
		final Envelope env = sim.getEnvelope();
		final double w = env.getWidth();
		final double h = env.getHeight();
		if ( surface != null ) {
			surface.outputChanged(w, h, this);
			return;
		}
		surface = GuiUtils.getDisplaySurfaceFor(displayType, this, w, h);
		surface.setSnapshotFileName(getName() + "_snapshot");
		surface.setAutoSave(autosave, (int) imageDimension.getX(), (int) imageDimension.getY());

		// Use only for opengl
		// if ( surface.getIGraphics() instanceof IGraphics.OpenGL ) {
		// IGraphics.OpenGL graphics = (IGraphics.OpenGL) surface.getIGraphics();
		// surface.initOutput3D(output3D, output3DNbCycles);
		// graphics.useTesselation(tesselation);
		// graphics.setAmbientLightValue((GamaColor) ambientLightColor);
		// graphics.setPolygonMode(polygonmode);
		// graphics.setCameraPosition(cameraPos);
		// graphics.setCameraLookPosition(cameraLookPos);
		// graphics.setCameraUpVector(cameraUpVector);
		// }
	}

	public void setSurface(final IDisplaySurface sur) {
		surface = sur;
	}

	public double getEnvWidth() {
		return envWidth;
	}

	public double getEnvHeight() {
		return envHeight;
	}

	@Override
	public String getViewId() {
		return isSwt ? GuiUtils.SWT_LAYER_VIEW_ID : GuiUtils.LAYER_VIEW_ID;
	}

	public boolean isSWT() {
		return this.isSwt;
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
		setLayers((List<AbstractLayerStatement>) commands);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public BufferedImage getImage() {
		return surface.getImage();
	}

	public void setBackgroundColor(final Color background) {
		this.backgroundColor = background;
		if ( surface != null ) {
			surface.setBackgroundColor(background);
		}
	}

	public void setLayers(final List<AbstractLayerStatement> layers) {
		this.layers = layers;
	}

	List<AbstractLayerStatement> getLayers() {
		return layers;
	}

	@Override
	public void pause() {
		super.pause();
		surface.setPaused(true);
	}

	@Override
	public void resume() {
		super.resume();
		surface.setPaused(false);
		// getScope().step(this);
	}

	public boolean isOpenGL() {
		return displayType.equals(OPENGL) || displayType.equals(THREED);
	}

	public boolean getTesselation() {
		return tesselation;
	}

	private void setTesselation(final boolean tesselation) {
		this.tesselation = tesselation;
	}

	public boolean getStencil() {
		return stencil;
	}

	private void setStencil(final boolean stencil) {
		this.stencil = stencil;
	}
	
	public boolean getZFighting() {
		return z_fighting;
	}

	private void setZFighting(final boolean z) {
		this.z_fighting = z;
	}

	public boolean getShowFPS() {
		return showfps;
	}

	private void setShowFPS(final boolean fps) {
		this.showfps = fps;
	}

	public boolean getDrawEnv() {
		return drawEnv;
	}

	private void setDrawEnv(final boolean drawEnv) {
		this.drawEnv = drawEnv;
	}

	public boolean getOutput3D() {
		return output3D;
	}

	private void setOutput3D(final boolean output3D) {
		this.output3D = output3D;
	}

	public ILocation getCameraPos() {
		return cameraPos;
	}

	private void setCameraPos(final ILocation cameraPos) {
		this.cameraPos = cameraPos;
	}

	public ILocation getCameraLookPos() {
		return cameraLookPos;
	}

	private void setCameraLookPos(final ILocation cameraLookPos) {
		this.cameraLookPos = cameraLookPos;
	}

	public ILocation getCameraUpVector() {
		return cameraUpVector;
	}

	private void setCameraUpVector(final ILocation cameraUpVector) {
		this.cameraUpVector = cameraUpVector;
	}

	public Color getAmbientLightColor() {
		return ambientLightColor;
	}

	private void setAmbientLightColor(final Color ambientLightColor) {
		this.ambientLightColor = ambientLightColor;
	}

	public Color getDiffuseLightColor() {
		return diffuseLightColor;
	}

	private void setDiffuseLightColor(final Color diffuseLightColor) {
		this.diffuseLightColor = diffuseLightColor;
	}

	public boolean getPolygonMode() {
		return polygonMode;
	}

	private void setPolygonMode(final boolean polygonMode) {
		this.polygonMode = polygonMode;
	}

	public ILocation getOutput3DNbCycles() {
		return output3DNbCycles;
	}

	private void setOutput3DNbCycles(final ILocation output3DNbCycles) {
		this.output3DNbCycles = output3DNbCycles;
	}

}
