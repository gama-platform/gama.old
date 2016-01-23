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
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaPair;
import msi.gama.util.file.*;
import msi.gaml.operators.Cast;
import ummisco.gama.opengl.JOGLRenderer;

public class RessourceObject extends AbstractObject implements Cloneable {

	public GamaFile file;
	public IAgent agent;
	public double z_layer;
	// public Color color;
	// public Double alpha;
	public GamaPoint size;
	public GamaPoint atLoc = null;
	Double rot = null;
	GamaPoint ptRot = null;
	Double rotInit = null;
	GamaPoint ptRotInit = null;
	Envelope env;

	public RessourceObject(final GamaFile fileName, final IAgent agent, final Color color, final Double alpha,
		final GamaPoint location, final GamaPoint dimensions, final GamaPair<Double, GamaPoint> rotate3D,
		final GamaPair<Double, GamaPoint> rotate3DInit, final Envelope env) {
		super(color, alpha);
		this.file = fileName;
		this.agent = agent;
		this.z_layer = z_layer;
		// this.color = color;
		// this.alpha = alpha;
		this.size = dimensions;
		if ( file instanceof GamaSVGFile && size == null ) {
			size = new GamaPoint(1, 1, 1);
		}
		atLoc = location;
		this.env = env;
		if ( rotate3D != null ) {
			rot = Cast.asFloat(null, rotate3D.key);
			ptRot = (GamaPoint) Cast.asPoint(null, rotate3D.value);
		}
		if ( rotate3DInit != null ) {
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
		// We first push the matrix so that all translations, etc. are done locally

		// gl.glPushMatrix();

		// If the file is SVG, we translate it to its center
		if ( file instanceof GamaSVGFile ) {
			if ( size != null ) {
				gl.glTranslated(-size.x / 2, renderer.yFlag * size.y / 2, 0);
			} else if ( env != null ) {
				gl.glTranslated(-env.getWidth() / 2, renderer.yFlag * env.getHeight() / 2, 0);
			}
		}

		// If a location is provided we use it otherwise we use that of the agent if it exists
		if ( atLoc != null ) {
			gl.glTranslated(atLoc.getX(), renderer.yFlag * atLoc.getY(), atLoc.getZ());
		} else {
			if ( agent != null ) {
				gl.glTranslated(this.agent.getLocation().getX(), renderer.yFlag * this.agent.getLocation().getY(),
					this.agent.getLocation().getZ());
			}
		}

		// If there is a rotation we apply it
		if ( this.rot != null ) {
			gl.glRotatef(rot.floatValue(), (float) ptRot.x, (float) ptRot.y, (float) ptRot.z);
		}

		// we also apply the initial rotation if there is any
		if ( this.rotInit != null ) {
			gl.glRotatef(rotInit.floatValue(), (float) ptRotInit.x, (float) ptRotInit.y, (float) ptRotInit.z);
		}

		// We then compute the scaling factor to apply
		double factor = 0.0;
		if ( this.size != null && env != null ) {
			if ( file instanceof GamaSVGFile ) {
				factor = Math.min(size.x / env.getWidth(), size.y / env.getHeight());
			} else {
				factor = Math.min(Math.min(size.x / env.getWidth(), size.y / env.getHeight()),
					size.z / ((Envelope3D) env).getDepth());
			}
			gl.glScaled(factor, factor, factor);
		}
		// And apply a color
		if ( getColor() != null ) { // does not work for obj files
			gl.glColor3d(getColor().getRed() / 255.0, getColor().getGreen() / 255.0, getColor().getBlue() / 255.0);
		}

		// Then we draw the geometry itself
		if ( picking ) {
			gl.glPushMatrix();
			gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null /* && !picked */ ) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgent(agent);
				}
			}
			gl.glColor3d(1.0, 0.0, 0.0);

			super.draw(gl, drawer, picking);
			gl.glPopMatrix();
		} else {
			System.out.println("Drawing at " + atLoc);
			super.draw(gl, drawer, picking);
		}

		// and we pop the matrix
		// gl.glPopMatrix();
		if ( this.size != null && env != null ) {
			gl.glScaled(1 / factor, 1 / factor, 1 / factor);
		}

		if ( this.rotInit != null ) {
			gl.glRotatef(-rotInit.floatValue(), (float) ptRotInit.x, (float) ptRotInit.y, (float) ptRotInit.z);
		}

		if ( this.rot != null ) {
			gl.glRotatef(-rot.floatValue(), (float) ptRot.x, (float) ptRot.y, (float) ptRot.z);
		}

		if ( atLoc != null ) {

			gl.glTranslated(-atLoc.getX(), -renderer.yFlag * atLoc.getY(), -atLoc.getZ());
		} else {
			gl.glTranslated(-this.agent.getLocation().getX(), -renderer.yFlag * this.agent.getLocation().getY(),
				-this.agent.getLocation().getZ());
		}
		if ( file instanceof GamaSVGFile ) {
			if ( size != null ) {
				gl.glTranslated(size.x / 2, -renderer.yFlag * size.y / 2, 0);
			} else if ( env != null ) {
				gl.glTranslated(env.getWidth() / 2, -renderer.yFlag * env.getHeight() / 2, 0);
			}
		}

	}

	@Override
	protected Texture computeTexture(final GL gl, final JOGLRenderer renderer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void preload(final GL2 gl, final JOGLRenderer renderer) {
		renderer.getGeometryCache().get(gl, file);
	}
}
