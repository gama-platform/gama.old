package msi.gama.runtime.benchmark;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.CsvWriter;
import msi.gaml.operators.Files;

public class BenchmarkCSVExporter {
	private static final String exportFolder = "benchmarks";

	public void saveAsCSV(final IExperimentPlan experiment, final BenchmarkTree original,
			final Map<String, ScopeRecord> records) {
		try {
			Files.newFolder(experiment.getExperimentScope(), exportFolder);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + exportFolder);
			GAMA.reportError(experiment.getExperimentScope(), e1, false);
			e1.printStackTrace();
			return;
		}
		final TOrderedHashMap<String, ScopeRecord> scopes = new TOrderedHashMap<>(records);
		final String exportFileName = FileUtils.constructAbsoluteFilePath(experiment.getExperimentScope(), exportFolder
				+ "/" + experiment.getModel().getName() + "_benchmark_" + Instant.now().toString() + ".csv", false);

		final List<String> headers = new ArrayList<>();
		final List<List<String>> contents = new ArrayList<>();
		headers.add("Execution");
		scopes.forEach((scope, record) -> {
			headers.add("Time in ms in " + scope);
			headers.add("Invocations in " + scope);
		});
		contents.add(headers);
		original.visitPreOrder(original.getRoot(), (n) -> {
			final IRecord r = n.getData();
			final List<String> line = new ArrayList<>();
			contents.add(line);
			r.fill(line, scopes);
		});

		try (final CsvWriter writer = new CsvWriter(exportFileName)) {
			writer.setDelimiter(';');
			writer.setUseTextQualifier(false);
			for (final List<String> ss : contents) {
				writer.writeRecord(ss.toArray(new String[ss.size()]));
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, experiment.getExperimentScope());
		}
	}

}
