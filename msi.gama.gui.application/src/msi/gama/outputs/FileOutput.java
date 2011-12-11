/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs;

import java.io.*;
import java.util.*;
import msi.gama.factories.DescriptionFactory;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.ExpressionDescription;
import msi.gama.internal.types.GamaStringType;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.Cast;
import msi.gaml.batch.Solution;

/**
 * The Class AbstractFileOutput.
 * 
 * @author drogoul
 */
@symbol(name = ISymbol.FILE, kind = ISymbolKind.OUTPUT)
@inside(symbols = ISymbol.OUTPUT)
@facets(value = {
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.DATA, type = IType.STRING_STR, optional = false),
	@facet(name = ISymbol.REFRESH_EVERY, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.HEADER, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.FOOTER, type = IType.STRING_STR, optional = true),
	@facet(name = ISymbol.REWRITE, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.TYPE, type = IType.ID, values = { ISymbol.CSV, ISymbol.TEXT, ISymbol.XML }, optional = true) })
public class FileOutput extends AbstractOutput {

	/**
	 * @throws GamaRuntimeException The Constructor.
	 * 
	 * @param sim the sim
	 */
	public FileOutput(/* final ISymbol context, */final IDescription desc) {
		super(desc);
	}

	private PrintWriter					writer				= null;

	File								file				= null;
	String								fileName			= "";
	boolean								rewrite				= false;
	String								header				= "";
	String								footer				= "";
	Object								lastValue			= null;
	List<String>						loggedBatchParam	= null;
	Solution							solution			= null;
	private String						expressionText		= null;
	private IExpression					data;
	private static final String			LOG_FOLDER			= "log";
	private static final String			XMLHeader			=
																"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";
	private static final int			XML					= 1;
	private static final int			CSV					= 2;
	private static final int			TEXT				= 0;
	private static final List<String>	extensions			= Arrays.asList("txt", "xml", "csv");
	private int							type;

	private void createType() {
		String t = getLiteral(ISymbol.TYPE, ISymbol.TEXT);
		type = t.equals(ISymbol.CSV) ? CSV : t.equals(ISymbol.XML) ? XML : TEXT;
	}

	private void createExpression() {
		data = getFacet(ISymbol.DATA);
		expressionText = data.toGaml();
		if ( expressionText == null ) { return; }
		try {
			refreshExpression();
		} catch (GamlException e) {
			e.printStackTrace();
		}
	}

	private void createHeader() throws GamaRuntimeException {
		IExpression exp = getFacet(ISymbol.HEADER);
		if ( exp == null ) {
			setHeader(getHeader());
		} else {
			setHeader(Cast.asString(exp.value(getOwnScope())));
		}
	}

	private void createFooter() throws GamaRuntimeException {
		IExpression exp = getFacet(ISymbol.FOOTER);
		if ( exp == null ) {
			setFooter(getFooter());
		} else {
			setFooter(Cast.asString(exp.value(getOwnScope())));
		}
	}

	private void createRewrite() throws GamaRuntimeException {
		IExpression exp = getFacet(ISymbol.REWRITE);
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
		createFileName();
		createRewrite();
		createHeader();
		createFooter();
		createExpression();
	}

	public FileOutput(final String name, final String expr, final List<String> columns,
		final IExperiment exp) throws GamlException {
		super(DescriptionFactory.createDescription(ISymbol.FILE, ISymbol.DATA, expr, ISymbol.TYPE,
			ISymbol.CSV, ISymbol.NAME, name == null ? expr : name));
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
		createFileName();
		createRewrite();
		createHeader();
		createFooter();
	}

	/**
	 * @throws GamaRuntimeException Creates a file name.
	 */
	private void createFileName() throws GamaRuntimeException {
		this.fileName = getName();
		final String dir = GAMA.getModel().getBaseDirectory() + "/" + LOG_FOLDER + "/";
		final File logFolder = new File(dir);
		if ( !logFolder.exists() ) {
			final boolean isCreated = logFolder.mkdir();
			if ( !isCreated ) {
				GUI.error("Impossible to create " + dir);
			}
		}

		file = new File(dir, fileName + "." + extensions.get(type));
		final boolean exist = file.exists();
		if ( exist && !getRewrite() ) {
			this.fileName = fileName + GamaStringType.getTimeInString();
		}
		file = new File(dir, fileName + "." + extensions.get(type));
		try {
			file.createNewFile();
		} catch (final IOException e) {
			throw new GamaRuntimeException(e);
		}
	}

	public void refreshExpression() throws GamlException {
		// in case the file writer persists over different simulations (like in the batch)
		data = GAMA.getExpressionFactory().createExpr(new ExpressionDescription(expressionText));
	}

	public Object getLastValue() {
		return lastValue;
	}

	@Override
	public void compute(final IScope scope, final Long cycle) throws GamaRuntimeException {
		setLastValue(data.value(scope));
	}

	@Override
	public void update() throws GamaRuntimeException {
		writeToFile(getOwnScope().getSimulationScope().getScheduler().getCycle());
	}

	public void doRefreshWriteAndClose(final Solution sol, final Object fitness)
		throws GamaRuntimeException {
		setSolution(sol);
		if ( fitness == null ) {
			compute(getOwnScope(), 0l);
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
				StringBuilder s = new StringBuilder();
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
			setHeader(getName() + " " + GamaStringType.getTimeInString());
		}
		return header;
	}

	private void setHeader(final String header) {
		this.header = header;
	}

	private String getFooter() {
		if ( footer == null ) {
			setFooter("End of " + getName() + " " + GamaStringType.getTimeInString());
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

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(final Solution solution) {
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
				StringBuilder s = new StringBuilder();
				for ( final String var : loggedBatchParam ) {
					s.append(var).append(',');
				}
				if ( getFacet(ISymbol.DATA) != null ) {
					s.append(getLiteral(ISymbol.DATA));
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
		type = t.equals(ISymbol.CSV) ? CSV : t.equals(ISymbol.XML) ? XML : TEXT;
	}

}
