package maps.gaml.skills;

import java.util.HashMap;
import java.util.Map.Entry;

import maps.gaml.edpSystem.SEIR;
import maps.gaml.edpSystem.SI;
import maps.gaml.edpSystem.SIRS;
import maps.gaml.edpSystem.SystemEDP;
import maps.gaml.edpSystem.UserEDPSystem;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@doc("The EDP skill is intended to define the set of actions for an agent to integrate EDP Systems")
@skill(name = "EDP")
public class EDPSkill extends Skill{
	
	// TODO : Mettre un attribut SystemEDP

	@action(name = "RK4SI")
	@args(names = { "S", "I", "beta", "nu", "N", "h" })
	public GamaList<Double> kr4SI(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    
	    GamaList<Double> SIinit = new GamaList<Double>();
	    SIinit.add(sInit);
	    SIinit.add(iInit);
	    
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);
	    double nu = (Double) scope.getArg("nu", IType.FLOAT);
	    
	    double N = (Double) scope.getArg("N", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIinit, new SI(beta, nu, N), h);
	}
	
	@action(name = "RK4SIR")
	@args(names = { "S", "I", "R", "alpha", "beta", "N", "h" })
	public GamaList<Double> kr4SIR(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    double rInit = (Double) scope.getArg("R", IType.FLOAT);
	    
	    GamaList<Double> SIRinit = new GamaList<Double>();
	    SIRinit.add(sInit);
	    SIRinit.add(iInit);
	    SIRinit.add(rInit);
	    
	    double alpha = (Double) scope.getArg("alpha", IType.FLOAT);
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);

	    double N = (Double) scope.getArg("N", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIRinit, new SIRS(alpha, beta, 0.0, 0.0, 0.0, 0.0, N), h);
	}
	
	@action(name = "RK4SIRS")
	@args(names = { "S", "I", "R", "alpha", "beta", "gamma", "b", "d1", "d2", "N", "h" })
	public GamaList<Double> kr4SIRS(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    double rInit = (Double) scope.getArg("R", IType.FLOAT);
	    
	    GamaList<Double> SIRSinit = new GamaList<Double>();
	    SIRSinit.add(sInit);
	    SIRSinit.add(iInit);
	    SIRSinit.add(rInit);
	    
	    double alpha = (Double) scope.getArg("alpha", IType.FLOAT);
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);
	    double gamma = (Double) scope.getArg("gamma", IType.FLOAT);
	
	    double b = (Double) scope.getArg("b", IType.FLOAT);
	    double d1 = (Double) scope.getArg("d1", IType.FLOAT);
	    double d2 = (Double) scope.getArg("d2", IType.FLOAT);

	    double N = (Double) scope.getArg("N", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIRSinit, new SIRS(alpha, beta, gamma, b, d1, d2, N), h);
	}

	@action(name = "RK4SEIR")
	@args(names = { "S", "E", "I", "R", "alpha", "beta", "a", "b", "d", "N", "h" })
	public GamaList<Double> kr4SEIR(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double eInit = (Double) scope.getArg("E", IType.FLOAT);	    
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    double rInit = (Double) scope.getArg("R", IType.FLOAT);
	    
	    GamaList<Double> SEIRinit = new GamaList<Double>();
	    SEIRinit.add(sInit);
	    SEIRinit.add(eInit);
	    SEIRinit.add(iInit);
	    SEIRinit.add(rInit);
	    
	    double alpha = (Double) scope.getArg("alpha", IType.FLOAT);
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);
	
	    double a = (Double) scope.getArg("a", IType.FLOAT);	    
	    double b = (Double) scope.getArg("b", IType.FLOAT);
	    double d = (Double) scope.getArg("d", IType.FLOAT);

	    double N = (Double) scope.getArg("N", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SEIRinit, new SEIR(alpha, beta, a, b, d, N), h);
	}	
	
//	@action(name = "RK4")
//	@args(names = {"equations", "var", "value", "param", "h" })
//	public GamaList<Double> kr4User(final IScope scope) throws GamaRuntimeException {
//		GamaList<String> equations = new GamaList<String>();
//		GamaList<String> varName = new GamaList<String>();	
//		GamaList<Double> initVal = new GamaList<Double>();
//		GamaMap param = new GamaMap(); 	
//		double h = 0.0;
//		
//		equations = (GamaList<String>) scope.getArg("equations", IType.LIST);
//		varName = (GamaList<String>) scope.getArg("var", IType.LIST);		
//		initVal = (GamaList<Double>) scope.getArg("value", IType.LIST);
//		param = (GamaMap) scope.getArg("param", IType.MAP);
//		h = (Double) scope.getArg("h", IType.FLOAT);		
//		
//		return rungeKuta4(initVal, new UserEDPSystem(equations, varName, param),h);
//	}
	
	@action(name = "RK4")
	@args(names = {"equations", "value", "param", "h" })
	public GamaList<Double> kr4User(final IScope scope) throws GamaRuntimeException {
		GamaList<String> equations = new GamaList<String>();
		GamaList<String> varName = new GamaList<String>();	
		GamaList<Double> initVal = new GamaList<Double>();
		GamaMap param = new GamaMap(); 	
		double h = 0.0;
				
		HashMap<String,String> equationsVar = (HashMap<String,String>) scope.getArg("equations", IType.MAP);
		initVal = (GamaList<Double>) scope.getArg("value", IType.LIST);
		param = (GamaMap) scope.getArg("param", IType.MAP);
		h = (Double) scope.getArg("h", IType.FLOAT);	
		
		for(Entry<String, String> entry : equationsVar.entrySet()) {
		    varName.add(entry.getKey());
		   equations.add(entry.getValue());
		}	
		
		GamaList<Double> intermVal = initVal.clone();
		UserEDPSystem systEDP = new UserEDPSystem(equations, varName, param);		
		
		return rungeKuta4(initVal, systEDP,h);
	}
	
