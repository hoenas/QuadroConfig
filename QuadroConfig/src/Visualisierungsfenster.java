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
	/**
	 * Create the frame.
	 */
	public LiveLineGraph motorGraph;
	public LiveLineGraph accGraph;
	public LiveLineGraph gyroGraph;
	
	public Visualisierungsfenster() {
		setBounds(100, 100, 805, 584);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Lageregelung", null, panel, null);
		panel.setLayout(null);
		
		JLabel label = new JLabel("Motoren");
		label.setBounds(10, 11, 200, 14);
		panel.add(label);
		
		motorGraph = new LiveLineGraph(Color.LIGHT_GRAY, 101, 1);
		motorGraph.setBounds(10, 31, 769, 138);
		panel.add(motorGraph);
		
		JLabel label_1 = new JLabel("Accelerometer:");
		label_1.setBounds(10, 175, 200, 14);
		panel.add(label_1);
		
		accGraph = new LiveLineGraph(Color.LIGHT_GRAY, 20, 10);
		accGraph.setBounds(10, 195, 769, 138);
		panel.add(accGraph);
		
		JLabel label_2 = new JLabel("Gyros:");
		label_2.setBounds(10, 339, 200, 14);
		panel.add(label_2);
		
		gyroGraph = new LiveLineGraph(Color.LIGHT_GRAY, 40, 20);
		gyroGraph.setBounds(10, 359, 769, 138);
		panel.add(gyroGraph);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Sensoren", null, panel_1, null);

	}
}
