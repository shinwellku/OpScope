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

import com.shinwell.mda.api.ArrayCimAPI;
import com.shinwell.mda.api.CimAPI;
import com.shinwell.mda.domain.SmisProvider;
import com.shinwell.mda.gui.factory.UIBuilderFactory;
import com.shinwell.mda.util.CommonUtil;
import com.shinwell.mda.util.ResourceBundleUtil;
import com.shinwell.mda.util.ResourceConstant;

/**
 * 
 * @author Shinwell Ku
 * @Date 2018年12月13日
 * @Desc
 */
public class ArrayPerfermanceChartDialog extends TitleAreaDialog {
	static Logger logger = Logger.getLogger(ArrayPerfermanceChartDialog.class.getSimpleName());
	static HashMap<String, Long> cache = new HashMap<String, Long>();
	TimeSeries totalTimeSeries;
	TimeSeries readTimeSeries;
	TimeSeries writeTimeSeries;
	CIMInstance instance;
	CIMInstance systemComputer;
	SmisProvider smisProvider;
	String changeKpi = "";
	Combo kpiCombo;
	Timer timer;
	
	String startTime = "";
	
	Long totalIOs = 0l;
	Long readIOs = 0l;
	Long writeIOs = 0l;
	
	Long totalKBytes = 0l;
	Long readKBytes = 0l;
	Long writeKBytes = 0l;
	
	Long totalHitIOs = 0l;
	Long readHitIOs = 0l;
	Long writeHitIOs = 0l;
	
	Long totalIOTime = 0l;
	Long readIOTime = 0l;
	Long writeIOTime = 0l;


