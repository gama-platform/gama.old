/*********************************************************************************************
 *
 *
 * 'ILayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IStepable;
import msi.gama.outputs.IDisplayOutput;
import msi.gaml.compilation.ISymbol;

/**
 * The class ILayerStatement. Supports the GAML definition of layers in a
 * display
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface ILayerStatement extends IStepable, ISymbol, Comparable<ILayerStatement> {

	public final static short GRID = 1;
	public final static short AGENTS = 2;
	public final static short SPECIES = 3;
	public final static short TEXT = 4;
	public final static short IMAGE = 5;
	public final static short GIS = 6;
	public final static short CHART = 7;
	public final static short QUADTREE = 8;
	public final static short EVENT = 9;
	public final static short GRAPHICS = 10;
	public final static short OVERLAY = 11;
	public final static short CAMERA = 12;

	public abstract short getType();

	public abstract Double getTransparency();

	void setOrder(Integer i);

	Integer getOrder();

	public abstract Boolean getRefresh();

	public abstract IDisplayLayerBox getBox();

	public Integer getTrace();

	public Boolean getFading();

	public abstract void setRefresh(Boolean refresh);

	public abstract void setTransparency(Double opacity);

	public abstract void setDisplayOutput(IDisplayOutput output);

	void setSelectable(Boolean s);

}