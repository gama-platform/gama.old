/*********************************************************************************************
 * 
 * 
 * 'OptimizingSkill.java', in plugin 'msi.gaml.extensions.cplex', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.cplex;

import ilog.cplex.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.skills.Skill;
import msi.gaml.types.*;
import org.eclipse.core.runtime.FileLocator;

@skill(name = "optimizing", concept = { IConcept.OPTIMIZATION, IConcept.SKILL })
public class OptimizingSkill extends Skill {

	public static int q_variable = 0;
	public static int a_variable = 1;

	@action(name = "doTest")
	@args(names = { "myarg" })
	public Object primDoTest(final IScope scope) throws GamaRuntimeException {
		String message = (String) scope.getArg("myarg", IType.NONE);
		if ( message != null && message.length() > 0 ) {
			System.out.println(message);
		}
		try {
			/**/
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			// System.setProperty( "java.library.path",
			// "/Users/van-minhle/Applications/IBM/ILOG/CPLEX_Studio124/cplex/bin/x86-64_darwin9_gcc4.0"
			// );
			System.setProperty("java.library.path", nativeLibPath);
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			System.out.println("" + System.getProperty("java.library.path"));
			/**/
			IloCplex cplex = new IloCplex();
			/**/
			double[] lb = { 0, 0, 0 };
			double[] ub = { 40, Double.MAX_VALUE, Double.MAX_VALUE };
			IloNumVar[] x = cplex.numVarArray(3, lb, ub);
			double[] objectiveValue = { 1, 2, 3 };

			cplex.addMaximize(cplex.scalProd(x, objectiveValue));
			cplex.addLe(cplex.sum(cplex.prod(-1, x[0]), cplex.prod(1, x[1]), cplex.prod(1, x[2])), 20);
			cplex.addLe(cplex.sum(cplex.prod(1, x[0]), cplex.prod(-3, x[1]), cplex.prod(1, x[2])), 30);
			if ( cplex.solve() ) {
				System.out.println("status " + cplex.getStatus());
				System.out.println("solution " + cplex.getObjValue());
				double[] values = cplex.getValues(x);
				for ( int i = 0; i < values.length; i++ ) {
					System.out.println("x[" + i + "] = " + values[i]);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@action(name = "optimizeSigns")
	@args(names = { "N", "E", "X", "Mu", "P", "max_aet", "max_signs", "time_limit" })
	public Object primDoOptimizeSigns(final IScope scope) throws GamaRuntimeException {
		Integer N = (Integer) scope.getArg("N", IType.NONE);
		if ( N == null ) { return null; }
		System.out.println(N);
		GamaMatrix<Double> C = (GamaMatrix<Double>) scope.getArg("E", IType.NONE);
		if ( C == null ) { return null; }
		System.out.println(C.get(scope, 1, 1));
		GamaList<Integer> X = (GamaList<Integer>) scope.getArg("X", IType.NONE);
		if ( X == null || X.size() == 0 ) { return null; }
		System.out.println(X.size());
		GamaList<Double> Mu = (GamaList<Double>) scope.getArg("Mu", IType.NONE);
		if ( Mu == null || Mu.size() == 0 ) { return null; }
		System.out.println(Mu.size());
		GamaMatrix<Double> P = (GamaMatrix<Double>) scope.getArg("P", IType.NONE);
		if ( P == null ) { return null; }
		System.out.println(P.get(scope, 1, 1));
		Double Qmax = (Double) scope.getArg("max_aet", IType.NONE);
		if ( Qmax == null ) { return null; }
		System.out.println(Qmax);
		Integer K = (Integer) scope.getArg("max_signs", IType.NONE);
		if ( K == null ) { return null; }
		System.out.println(K);

		Integer timeLimit = (Integer) scope.getArg("time_limit", IType.NONE);
		if ( timeLimit == null ) {
			timeLimit = 120;
		}
		System.out.println(timeLimit);

		boolean[] isShelterVertex = new boolean[N];
		for ( int i = 0; i < N; i++ ) {
			isShelterVertex[i] = false;
		}
		for ( int i = 0; i < X.size(); i++ ) {
			isShelterVertex[X.get(i)] = true;
		}

		try {
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			System.setProperty("java.library.path", nativeLibPath);
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			System.out.println("" + System.getProperty("java.library.path"));
			IloCplex cplex = new IloCplex();

			List<VariableEntry> variableList = new ArrayList<VariableEntry>();
			List<Double> objectiveList = new ArrayList<Double>();
			VariableEntry[] q = new VariableEntry[N];
			for ( int i = 0; i < N; i++ ) {
				IloNumVar currentVariable = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
				VariableEntry variableDescription = new VariableEntry(q_variable, i, -1, 0, currentVariable);
				q[i] = variableDescription;
				variableList.add(variableDescription);
				objectiveList.add(Mu.get(i));
			}

			VariableEntry[][] a = new VariableEntry[N][N];
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					a[i][j] = null;
				}
			}

			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							IloNumVar currentVariable = cplex.numVar(0, 1, IloNumVarType.Int);
							VariableEntry variableDescription = new VariableEntry(a_variable, i, j, 0, currentVariable);
							a[i][j] = variableDescription;
							variableList.add(variableDescription);
							objectiveList.add(0.0);
						}
					}
				}
			}
			IloNumVar[] x = new IloNumVar[variableList.size()];
			double[] objectiveValues = new double[variableList.size()];
			for ( int i = 0; i < variableList.size(); i++ ) {
				x[i] = variableList.get(i).cplexReference;
				objectiveValues[i] = objectiveList.get(i).doubleValue();
			}
			cplex.addMinimize(cplex.scalProd(x, objectiveValues));

			// constraints: i in X, qi = 0;
			for ( int i = 0; i < N; i++ ) {
				if ( isShelterVertex[i] ) {
					cplex.addEq(q[i].cplexReference, 0);
				}
			}

			// constraints: qi - qj -Qmax.aij > Cij - Qmax
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						cplex.addGe(
							cplex.sum(cplex.prod(1.0, q[i].cplexReference), cplex.prod(-1, q[j].cplexReference),
								cplex.prod(-Qmax, a[i][j].cplexReference)), C.get(scope, i, j) - Qmax);
					}
				}
			}

			// constraints: i not in X, qi - sum(Pij.qi) + sun(Qmax.aij) > sum(Pij,Cij);
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					IloNumExpr leftHandExpression = cplex.prod(1.0, q[i].cplexReference);
					double rightHandExpression = 0;
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							leftHandExpression =
								cplex.sum(leftHandExpression, cplex.prod(-P.get(scope, i, j), q[j].cplexReference),
									cplex.prod(Qmax, a[i][j].cplexReference));
							rightHandExpression = rightHandExpression + P.get(scope, i, j) * C.get(scope, i, j);
						}
					}
					cplex.addGe(leftHandExpression, rightHandExpression);
				}
			}

			// constraints: i not in X, sum(aij) <= 1;
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							tempVariables.add(a[i][j].cplexReference);
						}
					}
					if ( tempVariables.size() > 0 ) {
						int tempN = tempVariables.size();
						IloNumVar[] tempExpression = new IloNumVar[tempN];
						double[] tempCoefficients = new double[tempN];
						for ( int j = 0; j < tempN; j++ ) {
							tempExpression[j] = tempVariables.get(j);
							tempCoefficients[j] = 1.0;
						}
						cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), 1.0);
					}
				}
			}

			// constraints: sum(aij) <= K;
			List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						tempVariables.add(a[i][j].cplexReference);
					}
				}
			}
			if ( tempVariables.size() > 0 ) {
				int tempN = tempVariables.size();
				IloNumVar[] tempExpression = new IloNumVar[tempN];
				double[] tempCoefficients = new double[tempN];
				for ( int j = 0; j < tempN; j++ ) {
					tempExpression[j] = tempVariables.get(j);
					tempCoefficients[j] = 1.0;
				}
				cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), K);
			}
			cplex.setParam(Param.TiLim, timeLimit);
			cplex.setParam(Param.TreLim, 80 * 1024 * 1024);
			List<Object> result = new ArrayList<Object>();
			if ( cplex.solve() ) {
				System.out.println("Solution status = " + cplex.getStatus());
				System.out.println("Solution value = " + cplex.getObjValue());
				result.add(cplex.getObjValue());
				double[] val = cplex.getValues(x);
				int ncols = cplex.getNcols();
				IList<GamaPoint> resultList = GamaListFactory.create(Types.POINT);
				for ( int j = 0; j < ncols; ++j ) {
					System.out.println("Column: " + j + " Value = " + val[j]);
					if ( val[j] > 0 ) {
						VariableEntry variableDescription = variableList.get(j);
						if ( variableDescription.type == OptimizingSkill.a_variable ) {
							GamaPoint p = new GamaPoint(variableDescription.index1, variableDescription.index2);
							resultList.add(p);
						}
					}
				}
				result.add(resultList);
			} else {
				System.out.println("Something wrong!");
			}
			cplex.end();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@action(name = "optimizeDirection")
	@args(names = { "N", "E", "X", "Mu", "P", "max_aet", "fixed_position", "time_limit" })
	public Object primDoOptimizeDirection(final IScope scope) throws GamaRuntimeException {
		Integer N = (Integer) scope.getArg("N", IType.NONE);
		if ( N == null ) {
			System.out.println("Cannot load N");
			return null;
		}
		System.out.println(N);
		GamaMatrix<Double> C = (GamaMatrix<Double>) scope.getArg("E", IType.NONE);
		if ( C == null ) {
			System.out.println("Cannot load E");
			return null;
		}
		System.out.println(C.get(scope, 1, 1));
		GamaList<Integer> X = (GamaList<Integer>) scope.getArg("X", IType.NONE);
		if ( X == null || X.size() == 0 ) {
			System.out.println("Cannot load X");
			return null;
		}
		System.out.println(X.serialize(false));
		GamaList<Double> Mu = (GamaList<Double>) scope.getArg("Mu", IType.NONE);
		if ( Mu == null || Mu.size() == 0 ) {
			System.out.println("Cannot load Mu");
			return null;
		}
		System.out.println(Mu.size());
		GamaMatrix<Double> P = (GamaMatrix<Double>) scope.getArg("P", IType.NONE);
		if ( P == null ) {
			System.out.println("Cannot load P");
			return null;
		}
		System.out.println(P.get(scope, 1, 1));
		Double Qmax = (Double) scope.getArg("max_aet", IType.NONE);
		if ( Qmax == null ) {
			System.out.println("Cannot load max_aet");
			return null;
		}
		System.out.println(Qmax);

		Integer timeLimit = (Integer) scope.getArg("time_limit", IType.NONE);
		if ( timeLimit == null ) {
			timeLimit = 120;
		}
		System.out.println(timeLimit);

		GamaList<Integer> fixedPostions = (GamaList<Integer>) scope.getArg("fixed_position", IType.NONE);
		if ( fixedPostions == null || fixedPostions.size() == 0 ) { return null; }
		System.out.println(fixedPostions.size());

		boolean[] isShelterVertex = new boolean[N];
		for ( int i = 0; i < N; i++ ) {
			isShelterVertex[i] = false;
		}
		for ( int i = 0; i < X.size(); i++ ) {
			isShelterVertex[X.get(i)] = true;
		}

		try {
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			System.setProperty("java.library.path", nativeLibPath);
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			System.out.println("" + System.getProperty("java.library.path"));
			IloCplex cplex = new IloCplex();

			List<VariableEntry> variableList = new ArrayList<VariableEntry>();
			List<Double> objectiveList = new ArrayList<Double>();
			VariableEntry[] q = new VariableEntry[N];
			for ( int i = 0; i < N; i++ ) {
				IloNumVar currentVariable = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
				VariableEntry variableDescription = new VariableEntry(q_variable, i, -1, 0, currentVariable);
				q[i] = variableDescription;
				variableList.add(variableDescription);
				objectiveList.add(Mu.get(i));
			}

			VariableEntry[][] a = new VariableEntry[N][N];
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					a[i][j] = null;
				}
			}

			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							IloNumVar currentVariable = cplex.numVar(0, 1, IloNumVarType.Int);
							VariableEntry variableDescription = new VariableEntry(a_variable, i, j, 0, currentVariable);
							a[i][j] = variableDescription;
							variableList.add(variableDescription);
							objectiveList.add(0.0);
						}
					}
				}
			}
			IloNumVar[] x = new IloNumVar[variableList.size()];
			double[] objectiveValues = new double[variableList.size()];
			for ( int i = 0; i < variableList.size(); i++ ) {
				x[i] = variableList.get(i).cplexReference;
				objectiveValues[i] = objectiveList.get(i).doubleValue();
			}
			cplex.addMinimize(cplex.scalProd(x, objectiveValues));

			// constraints: i in X, qi = 0;
			for ( int i = 0; i < N; i++ ) {
				if ( isShelterVertex[i] ) {
					cplex.addEq(q[i].cplexReference, 0);
				}
			}

			// constraints: qi - qj -Qmax.aij > Cij - Qmax
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						cplex.addGe(
							cplex.sum(cplex.prod(1.0, q[i].cplexReference), cplex.prod(-1, q[j].cplexReference),
								cplex.prod(-Qmax, a[i][j].cplexReference)), C.get(scope, i, j) - Qmax);
					}
				}
			}

			// constraints: i not in X, qi - sum(Pij.qi) + sun(Qmax.aij) > sum(Pij,Cij);
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					IloNumExpr leftHandExpression = cplex.prod(1.0, q[i].cplexReference);
					double rightHandExpression = 0;
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							leftHandExpression =
								cplex.sum(leftHandExpression, cplex.prod(-P.get(scope, i, j), q[j].cplexReference),
									cplex.prod(Qmax, a[i][j].cplexReference));
							rightHandExpression = rightHandExpression + P.get(scope, i, j) * C.get(scope, i, j);
						}
					}
					cplex.addGe(leftHandExpression, rightHandExpression);
				}
			}

			// constraints: i not in X, sum(aij) <= 1;
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							tempVariables.add(a[i][j].cplexReference);
						}
					}
					if ( tempVariables.size() > 0 ) {
						int tempN = tempVariables.size();
						IloNumVar[] tempExpression = new IloNumVar[tempN];
						double[] tempCoefficients = new double[tempN];
						for ( int j = 0; j < tempN; j++ ) {
							tempExpression[j] = tempVariables.get(j);
							tempCoefficients[j] = 1.0;
						}
						cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), 1.0);
					}
				}
			}

			// constraints: sum(aij) <= K;
			List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						tempVariables.add(a[i][j].cplexReference);
					}
				}
			}
			if ( tempVariables.size() > 0 ) {
				int tempN = tempVariables.size();
				IloNumVar[] tempExpression = new IloNumVar[tempN];
				double[] tempCoefficients = new double[tempN];
				for ( int j = 0; j < tempN; j++ ) {
					tempExpression[j] = tempVariables.get(j);
					tempCoefficients[j] = 1.0;
				}
				cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), fixedPostions.length(scope));
			}

			// constraints: aij = 0 forall i do not belong to fixed position
			for ( int i = 0; i < N; i++ ) {
				if ( !fixedPostions.contains(i) ) {
					for ( int j = 0; j < N; j++ ) {
						if ( a[i][j] != null ) {
							cplex.addEq(a[i][j].cplexReference, 0);
						}
					}
				}
			}

			cplex.setParam(Param.TiLim, timeLimit);
			cplex.setParam(Param.TreLim, 80 * 1024 * 1024);
			List<Object> result = new ArrayList<Object>();
			if ( cplex.solve() ) {
				System.out.println("Solution status = " + cplex.getStatus());
				System.out.println("Solution value = " + cplex.getObjValue());
				result.add(cplex.getObjValue());
				double[] val = cplex.getValues(x);
				int ncols = cplex.getNcols();
				IList<GamaPoint> resultList = GamaListFactory.create(Types.POINT);
				for ( int j = 0; j < ncols; ++j ) {
					System.out.println("Column: " + j + " Value = " + val[j]);
					if ( val[j] > 0 ) {
						VariableEntry variableDescription = variableList.get(j);
						if ( variableDescription.type == OptimizingSkill.a_variable ) {
							GamaPoint p = new GamaPoint(variableDescription.index1, variableDescription.index2);
							resultList.add(p);
						}
					}
				}
				result.add(resultList);
			} else {
				System.out.println("Something wrong!");
			}
			cplex.end();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@action(name = "computeAverageEvacuationTime")
	@args(names = { "N", "E", "X", "Mu", "P", "max_aet", "signs_from", "signs_to", "time_limit" })
	public Object primDoComputeAverageEvacuationTime(final IScope scope) throws GamaRuntimeException {
		Integer N = (Integer) scope.getArg("N", IType.NONE);
		if ( N == null ) { return null; }
		System.out.println(N);
		GamaMatrix<Double> C = (GamaMatrix<Double>) scope.getArg("E", IType.NONE);
		if ( C == null ) { return null; }
		System.out.println(C.get(scope, 1, 1));
		GamaList<Integer> X = (GamaList<Integer>) scope.getArg("X", IType.NONE);
		if ( X == null || X.size() == 0 ) { return null; }
		System.out.println(X.size());
		GamaList<Double> Mu = (GamaList<Double>) scope.getArg("Mu", IType.NONE);
		if ( Mu == null || Mu.size() == 0 ) { return null; }
		System.out.println(Mu.size());
		GamaMatrix<Double> P = (GamaMatrix<Double>) scope.getArg("P", IType.NONE);
		if ( P == null ) { return null; }
		System.out.println(P.get(scope, 1, 1));
		Double Qmax = (Double) scope.getArg("max_aet", IType.NONE);
		if ( Qmax == null ) { return null; }
		System.out.println(Qmax);

		Integer timeLimit = (Integer) scope.getArg("time_limit", IType.NONE);
		if ( timeLimit == null ) {
			timeLimit = 120;
		}
		System.out.println(timeLimit);

		GamaList<Integer> signs_from = (GamaList<Integer>) scope.getArg("signs_from", IType.NONE);
		if ( signs_from == null || signs_from.size() == 0 ) { return null; }
		System.out.println(signs_from.size());

		GamaList<Integer> signs_to = (GamaList<Integer>) scope.getArg("signs_to", IType.NONE);
		if ( signs_to == null || signs_to.size() == 0 ) { return null; }
		System.out.println(signs_to.size());

		boolean[] isShelterVertex = new boolean[N];
		for ( int i = 0; i < N; i++ ) {
			isShelterVertex[i] = false;
		}
		for ( int i = 0; i < X.size(); i++ ) {
			isShelterVertex[X.get(i)] = true;
		}

		try {
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			System.setProperty("java.library.path", nativeLibPath);
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
			System.out.println("" + System.getProperty("java.library.path"));
			IloCplex cplex = new IloCplex();

			List<VariableEntry> variableList = new ArrayList<VariableEntry>();
			List<Double> objectiveList = new ArrayList<Double>();
			VariableEntry[] q = new VariableEntry[N];
			for ( int i = 0; i < N; i++ ) {
				IloNumVar currentVariable = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
				VariableEntry variableDescription = new VariableEntry(q_variable, i, -1, 0, currentVariable);
				q[i] = variableDescription;
				variableList.add(variableDescription);
				objectiveList.add(Mu.get(i));
			}

			VariableEntry[][] a = new VariableEntry[N][N];
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					a[i][j] = null;
				}
			}

			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							IloNumVar currentVariable = cplex.numVar(0, 1, IloNumVarType.Int);
							VariableEntry variableDescription = new VariableEntry(a_variable, i, j, 0, currentVariable);
							a[i][j] = variableDescription;
							variableList.add(variableDescription);
							objectiveList.add(0.0);
						}
					}
				}
			}
			IloNumVar[] x = new IloNumVar[variableList.size()];
			double[] objectiveValues = new double[variableList.size()];
			for ( int i = 0; i < variableList.size(); i++ ) {
				x[i] = variableList.get(i).cplexReference;
				objectiveValues[i] = objectiveList.get(i).doubleValue();
			}
			cplex.addMinimize(cplex.scalProd(x, objectiveValues));

			// constraints: i in X, qi = 0;
			for ( int i = 0; i < N; i++ ) {
				if ( isShelterVertex[i] ) {
					cplex.addEq(q[i].cplexReference, 0);
				}
			}

			// constraints: qi - qj -Qmax.aij > Cij - Qmax
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						cplex.addGe(
							cplex.sum(cplex.prod(1.0, q[i].cplexReference), cplex.prod(-1, q[j].cplexReference),
								cplex.prod(-Qmax, a[i][j].cplexReference)), C.get(scope, i, j) - Qmax);
					}
				}
			}

			// constraints: i not in X, qi - sum(Pij.qi) + sun(Qmax.aij) > sum(Pij,Cij);
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					IloNumExpr leftHandExpression = cplex.prod(1.0, q[i].cplexReference);
					double rightHandExpression = 0;
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							leftHandExpression =
								cplex.sum(leftHandExpression, cplex.prod(-P.get(scope, i, j), q[j].cplexReference),
									cplex.prod(Qmax, a[i][j].cplexReference));
							rightHandExpression = rightHandExpression + P.get(scope, i, j) * C.get(scope, i, j);
						}
					}
					cplex.addGe(leftHandExpression, rightHandExpression);
				}
			}

			// constraints: i not in X, sum(aij) <= 1;
			for ( int i = 0; i < N; i++ ) {
				if ( !isShelterVertex[i] ) {
					List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
					for ( int j = 0; j < N; j++ ) {
						if ( C.get(scope, i, j) > 0 ) {
							tempVariables.add(a[i][j].cplexReference);
						}
					}
					if ( tempVariables.size() > 0 ) {
						int tempN = tempVariables.size();
						IloNumVar[] tempExpression = new IloNumVar[tempN];
						double[] tempCoefficients = new double[tempN];
						for ( int j = 0; j < tempN; j++ ) {
							tempExpression[j] = tempVariables.get(j);
							tempCoefficients[j] = 1.0;
						}
						cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), 1.0);
					}
				}
			}

			// constraints: sum(aij) <= K;
			List<IloNumVar> tempVariables = new ArrayList<IloNumVar>();
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						tempVariables.add(a[i][j].cplexReference);
					}
				}
			}
			if ( tempVariables.size() > 0 ) {
				int tempN = tempVariables.size();
				IloNumVar[] tempExpression = new IloNumVar[tempN];
				double[] tempCoefficients = new double[tempN];
				for ( int j = 0; j < tempN; j++ ) {
					tempExpression[j] = tempVariables.get(j);
					tempCoefficients[j] = 1.0;
				}
				cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), signs_from.length(scope));
			}

			// constraints: aij = 1 (i sign from and j sign to) otherwise
			for ( int i = 0; i < N; i++ ) {
				for ( int j = 0; j < N; j++ ) {
					if ( a[i][j] != null ) {
						if ( isExistSign(scope, i, j, signs_from, signs_to) ) {
							cplex.addEq(a[i][j].cplexReference, 1);
						} else {
							cplex.addEq(a[i][j].cplexReference, 0);
						}
					}

				}
			}

			cplex.setParam(Param.TiLim, timeLimit);
			cplex.setParam(Param.TreLim, 80 * 1024 * 1024);
			Double result = new Double(0);
			if ( cplex.solve() ) {
				System.out.println("Solution status = " + cplex.getStatus());
				System.out.println("Solution value = " + cplex.getObjValue());
				result = new Double(cplex.getObjValue());
			} else {
				System.out.println("Something wrong!");
			}
			cplex.end();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isExistSign(final IScope scope, final int ik, final int jk, final GamaList<Integer> signs_from,
		final GamaList<Integer> signs_to) {
		if ( signs_from == null || signs_from.length(scope) == 0 ) { return false; }
		if ( signs_to == null || signs_to.length(scope) == 0 ) { return false; }
		if ( signs_from.length(scope) != signs_to.length(scope) ) { return false; }
		for ( int k = 0; k < signs_from.length(scope); k++ ) {
			int i = signs_from.get(k);
			int j = signs_to.get(k);
			if ( ik == i && jk == j ) { return true; }
		}
		return false;
	}
}

class VariableEntry {

	public int index1;
	public int index2;
	public double value;
	public int type;
	public IloNumVar cplexReference;

	public VariableEntry(final int t, final int i1, final int i2, final double val, final IloNumVar ref) {
		type = t;
		index1 = i1;
		index2 = i2;
		value = val;
		cplexReference = ref;
	}

	public String getVariableName() {
		if ( type == OptimizingSkill.a_variable ) {
			return "a" + index1 + "->" + index2;
		} else {
			return "q" + index1;
		}
	}
}
