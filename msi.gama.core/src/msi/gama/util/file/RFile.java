/*********************************************************************************************
 * 
 * 
 * 'GamaTextFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import rcaller.RCaller;
import rcaller.RCode;

@file(name = "R", extensions = {
		"r" }, buffer_type = IType.MAP, buffer_content = IType.LIST, buffer_index = IType.STRING, concept = {
				IConcept.FILE, IConcept.R })
@SuppressWarnings({ "rawtypes" })
public class RFile extends GamaFile<GamaMap<String, IList>, IList, String, IList> {

	private final boolean DEBUG = false; // Change DEBUG = false for release
											// version
	private final IContainer parameters;

	public RFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		parameters = null;
	}

	public RFile(final IScope scope, final String pathName, final IContainer p) {
		super(scope, pathName);
		parameters = p;
	}

	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		final StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for (final IList s : getBuffer().iterable(scope)) {
			sb.append(s).append(Strings.LN); // TODO Factorize the different
												// calls to
			// "new line" ...
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
		if (getBuffer() != null) {
			return;
		}
		if (parameters == null) {
			doRFileEvaluate(scope);
		} else {
			doRFileEvaluate(scope, this.parameters);
		}

	}

	public void doRFileEvaluate(final IScope scope, final IContainer param) {
		final int size = param.length(scope);
		// if ( size == 0 ) { throw GamaRuntimeException.error("Missing
		// Parameter Exception", scope); }

		final String RFile = getPath(scope);
		try {
			// Call R
			final RCaller caller = new RCaller();

			final String RPath = GamaPreferences.LIB_R.value(scope).getPath(scope);
			caller.setRscriptExecutable(RPath);

			final double[] vectorParam = new double[param.length(scope)];

			int k = 0;
			for (final Object o : param.iterable(scope)) {
				vectorParam[k++] = Cast.asFloat(scope, o);
			}

			final RCode c = new RCode();
			// Adding the parameters
			c.addDoubleArray("vectorParam", vectorParam);

			// Adding the codes in file
			final List<String> R_statements = new ArrayList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			final String fullPath = FileUtils.constructAbsoluteFilePath(scope, RFile, true);
			if (DEBUG) {
				scope.getGui().debug("Stats.R_compute_param.RScript:" + RPath);
				scope.getGui().debug("Stats.R_compute_param.Param:" + vectorParam.toString());
				scope.getGui().debug("Stats.R_compute_param.RFile:" + RFile);
				scope.getGui().debug("Stats.R_compute_param.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			final FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			final BufferedReader br = new BufferedReader(fr);
			String statement;

			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
			}
			br.close();
			fr.close();
			caller.setRCode(c);

			final GamaMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			final String var = computeVariable(R_statements.get(R_statements.size() - 1).toString());
			caller.runAndReturnResult(var);

			// DEBUG:
			// java.lang.System.out.println("Name: '" +
			// R_statements.length(scope) + "'");
			if (DEBUG) {
				scope.getGui().debug("Stats.R_compute_param.R_statements.length: '" + R_statements.size() + "'");
			}

			for (final String name : caller.getParser().getNames()) {
				String[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// java.lang.System.out.println("Name: '" + name + "'");
				if (DEBUG) {
					scope.getGui().debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length
							+ " - Value: " + results.toString());
				}

				result.put(name, GamaListFactory.create(scope, Types.NO_TYPE, results));
			}

			if (DEBUG) {
				scope.getGui().debug("Stats.R_compute_param.return:" + result.serialize(false));
			}

			setBuffer(result);

		} catch (final Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage(), scope);
		}
	}

	public void doRFileEvaluate(final IScope scope) {
		final String RFile = getPath(scope);
		try {
			// Call R
			final RCaller caller = new RCaller();

			final String RPath = GamaPreferences.LIB_R.value(scope).getPath(scope);
			caller.setRscriptExecutable(RPath);
			// caller.setRscriptExecutable("\"" + RPath + "\"");
			// if(java.lang.System.getProperty("os.name").startsWith("Mac"))
			// {
			// caller.setRscriptExecutable(RPath);
			// }

			final RCode c = new RCode();
			final List<String> R_statements = new ArrayList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			final String fullPath = FileUtils.constructAbsoluteFilePath(scope, RFile, true);
			if (DEBUG) {
				scope.getGui().debug("Stats.R_compute.RScript:" + RPath);
				scope.getGui().debug("Stats.R_compute.RFile:" + RFile);
				scope.getGui().debug("Stats.R_compute.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			final FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			final BufferedReader br = new BufferedReader(fr);
			String statement;
			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
				if (DEBUG) {
					scope.getGui().debug("Stats.R_compute.statement:" + statement);
				}

			}

			fr.close();
			br.close();
			caller.setRCode(c);

			final GamaMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			final String var = computeVariable(R_statements.get(R_statements.size() - 1).toString());
			caller.runAndReturnResult(var);
			for (final String name : caller.getParser().getNames()) {
				Object[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// for (int i = 0; i < results.length; i++) {
				// java.lang.System.out.println(results[i]);
				// }
				if (DEBUG) {
					scope.getGui().debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length
							+ " - Value: " + results.toString());
				}
				result.put(name, GamaListFactory.createWithoutCasting(Types.NO_TYPE, results));
			}
			if (DEBUG) {
				scope.getGui().debug("Stats.R_compute.return:" + result.serialize(false));
			}
			// return result;
			setBuffer(result);

		} catch (final Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage(), scope);
		}
	}

	private static String computeVariable(final String string) {
		final String[] tokens = string.split("<-");
		return tokens[0];
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method getType()
	 * 
	 * @see msi.gama.util.IContainer#getType()
	 */
	@Override
	public IContainerType<IGamaFile> getType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

}
