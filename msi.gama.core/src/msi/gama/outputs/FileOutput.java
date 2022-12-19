/*******************************************************************************************************
 *
 * FileOutput.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class AbstractFileOutput.
 * </p>
 * A particular output file especially design for the batch experiment output
 * </p>
 * @author drogoul
 */
@symbol (
		name = IKeyword.OUTPUT_FILE,
		kind = ISymbolKind.OUTPUT,
		with_sequence = false,
		concept = { IConcept.FILE, IConcept.SAVE_FILE })
@doc ("Represents an output that writes the result of expressions into a file")
@inside (
		symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				doc = @doc (
						value = "The name of the file where you want to export the data")),
				@facet (
						name = IKeyword.DATA,
						type = IType.STRING,
						optional = false,
						doc = @doc (
								value = "The data you want to export")),
				@facet (
						name = IKeyword.REFRESH_EVERY,
						type = IType.INT,
						optional = true,
						doc = @doc (
								value = "Allows to save the file every n time steps (default is 1)",
								deprecated = "Use refresh: every(n) instead")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Indicates the condition under which this file should be saved (default is true)")),
				@facet (
						name = IKeyword.HEADER,
						type = IType.STRING,
						optional = true,
						doc = @doc (
								value = "Define a header for your export file")),
				@facet (
						name = IKeyword.FOOTER,
						type = IType.STRING,
						optional = true,
						doc = @doc (
								value = "Define a footer for your export file")),
				@facet (
						name = IKeyword.REWRITE,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								value = "Rewrite or not the existing file")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.ID,
						values = { IKeyword.CSV, IKeyword.TEXT, IKeyword.XML },
						optional = true,
						doc = @doc (
								value = "The type of your output data")) },
		omissible = IKeyword.NAME)
public class FileOutput extends AbstractOutput {

	/**
	 * @throws GamaRuntimeException
	 *             The Constructor.
	 *
	 * @param sim
	 *            the sim
	 */
	public FileOutput(/* final ISymbol context, */final IDescription desc) {
		super(desc);
	}

	/** The writer. */
	private PrintWriter writer = null;

	/** The file. */
	File file = null;
	
	/** The file name. */
	String fileName = "";
	
	/** The rewrite. */
	boolean rewrite = false;
	
	/** The header. */
	String header = "";
	
	/** The footer. */
	String footer = "";
	
	/** The last value. */
	Object lastValue = null;
	
	/** The last values. */
	List<Object> lastValues = null;
	
	/** The logged batch param. */
	List<String> loggedBatchParam = null;
	
	/** The solution. */
	ParametersSet solution = null;
	
	/** The expression text. */
	private String expressionText = null;
	
	/** The data. */
	private IExpression data;
	
	/** The Constant LOG_FOLDER. */
	private static final String LOG_FOLDER = "log";
	
	/** The Constant XMLHeader. */
	private static final String XMLHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
	
	/** The Constant XML. */
	private static final int XML = 1;
	
	/** The Constant CSV. */
	private static final int CSV = 2;
	
	/** The Constant TEXT. */
	private static final int TEXT = 0;
	
	/** The Constant extensions. */
	private static final List<String> extensions = Arrays.asList("txt", "xml", "csv");
	
	/** The type. */
	private int type;

	/**
	 * Creates the type.
	 */
	private void createType() {
		final String t = getLiteral(IKeyword.TYPE, IKeyword.TEXT);
		type = IKeyword.CSV.equals(t) ? CSV : IKeyword.XML.equals(t) ? XML : TEXT;
	}

	/**
	 * Creates the expression.
	 */
	private void createExpression() {
		data = getFacet(IKeyword.DATA);
		expressionText = data.serialize(false);
		if (expressionText == null) return;
		refreshExpression();
	}

