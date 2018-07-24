package msi.gama.util.file;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IContainerType;
import msi.gaml.types.Types;

public class GamaGraphMLFile extends GamaGraphFile {

	public GamaGraphMLFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	public GamaGraphMLFile(final IScope scope, final String pathName, final IGraph container) {
		super(scope, pathName, container);
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContainerType getGamlType() {
		return Types.GRAPH;
	}

}
