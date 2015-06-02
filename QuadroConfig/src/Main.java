import java.awt.EventQueue;

import javax.net.ssl.SSLEngineResult.Status;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import jssc.SerialPort;
import jssc.SerialPortList;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import java.awt.Panel;
import java.awt.Font;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.Component;

import javax.swing.Box;


public class Main {

	private SerialPort port;
	private JFrame frame;
	private JFrame visualisierung = new JFrame();
	private Timer messtimer;
	private int messintervall = 200;
	private boolean messungAktiv = false;
	// Befehle f�r Protokoll
	private static int protocolStatus = 0;
	/* Werte f�r protocolStatus:
	 * 0: idle
	 * 1: receiveFrame
	 * 2: receiveConfiguration
	 * 3: sendConfiguration
	 */
	// TODO: richtige Befehlswerte senden
	private static byte COMMAND_MEASUREMENT = (byte)0x03;
	private static byte COMMAND_RESET = (byte)0b01000000;
	private static byte COMMAND_GET_CONFIGURATION = (byte)0b00100000;
	private static byte COMMAND_SET_CONFIGURATION = (byte)0b00010000;
	// TODO: richtige Werte
	private static int MEASUREMENT_FRAME_LENGTH = 24;
	public float[] messdaten = new float[MEASUREMENT_FRAME_LENGTH];
	private static int CONFIGURATION_FRAME_LENGTH = 24;	
	// Timeout in ms
	private static long COMMUNICATION_TIMEOUT = 100;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
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
	
	public byte[] floatToByteArray( float f, byte[] byteArray, int offset) {
		int integer = Float.floatToIntBits( f );
		byteArray[0 + offset] = (byte)( integer & 0b00000000000000000000000011111111);
		byteArray[1 + offset] = (byte)(( integer & 0b00000000000000001111111100000000) >> 8);
		byteArray[2 + offset] = (byte)(( integer & 0b00000000111111110000000000000000) >> 16);
		byteArray[3 + offset] = (byte)(( integer & 0b11111111000000000000000000000000) >> 24);
		
		return byteArray;
	}
	
