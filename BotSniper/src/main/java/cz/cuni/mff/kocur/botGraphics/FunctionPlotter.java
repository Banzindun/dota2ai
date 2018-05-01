package cz.cuni.mff.kocur.botGraphics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cz.cuni.mff.kocur.considerations.Consideration;
import cz.cuni.mff.kocur.graphics.ConstraintsBuilder;
import cz.cuni.mff.kocur.graphics.Window;

public class FunctionPlotter extends ConsiderationInfoPanel {

	protected ConsiderationInfoPanel parent;
	protected Window window;

	protected XYSeries series;

	public FunctionPlotter(ConsiderationInfoPanel parent, Consideration c) {
		this.parent = parent;
		this.consideration = c;

		this.setLayout(new GridBagLayout());

		build();

		window = new Window(this, c.getName()) {
			@Override
			public void closing() {
				parent.windowClosing();
			}
		};

		window.start();
	}

	private void build() {
		buildType();
		
		paramsWrapper = new JPanel();
		buildParameters();
		
		gbc = ConstraintsBuilder.build().gridxy(0).weightxy(1, 0).fill(GridBagConstraints.BOTH)
				.anchor(GridBagConstraints.LINE_START).insets(2).get();

		JPanel wrap = new JPanel();
		wrap.add(type);
		wrap.add(paramsWrapper);
		this.add(wrap, gbc);

		gbc.gridy++;
		gbc.weighty = 1;

		series = getSeries();
		buildGraph();
	}

	@Override
	public void update() {
		parent.updateParameters();
		parent.updateType();
		
		this.removeAll();
		
		this.build();
		
		this.revalidate();
		this.repaint();
	}

	public void updateSeries() {
		XYSeries series = getSeries();

		for (int i = 0; i < 1000; i++) {
			series.updateByIndex(i, series.getY(i));
		}
	}

	protected void buildGraph() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		JFreeChart chart = createChart(dataset);

		final XYPlot plot = (XYPlot) chart.getPlot();
		plot.getRangeAxis().setRange(new Range(-0.1, 1.1));

		ChartPanel chartPanel = new ChartPanel(chart);

		this.add(chartPanel, gbc);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		if (e.getSource() == type)
			update();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);

		if (e.getSource() == mParam || e.getSource() == kParam || e.getSource() == cParam || e.getSource() == bParam) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				update();
			}
		}
	}

	private XYSeries getSeries() {
		final XYSeries series = new XYSeries("OUT (y)");
		for (int i = 0; i < 1000; i++) {
			double value = consideration.computeResponseCurve((double) i / 1000);
			series.add((double) i / 1000, value);
		}

		return series;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(type.getName(), "IN (x)", "OUT (y)", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}

}
