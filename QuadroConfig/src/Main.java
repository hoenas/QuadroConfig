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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import LiveGraph.Dataset;

import java.awt.Color;

public class Main {

	private SerialPort port;
	private QuadrocopterCommunicator quadrocopter;
	private JFrame frame;
	private Timer messtimer;
	private JLabel statuslabel;
	private JPanel tabPort;
	private JPanel tabMessung;
	private JButton btnMessungStarten;
	private int messintervall = 24;
	private boolean messungAktiv = false;
	FlagsToolBar flagsToolBar;
	// Protokoll
	private static int protocolStatus = 0;
	/*
	 * Werte protocolStatus: 0: idle 1: receiveFrame 2: receiveConfiguration 3:
	 * sendConfiguration
	 */

	/* get status and global flags */
	public static byte USB_CMD_SEND_STATUS_FLOAT = (byte) 0x03;
	public static byte USB_CMD_GLOBAL_FLAGS = (byte) 0x04;

	/* configuration */
	public static byte USB_CMD_CONFIG_MODE = (byte) 0xC0;

	public static byte USB_CMD_GET_CONFIG = (byte) 0xC1;
	public static byte USB_CMD_UPDATE_CONFIG = (byte) 0xC2;
	public static byte USB_CMD_SAVE_CONFIG = (byte) 0xCE;
	public static byte USB_CMD_RESTORE_CONFIG = (byte) 0xCF;

	/* eeprom acces */
	public static byte USB_CMD_READ_BYTE = (byte) 0xC3;
	public static byte USB_CMD_READ_2BYTES = (byte) 0xC4;
	public static byte USB_CMD_READ_4BYTES = (byte) 0xC5;

	public static byte USB_CMD_WRITE_BYTE = (byte) 0xC6;
	public static byte USB_CMD_WRITE_2BYTES = (byte) 0xC7;
	public static byte USB_CMD_WRITE_4BYTES = (byte) 0xC8;

	/* reset */
	public static byte USB_CMD_RESET = (byte) 0xFF;

	private static int MEASUREMENT_FRAME_LENGTH = 128;
	public float[] messdaten = new float[MEASUREMENT_FRAME_LENGTH / 4];
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
	private JButton btnConfigurateQuadrocopter = new JButton(
			"Configurate Quadrocopter");

	// Motorendatensets
	private Dataset motor1Dataset = new Dataset("Motor 1", Color.BLUE, 2,
			historyLength);
	private Dataset motor2Dataset = new Dataset("Motor 2", Color.GREEN, 2,
			historyLength);
	private Dataset motor3Dataset = new Dataset("Motor 3", Color.RED, 2,
			historyLength);
	private Dataset motor4Dataset = new Dataset("Motor 4", Color.YELLOW, 2,
			historyLength);
	// Winkeldatensets
	private Dataset winkelXDataset = new Dataset("Winkel X", Color.GREEN, 2,
			historyLength);
	private Dataset winkelYDataset = new Dataset("Winkel Y", Color.RED, 2,
			historyLength);
	private Dataset winkelZDataset = new Dataset("Winkel Z", Color.YELLOW, 2,
			historyLength);
	// Winkelsetpointsdatensets
	private Dataset winkelSPXDataset = new Dataset("Winkel SP X", Color.GREEN, 2,
			historyLength);
	private Dataset winkelSPYDataset = new Dataset("Winkel SP Y", Color.RED, 2,
			historyLength);
	private Dataset winkelSPZDataset = new Dataset("Winkel SP Z", Color.YELLOW, 2,
			historyLength);
	// Beschl. Datensets
	private Dataset accXDataset = new Dataset("Accelerometer X", Color.GREEN,
			1, historyLength);
	private Dataset accYDataset = new Dataset("Accelerometer Y", Color.RED, 1,
			historyLength);
	private Dataset accZDataset = new Dataset("Accelerometer Z", Color.YELLOW,
			1, historyLength);
	// Gierraten Datensets
	private Dataset rateXDataset = new Dataset("Rate Pitch", Color.GREEN, 1,
			historyLength);
	private Dataset rateYDataset = new Dataset("Rate Roll", Color.RED, 1,
			historyLength);
	private Dataset rateZDataset = new Dataset("Rate Yaw", Color.YELLOW, 1,
			historyLength);

	/* RC Receiver */
	private Dataset rcSignalRollDataset = new Dataset("RC: Roll", Color.GREEN,
			1, historyLength);
	private Dataset rcSignalNickDataset = new Dataset("RC: Nick", Color.RED, 1,
			historyLength);
	private Dataset rcSignalYawDataset = new Dataset("RC: Yaw", Color.YELLOW,
			1, historyLength);
	private Dataset rcSignalThrottleDataset = new Dataset("RC: Throttle",
			Color.GREEN, 1, historyLength);
	private Dataset rcSignalLinPotiDataset = new Dataset("RC: Lin Poti",
			Color.RED, 1, historyLength);
	private Dataset rcSignalSwitchDataset = new Dataset("RC: Switch",
			Color.YELLOW, 1, historyLength);
	private Dataset rcSignalEnableDataset = new Dataset("RC: Motor enable",
			Color.YELLOW, 1, historyLength);

