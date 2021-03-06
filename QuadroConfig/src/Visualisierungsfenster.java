import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import LiveGraph.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.JTabbedPane;

public class Visualisierungsfenster extends JFrame {
	
	private Color panel_background = Color.GRAY;
	private Color window_background = Color.DARK_GRAY;
	
	// Globale Graphen-Definitionen
	// Tab: Position Control
	public LiveLineGraph accGraph;
	public LiveLineGraph gyroGraph;
	public LiveLineGraph pidGraph;
	public LiveLineGraph motorGraph;
	public LiveLineGraph rcGraph;
	public LiveArtificialHorizon horizonX;
	public LiveArtificialHorizon horizonY;
	public LiveArtificialHorizon horizonZ;
	// Tab: Instruments
	public LiveAltimeter cpuAltimeter;
	public LiveAltimeter relAltAltimeter;
	public LiveAltimeter voltAltimeter;
	public LiveAltimeter tempAltimeter;
	
	// Tab: PID
	public LiveXYViewer liveXYViewer_velocity;
	public LiveXYViewer liveXYViewer_accel;
	public LiveXYViewer liveXYViewer_motor;
	public LiveLineGraph liveLineGraph_velocity;
	public LiveLineGraph liveLineGraph_accel;
	public LiveLineGraph liveLineGraph_motor;
	private JLabel lblVel;
	
	// Tab: Fancy Instruments
	private JPanel panel_3;
	private JLabel lblFancyCpuLoad;
	public LiveGauge fancyCPUGraph;
	private JLabel lblFancyTemp;
	public LiveGauge fancyTempGauge;
	public LiveGauge fancyBatteryGauge;
	private JLabel lblFancyBattery;
	
