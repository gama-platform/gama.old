/*******************************************************************************************************
 *
 * msi.gama.util.file.RFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import rcaller.RCaller;
import rcaller.RCode;
import ummisco.gama.dev.utils.DEBUG;

@file (
		name = "R",
		extensions = { "r" },
		buffer_type = IType.MAP,
		buffer_content = IType.LIST,
		buffer_index = IType.STRING,
		concept = { IConcept.FILE, IConcept.R },
		doc = @doc ("Represents an R file. The internal representation is a map of lists (the result of the evaluation)"))
@SuppressWarnings ({ "rawtypes" })
public class RFile extends GamaFile<IMap, Object> {

	// GamaMap<String, IList>, IList, String, IList
	private final IContainer parameters;

	@doc (
			value = "This file constructor allows to read a R file",
			examples = { @example (
					value = "file f <-R_file(\"file.r\");",
					isExecutable = false) })
	public RFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		parameters = null;
	}

	@doc (
			value = "This file constructor allows to store a map in a R file (it does not save it - just store it in memory)",
			examples = { @example (
					value = "file f <-R_file(\"file.r\",map([\"param1\"::1.0,\"param2\"::10.0 ]));",
					isExecutable = false) })

	public RFile(final IScope scope, final String pathName, final IMap p) {
		super(scope, pathName, p);
		parameters = p;
	}

	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		final StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for (final Object s : getBuffer().iterable(scope)) {
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
		if (getBuffer() != null) { return; }
		if (parameters == null) {
			doRFileEvaluate(scope);
		} else {
			doRFileEvaluate(scope, this.parameters);
		}

	}

	public void doRFileEvaluate(final IScope scope, final IContainer param) {
		// final int size = param.length(scope);
		// if ( size == 0 ) { throw GamaRuntimeException.error("Missing
		// Parameter Exception", scope); }

		final String RFile = getPath(scope);
		try {
			// Call R
			final RCaller caller = new RCaller();

			final String RPath = GamaPreferences.External.LIB_R.value(scope).getPath(scope);
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
			final List<String> R_statements = new ArrayList<>();

			// tmthai.begin----------------------------------------------------------------------------
			final String fullPath = RFile; // AD already absolute FileUtils.constructAbsoluteFilePath(scope, RFile,
											// true);
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Stats.R_compute_param.RScript:" + RPath);
				DEBUG.OUT("Stats.R_compute_param.Param:" + Arrays.toString(vectorParam));
				DEBUG.OUT("Stats.R_compute_param.RFile:" + RFile);
				DEBUG.OUT("Stats.R_compute_param.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			try (FileReader fr = new FileReader(fullPath); BufferedReader br = new BufferedReader(fr)) {
				// tmthai.end----------------------------------------------------------------------------

				String statement;

				while ((statement = br.readLine()) != null) {
					c.addRCode(statement);
					R_statements.add(statement);
					// DEBUG.OUT(statement);
				}
			}
			caller.setRCode(c);

			final IMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			final String var = computeVariable(R_statements.get(R_statements.size() - 1));
			caller.runAndReturnResult(var);

			// DEBUG:
			// DEBUG.OUT("Name: '" +
			// R_statements.length(scope) + "'");
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Stats.R_compute_param.R_statements.length: '" + R_statements.size() + "'");
			}

			for (final String name : caller.getParser().getNames()) {
				String[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// DEBUG.OUT("Name: '" + name + "'");
				if (DEBUG.IS_ON()) {
					DEBUG.OUT("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length
							+ " - Value: " + Arrays.toString(results));
				}

				result.put(name, GamaListFactory.create(scope, Types.NO_TYPE, results));
			}

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Stats.R_compute_param.return:" + result.serialize(false));
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

			final String RPath = GamaPreferences.External.LIB_R.value(scope).getPath(scope);
			caller.setRscriptExecutable(RPath);
			// caller.setRscriptExecutable("\"" + RPath + "\"");
			// if(java.lang.System.getProperty("os.name").startsWith("Mac"))
			// {
			// caller.setRscriptExecutable(RPath);
			// }

			final RCode c = new RCode();
			final List<String> R_statements = new ArrayList<>();

			// tmthai.begin----------------------------------------------------------------------------
			final String fullPath = RFile; // FileUtils.constructAbsoluteFilePath(scope, RFile, true);
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Stats.R_compute.RScript:" + RPath);
				DEBUG.OUT("Stats.R_compute.RFile:" + RFile);
				DEBUG.OUT("Stats.R_compute.fullPath:" + fullPath);
			}

			// FileReader fr = new FileReader(RFile);
			try (final FileReader fr = new FileReader(fullPath); final BufferedReader br = new BufferedReader(fr);) {

				// tmthai.end----------------------------------------------------------------------------

				String statement;
				while ((statement = br.readLine()) != null) {
					c.addRCode(statement);
					R_statements.add(statement);
					// DEBUG.OUT(statement);
					if (DEBUG.IS_ON()) {
						DEBUG.OUT("Stats.R_compute.statement:" + statement);
					}

				}
			}
			caller.setRCode(c);

			final IMap<String, IList> result = GamaMapFactory.create(Types.STRING, Types.LIST);

			final String var = computeVariable(R_statements.get(R_statements.size() - 1));
			caller.runAndReturnResult(var);
			for (final String name : caller.getParser().getNames()) {
				Object[] results = null;
				results = caller.getParser().getAsStringArray(name);
				// for (int i = 0; i < results.length; i++) {
				// DEBUG.OUT(results[i]);
				// }
				if (DEBUG.IS_ON()) {
					DEBUG.OUT("Stats.R_compute_param.caller.Name: '" + name + "' length: " + results.length
							+ " - Value: " + Arrays.toString(results));
				}
				result.put(name, GamaListFactory.createWithoutCasting(Types.NO_TYPE, results));
			}
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("Stats.R_compute.return:" + result.serialize(false));
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
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.util.IContainer#getGamlType()
	 */
	@Override
	public IContainerType<IGamaFile> getGamlType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

}
