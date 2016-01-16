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

import java.io.*;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import rcaller.*;
import com.vividsolutions.jts.geom.Envelope;

@file(name = "R",
	extensions = { "r" },
	buffer_type = IType.MAP,
	buffer_content = IType.LIST,
	buffer_index = IType.STRING)
public class RFile extends GamaFile<GamaMap<String, IList>, IList, String, IList> {

	private final boolean DEBUG = false; // Change DEBUG = false for release version
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
		StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for ( IList s : getBuffer().iterable(scope) ) {
			sb.append(s).append("\n"); // TODO Factorize the different calls to "new line" ...
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
		if ( getBuffer() != null ) { return; }
		if ( parameters == null ) {
			doRFileEvaluate(scope);
		} else {
			doRFileEvaluate(scope, this.parameters);
		}

	}

	public void doRFileEvaluate(final IScope scope, final IContainer param) {
		int size = param.length(scope);
		// if ( size == 0 ) { throw GamaRuntimeException.error("Missing Parameter Exception", scope); }

		final String RFile = getPath();
		try {
			// Call R
			RCaller caller = new RCaller();

			String RPath = GamaPreferences.LIB_R.value(scope).getPath();
			caller.setRscriptExecutable(RPath);

			double[] vectorParam = new double[param.length(scope)];

			int k = 0;
			for ( Object o : param.iterable(scope) ) {
				vectorParam[k++] = Cast.asFloat(scope, o);
			}

			RCode c = new RCode();
			// Adding the parameters
			c.addDoubleArray("vectorParam", vectorParam);

			// Adding the codes in file
			List<String> R_statements = new ArrayList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			String fullPath = FileUtils.constructAbsoluteFilePath(scope, RFile, true);
			if ( DEBUG ) {
				scope.getGui().debug("Stats.R_compute_param.RScript:" + RPath);
				scope.getGui().debug("Stats.R_compute_param.Param:" + vectorParam.toString());
				scope.getGui().debug("Stats.R_compute_param.RFile:" + RFile);
				scope.getGui().debug("Stats.R_compute_param.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			BufferedReader br = new BufferedReader(fr);
			String statement;

			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
			}
			br.close();
			fr.close();
			caller.setRCode(c);

			GamaMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			String var = computeVariable(R_statements.get(R_statements.size() - 1).toString());
			caller.runAndReturnResult(var);

			// DEBUG:
			// java.lang.System.out.println("Name: '" + R_statements.length(scope) + "'");
			if ( DEBUG ) {
				scope.getGui().debug("Stats.R_compute_param.R_statements.length: '" + R_statements.size() + "'");
			}

			for ( String name : caller.getParser().getNames() ) {
				String[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// java.lang.System.out.println("Name: '" + name + "'");
				if ( DEBUG ) {
					scope.getGui().debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length +
						" - Value: " + results.toString());
				}

				result.put(name, GamaListFactory.create(scope, Types.NO_TYPE, results));
			}

			if ( DEBUG ) {
				scope.getGui().debug("Stats.R_compute_param.return:" + result.serialize(false));
			}

			setBuffer(result);

		} catch (Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage(), scope);
		}
	}

	public void doRFileEvaluate(final IScope scope) {
		final String RFile = getPath();
		try {
			// Call R
			RCaller caller = new RCaller();

			String RPath = GamaPreferences.LIB_R.value(scope).getPath();
			caller.setRscriptExecutable(RPath);
			// caller.setRscriptExecutable("\"" + RPath + "\"");
			// if(java.lang.System.getProperty("os.name").startsWith("Mac"))
			// {
			// caller.setRscriptExecutable(RPath);
			// }

			RCode c = new RCode();
			List<String> R_statements = new ArrayList<String>();

			// tmthai.begin----------------------------------------------------------------------------
			String fullPath = FileUtils.constructAbsoluteFilePath(scope, RFile, true);
			if ( DEBUG ) {
				scope.getGui().debug("Stats.R_compute.RScript:" + RPath);
				scope.getGui().debug("Stats.R_compute.RFile:" + RFile);
				scope.getGui().debug("Stats.R_compute.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			FileReader fr = new FileReader(fullPath);
			// tmthai.end----------------------------------------------------------------------------

			BufferedReader br = new BufferedReader(fr);
			String statement;
			while ((statement = br.readLine()) != null) {
				c.addRCode(statement);
				R_statements.add(statement);
				// java.lang.System.out.println(statement);
				if ( DEBUG ) {
					scope.getGui().debug("Stats.R_compute.statement:" + statement);
				}

			}

			fr.close();
			br.close();
			caller.setRCode(c);

			GamaMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			String var = computeVariable(R_statements.get(R_statements.size() - 1).toString());
			caller.runAndReturnResult(var);
			for ( String name : caller.getParser().getNames() ) {
				Object[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// for (int i = 0; i < results.length; i++) {
				// java.lang.System.out.println(results[i]);
				// }
				if ( DEBUG ) {
					scope.getGui().debug("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length +
						" - Value: " + results.toString());
				}
				result.put(name, GamaListFactory.createWithoutCasting(Types.NO_TYPE, results));
			}
			if ( DEBUG ) {
				scope.getGui().debug("Stats.R_compute.return:" + result.serialize(false));
			}
			// return result;
			setBuffer(result);

		} catch (Exception ex) {

			throw GamaRuntimeException.error("RCallerExecutionException " + ex.getMessage(), scope);
		}
	}

	private static String computeVariable(final String string) {
		String[] tokens = string.split("<-");
		return tokens[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO A faire.

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method getType()
	 * @see msi.gama.util.IContainer#getType()
	 */
	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

}
