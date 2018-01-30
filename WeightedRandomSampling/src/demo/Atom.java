package demo;

/** 
 * Weighted Algorithm Atom Object
 * @author Zeyu Ni 
 */ 
public class Atom {  
    /** 
     * Color of Ball 
     */  
    private String color;  
    /** 
     * Object Weighted Parameter 
     */  
    private int weight;
	public Atom(String color, int weight) {
		super();
		this.color = color;
		this.weight = weight;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	} 
}  