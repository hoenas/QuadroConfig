import java.awt.BorderLayout;

import javax.swing.JFrame;

import jssc.SerialPort;

import javax.swing.JPanel;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFormattedTextField;


public class ConfigWindow extends JDialog {
	
	private SerialPort port;
	private JTextField adr_textField;
	private JTextField data_textField;
	private JLabel statuslabel;
	JButton btnEnterConfigMode;
	private boolean isConfigMode;
	public boolean isOpen;
	
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
	
	
	public void openConfigWindow(SerialPort port, JLabel statuslabel, boolean isConfigMode){
		this.statuslabel = statuslabel;
		this.port = port;
		this.setVisible(true);
		this.setLocation(433, 50);
		this.setSize( this.getSize().width,this.getSize().height);
		this.isConfigMode = isConfigMode;
		if (isConfigMode) {
			btnEnterConfigMode.setText("leave config mode");
		} else {
			btnEnterConfigMode.setText("enter config mode");
		}
	}

	private static float byte2float (byte[] byteArray, int offset)
	{  
	     return  ByteBuffer.wrap(byteArray, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	private byte[] float2byte( float f, byte[] byteArray, int offset) {

		byte tmp[] = ByteBuffer.allocate(4).putFloat(f).array();
		byteArray[offset] = tmp[3];
		byteArray[offset + 1] = tmp[2];
		byteArray[offset + 2] = tmp[1];
		byteArray[offset + 3] = tmp[0];
		return byteArray;
	}
	
		
	private void loadConfig() {
		
		
	}
	private void sendConfig() {
		

		try {
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
			byte[] outbuffer = new byte[12 + 1];
//			outbuffer[0] = USB_CMD_UPDATE_CONFIG;
//			outbuffer = float2byte( Float.parseFloat( spinner_pxy.getValue().toString()), outbuffer, 1 );
//			outbuffer = float2byte(Float.parseFloat( spinner_ixy.getValue().toString()), outbuffer, 5 );
//			outbuffer = float2byte( Float.parseFloat(spinner_dxy.getValue().toString()), outbuffer, 9 );
//			outbuffer = float2byte( Float.parseFloat(spinner_pz.getValue().toString()), outbuffer, 13 );
//			outbuffer = float2byte(Float.parseFloat( spinner_iz.getValue().toString()), outbuffer, 17 );
//			outbuffer = float2byte(Float.parseFloat(spinner_dz.getValue().toString()), outbuffer, 21 );
//			outbuffer = float2byte( 0.99f, outbuffer, 25 );
//			outbuffer = float2byte( 0.99f, outbuffer, 29 );
//			// ...

			port.writeBytes(outbuffer);					
			statuslabel.setText("update Configuration");
			
			// dummy read
			while (port.getInputBufferBytesCount() != 1) {
				
			}
			byte[] dummy = port.readBytes();
			

			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		
	}
	
	private void saveConfig() {

		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_SAVE_CONFIG;
			port.writeBytes(outbuffer);					
			statuslabel.setText("save Configuration -> reset");
			
			// dummy read
			while (port.getInputBufferBytesCount() != 1) ;
			byte[] dummy = port.readBytes();
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		
	}
	
	private void readEEPROM() {
		
		//TODO readEEPROM()
		
	}
	
	private void writeEEPROM() {
		// TODO writeEEPROM()
	}
	
	private void toggleConfigMode() {
		if (isConfigMode) {
			isConfigMode = false;
			btnEnterConfigMode.setText("enter config mode");
			
		} else {
			isConfigMode = true;
			btnEnterConfigMode.setText("leave config mode");
		}	
		
		try {
			byte[] outbuffer = new byte[1];
			outbuffer[0] = USB_CMD_CONFIG_MODE;
			port.writeBytes(outbuffer);					
			
			
			// dummy read
			while (port.getInputBufferBytesCount() != 1) ;
			byte[] dummy = port.readBytes();
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public ConfigWindow() {
			
		setTitle("Configuration");
		setBounds(433, 377, 700, 600);
		getContentPane().setLayout(null);
		
		JPanel PID_angle_XY_panel = new JPanel();
		PID_angle_XY_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		PID_angle_XY_panel.setBounds(12, 12, 166, 169);
		getContentPane().add(PID_angle_XY_panel);
		PID_angle_XY_panel.setLayout(null);
		
		JSpinner p_ang_xy_spinner = new JSpinner();
		p_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		p_ang_xy_spinner.setBounds(77, 36, 60, 20);
		PID_angle_XY_panel.add(p_ang_xy_spinner);
		
		JSpinner i_ang_xy_spinner = new JSpinner();
		i_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		i_ang_xy_spinner.setBounds(77, 60, 60, 20);
		PID_angle_XY_panel.add(i_ang_xy_spinner);
		
		JSpinner d_ang_xy_spinner = new JSpinner();
		d_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		d_ang_xy_spinner.setBounds(77, 84, 60, 20);
		PID_angle_XY_panel.add(d_ang_xy_spinner);
		
		JSpinner scale_ang_xy_spinner = new JSpinner();
		scale_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		scale_ang_xy_spinner.setBounds(77, 132, 60, 20);
		PID_angle_XY_panel.add(scale_ang_xy_spinner);
		
		JSpinner gain_ang_xy_spinner = new JSpinner();
		gain_ang_xy_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		gain_ang_xy_spinner.setBounds(77, 108, 60, 20);
		PID_angle_XY_panel.add(gain_ang_xy_spinner);
		
		JLabel lblPidAngleXy = new JLabel("PID Angle XY");
		lblPidAngleXy.setBounds(27, 12, 110, 20);
		PID_angle_XY_panel.add(lblPidAngleXy);
		
		JLabel lblP = new JLabel("P");
		lblP.setBounds(12, 37, 50, 16);
		PID_angle_XY_panel.add(lblP);
		
		JLabel lblI = new JLabel("I");
		lblI.setBounds(12, 59, 50, 16);
		PID_angle_XY_panel.add(lblI);
		
		JLabel lblD = new JLabel("D");
		lblD.setBounds(12, 85, 50, 16);
		PID_angle_XY_panel.add(lblD);
		
		JLabel lblGain = new JLabel("Gain");
		lblGain.setBounds(12, 109, 50, 16);
		PID_angle_XY_panel.add(lblGain);
		
		JLabel lblSpScale = new JLabel("SP scale");
		lblSpScale.setBounds(12, 133, 60, 16);
		PID_angle_XY_panel.add(lblSpScale);
		
		JPanel PID_angle_Z_panel = new JPanel();
		PID_angle_Z_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		PID_angle_Z_panel.setLayout(null);
		PID_angle_Z_panel.setBounds(184, 12, 166, 169);
		getContentPane().add(PID_angle_Z_panel);
		
		JSpinner p_ang_z_spinner = new JSpinner();
		p_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		p_ang_z_spinner.setBounds(77, 36, 60, 20);
		PID_angle_Z_panel.add(p_ang_z_spinner);
		
		JSpinner i_ang_z_spinner = new JSpinner();
		i_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		i_ang_z_spinner.setBounds(77, 60, 60, 20);
		PID_angle_Z_panel.add(i_ang_z_spinner);
		
		JSpinner d_ang_z_spinner = new JSpinner();
		d_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		d_ang_z_spinner.setBounds(77, 84, 60, 20);
		PID_angle_Z_panel.add(d_ang_z_spinner);
		
		JSpinner scale_ang_z_spinner = new JSpinner();
		scale_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		scale_ang_z_spinner.setBounds(77, 132, 60, 20);
		PID_angle_Z_panel.add(scale_ang_z_spinner);
		
		JSpinner gain_ang_z_spinner = new JSpinner();
		gain_ang_z_spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		gain_ang_z_spinner.setBounds(77, 108, 60, 20);
		PID_angle_Z_panel.add(gain_ang_z_spinner);
		
		JLabel label = new JLabel("PID Angle Z");
		label.setBounds(27, 12, 110, 20);
		PID_angle_Z_panel.add(label);
		
		JLabel label_1 = new JLabel("P");
		label_1.setBounds(12, 37, 50, 16);
		PID_angle_Z_panel.add(label_1);
		
		JLabel label_2 = new JLabel("I");
		label_2.setBounds(12, 59, 50, 16);
		PID_angle_Z_panel.add(label_2);
		
		JLabel label_3 = new JLabel("D");
		label_3.setBounds(12, 85, 50, 16);
		PID_angle_Z_panel.add(label_3);
		
		JLabel label_4 = new JLabel("Gain");
		label_4.setBounds(12, 109, 50, 16);
		PID_angle_Z_panel.add(label_4);
		
		JLabel label_5 = new JLabel("SP scale");
		label_5.setBounds(12, 133, 60, 16);
		PID_angle_Z_panel.add(label_5);
		
		JPanel compfilter_panel = new JPanel();
		compfilter_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		compfilter_panel.setBounds(12, 188, 166, 91);
		getContentPane().add(compfilter_panel);
		compfilter_panel.setLayout(null);
		
		JLabel lblComplFilter = new JLabel("Comp.  Filter");
		lblComplFilter.setBounds(12, 12, 169, 15);
		compfilter_panel.add(lblComplFilter);
		
		JLabel lblXy = new JLabel("XY");
		lblXy.setBounds(22, 34, 50, 15);
		compfilter_panel.add(lblXy);
		
		JLabel lblZ = new JLabel("Z");
		lblZ.setBounds(22, 58, 50, 15);
		compfilter_panel.add(lblZ);
		
		JSpinner compFilterXY_Spinner = new JSpinner();
		compFilterXY_Spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		compFilterXY_Spinner.setBounds(65, 34, 60, 20);
		compfilter_panel.add(compFilterXY_Spinner);
		
		JSpinner compFilterZ_Spinner = new JSpinner();
		compFilterZ_Spinner.setModel(new SpinnerNumberModel(new Float(1), null, null, new Float(0.1)));
		compFilterZ_Spinner.setBounds(65, 58, 60, 20);
		compfilter_panel.add(compFilterZ_Spinner);
		
		JPanel eeprom_access_panel = new JPanel();
		eeprom_access_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		eeprom_access_panel.setBounds(12, 397, 328, 147);
		getContentPane().add(eeprom_access_panel);
		eeprom_access_panel.setLayout(null);
		
		JLabel lblDirectEepromAccess = new JLabel("Direct EEPROM Access");
		lblDirectEepromAccess.setBounds(12, 12, 180, 15);
		eeprom_access_panel.add(lblDirectEepromAccess);
		
		JButton btnRead = new JButton("read");
		// TODO
		btnRead.setEnabled(false);
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readEEPROM();
			}
		});
		btnRead.setBounds(22, 92, 125, 25);
		eeprom_access_panel.add(btnRead);
		
		JButton btnWrite = new JButton("write");
		// TODO
		btnWrite.setEnabled(false);
		btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeEEPROM();
			}
		});
		btnWrite.setBounds(185, 92, 125, 25);
		eeprom_access_panel.add(btnWrite);
		