	/**
	 * Creates the header.
	 *
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	private void createHeader() throws GamaRuntimeException {
		final IExpression exp = getFacet(IKeyword.HEADER);
		if (exp == null) {
			setHeader(getHeader());
		} else {
			setHeader(Cast.asString(getScope(), exp.value(getScope())));
		}
	}

	/**
	 * Creates the footer.
	 *
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	private void createFooter() throws GamaRuntimeException {
		final IExpression exp = getFacet(IKeyword.FOOTER);
		if (exp == null) {
			setFooter(getFooter());
		} else {
			setFooter(Cast.asString(getScope(), exp.value(getScope())));
		}
	}

	/**
	 * Creates the rewrite.
	 *
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	private void createRewrite() throws GamaRuntimeException {
		final IExpression exp = getFacet(IKeyword.REWRITE);
		if (exp == null) {
			setRewrite(false);
		} else {
			setRewrite(Cast.asBool(getScope(), exp.value(getScope())));
		}
	}

	@Override
	public void dispose() {
		if (isOpen()) { close(); }
		writer = null;
		file = null;
		super.dispose();
	}

	@Override
	public void open() {
		try (PrintWriter printWriter = new PrintWriter(file)) {
			setWriter(printWriter);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		switch (type) {
			case TEXT:
				getWriter().println(getHeader());
				getWriter().flush();
				break;
			case XML:
				getWriter().println(XMLHeader);
				getWriter().println("<" + getName() + ">");
				getWriter().flush();
				break;
			default:
		}

		super.open();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.outputs.AbstractOutput#close()
	 */
	@Override
	public void close() {
		switch (type) {
			case TEXT:
				getWriter().println(getFooter());
				getWriter().flush();
				break;
			case XML:
				getWriter().println("</" + getName() + ">");
				getWriter().flush();
				break;
			default:
		}
		writer.flush();
		writer.close();
		super.close();
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		final boolean result = super.init(scope);
		if (!result) return false;
		createType();
		createRewrite();
		createFileName(scope);
		createHeader();
		createFooter();
		createExpression();
		return true;
	}

