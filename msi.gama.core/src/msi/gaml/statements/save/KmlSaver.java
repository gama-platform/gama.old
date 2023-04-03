/*******************************************************************************************************
 *
 * KmlSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.util.Set;

import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaKmlExport;

/**
 * The Class KmlSaver.
 */
public class KmlSaver extends AbstractSaver {

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param fileToSave
	 *            the file to save
	 * @param type
	 *            the type
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave) {
		final Object kml = item.value(scope);
		String path = file.getAbsolutePath();
		if (!(kml instanceof GamaKmlExport export)) return;
		if ("kml".equals(type)) {
			export.saveAsKml(scope, path);
		} else {
			export.saveAsKmz(scope, path);
		}
	}

	@Override
	public Set<String> computeFileTypes() {
		return Set.of("kml", "kmz");
	}

}