		JComboBox nrBytes_comboBox = new JComboBox();
		nrBytes_comboBox.setBounds(240, 36, 70, 20);
		nrBytes_comboBox.addItem("1");
		nrBytes_comboBox.addItem("2");
		nrBytes_comboBox.addItem("4");
		eeprom_access_panel.add(nrBytes_comboBox);
		
		JComboBox format_comboBox = new JComboBox();
		format_comboBox.setBounds(240, 60, 70, 20);
		format_comboBox.addItem("hex");
		format_comboBox.addItem("dec");
		format_comboBox.addItem("float");
		format_comboBox.addItem("ascii");
		eeprom_access_panel.add(format_comboBox);
		
		adr_textField = new JTextField();
		adr_textField.setBounds(100, 36, 70, 20);
		eeprom_access_panel.add(adr_textField);
		adr_textField.setColumns(4);
		
		data_textField = new JTextField();
		data_textField.setBounds(100, 60, 70, 20);
		eeprom_access_panel.add(data_textField);
		data_textField.setColumns(10);
		
		JLabel lblbytes = new JLabel("bytes");
		lblbytes.setBounds(190, 36, 60, 20);
		eeprom_access_panel.add(lblbytes);
		
		JLabel lblAddress = new JLabel("address");
		lblAddress.setBounds(22, 36, 70, 15);
		eeprom_access_panel.add(lblAddress);
		
