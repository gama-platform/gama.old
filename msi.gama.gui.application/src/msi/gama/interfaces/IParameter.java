/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import java.util.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 4 juin 2010
 * 
 * @todo Description
 * 
 */
public interface IParameter {

	public abstract String getName();

	public abstract String getTitle();

	public abstract String getCategory();

	public abstract String getUnitLabel();

	public abstract Integer getDefinitionOrder();

	public abstract void setValue(Object value);

	public abstract Object value(IScope iScope) throws GamaRuntimeException;

	public abstract Object value(IAgent agent) throws GamaRuntimeException;

	public abstract void setVal(IScope scope, IAgent agent, Object v) throws GamaRuntimeException;

	public abstract IType type();

	public IType getContentType();

	public String toGaml();

	// public String serializeToGaml();

	public abstract Object getInitialValue();

	public abstract Number getMinValue();

	public abstract Number getMaxValue();

	public abstract List getAmongValue();

	public abstract boolean isEditable();

	public abstract boolean isLabel();

	public abstract boolean allowsTooltip();

	public abstract Number getStepValue();

	public interface Batch extends IParameter {

		public Object value();

		public void setCategory(String cat);

		public void reinitRandomly();

		public abstract Set<Object> neighbourValues() throws GamaRuntimeException;

		public void setEditable(boolean b);

		public boolean canBeExplored();

	}
}
