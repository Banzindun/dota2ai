package cz.cuni.mff.kocur.base;

/**
 * Simple pair representation. 
 * Represents and stores key and value.
 * @author kocur
 *
 * @param <L> Type of key.
 * @param <R> Type of value.
 */
public class Pair<L,R> {
	/**
	 * Key.
	 */
	private L key;
	
	/**
	 * Value.
	 */
	private R value;
	
	/**
	 * Pair constructor, takes key and value.
	 * @param k key
	 * @param v value
	 */
	public Pair(L k, R v){
	        this.key = k;
	        this.value = v;
	}
	
	/**
	 * 
	 * @return Returns the value stored inside this pair.
	 */
	public R getValue(){
		return value; 
	}
	
	/**
	 * Sets value. 
	 * @param v Value to be stored.
	 */
	public void setValue(R v){
		this.value = v; 
	}
	
	/**
	 * Sets new key.
	 * @param k New key.
	 */
	public void setKey(L k){ 
		this.key = k; 
	}
	
	/**
	 * 
	 * @return Returns key stored in this pair.
	 */
	public L getKey(){
		return key; 
	}
}
