import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import jssc.SerialPort;
import jssc.SerialPortList;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSeparator;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.Image;

import javax.swing.border.LineBorder;

import LiveGraph.Dataset;

import java.awt.Color;


public class Main {

	private SerialPort port;
	private JFrame frame;
	private Timer messtimer;
	private int messintervall = 16;
	private boolean messungAktiv = false;
	// Befehle f�r Protokoll
	private static int protocolStatus = 0;
	/* Werte f�r protocolStatus:
	 * 0: idle
	 * 1: receiveFrame
	 * 2: receiveConfiguration
	 * 3: sendConfiguration
	 */
	
	/* get status and global flags*/
	private static byte USB_CMD_SEND_STATUS_FLOAT = (byte)0x03;
	private static byte USB_CMD_GLOBAL_FLAGS = (byte)0x04;
	
	/* configuration*/
	private static byte USB_CMD_CONFIG_MODE = (byte)0xC0;
	
	private static byte USB_CMD_GET_CONFIG = (byte)0xC1;
	private static byte USB_CMD_UPDATE_CONFIG = (byte)0xC2;
	private static byte USB_CMD_SAVE_CONFIG = (byte)0xCE;
	private static byte USB_CMD_RESTORE_CONFIG = (byte)0xCF;

	/* eeprom acces */
	private static byte USB_CMD_READ_BYTE = (byte)0xC3;
	private static byte USB_CMD_READ_2BYTES = (byte)0xC4;
	private static byte USB_CMD_READ_4BYTES = (byte)0xC5;

	private static byte USB_CMD_WRITE_BYTE = (byte)0xC6;
	private static byte USB_CMD_WRITE_2BYTES = (byte)0xC7;
	private static byte USB_CMD_WRITE_4BYTES = (byte)0xC8;	
	
	/* reset */
	private static byte USB_CMD_RESET = (byte)0xFF;	
	
	
	// TODO: richtige Werte
	private static int MEASUREMENT_FRAME_LENGTH = 124;
	public float[] messdaten = new float[MEASUREMENT_FRAME_LENGTH/4];
	private static int CONFIGURATION_FRAME_LENGTH = 32;	
	// Timeout in ms
	private static long COMMUNICATION_TIMEOUT = 1000;
	private long anzahlMessungen = 0;
	private int historyLength = 1000;
	// #########################################################
	// Visualisierung:
	// #########################################################
	private Messwertfenster messwertfensterfenster = new Messwertfenster();
	private Visualisierungsfenster visualisierungsfenster = new Visualisierungsfenster();
	// Motorendatensets
	private Dataset motor1Dataset = new Dataset("Motor 1", Color.BLUE, 2, historyLength);
	private Dataset motor2Dataset = new Dataset("Motor 2", Color.GREEN, 2, historyLength);
	private Dataset motor3Dataset = new Dataset("Motor 3", Color.RED, 2, historyLength);
	private Dataset motor4Dataset = new Dataset("Motor 4", Color.YELLOW, 2, historyLength);
	// Winkeldatensets
	private Dataset winkelXDataset = new Dataset("Winkel X", Color.GREEN, 2, historyLength);
	private Dataset winkelYDataset = new Dataset("Winkel Y", Color.RED, 2, historyLength);
	private Dataset winkelZDataset = new Dataset("Winkel Z", Color.YELLOW, 2, historyLength);
	// Beschl. Datensets
	private Dataset accXDataset = new Dataset("Accelerometer X", Color.GREEN, 1, historyLength);
	private Dataset accYDataset = new Dataset("Accelerometer Y", Color.RED, 1, historyLength);
	private Dataset accZDataset = new Dataset("Accelerometer Z", Color.YELLOW, 1, historyLength);
	// Gierraten Datensets
	private Dataset rateXDataset = new Dataset("Rate Pitch", Color.GREEN, 1, historyLength);
	private Dataset rateYDataset = new Dataset("Rate Roll", Color.RED, 1, historyLength);
	private Dataset rateZDataset = new Dataset("Rate Yaw", Color.YELLOW, 1, historyLength);
	
