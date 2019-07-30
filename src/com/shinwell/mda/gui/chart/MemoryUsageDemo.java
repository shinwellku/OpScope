package com.shinwell.mda.gui.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class MemoryUsageDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	private TimeSeries total;
	private TimeSeries free;

	public MemoryUsageDemo(int paramInt) {
		super(new BorderLayout());
		total = new TimeSeries("Total Memory");
		this.total.setMaximumItemAge(paramInt);
		this.free = new TimeSeries("Free Memory");
		this.free.setMaximumItemAge(paramInt);
		TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
		localTimeSeriesCollection.addSeries(this.total);
		localTimeSeriesCollection.addSeries(this.free);
		DateAxis localDateAxis = new DateAxis("Time");
		NumberAxis localNumberAxis = new NumberAxis("Memory");
		localDateAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localNumberAxis.setTickLabelFont(new Font("SansSerif", 0, 12));
		localDateAxis.setLabelFont(new Font("SansSerif", 0, 14));
		localNumberAxis.setLabelFont(new Font("SansSerif", 0, 14));
		XYLineAndShapeRenderer localXYLineAndShapeRenderer = new XYLineAndShapeRenderer(true, false);
		localXYLineAndShapeRenderer.setSeriesPaint(0, Color.red);
		localXYLineAndShapeRenderer.setSeriesPaint(1, Color.green);
		localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(3.0F, 0, 2));
		localXYLineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(3.0F, 0, 2));
		XYPlot localXYPlot = new XYPlot(localTimeSeriesCollection, localDateAxis, localNumberAxis, localXYLineAndShapeRenderer);
		localDateAxis.setAutoRange(true);
		localDateAxis.setLowerMargin(0.0D);
		localDateAxis.setUpperMargin(0.0D);
		localDateAxis.setTickLabelsVisible(true);
		localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		JFreeChart localJFreeChart = new JFreeChart("JVM Memory Usage", new Font("SansSerif", 1, 24), localXYPlot, true);
		ChartUtilities.applyCurrentTheme(localJFreeChart);
		ChartPanel localChartPanel = new ChartPanel(localJFreeChart, true);
		localChartPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createLineBorder(Color.black)));
		add(localChartPanel);
	}

	private void addTotalObservation(double paramDouble) {
		this.total.add(new Millisecond(), paramDouble);
	}

	private void addFreeObservation(double paramDouble) {
		this.free.add(new Millisecond(), paramDouble);
	}

	public static void main(String[] paramArrayOfString) {
		JFrame localJFrame = new JFrame("Memory Usage Demo");
		MemoryUsageDemo localMemoryUsageDemo = new MemoryUsageDemo(30000);
		localJFrame.getContentPane().add(localMemoryUsageDemo, "Center");
		localJFrame.setBounds(200, 120, 600, 280);
		localJFrame.setVisible(true);
		MemoryUsageDemo tmp56_55 = localMemoryUsageDemo;
		tmp56_55.new DataGenerator(100).start();
		localJFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent paramWindowEvent) {
				System.exit(0);
			}
		});
	}

	class DataGenerator extends Timer implements ActionListener {
		private static final long serialVersionUID = 1L;

		DataGenerator(int paramInt) {
			super(paramInt, null);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent paramActionEvent) {
			long l1 = Runtime.getRuntime().freeMemory();
			long l2 = Runtime.getRuntime().totalMemory();
			MemoryUsageDemo.this.addTotalObservation(l2);
			MemoryUsageDemo.this.addFreeObservation(l1);
		}
	}
}