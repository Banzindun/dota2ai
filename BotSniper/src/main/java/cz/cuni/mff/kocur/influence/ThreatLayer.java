package cz.cuni.mff.kocur.influence;

public class ThreatLayer extends InfluenceLayer {
	
	private double decay = 1;
	
	public ThreatLayer(int width, int height, int resolution) {
		super(width, height, resolution);
	}
	
	public ThreatLayer(InfluenceLayer l) {
		super(l);
	}

	@Override
	public void propagate() {
		// This will apply decay to everything
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				map[y][x] = map[y][x] * decay;
			}
		}
		
		this.min = min*decay;
		this.max = max*decay;
	}

	@Override
	public void createParams() {
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

}
