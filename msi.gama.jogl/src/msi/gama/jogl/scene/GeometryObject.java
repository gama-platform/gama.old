package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryObject extends AbstractObject implements Cloneable {

	public Geometry geometry;
	public IAgent agent;
	public double z_layer;
	public int layerId;
	public String type;
	public Color border;
	public Boolean isTextured;
	public int angle;
	public double height;
	public boolean rounded;
	public String populationName;

	public GeometryObject(final Geometry geometry, final IAgent agent, final double z_layer, final int layerId,
		final Color color, final Double alpha, final Boolean fill, final Color border, final Boolean isTextured,
		final int angle, final double height, final GamaPoint offset, final GamaPoint scale, final boolean rounded,
		final String type, final String populationName) {
		super(color, offset, scale, alpha);

		if ( type != null && type.compareTo("gridLine") == 0 ) {
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
		this.layerId = layerId;
		this.type = type;
		this.fill = fill;
		this.border = border;
		this.isTextured = false;
		this.angle = angle;
		this.height = height;
		this.rounded = rounded;
		this.populationName = populationName;
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
					renderer.displaySurface.selectAgents(agent, layerId - 1);
				}
			}
			super.draw(drawer, picking);
			renderer.gl.glPopMatrix();
		} else {
			super.draw(drawer, picking);
		}
	}
}
