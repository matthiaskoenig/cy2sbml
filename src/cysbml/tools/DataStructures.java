package cysbml.tools;

import java.util.LinkedList;
import java.util.List;

public class DataStructures {
	
	public static List<String> convertArrayToList(String[] A){
		List<String> L = new LinkedList<String>();
		if (A != null){
			for (String tmp: A){
				L.add(tmp);
			}
		}
		return L;
	}
	 
	public static String[] convertListToArray(List<String> L){
		int size = L.size();
		String[] A = new String[size];
		for (int k=0; k<size; ++k){
			A[k] = L.get(k);
		}
		return A;
	}
	
}
