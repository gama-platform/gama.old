/*******************************************************************************************************
 *
 * ExperimentJob.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.common.Display2D;
import msi.gama.headless.common.Globals;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.IRichExperiment;
import msi.gama.headless.core.RichExperiment;
import msi.gama.headless.core.RichOutput;
import msi.gama.headless.xml.Writer;
import msi.gama.headless.xml.XmlTAG;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ExperimentJob.
 */
public class ExperimentJob implements IExperimentJob {

	static {
		DEBUG.ON();
	}

	/** The global id generator. */
	private static long GLOBAL_ID_GENERATOR = 0;

	/**
	 * The Enum OutputType.
	 */
	public enum OutputType {
		
		/** The output. */
		OUTPUT, 
 /** The experiment attribute. */
 EXPERIMENT_ATTRIBUTE, 
 /** The simulation attribute. */
 SIMULATION_ATTRIBUTE
	}

	/**
	 * Variable listeners
	 */
	protected ListenedVariable[] listenedVariables;
	
	/** The parameters. */
	private List<Parameter> parameters;
	
	/** The outputs. */
	private List<Output> outputs;
	
	/** The output file. */
	protected Writer outputFile;
	
	/** The source path. */
	private String sourcePath;
	
	/** The experiment name. */
	private String experimentName;
	
	/** The model name. */
	private String modelName;
	
	/** The seed. */
	private double seed;
	/**
	 * current step
	 */
	protected long step;

	/**
	 * id of current experiment
	 */
	private String experimentID;
	
	/** The final step. */
	public long finalStep;
	
	/** The until cond. */
	private String untilCond;
	
	/** The end condition. */
	IExpression endCondition;

	/**
	 * simulator to be loaded
	 */
	public IRichExperiment simulator;

	/**
	 * Gets the simulation.
	 *
	 * @return the simulation
	 */
	public IRichExperiment getSimulation() {
		return simulator;
	}

	/**
	 * Gets the source path.
	 *
	 * @return the source path
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * Generate ID.
	 *
	 * @return the long
	 */
	private static long generateID() {
		return ExperimentJob.GLOBAL_ID_GENERATOR++;
	}

	/**
	 * Sets the buffered writer.
	 *
	 * @param w the new buffered writer
	 */
	public void setBufferedWriter(final Writer w) {
		this.outputFile = w;
	}

	@Override
	public void addParameter(final Parameter p) {
		this.parameters.add(p);
	}

	@Override
	public void addOutput(final Output p) {
		p.setId("" + outputs.size());
		this.outputs.add(p);
	}

	/**
	 * Instantiates a new experiment job.
	 */
	private ExperimentJob() {
		initialize();

	}

	/**
	 * Instantiates a new experiment job.
	 *
	 * @param clone the clone
	 */
	public ExperimentJob(final ExperimentJob clone) {
		this();
		this.experimentID = clone.experimentID != null ? clone.experimentID : "" + ExperimentJob.generateID();
		this.sourcePath = clone.sourcePath;
		this.finalStep = clone.finalStep;
		this.experimentName = clone.experimentName;
		this.modelName = clone.modelName;
		this.parameters = new ArrayList<>();
		this.outputs = new ArrayList<>();
		this.listenedVariables = clone.listenedVariables;
		this.step = clone.step;
		this.seed = clone.seed;
		for (final Parameter p : clone.parameters) {
			this.addParameter(new Parameter(p));
		}
		for (final Output o : clone.outputs) {
			this.addOutput(new Output(o));
		}

	}

	/**
	 * Instantiates a new experiment job.
	 *
	 * @param sourcePath the source path
	 * @param exp the exp
	 * @param max the max
	 * @param untilCond the until cond
	 * @param s the s
	 */
	public ExperimentJob(final String sourcePath, final String exp, final long max, final String untilCond,
			final double s) {
		this(sourcePath, new Long(ExperimentJob.generateID()).toString(), exp, max, untilCond, s);
	}

