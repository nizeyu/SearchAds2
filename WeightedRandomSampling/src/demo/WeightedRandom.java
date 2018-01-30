package demo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;  
  
/** 
 * Weighted Random Algorithm: get weighted Random Sampling 
 * @author Zeyu Ni 
 */  
public class WeightedRandom {  
    
	/** 
     * get weighted random object
     * @param atomList, numOfSamples
     * @return void 
     */ 
	public static void getWeightedRandomSampling(ArrayList<Atom> atomList, int numOfSamples){
		int weightSum = 0;//total weight sum  
        for(Atom atom : atomList){  
            weightSum += atom.getWeight();  
        }  
        
        Atom atom;  
        
        //Count the number of occurrences for the object  
        Map<String, Integer> countAtom = new HashMap<String, Integer>();  
        for (int i = 0; i < numOfSamples; i++) {  
            atom = getWeightedRandomAtom(atomList, weightSum);  
            if (countAtom.containsKey(atom.getColor())) {  
                countAtom.put(atom.getColor(), countAtom.get(atom.getColor()) + 1);  
            } else {  
                countAtom.put(atom.getColor(), 1);  
            }  
        }  
        
        // Print
        System.out.println("Probability and Statistics:");  
        for (String id : countAtom.keySet()) {  
            System.out.println(id + " appeared " + countAtom.get(id) + " times.");  
        }  
	}
	
	
    /** 
     * get weighted random object
     * @param atomList, weigthSum
     * @return Atom 
     */  
    private static Atom getWeightedRandomAtom(ArrayList<Atom> atomList, int weightSum){  
        if(atomList.isEmpty()){  
            return null;  
        }  
        
        //get the random value in total weight sum range 
        int random = new Random().nextInt(weightSum);  //random in [0, weightSum)   
        //{.},{..},{...},{....}...get weighted random object according to weighted probability range  
        for(Atom atom : atomList){  
            random -= atom.getWeight();  
            if (random < 0) {  
                return atom;  
            }  
        }  
        return null;  
    }  
}  