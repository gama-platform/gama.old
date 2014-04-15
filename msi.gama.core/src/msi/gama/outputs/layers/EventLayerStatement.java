/*********************************************************************************************
 * 
 *
 * 'EventLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * Written by Marilleau Modified on 16 novembre 2012
 * @todo Description
 * 
 */
@symbol(name = IKeyword.EVENT, kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = { IKeyword.DISPLAY })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, values = { "mouse_up", "mouse_down", "mouse_drag" }, optional = false),
	@facet(name = IKeyword.ACTION, type = IType.STRING, optional = false) }, omissible = IKeyword.NAME)
public class EventLayerStatement extends AbstractLayerStatement {

	public static int MOUSE_PRESSED = 0;
	public static int MOUSE_RELEASED = 1;
	public static int MOUSE_DRAGGED = 2;

	public EventLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(/* context, */desc);
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		IExpression eventType = getFacet(IKeyword.NAME);
		IExpression actionName = getFacet(IKeyword.ACTION);
		return true;
	}

	@Override
	public short getType() {
		return EVENT;
	}

	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
		return "Event layer: " + this.getFacet(IKeyword.NAME).literalValue();
	}

	/**
	 * Method _step()
	 * @see msi.gama.outputs.layers.AbstractLayerStatement#_step(msi.gama.runtime.IScope)
	 */
	@Override
	protected boolean _step(final IScope scope) {
		return true;
	}
}