	/**
	 * Instantiates a new experiment job.
	 *
	 * @param sourcePath the source path
	 * @param expId the exp id
	 * @param exp the exp
	 * @param max the max
	 * @param untilCond the until cond
	 * @param s the s
	 */
	public ExperimentJob(final String sourcePath, final String expId, final String exp, final long max,
			final String untilCond, final double s) {
		this();
		this.experimentID = expId;
		this.sourcePath = sourcePath;
		this.finalStep = max;
		this.untilCond = untilCond;
		this.experimentName = exp;
		this.seed = s;
		this.modelName = null;

	}

	@Override
	public void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			IOException, GamaHeadlessException {

		this.load();
		this.listenedVariables = new ListenedVariable[outputs.size()];

		for (final Parameter temp : parameters) {
			if (temp.getName() == null || "".equals(temp.getName())) {
				this.simulator.setParameter(temp.getVar(), temp.getValue());
			} else {
				this.simulator.setParameter(temp.getName(), temp.getValue());
			}
		}
		this.setup();
		simulator.setup(experimentName, this.seed);
		for (int i = 0; i < outputs.size(); i++) {
			final Output temp = outputs.get(i);
			this.listenedVariables[i] = new ListenedVariable(temp.getName(), temp.getWidth(), temp.getHeight(),
					temp.getFrameRate(), simulator.getTypeOf(temp.getName()), temp.getOutputPath());
		}

		// Initialize the enCondition
		if (untilCond == null || "".equals(untilCond)) {
			endCondition = IExpressionFactory.FALSE_EXPR;
		} else {
			endCondition = GAML.getExpressionFactory().createExpr(untilCond, simulator.getModel().getDescription());
			// endCondition = GAML.compileExpression(untilCond, simulator.getSimulation(), true);
		}
		if (endCondition.getGamlType() != Types.BOOL)
			throw GamaRuntimeException.error("The until condition of the experiment should be a boolean",
					simulator.getSimulation().getScope());
	}

	/**
	 * Load.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public void load() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException,
			GamaHeadlessException {
		System.setProperty("user.dir", this.sourcePath);
		final List<GamlCompilationError> errors = new ArrayList<>();
		final IModel mdl = HeadlessSimulationLoader.loadModel(new File(this.sourcePath), errors);
		this.modelName = mdl.getName();
		this.simulator = new RichExperiment(mdl);
	}

	/**
	 * Setup.
	 */
	public void setup() {
		this.step = 0;

	}

	@Override
	public void playAndDispose() {
		DEBUG.TIMER("Simulation duration", () -> {
			play();
			dispose();
		});
	}

	@Override
	public void play() {
		if (this.outputFile != null) { this.outputFile.writeSimulationHeader(this); }
		// DEBUG.LOG("Simulation is running...", false);
		final long affDelay = finalStep < 100 ? 1 : finalStep / 100;

		try {
			int step = 0;
			// Added because the simulation may be null in case we deal with a batch experiment
			while (finalStep >= 0 ? step < finalStep : true) {
				if (step % affDelay == 0) { DEBUG.LOG(".", false); }
				if (simulator.isInterrupted()) { break; }
				final SimulationAgent sim = simulator.getSimulation();
				final IScope scope = sim == null ? GAMA.getRuntimeScope() : sim.getScope();
				if (Cast.asBool(scope, endCondition.value(scope))) { break; }
				doStep();
				step++;
			}
		} catch (final GamaRuntimeException e) {
			DEBUG.ERR("\n The simulation has stopped before the end due to the following exception: ", e);
		}
	}

	@Override
	public void dispose() {
		if (this.simulator != null) { this.simulator.dispose(); }
		if (this.outputFile != null) { this.outputFile.close(); }
	}

	@Override
	public void doStep() {
		this.step = simulator.step();
		this.exportVariables();
	}

	@Override
	public String getExperimentID() {
		return experimentID;
	}

	/**
	 * Sets the id of current experiment.
	 *
	 * @param experimentID the new id of current experiment
	 */
	public void setExperimentID(final String experimentID) {
		this.experimentID = experimentID;
	}

	
	public ListenedVariable[] getListenedVariables() {
		return listenedVariables;
	}
	
