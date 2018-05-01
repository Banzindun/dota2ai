package cz.cuni.mff.kocur.influence;

public class MinMaxObject {
	private double min = Double.POSITIVE_INFINITY;
	
	private double max = Double.NEGATIVE_INFINITY; 
	
	private int[] minLocation = new int[2];
	
	private int[] maxLocation = new int[2];
	
	public MinMaxObject(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public int[] getMinLocation() {
		return minLocation;
	}
	public void setMinLocation(int[] minLocation) {
		this.minLocation = minLocation;
	}
	public int[] getMaxLocation() {
		return maxLocation;
	}
	public void setMaxLocation(int[] maxLocation) {
		this.maxLocation = maxLocation;
	}
	
}
