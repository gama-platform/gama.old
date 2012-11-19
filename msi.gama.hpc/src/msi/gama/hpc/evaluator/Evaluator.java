package msi.gama.hpc.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import msi.gama.kernel.batch.IExploration;

import org.basex.query.QueryException;

import fr.expression4j.basic.MathematicalElement;
import fr.expression4j.basic.impl.RealImpl;
import fr.expression4j.core.Expression;
import fr.expression4j.core.Parameters;
import fr.expression4j.core.exception.EvalException;
import fr.expression4j.core.exception.ParsingException;
import fr.expression4j.factory.ExpressionFactory;

public class Evaluator {
	
	String function;
	ArrayList<String> variables;
	String file;
	double fitness;
	short fitnessCombination = 2;
	
	public Evaluator(String function, ArrayList<String> variables, String file, short fitnessCombination) {
		super();
		this.function = function;
		this.variables = variables;
		this.file = file;
		this.fitnessCombination = fitnessCombination;
	}

	public double evaluate() {
		
		ArrayList<String> listChar = getCharacterVariableFromString(this.function);
		
		try {
			Expression expression = ExpressionFactory.createExpression(this.function.trim());
			Parameters parameters = ExpressionFactory.createParameters();
			
			for(int i =0; i < listChar.size(); i++) {
				System.out.println("variable : " + listChar.get(i) + " : " + variables.get(i));
				System.out.println("values : " + getValues(variables.get(i)));
				
				Double value = fitnessCombination == IExploration.C_MAX ? Collections.max(getValues(variables.get(i)))
						: fitnessCombination == IExploration.C_MIN ? Collections.min(getValues(variables.get(i)))
							: Evaluator.calculateMean(getValues(variables.get(i)), true);
						
				System.out.println("normalized value " + value);
				
				RealImpl param = new RealImpl(value);
				parameters.addParameter(listChar.get(i),param);
			}
			
			MathematicalElement result = expression.evaluate(parameters);
			System.out.println(result.getRealValue());
			this.fitness = result.getRealValue();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.fitness;
	}
	
	public ArrayList<String> getCharacterVariableFromString(String strFunc) {
		
		ArrayList<String> listCharacterVariable = new ArrayList<String>();
		
		String[] temp = strFunc.split("=", 2);
		
		int beginIndex = temp[0].indexOf("(");
		int endIndex = temp[0].indexOf(")");
		
		temp[0] = temp[0].substring(beginIndex + 1, endIndex); 
		String[] arrVar = temp[0].split(",");
		
		for(int i =0; i < arrVar.length; i ++)
			listCharacterVariable.add(arrVar[i]);
		
		return listCharacterVariable;
	}
	
	public ArrayList<Double> getValues(String variable) {
		ArrayList<String> arrayString = this.getOutput(this.file, variable);
		ArrayList<Double> arrayDouble = new ArrayList<Double>();
		for(String value : arrayString)
			arrayDouble.add(Double.valueOf(value));
		return arrayDouble;
	}
	 
	 public ArrayList<String> getOutput(String outputFile, String variable) {

		 String strDocInput = "doc('" + outputFile + "')";

		 String strQuery = "for $x in"
				 +	strDocInput
				 +	"//Simulation/Step/Variable" 
				 +	" where $x/@name='"
				 +	variable
				 +	"' return ($x/@value/string())";
		 try {
			 return QueryUtilities.query(strQuery);
		 } catch (QueryException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 return null;
	 }
	 
	 public static double calculateMean(Collection values, boolean includeNullAndNaN) {

		 if (values == null) {
	            throw new IllegalArgumentException("Null 'values' argument.");
	        }
	        int count = 0;
	        double total = 0.0;
	        Iterator iterator = values.iterator();
	        while (iterator.hasNext()) {
	            Object object = iterator.next();
	            if (object == null) {
	                if (includeNullAndNaN) {
	                    return Double.NaN;
	                }
	            }
	            else {
	                if (object instanceof Number) {
	                    Number number = (Number) object;
	                    double value = number.doubleValue();
	                    if (Double.isNaN(value)) {
	                        if (includeNullAndNaN) {
	                            return Double.NaN;
	                        }
	                    }
	                    else {
	                        total = total + number.doubleValue();
	                        count = count + 1;
	                    }
	                }
	            }
	        }
	        
	        return total / count;
	 }
	 
	 public static void main(String[] args) {
		 String function = "f(x,y)=x+y";
		 ArrayList<String> variables = new ArrayList<String>();
		 variables.add("number_of_preys");
		 variables.add("number_of_predators");
		 String file = "/Users/langthang/Desktop/MonGAMA/eclipse/tmp/output/experiment_out_0/simulation-outputs.xml";
		 short fitnessCombination = 2;
		 Evaluator evaluator = new Evaluator(function, variables, file, fitnessCombination);
		 double fitness = evaluator.evaluate();
	 }
}
