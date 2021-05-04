
package msi.gama.ext.svgsalamander;

import java.awt.geom.GeneralPath;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class MoveTo extends PathCommand {

	public MoveTo(final boolean isRelative, final float x, final float y) {
		super(x, y, 2, isRelative);
	}

	@Override
	public void appendPath(final GeneralPath path, final BuildHistory hist) {
		float offx = isRelative ? hist.history[0].x : 0f;
		float offy = isRelative ? hist.history[0].y : 0f;
		path.moveTo(x + offx, y + offy);
		hist.setPoint(x + offx, y + offy);
	}

}
