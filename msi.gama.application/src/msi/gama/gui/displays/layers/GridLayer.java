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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.displays.layers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ImageUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import org.eclipse.swt.widgets.Composite;
import com.vividsolutions.jts.geom.Envelope;

public class GridLayer extends ImageLayer {

	private boolean turnGridOn;
	private double[] gridValue;
	private double[] gridValueMatrix;
	private GamaFile textureFile;
	private boolean isTextured;
	private boolean isTexturedWithImage;
	private boolean isTriangulated;
	private boolean isShowText;
	private boolean drawAsDEM;
	private int cellSize;

	public GridLayer(final ILayerStatement layer) {
		super(layer);
		turnGridOn = ((GridLayerStatement) layer).drawLines();
		drawAsDEM = false;
	}

	@Override
	public void outputChanged() {
		super.outputChanged();
		if ( image != null ) {
			image.flush();
			image = null;
		}
	}

	@Override
	public void fillComposite(final Composite compo, final IDisplaySurface container) {
		super.fillComposite(compo, container);
		EditorFactory.create(compo, "Draw grid:", turnGridOn, new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) throws GamaRuntimeException {
				turnGridOn = newValue;
				container.forceUpdateDisplay();
			}
		});
	}

	@Override
	protected void buildImage() {
		final GridLayerStatement g = (GridLayerStatement) definition;
		final IGrid m = g.getEnvironment();
		final ILocation p = m.getDimensions();
		cellSize=(int) m.getAgents().get(0).getGeometry().getEnvelope().getWidth();
		
		if ( image == null ) {
			image = ImageUtils.createCompatibleImage(p.getX(), p.getY());
		}
		image.setRGB(0, 0, (int) p.getX(), (int) p.getY(), m.getDisplayData(), 0, (int) p.getX());
	
			//As their is 2 ways to give the dem we need to check which one is active
			//FIXME : what happen if the 2 are defined in the model?
			if(g.getGridValueMatrix() != null){
				gridValueMatrix=g.getGridValueMatrix().getMatrix();
				textureFile = g.textureFile();	
				
				if(textureFile!=null){
					isTextured = true;
				}
				else{
					isTextured = g.isTextured();
				}
				isTriangulated = g.isTriangulated();
				isShowText = g.isShowText();
				drawAsDEM = true;
			}
			if(m.getGridValue() != null){
				gridValueMatrix = m.getGridValue();
				textureFile = g.textureFile();
				
				if(textureFile!=null){
					isTextured = true;
				}
				else{
					isTextured = g.isTextured();
				}
				
				isTriangulated = g.isTriangulated();
				isShowText = g.isShowText();
				drawAsDEM = true;
			}
	}

	
	@Override
	public void privateDrawDisplay(final IScope scope, final IGraphics dg) {
		buildImage();
		if ( image == null ) { return; }
		Color lineColor = null;
		if ( turnGridOn ) {
			lineColor = ((GridLayerStatement) definition).getLineColor();
			if ( lineColor == null ) {
				lineColor = Color.black;
			}
		}
		
		if(drawAsDEM){
			
		if(textureFile !=null){ //display grid dem:texturefile
			BufferedImage texture = null;
			try {
				texture = ImageUtils.getInstance().getImageFromFile(textureFile.getPath());
			} catch (final IOException e) {
				e.printStackTrace();
			}
			dg.drawGrid(scope, texture, gridValueMatrix,isTextured,isTriangulated,isShowText,null, null, lineColor, null, 0.0, true,cellSize);
		}
		else{
			dg.drawGrid(scope, image, gridValueMatrix,isTextured,isTriangulated,isShowText,null, null, lineColor, null, 0.0, true,cellSize);		
		}
			
			
		}
		else{
			dg.drawImage(scope, image, null, null, lineColor, null, 0.0, true);	
		}		
	}

	private IAgent getPlaceAt(final GamaPoint loc) {
		return ((GridLayerStatement) definition).getEnvironment().getAgentAt(loc);
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		final Set<IAgent> result = new HashSet();
		result.add(getPlaceAt(this.getModelCoordinatesFrom(x, y, g)));
		return result;
	}

	@Override
	protected String getType() {
		return "Grid layer";
	}

}