//	@action(name = "RK4iterated")
//	@args(names = {"equations", "var", "value", "param", "h", "nbSteps" })
//	public GamaList<Double> kr4UserIterated(final IScope scope) throws GamaRuntimeException {
//		GamaList<String> equations = new GamaList<String>();
//		GamaList<String> varName = new GamaList<String>();	
//		GamaList<Double> initVal = new GamaList<Double>();
//		GamaMap param = new GamaMap(); 	
//		double h = 0.0;
//		int nbSteps = 0;
//				
//		equations = (GamaList<String>) scope.getArg("equations", IType.LIST);
//		varName = (GamaList<String>) scope.getArg("var", IType.LIST);		
//		initVal = (GamaList<Double>) scope.getArg("value", IType.LIST);
//		param = (GamaMap) scope.getArg("param", IType.MAP);
//		h = (Double) scope.getArg("h", IType.FLOAT);	
//		nbSteps = (Integer) scope.getArg("nbSteps", IType.INT);
//		
//		GamaList<Double> intermVal = initVal.clone();
//		
//		for(int i =0; i<nbSteps;i++){
//			intermVal = rungeKuta4(intermVal, new UserEDPSystem(equations, varName, param), h);
//		}
//		
//		return intermVal;
//	}

	
	@action(name = "RK4iterated")
	@args(names = {"equations", "value", "param", "h", "nbSteps" })
	public GamaList<Double> kr4UserIterated(final IScope scope) throws GamaRuntimeException {
		GamaList<String> equations = new GamaList<String>();
		GamaList<String> varName = new GamaList<String>();	
		GamaList<Double> initVal = new GamaList<Double>();
		GamaMap param = new GamaMap(); 	
		double h = 0.0;
		int nbSteps = 0;
				
		HashMap<String,String> equationsVar = (HashMap<String,String>) scope.getArg("equations", IType.MAP);
		varName = (GamaList<String>) scope.getArg("var", IType.LIST);		
		initVal = (GamaList<Double>) scope.getArg("value", IType.LIST);
		param = (GamaMap) scope.getArg("param", IType.MAP);
		h = (Double) scope.getArg("h", IType.FLOAT);	
		nbSteps = (Integer) scope.getArg("nbSteps", IType.INT);
		
		for(Entry<String, String> entry : equationsVar.entrySet()) {
		    varName.add(entry.getKey());
		   equations.add(entry.getValue());
		}	
		
		GamaList<Double> intermVal = initVal.clone();
		UserEDPSystem systEDP = new UserEDPSystem(equations, varName, param);
		
		for(int i =0; i<nbSteps;i++){
			intermVal = rungeKuta4(intermVal, systEDP, h);
		}
		
		return intermVal;
	}	
	
	GamaList<Double> rungeKuta4(GamaList<Double> init, SystemEDP edp, double h) {
		GamaList<Double> edp_temp = new GamaList<Double>();
		GamaList<Double> Q1 = edp.compute(init);
		int numEq = edp.getNumberEquation();
		
		for(int i = 0; i <= numEq-1; i++){
			edp_temp.add(init.get(i) + (h / 2.0) * Q1.get(i));
		}
	
		GamaList<Double> Q2 = edp.compute(edp_temp);

		for(int i = 0; i <= numEq-1; i++){
			edp_temp.set(i, init.get(i) + (h / 2.0) * Q2.get(i));
		}

		GamaList<Double> Q3 = edp.compute(edp_temp);
		
		for(int i = 0; i <= numEq-1; i++){
			edp_temp.set(i, init.get(i) + h * Q3.get(i));
		}

		GamaList<Double> Q4 = edp.compute(edp_temp);

		for(int i = 0; i <= numEq-1; i++){
			edp_temp.set(i, init.get(i) + (h / 6.0) * (Q1.get(i) + 2.0 * Q2.get(i) + 2.0 * Q3.get(i) + Q4.get(i)));
		}

		return edp_temp;
	}
}