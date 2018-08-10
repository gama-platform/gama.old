package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file (
		name = "saved_simulation",
		extensions = { "gsim", "gasim" },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE},
		doc = @doc ("Represents a saved simulation file. The internal contents is a string at index 0"))
// TODO : this type needs to be improved .... 
@SuppressWarnings ({ "unchecked" })
public class GamaSavedSimulationFile extends GamaFile<IList<String>, String> {
	
	public static class SavedSimulationInfo extends GamaFileMetaData {

		public String savedModel;
		public String savedExperiment;
		public int savedCycle;

		public SavedSimulationInfo(final String fileName, final long modificationStamp) {
			super(modificationStamp);
		
			final File f = new File(fileName);
			final GamaSavedSimulationFile simulationFile = new GamaSavedSimulationFile(null, f.getAbsolutePath(), false);

			
			savedModel = simulationFile.getModelName();
			savedExperiment = simulationFile.getExperiment();
			savedCycle = simulationFile.getCycle();
		}

		public SavedSimulationInfo(final String propertyString) {
			super(propertyString);
			
			final String[] segments = split(propertyString);
			savedModel = segments[1];
			savedExperiment = segments[2];
			savedCycle = Integer.valueOf(segments[3]);
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Model: ").append(savedModel).append(Strings.LN);
			sb.append("Experiment: ").append(savedExperiment).append(Strings.LN);
			sb.append("Cycle: ").append(savedCycle).append(Strings.LN);
			
			return sb.toString();
		}

		@Override
		public String getSuffix() {
			return "" + savedModel + " | " + "Experiment: " + savedExperiment + " | " + "Cycle: " + savedCycle;
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			sb.append(savedModel).append(SUFFIX_DEL);
			sb.append("Experiment: ").append(savedExperiment).append(SUFFIX_DEL);
			sb.append("Cycle: ").append(savedCycle);
		}

		/**
		 * @return
		 */
		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + savedModel + DELIMITER + savedExperiment + DELIMITER + savedCycle;
		}
	}	


	private int savedCycle;
	private String savedExperiment;
	private String savedModelPath;	
	private String savedModelName;

	public GamaSavedSimulationFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, true);
	}	

	public GamaSavedSimulationFile(final IScope scope, final String pathName, final IList<String> text) {
		super(scope, pathName, text);
		fillBuffer(scope);		
	}		
	
	public GamaSavedSimulationFile(final IScope scope, final String pathName, boolean fillBuffer) throws GamaRuntimeException {
		super(scope, pathName);
				
		if(fillBuffer) {
			fillBuffer(scope);		
		} else {
			metadataOnly(scope);			
		}
	}

	private void metadataOnly(final IScope scope) {		
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			readMetada(in);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}		
	}

	private void readMetada(BufferedReader in) throws IOException {
		savedModelPath = in.readLine();
		savedExperiment = in.readLine() ;
		savedCycle = Integer.parseInt(in.readLine());		
		
		savedModelName = (new File(savedModelPath)).getName();
	}
	
	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

	public String getModelPath() { return savedModelPath; }
	public String getModelName() {return savedModelName; }
	public String getExperiment() { return savedExperiment; }
	public int getCycle() { return savedCycle; }
	
	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		final StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for (final String s : getBuffer().iterable(scope)) {
			sb.append(s).append(Strings.LN);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			final StringBuilder sb = new StringBuilder();	
			
			// manage the metadata (and thus remove the first metadata lines)
			readMetada(in);
			
			// Continue with the core of the file
			String str = in.readLine();
			while (str != null) {
				sb.append(str);
				sb.append(System.lineSeparator());
				str = in.readLine();
			}
			final IList<String> contents = GamaListFactory.create(Types.STRING);
			contents.add(sb.toString());	
			setBuffer(contents);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		if (getBuffer() != null && !getBuffer().isEmpty()) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(scope)))) {
				for (final String s : getBuffer()) {
					writer.append(s).append(Strings.LN);
				}
				writer.flush();
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}

	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}
		
}