	public float byteArrayToFloat( byte[] byteArray, int offset) {
		int temp = (int)byteArray[0 + offset];
		temp += (int)(byteArray[1 + offset] << 8);
		temp += (int)(byteArray[2 + offset] << 16);
		temp += (int)(byteArray[3 + offset] << 24);
		
		return Float.intBitsToFloat( temp );
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 433, 328);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 396, 240);
		frame.getContentPane().add(tabbedPane);
		
		JLabel statuslabel = new JLabel("");
		statuslabel.setBounds(10, 262, 414, 14);
		frame.getContentPane().add(statuslabel);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Konfiguration", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblPortname = new JLabel("Port-Name:");
		lblPortname.setBounds(10, 11, 74, 14);
		panel.add(lblPortname);
		
		JLabel lblBaudrate = new JLabel("Baud-Rate:");
		lblBaudrate.setBounds(10, 39, 74, 14);
		panel.add(lblBaudrate);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(94, 8, 109, 20);
		String[] portNames = SerialPortList.getPortNames();
		for(int i = 0; i < portNames.length; i++){
            comboBox.addItem(portNames[i]);
        }
		panel.add(comboBox);
		
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(94, 36, 109, 20);
		comboBox_1.addItem(SerialPort.BAUDRATE_110);
		comboBox_1.addItem(SerialPort.BAUDRATE_300);
		comboBox_1.addItem(SerialPort.BAUDRATE_600);
		comboBox_1.addItem(SerialPort.BAUDRATE_1200);
		comboBox_1.addItem(SerialPort.BAUDRATE_4800);
		comboBox_1.addItem(SerialPort.BAUDRATE_9600);
		comboBox_1.addItem(SerialPort.BAUDRATE_14400);
		comboBox_1.addItem(SerialPort.BAUDRATE_38400);
		comboBox_1.addItem(SerialPort.BAUDRATE_57600);
		comboBox_1.addItem(SerialPort.BAUDRATE_115200);
		comboBox_1.addItem(SerialPort.BAUDRATE_128000);
		comboBox_1.setSelectedIndex(9);
		panel.add(comboBox_1);
		
		JLabel lblParitt = new JLabel("Parit\u00E4t:");
		lblParitt.setBounds(10, 126, 74, 14);
		panel.add(lblParitt);
		
		JComboBox comboBox_2 = new JComboBox();
		panel.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setBounds(94, 64, 109, 20);
		comboBox_3.addItem(SerialPort.DATABITS_5);
		comboBox_3.addItem(SerialPort.DATABITS_6);
		comboBox_3.addItem(SerialPort.DATABITS_7);
		comboBox_3.addItem(SerialPort.DATABITS_8);
		comboBox_3.setSelectedIndex(3);
		panel.add(comboBox_3);
		
		JLabel lblDatenbits = new JLabel("Datenbits:");
		lblDatenbits.setBounds(10, 67, 74, 14);
		panel.add(lblDatenbits);
		
		JLabel lblStoppbits = new JLabel("Stoppbits:");
		lblStoppbits.setBounds(10, 98, 74, 14);
		panel.add(lblStoppbits);
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setBounds(94, 95, 109, 20);
		comboBox_4.addItem(SerialPort.STOPBITS_1);
		comboBox_4.addItem(SerialPort.STOPBITS_2);
		comboBox_4.addItem(SerialPort.STOPBITS_1_5);
		comboBox_4.setSelectedIndex(0);
		panel.add(comboBox_4);
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setBounds(94, 123, 109, 20);
		comboBox_5.addItem("keine");
		comboBox_5.addItem("gerade");
		comboBox_5.addItem("ungerade");
		comboBox_5.setSelectedIndex(2);
		panel.add(comboBox_5);
		
		JButton btnNewButton = new JButton("Port \u00F6ffnen");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if( port == null || !port.isOpened() ) {
						port = new SerialPort( comboBox.getSelectedItem().toString() );
						int parity = 0;
						String auswahl = comboBox_5.getSelectedItem().toString();
						
						if( auswahl == "gerade" ) {
							parity = SerialPort.PARITY_EVEN;
						} else if ( auswahl == "ungerade" ) {
							parity = SerialPort.PARITY_ODD;
						} else {
							parity = SerialPort.PARITY_NONE;
						}

						port.openPort();	
						port.setParams(Integer.valueOf(comboBox_1.getSelectedItem().toString()), Integer.valueOf(comboBox_3.getSelectedItem().toString()), Integer.valueOf(comboBox_4.getSelectedItem().toString()), parity);
								
						comboBox.setEnabled(false);
						comboBox_1.setEnabled(false);
						comboBox_3.setEnabled(false);
						comboBox_4.setEnabled(false);
						comboBox_5.setEnabled(false);
						statuslabel.setText("Port ge�ffnet");
						btnNewButton.setText("Port schlie�en");
					}
					else
					{
						port.closePort();
						comboBox.setEnabled(true);
						comboBox_1.setEnabled(true);
						comboBox_3.setEnabled(true);
						comboBox_4.setEnabled(true);
						comboBox_5.setEnabled(true);
						statuslabel.setText("Port geschlossen");
						btnNewButton.setText("Port �ffnen");
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
		btnNewButton.setBounds(10, 151, 193, 23);
		panel.add(btnNewButton);
		

		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Messung", null, panel_1, null);
		tabbedPane.setEnabledAt(1, true);
		panel_1.setLayout(null);
		
		JButton btnMessungStarten = new JButton("Messung starten");
		btnMessungStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( !messungAktiv ) {
					btnMessungStarten.setText("Messung pausieren");
					statuslabel.setText("Messung fortgesetzt");
					
				} else {
					btnMessungStarten.setText("Messung starten");
					statuslabel.setText("Messung pausiert");
				}
				messungAktiv = !messungAktiv;
			}	
		});
		btnMessungStarten.setBounds(10, 11, 371, 23);
		panel_1.add(btnMessungStarten);
		
		JButton btnNewButton_1 = new JButton("Visualisierungsfenster anzeigen");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visualisierung.setSize(500, 500);
				visualisierung.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(10, 45, 371, 23);
		panel_1.add(btnNewButton_1);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Quadrokopter konfigurieren", null, panel_2, null);
		panel_2.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 11, 179, 115);
		panel_2.add(panel_3);
		panel_3.setLayout(null);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(43, 11, 1, 2);
		panel_3.add(separator);
		
		JLabel lblPanteil = new JLabel("P-Anteil:");
		lblPanteil.setBounds(10, 37, 65, 14);
		panel_3.add(lblPanteil);
		
		JLabel label = new JLabel("I-Anteil:");
		label.setBounds(10, 62, 65, 14);
		panel_3.add(label);
		
		JSpinner spinner_pxy = new JSpinner();
		spinner_pxy.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_pxy.setBounds(85, 34, 84, 20);
		panel_3.add(spinner_pxy);
		
		JSpinner spinner_ixy = new JSpinner();
		spinner_ixy.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_ixy.setBounds(85, 59, 84, 20);
		panel_3.add(spinner_ixy);
		
		JLabel label_1 = new JLabel("D-Anteil");
		label_1.setBounds(10, 87, 65, 14);
		panel_3.add(label_1);
		
		JSpinner spinner_dxy = new JSpinner();
		spinner_dxy.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_dxy.setBounds(85, 84, 84, 20);
		panel_3.add(spinner_dxy);
		
		JLabel lblPidreglerXy = new JLabel("PID-Regler XY:");
		lblPidreglerXy.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPidreglerXy.setBounds(10, 11, 84, 14);
		panel_3.add(lblPidreglerXy);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBounds(199, 11, 179, 115);
		panel_2.add(panel_4);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(43, 11, 1, 2);
		panel_4.add(separator_1);
		
		JLabel label_3 = new JLabel("P-Anteil:");
		label_3.setBounds(10, 37, 65, 14);
		panel_4.add(label_3);
		
		JLabel label_4 = new JLabel("I-Anteil:");
		label_4.setBounds(10, 62, 65, 14);
		panel_4.add(label_4);
		
		JSpinner spinner_pz = new JSpinner();
		spinner_pz.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_pz.setBounds(85, 34, 84, 20);
		panel_4.add(spinner_pz);
		
		JSpinner spinner_iz = new JSpinner();
		spinner_iz.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_iz.setBounds(85, 59, 84, 20);
		panel_4.add(spinner_iz);
		
		JLabel label_5 = new JLabel("D-Anteil");
		label_5.setBounds(10, 87, 65, 14);
		panel_4.add(label_5);
		
		JSpinner spinner_dz = new JSpinner();
		spinner_dz.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(1)));
		spinner_dz.setBounds(85, 84, 84, 20);
		panel_4.add(spinner_dz);
		
		JLabel label_2 = new JLabel("PID-Regler Z:");
		label_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_2.setBounds(10, 11, 84, 14);
		panel_4.add(label_2);
		
		JButton btnWerteLaden = new JButton("Werte laden");
		btnWerteLaden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Keine neuen Messungen anfordern
				messungAktiv = false;
				// Falls Port offen ist Konfiguration anfordern
				while( protocolStatus != 0 ) {
					// Abwarten, bis Portzustand: idle
				}
				
				try {
					port.writeByte( COMMAND_GET_CONFIGURATION );
					// Protokollstatus: receiveConfiguration
					protocolStatus = 2;
					
					boolean timeout = false;
					LocalTime begin = LocalTime.now();
					while( port.getInputBufferBytesCount() != CONFIGURATION_FRAME_LENGTH) {
						// warte bis alle Bytes da sind
						// auf Timeout pr�fen
						if( LocalTime.now().isAfter( begin.plus(COMMUNICATION_TIMEOUT, ChronoUnit.MILLIS) ) ) {
							timeout = true;
							protocolStatus = 0;
							statuslabel.setText("Timout bei Kommunikation. Konfiguration nicht erhalten.");
							break;
						}
					}
					
					if(!timeout) {
						/* Konfigurationsframe:
						 * Byte
						 * 0 - 3	: P-Anteil XY (float)
						 * 4 - 7	: I-Anteil XY (float)
						 * 8 - 11	: Z-Anteil XY (float)
						 * 12 - 15	: P-Anteil Z (float)
						 * 16 - 19	: I-Anteil Z (float)
						 * 20 - 23	: D-Anteil Z (float)
						 * ...
						 */
						byte[] byteArray = port.readBytes( CONFIGURATION_FRAME_LENGTH );
						spinner_pxy.setValue( byteArrayToFloat(byteArray, 0) );
						spinner_ixy.setValue( byteArrayToFloat(byteArray, 4) );
						spinner_dxy.setValue( byteArrayToFloat(byteArray, 8) );
						spinner_pz.setValue( byteArrayToFloat(byteArray, 12) );
						spinner_iz.setValue( byteArrayToFloat(byteArray, 16) );
						spinner_dz.setValue( byteArrayToFloat(byteArray, 20) );
						
						protocolStatus = 0;
						statuslabel.setText("Konfiguration empfangen");
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		btnWerteLaden.setBounds(5, 178, 113, 23);
		panel_2.add(btnWerteLaden);
		
		JButton button = new JButton("Werte speichern");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( port != null && port.isOpened() ) {
					// Keine neuen Messungen anfordern
					messungAktiv = false;
					// Falls Port offen ist Konfiguration senden
					while( protocolStatus != 0 ) {
						// Abwarten, bis Portzustand: idle
					}
					try {
						// Anfrage stellen
						port.writeByte( COMMAND_SET_CONFIGURATION );
						// Protokollstatus: sendConfiguration
						protocolStatus = 3;
						/* Konfigurationsframe:
						 * Byte
						 * 0 - 3	: P-Anteil XY (float)
						 * 4 - 7	: I-Anteil XY (float)
						 * 8 - 11	: Z-Anteil XY (float)
						 * 12 - 15	: P-Anteil Z (float)
						 * 16 - 19	: I-Anteil Z (float)
						 * 20 - 23	: D-Anteil Z (float)
						 * ...
						 */
						byte[] outbuffer = new byte[24];
						outbuffer = floatToByteArray( (float)spinner_pxy.getValue(), outbuffer, 0 );
						outbuffer = floatToByteArray( (float)spinner_ixy.getValue(), outbuffer, 4 );
						outbuffer = floatToByteArray( (float)spinner_dxy.getValue(), outbuffer, 8 );
						outbuffer = floatToByteArray( (float)spinner_pz.getValue(), outbuffer, 12 );
						outbuffer = floatToByteArray( (float)spinner_iz.getValue(), outbuffer, 16 );
						outbuffer = floatToByteArray( (float)spinner_dz.getValue(), outbuffer, 20 );
						// ...

						port.writeBytes(outbuffer);					
						protocolStatus = 0;
						statuslabel.setText("Konfiguration �bertragen");
						
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		button.setBounds(128, 178, 113, 23);
		panel_2.add(button);
	
		
		// Timerkonfiguration
		messtimer = new Timer();
		messtimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(messungAktiv && port.isOpened() ) {
					try {
						// Daten anfordern
						port.writeByte(COMMAND_MEASUREMENT);
						
						// Warten auf Daten
						boolean timeout = false;
						LocalTime begin = LocalTime.now();
						while( port.getInputBufferBytesCount() != MEASUREMENT_FRAME_LENGTH ) {
							// warte bis alle Bytes da sind
							// auf Timeout pr�fen
							if( LocalTime.now().isAfter( begin.plus(COMMUNICATION_TIMEOUT, ChronoUnit.MILLIS) ) ) {
								timeout = true;
								protocolStatus = 0;
								statuslabel.setText("Timout bei Kommunikation. Messwerte nicht erhalten.");
								break;
							}
						}
						
						if(!timeout) {
							// Daten auswerten
							byte[] temp = port.readBytes();
							
							for(int i = 0; i < messdaten.length; i++) {
								messdaten[i] = byteArrayToFloat(temp, i * 4);
							}							
						}
						
					} catch( Exception e ) {
						System.out.println( e.getMessage() );
					}
				}
			}
		}, 0, messintervall);
	}
}
