/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.ILayerStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IStepable;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;

/**
 * The class ILayerStatement. Supports the GAML definition of layers in a display
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
public interface ILayerStatement extends IStepable, ISymbol, Comparable<ILayerStatement> {

	public static enum LayerType {

		GRID(IKeyword.GRID),
		AGENTS(IKeyword.AGENTS),
		GRID_AGENTS("grid_agents"),
		SPECIES(IKeyword.SPECIES),
		IMAGE(IKeyword.IMAGE),
		GIS(IKeyword.GIS),
		CHART(IKeyword.CHART),
		EVENT(IKeyword.EVENT),
		GRAPHICS(IKeyword.GRAPHICS),
		OVERLAY(IKeyword.OVERLAY),
		CAMERA(IKeyword.CAMERA),
		LIGHT("light");

		private final String name;

		LayerType(final String s) {
			name = s;
		}

		static LayerType get(final String s) {
			for (final LayerType lt : values()) {
				if (lt.name.equals(s)) { return lt; }
			}
			return null;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public abstract LayerType getType(LayeredDisplayOutput output);

	public abstract void setDisplayOutput(IDisplayOutput output);

	public abstract IExpression getRefreshFacet();

}