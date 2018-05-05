package cz.cuni.mff.kocur.influence;

import cz.cuni.mff.kocur.base.Constants;
import cz.cuni.mff.kocur.world.GridBase;

/**
 * This layer represents heights. It basically sets influence according to tiles
 * height. It's purpose was to be added to threats to make tiles, that are
 * higher be preferred over the low ones.
 * 
 * @author kocur
 *
 */
public class HeightsLayer extends InfluenceLayer {

	/**
	 * True if the layer is propagated.
	 */
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

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Integer[] gridCoords = GridBase.toTile(resolveXYBack(x, y));
				short height = grid.getTileHeight(gridCoords[0], gridCoords[1]);

				// I set influence to height/maxHeight. The bigger the height the better it is
				// for me to go there.
				setInfluence(x, y, ((double) height / Constants.maxHeight) / 2.0);
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
