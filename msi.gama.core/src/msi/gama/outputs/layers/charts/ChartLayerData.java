/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartLayerData.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.util.LinkedHashMap;
import java.util.Map;

import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.LayerData;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class ChartLayerData extends LayerData {

	final Map<String, Double> lastValues = new LinkedHashMap<>();
	Long lastComputeCycle;

	public ChartLayerData(final ILayerStatement def) throws GamaRuntimeException {
		super(def);

	}

}
