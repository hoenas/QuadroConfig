import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import LiveGraph.LiveAltimeter;

import java.awt.Color;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.security.PermissionCollection;
import java.awt.event.ActionEvent;
import java.util.Random;

import LiveGraph.Dataset;
import LiveGraph.LiveCompass;
import LiveGraph.LiveLineGraph;
import LiveGraph.LiveArtificialHorizon;

import javax.swing.JTabbedPane;
import javax.swing.Timer;

import java.awt.BorderLayout;

import LiveGraph.LiveXYViewer;
import LiveGraph.LiveGauge;
import javax.swing.JCheckBox;


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
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		LiveAltimeter liveAltimeter = new LiveAltimeter(Color.BLACK, Color.GREEN, Color.WHITE, true, 3, 5, 100, "m");
		liveAltimeter.setBounds(10, 10, 100, 242);
		contentPane.add(liveAltimeter);
	
		
		LiveLineGraph liveLineGraph = new LiveLineGraph(Color.BLACK, 100, 0);
		liveLineGraph.setBounds(116, 10, 438, 100);
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
		
		LiveXYViewer liveXYViewer = new LiveXYViewer(Color.BLACK, Color.GREEN, 10, 2, 100, 0, Color.WHITE, true, 1, 10, true, 25, Color.GREEN);
		liveXYViewer.setBounds(560, 116, 100, 100);
		contentPane.add(liveXYViewer);
		liveXYViewer.setDatasetX(dataset1);
		liveXYViewer.setDatasetY(dataset2);
		
		LiveGauge liveGauge = new LiveGauge(Color.DARK_GRAY, Color.WHITE, Color.BLACK, false, 0, 0, 0, (String) null);
		liveGauge.setBounds(560, 222, 132, 94);
		contentPane.add(liveGauge);
		
		JCheckBox chckbxGraphAnzeigen = new JCheckBox("Graph1 anzeigen");
		chckbxGraphAnzeigen.setBounds(560, 8, 148, 23);
		chckbxGraphAnzeigen.setSelected(true);
		contentPane.add(chckbxGraphAnzeigen);
		
		JCheckBox chckbxGraphAnzeigen_1 = new JCheckBox("Graph2 anzeigen");
		chckbxGraphAnzeigen_1.setBounds(560, 35, 148, 23);
		chckbxGraphAnzeigen_1.setSelected(true);
		contentPane.add(chckbxGraphAnzeigen_1);
		
		JCheckBox chckbxZufall = new JCheckBox("Zufallswerte");
		chckbxZufall.setBounds(563, 56, 129, 23);
		contentPane.add(chckbxZufall);
		
		Timer timer = new Timer(100, new ActionListener() {
			float percentage = 0.0f;
			float winkel = 0.0f;
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( chckbxGraphAnzeigen.isSelected()) {
					dataset1.setVisible(true);
				} else {
					dataset1.setVisible(false);
				}
				
				if( chckbxGraphAnzeigen_1.isSelected()) {
					dataset2.setVisible(true);
				} else {
					dataset2.setVisible(false);
				}
				if( chckbxZufall.isSelected() ) {
					liveAltimeter.update( ran.nextInt(100) );
					liveArtificialHorizon.update( ran.nextInt(180) );
					dataset1.addValue(ran.nextFloat() * 100);
					dataset2.addValue(ran.nextFloat() * 100);
					liveLineGraph.update();
					liveCompass.update( ran.nextInt( 360 ) );
					liveArtificialHorizon_1.update( ran.nextInt(180) );
					liveArtificialHorizon_2.update( ran.nextInt(180) );
					liveXYViewer.update();
					liveGauge.update( ran.nextFloat() * 100 );
				} else {
					
					double tmp = Math.sin(winkel);
					double tmp2 = Math.cos(winkel);
					
					liveAltimeter.update( (int)(Math.abs(tmp) * 100) );
					liveArtificialHorizon.update( (int)(tmp * 180) );
					dataset1.addValue( Math.abs( (float)tmp) * 100 );
					dataset2.addValue( Math.abs( (float)tmp2) * 100 );
					liveLineGraph.update();
					liveCompass.update( (int)(tmp * 360) );
					liveArtificialHorizon_1.update( (int)(tmp * 180) );
					liveArtificialHorizon_2.update( (int)(tmp * 180) );
					liveXYViewer.update();
					liveGauge.update( (float)Math.abs(tmp) * 100 );
					
					if(winkel>= 2 * Math.PI) {
						winkel = 0.0f;
					} else {
						winkel += 0.05;
					}
					
					if(percentage >= 100.0f) {
						percentage = 0.0f;
					} else {
						percentage += 0.1f;
					}
					
				}
			}
		});
		
		JButton btnNewButton = new JButton("Timer starten");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( timer.isRunning() )
				{
					timer.stop();
					btnNewButton.setText("Timer starten");
				} else {
					timer.start();
					btnNewButton.setText("Timer stoppen");
				}
			}
		});
		btnNewButton.setBounds(560, 87, 132, 23);
		contentPane.add(btnNewButton);
	}
}
