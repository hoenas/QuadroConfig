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

public class Visualisierungsfenster extends JFrame {
	public LiveLineGraph motorGraph;
	public LiveLineGraph accGraph;
	public LiveLineGraph gyroGraph;
	/**
	 * Create the frame.
	 */
	public Visualisierungsfenster() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 805, 597);
		getContentPane().setLayout(null);
		
		JLabel lblMotoren = new JLabel("Motoren");
		lblMotoren.setBounds(10, 11, 46, 14);
		getContentPane().add(lblMotoren);
		
		motorGraph = new LiveLineGraph( Color.lightGray, 100 );
		motorGraph.setBounds(10, 31, 769, 138);
		getContentPane().add(motorGraph);
		
		accGraph = new LiveLineGraph( Color.lightGray, 20);
		accGraph.setBounds(10, 195, 769, 138);
		getContentPane().add(accGraph);
		
		JLabel label = new JLabel("Accelerometer:");
		label.setBounds(10, 175, 82, 14);
		getContentPane().add(label);
		
		gyroGraph = new LiveLineGraph( Color.lightGray, 20 );
		gyroGraph.setBounds(10, 359, 769, 138);
		getContentPane().add(gyroGraph);
		
		JLabel label_1 = new JLabel("Gyros:");
		label_1.setBounds(10, 339, 82, 14);
		getContentPane().add(label_1);

	}
}
