/*********************************************************************************************
 * 
 * 
 * 'GeometryObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.scene;

import java.awt.Color;
import java.util.List;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryObject extends AbstractObject implements Cloneable {

	public Geometry geometry;
	public IAgent agent;
	public double z_layer;
	public IShape.Type type; // see IShape.Type constants
	public Color border;
	public Boolean isTextured;
	public List<String> textureFileNames;
	public double height;
	public boolean rounded;
	private final MyTexture[] textures;

	public GeometryObject(final Geometry geometry, final IAgent agent, final double z_layer, final int layerId,
		final Color color, final Double alpha, final Boolean fill, final Color border, final Boolean isTextured,
		final List<String> textureFileNames, final int angle, final double height, final boolean rounded,
		final IShape.Type type) {
		super(color, alpha);

		if ( type == IShape.Type.GRIDLINE ) {
			this.fill = false;
			setZ_fighting_id((double) layerId);
		}
		// FIXME:Need to check that
		/*
		 * if (type.compareTo("env") == 0){
		 * setZ_fighting_id(0.1);
		 * }
		 */
		/*
		 * The z_fight value must be a unique value so the solution has been to make the hypothesis that
		 * a layer has less than 1 000 000 agent to make a unique z-fighting value per agent.
		 */
		if ( agent != null && agent.getLocation().getZ() == 0 && height == 0 ) {
			Double z_fight = Double.parseDouble(layerId + "." + agent.getIndex());
			setZ_fighting_id(z_fight);
		}

		this.geometry = geometry;
		this.agent = agent;
		this.z_layer = z_layer;
		this.type = type;
		this.fill = fill;
		this.border = border;
		this.isTextured = isTextured;
		this.textureFileNames = textureFileNames;
		if ( textureFileNames == null || textureFileNames.isEmpty() ) {
			textures = null;
		} else {
			textures = new MyTexture[textureFileNames.size()];
		}
		this.height = height;
		this.rounded = rounded;
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
	public void draw(final ObjectDrawer drawer, final boolean picking) {
		if ( picking ) {
			JOGLAWTGLRenderer renderer = drawer.renderer;
			renderer.gl.glPushMatrix();
			renderer.gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				if ( agent != null /* && !picked */) {
					renderer.setPicking(false);
					pick();
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgents(agent);
				}
			}
			super.draw(drawer, picking);
			renderer.gl.glPopMatrix();
		} else {
			super.draw(drawer, picking);
		}
	}

	@Override
	protected MyTexture computeTexture(final JOGLAWTGLRenderer renderer) {
		return getTexture(renderer, 0);
	}

	public MyTexture getTexture(final JOGLAWTGLRenderer renderer, final int order) {
		if ( textures == null ) { return null; }
		if ( order < 0 || order > textures.length - 1 ) { return null; }
		if ( textures[order] == null ) {
			textures[order] = computeTexture(renderer, order);
		}
		return textures[order];
	}

	private MyTexture computeTexture(final JOGLAWTGLRenderer renderer, final int order) {
		return renderer.getScene().createTexture(textureFileNames.get(order), false);
	}

	public boolean hasTextures() {
		return textures != null && textures.length > 1;
	}

	public boolean isPie3D() {
		return false;
	}
}