	/**
	 * Export variables.
	 */
	protected void exportVariables() {
		final int size = this.listenedVariables.length;
		if (size == 0) return;
		for (int i = 0; i < size; i++) {
			final ListenedVariable v = this.listenedVariables[i];
			if (this.step % v.frameRate == 0) {
				final RichOutput out = simulator.getRichOutput(v);
				if (out == null || out.getValue() == null) {} else if (out.getValue() instanceof BufferedImage) {
					v.setValue(writeImageInFile((BufferedImage) out.getValue(), v.getName(), v.getPath()), step,
							out.getType());
				} else {
					v.setValue(out.getValue(), out.getStep(), out.getType());
				}
			} else {
				v.setValue(null, this.step);
			}
		}
		if (this.outputFile != null) { this.outputFile.writeResultStep(this.step, this.listenedVariables); }

	}

	/**
	 * Initialize.
	 */
	public void initialize() {
		parameters = new Vector<>();
		outputs = new Vector<>();
		if (simulator != null) {
			simulator.dispose();
			simulator = null;
		}
		untilCond = "";
	}

	@Override
	public long getStep() {
		return step;
	}

	/**
	 * Write image in file.
	 *
	 * @param img the img
	 * @param name the name
	 * @param outputPath the output path
	 * @return the display 2 D
	 */
	protected Display2D writeImageInFile(final BufferedImage img, final String name, final String outputPath) {
		final String fileName = name + this.getExperimentID() + "-" + step + ".png";
		String fileFullName = Globals.IMAGES_PATH + "/" + fileName;
		if (outputPath != "" && outputPath != null) {
			// a specific output path has been specified with the "output_path"
			// keyword in the xml
			fileFullName = outputPath + "-" + step + ".png";
			// check if the folder exists, create a new one if it does not
			final File tmp = new File(fileFullName);
			tmp.getParentFile().mkdirs();
		}
		try {
			ImageIO.write(img, "png", new File(fileFullName));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new Display2D(name + this.getExperimentID() + "-" + step + ".png");
	}

	@Override
	public void setSeed(final double s) {
		this.seed = s;
	}

	@Override
	public double getSeed() {
		return this.seed;
	}

	@Override
	public Element asXMLDocument(final Document doc) {
		final Element simulation = doc.createElement(XmlTAG.SIMULATION_TAG);

		final Attr attr = doc.createAttribute(XmlTAG.ID_TAG);
		attr.setValue(this.experimentID);
		simulation.setAttributeNode(attr);

		final Attr attr3 = doc.createAttribute(XmlTAG.SOURCE_PATH_TAG);
		attr3.setValue(this.sourcePath);
		simulation.setAttributeNode(attr3);

		final Attr attr2 = doc.createAttribute(XmlTAG.FINAL_STEP_TAG);
		attr2.setValue(new Long(this.finalStep).toString());
		simulation.setAttributeNode(attr2);

		final Attr attr5 = doc.createAttribute(XmlTAG.SEED_TAG);
		attr5.setValue(new Float(this.seed).toString());
		simulation.setAttributeNode(attr5);

		final Attr attr4 = doc.createAttribute(XmlTAG.EXPERIMENT_NAME_TAG);
		attr4.setValue(this.experimentName);
		simulation.setAttributeNode(attr4);

		final Element parameters = doc.createElement(XmlTAG.PARAMETERS_TAG);
		simulation.appendChild(parameters);

		for (final Parameter p : this.parameters) {
			final Element aparameter = doc.createElement(XmlTAG.PARAMETER_TAG);
			parameters.appendChild(aparameter);

			final Attr ap1 = doc.createAttribute(XmlTAG.NAME_TAG);
			ap1.setValue(p.getName());
			aparameter.setAttributeNode(ap1);

			final Attr ap2 = doc.createAttribute(XmlTAG.VAR_TAG);
			ap2.setValue(p.getVar());
			aparameter.setAttributeNode(ap2);

			final Attr ap3 = doc.createAttribute(XmlTAG.TYPE_TAG);
			ap3.setValue(p.getType().toString());
			aparameter.setAttributeNode(ap3);

			final Attr ap4 = doc.createAttribute(XmlTAG.VALUE_TAG);
			ap4.setValue(p.getValue().toString());
			aparameter.setAttributeNode(ap4);
		}

		final Element outputs = doc.createElement(XmlTAG.OUTPUTS_TAG);
		simulation.appendChild(outputs);

		for (final Output o : this.outputs) {
			final Element aOutput = doc.createElement(XmlTAG.OUTPUT_TAG);
			outputs.appendChild(aOutput);

			final Attr o3 = doc.createAttribute(XmlTAG.ID_TAG);
			o3.setValue(o.getId());
			aOutput.setAttributeNode(o3);

			final Attr o1 = doc.createAttribute(XmlTAG.NAME_TAG);
			o1.setValue(o.getName());
			aOutput.setAttributeNode(o1);

			final Attr o2 = doc.createAttribute(XmlTAG.FRAMERATE_TAG);
			o2.setValue(new Integer(o.getFrameRate()).toString());
			aOutput.setAttributeNode(o2);
		}
		return simulation;
	}

	/**
	 * Load and build job.
	 *
	 * @param expD the exp D
	 * @param path the path
	 * @param model the model
	 * @return the experiment job
	 */
	public static ExperimentJob loadAndBuildJob(final ExperimentDescription expD, final String path,
			final IModel model) {
		final String expName = expD.getName();
		final IExpressionDescription seedDescription = expD.getFacet(IKeyword.SEED);
		double mseed = 0.0;
		if (seedDescription != null) {
			mseed = Double.valueOf(seedDescription.getExpression().literalValue()).doubleValue();
		}
		final IDescription d = expD.getChildWithKeyword(IKeyword.OUTPUT);
		final ExperimentJob expJob =
				new ExperimentJob(path, new Long(ExperimentJob.generateID()).toString(), expName, 0, "", mseed);

		if (d != null) {
			final Iterable<IDescription> monitors = d.getChildrenWithKeyword(IKeyword.MONITOR);
			for (final IDescription moni : monitors) {
				expJob.addOutput(Output.loadAndBuildOutput(moni));
			}

			final Iterable<IDescription> displays = d.getChildrenWithKeyword(IKeyword.DISPLAY);
			for (final IDescription disp : displays) {
				if (disp.getFacetExpr(IKeyword.VIRTUAL) != IExpressionFactory.TRUE_EXPR) {
					expJob.addOutput(Output.loadAndBuildOutput(disp));
				}
			}
		}

		final Iterable<IDescription> parameters = expD.getChildrenWithKeyword(IKeyword.PARAMETER);
		for (final IDescription para : parameters) {
			expJob.addParameter(Parameter.loadAndBuildParameter(para, model));
		}

		return expJob;
	}

	@Override
	public String getExperimentName() {

		return this.experimentName;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param name the name
	 * @return the parameter
	 */
	private Parameter getParameter(final String name) {
		for (final Parameter p : parameters) {
			if (p.getName().equals(name)) return p;
		}
		return null;
	}

	@Override
	public List<Parameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Gets the output.
	 *
	 * @param name the name
	 * @return the output
	 */
	private Output getOutput(final String name) {
		for (final Output p : outputs) {
			if (p.getName().equals(name)) return p;
		}
		return null;
	}

	@Override
	public List<Output> getOutputs() {
		return this.outputs;
	}

	@Override
	public void setParameterValueOf(final String name, final Object val) {
		this.getParameter(name).setValue(val);
	}

	@Override
	public void removeOutputWithName(final String name) {
		this.outputs.remove(this.getOutput(name));
	}

	@Override
	public void setOutputFrameRate(final String name, final int frameRate) {
		this.getOutput(name).setFrameRate(frameRate);
	}

	@Override
	public List<String> getOutputNames() {
		final List<String> res = new ArrayList<>();
		for (final Output o : outputs) {
			res.add(o.getName());
		}
		return res;
	}

	/**
	 * Gets the final step.
	 *
	 * @return the final step
	 */
	public long getFinalStep() {
		return finalStep;
	}

	@Override
	public void setFinalStep(final long finalStep) {
		this.finalStep = finalStep;
	}

	@Override
	public String getModelName() {
		return this.modelName;
	}

}
