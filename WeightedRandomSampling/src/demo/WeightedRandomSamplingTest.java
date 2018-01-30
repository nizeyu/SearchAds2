/**
 * @author nizeyu
 *
 */
package demo;

import java.util.ArrayList;


public class WeightedRandomSamplingTest{
	/** Main method */
	public static void main(String[] args) throws Exception {
		
		ArrayList<Atom> atomList = new ArrayList<Atom>();  
        atomList.add(new Atom("Blue", 10));  
        atomList.add(new Atom("Red", 40));  
        atomList.add(new Atom("Yellow", 50));  
        
        int numOfSamples = 1000000;
        
        WeightedRandom.getWeightedRandomSampling(atomList, numOfSamples);
    }
}