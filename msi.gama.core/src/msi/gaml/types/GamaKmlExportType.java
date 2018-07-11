/*********************************************************************************************
 *
 * 'GamaRegressionType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;


@type(name = "kml", id = IType.KML, wraps = {
		GamaKmlExport.class }, kind = ISymbolKind.Variable.REGULAR, concept = { IConcept.TYPE })
public class GamaKmlExportType extends GamaType<GamaKmlExport> {

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public GamaKmlExport cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof GamaKmlExport) {
			return (GamaKmlExport) obj;
		}
		return null;
	}

	@Override
	public GamaKmlExport getDefault() {
		return new GamaKmlExport(); 
	}

}
