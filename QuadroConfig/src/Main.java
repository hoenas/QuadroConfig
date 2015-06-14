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
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSeparator;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.border.LineBorder;

import LiveGraph.Dataset;

import java.awt.Color;


public class Main {

	private SerialPort port;
	private JFrame frame;
	private Timer messtimer;
	private JPanel tabPort;
	private JPanel tabMessung;
	private JPanel tabKonfiguration;
	private JButton btnMessungStarten;
	private JButton btnOpenCfg;
	private int messintervall = 24;
	private boolean messungAktiv = false;
	FlagsToolBar flagsToolBar;
	// Protokoll
	private static int protocolStatus = 0;
	/* Werte protocolStatus:
	 * 0: idle
	 * 1: receiveFrame
	 * 2: receiveConfiguration
	 * 3: sendConfiguration
	 */
	
	/* get status and global flags*/
	public static byte USB_CMD_SEND_STATUS_FLOAT = (byte)0x03;
	public static byte USB_CMD_GLOBAL_FLAGS = (byte)0x04;
	
	/* configuration*/
	public static byte USB_CMD_CONFIG_MODE = (byte)0xC0;
	
	public static byte USB_CMD_GET_CONFIG = (byte)0xC1;
	public static byte USB_CMD_UPDATE_CONFIG = (byte)0xC2;
	public static byte USB_CMD_SAVE_CONFIG = (byte)0xCE;
	public static byte USB_CMD_RESTORE_CONFIG = (byte)0xCF;

	/* eeprom acces */
	public static byte USB_CMD_READ_BYTE = (byte)0xC3;
	public static byte USB_CMD_READ_2BYTES = (byte)0xC4;
	public static byte USB_CMD_READ_4BYTES = (byte)0xC5;

	public static byte USB_CMD_WRITE_BYTE = (byte)0xC6;
	public static byte USB_CMD_WRITE_2BYTES = (byte)0xC7;
	public static byte USB_CMD_WRITE_4BYTES = (byte)0xC8;	
	
	/* reset */
	public static byte USB_CMD_RESET = (byte)0xFF;	
	
	
	private static int MEASUREMENT_FRAME_LENGTH = 128;
	public float[] messdaten = new float[MEASUREMENT_FRAME_LENGTH/4];
	private static int CONFIGURATION_FRAME_LENGTH = 32;	
	// Timeout in ms
	private static long COMMUNICATION_TIMEOUT = 100;
	private long anzahlMessungen = 0;
	private int historyLength = 300;
	// #########################################################
	// Visualisierung:
	// #########################################################
	private Messwertfenster messwertfenster = new Messwertfenster();
	private Visualisierungsfenster visualisierungsfenster = new Visualisierungsfenster();
	private ConfigWindow cfgWindow = new ConfigWindow();

	
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
    // #########################################################
	
	
	// Bildschirmaufl�sung
	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
					window.frame.setLocation(0, 0);
					// Testframe um Komponenten zu testen
					//TestFrame myFrame = new TestFrame();
					//myFrame.setVisible(true);
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
	
	public static byte [] float2ByteArray (float value)
	{  
	     return ByteBuffer.allocate(4).putFloat(value).array();
	}
	
	public byte[] floatToByteArray( float f, byte[] byteArray, int offset) {
//		int integer = Float.floatToIntBits( f );
//		byteArray[0 + offset] = (byte)(integer>>>24);
//		byteArray[1 + offset] = (byte)(integer>>>16);
//		byteArray[2 + offset] = (byte)(integer>>>8);
//		byteArray[3 + offset] = (byte)(integer>>>0);

		byte tmp[] = float2ByteArray(f);
		byteArray[offset] = tmp[3];
		byteArray[offset + 1] = tmp[2];
		byteArray[offset + 2] = tmp[1];
		byteArray[offset + 3] = tmp[0];
		return byteArray;
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 433, 377);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// VISUALISIERUNG INITIALISIEREN
		visualisierungsfenster.accGraph.addGraph( accXDataset );
		visualisierungsfenster.accGraph.addGraph( accYDataset );
		visualisierungsfenster.accGraph.addGraph( accZDataset );
		visualisierungsfenster.gyroGraph.addGraph( rateXDataset );
		visualisierungsfenster.gyroGraph.addGraph( rateYDataset );
		visualisierungsfenster.gyroGraph.addGraph( rateZDataset );
		visualisierungsfenster.motorGraph.addGraph( motor4Dataset );
		visualisierungsfenster.motorGraph.addGraph( motor1Dataset );
		visualisierungsfenster.motorGraph.addGraph( motor2Dataset );
		visualisierungsfenster.motorGraph.addGraph( motor3Dataset );
		visualisierungsfenster.pidGraph.addGraph(pidXOutDataset);
		visualisierungsfenster.pidGraph.addGraph(pidYOutDataset);
		visualisierungsfenster.pidGraph.addGraph(pidZOutDataset);
		visualisierungsfenster.rcGraph.addGraph( rcSignalRollDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalNickDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalYawDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalThrottleDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalEnableDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalLinPotiDataset );
		visualisierungsfenster.rcGraph.addGraph( rcSignalSwitchDataset );
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setBounds(10, 36, 396, 240);
		frame.getContentPane().add(tabs);
		
