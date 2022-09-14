package msi.gama.kernel.batch.exploration.sampling;

/**
*
* @author Tom ROY
*
*/
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;

/**
 * 
 * This class create a Latin Hypercube Sampling.
 *
 */

public class LatinhypercubeSampling extends SamplingUtils{
	
	public LatinhypercubeSampling() { }
	
	public static double nextDouble(double min, double max,Random r) {
        return min + r.nextDouble() * (max - min);
    }

    public static int nextInt(int n,Random r) {
        return r.nextInt(n);
    }


    public List<Double> shuffle(List<Double> array,Random r) {
        for (int i = array.size() - 1; i >= 1; i--) {
            int j = nextInt(i + 1,r);
            if (i != j) {
                double temp = array.get(i);
                array.set(i,array.get(j));
                array.set(j,temp);
            }
        }
        return array;
    }

    /**
     * Build a sample with values between 0 and 1
     * @param N: number of samples. 
     * @param names : Names of parameters
     * @param r : Random object
     * @return sample with values between 0 and 1
     */
    public Map<String,List<Double>> generate(int N, List<String> names,Random r) {
        Map<String,List<Double>> results= new LinkedHashMap<>();
        List<Double> temp=new ArrayList<>();
        double d = 1.0 / N;
        for (int i = 0; i < names.size(); i++) {
            for (int j = 0; j < N; j++) {
                temp.add(nextDouble(j * d, (j + 1) * d,r));
            }
        }
        for(int i=0; i<names.size();i++){
            List<Double> new_temp= new ArrayList<>(shuffle(temp,r));
            results.put(names.get(i),new_temp);

        }
        
        return results;
    }

    /**
     * Transform a Map of List into a List of map
     * @param MapList
     * @param names: Names of parameters
     * @return List of map
     */
    public  List<Map<String,Double>>  transformMapListToListMap(Map<String,List<Double>> MapList,List<String> names){
        List<Map<String,Double>> ListMap= new ArrayList<>();
        for(int i=0;i<MapList.get(names.get(0)).size();i++){
            Map<String,Double> tempMap=new LinkedHashMap<>();
            for(int j=0;j<names.size();j++){
                tempMap.put(names.get(j),MapList.get(names.get(j)).get(i));
            }
            ListMap.add(tempMap);
        }
        return ListMap;
    }

    /**
     * Building  Latin Hypercube samples of size N*Number of parameters
     * @param N : Number of samples
     * @param inputs : Inputs with shape: Map<String,Map<String,List<Object>>>
     * Example: {Var1={Int,[0,10]},Var2={Double,[0,1]}
     * @param r : a Random object
     * @return
     */
    public List<ParametersSet> LatinHypercubeSamples(int N, List<Batch> parameters,Random r,IScope scope){
    	List<ParametersSet>   finalSamp= new ArrayList<>();
        List<String> names= new ArrayList<>();
        for(int i=0;i<parameters.size();i++) {
        	names.add(parameters.get(i).getName());
        }
        Map<String,List<Double>> sampletempmap= generate(N,names,r);
        List<Map<String,Double>>  sampling =transformMapListToListMap(sampletempmap,names);    
        finalSamp= BuildParametersSetfromSample(scope,parameters,sampling);  
        return finalSamp;
        
    }
}
