package cz.cuni.mff.kocur.Dota2AIFramework;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.kocur.world.Creep;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.GridSystem;

public class GridSystemTest {
	
	/**
	 * Will be grid from system with base [0,0], with resolution == 10. 
	 */
	private GridSystem gs0 = null;
	
	/**
	 * Will be grid starting from system with origin at: [100, 100], with resolution == 10. 
	 */
	private GridSystem gs100 = null;
	
	int resolution = 10;
	int width = 1000;
	int height = 1000;
	
	int xOrigin= 100;
	int yOrigin = 100;
	
	@Before
	public void testGrid() {
		
		gs0 = new GridSystem() {	};
		gs0.setResolution(1);
		gs0.setSize(width, height);
		// Shifts are 0
		
		gs100 = new GridSystem() {};
		gs100.setResolution(resolution);
		gs100.setSize(width/resolution, height/resolution);
		gs100.setOrigin(xOrigin, yOrigin);
		gs100.setParent(gs0);
	}
	
	
	@Test
	public void testSetters() {
		assertTrue("Gs0 is not equal to parent of gs100", gs0 == gs100.getParent());
		
		assertTrue(width == gs0.getWidth());
		assertTrue(width/resolution == gs100.getWidth());
		assertTrue(height == gs0.getHeight());
		assertTrue(height/resolution == gs100.getHeight());
	
		assertTrue(1 == gs0.getResolution());
		assertTrue(resolution == gs100.getResolution());
		
	}
	
	@Test
	public void testReverseResolution() {
		// Lets say we have x=20 coordinate in gs0
		// Applying only resolution, this should give us 20/resolution in gs100
		assertTrue(gs100.reverseResolution(20) == 20/resolution);
		
	}
	
	@Test
	public void testToBase() {
		// Coordinate [20,20] in gs100 should give us [(20+shiftx)*resolution ..] in gs0
		assertTrue(gs100.toBase(20, 20)[0] == 20*resolution+xOrigin);
		assertTrue(gs100.toBase(20, 20)[1] == 20*resolution+yOrigin);
		
		assertTrue(gs0.toBase(20, 20)[0] == 20);
		assertTrue(gs0.toBase(20, 20)[1] == 20);
	}
	
	@Test
	public void testResolve() {
		assertTrue(gs100.resolveX(20) == (20-xOrigin)/resolution);
		assertTrue(gs100.resolveY(20) == (20-yOrigin)/resolution);
		
	}

	@Test
	public void testcenterOfTile() {
		double xy[] = gs100.centerOfTile(20, 20);
		
		assertTrue( xy[0] == 20+resolution/2.0);
		assertTrue( xy[1] == 20+resolution/2.0);
	}
	
	@Test	
	public void testGetEntityCoordinates() {
		Creep e = new Creep();
		e.setX(20);
		e.setY(20);
		e.setZ(20);
		
		double[] xyz = gs100.getEntityCoordinates(e);
		
		assertTrue(xyz[0] == (20-xOrigin)/resolution);
		assertTrue(xyz[1] == (20-yOrigin)/resolution);
		assertTrue(xyz[2] == 20);
	}
	
	@Test
	public void testResolveBack() {
		Creep e = new Creep();
		e.setX(20);
		e.setY(20);
		e.setZ(20);
		
		double[] xyz = gs100.getEntityCoordinates(e);
		
		assertTrue(gs100.resolveXBack(xyz[0]) + " != " + e.getX(), gs100.resolveXBack(xyz[0]) == e.getX());
		assertTrue(gs100.resolveYBack(xyz[1]) + " != " + e.getY(), gs100.resolveYBack(xyz[1]) == e.getY());
	}
	
	@Test
	public void testDistanceTileToTile() {
		assertTrue(GridBase.distanceTileToTile(2, 2, 2, 2)==0);
		assertTrue(GridBase.distanceTileToTile(1, 1, 1, 2)==1);
		assertTrue(GridBase.distance(2, 2, 3, 3) == GridBase.distance(0, 0, 1, 1));
		assertTrue(GridBase.distance(1,1,2,2) == Math.sqrt(2));	
	}	

}