	public Visualisierungsfenster() {
		setTitle("Monitoring Visualisation");
		setBounds(100, 100, 805, 584);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().setBackground(window_background);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setBackground(panel_background);
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		tabbedPane.addTab("Position Control", null, panel, null);
		panel.setLayout(new MigLayout("", "[75][100px:41.00,grow,fill]", "[15.00][10:75:75,grow 150,shrink 10,fill][][10:75:75,grow 150,shrink 10,fill][][10:75:75,grow 150,shrink 10,fill][][10:75:75,grow 150,shrink 10,fill][][10:75:75,grow 150,shrink 10,fill]"));
		
		JLabel lblAngleX = new JLabel("Angle X-Axis");
		panel.add(lblAngleX, "cell 0 0");
		
		JLabel lblMotoren = new JLabel("Accelerometer");
		panel.add(lblMotoren, "cell 1 0,growx");
		
		horizonX = new LiveArtificialHorizon(Color.BLACK, Color.GREEN, Color.WHITE, true, 1);
		panel.add(horizonX, "cell 0 1,grow");
		
		accGraph = new LiveLineGraph(Color.BLACK, 30, 15);
		accGraph.setRastertYColor(Color.WHITE);
		accGraph.setUseRasterY(true);
		accGraph.setRasterLineCountY(5);
		panel.add(accGraph, "cell 1 1,alignx left,growy");
		
		JLabel lblAngleYaxis = new JLabel("Angle Y-Axis");
		panel.add(lblAngleYaxis, "cell 0 2");
		
		JLabel lblAccelerometer = new JLabel("Gyros");
		panel.add(lblAccelerometer, "cell 1 2");
		
		horizonY = new LiveArtificialHorizon(Color.BLACK, Color.RED, Color.WHITE, true, 1);
		panel.add(horizonY, "cell 0 3,grow");
		
		gyroGraph = new LiveLineGraph(Color.BLACK, 30, 15);
		gyroGraph.setUseRasterY(true);
		gyroGraph.setRastertYColor(Color.WHITE);
		gyroGraph.setRasterLineCountY(5);
		panel.add(gyroGraph, "cell 1 3,grow");
		
		JLabel lblAngleZachsis = new JLabel("Angle Z-Axis");
		panel.add(lblAngleZachsis, "cell 0 4");
		
		JLabel lblGyro = new JLabel("PID-Output");
		panel.add(lblGyro, "cell 1 4");
		
		horizonZ = new LiveArtificialHorizon(Color.BLACK, Color.BLUE, Color.WHITE, true, 1);
		panel.add(horizonZ, "cell 0 5,grow");
		
		pidGraph = new LiveLineGraph(Color.BLACK, 20, 10);
		pidGraph.setRastertYColor(Color.WHITE);
		pidGraph.setRasterLineCountY(4);
		pidGraph.setUseRasterY(true);
		panel.add(pidGraph, "cell 1 5,grow");
		
		JLabel lblRegleroutput = new JLabel("Motors");
		panel.add(lblRegleroutput, "cell 1 6");
		
		motorGraph = new LiveLineGraph(Color.BLACK, 100, 0);
		motorGraph.setUseRasterY(true);
		motorGraph.setRasterLineCountY(5);
		motorGraph.setRastertYColor(Color.WHITE);
		panel.add(motorGraph, "cell 1 7,grow");
		
		JLabel label = new JLabel("RC Control Values");
		panel.add(label, "cell 1 8");
		
		rcGraph = new LiveLineGraph(Color.BLACK, 101, 0);
		rcGraph.setUseRasterY(true);
		rcGraph.setRastertYColor(Color.WHITE);
		rcGraph.setRasterLineCountY(5);
		panel.add(rcGraph, "cell 1 9,grow");
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("PIDPIDPIDPID", null, panel_2, null);
		panel_2.setLayout(new MigLayout("", "[100][550]", "[20][100][20][100][20][100px]"));
		
		lblVel = new JLabel("Vel");
		panel_2.add(lblVel, "cell 0 0");
		
		liveXYViewer_velocity = new LiveXYViewer(Color.BLACK, Color.GREEN, 10, 2, 40, 20, Color.WHITE, true, 1, 10, true, 25, Color.GREEN);
		liveXYViewer_velocity.setForeground(Color.RED);
		liveXYViewer_velocity.setRastertColor(Color.WHITE);
		liveXYViewer_velocity.setRasterWidth(1);
		liveXYViewer_velocity.setRasterThickness(1);
		liveXYViewer_velocity.setBackground(Color.BLACK);
		panel_2.add(liveXYViewer_velocity, "cell 0 1,grow");
		
		liveLineGraph_velocity = new LiveLineGraph(Color.BLACK, 20,10);
		liveLineGraph_velocity.setUseRasterY(true);
		liveLineGraph_velocity.setRastertYColor(Color.WHITE);
		liveLineGraph_velocity.setRasterLineCountY(5);
		liveLineGraph_velocity.setBackground(Color.BLACK);
		panel_2.add(liveLineGraph_velocity, "cell 1 1,grow");
		
		liveXYViewer_accel = new LiveXYViewer(Color.BLACK, Color.GREEN, 10, 2, 40, 20, Color.WHITE, true, 1, 10, true, 25, Color.GREEN);
		liveXYViewer_accel.setRastertColor(Color.WHITE);
		liveXYViewer_accel.setRasterWidth(1);
		liveXYViewer_accel.setRasterThickness(1);
		liveXYViewer_accel.setForeground(Color.GREEN);
		liveXYViewer_accel.setBackground(Color.BLACK);
		panel_2.add(liveXYViewer_accel, "cell 0 3,grow");
		
		liveLineGraph_accel = new LiveLineGraph(Color.BLACK, 20, 0);
		liveLineGraph_accel.setUseRasterY(true);
		liveLineGraph_accel.setBackground(Color.BLACK);
		panel_2.add(liveLineGraph_accel, "cell 1 3,grow");
		
		liveXYViewer_motor = new LiveXYViewer(Color.BLACK, Color.GREEN, 10, 2, 40, 20, Color.WHITE, true, 1, 10, true, 25, Color.GREEN);
		liveXYViewer_motor.setRastertColor(Color.WHITE);
		liveXYViewer_motor.setRasterWidth(1);
		liveXYViewer_motor.setRasterThickness(1);
		liveXYViewer_motor.setForeground(Color.BLUE);
		liveXYViewer_motor.setBackground(Color.BLACK);
		panel_2.add(liveXYViewer_motor, "cell 0 5,grow");
		
		liveLineGraph_motor = new LiveLineGraph(Color.BLACK, 16, 8);
		liveLineGraph_motor.setUseRasterY(true);
		liveLineGraph_motor.setBackground(Color.BLACK);
		panel_2.add(liveLineGraph_motor, "cell 1 5,grow");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GRAY);
		tabbedPane.addTab("Instruments", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[100.00][100.00][100.00][100][]", "[14.00][100px:500.00,top]"));
		
		JLabel lblCpuLoad = new JLabel("CPU Load");
		panel_1.add(lblCpuLoad, "cell 0 0");
		
		JLabel lblAltitude = new JLabel("Rel. Altitude");
		panel_1.add(lblAltitude, "cell 1 0");
		
		JLabel lblBatteryVoltage = new JLabel("Battery Voltage");
		panel_1.add(lblBatteryVoltage, "cell 2 0");
		
		JLabel lblTemperature = new JLabel("Temperature");
		panel_1.add(lblTemperature, "cell 3 0");
		
		cpuAltimeter = new LiveAltimeter(Color.BLACK, Color.GREEN, Color.WHITE, true, 1, 5, 100, "%");
		panel_1.add(cpuAltimeter, "cell 0 1,grow");
		
		relAltAltimeter = new LiveAltimeter(Color.BLACK, Color.RED, Color.WHITE, true, 1, 100, 100, "m");
		panel_1.add(relAltAltimeter, "cell 1 1,grow");
		
		voltAltimeter = new LiveAltimeter(Color.BLACK, Color.ORANGE, Color.WHITE, true, 1, 5, 15000, "mV");
		panel_1.add(voltAltimeter, "cell 2 1,grow");
		
		tempAltimeter = new LiveAltimeter(Color.BLACK, Color.RED, Color.WHITE, true, 1, 5, 50, "\u00B0C");
		panel_1.add(tempAltimeter, "cell 3 1,grow");
		
		panel_3 = new JPanel();
		panel_3.setBackground(Color.GRAY);
		tabbedPane.addTab("Fancy Instruments", null, panel_3, null);
		panel_3.setLayout(new MigLayout("", "[100][100][100]", "[][50]"));
		
		lblFancyCpuLoad = new JLabel("Fancy CPU Load");
		panel_3.add(lblFancyCpuLoad, "cell 0 0");
		
		lblFancyTemp = new JLabel("Fancy Temp");
		panel_3.add(lblFancyTemp, "cell 1 0");
		
		lblFancyBattery = new JLabel("Fancy Battery");
		panel_3.add(lblFancyBattery, "cell 2 0");
		
		fancyCPUGraph = new LiveGauge(Color.GRAY, Color.WHITE, Color.DARK_GRAY, true, 100);
		panel_3.add(fancyCPUGraph, "cell 0 1,grow");
		
		fancyTempGauge = new LiveGauge(Color.GRAY, Color.WHITE, Color.DARK_GRAY, true, 100);
		panel_3.add(fancyTempGauge, "cell 1 1,grow");
		
		fancyBatteryGauge = new LiveGauge(Color.GRAY, Color.WHITE, Color.DARK_GRAY, true, 16000);
		panel_3.add(fancyBatteryGauge, "cell 2 1,grow");

	}
}