		JLabel statuslabel = new JLabel("idle");
		statuslabel.setBounds(69, 11, 338, 14);
		frame.getContentPane().add(statuslabel);
		
		tabPort = new JPanel();
		tabs.addTab("Port", null, tabPort, null);
		tabPort.setLayout(null);
		
		JLabel lblPortname = new JLabel("Port Name:");
		lblPortname.setBounds(10, 11, 74, 14);
		tabPort.add(lblPortname);
		
		JLabel lblBaudrate = new JLabel("Baudrate:");
		lblBaudrate.setBounds(10, 39, 74, 14);
		tabPort.add(lblBaudrate);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(94, 8, 109, 20);
		String[] portNames = SerialPortList.getPortNames();
		for(int i = 0; i < portNames.length; i++){
            comboBox.addItem(portNames[i]);
        }
		tabPort.add(comboBox);
		
		
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
		tabPort.add(comboBox_1);
		
		JLabel lblParitt = new JLabel("Parity:");
		lblParitt.setBounds(10, 126, 74, 14);
		tabPort.add(lblParitt);
		
		JComboBox comboBox_2 = new JComboBox();
		tabPort.add(comboBox_2);
		
		JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setBounds(94, 64, 109, 20);
		comboBox_3.addItem(SerialPort.DATABITS_5);
		comboBox_3.addItem(SerialPort.DATABITS_6);
		comboBox_3.addItem(SerialPort.DATABITS_7);
		comboBox_3.addItem(SerialPort.DATABITS_8);
		comboBox_3.setSelectedIndex(3);
		tabPort.add(comboBox_3);
		
		JLabel lblDatenbits = new JLabel("Databits:");
		lblDatenbits.setBounds(10, 67, 74, 14);
		tabPort.add(lblDatenbits);
		
		JLabel lblStoppbits = new JLabel("Stopbits:");
		lblStoppbits.setBounds(10, 98, 74, 14);
		tabPort.add(lblStoppbits);
		
		JComboBox comboBox_4 = new JComboBox();
		comboBox_4.setBounds(94, 95, 109, 20);
		comboBox_4.addItem(SerialPort.STOPBITS_1);
		comboBox_4.addItem(SerialPort.STOPBITS_2);
		comboBox_4.addItem(SerialPort.STOPBITS_1_5);
		comboBox_4.setSelectedIndex(0);
		tabPort.add(comboBox_4);
		
		JComboBox comboBox_5 = new JComboBox();
		comboBox_5.setBounds(94, 123, 109, 20);
		comboBox_5.addItem("none");
		comboBox_5.addItem("even");
		comboBox_5.addItem("odd");
		comboBox_5.setSelectedIndex(2);
		tabPort.add(comboBox_5);
		