	public ArrayPerfermanceChartDialog(Shell parentShell, CIMInstance instance, CIMInstance systemComputer, SmisProvider smisProvider) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL );
		setHelpAvailable(false);
		this.instance = instance;
		this.systemComputer = systemComputer;
		this.smisProvider = smisProvider;
		UIBuilderFactory.setCenter(parentShell);
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
		kpiCombo.setItems(new String[] { "I/O Rate", "AVG KBPS","% HitIO","I/O Response Time" });
		kpiCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		kpiCombo.select(0);

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

		totalTimeSeries = new TimeSeries("Total");
		readTimeSeries = new TimeSeries("Read");
		writeTimeSeries = new TimeSeries("Write");
		totalTimeSeries.setMaximumItemAge(300000);
		readTimeSeries.setMaximumItemAge(300000);
		writeTimeSeries.setMaximumItemAge(300000);

		TimeSeriesCollection localTimeSeriesCollection = new TimeSeriesCollection();
		localTimeSeriesCollection.addSeries(totalTimeSeries);
		localTimeSeriesCollection.addSeries(readTimeSeries);
		localTimeSeriesCollection.addSeries(writeTimeSeries);


		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"["+instance.getPropertyValue("ElementName") + "]" + kpiCombo.getText() + ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS), // title
				ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_SAMPLETIME), // x-axis label
				kpiCombo.getText(), // y-axis label
				localTimeSeriesCollection, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);
		chart.setBackgroundPaint(Color.white);
		

		XYPlot xpPlot = (XYPlot) chart.getPlot();
		xpPlot.setBackgroundPaint(Color.white);
		xpPlot.setDomainGridlinePaint(Color.white);
		xpPlot.setRangeGridlinePaint(Color.lightGray);
		xpPlot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		xpPlot.setDomainCrosshairVisible(true);
		xpPlot.setRangeCrosshairVisible(true);
		
		
		XYItemRenderer r = xpPlot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer localXYLineAndShapeRenderer = (XYLineAndShapeRenderer) r;
			localXYLineAndShapeRenderer.setBaseShapesVisible(true);
			localXYLineAndShapeRenderer.setBaseShapesFilled(true);
			
			localXYLineAndShapeRenderer.setSeriesPaint(0, Color.RED);
			localXYLineAndShapeRenderer.setSeriesPaint(1, Color.BLUE);
			localXYLineAndShapeRenderer.setSeriesPaint(2, Color.BLACK);
			localXYLineAndShapeRenderer.setSeriesStroke(0, new BasicStroke(1.0F, 1, 2));
			localXYLineAndShapeRenderer.setSeriesStroke(1, new BasicStroke(1.0F, 1, 2));
			localXYLineAndShapeRenderer.setSeriesStroke(2, new BasicStroke(1.0F, 1, 2));
		}

		//final DateAxis dateAxis = (DateAxis) xpPlot.getDomainAxis();
	    //dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		final NumberAxis numberAxis = (NumberAxis) xpPlot.getRangeAxis();
		
		ChartComposite frame = new ChartComposite(chartComposite, SWT.NONE, chart, true);
		frame.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		frame.setDisplayToolTips(true);
		frame.setHorizontalAxisTrace(true);
		frame.setVerticalAxisTrace(true);
		
		startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String kpivalue = kpiCombo.getText();
				//如果KPI选项发生改变，则重置时间序列
				if (!changeKpi.equalsIgnoreCase(kpivalue)||changeKpi.equalsIgnoreCase("")) {
					changeKpi = kpivalue;
					totalTimeSeries.clear();
					readTimeSeries.clear();
					writeTimeSeries.clear();
					if (kpivalue.contains("I/O Rate")) {
						numberAxis.setLabel(kpivalue+" (ops/s)");
					}else if (kpivalue.contains("AVG KBPS")){
						numberAxis.setLabel(kpivalue+" (kb/op)");
					}else if (kpivalue.contains("% HitIO")){
						numberAxis.setLabel(kpivalue+" (%)");
					}else if (kpivalue.contains("I/O Response Time")){
						numberAxis.setLabel(kpivalue+" (ms)");
					}
					//numberAxis.setLabel(kpivalue);
					//dateAxis.setLabel(kpivalue);
					chart.setTitle("["+instance.getPropertyValue("ElementName") + "]" + kpiCombo.getText() + ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS));
				}
				if (startButton.getText().equalsIgnoreCase(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_START))) {
					timer = new Timer();
					startButton.setText(ResourceBundleUtil.getValueByKey(ResourceConstant.COMPONET_TYPE_PERFORMANCE_ANALYSIS_BUTTON_STOP));
					timer.schedule(new TimerTask() {
						public void run() {
							executePrfQuery(kpivalue);
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

	public void executePrfQuery(String kpivalue) {
		CimAPI cimAPI = new CimAPI();
		try {
			cimAPI.getWbemClient(smisProvider);
			ArrayCimAPI arrayCimAPI = new ArrayCimAPI(cimAPI);
			CIMInstance perfInstance = arrayCimAPI.queryStatisticalData(instance);
			Double _total = 0.0;
			Double _read = 0.0;
			Double _write = 0.0;
			
			Long _totalIOs = 0l;
			Long _readIOs = 0l;
			Long _writeIOs = 0l;
			
			Long _totalKBytes = 0l;
			Long _readKBytes = 0l;
			Long _writeKBytes = 0l;
			
			Long _totalHitIOs = 0l;
			Long _readHitIOs = 0l;
			Long _writeHitIOs = 0l;
			
			Long _totalIOTime = 0l;
			Long _readIOTime = 0l;
			Long _writeIOTime = 0l;
			
			String endTime = "";
			
			if (null != perfInstance) {
				CIMProperty<?>[] perfProperties = perfInstance.getProperties();
				for (int i = 0; i < perfProperties.length; i++) {
					final CIMProperty<?> cimProperty = perfProperties[i];
					final String propertyName = cimProperty.getName();
					
					if (propertyName.contains("StatisticTime")) {
						endTime = cimProperty.getValue().toString();
					}
					if (propertyName.contains("TotalIOs")) {
						_totalIOs = Long.valueOf(cimProperty.getValue().toString());
						//_totalIOs = totalIOs<=0?0:_totalIOs - totalIOs;
						_totalIOs = (_totalIOs - totalIOs)<0?0:(_totalIOs - totalIOs);
						totalIOs = Long.valueOf(cimProperty.getValue().toString());
						
					}
					if (propertyName.contains("ReadIOs")) {
						_readIOs = Long.valueOf(cimProperty.getValue().toString());
						//_readIOs = readIOs<=0?0:_readIOs - readIOs;
						_readIOs = (_readIOs - readIOs)<0?0:(_readIOs - readIOs);
						readIOs = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("WriteIOs")) {
						_writeIOs = Long.valueOf(cimProperty.getValue().toString());
						//_writeIOs = writeIOs<=0?0:_writeIOs - writeIOs;
						_writeIOs = (_writeIOs - writeIOs)<0?0:(_writeIOs - writeIOs);
						writeIOs = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("KBytesTransferred")) {
						_totalKBytes = Long.valueOf(cimProperty.getValue().toString());
						//_totalKBytes = totalKBytes<=0?0:_totalKBytes - totalKBytes;
						_totalKBytes = (_totalKBytes - totalKBytes)<0?0:(_totalKBytes - totalKBytes);
						totalKBytes = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("KBytesRead")) {
						_readKBytes = Long.valueOf(cimProperty.getValue().toString());
						//_readKBytes = readKBytes<=0?0:_readKBytes - readKBytes;
						_readKBytes = (_readKBytes - readKBytes)<0?0:(_readKBytes - readKBytes);
						readKBytes = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("KBytesWritten")) {
						_writeKBytes = Long.valueOf(cimProperty.getValue().toString());
						//_writeKBytes = writeKBytes<=0?0:_writeKBytes - writeKBytes;
						_writeKBytes = (_writeKBytes - writeKBytes)<0?0:(_writeKBytes - writeKBytes);
						writeKBytes = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("ReadHitIOs")) {
						_readHitIOs = Long.valueOf(cimProperty.getValue().toString());
						//_readHitIOs = readHitIOs<=0?0:_readHitIOs - readHitIOs;
						_readHitIOs = (_readHitIOs - readHitIOs)<0?0:(_readHitIOs - readHitIOs);
						readHitIOs = Long.valueOf(cimProperty.getValue().toString());
						_totalHitIOs = _totalHitIOs + _readHitIOs;
						
					}
					if (propertyName.contains("WriteHitIOs")) {
						_writeHitIOs = Long.valueOf(cimProperty.getValue().toString());
						//_writeHitIOs = writeHitIOs<=0?0:_writeHitIOs - writeHitIOs;
						_writeHitIOs = (_writeHitIOs - writeHitIOs)<0?0:(_writeHitIOs - writeHitIOs);
						writeHitIOs = Long.valueOf(cimProperty.getValue().toString());
						_totalHitIOs = _totalHitIOs + _writeHitIOs;
					}
					if (propertyName.contains("IOTimeCounter")
							&&!propertyName.contains("ReadHitIOTimeCounter")
							&&!propertyName.contains("WriteHitIOTimeCounter")
							&&!propertyName.contains("ReadIOTimeCounter")
							&&!propertyName.contains("WriteIOTimeCounter")) {
						_totalIOTime = Long.valueOf(cimProperty.getValue().toString());
						//_totalIOTime = totalIOTime<=0?0:_totalIOTime - totalIOTime;
						_totalIOTime = (_totalIOTime - totalIOTime)<0?0:(_totalIOTime - totalIOTime);
						totalIOTime = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("ReadIOTimeCounter")) {
						_readIOTime = Long.valueOf(cimProperty.getValue().toString());
						//_readIOTime = readIOTime<=0?0:_readIOTime - readIOTime;
						_readIOTime = (_readIOTime - readIOTime)<0?0:(_readIOTime - readIOTime);
						readIOTime = Long.valueOf(cimProperty.getValue().toString());
					}
					if (propertyName.contains("WriteIOTimeCounter")) {
						_writeIOTime = Long.valueOf(cimProperty.getValue().toString());
						//_writeIOTime = writeIOTime<=0?0:_writeIOTime - writeIOTime;
						_writeIOTime = (_writeIOTime - writeIOTime)<0?0:(_writeIOTime - writeIOTime);
						writeIOTime = Long.valueOf(cimProperty.getValue().toString());
					}
				}

				long seconds = 1;
				if (startTime.length()==0) {
					startTime = endTime;
					seconds = CommonUtil.calculateTimeDiffer(startTime, endTime);//*1000;
				}else {
					seconds = CommonUtil.calculateTimeDiffer(startTime, endTime);//*1000;
					startTime = endTime;
				}
				
				if (kpivalue.equalsIgnoreCase("I/O Rate")) {
					_total = (seconds==0?0.00:CommonUtil.calculateDoubleForLongData(_totalIOs , seconds));
					_read = (seconds==0?0.00:CommonUtil.calculateDoubleForLongData(_readIOs , seconds));
					_write = (seconds==0?0.00:CommonUtil.calculateDoubleForLongData(_writeIOs , seconds));
				} else if (kpivalue.equalsIgnoreCase("AVG KBPS")) {
					_total = (_totalIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_totalKBytes , _totalIOs);
					_read = (_readIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_readKBytes , _readIOs);
					_write = (_writeIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_writeKBytes , _writeIOs);
				} else if (kpivalue.equalsIgnoreCase("% HitIO")) {
					_total = (_totalIOs<=0)?0:(100.0 * (CommonUtil.calculateDoubleForLongData(_totalHitIOs, _totalIOs)));
					_read = (_readIOs<=0)?0:(100.0 * (CommonUtil.calculateDoubleForLongData(_readHitIOs , _readIOs)));
					_write = (_writeIOs<=0)?0:(100.0 * (CommonUtil.calculateDoubleForLongData(_writeHitIOs , _writeIOs)));
				} else if (kpivalue.equalsIgnoreCase("I/O Response Time")) {
					_total = (_totalIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_totalIOTime , _totalIOs);
					_read = (_readIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_readIOTime , _readIOs);
					_write = (_writeIOs<=0)?0:CommonUtil.calculateDoubleForLongData(_writeIOTime , _writeIOs);
				}
				logger.info("============totalIOs=" + totalIOs+" _totalIOs="+_totalIOs);
				logger.info("============readIOs=" + readIOs+" _readIOs="+_readIOs);
				logger.info("============writeIOs=" + writeIOs+" _writeIOs="+_writeIOs);
				logger.info("============totalKBytes=" + totalKBytes+" _totalKBytes="+_totalKBytes);
				logger.info("============readKBytes=" + readKBytes+" _readKBytes="+_readKBytes);
				logger.info("============writeKBytes=" + writeKBytes+" _writeKBytes="+_writeKBytes);
				logger.info("============totalHitIOs=" + totalHitIOs+" _totalHitIOs="+_totalHitIOs);
				logger.info("============readHitIOs=" + readHitIOs+" _readHitIOs="+_readHitIOs);
				logger.info("============writeHitIOs=" + writeHitIOs+" _writeHitIOs="+_writeHitIOs);
				logger.info("============totalIOTime=" + totalIOTime+" _totalIOTime="+_totalIOTime);
				logger.info("============readIOTime=" + readIOTime+" _readIOTime="+_readIOTime);
				logger.info("============writeIOTime=" + _writeIOTime+" _writeIOTime="+_writeIOTime);
				logger.info("============total=" + _total + ",      read=" + _read + ",     write=" + _write);
			}
			final Double total = _total;
			final Double read = _read;
			final Double write = _write;
			Display display = getShell().getDisplay();
			if (null != display) {
				display.syncExec(new Runnable() {
					public void run() {
						totalTimeSeries.add(new Millisecond(), total);
						readTimeSeries.add(new Millisecond(), read);
						writeTimeSeries.add(new Millisecond(), write);
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
