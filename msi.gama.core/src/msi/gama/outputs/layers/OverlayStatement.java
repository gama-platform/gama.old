/*********************************************************************************************
 * 
 *
 * 'OverlayStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ThreadedUpdater;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.OVERLAY, kind = ISymbolKind.LAYER, with_sequence = false, unique_in_context = true)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = { @facet(name = IKeyword.LEFT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.RIGHT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.CENTER, type = IType.NONE, optional = true),
	@facet(name = IKeyword.COLOR, type = { IType.LIST, IType.COLOR }, optional = true) }, omissible = IKeyword.LEFT)
@doc(value="`agents` allows the modeler to display only the agents that fulfill a given condition.", 
	see={IKeyword.DISPLAY,IKeyword.AGENTS,IKeyword.CHART,IKeyword.EVENT,"graphics",IKeyword.GRID_POPULATION,IKeyword.IMAGE,IKeyword.OVERLAY,IKeyword.QUADTREE,IKeyword.POPULATION,IKeyword.TEXT})
public class OverlayStatement extends AbstractLayerStatement implements IOverlayProvider<OverlayInfo> {

	final IExpression left, right, center;
	final IExpression color;
	String leftValue, rightValue, centerValue;
	List<int[]> constantColors;
	IUpdaterTarget<OverlayInfo> overlay;

	public static class OverlayInfo implements ThreadedUpdater.IUpdaterMessage {

		public String[] infos;

		public java.util.List<int[]> colors;

		OverlayInfo(final String[] infos, final java.util.List<int[]> colors) {
			this.infos = infos;
			this.colors = colors;
		}

		@Override
		public boolean isEmpty() {
			return infos == null;
		}
	}

	public OverlayStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		left = getFacet(IKeyword.LEFT);
		right = getFacet(IKeyword.RIGHT);
		center = getFacet(IKeyword.CENTER);
		color = getFacet(IKeyword.COLOR);
		if ( color != null && color.isConst() ) {
			constantColors = computeColors(null);
		}
	}

	private List<int[]> computeColors(final IScope scope) {
		if ( constantColors != null ) { return constantColors; }
		if ( color == null ) { return null; }
		if ( color.getType().id() == IType.LIST ) {
			IList list = Cast.asList(scope, color.value(scope));
			List<int[]> result = new GamaList();
			int i = 0;
			for ( Object o : list ) {
				int[] rgb = computeColor(scope, o);
				result.add(rgb);
				if ( ++i > 2 ) {
					break;
				}
			}
			return result;
		} else {
			int[] rgb = computeColor(scope, color.value(scope));
			return GamaList.with(rgb, rgb, rgb);
		}
	}

	private int[] computeColor(final IScope scope, final Object color) {
		GamaColor c = Cast.asColor(scope, color);
		int[] rgb = new int[] { c.red(), c.green(), c.blue() };
		return rgb;
	}

	@Override
	public short getType() {
		return ILayerStatement.OVERLAY;
	}

	@Override
	protected boolean _init(final IScope scope) {
		return true;
	}

	@Override
	protected boolean _step(final IScope scope) {
		if ( overlay == null ) { return true; }
		leftValue = left == null ? null : Cast.asString(scope, left.value(scope));
		rightValue = right == null ? null : Cast.asString(scope, right.value(scope));
		centerValue = center == null ? null : Cast.asString(scope, center.value(scope));
		overlay.updateWith(new OverlayInfo(getValues(), computeColors(scope)));
		return true;
	}

	private String[] getValues() {
		return new String[] { leftValue, centerValue, rightValue };
	}

	/**
	 * Method setTarget()
	 * @see msi.gama.common.interfaces.IOverlayProvider#setTarget(msi.gama.common.interfaces.IOverlay)
	 */
	@Override
	public void setTarget(final IUpdaterTarget overlay) {
		this.overlay = overlay;
		GAMA.run(new GAMA.InScope.Void() {

			@Override
			public void process(final IScope scope) {
				_step(scope);
			}
		});
	}

}
