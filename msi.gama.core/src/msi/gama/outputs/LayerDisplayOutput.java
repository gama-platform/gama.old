/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.topology.IEnvironment;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.types.IType;

/**
 * The Class LayerDisplayOutput.
 * 
 * @author drogoul
 */
@symbol(name = { IKeyword.DISPLAY }, kind = ISymbolKind.OUTPUT)
@facets(value = { @facet(name = IKeyword.BACKGROUND, type = IType.COLOR_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, optional = true),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT_STR, optional = true),
	@facet(name = "autosave", type = IType.BOOL_STR, optional = true) })
@with_sequence
@inside(symbols = IKeyword.OUTPUT)
public class LayerDisplayOutput extends AbstractDisplayOutput {

	public static String snapshotFolder = "snapshots";
	private List<AbstractDisplayLayer> layers;
	private Color backgroundColor;
	protected IDisplaySurface surface;
	String snapshotFileName;
	private final boolean openGL = false;
	private boolean autosave = false;

	// private GLContext glcontext;
	// private GLCanvas glcanvas;

	public LayerDisplayOutput(final IDescription desc) {
		super(desc);
		layers = new GamaList<AbstractDisplayLayer>();
	}

	@Override
	public void prepare(final ISimulation sim) throws GamaRuntimeException {
		super.prepare(sim);
		IExpression color = getFacet(IKeyword.BACKGROUND);
		if ( color != null ) {
			setBackgroundColor(Cast.asColor(getOwnScope(), color.value(getOwnScope())));
		} else {
			if ( getBackgroundColor() == null ) {
				setBackgroundColor(Cast.asColor(getOwnScope(), "white"));
			}
		}
		/***/
		IExpression auto = getFacet("autosave");
		if ( auto != null ) {
			autosave = Cast.asBool(getOwnScope(), auto.value(getOwnScope()));
		}
		/***/
		for ( final ISymbol layer : getLayers() ) {
			try {
				((IDisplayLayer) layer).prepare(this, getOwnScope());
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
			}
		}
		createSurface(sim);
	}

	@Override
	public void compute(final IScope scope, final int cycle) throws GamaRuntimeException {
		// GUI.debug("Computing the expressions of output " + getName() + " at cycle " + cycle);
		for ( IDisplayLayer layer : getLayers() ) {
			layer.compute(scope, cycle);
		}
	}

	@Override
	public void update() throws GamaRuntimeException {
		// GUI.debug("Updating output " + getName());
		if ( surface != null && surface.canBeUpdated() ) {
			// GUI.debug("Updating the surface of output " + getName());
			surface.updateDisplay();
		}
		if ( autosave ) {
			save(getOwnScope());
		}
	}

	@Override
	public void schedule() throws GamaRuntimeException {
		compute(getOwnScope(), 0);
		super.schedule();
	}

	public void save(final IScope scope) {
		try {
			Files.newFolder(scope, snapshotFolder);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + snapshotFolder);
			GAMA.reportError(e1);
			e1.printStackTrace();
			return;
		}
		String snapshotFile =
			scope.getSimulationScope().getModel()
				.getRelativeFilePath(snapshotFolder + "/" + snapshotFileName, false);

		String file = snapshotFile + SimulationClock.getCycle() + ".png";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(file));
			RenderedImage im = surface.getImage();
			ImageIO.write(im, "png", os);
		} catch (java.io.IOException ex) {
			GamaRuntimeException e = new GamaRuntimeException(ex);
			e.addContext("Unable to create output stream for snapshot image");
			GAMA.reportError(e);
		} finally {
			try {
				if ( os != null ) {
					os.close();
				}
			} catch (Exception ex) {
				GamaRuntimeException e = new GamaRuntimeException(ex);
				e.addContext("Unable to close output stream for snapshot image");
				GAMA.reportError(e);
			}
		}
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

	protected void createSurface(final ISimulation sim) {
		if ( openGL ) { return; }
		// TEST SUR OPENGL -> return;
		// ITopology env = sim.getWorldEnvironment();
		IEnvironment env = sim.getModel().getModelEnvironment();
		double w = env.getWidth();
		double h = env.getHeight();
		if ( surface != null ) {
			surface.outputChanged(w, h, this);
			return;
		}
		surface = outputManager.getDisplaySurfaceFor(this, w, h);
		setImageFileName(getName() + "_snapshot");
	}

	public void setSurface(final IDisplaySurface sur) {
		surface = sur;
	}

	@Override
	public String getViewId() {
		// The dependency to msi.gama.gui.opengl is put on hold for the moment.
		return /* openGL ? OpenglLayeredDisplayView.ID : */GuiUtils.LAYER_VIEW_ID;
	}

	// public void setContext(final GLContext cont) {
	// glcontext = cont;
	// GL gl = glcontext.getGL();
	// gl.glClearColor(0.0f, 0.0f, 0.5f, 0.0f);
	// }

	// public void setCanvas(final GLCanvas can) {
	// glcanvas = can;
	// }

	// public GLContext getContext() {
	// return glcontext;
	// }

	// public GLCanvas getCanvas() {
	// return glcanvas;
	// }

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
		setLayers((List<AbstractDisplayLayer>) commands);
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

	public void setLayers(final List<AbstractDisplayLayer> layers) {
		this.layers = layers;
	}

	List<AbstractDisplayLayer> getLayers() {
		return layers;
	}

}
