package msi.gama.kernel.batch.exploration.stochanalysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

import org.apache.commons.collections4.map.HashedMap;

import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gaml.operators.Cast;


public class Stochanalysis {

	Map<String,List<Double>> Outputs;	

	List<Map<String,Object>> MySample;
	List<String> ParametersNames;
	double threshold;
	

	
	int min_replicat;
	
	
	
	public Stochanalysis() {
		
	}
	
    public String buildResultTxt(int nb_replicat){
    	StringBuffer sb= new StringBuffer();
    	sb.append("STOCHASTICITY ANALYSIS: \n");
    	sb.append("\n");
    	sb.append("Nb minimum replicat found: ");
    	sb.append(nb_replicat);

    	String s = sb.toString();
    	return s;
    	
    }
    
    public void WriteAndTellResult(String path,int val,IScope scope) {
        try{
        	File file= new File(path);
            FileWriter fw = new FileWriter(file, false);
            fw.write(this.buildResultTxt(val));
            fw.close();
        }catch (IOException e) {
            throw GamaRuntimeException.error("File "+ path+" not found", scope);
        }	
    }
	
	
	
	public List<Double> computeMean(List<Object> val,IScope scope) {
		List<Double> mean=new ArrayList<>();
		double tmp_mean=0;
		for(int i=0;i<val.size();i++) {
			double tmp_val=Cast.asFloat(scope, val.get(i));
			tmp_mean=tmp_mean+tmp_val;
			mean.add(tmp_mean/(i+1));
		}	
		return mean;
		
	}
	
	public List<Double> computeSTD(List<Double> mean,List<Object> val,IScope scope) {
		List<Double> STD=new ArrayList<>();

		for(int i=0;i<mean.size();i++) {
			double sum=0;
			for(int y=0;y<i;y++) {
				double tmp_val=Cast.asFloat(scope, val.get(y));
				sum= sum + Math.pow((tmp_val-mean.get(i)), 2);
			}
			STD.add(Math.sqrt(sum/(i+1)));
			
		}
		
		
		return STD;
	
	}
	
	public List<Double> computeCV(List<Double> STD,List<Double> mean) {
		List<Double> CV=new ArrayList<>();
		for(int i=0;i<mean.size();i++) {
			if(i==0) {
				//CV.add(1.0);
			}else {
				CV.add(STD.get(i)/mean.get(i));
			}
		}
		return CV;
		
	}
	


	public int FindWithThreshold(List<Double> CV){
		boolean thresh_ok=false;
		int id_sample=0;
		for(int i=0;i<CV.size()-2;i++) {
			for(int y=i+1;y<CV.size();y++) {
				double tmp_val=Math.abs(CV.get(i)-CV.get(y));
				if((tmp_val<=this.threshold) && (!thresh_ok)) {
					thresh_ok=true;
					id_sample=i+1;
				}
			}
		}
		if(!thresh_ok) {
			return CV.size();
		}
		return id_sample;
	}
	
	
	public int StochasticityAnalysis(IMap<ParametersSet,List<Object>> sample,IScope scope) {
		int tmp_replicat=0;
		for(ParametersSet ps : sample.keySet()) {
			List<Double> mean = computeMean(sample.get(ps),scope);
			List<Double> std = computeSTD(mean,sample.get(ps),scope);
			List<Double> cv = computeCV(std,mean);
			tmp_replicat= tmp_replicat+FindWithThreshold(cv);
		}
		min_replicat=tmp_replicat/sample.keySet().size();
		return min_replicat;
	}
	
	
	
	  public Map<String,List<Double>>  readSimulation(String path,int idOutput,IScope scope){
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
	                  Map<String,Object> temp_map= new LinkedHashMap<>();
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
	          throw GamaRuntimeException.error("File "+ path+" not found", scope);
	      }
	      Map<String,List<Double>> new_Outputs= new LinkedHashMap<>();
	      List<String> tmpNames= parameters.get(0).keySet().stream().toList();
	      IntStream.range(0,parameters.size()).forEach(i->{
	          for(int y= idOutput;y<tmpNames.size();y++){
	              List<Double> tmpList;
	              try{
	                  tmpList=new ArrayList<>(new_Outputs.get(tmpNames.get(y)));
	                  double val= Double.parseDouble((String)parameters.get(i).get(tmpNames.get(y)));
	                  tmpList.add(val);
	                  new_Outputs.replace(tmpNames.get(y),tmpList);
	              }catch(Exception ignored){
	                  tmpList=new ArrayList<>();
	                  double val= Double.parseDouble((String)parameters.get(i).get(tmpNames.get(y)));
	                  tmpList.add(val);
	                  new_Outputs.put(tmpNames.get(y),tmpList);
	              }
	              parameters.get(i).remove(tmpNames.get(y));
	          }
	      });
	      MySample=parameters;
	      ParametersNames=parameters.get(0).keySet().stream().toList();
	      return new_Outputs;
	  }
	  
	  
	public String BuildString(Map<String,Object> s) {
		String txt="";
		for(String name: s.keySet()) {
			txt=txt+s.get(name).toString()+"_";
		}
		return txt;
		
	}
	
	public String StochasticityAnalysis_From_CSV(int replicat,int threshold, String path_to_data,int id_output, IScope scope) {
		this.threshold=threshold;
		Map<String,List<Double>>  Outputs= readSimulation(path_to_data,id_output,scope);
		
		for(String name: Outputs.keySet()) {
			
			Map<String,List<Object>> sample= new HashedMap<>();
			
			for(Map<String,Object> m: MySample) {
				String s= BuildString(m);
				if(sample.containsKey(s)) {
					List<Object> tmp_l=sample.get(s);
					tmp_l.add(Outputs.get(name));
					m.replace(s, tmp_l);
				}else {
					List<Object> tmp_l= new ArrayList<>();
					tmp_l.add(Outputs.get(name));
					m.put(s, tmp_l);
					}
				}
			
			int tmp_replicat=0;
			
			for(String ps : sample.keySet()) {
				List<Double> mean = computeMean(sample.get(ps),scope);
				List<Double> std = computeSTD(mean,sample.get(ps),scope);
				List<Double> cv = computeCV(std,mean);
				tmp_replicat= tmp_replicat+FindWithThreshold(cv);
				}
			min_replicat=tmp_replicat/sample.keySet().size();
			}
		min_replicat=min_replicat/(Outputs.keySet().size());
		return Cast.asString(scope, min_replicat);
		}

}