		JLabel lblData = new JLabel("data");
		lblData.setBounds(22, 60, 70, 15);
		eeprom_access_panel.add(lblData);
		
		JLabel lblFormat = new JLabel("format");
		lblFormat.setBounds(190, 60, 60, 20);
		eeprom_access_panel.add(lblFormat);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(434, 12, 254, 169);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnLoad = new JButton("load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadConfig();
			}
		});
		btnLoad.setBounds(12, 12, 230, 25);
		panel.add(btnLoad);
		
		JButton btnUpdate = new JButton("update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendConfig();
			}
		});
		btnUpdate.setBounds(12, 42, 230, 25);
		panel.add(btnUpdate);
		
		JButton btnSaveToEeprom = new JButton("save to eeprom");
		btnSaveToEeprom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		btnSaveToEeprom.setBounds(12, 72, 230, 25);
		panel.add(btnSaveToEeprom);
		
		btnEnterConfigMode = new JButton("config mode");
		btnEnterConfigMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleConfigMode();			
			}
		});
		btnEnterConfigMode.setBounds(12, 126, 230, 25);
		panel.add(btnEnterConfigMode);
		
		JButton btnEndConfig = new JButton("End Config");
		btnEndConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		btnEndConfig.setBounds(560, 503, 117, 25);
		getContentPane().add(btnEndConfig);
		
		
	}
}
