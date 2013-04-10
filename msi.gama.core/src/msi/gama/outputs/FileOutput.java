/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.io.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class AbstractFileOutput.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.OUTPUT_FILE, kind = ISymbolKind.OUTPUT, with_sequence = false)
@inside(symbols = IKeyword.OUTPUT)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.DATA, type = IType.STRING, optional = false),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true),
	@facet(name = IKeyword.HEADER, type = IType.STRING, optional = true),
	@facet(name = IKeyword.FOOTER, type = IType.STRING, optional = true),
	@facet(name = IKeyword.REWRITE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.CSV, IKeyword.TEXT,
		IKeyword.XML }, optional = true) }, omissible = IKeyword.NAME)
public class FileOutput extends AbstractOutput {

	/**
	 * @throws GamaRuntimeException The Constructor.
	 * 
	 * @param sim the sim
	 */
	public FileOutput(/* final ISymbol context, */final IDescription desc) {
		super(desc);
	}

	private PrintWriter writer = null;

	File file = null;
	String fileName = "";
	boolean rewrite = false;
	String header = "";
	String footer = "";
	Object lastValue = null;
	List<String> loggedBatchParam = null;
	ParametersSet solution = null;
	private String expressionText = null;
	private IExpression data;
	private static final String LOG_FOLDER = "log";
	private static final String XMLHeader =
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
	private static final int XML = 1;
	private static final int CSV = 2;
	private static final int TEXT = 0;
	private static final List<String> extensions = Arrays.asList("txt", "xml", "csv");
	private int type;

	private void createType() {
		String t = getLiteral(IKeyword.TYPE, IKeyword.TEXT);
		type = t.equals(IKeyword.CSV) ? CSV : t.equals(IKeyword.XML) ? XML : TEXT;
	}

	private void createExpression() {
		data = getFacet(IKeyword.DATA);
		expressionText = data.toGaml();
		if ( expressionText == null ) { return; }
		refreshExpression();
	}

	private void createHeader() throws GamaRuntimeException {
		IExpression exp = getFacet(IKeyword.HEADER);
		if ( exp == null ) {
			setHeader(getHeader());
		} else {
			setHeader(Cast.asString(getOwnScope(), exp.value(getOwnScope())));
		}
	}

	private void createFooter() throws GamaRuntimeException {
		IExpression exp = getFacet(IKeyword.FOOTER);
		if ( exp == null ) {
			setFooter(getFooter());
		} else {
			setFooter(Cast.asString(getOwnScope(), exp.value(getOwnScope())));
		}
	}

	private void createRewrite() throws GamaRuntimeException {
		IExpression exp = getFacet(IKeyword.REWRITE);
		if ( exp == null ) {
			setRewrite(false);
		} else {
			setRewrite(Cast.asBool(getOwnScope(), exp.value(getOwnScope())));
		}
	}

	@Override
	public void dispose() {
		writer = null;
		file = null;
		super.dispose();
	}

	@Override
	public void open() {
		try {
			setWriter(new PrintWriter(file));
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
			case CSV:
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
			case CSV:
		}
		writer.flush();
		writer.close();
		super.close();
	}

	@Override
	public void prepare(final ISimulation sim) throws GamaRuntimeException {
		super.prepare(sim);
		createType();
		createFileName(sim.getModel());
		createRewrite();
		createHeader();
		createFooter();
		createExpression();
	}

	public FileOutput(final String name, final String expr, final List<String> columns,
		final IExperiment exp) throws GamaRuntimeException {
		super(DescriptionFactory.create(IKeyword.FILE, IKeyword.DATA, expr, IKeyword.TYPE,
			IKeyword.CSV, IKeyword.NAME, name == null ? expr : name));
		prepare(exp);
		expressionText = expr;
		refreshExpression();
		this.setPermanent(true);
		this.setRefreshRate(0);
		this.setLoggedBatchParam(columns);
		this.writeHeaderAndClose();
	}

	public void prepare(final IExperiment exp) throws GamaRuntimeException {
		setOwnScope(exp.getExperimentScope());
		outputManager = exp.getOutputManager();
		createType();
		createFileName(exp.getModel());
		createRewrite();
		createHeader();
		createFooter();
	}

	/**
	 * @throws GamaRuntimeException Creates a file name.
	 */
	private void createFileName(final IModel model) throws GamaRuntimeException {
		this.fileName = getName();
		final String dir = model.getFolderPath() + "/" + LOG_FOLDER + "/";
		final File logFolder = new File(dir);
		if ( !logFolder.exists() ) {
			final boolean isCreated = logFolder.mkdir();
			if ( !isCreated ) {
				GuiUtils.error("Impossible to create " + dir);
			}
		}

		file = new File(dir, fileName + "." + extensions.get(type));
		final boolean exist = file.exists();
		if ( exist && !getRewrite() ) {
			this.fileName = fileName + StringUtils.getTimeInString();
		}
		file = new File(dir, fileName + "." + extensions.get(type));
		try {
			file.createNewFile();
		} catch (final IOException e) {
			throw new GamaRuntimeException(e);
		}
	}