	/* height */
	private Dataset heightDataset = new Dataset("Height", Color.GREEN, 1,
			historyLength);
	private Dataset relHeightDataset = new Dataset("rel Height", Color.RED, 1,
			historyLength);
	private Dataset dHDataset = new Dataset("dH", Color.YELLOW, 1,
			historyLength);

	/* PID */
	private Dataset pidXOutDataset = new Dataset("PID X", Color.GREEN, 1,
			historyLength);
	private Dataset pidYOutDataset = new Dataset("PID Y", Color.RED, 1,
			historyLength);
	private Dataset pidZOutDataset = new Dataset("PID Z", Color.YELLOW, 1,
			historyLength);
	
	/* magnetometer */
	private Dataset magnetometerXDataset = new Dataset("Magnetometer X", Color.GREEN, 1, historyLength);
	private Dataset magnetometerYDataset = new Dataset("Magnetometer X", Color.GREEN, 1, historyLength);
	private Dataset magnetometerZDataset = new Dataset("Magnetometer X", Color.GREEN, 1, historyLength);

	/* temp */
	private Dataset tempDataset = new Dataset("Temperature", Color.YELLOW, 1,
			historyLength);

	/* akku Voltage */
	private Dataset akkuVoltageDataset = new Dataset("Akku Voltage",
			Color.YELLOW, 1, historyLength);
	
	private Dataset cpuDataset = new Dataset("CPU Load",
			Color.GREEN, 1, historyLength);
	
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
					// TestFrame myFrame = new TestFrame();
					// myFrame.setVisible(true);
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
		frame.setBounds(100, 100, 433, 377);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// VISUALISIERUNG INITIALISIEREN
		visualisierungsfenster.accGraph.addGraph(accXDataset);
		visualisierungsfenster.accGraph.addGraph(accYDataset);
		visualisierungsfenster.accGraph.addGraph(accZDataset);
		visualisierungsfenster.gyroGraph.addGraph(rateXDataset);
		visualisierungsfenster.gyroGraph.addGraph(rateYDataset);
		visualisierungsfenster.gyroGraph.addGraph(rateZDataset);
		visualisierungsfenster.motorGraph.addGraph(motor4Dataset);
		visualisierungsfenster.motorGraph.addGraph(motor1Dataset);
		visualisierungsfenster.motorGraph.addGraph(motor2Dataset);
		visualisierungsfenster.motorGraph.addGraph(motor3Dataset);
		visualisierungsfenster.pidGraph.addGraph(pidXOutDataset);
		visualisierungsfenster.pidGraph.addGraph(pidYOutDataset);
		visualisierungsfenster.pidGraph.addGraph(pidZOutDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalRollDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalNickDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalYawDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalThrottleDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalEnableDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalLinPotiDataset);
		visualisierungsfenster.rcGraph.addGraph(rcSignalSwitchDataset);

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setBounds(10, 36, 396, 240);
		frame.getContentPane().add(tabs);