	/* RC Receiver */ 
	private Dataset rcSignalRollDataset = new Dataset("RC: Roll", Color.GREEN, 1, historyLength);
	private Dataset rcSignalNickDataset = new Dataset("RC: Nick", Color.RED, 1, historyLength);
	private Dataset rcSignalYawDataset = new Dataset("RC: Yaw", Color.YELLOW, 1, historyLength);	
	private Dataset rcSignalThrottleDataset = new Dataset("RC: Throttle", Color.GREEN, 1, historyLength);
	private Dataset rcSignalLinPotiDataset = new Dataset("RC: Lin Poti", Color.RED, 1, historyLength);
	private Dataset rcSignalSwitchDataset = new Dataset("RC: Switch", Color.YELLOW, 1, historyLength);
	private Dataset rcSignalEnableDataset = new Dataset("RC: Motor enable", Color.YELLOW, 1, historyLength);
	
	/* height*/
	private Dataset heightDataset = new Dataset("Height", Color.GREEN, 1, historyLength);
	private Dataset relHeightDataset = new Dataset("rel Height", Color.RED, 1, historyLength);
	private Dataset dHDataset = new Dataset("dH", Color.YELLOW, 1, historyLength);
	
	/* PID*/
	private Dataset pidXOutDataset = new Dataset("PID X", Color.GREEN, 1, historyLength);
	private Dataset pidYOutDataset = new Dataset("PID Y", Color.RED, 1, historyLength);
	private Dataset pidZOutDataset = new Dataset("PID Z", Color.YELLOW, 1, historyLength);
	
	/* temp */
	private Dataset tempDataset = new Dataset("Temperature", Color.YELLOW, 1, historyLength);
	
	/*akku Voltage */
	private Dataset akkuVoltageDataset = new Dataset("Akku Voltage", Color.YELLOW, 1, historyLength);
	
	private boolean wasVisible = false;
    // #########################################################
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
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
		
