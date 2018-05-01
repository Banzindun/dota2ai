package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.world.BaseEntity;
import cz.cuni.mff.kocur.world.GridBase;

public class LinearRadiusPropagationFunction<T extends BaseEntity> implements PropagationFunction<T> {

	private Logger logger = LogManager.getLogger(LinearRadiusPropagationFunction.class.getName());
	
	private int sign = 1;
	
	protected double maxInfluence = 1;
	
	public LinearRadiusPropagationFunction() {
		
	}
	
	public LinearRadiusPropagationFunction(int sign) {
		this.sign = sign;
	}
		
	@Override
	public void propagate(InfluenceLayer l, T e) {
		//logger.debug( "Linear radius propagation propagation." + e.getName());
		double[] xyz = l.getEntityCoordinates(e);
		
		double ex = xyz[0];
		double ey = xyz[1];
		
		// R will be in layer coordinates
		double r = l.reverseResolution((int) (e.getAttackRange() + e.getSpeed()));
		
		double yMin = (ey-r) > 0 ? ey-r : 0;
		double yMax = (ey+r) < l.getHeight() ? ey+r : l.getHeight()-1;
		
		double xMin = (ex-r) > 0 ? ex-r : 0;
		double xMax = (ex+r) < l.getWidth() ? ex+r : l.getWidth()-1;
		
		for (int y = (int) yMin; y < yMax; y++)
		{
			for (int x = (int) xMin; x < xMax; x++) {
				double distance = GridBase.distanceTileToTile(x, y, (int) ex, (int) ey);
				if (distance > r) 
					continue;
				
				double inf = maxInfluence-(distance/r);
				l.addInfluence(x, y, sign*inf);				
			}
		}
		
	}

}
