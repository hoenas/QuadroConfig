import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import LiveGraph.LiveAltimeter;

import java.awt.Color;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;

import LiveGraph.Dataset;
import LiveGraph.LiveCompass;
import LiveGraph.LiveLineGraph;
import LiveGraph.LiveArtificialHorizon;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;


public class TestFrame extends JFrame {
	
	private Random ran = new Random();
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public TestFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 413);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		LiveAltimeter liveAltimeter = new LiveAltimeter(Color.BLACK, Color.GREEN, Color.WHITE, true, 3, 5, 100);
		liveAltimeter.setBounds(10, 10, 100, 242);
		contentPane.add(liveAltimeter);
	
		
		LiveLineGraph liveLineGraph = new LiveLineGraph(Color.BLACK, 100, 0);
		liveLineGraph.setBounds(116, 10, 481, 100);
		contentPane.add(liveLineGraph);
		
		LiveArtificialHorizon liveArtificialHorizon = new LiveArtificialHorizon(Color.BLACK, Color.GREEN, Color.WHITE, true, 2);
		liveArtificialHorizon.setBounds(222, 116, 100, 100);
		contentPane.add(liveArtificialHorizon);
		
		Dataset dataset1 = new Dataset("LOL", Color.green, 1, 100);
		Dataset dataset2 = new Dataset("ROFL", Color.blue, 1, 100);
		
		liveLineGraph.addGraph( dataset1 );
		liveLineGraph.addGraph( dataset2 );
		
		
		LiveCompass liveCompass = new LiveCompass(Color.BLACK, Color.GREEN, Color.WHITE, true, 2);
		liveCompass.setBounds(116, 116, 100, 100);
		contentPane.add(liveCompass);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(354, 116, 200, 200);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("New tab", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		LiveArtificialHorizon liveArtificialHorizon_1 = new LiveArtificialHorizon(Color.BLACK, Color.GREEN, Color.WHITE, true, 1);
		liveArtificialHorizon_1.setBackground(Color.BLACK);
		panel.add(liveArtificialHorizon_1);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("New tab", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		LiveArtificialHorizon liveArtificialHorizon_2 = new LiveArtificialHorizon(Color.BLACK, Color.GREEN, Color.WHITE, true, 3);
		panel_1.add(liveArtificialHorizon_2, BorderLayout.CENTER);
		
		JButton btnNewButton = new JButton("refresh");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				liveAltimeter.update( ran.nextInt(100) );
				liveArtificialHorizon.update( ran.nextInt(180) );
				float[] floatis = new float[2];
				floatis[0] = ran.nextFloat() * 100;
				floatis[1] = ran.nextFloat() * 100;
				liveLineGraph.update( floatis );
				liveCompass.update( ran.nextInt( 360 ) );
				liveArtificialHorizon_1.update( ran.nextInt(180) );
				liveArtificialHorizon_2.update( ran.nextInt(180) );
			}
		});
		btnNewButton.setBounds(603, 11, 89, 23);
		contentPane.add(btnNewButton);
	}
}
