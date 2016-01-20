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
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GLModel;

public class RessourceObject extends AbstractObject implements Cloneable {

	public GamaFile file;
	public IAgent agent;
	public double z_layer;
	public Color color;
	public Double alpha;
	public GamaPoint size;
	public GamaPair<Double, GamaPoint> rotate3D = null;
	public GamaPair<Double, GamaPoint> rotate3DInit = null;
	public GamaPoint atLoc = null;
	


	public RessourceObject(final GamaFile fileName, final IAgent agent, final Color color, Double alpha, final GamaPoint location,
			final GamaPoint dimensions, final GamaPair<Double, GamaPoint> rotate3D,final GamaPair<Double, GamaPoint> rotate3DInit) {
		super(color, alpha);
        this.file = fileName;
		this.agent = agent;
		this.z_layer = z_layer;
		this.color= color;
		this.alpha = alpha;
		this.size = dimensions;
		this.rotate3D = rotate3D;
		this.rotate3DInit = rotate3DInit;
		atLoc = location;
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
		
			if (this.rotate3D != null || this.rotate3DInit != null) {
				gl.glTranslated(this.agent.getLocation().getX(), renderer.yFlag*this.agent.getLocation().getY(), this.agent.getLocation().getZ());
			}
			if(this.rotate3D != null){
				gl.glRotatef(this.rotate3D.key.floatValue() , (float) this.rotate3D.value.x, (float) this.rotate3D.value.y, (float) this.rotate3D.value.z);	
			}
			if(this.rotate3DInit != null){
				gl.glRotatef(this.rotate3DInit.key.floatValue() , (float) this.rotate3DInit.value.x, (float) this.rotate3DInit.value.y, (float) this.rotate3DInit.value.z);	
			}
			if(this.size!=null){
				  gl.glScaled(size.x, size.x, size.x);
			}
			super.draw(gl, drawer, picking);
			if(this.size!=null){
				  gl.glScaled(1/size.x, 1/size.x, 1/size.x);
			}	
			if(this.rotate3DInit != null){
				((GL2) gl).glRotatef(-this.rotate3DInit.key.floatValue() , (float) this.rotate3DInit.value.x, (float) this.rotate3DInit.value.y, (float) this.rotate3DInit.value.z);				
			}
			if(this.rotate3D != null){
				((GL2) gl).glRotatef(-this.rotate3D.key.floatValue() , (float) this.rotate3D.value.x, (float) this.rotate3D.value.y, (float) this.rotate3D.value.z);				
			}
			if (this.rotate3D != null || this.rotate3DInit != null) {
				((GL2) gl).glTranslated(-this.agent.getLocation().getX(), -renderer.yFlag*this.agent.getLocation().getY(), -this.agent.getLocation().getZ());
			}
	}

	@Override
	protected Texture computeTexture(GL gl, JOGLRenderer renderer) {
		// TODO Auto-generated method stub
		return null;
	}
}
