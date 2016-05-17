package ummisco.gama.network.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SimpleMapSerializer {

		static final char PAIR_SEPARATOR = 30;
		static final char DATA_SEPARATOR = 31;
		
		public static String map2String(Map<String, String> myMap)
		{
			String data="";
			Set<String> keys = myMap.keySet();
			for(String k:keys)
			{
				data+=k+PAIR_SEPARATOR+myMap.get(k).toString()+DATA_SEPARATOR;
			}
			return data;
		}
		
		public static Map<String,String> string2Map(String data)
		{
			Map<String, String> res = new HashMap<String, String>();
			String[] pairs = data.split(""+DATA_SEPARATOR);
			for(int i = 0;i<pairs.length; i++)
			{
				String[] md = pairs[i].split(""+PAIR_SEPARATOR);
				res.put(md[0],md[1]);		
			}
			return res;
		}
}
