/*******************************************************************************************************
 *
 * GamaSavedSimulationFile.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaFileMetaData;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaSavedSimulationFile.
 */
@file (
		name = "saved_simulation",
		extensions = { "gsim", "gasim" },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		doc = @doc ("Represents a saved simulation file. The internal contents is a string at index 0"))
// TODO : this type needs to be improved ....
@SuppressWarnings ({ "unchecked" })
public class GamaSavedSimulationFile extends GamaFile<IList<String>, String> {

	/**
	 * The Class SavedSimulationInfo.
	 */
	public static class SavedSimulationInfo extends GamaFileMetaData { // NO_UCD (unused code)

		/** The saved model. */
 public String savedModel;
		
		/** The saved experiment. */
		public String savedExperiment;
		
		/** The saved cycle. */
		public int savedCycle;

		/**
		 * Instantiates a new saved simulation info.
		 *
		 * @param fileName the file name
		 * @param modificationStamp the modification stamp
		 */
		public SavedSimulationInfo(final String fileName, final long modificationStamp) {
			super(modificationStamp);

			final File f = new File(fileName);
			final GamaSavedSimulationFile simulationFile =
					new GamaSavedSimulationFile(null, f.getAbsolutePath(), false);

			savedModel = simulationFile.getModelName();
			savedExperiment = simulationFile.getExperiment();
			savedCycle = simulationFile.getCycle();
		}

		/**
		 * Instantiates a new saved simulation info.
		 *
		 * @param propertyString the property string
		 */
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
			return super.toPropertyString() + DELIMITER + savedModel + DELIMITER + savedExperiment + DELIMITER
					+ savedCycle;
		}
	}

	/** The saved cycle. */
	private int savedCycle;
	
	/** The saved experiment. */
	private String savedExperiment;
	
	/** The saved model path. */
	private String savedModelPath;
	
	/** The saved model name. */
	private String savedModelName;

	/**
	 * Instantiates a new gama saved simulation file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc ("Constructor for saved simulation files: read the metadata and content.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, true);
	}

	/**
	 * Instantiates a new gama saved simulation file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param contents the contents
	 */
	@doc ("Constructor for saved simulation files from a list of agents: it is used with aim of saving a simulation agent.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName, final IList<IAgent> contents) {
		super(scope, pathName, null);
		final IAgent agent = contents.firstValue(scope);

		// Set first the metadata
		setMetadata(scope);

		// Set the buffer
		final String serializedAgent = ReverseOperators.serializeAgent(scope, agent);
		final IList<String> c = GamaListFactory.create();
		c.add(serializedAgent);

		setContents(c);
	}

	/**
	 * Instantiates a new gama saved simulation file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param fillBuffer the fill buffer
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc ("Constructor for saved simulation files: read the metadata. If and only if the boolean operand is true, the content of the file is read.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName, final boolean fillBuffer)
			throws GamaRuntimeException {
		super(scope, pathName);

		if (fillBuffer) {
			fillBuffer(scope);
		} else {
			metadataOnly(scope);
		}
	}

	/**
	 * Sets the metadata.
	 *
	 * @param scope the new metadata
	 */
	private void setMetadata(final IScope scope) {
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();
		savedCycle = simAgt.getClock().getCycle();
		savedModelPath = expAgt.getModel().getFilePath();
		savedExperiment = (String) expAgt.getSpecies().getFacet(IKeyword.NAME).value(scope);
		savedModelName = new File(savedModelPath).getName();
	}

	/**
	 * Metadata only.
	 *
	 * @param scope the scope
	 */
	private void metadataOnly(final IScope scope) {
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			readMetada(in);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Read metada.
	 *
	 * @param in the in
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void readMetada(final BufferedReader in) throws IOException {
		savedModelPath = in.readLine();
		savedExperiment = in.readLine();
		savedCycle = Integer.parseInt(in.readLine());

		savedModelName = new File(savedModelPath).getName();
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

	/**
	 * Gets the model path.
	 *
	 * @return the model path
	 */
	public String getModelPath() {
		return savedModelPath;
	}

	/**
	 * Gets the model name.
	 *
	 * @return the model name
	 */
	public String getModelName() {
		return savedModelName;
	}

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public String getExperiment() {
		return savedExperiment;
	}

	/**
	 * Gets the cycle.
	 *
	 * @return the cycle
	 */
	public int getCycle() {
		return savedCycle;
	}

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
				// Write the Metadata
				writer.append(savedModelPath).append(Strings.LN);
				writer.append(savedExperiment).append(Strings.LN);
				writer.append("" + savedCycle).append(Strings.LN);

				// Write the Buffer
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
