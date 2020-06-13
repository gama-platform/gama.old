/*******************************************************************************************************
 *
 * msi.gama.outputs.MonitorOutput.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.file.csv.CsvWriter;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class MonitorOutput.
 *
 * @author drogoul
 */
@symbol (
		name = IKeyword.MONITOR,
		kind = ISymbolKind.OUTPUT,
		with_sequence = false,
		concept = { IConcept.MONITOR })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.LABEL,
				optional = false,
				doc = @doc ("identifier of the monitor")),
				@facet (
						name = IKeyword.REFRESH_EVERY,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "Allows to refresh the monitor every n time steps (default is 1)",
								deprecated = "Use refresh: every(n) instead")),
				@facet (
						name = IKeyword.COLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Indicates the (possibly dynamic) color of this output (default is a light gray)")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates the condition under which this output should be refreshed (default is true)")),
				@facet (
						name = IKeyword.VALUE,
						type = IType.NONE,
						optional = false,
						doc = @doc ("expression that will be evaluated to be displayed in the monitor")) },
		omissible = IKeyword.NAME)
@inside (
		symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@doc (
		value = "A monitor allows to follow the value of an arbitrary expression in GAML.",
		usages = { @usage (
				value = "An example of use is:",
				examples = @example (
						value = "monitor \"nb preys\" value: length(prey as list) refresh_every: 5;  ",
						isExecutable = false)) })
public class MonitorOutput extends AbstractValuedDisplayOutput {
	private static String monitorFolder = "monitors";
	protected IExpression colorExpression = null;
	protected GamaColor color = null;
	protected GamaColor constantColor = null;
	protected List<Object> history;

	public MonitorOutput(final IDescription desc) {
		super(desc);
		setColor(getFacet(IKeyword.COLOR));

	}

	/**
	 * @param facet
	 */
	private void setColor(final IExpression facet) {
		colorExpression = facet;
		if (facet != null && facet.isConst()) {
			constantColor = Types.COLOR.cast(null, facet.getConstValue(), null, false);
		}
	}

	public MonitorOutput(final IScope scope, final String name, final String expr) {
		super(DescriptionFactory.create(IKeyword.MONITOR, IKeyword.VALUE, expr, IKeyword.NAME,
				name == null ? expr : name));
		setScope(scope.copy("in monitor '" + expr + "'"));
		setNewExpressionText(expr);
		if (getScope().init(this).passed()) {
			getScope().getSimulation().addOutput(this);
			setPaused(false);
			open();
		}
	}

	@Override
	public String getViewId() {
		return IGui.MONITOR_VIEW_ID;
	}

	@Override
	public String getId() {
		return getViewId() + ":" + getName();
	}

	@Override
	public boolean init(final IScope scope) {
		super.init(scope);
		if (colorExpression == null) {
			final ITopLevelAgent sim = scope.getRoot();
			if (sim != null) {
				constantColor = sim.getColor();
			}
		}
		return true;
	}

	@Override
	public boolean step(final IScope scope) {
		getScope().setCurrentSymbol(this);
		if (getScope().interrupted()) { return false; }
		if (getValue() != null) {
			try {
				lastValue = getValue().value(getScope());
				if (history != null) {
					history.add(lastValue);
				}
			} catch (final GamaRuntimeException e) {
				lastValue = ItemList.ERROR_CODE + e.getMessage();
			}
		} else {
			lastValue = null;
		}
		if (constantColor == null) {
			if (colorExpression != null) {
				color = Cast.asColor(scope, colorExpression.value(scope));
			}
		}
		return true;
	}

	public GamaColor getColor() {
		return constantColor == null ? color : constantColor;
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public String getName() {
		String result = super.getName();
		if (result == null) {
			result = getExpressionText();
		}
		return result;
	}

	@Override
	protected void setValue(final IExpression value) {
		if (history != null) {
			history.clear();
			history = null;
		}
		super.setValue(value);
		if (value != null) {
			final IType<?> t = value.getGamlType();
			if (t.isNumber() || t.isContainer() && t.getContentType().isNumber()) {
				history = GamaListFactory.create(t);
			}
		}
	}

	public void saveHistory() {
		if (getScope() == null) { return; }
		if (history == null || history.isEmpty()) { return; }
		Files.newFolder(getScope(), monitorFolder);
		String file =
				monitorFolder + "/" + "monitor_" + getName() + "_cycle_" + getScope().getClock().getCycle() + ".csv";
		file = FileUtils.constructAbsoluteFilePath(getScope(), file, false);
		try (final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				final CsvWriter w = new CsvWriter(bw, CsvWriter.Letters.COMMA)) {
			for (final Object o : history) {
				String[] strings = null;
				if (o instanceof Number) {
					strings = new String[] { o.toString() };
				} else if (o instanceof List) {
					final List<?> l = (List<?>) o;
					strings = new String[l.size()];
					for (int i = 0; i < strings.length; i++) {
						strings[i] = l.get(i).toString();
					}
				}
				w.writeRecord(strings);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
