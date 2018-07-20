package msi.gama.runtime.benchmark;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.CsvWriter;
import msi.gama.util.tree.GamaTree.Order;
import msi.gaml.operators.Files;

public class BenchmarkCSVExporter {
	private static final String exportFolder = "benchmarks";

	public void save(final IExperimentPlan experiment, final Benchmark records) {
		final IScope scope = experiment.getExperimentScope();
		try {
			Files.newFolder(scope, exportFolder);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + exportFolder);
			GAMA.reportError(scope, e1, false);
			e1.printStackTrace();
			return;
		}
		final TOrderedHashMap<IScope, Benchmark.ScopeRecord> scopes = new TOrderedHashMap<>(records);
		final String exportFileName = FileUtils.constructAbsoluteFilePath(scope, exportFolder + "/"
				+ experiment.getModel().getName() + "_benchmark_" + Instant.now().toString() + ".csv", false);

		final List<String> headers = new ArrayList<>();
		final List<List<String>> contents = new ArrayList<>();
		headers.add("Execution");
		scopes.forEach((scopeRecord, record) -> {
			headers.add("Time in ms in " + scopeRecord);
			headers.add("Invocations in " + scopeRecord);
		});
		contents.add(headers);
		records.tree.visit(Order.PRE_ORDER, (n) -> {
			final IBenchmarkable r = n.getData();
			final List<String> line = new ArrayList<>();
			contents.add(line);
			line.add(r.getNameForBenchmarks());
			scopes.forEach((scope1, scopeRecord) -> {
				final BenchmarkRecord record1 = scopeRecord.find(r);
				line.add(record1.isUnrecorded() ? "" : String.valueOf(record1.milliseconds));
				line.add(record1.isUnrecorded() ? "" : String.valueOf(record1.times));
			});
		});

		try (final CsvWriter writer = new CsvWriter(exportFileName)) {
			writer.setDelimiter(';');
			writer.setUseTextQualifier(false);
			for (final List<String> ss : contents) {
				writer.writeRecord(ss.toArray(new String[ss.size()]));
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
