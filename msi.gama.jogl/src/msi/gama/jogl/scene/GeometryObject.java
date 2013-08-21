package msi.gama.jogl.scene;

import java.awt.Color;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import com.vividsolutions.jts.geom.Geometry;

public class GeometryObject extends AbstractObject implements Cloneable {

	static int index = 0;
	static Color pickedColor = Color.red;

	public Geometry geometry;
	public IAgent agent;
	public double z_layer;
	public int layerId;
	public String type;
	public Boolean fill = true;
	public Color border;
	public Boolean isTextured;
	public int angle;
	public double height;
	public double altitude;
	public boolean rounded;
	public int pickingIndex = index++;
	public boolean picked = false;
	private Color previousColor = null;

	public GeometryObject(final Geometry geometry, final IAgent agent, final double z_layer, final int layerId,
		final Color color, final Double alpha, final Boolean fill, final Color border, final Boolean isTextured,
		final int angle, final double height, final GamaPoint offset, final GamaPoint scale, final boolean rounded,
		final String type) {
		super(color, offset, scale, alpha, layerId);
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
		this.altitude = 0.0f;
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
		// picked = false;
		color = previousColor;
	}

	public void pick() {
		// picked = true;
		previousColor = color;
		color = pickedColor;
	}

	@Override
	public void draw(final ObjectDrawer drawer, final boolean picking) {
		if ( picking ) {
			JOGLAWTGLRenderer renderer = drawer.renderer;
			renderer.gl.glPushMatrix();
			renderer.gl.glLoadName(pickingIndex);
			if ( renderer.pickedObjectIndex == pickingIndex ) {
				renderer.setPickedObjectIndex(-1);
				if ( agent != null /* && !picked */) {
					pick();
					if ( renderer.currentPickedObject != null ) {
						renderer.currentPickedObject.unpick();
					}
					renderer.currentPickedObject = this;
					renderer.displaySurface.selectAgents(0, 0, agent, layerId - 1);
					// unpick();
				}
			}
			super.draw(drawer, picking);
			renderer.gl.glPopMatrix();
		} else {
			super.draw(drawer, picking);
		}
	}

}
