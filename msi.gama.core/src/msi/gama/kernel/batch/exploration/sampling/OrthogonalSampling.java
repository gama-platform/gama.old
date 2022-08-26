package msi.gama.kernel.batch.exploration.sampling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.runtime.IScope;
/**
 * 
 * @author tomroy
 *
 */

/**
 * 
 * 
 * Orthogonal sampling for exhaustive exploration
 *
 */
public class OrthogonalSampling extends SamplingUtils {
	
	/**
	 * Build a Cut List with value between 0 and 1
	 * @param sample
	 * @return
	 */
	public List<Double> buildCut(int sample){
        List<Double> tmp= new ArrayList<>();
        for(int i=0;i<sample+1;i++){
            tmp.add(((double)i)/sample);
        }

        return tmp;

    }
	/**
	 * Build the first part of the sample
	 * @param sample
	 * @param n
	 * @param ParametersNames
	 * @param r
	 * @return
	 */
    public List<Map<String,Double>> buildU(int sample, int n,List<String> ParametersNames,Random r){
        List<Map<String,Double>> tmpL= new ArrayList<>();
        for(int i=0;i<sample;i++){
            Map<String,Double> tmpMap=new LinkedHashMap<>();
            for(int j=0;j<n;j++){
                tmpMap.put(ParametersNames.get(j),r.nextDouble());
            }
            tmpL.add(tmpMap);
        }
        return tmpL;

    }
    /**
     * Build vector a
     * @param sample
     * @param cut
     * @return
     */
    public List<Double> buildA(int sample,List<Double> cut){
        List<Double> a= new ArrayList<>(cut);
        a.remove(cut.size()-1);
        return a;
    }
    /**
     * Build vector b
     * @param sample
     * @param cut
     * @return
     */
    public List<Double> buildB(int sample,List<Double> cut){
        List<Double> b= new ArrayList<>(cut);
        b.remove(0);
        return b;
    }

    /**
     * build the second part of the sample
     * @param sample
     * @param ParametersNames
     * @param u
     * @param a
     * @param b
     * @return
     */
    public List<Map<String,Double>> changeValueSample(int sample,List<String> ParametersNames,List<Map<String,Double>> u,List<Double> a, List<Double> b){
        List<Map<String,Double>> tmpL= new ArrayList<>();
        for(int i=0;i<sample;i++){
            Map<String,Double> tmpMap=new LinkedHashMap<>();
            for(int j=0;j<ParametersNames.size();j++){
                Double val=(u.get(i).get(ParametersNames.get(j)))*(b.get(i)-a.get(i))+a.get(i);
                tmpMap.put(ParametersNames.get(j),val);
            }
            tmpL.add(tmpMap);
        }
        return tmpL;
    }

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
    public Map<String,List<Double>> transformListMapToMapList(List<Map<String,Double>> ListMap,List<String> ParametersNames){
        Map<String,List<Double>> MapList= new HashMap<>();
        for(int i=0;i<ParametersNames.size();i++) {
            List<Double> tmpList = new ArrayList<>();
            int finalI = i;
            ListMap.forEach(map -> {
                tmpList.add(map.get(ParametersNames.get(finalI)));
            });
            MapList.put(ParametersNames.get(i),tmpList);
        }
        return MapList;
    }
    
    /**
     * Shuffle the values of the sample
     * @param s
     * @param sample
     * @param ParametersNames
     * @return
     */
    public List<Map<String,Double>> shuffle(List<Map<String,Double>> s,int sample,List<String> ParametersNames){
        Map<String,List<Double>> tmpMap= transformListMapToMapList(s,ParametersNames);
        Map<String,List<Double>> tmpMap2=new LinkedHashMap<>();
        for(int i=0;i<tmpMap.size();i++){
            List<Double> tmpL= tmpMap.get(ParametersNames.get(i));
            Collections.shuffle(tmpL);
            tmpMap2.put(ParametersNames.get(i),tmpL);
        }
        List<Map<String,Double>> sampleFinal= transformMapListToListMap(tmpMap2,ParametersNames);
        return sampleFinal;


    }

    /**
     * Compute the spatial distance between each points
     * @param s
     * @param ParametersNames
     * @return
     */
    public List<Double> computeSpatialDistance(List<Map<String,Double>> s,List<String> ParametersNames){
        List<Double> SD= new ArrayList<>();
        for(int i=0;i<s.size();i++){
            Map<String,Double> p1= s.get(i);
            for(int j=i+1;j<s.size();j++){
                Map<String,Double> p2=s.get(j);
                double val=0.0;

                for(int z=0;z<p1.size();z++){
                    val=val + (Math.pow(p1.get(ParametersNames.get(z))-p2.get(ParametersNames.get(z)),2));
                }

                val=Math.sqrt(val);
                SD.add(val);


            }
        }

        return SD;
    }


    /**
     * Find the min of a list
     * @param list
     * @return
     */
    public Double findMin(List<Double> list){
        AtomicReference<Double> min= new AtomicReference<>(Double.MAX_VALUE);
        list.forEach(v->{
            if(v< min.get()){
                min.set(v);
            }
        });
        return min.get();
    }

    /**
     * Generate the sampling
     * @param n
     * @param sample
     * @param iteration
     * @param ParametersNames
     * @param R
     * @return
     */
    public List<Map<String,Double>> generate(int n, int sample, int iteration,List<String> ParametersNames, Random R){
        double maxdist=0.0;
        List<Map<String,Double>> samplingFinal=new ArrayList<>();
        for(int i=0;i<iteration;i++){

            List<Double> cut= buildCut(sample);
            List<Map<String,Double>> u =buildU(sample,n,ParametersNames,R);
            List<Double> a = buildA(sample,cut);
            List<Double> b = buildB(sample,cut);
            List<Map<String,Double>> firstsample=changeValueSample(sample,ParametersNames,u,a,b);

            List<Map<String,Double>> secondsample=shuffle(firstsample,sample,ParametersNames);

            List<Double> SD= computeSpatialDistance(secondsample,ParametersNames);

            double minSD=findMin(SD);
            System.out.println(minSD);
            if (maxdist< minSD){
                System.out.println(i);
                maxdist= minSD;
                samplingFinal=secondsample;


            }


        }
        return samplingFinal;

    }
    
    /**
     * Generate List of ParametersSet according with the orthogonal sampling
     * @param N : sample size
     * @param iteration : number of iterations
     * @param parameters
     * @param r
     * @param scope
     * @return
     */
    public List<ParametersSet> OrthogonalSamples(int N,int iteration, List<Batch> parameters,Random r,IScope scope){
    	List<ParametersSet>   finalSamp= new ArrayList<>();
        List<String> names= new ArrayList<>();
        for(int i=0;i<parameters.size();i++) {
        	names.add(parameters.get(i).getName());
        }
        List<Map<String,Double>> sampletempmap= generate(names.size(),N,iteration,names,r); 
        finalSamp= BuildParametersSetfromSample(scope,parameters,sampletempmap);  
        return finalSamp;
        
    }
    
    

}
