package cz.cuni.mff.kocur.utility;

public abstract class UtilityFunction {
	
	public static enum TYPE{
		BINARY_FUNCTION,
		LINEAR_FUNCTION,
		LOGISTIC_FUNCTION,
		LOGIT_FUNCTION,
		POLYNOMIAL_FUNCTION, 
		SINUSOID_FUNCTION		
	};
	
	protected TYPE type; 
	
	protected double m;
	protected double k;
	protected double c;
	protected double b;
	
	public abstract double calculate(double x);

	public UtilityFunction() {
		
	}
	
	public UtilityFunction(double m, double k, double c, double b) {
		setMKCB(m,k,c,b);
	}
	
	public void setMKCB(double m, double k, double c, double b) {
		this.m = m;
		this.k = k;
		this.c = c;
		this.b = b;
	}
	
	public double[] getSignature() {
		return new double[]{m,k,c,b};
	}
	
	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}
	
	
	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}