	public void refreshExpression() throws GamaRuntimeException {
		// in case the file writer persists over different simulations (like in the batch)
		data = GAMA.getExpressionFactory().createExpr(expressionText, GAMA.getModelContext());
	}

	public Object getLastValue() {
		return lastValue;
	}

	@Override
	public void compute(final IScope scope, final int cycle) throws GamaRuntimeException {
		setLastValue(data.value(scope));
	}

	@Override
	public void update() throws GamaRuntimeException {
		writeToFile(getOwnScope().getClock().getCycle());
	}

	public void doRefreshWriteAndClose(final ParametersSet sol, final Object fitness)
		throws GamaRuntimeException {
		setSolution(sol);
		if ( fitness == null ) {
			compute(getOwnScope(), 0);
		} else {
			setLastValue(fitness);
		}
		// compute(getOwnScope(), 0l);
		FileWriter fileWriter;
		switch (type) {
			case XML:
				break;
			case TEXT:
				try {
					fileWriter = new FileWriter(file, true);
					if ( getLastValue() != null ) {
						fileWriter.write(getLastValue().toString());
					}
					fileWriter.flush();
					fileWriter.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
			case CSV:
				if ( solution == null ) { return; }
				StringBuilder s = new StringBuilder(loggedBatchParam.size() * 8);
				for ( final String var : loggedBatchParam ) {
					s.append(solution.get(var)).append(',');
				}
				if ( lastValue != null ) {
					s.append(lastValue);
				} else {
					s.setLength(s.length() - 1);
				}
				s.append(System.getProperty("line.separator"));
				try {
					fileWriter = new FileWriter(file, true);
					fileWriter.write(s.toString());
					fileWriter.flush();
					fileWriter.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}

				break;
		}
	}

	void writeToFile(final long cycle) {
		switch (type) {
			case CSV:
			case TEXT:
				getWriter().println(getLastValue());
				getWriter().flush();
				break;
			case XML:
				getWriter().println(
					"<data step=\"" + cycle + "\" value=\"" + getLastValue() + "\" />");
				getWriter().flush();
				break;

		}
	}

	private boolean getRewrite() {
		return rewrite;
	}

	private void setRewrite(final boolean rewrite) {
		this.rewrite = rewrite;
	}

	private String getHeader() {
		if ( header == null ) {
			setHeader(getName() + " " + StringUtils.getTimeInString());
		}
		return header;
	}

	private void setHeader(final String header) {
		this.header = header;
	}

	private String getFooter() {
		if ( footer == null ) {
			setFooter("End of " + getName() + " " + StringUtils.getTimeInString());
		}
		return header;
	}

	private void setFooter(final String header) {
		this.header = header;
	}

	private PrintWriter getWriter() {
		return writer;
	}

	private void setWriter(final PrintWriter writer) {
		this.writer = writer;
	}

	public void setLastValue(final Object lastValue) {
		this.lastValue = lastValue;
	}

	public List<String> getLoggedBatchParam() {
		return loggedBatchParam;
	}

	public void setLoggedBatchParam(final List<String> loggedBatchParam) {
		this.loggedBatchParam = loggedBatchParam;
	}

	public ParametersSet getSolution() {
		return solution;
	}

	public void setSolution(final ParametersSet solution) {
		this.solution = solution;
	}

	public void writeHeaderAndClose() {
		FileWriter fileWriter;
		switch (type) {
			case XML:
				break;
			case TEXT:
				try {
					fileWriter = new FileWriter(file);
					fileWriter.write(getHeader());
					fileWriter.flush();
					fileWriter.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
			case CSV:
				StringBuilder s = new StringBuilder(loggedBatchParam.size() * 8);
				for ( final String var : loggedBatchParam ) {
					s.append(var).append(',');
				}
				if ( getFacet(IKeyword.DATA) != null ) {
					s.append(getLiteral(IKeyword.DATA));
				} else {
					s.setLength(s.length() - 1);
				}
				s.append(System.getProperty("line.separator"));
				try {
					fileWriter = new FileWriter(file);
					fileWriter.write(s.toString());
					fileWriter.flush();
					fileWriter.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public void setType(final String t) {
		type = t.equals(IKeyword.CSV) ? CSV : t.equals(IKeyword.XML) ? XML : TEXT;
	}

}
