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
