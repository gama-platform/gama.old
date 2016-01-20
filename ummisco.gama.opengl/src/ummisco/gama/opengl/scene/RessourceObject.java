/*********************************************************************************************
 *
 *
 * 'thisObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.util.AbstractGui;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaPair;
import msi.gama.util.file.GamaFile;
import msi.gaml.operators.Cast;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GLModel;
import ummisco.gama.opengl.files.GamaObjFile;

public class RessourceObject extends AbstractObject implements Cloneable {

	public GamaFile file;
	public IAgent agent;
	public double z_layer;
	public Color color;
	public Double alpha;
	public GamaPoint size;
	public GamaPoint atLoc = null;
	Double rot = null;
	GamaPoint ptRot = null;
	Double rotInit = null;
	GamaPoint ptRotInit = null;
	
	


	public RessourceObject(final GamaFile fileName, final IAgent agent, final Color color, Double alpha, final GamaPoint location,
			final GamaPoint dimensions, final GamaPair<Double, GamaPoint> rotate3D,final GamaPair<Double, GamaPoint> rotate3DInit) {
		super(color, alpha);
        this.file = fileName;
		this.agent = agent;
		this.z_layer = z_layer;
		this.color= color;
		this.alpha = alpha;
		this.size = dimensions;
		atLoc = location;
		if (rotate3D != null) {
			rot = Cast.asFloat(null, rotate3D.key);
			ptRot = (GamaPoint) Cast.asPoint(null, rotate3D.value);
		}
		if (rotate3DInit != null) {
			rotInit = Cast.asFloat(null, rotate3DInit.key);
			ptRotInit = (GamaPoint) Cast.asPoint(null, rotate3DInit.value);
		}
		
	}

	@Override
	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return o;
	}

	@Override
	public void unpick() {
		picked = false;
	}

	public void pick() {
		picked = true;
	}

	@Override
	public Color getColor() {
		if ( picked ) { return pickedColor; }
		return super.getColor();
	}

	@Override
	public void draw(final GL2 gl, final ObjectDrawer drawer, final boolean picking) {
		JOGLRenderer renderer = drawer.renderer;
		    //FIXME: To simplify the process picking is not yet taken in account
			
			if (atLoc != null) {
				gl.glTranslated(atLoc.getX(), renderer.yFlag*atLoc.getY(), atLoc.getZ());
			} else {
				gl.glTranslated(this.agent.getLocation().getX(), renderer.yFlag*this.agent.getLocation().getY(), this.agent.getLocation().getZ());	
			}
			if(this.rot != null){
				gl.glRotatef(rot.floatValue() , (float) ptRot.x, (float) ptRot.y, (float) ptRot.z);	
			}
			if(this.rotInit != null){
				gl.glRotatef(rotInit.floatValue() , (float) ptRotInit.x, (float) ptRotInit.y, (float) ptRotInit.z);		
			}
			if(this.size!=null){
					Envelope3D env = (Envelope3D) file.computeEnvelope(null);
				  gl.glScaled(size.x / env.getWidth(), size.y / env.getHeight(), size.z/ env.getHeight());
			}
			
			super.draw(gl, drawer, picking);
			
			
			if(this.size!=null){
				Envelope3D env = (Envelope3D) file.computeEnvelope(null);
				 gl.glScaled(env.getWidth() /size.x, env.getHeight()/size.y, env.getHeight()/size.z);
				 
			}
			if(this.rotInit != null){
				gl.glRotatef(- rotInit.floatValue() , (float) ptRotInit.x, (float) ptRotInit.y, (float) ptRotInit.z);		
			}
			
			if(this.rot != null){
				gl.glRotatef(- rot.floatValue() , (float) ptRot.x, (float) ptRot.y, (float) ptRot.z);	
			}
			
			if (atLoc != null) {
				gl.glTranslated(-atLoc.getX(), - renderer.yFlag*atLoc.getY(), - atLoc.getZ());
			} else {
				gl.glTranslated(-this.agent.getLocation().getX(), - renderer.yFlag*this.agent.getLocation().getY(), - this.agent.getLocation().getZ());	
			}
			
	}

	@Override
	protected Texture computeTexture(GL gl, JOGLRenderer renderer) {
		// TODO Auto-generated method stub
		return null;
	}
}
