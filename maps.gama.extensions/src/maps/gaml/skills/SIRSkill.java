package maps.gaml.skills;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@skill(name = "SIR")
public class SIRSkill extends Skill{

	@action(name = "RK4SI")
	@args(names = { "S", "I", "R", "beta", "gamma", "N", "h" })
	public GamaList<Double> kr4SI(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    
	    GamaList<Double> SIinit = new GamaList<Double>();
	    SIinit.add(sInit);
	    SIinit.add(iInit);
	    
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);
	    double gamma = (Double) scope.getArg("gamma", IType.FLOAT);
	    
	    double N = (Double) scope.getArg("N", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIinit, new SI(beta, gamma, N), h);
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