	/**
	 * Instantiates a new file output.
	 *
	 * @param name the name
	 * @param expr the expr
	 * @param columns the columns
	 * @param exp the exp
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public FileOutput(final String name, final String expr, final List<String> columns, final IExperimentPlan exp)
			throws GamaRuntimeException {
		// WARNING Created by the batch. Is it still necessary to keep this ?
		// TODO Should be deprecated in favor of a regular file output in the
		// permanent
		// outputs of the experiment.
		super(DescriptionFactory.create(IKeyword.FILE, IKeyword.DATA, expr, IKeyword.TYPE, IKeyword.CSV, IKeyword.NAME,
				name == null ? expr : name));
		// prepare(exp);
		expressionText = expr;
		refreshExpression();
		// this.setPermanent(true);
		this.setRefreshRate(0);
		this.setLoggedBatchParam(columns);
		this.writeHeaderAndClose();
	}

	// public void prepare(final IExperimentPlan exp) throws
	// GamaRuntimeException {
	// // FIXME Verify this scope
	// setScope(GAMA.obtainNewScope());
	// outputManager = exp.getOutputManager();
	// createType();
	// createFileName(exp.getModel());
	// createRewrite();
	// createHeader();
	// createFooter();
	// }

	/**
	 * @throws GamaRuntimeException
	 *             Creates a file name.
	 */
	private void createFileName(final IScope scope) throws GamaRuntimeException {
		this.fileName = getName();
		final String dir = scope.getExperiment().getWorkingPath() + "/" + LOG_FOLDER + "/";
		final File logFolder = new File(dir);
		if (!logFolder.exists()) {
			final boolean isCreated = logFolder.mkdirs();
			if (!isCreated) { scope.getGui().error("Impossible to create " + dir); }
		}

		file = new File(dir, fileName + "." + extensions.get(type));
		final boolean exist = file.exists();

		if (exist && !getRewrite()) {
			final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			this.fileName = fileName + sdf.format(Calendar.getInstance().getTime());
		}
		file = new File(dir, fileName + "." + extensions.get(type));
		try {
			file.createNewFile();
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() { return file; }

	/**
	 * Refresh expression.
	 *
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void refreshExpression() throws GamaRuntimeException {
		// in case the file writer persists over different simulations (like in
		// the batch)
		data = GAML.getExpressionFactory().createExpr(expressionText, GAML.getModelContext());
	}

	/**
	 * Gets the last value.
	 *
	 * @return the last value
	 */
	public Object getLastValue() { return lastValue; }

	@Override
	public boolean step(final IScope scope) {
		if (getScope().interrupted()) return false;
		getScope().setCurrentSymbol(this);
		setLastValue(data.value(getScope()));
		return true;
	}

	@Override
	public void update() throws GamaRuntimeException {
		writeToFile(getScope().getClock().getCycle());
	}
	
	/**
	 * Do write report and close.
	 *
	 * @param report the report
	 */
	public void doWriteReportAndClose(final String report) {
		switch (type) {
			case XML:
				break;
			case TEXT:
				try (FileWriter fileWriter = new FileWriter(file, true)) {
					fileWriter.write(report);
					fileWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
			case CSV:
				break;
		}
	}
 
	/**
	 * Main method to write down a set of given values for a single a point in the parameter space
	 * @param sol
	 * @param outputs
	 * @throws GamaRuntimeException
	 */
	public void doRefreshWriteAndClose(final ParametersSet sol, final IMap<String,Object> outputs) throws GamaRuntimeException {
		setSolution(sol);
		if (outputs == null || outputs.isEmpty()) {
			if (!getScope().step(this).passed()) { return; }
		} else {
			this.lastValues = outputs.values().stream().toList(); //setLastValue(fitness);
		}
		// compute(getOwnScope(), 0l);
		switch (type) {
			case XML:
				break;
			case TEXT:
				try (FileWriter fileWriter = new FileWriter(file, true)) {
					if (getLastValue() != null) {
						fileWriter.write(getLastValue().toString());
					} else if (this.lastValues != null && !this.lastValues.isEmpty()) {
						fileWriter.write(lastValues.toString());
					}
					fileWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
			case CSV:
				if (solution == null) return;
				final StringBuilder s = new StringBuilder(loggedBatchParam.size() * 8);
				for (final String var : loggedBatchParam) { s.append(solution.get(var)).append(','); }
				if (lastValue != null) {
					s.append(lastValue);
				} else if (lastValues != null && !lastValues.isEmpty()) {
					for (Object val : lastValues) {s.append(',').append(val); }
				} else {
					s.setLength(s.length() - 1);
				}
				s.append(System.lineSeparator());
				try (FileWriter fileWriter = new FileWriter(file, true)) {
					fileWriter.write(s.toString());
					fileWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}

				break;
		}
	}

	/**
	 * Write to file.
	 *
	 * @param cycle the cycle
	 */
	void writeToFile(final long cycle) {
		switch (type) {
			case CSV:
			case TEXT:
				getWriter().println(getLastValue());
				getWriter().flush();
				break;
			case XML:
				getWriter().println("<data step=\"" + cycle + "\" value=\"" + getLastValue() + "\" />");
				getWriter().flush();
				break;
			default:
		}
	}

	/**
	 * Gets the rewrite.
	 *
	 * @return the rewrite
	 */
	private boolean getRewrite() { return rewrite; }

	/**
	 * Sets the rewrite.
	 *
	 * @param rewrite the new rewrite
	 */
	private void setRewrite(final boolean rewrite) { this.rewrite = rewrite; }

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	private String getHeader() {
		if (header == null) {
			final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			setHeader(getName() + " " + sdf.format(Calendar.getInstance().getTime()));
		}
		return header;
	}

	/**
	 * Sets the header.
	 *
	 * @param header the new header
	 */
	private void setHeader(final String header) { this.header = header; }

	/**
	 * Gets the footer.
	 *
	 * @return the footer
	 */
	private String getFooter() {
		if (footer == null) {
			final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			setFooter("End of " + getName() + " " + sdf.format(Calendar.getInstance().getTime()));
		}
		return footer;
	}

	/**
	 * Sets the footer.
	 *
	 * @param footer the new footer
	 */
	private void setFooter(final String footer) { this.footer = footer; }

	/**
	 * Gets the writer.
	 *
	 * @return the writer
	 */
	private PrintWriter getWriter() { return writer; }

	/**
	 * Sets the writer.
	 *
	 * @param writer the new writer
	 */
	private void setWriter(final PrintWriter writer) { this.writer = writer; }

	/**
	 * Sets the last value.
	 *
	 * @param lastValue the new last value
	 */
	public void setLastValue(final Object lastValue) { this.lastValue = lastValue; }

	/**
	 * Gets the logged batch param.
	 *
	 * @return the logged batch param
	 */
	public List<String> getLoggedBatchParam() { return loggedBatchParam; }

	/**
	 * Sets the logged batch param.
	 *
	 * @param loggedBatchParam the new logged batch param
	 */
	public void setLoggedBatchParam(final List<String> loggedBatchParam) { this.loggedBatchParam = loggedBatchParam; }

	/**
	 * Gets the solution.
	 *
	 * @return the solution
	 */
	public ParametersSet getSolution() { return solution; }

	/**
	 * Sets the solution.
	 *
	 * @param solution the new solution
	 */
	public void setSolution(final ParametersSet solution) { this.solution = solution; }

	/**
	 * Write header and close.
	 */
	public void writeHeaderAndClose() {
		switch (type) {
			case XML:
				break;
			case TEXT:
				try (FileWriter fileWriter = new FileWriter(file)) {
					fileWriter.write(getHeader());
					fileWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
			case CSV:
				final StringBuilder s = new StringBuilder(loggedBatchParam.size() * 8);
				for (final String var : loggedBatchParam) { s.append(var).append(','); }
				if (getFacet(IKeyword.DATA) != null) {
					s.append(getLiteral(IKeyword.DATA));
				} else {
					s.setLength(s.length() - 1);
				}
				s.append(System.lineSeparator());
				try (FileWriter fileWriter = new FileWriter(file)) {
					fileWriter.write(s.toString());
					fileWriter.flush();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	// @Override
	// public void setType(final String t) {
	// type = t.equals(IKeyword.CSV) ? CSV : t.equals(IKeyword.XML) ? XML :
	// TEXT;
	// }

}
