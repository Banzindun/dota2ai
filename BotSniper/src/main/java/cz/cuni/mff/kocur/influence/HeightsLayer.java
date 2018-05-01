package cz.cuni.mff.kocur.influence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.world.GridBase;
import cz.cuni.mff.kocur.world.WorldManager;

public class HeightsLayer extends InfluenceLayer {
	private static final Logger logger = LogManager.getLogger(HeightsLayer.class.getName());

	private boolean propagated = false;
	
	public HeightsLayer(int width, int height, int resolution) {
		super(width, height, resolution);
	}

	public HeightsLayer(HeightsLayer heightsLayer) {
		super(heightsLayer);
	}

	@Override
	public void propagate() {
		GridBase grid = GridBase.getInstance();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++) {
				Integer[] gridCoords = GridBase.toTile(resolveXYBack(x, y));
				short height = grid.getTileHeight(gridCoords[0], gridCoords[1]);
				
				// I set influence to height/maxHeight. The bigger the height the better it is for me to go there.
				setInfluence(x, y, ((double) height/Constants.maxHeight)/2.0);
			}
		}
		propagated = true;
		
	}
	
	public boolean isPropagated() {
		return propagated;
	}

	@Override
	public void createParams() {
		
		
	}

}
