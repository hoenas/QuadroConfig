import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JButton;

import java.awt.RenderingHints.Key;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jfree.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

import javax.swing.SwingConstants;

import java.awt.Color;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import java.text.AttributedCharacterIterator;
import java.util.*;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.SpringLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import net.miginfocom.swing.MigLayout;

import java.awt.CardLayout;
import org.jfree.ui.*;

public class Main {

	private JFrame frame;
	private static boolean messungAktiv = false;
	
	private static Timer messtimer;
	private static SerialPortEventListener portListener;
	private static int messintervall = 250;
	
	private static SerialPort port;
	
	// Befehle für Protokoll
	private static byte COMMAND_MEASUREMENT = (byte)0b10000000;
	private static byte COMMAND_RESET = (byte)0b01000000;
	private static byte COMMAND_CONFIGURATION = (byte)0b00100000;
	
	private static byte[] buffer;
	private static int bufferLength = 50;
	
	// Visuelle Darstellung
	private static Plot plots[];
	private static int plotHistoryLength;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
					
					// Port-Konfiguration aufrufen
					PortConfig config = new PortConfig();
					config.setVisible(true);
					
					// Port konfigurieren
					port = new SerialPort( config.portName );
					port.setParams(config.baudRate, config.databits, config.stoppbits, config.parity);
					port.openPort();
					
					// Nur bei einkommenden Daten Event feuern
					port.setEventsMask( SerialPort.MASK_RXCHAR );
					
					// Listener erstellen
					portListener = new SerialPortEventListener() {
						
						@Override
						public void serialEvent(SerialPortEvent arg0) {
							
						}
					};
					port.addEventListener( portListener );
					
					buffer = new byte[ bufferLength ];
					plots = new Plot[ plotHistoryLength ];
					
					Rectangle rect = new Rectangle(0, 0, 500, 500);
					
					JFreeChart mychart = new JFreeChart(plots[0]);
					mychart.setAntiAlias(true);
					

					
					// mychart.draw(gfx, rect);				
					
					// Timerkonfiguration
					messtimer = new Timer();
					messtimer.scheduleAtFixedRate(new TimerTask() {
						public void run() {
							if(messungAktiv) {
								try {
									// Daten anfordern
									System.out.println("Messung angefordert");
									//port.writeByte(COMMAND_MEASUREMENT);
								} catch( Exception e ) {
									System.out.println( e.getMessage() );
								}
							}
						}
					}, 0, messintervall);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 763, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JButton btnMessungStartenpausieren = new JButton("Messung starten");
		menuBar.add(btnMessungStartenpausieren);
		btnMessungStartenpausieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!messungAktiv) {
					btnMessungStartenpausieren.setText("Messung pausieren");
				} else {
					btnMessungStartenpausieren.setText("Messung starten");
				}
				messungAktiv = !messungAktiv;
			}
		});
		btnMessungStartenpausieren.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});

		JButton btnQuadrokopterKonfigurieren = new JButton(
				"Quadrokopter konfigurieren");
		btnQuadrokopterKonfigurieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menuBar.add(btnQuadrokopterKonfigurieren);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		Canvas canvas = new Canvas();
		frame.getContentPane().add(canvas, "name_10652026702458");

	}

	private void updateGraphs() {
		System.out.println("Update");
	}
}
