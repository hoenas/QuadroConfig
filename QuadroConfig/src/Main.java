import java.awt.EventQueue;

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
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


public class Main {

	private SerialPort port;
	private JFrame frame;
	private Timer messtimer;
	private int messintervall = 200;
	private boolean messungAktiv = false;
	// Befehle für Protokoll
	private static int protocolStatus = 0;
	/* Werte für protocolStatus:
	 * 0: idle
	 * 1: receiveFrame
	 * 2: receiveConfiguration
	 * 3: sendConfiguration
	 */
	private static byte COMMAND_MEASUREMENT = (byte)0b10000000;
	private static byte COMMAND_RESET = (byte)0b01000000;
	private static byte COMMAND_CONFIGURATION = (byte)0b00100000;
	
	private static byte[] buffer;
	private static int bufferLength = 50;
	private static int frameLength = 10;
	private static int frameCounter = 0;
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 414, 240);
		frame.getContentPane().add(tabbedPane);
		
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
						btnNewButton.setText("Port schließen");
					}
					else
					{
						port.closePort();
						comboBox.setEnabled(true);
						comboBox_1.setEnabled(true);
						comboBox_3.setEnabled(true);
						comboBox_4.setEnabled(true);
						comboBox_5.setEnabled(true);
						btnNewButton.setText("Port schließen");
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
				} else {
					btnMessungStarten.setText("Messung starten");
				}
				messungAktiv = !messungAktiv;
			}	
		});
		btnMessungStarten.setBounds(10, 11, 113, 23);
		panel_1.add(btnMessungStarten);
		
		JLabel lblIntervallms = new JLabel("Intervall (ms):");
		lblIntervallms.setBounds(133, 15, 75, 14);
		panel_1.add(lblIntervallms);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(200, 25, 1000, 1));
		spinner.setBounds(218, 12, 69, 20);
		panel_1.add(spinner);
		
		// Timerkonfiguration
		messtimer = new Timer();
		messtimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(messungAktiv && port.isOpened() ) {
					try {
						// Daten anfordern
						System.out.println("Messung angefordert");
						port.writeByte(COMMAND_MEASUREMENT);
					} catch( Exception e ) {
						System.out.println( e.getMessage() );
					}
				}
			}
		}, 0, messintervall);
	}
}
