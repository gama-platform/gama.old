/**
 * Created by drogoul, 23 févr. 2016
 *
 */
package msi.gama.gui.displays.awt;

import java.awt.Point;
import msi.gama.common.interfaces.IDisplaySurface;

/**
 * Class AbstractJava2DDisplaySurface.
 *
 * @author drogoul
 * @since 23 févr. 2016
 *
 */
public interface IJava2DDisplaySurface extends IDisplaySurface {

	public abstract Point getOrigin();

	public abstract void setOrigin(int i, int j);

	public abstract void repaint();

	public abstract void centerOnDisplayCoordinates(Point point);

	public abstract void selectAgents(int x, int y);

	public abstract void canBeUpdated(boolean b);

	public abstract double getZoomIncrement();

	public abstract double applyZoom(double d);

}
