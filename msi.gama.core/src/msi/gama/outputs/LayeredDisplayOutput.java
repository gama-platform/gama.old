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
import msi.gama.kernel.simulation.ISimulationAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.layers.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
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
		LayeredDisplayOutput.OPENGL }, optional = true),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true),
	@facet(name = IKeyword.TESSELATION, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.AMBIANT_LIGHT, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.POLYGONMODE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.AUTOSAVE, type = { IType.BOOL, IType.POINT }, optional = true),
	@facet(name = IKeyword.OUTPUT3D, type = { IType.BOOL, IType.POINT }, optional = true) }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.OUTPUT)
public class LayeredDisplayOutput extends AbstractDisplayOutput {

	public static final String JAVA2D = "java2D";
	public static final String OPENGL = "opengl";

	private List<AbstractLayerStatement> layers;
	private Color backgroundColor;
	protected IDisplaySurface surface;
	String snapshotFileName;
	private boolean autosave = false;
	private boolean output3D = false;
	private boolean tesselation = true;
	private double ambiantLight = 1.0;
	private boolean polygonmode = true;
	private String displayType = JAVA2D;
	private ILocation imageDimension = new GamaPoint(-1, -1);
	private ILocation output3DNbCycles = new GamaPoint(0, 0);

	public LayeredDisplayOutput(final IDescription desc) {
		super(desc);

		if ( hasFacet(IKeyword.TYPE) ) {
			displayType = getLiteral(IKeyword.TYPE);
		}
		layers = new GamaList<AbstractLayerStatement>();

	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);

		IExpression color = getFacet(IKeyword.BACKGROUND);
		if ( color != null ) {
			setBackgroundColor(Cast.asColor(getOwnScope(), color.value(getOwnScope())));
		} else {
			if ( getBackgroundColor() == null ) {
				setBackgroundColor(Cast.asColor(getOwnScope(), "white"));
			}
		}

		IExpression auto = getFacet(IKeyword.AUTOSAVE);
		if ( auto != null ) {
			if ( auto.getType().equals(Types.get(IType.POINT)) ) {
				autosave = true;
				imageDimension = Cast.asPoint(getOwnScope(), auto.value(getOwnScope()));
			} else {
				autosave = Cast.asBool(getOwnScope(), auto.value(getOwnScope()));
			}
		}

		for ( final ILayerStatement layer : getLayers() ) {
			try {
				layer.setDisplayOutput(this);
				layer.init(getOwnScope());
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
			}
		}

		// OpenGL parameter initialization
		IExpression tess = getFacet(IKeyword.TESSELATION);
		if ( tess != null ) {
			tesselation = Cast.asBool(getOwnScope(), tess.value(getOwnScope()));
		}

		IExpression light = getFacet(IKeyword.AMBIANT_LIGHT);
		if ( light != null ) {
			ambiantLight = Cast.asFloat(getOwnScope(), light.value(getOwnScope()));
		}

		IExpression poly = getFacet(IKeyword.POLYGONMODE);
		if ( poly != null ) {
			polygonmode = Cast.asBool(getOwnScope(), poly.value(getOwnScope()));
		}

		IExpression out3D = getFacet(IKeyword.OUTPUT3D);
		if ( out3D != null ) {
			if ( out3D.getType().equals(Types.get(IType.POINT)) ) {
				output3D = true;
				output3DNbCycles = Cast.asPoint(getOwnScope(), out3D.value(getOwnScope()));
			} else {
				output3D = Cast.asBool(getOwnScope(), out3D.value(getOwnScope()));
			}
		}

		createSurface(scope.getSimulationScope());
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		// GUI.debug("Computing the expressions of output " + getName() + " at cycle " + cycle);
		for ( ILayerStatement layer : getLayers() ) {
			layer.step(scope);
		}
	}

	@Override
	public void update() throws GamaRuntimeException {
		// GUI.debug("Updating output " + getName());
		if ( surface != null && surface.canBeUpdated() ) {
			// GUI.debug("Updating the surface of output " + getName());
			surface.updateDisplay();
			// Use to define which technique is used in opengl to triangulate polygon

		}
	}

	@Override
	public void forceUpdate() throws GamaRuntimeException {
		// GUI.debug("Updating output " + getName());
		if ( surface != null && surface.canBeUpdated() ) {
			// GUI.debug("Updating the surface of output " + getName());
			surface.forceUpdateDisplay();
		}
	}

	@Override
	public void schedule() throws GamaRuntimeException {
		step(getOwnScope());
		super.schedule();
	}

	public void setImageFileName(final String fileName) {
		snapshotFileName = fileName;
	}

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		super.dispose();
		if ( surface != null ) {
			surface.dispose();
		}
		surface = null;
		getLayers().clear();
	}

	protected void createSurface(final ISimulationAgent sim) {
		Envelope env = sim.getEnvelope();
		double w = env.getWidth();
		double h = env.getHeight();
		if ( surface != null ) {
			surface.outputChanged(w, h, this);
			return;
		}
		surface = outputManager.getDisplaySurfaceFor(displayType, this, w, h);
		surface.setSnapshotFileName(getName() + "_snapshot");
		surface.setAutoSave(autosave, (int) imageDimension.getX(), (int) imageDimension.getY());

		// Use only for opengl
		if ( surface.getMyGraphics() != null ) {
			// surface.setOutput3D(output3D);
			surface.initOutput3D(output3D, output3DNbCycles);
			surface.getMyGraphics().useTesselation(tesselation);
			surface.getMyGraphics().setAmbiantLight((float) ambiantLight);
			surface.getMyGraphics().setPolygonMode(polygonmode);
		}
	}

	public void setSurface(final IDisplaySurface sur) {
		surface = sur;
	}

	@Override
	public String getViewId() {

		return GuiUtils.LAYER_VIEW_ID;
	}

	@Override
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

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public BufferedImage getImage() {
		return surface.getImage();
	}

	@Override
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
	}

}
