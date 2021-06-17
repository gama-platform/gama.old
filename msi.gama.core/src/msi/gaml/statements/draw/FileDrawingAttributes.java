/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.FileDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;

public class FileDrawingAttributes extends DrawingAttributes {

	public final IAgent agentIdentifier;
	public static final int USE_CACHE = 16;

	public FileDrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaColor color, final GamaColor border, final IAgent agent, final Double lineWidth,
			final boolean isImage, final Boolean lighting) {
		super(size, rotation, location, color, border, lighting);
		this.agentIdentifier = agent;
		setLineWidth(lineWidth);
		setType(isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE);
		setUseCache(true); // by default
	}

	public FileDrawingAttributes(final GamaPoint location, final boolean isImage) {
		super(null, null, location, null, null, null);
		agentIdentifier = null;
		setType(isImage ? IShape.Type.POLYGON : IShape.Type.THREED_FILE);
		setUseCache(true); // by default
	}

	@Override
	public boolean useCache() {
		return isSet(USE_CACHE);
	}

	@Override
	public IAgent getAgentIdentifier() {
		return agentIdentifier;
	}

	public void setUseCache(final boolean b) {
		setFlag(USE_CACHE, b);
	}

}