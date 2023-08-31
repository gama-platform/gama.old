package msi.gama.metamodel.topology.grid;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.IList;

/**
 * The Interface IGridAgent.
 */
public interface IGridAgent extends IAgent {

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public GamaColor getColor();

	/**
	 * Sets the color.
	 *
	 * @param color the new color
	 */
	public void setColor(final GamaColor color);

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public int getX();

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public int getY();

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public double getValue();

	/**
	 * Gets the bands.
	 *
	 * @return the bands
	 */
	public IList<Double> getBands();

	/**
	 * Gets the neighbors.
	 *
	 * @param scope the scope
	 * @return the neighbors
	 */
	public IList<IAgent> getNeighbors(IScope scope);

	/**
	 * Sets the value.
	 *
	 * @param d the new value
	 */
	public void setValue(final double d);
}