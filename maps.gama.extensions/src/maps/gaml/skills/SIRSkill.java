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

	@action(name = "RK4SIR")
	@args(names = { "S", "I", "R", "alpha", "beta", "n", "h" })
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

	    double n = (Double) scope.getArg("n", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIRinit, alpha, beta, 0.0, 0.0, 0.0, 0.0, n, h);
	}
	
	@action(name = "RK4SIRS")
	@args(names = { "S", "I", "R", "alpha", "beta", "gamma", "b", "d1", "d2", "n", "h" })
	public GamaList<Double> kr4SIRS(final IScope scope) throws GamaRuntimeException {
		
	    double sInit = (Double) scope.getArg("S", IType.FLOAT);
	    double iInit = (Double) scope.getArg("I", IType.FLOAT);
	    double rInit = (Double) scope.getArg("R", IType.FLOAT);
	    
	    GamaList<Double> SIRinit = new GamaList<Double>();
	    SIRinit.add(sInit);
	    SIRinit.add(iInit);
	    SIRinit.add(rInit);
	    
	    double alpha = (Double) scope.getArg("alpha", IType.FLOAT);
	    double beta = (Double) scope.getArg("beta", IType.FLOAT);
	    double gamma = (Double) scope.getArg("gamma", IType.FLOAT);
	
	    double b = (Double) scope.getArg("b", IType.FLOAT);
	    double d1 = (Double) scope.getArg("d1", IType.FLOAT);
	    double d2 = (Double) scope.getArg("d2", IType.FLOAT);

	    double n = (Double) scope.getArg("n", IType.FLOAT);
	    double h = (Double) scope.getArg("h", IType.FLOAT);
	
	    return rungeKuta4(SIRinit, alpha, beta, gamma, b, d1, d2, n, h);
	}
	
	GamaList<Double> SIR(final GamaList<Double> sir, double alpha ,double beta, double gamma, double b, double d1, double d2, double n) {

		double ds = (- beta * sir.get(0) * sir.get(1) / n) + (gamma * sir.get(2)) + (b * n)  - (d1 * sir.get(0));
		double di = (beta * sir.get(0) * sir.get(1) / n) - (alpha * sir.get(1)) - (d2 * sir.get(1));
		double dr = (alpha * sir.get(1)) - (gamma * sir.get(2)) - (d1 * sir.get(2));
		
		GamaList<Double> sirReturn = new GamaList<Double>();
		sirReturn.add(ds);
		sirReturn.add(di);
		sirReturn.add(dr);
		
		return sirReturn;
	}
	
	GamaList<Double> rungeKuta4(GamaList<Double> init, double alpha, double beta, double gamma, double b, double d1, double d2, double n, double h) {
		// GamaList<Double> temp = init.clone();
		GamaList<Double> sir = new GamaList<Double>();
		GamaList<Double> Q1 = SIR(init,alpha,beta, gamma, b, d1, d2,n);
		
		for(int i = 0; i <= 2; i++){
			sir.add(init.get(i) + (h / 2.0) * Q1.get(i));
		}
	
		GamaList<Double> Q2 = SIR(sir, alpha, beta,gamma, b, d1, d2, n);

		for(int i = 0; i <= 2; i++){
			sir.set(i, init.get(i) + (h / 2.0) * Q2.get(i));
		}

		GamaList<Double> Q3 = SIR(sir, alpha, beta,gamma, b, d1, d2, n);
		
		for(int i = 0; i <= 2; i++){
			sir.set(i, init.get(i) + h * Q3.get(i));
		}

		GamaList<Double> Q4 = SIR(sir, alpha, beta,gamma, b, d1, d2, n);

		for(int i = 0; i <= 2; i++){
			sir.set(i, init.get(i) + (h / 6.0) * (Q1.get(i) + 2.0 * Q2.get(i) + 2.0 * Q3.get(i) + Q4.get(i)));
		}

		return sir;
	}
}