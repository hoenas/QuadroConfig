import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import LiveGraph.*;

public class Visualisierungsfenster extends JFrame {

	private JPanel contentPane;
	public LiveLineGraph motorsGraph;

	/**
	 * Create the frame.
	 */
	public Visualisierungsfenster() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		motorsGraph = new LiveLineGraph(Color.WHITE, 100);
		contentPane.add(motorsGraph, BorderLayout.CENTER);

	}

}
