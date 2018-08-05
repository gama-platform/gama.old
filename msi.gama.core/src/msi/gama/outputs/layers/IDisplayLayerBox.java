/*********************************************************************************************
 *
 * 'IDisplayLayerBox.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

/**
 * The class IDisplayLayerBox.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface IDisplayLayerBox {

	public abstract void compute(final IScope sim) throws GamaRuntimeException;

	public abstract void setTransparency(final IScope sim, final IExpression t) throws GamaRuntimeException;

	public abstract void setPosition(final IScope sim, final IExpression p) throws GamaRuntimeException;

	public abstract void setSize(final IScope sim, final IExpression e) throws GamaRuntimeException;

	public abstract void setRefresh(final IScope sim, final IExpression r) throws GamaRuntimeException;

	public abstract void setTrace(IScope scope, final IExpression t);

	public abstract void setFading(IScope scope, final IExpression f);

	public abstract void setTransparency(final double f);

	public abstract void setSize(final ILocation p);

	public abstract void setSize(final double width, final double height, final double depth);

	public abstract boolean isRelativePosition();

	public abstract boolean isRelativeSize();

	public abstract void setPosition(final ILocation p);

	public abstract void setPosition(final double x, final double y, final double z);

	public abstract void setRefresh(final Boolean r);

	public abstract Double getTransparency();

	public abstract ILocation getPosition();

	public abstract ILocation getSize();

	public abstract Boolean getRefresh();

	public abstract Integer getTrace();

	public abstract Boolean getFading();

	public abstract Boolean isSelectable();

	public abstract void setSelectable(IScope s, IExpression r);

	public abstract void setSelectable(Boolean b);

	public abstract void setConstantBoundingBox(boolean b);

}