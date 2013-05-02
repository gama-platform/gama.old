package msi.gama.jogl.utils.GraphicDataType;

import java.awt.Color;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import com.vividsolutions.jts.geom.Geometry;

public class MyJTSGeometry implements Cloneable {

	public Geometry geometry;

	public IAgent agent;

	public double z_layer;

	public int layerId;

	public Color color;

	public double alpha;

	public String type;

	public Boolean fill = true;

	public Color border;

	public Boolean isTextured;

	public int angle;

	public double height;

	public double altitude;

	public boolean rounded;

	public GamaPoint offSet;

	public GamaPoint scale;

	public MyJTSGeometry(Geometry geometry, IAgent agent, double z_layer, int layerId, Color color, double alpha,
		Boolean fill, Color border, Boolean isTextured, int angle, double height, GamaPoint offSet, GamaPoint scale,
		boolean rounded, String type) {
		this.geometry = geometry;
		this.agent = agent;
		this.z_layer = z_layer;
		this.layerId = layerId;
		this.color = color;
		this.alpha = alpha;
		this.type = type;
		this.fill = fill;
		this.border = border;
		this.isTextured = false;
		this.angle = angle;
		this.height = height;
		this.altitude = 0.0f;
		this.offSet = offSet;
		this.scale = scale;
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
}
