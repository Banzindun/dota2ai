package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.world.GridBase;

public class CollisionLayer extends InfluenceLayer{

	public CollisionLayer(int width, int height, int resolution) {
		super(width, height, resolution);
	}
	
	public CollisionLayer(InfluenceLayer l) {
		super(l);
	}

	@Override
	public void propagate() {
		GridBase grid = GridBase.getInstance();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++) {
				Integer[] gridCoords = toTile(resolveXYBack(x, y));
				if (grid.passable(gridCoords[0], gridCoords[1]))
					setInfluence(x, y, 1.0);
				else {
					setInfluence(x, y, 0);
				}
			}
		}		
	}

	@Override
	public void createParams() {
		
		
	}

}
