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
	
	public Visualisierungsfenster() {
		setBounds(100, 100, 805, 831);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Position Control", null, panel, null);
		panel.setLayout(new MigLayout("", "[150.00][41.00,grow,fill]", "[15.00][150.00][][150.00][][150.00][][150.00][][150.00]"));
		
		JLabel lblAngleX = new JLabel("Angle X-Axis");
		panel.add(lblAngleX, "cell 0 0");
		
		JLabel lblMotoren = new JLabel("Accelerometer");
		panel.add(lblMotoren, "cell 1 0,growx");
		
		horizonX = new LiveArtificialHorizon(Color.BLACK, Color.GREEN, Color.WHITE, true, 1);
		panel.add(horizonX, "cell 0 1");
		
		accGraph = new LiveLineGraph(Color.BLACK, 200, 100);
		accGraph.setRastertYColor(Color.WHITE);
		accGraph.setUseRasterY(true);
		accGraph.setRasterLineCountY(5);
		panel.add(accGraph, "cell 1 1");
		
		JLabel lblAngleYaxis = new JLabel("Angle Y-Axis");
		panel.add(lblAngleYaxis, "cell 0 2");
		
		JLabel lblAccelerometer = new JLabel("Gyros");
		panel.add(lblAccelerometer, "cell 1 2");
		
		horizonY = new LiveArtificialHorizon(Color.BLACK, Color.RED, Color.WHITE, true, 1);
		panel.add(horizonY, "cell 0 3");
		
		gyroGraph = new LiveLineGraph(Color.BLACK, 200, 100);
		gyroGraph.setUseRasterY(true);
		gyroGraph.setRastertYColor(Color.WHITE);
		gyroGraph.setRasterLineCountY(5);
		panel.add(gyroGraph, "cell 1 3");
		
		JLabel lblAngleZachsis = new JLabel("Angle Z-Achsis");
		panel.add(lblAngleZachsis, "cell 0 4");
		
		JLabel lblGyro = new JLabel("PID-Output");
		panel.add(lblGyro, "cell 1 4");
		
		horizonZ = new LiveArtificialHorizon(Color.BLACK, Color.BLUE, Color.WHITE, true, 1);
		panel.add(horizonZ, "cell 0 5");
		
		pidGraph = new LiveLineGraph(Color.BLACK, 4, 2);
		pidGraph.setRastertYColor(Color.WHITE);
		pidGraph.setRasterLineCountY(4);
		pidGraph.setUseRasterY(true);
		panel.add(pidGraph, "cell 1 5");
		
		JLabel lblRegleroutput = new JLabel("Motors");
		panel.add(lblRegleroutput, "cell 1 6");
		
		motorGraph = new LiveLineGraph(Color.BLACK, 100, 0);
		motorGraph.setRasterLineCountY(5);
		motorGraph.setUseRasterY(true);
		motorGraph.setRastertYColor(Color.WHITE);
		panel.add(motorGraph, "cell 1 7");
		
		JLabel label = new JLabel("RC Control Values");
		panel.add(label, "cell 1 8");
		
		rcGraph = new LiveLineGraph(Color.BLACK, 100, 0);
		panel.add(rcGraph, "cell 1 9");
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Instruments", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[80.00][80.00][80.00]", "[14.00][250.00,grow]"));
		
		JLabel lblCpuLoad = new JLabel("CPU Load");
		panel_1.add(lblCpuLoad, "cell 0 0");
		
		JLabel lblAltitude = new JLabel("Altitude");
		panel_1.add(lblAltitude, "cell 1 0");
		
		JLabel lblBatteryVoltage = new JLabel("Battery Voltage");
		panel_1.add(lblBatteryVoltage, "cell 2 0");
		
		cpuAltimeter = new LiveAltimeter(Color.BLACK, Color.GREEN, Color.WHITE, true, 1, 5, 100);
		panel_1.add(cpuAltimeter, "cell 0 1");
		
		altAltimeter = new LiveAltimeter(Color.BLACK, Color.RED, Color.WHITE, true, 1, 100, 100);
		panel_1.add(altAltimeter, "cell 1 1");
		
		voltAltimeter = new LiveAltimeter(Color.BLACK, Color.ORANGE, Color.WHITE, true, 1, 5, 15);
		panel_1.add(voltAltimeter, "cell 2 1");

	}
}
