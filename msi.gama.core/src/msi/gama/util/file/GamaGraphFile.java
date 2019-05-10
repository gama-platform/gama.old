/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGraphFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IContainerType;

public class GamaGraphFile extends GamaFile<IGraph<?, ?>, Object> {

	public GamaGraphFile(final IScope scope, final String pn) throws GamaRuntimeException {
		super(scope, pn);
		// TODO Auto-generated constructor stub
	}

	public GamaGraphFile(final IScope scope, final String pathName, final IGraph<?, ?> container) {
		super(scope, pathName, container);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContainerType<?> getGamlType() {
		return null;
	}

}
