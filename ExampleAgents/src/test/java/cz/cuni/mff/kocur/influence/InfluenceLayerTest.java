package cz.cuni.mff.kocur.influence;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class InfluenceLayerTest {

	
	RadiantThreatLayer il = null;
	
	private int width = 100; 
	private int height = 100;
	private int resolution = 10; 
	
	@Before
	public void initialize() {
		il = new RadiantThreatLayer(width, height, resolution);
		
	}
	
	@Test
	public void peaksTest() {
		il.clearInfluence();
		
		assertTrue(il.getHighestValue() == Double.NEGATIVE_INFINITY);
		assertTrue(il.getLowestValue() == Double.POSITIVE_INFINITY);
		
		checkLocation(il.getHighestLocation(), -1, -1);
		checkLocation(il.getLowestLocation(), -1, -1);

		il.setInfluence(10, 10, 2);
		il.setInfluence(11, 11, -2);
		
		checkLocation(il.getHighestLocation(), 10, 10);
		checkLocation(il.getLowestLocation(), 11, 11);
		
		assertTrue(il.getHighestValue() == 2);
		assertTrue(il.getLowestValue() == -2);
	}

	private void checkLocation(int[] location, int i, int j) {
		assertTrue(location[0] + "!=" + i, location[0] == i);
		assertTrue(location[1] + "!=" + i, location[1] == j);
	}
	
	@Test
	public void testNormalize() {
		Random rnd = new Random();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int sign = rnd.nextBoolean() ? 1 : -1;
				il.setInfluence(x, y, sign*rnd.nextDouble()*2);
			}
		}
		
		double max = il.getHighestValue();
		double min = il.getLowestValue();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", (inf <= max) && (inf >= min));
			}
		}
		
		
		il.normalize();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", (inf <= 1.0) && (inf >= -1.0));
			}
		}
		
		assertTrue("Highest value: " + il.getHighestValue(), il.getHighestValue() == 1.0);
		assertTrue("Lowest value: " + il.getLowestValue(), il.getLowestValue() == -1.0);		
	} 	
	
	@Test
	public void checkAddMultiplyFails(){
		// Should fail, resolution is bigger than original.
		//RadiantThreatLayer rtl = new RadiantThreatLayer(width, height, resolution+5);
		
		
		/*try {
			il.multiplyLayer(rtl);
			assertTrue(false);
		} catch (InfluenceCombinationException e) {
		}
		
		try {
			il.addToLayer(rtl);
			assertTrue(false);
		} catch (InfluenceCombinationException e) {			
		}	
		
		// Should fail, width height too big.
		rtl = new RadiantThreatLayer(1000, 1000, resolution);
		try {
			il.multiplyLayer(rtl);
			assertTrue(false);
		} catch (InfluenceCombinationException e) {
		}
		
		try {
			il.addToLayer(rtl);
			assertTrue(false);
		} catch (InfluenceCombinationException e) {			
		}*/
	}
	
	@Test
	public void checkMultiply(){
		il.clearInfluence();
		
		// Same parameters, this should work.
		RadiantThreatLayer rtl = new RadiantThreatLayer(width, height, resolution);
		
		rtl.fillWithInfluence(2.0);
		il.fillWithInfluence(2.0);
		
		try {
			il.multiplyLayer(rtl);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 4.0);
			}
		}
		
		il.fillWithInfluence(1.0);
		
		try {
			il.multiplyLayer(rtl, 2.0);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 4.0);
			}
		}
		
		
		// Smaller resolution, should work too, but should fill only half of the il layers.
		rtl = new RadiantThreatLayer(width, height, 5);
		rtl.fillWithInfluence(2.0);
		il.fillWithInfluence(1.0);
		
		try {
			il.multiplyLayer(rtl);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
		
		for (int y = 0; y < height/2; y++) {
			for (int x = 0; x < width/2; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 2.0);
			}
		}
		
		//System.out.println(il.toString());
		
		for (int y = height/2; y < height; y++) {
			for (int x = width/2; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 1.0);
			}
		}
		
		
	}
	
	@Test
	public void checkAdd(){
		il.clearInfluence();
		
		// Same parameters, this should work.
		RadiantThreatLayer rtl = new RadiantThreatLayer(width, height, resolution);
		
		rtl.fillWithInfluence(2.0);
		il.fillWithInfluence(2.0);
		
		try {
			il.addToLayer(rtl);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 4.0);
			}
		}
		
		il.fillWithInfluence(1.0);
		
		try {
			il.addToLayer(rtl, 2.0, 3.0);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 8.0);
			}
		}
		
		
		// Smaller resolution, should work too, but should fill only half of the il layers.
		rtl = new RadiantThreatLayer(width, height, 5);
		rtl.fillWithInfluence(2.0);
		il.fillWithInfluence(1.0);
		
		try {
			il.addToLayer(rtl);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
		
		for (int y = 0; y < height/2; y++) {
			for (int x = 0; x < width/2; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 3.0);
			}
		}
		
		for (int y = height/2; y < height; y++) {
			for (int x = width/2; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 1.0);
			}
		}
		
		
	}
	
	@Test 
	public void testMinMax() {
		il.clearInfluence();
		
		// Same parameters, this should work.
		RadiantThreatLayer rtl = new RadiantThreatLayer(width, height, resolution);
		
		rtl.fillWithInfluence(2.0);
		il.fillWithInfluence(2.0);
		
		try {
			il.addToLayer(rtl);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double inf = il.get(x, y);
				assertTrue(inf + "", inf == 4.0);
			}
		}
		
		il.fillWithInfluence(1.0);
		
		try {
			il.addToLayer(rtl, 2.0, 3.0);
		} catch (InfluenceCombinationException e) {
			assertTrue(false);
		}
		
		il.addInfluence(0, 0, 4);
		il.addInfluence(0, 1, -4);
		
		MinMaxObject mmx = il.findMinMax(0, 0, il.getWidth(), il.getHeight());
		assertTrue(mmx.getMax() == il.getMax());
		assertTrue(mmx.getMaxLocation()[0] == 0);
		assertTrue(mmx.getMaxLocation()[1] == 0);
		assertTrue(mmx.getMinLocation()[0] == 0);
		assertTrue(mmx.getMinLocation()[1] == 1);
	}
}