		statuslabel = new JLabel("idle");
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
		for (int i = 0; i < portNames.length; i++) {
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
					if (port == null || !port.isOpened()) {
						port = new SerialPort(comboBox.getSelectedItem()
								.toString());
						int parity = 0;
						String auswahl = comboBox_5.getSelectedItem()
								.toString();

						if (auswahl == "even") {
							parity = SerialPort.PARITY_EVEN;
						} else if (auswahl == "odd") {
							parity = SerialPort.PARITY_ODD;
						} else {
							parity = SerialPort.PARITY_NONE;
						}

						port.openPort();
						port.setParams(Integer.valueOf(comboBox_1
								.getSelectedItem().toString()), Integer
								.valueOf(comboBox_3.getSelectedItem()
										.toString()), Integer
								.valueOf(comboBox_4.getSelectedItem()
										.toString()), parity);
						quadrocopter = new QuadrocopterCommunicator(60);
						quadrocopter.setPort(port);
						messungAktiv = true;
						messtimer.start();
						btnMessungStarten.setText("Stop Monitoring");

						comboBox.setEnabled(false);
						comboBox_1.setEnabled(false);
						comboBox_3.setEnabled(false);
						comboBox_4.setEnabled(false);
						comboBox_5.setEnabled(false);
						statuslabel.setText("Port opened");
						btnNewButton.setText("Close Port");
						btnConfigurateQuadrocopter.setEnabled(true);
						
						frame.setState(Frame.ICONIFIED);

					} else {
						port.closePort();
						quadrocopter = null;
						messungAktiv = false;
						messtimer.stop();
						btnMessungStarten.setText("Start Monitoring");
						comboBox.setEnabled(true);
						comboBox_1.setEnabled(true);
						comboBox_3.setEnabled(true);
						comboBox_4.setEnabled(true);
						comboBox_5.setEnabled(true);
						statuslabel.setText("Port closed");
						btnNewButton.setText("Open Port");
						btnConfigurateQuadrocopter.setEnabled(false);
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
				for (int i = 0; i < portNames.length; i++) {
					comboBox.addItem(portNames[i]);
				}
			}
		});
		btnAktualisieren.setBounds(213, 7, 168, 23);
		tabPort.add(btnAktualisieren);

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
				if (!messungAktiv && port != null && port.isOpened()) {
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
				messwertfenster.setSize(messwertfenster.getSize().width,
						screen.height - frame.getHeight() - 40);
			}
		});
		btnNewButton_1.setBounds(10, 45, 371, 23);
		tabMessung.add(btnNewButton_1);

		JButton btnNewButton_3 = new JButton("View Monitoring Visualisation");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				visualisierungsfenster.setVisible(true);
				visualisierungsfenster.setLocation(frame.getWidth(), 0);
				visualisierungsfenster.setSize(screen.width - frame.getWidth(),
						screen.height - 40);
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

		btnConfigurateQuadrocopter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (port != null && port.isOpened()) {
					// Keine neuen Messungen anfordern
					messungAktiv = false;
					// Falls Port offen ist Konfiguration senden
					while (protocolStatus != 0) {
						// Abwarten, bis Portzustand: idle
					}

					cfgWindow.openConfigWindow(port, statuslabel,
							flagsToolBar.isConfigMode);

				}

			}
		});
		btnConfigurateQuadrocopter.setEnabled(false);
		btnConfigurateQuadrocopter.setBounds(10, 139, 369, 25);
		tabMessung.add(btnConfigurateQuadrocopter);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (port != null && port.isOpened()) {
					// Keine neuen Messungen anfordern
					messungAktiv = false;
					// Falls Port offen ist Konfiguration senden
					while (protocolStatus != 0) {
						// Abwarten, bis Portzustand: idle
					}

					try {
						byte[] outbuffer = new byte[1];
						outbuffer[0] = USB_CMD_RESET;
						port.writeBytes(outbuffer);
						statuslabel.setText("Reset");

						// dummy read
						while (port.getInputBufferBytesCount() != 1)
							;
						byte[] dummy = port.readBytes();

					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		btnReset.setBounds(10, 176, 371, 25);
		tabMessung.add(btnReset);

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
				messtimer.setDelay((int) spinner.getValue());
				
				if (messungAktiv && port.isOpened()) {
					// updateSensorGraphs();
					getFrame();					
				}
			}
		});

		// GUI anzeigen
		messwertfenster.setVisible(true);
		messwertfenster.setLocation(0, 0);
		messwertfenster.setSize(frame.getWidth(), screen.height);
		//messwertfenster.setState(Frame.MAXIMIZED_VERT);
		visualisierungsfenster.setVisible(true);
		visualisierungsfenster.setLocation(frame.getWidth(), 0);
		visualisierungsfenster.setSize(screen.width - frame.getWidth(), screen.height);
	}
	
	public void getFrame() {
		int i = 0;
		
		if( messwertfenster.chckbxAccelerometer.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckbxGyro.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxMagnetometer.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxAngle.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxMotors.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxRC.isSelected() ) {
			i++;
		}
		
//		if( messwertfenster.chckBxPID.isSelected() ) {
//			i++;
//		}
		
		if( messwertfenster.chckBxHeight.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxTemp.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxBatt.isSelected() ) {
			i++;
		}
		
		if( messwertfenster.chckBxCPU.isSelected() ) {
			i++;
		}
		
		QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS[] customFrame = new QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS[i];
		
		i = 0;
		int responseLength = 0;
		
		if( messwertfenster.chckbxAccelerometer.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.ACCEL;
			i++;
		}
		
		if( messwertfenster.chckbxGyro.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.GYRO;
			i++;
		}
		
		if( messwertfenster.chckBxMagnetometer.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.MAGNETOMETER;
			i++;
		}
		
		if( messwertfenster.chckBxAngle.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.ANGLE;
			i++;
		}
		
		if( messwertfenster.chckBxMotors.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.MOTOR;
			i++;
		}
		
		if( messwertfenster.chckBxRC.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.RC;
			i++;
		}
		
//		if( messwertfenster.chckBxPID.isSelected() ) {
//			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.;
//			i++;
//		}
		
		if( messwertfenster.chckBxHeight.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.HEIGHT;
			i++;
		}
		
		if( messwertfenster.chckBxTemp.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.TEMP;
			i++;
		}
		
		if( messwertfenster.chckBxBatt.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.AKKU;
			i++;
		}
		
		if( messwertfenster.chckBxCPU.isSelected() ) {
			customFrame[i] = QuadrocopterCommunicator.CUSTOM_FRAME_IDENTIFIERS.CPU;
			i++;
		}
		
		// receive custom frame from quadrocopter
		quadrocopter.getCustomFrame(customFrame);
		
		// push new values in datasets
		
		// Accelerometer
		accXDataset.addValue( quadrocopter.accelX );
		accYDataset.addValue( quadrocopter.accelY );
		accZDataset.addValue( quadrocopter.accelZ );
		// Gyro
		rateXDataset.addValue( quadrocopter.gyroX );
		rateYDataset.addValue( quadrocopter.gyroY );
		rateZDataset.addValue( quadrocopter.gyroZ );
		// Magnetometer
		magnetometerXDataset.addValue( quadrocopter.magnX );
		magnetometerYDataset.addValue( quadrocopter.magnY );
		magnetometerZDataset.addValue( quadrocopter.magnZ );
		// Angles
		winkelXDataset.addValue( quadrocopter.angleX );
		winkelYDataset.addValue( quadrocopter.angleY );
		winkelZDataset.addValue( quadrocopter.angleZ );
		// AnglesSP
		winkelSPXDataset.addValue( quadrocopter.angleSPX );
		winkelSPYDataset.addValue( quadrocopter.angleSPY );
		winkelSPZDataset.addValue( quadrocopter.angleSPZ );
		// Height
		heightDataset.addValue( quadrocopter.height );
		dHDataset.addValue( quadrocopter.heightDelta );
		relHeightDataset.addValue( quadrocopter.heightRel );
		// RC
		rcSignalNickDataset.addValue( quadrocopter.rcNick );
		rcSignalRollDataset.addValue( quadrocopter.rcRoll );
		rcSignalYawDataset.addValue( quadrocopter.rcYaw );
		rcSignalThrottleDataset.addValue( quadrocopter.rcThrottle );
		rcSignalLinPotiDataset.addValue( quadrocopter.rcLinPoti );
		if( quadrocopter.rcEnableMotors ) {
			rcSignalEnableDataset.addValue(1.0f);
		} else {
			rcSignalEnableDataset.addValue(0.0f);
		}
		if( quadrocopter.rcSwitch ) {
			rcSignalSwitchDataset.addValue(1.0f);
		} else {
			rcSignalSwitchDataset.addValue(0.0f);
		}
		
		// Visualisierungen aktualisieren
	}
	
	public void updateSensorGraphs() {
		byte[] data = quadrocopter.getSensorData();
		if( data[data.length - 1] == 1) {
			// Daten aus array auslesen
			// Accelerometerwerte
			float[] values = new float[7];
			values[0] = quadrocopter.byteArrayToFloat(data, 0) ;
			values[1] = quadrocopter.byteArrayToFloat(data, 4);
			values[2] = quadrocopter.byteArrayToFloat(data, 8);
			visualisierungsfenster.accGraph.update(values);
			// Gyrowerte
			values[0] = quadrocopter.byteArrayToFloat(data, 12) / 10;
			values[1] = quadrocopter.byteArrayToFloat(data, 16) / 10;
			values[2] = quadrocopter.byteArrayToFloat(data, 20) / 10;
			visualisierungsfenster.gyroGraph.update(values);
			// Magnetfeldsensorwerte
			// TODO: ANZEIGE FÜR KOMPASS
			// Temperatur
			visualisierungsfenster.tempAltimeter.update((int)quadrocopter.byteArrayToFloat(data, 36));
			// TODO: dataset für Hoehe
			// rel Hoehe
			visualisierungsfenster.relAltAltimeter.update((int)quadrocopter.byteArrayToFloat(data, 44));
			// TODO: dataset für delta H
			// Fernsteuerungswerte
			values[0] = quadrocopter.byteArrayToFloat(data, 52) * 100 + 50;
			values[1] = quadrocopter.byteArrayToFloat(data, 56) * 100 + 50;
			values[2] = quadrocopter.byteArrayToFloat(data, 60) * 100 + 50;
			values[3] = quadrocopter.byteArrayToFloat(data, 64) * 100;
			values[4] = quadrocopter.byteArrayToFloat(data, 68) * 100;
			values[5] = quadrocopter.byteArrayToFloat(data, 72) * 100;
			values[6] = quadrocopter.byteArrayToFloat(data, 76) * 100;
			visualisierungsfenster.rcGraph.update(values);
		} else {
			statuslabel.setText("FEHLER BEI SENSORMESSWERTEN!");
		}
	}
}