		// VISUALISIERUNG INITIALISIEREN
		visualisierungsfenster.motorsGraph.setRasterLineCountY(5);
		visualisierungsfenster.motorsGraph.setRastertYColor(Color.black);
		visualisierungsfenster.motorsGraph.setUseRasterY(true);
		visualisierungsfenster.motorsGraph.addGraph( motor1Dataset );
		visualisierungsfenster.motorsGraph.addGraph( motor2Dataset );
		visualisierungsfenster.motorsGraph.addGraph( motor3Dataset );
		visualisierungsfenster.motorsGraph.addGraph( motor4Dataset );
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 396, 240);
		frame.getContentPane().add(tabbedPane);
		
		JLabel statuslabel = new JLabel("");
		statuslabel.setBounds(10, 262, 397, 14);
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
		
		JButton btnNewButton_1 = new JButton("Messwerte anzeigen");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				messwertfensterfenster.setVisible( true );
			}
		});
		btnNewButton_1.setBounds(10, 45, 371, 23);
		panel_1.add(btnNewButton_1);
		
		JButton btnNewButton_3 = new JButton("Visualisierung anzeigen");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visualisierungsfenster.setVisible( true );
				if( !wasVisible )
				{
					wasVisible = true;
					visualisierungsfenster.motorsGraph.setBufferStrategy();
				}
			}
		});
		btnNewButton_3.setBounds(10, 79, 371, 23);
		panel_1.add(btnNewButton_3);		

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Quadrokopter konfigurieren", null, panel_2, null);
		panel_2.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
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
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
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
				btnMessungStarten.setText("Messung starten");
				// Falls Port offen ist Konfiguration anfordern
				while( protocolStatus != 0 ) {
					// Abwarten, bis Portzustand: idle
				}
				
				try {
					port.writeByte( USB_CMD_GET_CONFIG );
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
						 * 24 - 	: Complementary Filter XY
						 * 28 - 	: Complementary Filter Z
						 * ...
						 */
						byte[] byteArray = port.readBytes( CONFIGURATION_FRAME_LENGTH );
						spinner_pxy.setValue( byteArrayToFloat(byteArray, 0) );
						spinner_ixy.setValue( byteArrayToFloat(byteArray, 4) );
						spinner_dxy.setValue( byteArrayToFloat(byteArray, 8) );
						spinner_pz.setValue( byteArrayToFloat(byteArray, 12) );
						spinner_iz.setValue( byteArrayToFloat(byteArray, 16) );
						spinner_dz.setValue( byteArrayToFloat(byteArray, 20) );
//						spinner_cxy.setValue( byteArrayToFloat(byteArray, 24) );
//						spinner_cz.setValue( byteArrayToFloat(byteArray, 28) );
				
						protocolStatus = 0;
						statuslabel.setText("Konfiguration empfangen");
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		btnWerteLaden.setBounds(5, 178, 155, 23);
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
						//Protokollstatus: sendConfiguration
						protocolStatus = 3;
						/* Konfigurationsframe:
						 * Byte
						 * 0 - 3	: P-Anteil XY (float)
						 * 4 - 7	: I-Anteil XY (float)
						 * 8 - 11	: Z-Anteil XY (float)
						 * 12 - 15	: P-Anteil Z (float)
						 * 16 - 19	: I-Anteil Z (float)
						 * 20 - 23	: D-Anteil Z (float)
						 * 24 - 	: Complementary Filter XY
						 * 28 - 	: Complementary Filter Z
						 * ...
						 */
						byte[] outbuffer = new byte[CONFIGURATION_FRAME_LENGTH + 1];
						outbuffer[1] = USB_CMD_UPDATE_CONFIG;
						outbuffer = floatToByteArray( (float)spinner_pxy.getValue(), outbuffer, 1 );
						outbuffer = floatToByteArray( (float)spinner_ixy.getValue(), outbuffer, 5 );
						outbuffer = floatToByteArray( (float)spinner_dxy.getValue(), outbuffer, 9 );
						outbuffer = floatToByteArray( (float)spinner_pz.getValue(), outbuffer, 13 );
						outbuffer = floatToByteArray( (float)spinner_iz.getValue(), outbuffer, 17 );
						outbuffer = floatToByteArray( (float)spinner_dz.getValue(), outbuffer, 21 );
//						outbuffer = floatToByteArray( (float)spinner_cxy.getValue(), outbuffer, 25 );
//						outbuffer = floatToByteArray( (float)spinner_cz.getValue(), outbuffer, 29 );
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
		button.setBounds(226, 178, 155, 23);
		panel_2.add(button);
	
		
		// Timerkonfiguration
		messtimer = new Timer();
		messtimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {

				if( visualisierungsfenster.isVisible() ) {
					float[] update = new float[13];
					Random ran = new Random();
					for(int i = 0; i < 13; i++) {
						update[i] = 50.0f * (float)(Math.sin(0.05 * ( anzahlMessungen + i * 100 ))) + 50.0f;
					}
					visualisierungsfenster.motorsGraph.update(update);
					messwertfensterfenster.setLabelText(update);
				}
				
				if(messungAktiv && port.isOpened() ) {
					
					try {
						// Daten anfordern
						port.writeByte(USB_CMD_SEND_STATUS_FLOAT);
						
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
							if( port.getInputBufferBytesCount() == MEASUREMENT_FRAME_LENGTH ) {
								byte[] temp = port.readBytes();
								
								for(int i = 0; i < messdaten.length; i++) {
									messdaten[i] = byteArrayToFloat(temp, i * 4);
									
								}
								messwertfensterfenster.setLabelText(messdaten);
								anzahlMessungen++;
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
