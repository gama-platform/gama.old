package msi.gaml.extensions.cplex;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.runtime.ExecutionStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IPath;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import ilog.cplex.*;
import ilog.concert.*;

@skill(name = "optimizing")
public class OptimizingSkill extends Skill{
	public static int q_variable = 0;
	public static int a_variable = 1;
	@action(name = "doTest")
	@args(names = { "myarg"})
	public Object primDoTest(final IScope scope) throws GamaRuntimeException {
		String message = (String) scope.getArg("myarg", IType.NONE);
		if ( (message != null) && (message.length() > 0) ){
			System.out.println(message);
		}
		try{
			/**/			
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			//System.setProperty( "java.library.path", "/Users/van-minhle/Applications/IBM/ILOG/CPLEX_Studio124/cplex/bin/x86-64_darwin9_gcc4.0" );
			System.setProperty( "java.library.path", nativeLibPath );
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
            System.out.println("" + System.getProperty("java.library.path"));
            /**/
            IloCplex cplex = new IloCplex();
            /**/
            double[] lb = {0, 0, 0};
            double[] ub = {40, Double.MAX_VALUE, Double.MAX_VALUE};
            IloNumVar[] x = cplex.numVarArray(3, lb, ub);
            double[] objectiveValue = {1, 2, 3};
            
            cplex.addMaximize(cplex.scalProd(x, objectiveValue));
            cplex.addLe(cplex.sum(cplex.prod(-1, x[0]), cplex.prod(1, x[1]), cplex.prod(1, x[2])), 20);
            cplex.addLe(cplex.sum(cplex.prod(1, x[0]), cplex.prod(-3, x[1]), cplex.prod(1, x[2])), 30);
            if (cplex.solve()){
                    System.out.println("status " + cplex.getStatus());
                    System.out.println("solution " + cplex.getObjValue());
                    double[] values = cplex.getValues(x);
                    for (int i= 0; i < values.length; i++){
                            System.out.println("x[" + i + "] = " + values[i]);
                    }
            }
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return null;
	}
	@action(name = "optimizeSigns")
	@args(names = { "N", "E", "X", "Mu", "P", "max_aet", "max_signs"})
	public Object primDoOptimizeSigns(final IScope scope) throws GamaRuntimeException {
		Integer N = (Integer) scope.getArg("N", IType.NONE);
		if ((N == null)){
			return null;
		}
		System.out.println(N);
		GamaMatrix<Double> C = (GamaMatrix<Double>) scope.getArg("E", IType.NONE);
		if ((C == null)){
			return null;
		}
		System.out.println(C.get(1, 1));
		GamaList<Integer> X = (GamaList<Integer>) scope.getArg("X", IType.NONE);
		if ((X == null) || (X.size() == 0)){
			return null;
		}
		System.out.println(X.size());
		GamaList<Double> Mu = (GamaList<Double>) scope.getArg("Mu", IType.NONE);
		if ((Mu == null) || (Mu.size() == 0)){
			return null;
		}
		System.out.println(Mu.size());
		GamaMatrix<Double> P = (GamaMatrix<Double>) scope.getArg("P", IType.NONE);
		if ((P == null)){
			return null;
		}
		System.out.println(P.get(1, 1));
		Double Qmax = (Double) scope.getArg("max_aet", IType.NONE);
		if ((Qmax == null)){
			return null;
		}
		System.out.println(Qmax);
		Integer K = (Integer) scope.getArg("max_signs", IType.NONE);
		if ((K == null)){
			return null;
		}
		System.out.println(K);
		
		boolean[] isShelterVertex = new boolean[N];
		for (int i = 0; i < N; i++){
			isShelterVertex[i] = false;
		}
		for (int i = 0; i<X.size(); i++){
			isShelterVertex[X.get(i)] = true;
		}
		
		try{
			URL urlRep = FileLocator.toFileURL(new URL("platform:/plugin/msi.gaml.extensions.cplex/"));
			String nativeLibPath = urlRep.getPath() + "lib";
			System.setProperty( "java.library.path", nativeLibPath );
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
            System.out.println("" + System.getProperty("java.library.path"));
            IloCplex cplex = new IloCplex();
            
            GamaList<VariableEntry> variableList = new GamaList<VariableEntry>();
            GamaList<Double> objectiveList = new GamaList<Double>();
            VariableEntry[] q = new VariableEntry[N];
            for (int i = 0;  i < N; i++){
            	IloNumVar currentVariable = cplex.numVar(0, Double.MAX_VALUE, IloNumVarType.Float);
            	VariableEntry variableDescription = new VariableEntry(q_variable, i, -1, 0, currentVariable);
            	q[i] = variableDescription;
            	variableList.add(variableDescription);
            	objectiveList.add(Mu.get(i));
            }
            
            VariableEntry[][] a = new VariableEntry[N][N];
            for (int i = 0; i < N; i++){
            	for (int j = 0; j < N; j++){
            		a[i][j] = null;
            	}
            }
            
            for (int i = 0; i < N; i++){
            	if (!isShelterVertex[i]){
            		for (int j = 0; j < N; j++){
            			if (C.get(i,j) > 0){
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
            for (int i = 0; i < variableList.size(); i++){
            	x[i] = variableList.get(i).cplexReference;
            	objectiveValues[i] = objectiveList.get(i).doubleValue();
            }
            cplex.addMinimize(cplex.scalProd(x, objectiveValues));
            
            //constraints: i in X, qi = 0;
            for (int i = 0; i < N; i++){
            	if (isShelterVertex[i])
            		cplex.addEq(q[i].cplexReference, 0);
            }
            
            //constraints: qi - qj -Qmax.aij > Cij - Qmax
            for (int i = 0; i < N; i++){
            	for (int j = 0; j < N; j++){
            		if (a[i][j] != null){
            			cplex.addGe(cplex.sum(cplex.prod(1.0, q[i].cplexReference),
            					cplex.prod(-1, q[j].cplexReference),
            					cplex.prod(-Qmax, a[i][j].cplexReference)
            					), C.get(i,j) - Qmax);
            		}
            	}
            }
            
            //constraints: i not in X, qi - sum(Pij.qi) + sun(Qmax.aij) > sum(Pij,Cij);
            for (int i = 0; i < N; i++){
            	if (!isShelterVertex[i]){
            		IloNumExpr leftHandExpression = cplex.prod(1.0, q[i].cplexReference);
            		double rightHandExpression = 0;
            		for (int j = 0; j < N; j++){
            			if (C.get(i,j) > 0){
            				leftHandExpression = cplex.sum(leftHandExpression,
                					cplex.prod(-P.get(i,j), q[j].cplexReference),
                					cplex.prod(Qmax, a[i][j].cplexReference));
                			rightHandExpression = rightHandExpression + 
                					P.get(i,j)*C.get(i,j);	
            			}
            		}
            		cplex.addGe(leftHandExpression, rightHandExpression);
            	}
            }
            
            //constraints: i not in X, sum(aij) <= 1;
            for (int i = 0; i < N; i++){
            	if (!isShelterVertex[i]){
            		GamaList<IloNumVar> tempVariables = new GamaList<IloNumVar>();
            		for (int j = 0; j < N; j++){
            			if (C.get(i,j) > 0){
            				tempVariables.add(a[i][j].cplexReference);
            			}
            		}
            		if (tempVariables.size() > 0){
            			int tempN = tempVariables.size();
            			IloNumVar[] tempExpression = new IloNumVar[tempN];
            			double[] tempCoefficients = new double[tempN];
            			for (int j = 0; j < tempN; j++){
            				tempExpression[j] = tempVariables.get(j);
            				tempCoefficients[j] = 1.0;
            			}
            			cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), 1.0);
            		}
            	}
            }
            
            //constraints: sum(aij) <= K;
            GamaList<IloNumVar> tempVariables = new GamaList<IloNumVar>();
            for (int i = 0; i < N; i++){
            	for (int j = 0; j < N; j++){
            		if (a[i][j] != null){
            			tempVariables.add(a[i][j].cplexReference);
            		}
            	}
            }
            if (tempVariables.size() > 0){
    			int tempN = tempVariables.size();
    			IloNumVar[] tempExpression = new IloNumVar[tempN];
    			double[] tempCoefficients = new double[tempN];
    			for (int j = 0; j < tempN; j++){
    				tempExpression[j] = tempVariables.get(j);
    				tempCoefficients[j] = 1.0;
    			}
    			cplex.addLe(cplex.scalProd(tempExpression, tempCoefficients), K);
    		}
            cplex.setParam(IloCplex.DoubleParam.TiLim, 1*60);
            cplex.setParam(IloCplex.DoubleParam.TreLim, 80*1024*1024);
            GamaList<Object> result = new GamaList<Object>();
            if ( cplex.solve() ) {
            	System.out.println("Solution status = " + cplex.getStatus()); 
            	System.out.println("Solution value = " + cplex.getObjValue());
            	result.add(cplex.getObjValue());
        	    double[] val = cplex.getValues(x);
        	    int ncols = cplex.getNcols();
        	    GamaList<GamaPoint> resultList = new GamaList<GamaPoint>(); 
        	    for (int j = 0; j < ncols; ++j){
        	    	System.out.println("Column: " + j + " Value = " + val[j]);
        	    	if ((val[j] > 0)){
        	    		VariableEntry variableDescription = variableList.get(j);
        	    		if ((variableDescription.type == OptimizingSkill.a_variable)){
        	    			GamaPoint p = new GamaPoint(variableDescription.index1, variableDescription.index2);
            	    		resultList.add(p);	
        	    		}
        	    	}
        	    }
        	    result.add(resultList);
    	    }
            else{
            	System.out.println("Something wrong!");
            }
    	    cplex.end();
    	    return result;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

class VariableEntry{
	public int index1;
	public int index2;
	public double value;
	public int type;
	public IloNumVar cplexReference;
	public VariableEntry(int t, int i1, int i2, double val, IloNumVar ref){
		type = t;
		index1 = i1;
		index2 = i2;
		value = val;
		cplexReference = ref;
	}
	public String getVariableName(){
		if (type == OptimizingSkill.a_variable){
			return "a"+index1+"->"+index2;
		}
		else{
			return "q"+index1;
		}
	}
}
