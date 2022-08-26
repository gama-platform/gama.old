/*******************************************************************************************************
 *
 * ExplicitExploration.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class ExplicitExploration.
 */
@symbol (
		name = IKeyword.EXPLICIT,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")),
				@facet (
						name = ExplicitExploration.PARAMETER_SET,
						type = IType.LIST,
						of = IType.MAP,
						optional = false,
						doc = @doc ("the list of parameter sets to explore; a parameter set is defined by a map: key: name of the variable, value: expression for the value of the variable")
						),
				@facet(
						name = ExplicitExploration.PARAMETER_CSV_PATH,
						type= IType.STRING,
						optional=true,
						doc= @doc ("path to csv files for parameter value")
						)
		},
		omissible = IKeyword.NAME)


@doc (
		value = "This algorithm run simulations with the given parameter sets",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the `explicit` statement uses `method explicit` instead of the expected `explicit name: id` : ",
				examples = { @example (
						value = "method explicit [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method explicit parameter_sets:[[\"a\"::0.5, \"b\"::10],[\"a\"::0.1, \"b\"::100]]; ",
								isExecutable = false) }) })
public class ExplicitExploration extends AExplorationAlgorithm {

	/** The Constant PARAMETER_SET. */
	protected static final String PARAMETER_SET = "parameter_sets";

	/*I add this*/
	protected static final String PARAMETER_CSV_PATH = "CSV";
	protected String path =null;


	/** The parameter sets. */
	protected List<Map<String, Object>> parameterSets;

	/**
	 * Instantiates a new explicit exploration.
	 *
	 * @param desc the desc
	 */
	public ExplicitExploration(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		if (hasFacet(PARAMETER_CSV_PATH)) {
			IExpression path_facet= getFacet(PARAMETER_CSV_PATH);
			path= Cast.asString(scope, path_facet.value(scope));
			String new_path= scope.getExperiment().getWorkingPath() + "/" +path;
			try {
				List<ParametersSet> solutions = buildParametersFromCSV(scope,new_path,new ArrayList<>());
				if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
					currentExperiment.launchSimulationsWithSolution(solutions);
				} else {
					for (ParametersSet sol : solutions) { currentExperiment.launchSimulationsWithSolution(sol); }
				}
			} catch (FileNotFoundException e) {
				throw GamaRuntimeException.error("File not found", scope);
			}
		} else {

			List<ParametersSet> solutions = buildParameterSets(scope, new ArrayList<>(), 0);
			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
				currentExperiment.launchSimulationsWithSolution(solutions);
			} else {
				for (ParametersSet sol : solutions) { currentExperiment.launchSimulationsWithSolution(sol); }
			}
		}
	}

	@SuppressWarnings ("unchecked")
	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, final List<ParametersSet> sets, final int index) {
		IExpression psexp = getFacet(PARAMETER_SET);
		parameterSets = Cast.asList(scope, psexp.value(scope));

		for (Map<String, Object> parameterSet : parameterSets) {
			ParametersSet p = new ParametersSet();
			for (String v : parameterSet.keySet()) {
				Object val = parameterSet.get(v);
				p.put(v, val instanceof IExpression ? ((IExpression) val).value(scope) : val);
			}
			sets.add(p);
		}
		return sets;
	}

	
	/**
	 * Create a List of Parameters Set with values contains in a CSV file
	 * @param scope
	 * @param path
	 * @param sets
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<ParametersSet> buildParametersFromCSV(final IScope scope,final String path,final List<ParametersSet> sets) throws FileNotFoundException{
		List<Map<String,Object>> parameters = new ArrayList<>();
		try {
		      File file = new File(path);
		      FileReader fr = new FileReader(file);
		      BufferedReader br = new BufferedReader(fr);
		      String line = " ";
		      String[] tempArr;
		      List<String> list_name= new ArrayList<>();
		      int i=0;
		      while ((line = br.readLine()) != null) {
		        tempArr = line.split(",");
		        for (String tempStr: tempArr) {
		        	if (i==0) {
		        		list_name.add(tempStr);
		        	}
		        }
		        if(i>0) {
		        	Map<String,Object> temp_map= new HashMap<>();
		        	for(int y=0;y<tempArr.length;y++) {
		        		temp_map.put(list_name.get(y),tempArr[y]);
		        	}
		        	parameters.add(temp_map);
		        }
		        i++;
		      }
		      br.close();
		    }
		    catch(IOException ioe) {
		    	throw GamaRuntimeException.error("Error during the reading of the CSV file", scope);
		    }
		
		for (Map<String, Object> parameterSet : parameters) {
			ParametersSet p = new ParametersSet();
			for (String v : parameterSet.keySet()) {
				Object val = parameterSet.get(v);
				p.put(v, val instanceof IExpression ? ((IExpression) val).value(scope) : val);
			}
			sets.add(p);
		}

		return sets;
	}

	public static void main(String[] args) throws Exception {
	}

}