		JButton btnNewButton = new JButton("Open Port");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if( port == null || !port.isOpened() ) {
						port = new SerialPort( comboBox.getSelectedItem().toString() );
						int parity = 0;
						String auswahl = comboBox_5.getSelectedItem().toString();
						
						if( auswahl == "even" ) {
							parity = SerialPort.PARITY_EVEN;
						} else if ( auswahl == "odd" ) {
							parity = SerialPort.PARITY_ODD;
						} else {
							parity = SerialPort.PARITY_NONE;
						}

						port.openPort();	
						port.setParams(Integer.valueOf(comboBox_1.getSelectedItem().toString()), Integer.valueOf(comboBox_3.getSelectedItem().toString()), Integer.valueOf(comboBox_4.getSelectedItem().toString()), parity);
						messungAktiv = true;
						messtimer.start();
						btnMessungStarten.setText( "Stop Monitoring" );
						
						comboBox.setEnabled(false);
						comboBox_1.setEnabled(false);
						comboBox_3.setEnabled(false);
						comboBox_4.setEnabled(false);
						comboBox_5.setEnabled(false);
						statuslabel.setText("Port opened");
						btnNewButton.setText("Close Port");
						btnOpenCfg.setEnabled(true);
						
					}
					else
					{
						port.closePort();
						messungAktiv = false;
						messtimer.stop();
						btnMessungStarten.setText( "Start Monitoring" );
						comboBox.setEnabled(true);
						comboBox_1.setEnabled(true);
						comboBox_3.setEnabled(true);
						comboBox_4.setEnabled(true);
						comboBox_5.setEnabled(true);
						statuslabel.setText("Port closed");
						btnNewButton.setText("Open Port");
						btnOpenCfg.setEnabled(false);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
		btnNewButton.setBounds(10, 151, 193, 23);
		tabPort.add(btnNewButton);
		
		JButton btnAktualisieren = new JButton("refresh");
		btnAktualisieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comboBox.removeAllItems();
				String[] portNames = SerialPortList.getPortNames();
				for(int i = 0; i < portNames.length; i++){
		            comboBox.addItem(portNames[i]);
		        }
			}
		});
		btnAktualisieren.setBounds(213, 7, 168, 23);
		tabPort.add(btnAktualisieren);
		
		btnOpenCfg = new JButton("open cfg");
		btnOpenCfg.setEnabled(false);
		btnOpenCfg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if( port != null && port.isOpened() ) {
					// Keine neuen Messungen anfordern
					messungAktiv = false;
					// Falls Port offen ist Konfiguration senden
					while( protocolStatus != 0 ) {
						// Abwarten, bis Portzustand: idle
					}
				
					cfgWindow.openConfigWindow(port,statuslabel,flagsToolBar.isConfigMode);
				
					
				}
				
			}
		});
		btnOpenCfg.setBounds(264, 150, 117, 25);
		tabPort.add(btnOpenCfg);

				cfgWindow.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				messungAktiv = true;
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
		
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
	
		});
			
		
		
		tabMessung = new JPanel();
		tabs.addTab("Monitoring", null, tabMessung, null);
		tabs.setEnabledAt(1, true);
		tabMessung.setLayout(null);
		
		btnMessungStarten = new JButton("Start Monitoring");
		btnMessungStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( !messungAktiv && port != null && port.isOpened() ) {
					messtimer.start();
					btnMessungStarten.setText("Stop Monitoring");
					statuslabel.setText("Resumed monitoring...");
					
				} else {
					messtimer.stop();
					btnMessungStarten.setText("Start Monitoring");
					statuslabel.setText("Stopped monitoring.");
				}
				messungAktiv = !messungAktiv;
			}	
		});
		btnMessungStarten.setBounds(10, 11, 178, 23);
		tabMessung.add(btnMessungStarten);
		
		JButton btnNewButton_1 = new JButton("View Monitoring Values");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				messwertfenster.setVisible(true);
				messwertfenster.setLocation(0, frame.getHeight());
				messwertfenster.setSize( messwertfenster.getSize().width, screen.height - frame.getHeight() - 40); 
			}
		});
		btnNewButton_1.setBounds(10, 45, 371, 23);
		tabMessung.add(btnNewButton_1);
		
		JButton btnNewButton_3 = new JButton("View Monitoring Visualisation");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visualisierungsfenster.setVisible(true);
				visualisierungsfenster.setLocation( frame.getWidth(), 0);
				visualisierungsfenster.setSize( screen.width - frame.getWidth(), screen.height - 40);
			}
		});
		btnNewButton_3.setBounds(10, 79, 371, 23);
		tabMessung.add(btnNewButton_3);		
		
		JLabel lblIntervallms = new JLabel("Interval (ms):");
		lblIntervallms.setBounds(198, 15, 87, 14);
		tabMessung.add(lblIntervallms);
		
		JSpinner spinner = new JSpinner();
		
		spinner.setModel(new SpinnerNumberModel(24, 10, 5000, 1));
		spinner.setBounds(295, 12, 86, 20);
		tabMessung.add(spinner);

		
		
		flagsToolBar = new FlagsToolBar();
		flagsToolBar.setBounds(10, 287, 396, 41);
		frame.getContentPane().add(flagsToolBar);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(13, 11, 46, 14);
		frame.getContentPane().add(lblStatus);
	
		
		// Timerkonfiguration
		messtimer = new Timer(messintervall, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Timer wurde ausgelöst
				// Timer neu einstellen
				messtimer.setDelay( (int)spinner.getValue() );
				
				if( messungAktiv && port.isOpened() ) {
					
					try {
						// Daten anfordern
						port.writeByte(USB_CMD_SEND_STATUS_FLOAT);
						
						// Warten auf Daten
						boolean timeout = false;
						LocalTime begin = LocalTime.now();
						while( port.getInputBufferBytesCount() != MEASUREMENT_FRAME_LENGTH ) {
							// warte bis alle Bytes da sind
							// auf Timeout prÃ¼fen
							if( LocalTime.now().isAfter( begin.plus(COMMUNICATION_TIMEOUT, ChronoUnit.MILLIS) ) ) {
								timeout = true;
								protocolStatus = 0;
								statuslabel.setText("Communication timeout...");
								break;
							}
						}
						
						if(!timeout) {
							// Daten auswerten
							if( port.getInputBufferBytesCount() == MEASUREMENT_FRAME_LENGTH ) {
								byte[] temp = port.readBytes();
								
								for(int i = 0; i < messdaten.length; i++) {
									messdaten[i] = ByteBuffer.wrap(temp, i *4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
								}
								messwertfenster.setLabelText(messdaten);
								statuslabel.setText("monitoring...");
								if( visualisierungsfenster.isVisible() ) {
									// Accelerometerwerte aktualsierien
									float[] accs = new float[3];
									accs[0] = messdaten[0];
									accs[1] = messdaten[1];
									accs[2] = messdaten[2];
									visualisierungsfenster.accGraph.update( accs );
									// Gyrowerte aktualisieren
									float[] gyros = new float[3];
									gyros[0] = messdaten[3] / 10;
									gyros[1] = messdaten[4] / 10;
									gyros[2] = messdaten[5] / 10;
									visualisierungsfenster.gyroGraph.update( gyros );
									// Motorenwerte aktualisieren
									float[] motoren = new float[4];
									motoren[0] = messdaten[19] * 100;
									motoren[1] = messdaten[20] * 100;
									motoren[2] = messdaten[21] * 100;
									motoren[3] = messdaten[22] * 100;
									visualisierungsfenster.motorGraph.update( motoren );
									// Winkel aktualisieren
									visualisierungsfenster.horizonX.update( (int)messdaten[9] );
									visualisierungsfenster.horizonY.update( (int)messdaten[10] );
									visualisierungsfenster.horizonZ.update( (int)messdaten[11] );
									// PID-Outputs aktualisieren
									float[] pid  = new float[3];
									pid[0] = messdaten[28]*1000;
									pid[1] = messdaten[29]*1000;
									pid[2] = messdaten[30]*1000;
									visualisierungsfenster.pidGraph.update( pid );
									// Fernsteuerungswerte aktualisieren
									float[] rcs = new float[7];
									rcs[0] = messdaten[12] * 100 + 50;
									rcs[1] = messdaten[13] * 100 + 50;
									rcs[2] = messdaten[14] * 100 + 50;
									rcs[3] = messdaten[15] * 100;
									rcs[4] = messdaten[16] * 100;
									rcs[5] = messdaten[17] * 100;
									rcs[6] = messdaten[18] * 100;
									visualisierungsfenster.rcGraph.update( rcs );
									// CPU-Load aktualisieren
									visualisierungsfenster.cpuAltimeter.update( (int)(messdaten[31] * 100));
									// Hoehe aktualisieren
									visualisierungsfenster.altAltimeter.update( (int)messdaten[25] );
									// Spannung aktualisieren
									visualisierungsfenster.voltAltimeter.update( (int)(messdaten[27]*1000) );
									// Temperatur aktualisieren
									visualisierungsfenster.tempAltimeter.update( (int)messdaten[23] );
								}
								anzahlMessungen++;
							}
						} else {
							// Dummy read
							port.readBytes();
						}
					} catch( Exception ex ) {
						System.out.println( ex.getMessage() );
					}
					
					try {
						// get global flags
						port.writeByte(USB_CMD_GLOBAL_FLAGS);
						//Warten auf Daten
						boolean timeout = false;
						LocalTime begin = LocalTime.now();
						while( port.getInputBufferBytesCount() != 4 ) {
							// warte bis alle Bytes da sind
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
								flagsToolBar.update(temp);
								statuslabel.setText("monitoring...");
						} else {
							// Dummy read
							port.readBytes();
						}
					} catch( Exception ex ) {
						System.out.println( ex.getMessage() );
					}
					
				}
			}
		});
		
		// GUI anzeigen
		messwertfenster.setVisible(true);
		messwertfenster.setLocation(0, frame.getHeight());
		messwertfenster.setSize( messwertfenster.getSize().width, screen.height - frame.getHeight() - 40); 
		visualisierungsfenster.setVisible(true);
		visualisierungsfenster.setLocation( frame.getWidth(), 0);
		visualisierungsfenster.setSize( screen.width - frame.getWidth(), screen.height - 40);
	}
}

