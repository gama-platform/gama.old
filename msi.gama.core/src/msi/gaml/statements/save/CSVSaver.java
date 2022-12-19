package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.statements.SaveStatement;
import msi.gaml.types.IType;

public class CSVSaver {

	public void save(final IScope scope, final IExpression item, final OutputStream os, final boolean header)
			throws GamaRuntimeException {
		if (os == null) return;
		save(scope, new OutputStreamWriter(os), header, item);
	}

	public void save(final IScope scope, final IExpression item, final File f, final boolean header)
			throws GamaRuntimeException, IOException {
		if (f == null) return;
		save(scope, new FileWriter(f, true), header, item);
	}

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
			if (sd != null) {
				final Collection<String> attributeNames = sd.getAttributeNames();
				attributeNames.removeAll(SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES);
				if (header) {
					fw.write("cycle;name;location.x;location.y;location.z");
					for (final String v : attributeNames) { fw.write(";" + v); }
					fw.write(Strings.LN);
				}
				for (final Object obj : values) {
					if (obj instanceof IAgent) {
						final IAgent ag = Cast.asAgent(scope, obj);
						fw.write(scope.getClock().getCycle() + ";" + ag.getName().replace(';', ',') + ";"
								+ ag.getLocation().getX() + ";" + ag.getLocation().getY() + ";"
								+ ag.getLocation().getZ());
						for (final String v : attributeNames) {
							String val = Cast.toGaml(ag.getDirectVarValue(scope, v)).replace(';', ',');
							if (val.startsWith("'") && val.endsWith("'")
									|| val.startsWith("\"") && val.endsWith("\"")) {
								val = val.substring(1, val.length() - 1);
							}
							fw.write(";" + val);
						}
						fw.write(Strings.LN);
					}

				}
			} else {
				if (header) {
					fw.write(item.serialize(true).replace("]", "").replace("[", ""));
					fw.write(Strings.LN);
				}
				if (itemType.id() == IType.MATRIX) {
					final String[] tmpValue = value.toString().replace("[", "").replace("]", "").split(",");
					for (int i = 0; i < tmpValue.length; i++) {
						if (i > 0) { fw.write(','); }
						fw.write(toCleanString(tmpValue[i]));
					}
				} else {
					final int size = values.size();
					for (int i = 0; i < size; i++) {
						if (i > 0) { fw.write(','); }
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
		String val = Cast.toGaml(o).replace(';', ',');
		if (val.startsWith("'") && val.endsWith("'") || val.startsWith("\"") && val.endsWith("\"")) {
			val = val.substring(1, val.length() - 1);
		}

		if (o instanceof String) {
			val = val.replace("\\'", "'");
			val = val.replace("\\\"", "\"");

		}
		return val;
	}

}
