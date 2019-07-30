package com.shinwell.mda.gui.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.wbem.WBEMException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

import swing2swt.layout.BorderLayout;

import com.shinwell.mda.api.CimAPI;
import com.shinwell.mda.api.FabricCimAPI;
import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

/**
 * @author Shinwell Ku
 * @Date 2018年12月14日
 * @Desc 
 */
public class SwitchPerfermanceChartDialog extends TitleAreaDialog {
	static Logger logger = Logger.getLogger(SwitchPerfermanceChartDialog.class.getSimpleName());
	static HashMap<String, Long> cache = new HashMap<String, Long>();
	TimeSeries timeSeries;
	CIMInstance instance;
	CIMInstance systemComputer;
	SmisProvider smisProvider;
	String changeKpi = "";
	Combo kpiCombo;
	Timer timer;

	public SwitchPerfermanceChartDialog(Shell parentShell, CIMInstance instance, CIMInstance systemComputer, SmisProvider smisProvider) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL );
		setHelpAvailable(false);
		this.instance = instance;
		this.systemComputer = systemComputer;
		this.smisProvider = smisProvider;
		UIBuilderFactory.setCenter1(parentShell);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS));
		setMessage(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_MESSAGE));
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite topComposite = new Composite(container, SWT.NONE);
		topComposite.setLayoutData(BorderLayout.NORTH);
		topComposite.setLayout(new GridLayout(6, false));

		Label kpiLabel = new Label(topComposite, SWT.NONE);
		kpiLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		kpiLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_KPI));

		kpiCombo = new Combo(topComposite, SWT.READ_ONLY);
		kpiCombo.setItems(
				new String[] { 
						"PacketsReceived",
						"PacketsTransmitted",
						"BytesTransmitted",
						"BytesReceived",
						"CRCErrors",
						"LossOfSignalCounter",
						"LossOfSyncCounter", 
						"Class3FramesDiscarded",
						"DelimiterErrors",
						"EncodingDisparityErrors",
						"FBSYFrames",
						"FRJTFrames",
						"FramesTooLong",
						"FramesTooShort",
						"LinkFailures",
						"LinkResetsReceived",
						"LinkResetsTransmitted",
						"PBSYFrames",
						"PRJTFrames"
						}
				);
		kpiCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		kpiCombo.select(0);
		changeKpi = kpiCombo.getText();

		Label frequencyLabel = new Label(topComposite, SWT.NONE);
		frequencyLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		frequencyLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_FREQ));

		final Spinner spinner = new Spinner(topComposite, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		spinner.setMaximum(30);
		spinner.setMinimum(1);
		
		Label frequencyUnitLabel = new Label(topComposite, SWT.NONE);
		frequencyUnitLabel.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLE_FREQ_UNIT));

		final Button startButton = new Button(topComposite, SWT.NONE);
		startButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		startButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START));

		Composite chartComposite = new Composite(container, SWT.BORDER);
		chartComposite.setLayoutData(BorderLayout.CENTER);
		chartComposite.setLayout(new GridLayout(1, false));

		timeSeries = new TimeSeries("");
		timeSeries.setMaximumItemAge(100000);

		TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
		localTimeSeriesCollection.addSeries(timeSeries);

		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"["+instance.getPropertyValue("PermanentAddress") + "]" + kpiCombo.getText() + ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS), // title
				ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLETIME), // x-axis label
				"NUM", // y-axis label
				localTimeSeriesCollection, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);
		chart.setBackgroundPaint(Color.white);
		

		XYPlot xpPlot = (XYPlot) chart.getPlot();
		xpPlot.setBackgroundPaint(Color.lightGray);
		xpPlot.setDomainGridlinePaint(Color.white);
		xpPlot.setRangeGridlinePaint(Color.white);
		xpPlot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		xpPlot.setDomainCrosshairVisible(true);
		xpPlot.setRangeCrosshairVisible(true);
		
		XYItemRenderer r = xpPlot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer) r;
			localXYLineAndShapeRenderer.setBaseShapesVisible(true);
			localXYLineAndShapeRenderer.setBaseShapesFilled(true);
			
			localXYLineAndShapeRenderer.setSeriesPaint(0, Color.blue);
			localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(2F, 2, 2));
		}

		//final DateAxis dateAxis = (DateAxis) xpPlot.getDomainAxis();
	    //dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		final NumberAxis numberAxis = (NumberAxis) xpPlot.getRangeAxis();
		
		ChartComposite frame = new ChartComposite(chartComposite, SWT.NONE, chart, true);
		frame.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(false);
		frame.setVerticalAxisTrace(false);
		cache.put("oldValue", -1l);//首次置为-1
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String kpivalue = kpiCombo.getText();
				final long period = Long.valueOf(spinner.getText());
				if (kpivalue.contains("Packets")) {
					numberAxis.setLabel("pkt/s");
				}else if (kpivalue.contains("Bytes")){
					numberAxis.setLabel("mb/s");
				}else {
					numberAxis.setLabel("num/s");
				}
				timeSeries.setKey(kpivalue);
				//如果KPI选项发生改变，则重置时间序列
				if (!changeKpi.equalsIgnoreCase(kpivalue)&&startButton.getText().equalsIgnoreCase(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START))) {
					changeKpi = kpivalue;
					timeSeries.clear();
					cache.put("oldValue", -1l);
					//dateAxis.setLabel(kpivalue);
					chart.setTitle("["+instance.getPropertyValue("PermanentAddress") + "]" + kpiCombo.getText() + ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS));
				}
				if (startButton.getText().equalsIgnoreCase(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START))) {
					timer = new Timer();
					startButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_STOP));
					timer.schedule(new TimerTask() {
						public void run() {
							long oldValue = cache.get("oldValue");
							executePrfQuery(kpivalue,oldValue,period);
						}
					},0,1000*Long.valueOf(spinner.getText()));
				}else if(startButton.getText().equalsIgnoreCase(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_STOP))){
					startButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START));
					if (null!=timer) {
						timer.cancel();
					}
				}
				
			}
		});
		
		return area;
	}

	public void executePrfQuery(String kpivalue, long oldValue, long period) {
		CimAPI cimAPI = new CimAPI();
		try {
			cimAPI.getWbemClient(smisProvider);
			FabricCimAPI fabricCimAPI = new FabricCimAPI(cimAPI);
			CIMInstance perfInstance = fabricCimAPI.queryStatisticalData(instance);
			long value = 0l;
			if (null != perfInstance) {
				CIMProperty<?>[] perfProperties = perfInstance.getProperties();
				for (int i = 0; i < perfProperties.length; i++) {
					 final CIMProperty<?> cimProperty = perfProperties[i];
					 final String propertyName = cimProperty.getName();
							if (propertyName.contains(kpivalue)) {
								long newValue = Long.valueOf(cimProperty.getValue().toString());
								logger.info("============newValue:"+newValue);
								logger.info("============oldValue:"+oldValue);
								if (kpivalue.contains("Bytes")){
									value = (oldValue==-1)?0:(newValue - oldValue)/1024/period;
									logger.info("============value:("+newValue+" - "+oldValue+")/1024/period="+value);
								}else {
									value = (oldValue==-1)?0:(newValue - oldValue)/period;
									logger.info("============value:("+newValue+" - "+oldValue+")/period="+value);
								}
								cache.put("oldValue", newValue);
							}
				}
			}
			final long _value = value;
			Display display = getShell().getDisplay();
			if (null != display) {
				display.syncExec(new Runnable() {
					public void run() {
						timeSeries.add(new Millisecond(),_value);
					}
				});
			}
		} catch (final WBEMException e1) {
			logger.error("Query Error:", e1);
		} finally {
			cimAPI.close();
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null!=timer) {
					timer.cancel();
				}
			}
		});
		//createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	@Override
	protected void okPressed() {
		if (null!=timer) {
			timer.cancel();
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1253, 743);
	}

	@Override
	public boolean close() {
		if (null!=timer) {
			timer.cancel();
		}
		return super.close();
	}
	
	


}
