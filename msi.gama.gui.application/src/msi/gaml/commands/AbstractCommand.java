/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gaml.commands;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.Facet;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 */

public abstract class AbstractCommand extends Symbol implements ICommand {

	public AbstractCommand(final IDescription desc) {
		super(desc);
	}

	@Override
	public Object executeOn(final IScope stack) throws GamaRuntimeException {
		Object result = null;
		try {
			result = privateExecuteIn(stack);
		} catch (GamaRuntimeException e) {
			e.addContext(this);
			if ( description != null ) {
				e.addSource(description.getSourceInformation());
			}
			if ( e.isWarning() ) {
				GAMA.reportError(e);
				return null;
			}
			throw e;
		}
		return result;
	}

	protected abstract Object privateExecuteIn(IScope stack) throws GamaRuntimeException;

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	@Override
	public IType getReturnType() {
		return null;
	}

	@Override
	public IType getReturnContentType() {
		return null;
	}

	@Override
	public String toString() {
		if ( name == null ) {
			String k = getLiteral(ISymbol.KEYWORD);
			String n = getLiteral(ISymbol.NAME);
			setName(k == null ? "" : k + " " + n == null ? "" : n);
		}
		return name + description.getFacets();
	}

	@Override
	public String toGaml() {
		String k = getLiteral(ISymbol.KEYWORD);
		StringBuilder sb = new StringBuilder();
		sb.append(k).append(' ');
		for ( Map.Entry<String, Facet> e : description.getFacets().entrySet() ) {
			if ( !e.getKey().equals(ISymbol.KEYWORD) ) {
				sb.append(e.getKey()).append(": ").append(e.getValue().getExpression().toGaml())
					.append(" ");
			}
		}
		return sb.toString();
	}
}
