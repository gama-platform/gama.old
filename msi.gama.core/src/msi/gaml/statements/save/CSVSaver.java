/*******************************************************************************************************
 *
 * CSVSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.csv.AbstractCSVManipulator;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.statements.SaveStatement;
import msi.gaml.types.IType;

/**
 * The Class CSVSaver.
 */
public class CSVSaver extends AbstractSaver {

	// TODO Attributes not used for the moment ?

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param os
	 *            the os
	 * @param header
	 *            the header
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public void save(final IScope scope, final IExpression item, final OutputStream os, final boolean header)
			throws GamaRuntimeException {
		if (os == null) return;
		save(scope, new OutputStreamWriter(os), header, item);
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param f
	 *            the f
	 * @param header
	 *            the header
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave)
			throws GamaRuntimeException, IOException {
		save(scope, new FileWriter(file, true), addHeader, item);
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param fw
	 *            the fw
	 * @param header
	 *            the header
	 * @param item
	 *            the item
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	private void save(final IScope scope, final Writer fw, final boolean header, final IExpression item)
			throws GamaRuntimeException {
		try (fw) {
			final IType itemType = item.getGamlType();
			final SpeciesDescription sd;
			if (itemType.isAgentType()) {
				sd = itemType.getSpecies();
			} else if (itemType.getContentType().isAgentType()) {
				sd = itemType.getContentType().getSpecies();
			} else {
				sd = null;
			}
			final Object value = item.value(scope);
			final IList values =
					itemType.isContainer() ? Cast.asList(scope, value) : GamaListFactory.create(scope, itemType, value);
			if (values.isEmpty()) return;
			char del = AbstractCSVManipulator.getDefaultDelimiter();
			if (sd != null) {
				final Collection<String> attributeNames = sd.getAttributeNames();
				attributeNames.removeAll(SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES);
				if (header) {
					fw.write("cycle" + del + "name;location.x" + del + "location.y" + del + "location.z");
					for (final String v : attributeNames) { fw.write(del + v); }
					fw.write(Strings.LN);
				}
				for (final Object obj : values) {
					if (obj instanceof IAgent) {
						final IAgent ag = Cast.asAgent(scope, obj);
						fw.write(scope.getClock().getCycle() + del + ag.getName().replace(';', ',') + del
								+ ag.getLocation().getX() + del + ag.getLocation().getY() + del
								+ ag.getLocation().getZ());
						for (final String v : attributeNames) {
							String val = StringUtils.toGaml(ag.getDirectVarValue(scope, v), false).replace(';', ',');
							if (val.startsWith("'") && val.endsWith("'")
									|| val.startsWith("\"") && val.endsWith("\"")) {
								val = val.substring(1, val.length() - 1);
							}
							fw.write(del + val);
						}
						fw.write(Strings.LN);
					}

				}
			} else {
				if (header) {
					fw.write(item.serializeToGaml(true).replace("]", "").replace("[", "").replace(',', del));
					fw.write(Strings.LN);
				}
				if (itemType.id() == IType.MATRIX) {
					GamaMatrix<?> matrix = (GamaMatrix) value;
					matrix.rowByRow(scope, v -> fw.write(toCleanString(v)), () -> fw.write(del),
							() -> fw.write(Strings.LN));
				} else {
					final int size = values.size();
					for (int i = 0; i < size; i++) {
						if (i > 0) { fw.write(del); }
						fw.write(toCleanString(values.get(i)));
					}
				}
				fw.write(Strings.LN);
			}

		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

	/**
	 * To clean string.
	 *
	 * @param o
	 *            the o
	 * @return the string
	 */
	private String toCleanString(final Object o) {
		// Verify this (shouldn't we use AbstractCSVManipulator.getDefaultDelimiter() ?)
		String val = StringUtils.toGaml(o, false).replace(';', ',');
		if (val.startsWith("'") && val.endsWith("'") || val.startsWith("\"") && val.endsWith("\"")) {
			val = val.substring(1, val.length() - 1);
		}

		if (o instanceof String) {
			val = val.replace("\\'", "'");
			val = val.replace("\\\"", "\"");

		}
		return val;
	}

	@Override
	protected Set<String> computeFileTypes() {
		return Set.of("csv");
	}

}
