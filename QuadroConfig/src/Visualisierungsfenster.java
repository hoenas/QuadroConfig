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
	public LiveAltimeter altAltimeter;
	public LiveAltimeter voltAltimeter;
	public LiveAltimeter tempAltimeter;
	
	public Visualisierungsfenster() {
		setTitle("Monitoring Visualisation");
		setBounds(100, 100, 805, 584);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
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
		panel.add(accGraph, "cell 1 1,grow");
		
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
		
		JPanel panel_1 = new JPanel();
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
		
		altAltimeter = new LiveAltimeter(Color.BLACK, Color.RED, Color.WHITE, true, 1, 100, 100, "m");
		panel_1.add(altAltimeter, "cell 1 1,grow");
		
		voltAltimeter = new LiveAltimeter(Color.BLACK, Color.ORANGE, Color.WHITE, true, 1, 5, 15000, "mV");
		panel_1.add(voltAltimeter, "cell 2 1,grow");
		
		tempAltimeter = new LiveAltimeter(Color.BLACK, Color.RED, Color.WHITE, true, 1, 5, 50, "\u00B0C");
		panel_1.add(tempAltimeter, "cell 3 1,grow");

	}
}
