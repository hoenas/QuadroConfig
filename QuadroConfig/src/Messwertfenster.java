import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import java.awt.Font;
import javax.swing.JCheckBox;

public class Messwertfenster extends JFrame {
	public JCheckBox chckbxAccelerometer;
	public JCheckBox chckbxGyro;
	public JCheckBox chckBxMagnetometer;
	public JCheckBox chckBxAngle;
	public JCheckBox chckBxMotors;
	public JCheckBox chckBxRC;
	public JCheckBox chckBxVel;
	public JCheckBox chckBxHeight;
	public JCheckBox chckBxTemp;
	public JCheckBox chckBxBatt;
	public JCheckBox chckBxCPU;
	public JCheckBox chckbxAngleSetpoints;
	public JCheckBox chckbxVelocitySetpoints;
	public JCheckBox chckbxMotorSetpoints;

	public Messwertfenster() {
		setTitle("Monitoring Values");
		setBounds(100, 100, 433, 703);
		getContentPane().setLayout(null);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBounds(0, 0, 417, 664);
		getContentPane().add(splitPane);

		JLabel label = new JLabel(
				"<html><h2>Monitoring Values:</h2><table><tr><td><b>Acc. X: </b><br><b>Acc. Y: </b><br><b>Acc. Z: </b><br></td><td><dynamic> m/s²<br><dynamic> m/s²<br><dynamic> m/s²<br></td></tr><tr><td><b>Rate X: </b><br><b>Rate Y: </b><br><b>Rate Z: </b><br></td><td><dynamic> °/s<br><dynamic> °/s<br><dynamic> °/s<br></td></tr><tr><td><b>Magnet X: </b><br><b>Magnet Y: </b><br><b>Magnet Z: </b><br></td><td><dynamic>?<br><dynamic>?<br><dynamic>?<br></td></tr><tr><td><b>Angle X: </b><br><b>Angle Y: </b><br><b>Angle Z: </b><br></td><td><dynamic> °<br><dynamic> °<br><dynamic> °<br></td></tr><tr><td><b>Motor 1: </b><br><b>Motor 2: </b><br><b>Motor 3: </b><br><b>Motor 4: </b><br></td><td><dynamic> %<br><dynamic> %<br><dynamic> %<br><dynamic> %<br></td></tr><tr><td><b>RC Signal Roll: </b><br><b>RC Signal Nick: </b><br><b>RC Signal Yaw: </b><br><b>RC Signal Throttle: </b><br><b>RC Signal Enable: </b><br><b>RC Signal Lin Poti: </b><br><b>RC Signal Switch: </b><br></td><td><dynamic> <br><dynamic> <br><dynamic> <br><dynamic> <br><dynamic> <br><dynamic> <br><dynamic> <br></td></tr><tr><td><b>PID X: </b><br><b>PID Y: </b><br><b>PID Z: </b><br></td><td><dynamic> <br><dynamic> <br><dynamic> <br></td></tr><tr><td><b>Height: </b><br><b>rel Height: </b><br><b>dH: </b><br></td><td><dynamic> m<br><dynamic> m<br><dynamic> m<br></td></tr><tr><td><b>Temp: </b><br><b>Batt. Voltage: </b><br><b>CPU Load: </b><br></td><td><dynamic> °C<br><dynamic> V<br><dynamic> %<br></td></tr></table></html>");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		splitPane.setLeftComponent(label);

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(null);

		chckbxAccelerometer = new JCheckBox("Accelerometer");
		chckbxAccelerometer.setSelected(true);
		chckbxAccelerometer.setBounds(6, 32, 224, 25);
		panel.add(chckbxAccelerometer);

		chckbxGyro = new JCheckBox("Gyro");
		chckbxGyro.setSelected(true);
		chckbxGyro.setBounds(6, 62, 224, 25);
		panel.add(chckbxGyro);

		chckBxMagnetometer = new JCheckBox("Magnetometer");
		chckBxMagnetometer.setBounds(6, 92, 224, 25);
		panel.add(chckBxMagnetometer);

		chckBxAngle = new JCheckBox("Angles");
		chckBxAngle.setSelected(true);
		chckBxAngle.setBounds(6, 122, 224, 25);
		panel.add(chckBxAngle);

		chckBxMotors = new JCheckBox("Motor-Values");
		chckBxMotors.setSelected(true);
		chckBxMotors.setBounds(6, 272, 224, 25);
		panel.add(chckBxMotors);

		chckBxRC = new JCheckBox("RC-Values");
		chckBxRC.setBounds(6, 302, 224, 25);
		panel.add(chckBxRC);

		chckBxVel = new JCheckBox("Velocities");
		chckBxVel.setBounds(6, 182, 224, 25);
		panel.add(chckBxVel);

		chckBxHeight = new JCheckBox("Height");
		chckBxHeight.setBounds(6, 332, 224, 25);
		panel.add(chckBxHeight);

		chckBxTemp = new JCheckBox("Temperature");
		chckBxTemp.setBounds(6, 362, 224, 25);
		panel.add(chckBxTemp);

		chckBxBatt = new JCheckBox("Battery Voltage");
		chckBxBatt.setBounds(6, 392, 224, 25);
		panel.add(chckBxBatt);

		chckBxCPU = new JCheckBox("CPU Load");
		chckBxCPU.setBounds(6, 422, 224, 25);
		panel.add(chckBxCPU);

		JLabel lblRequestData = new JLabel("<html><h2>Request Data:</h2></html>");
		lblRequestData.setBounds(6, 11, 224, 21);
		panel.add(lblRequestData);

		chckbxAngleSetpoints = new JCheckBox("Angle SetPoints");
		chckbxAngleSetpoints.setBounds(6, 152, 183, 25);
		panel.add(chckbxAngleSetpoints);

		chckbxVelocitySetpoints = new JCheckBox("Velocity SetPoints");
		chckbxVelocitySetpoints.setBounds(6, 212, 158, 25);
		panel.add(chckbxVelocitySetpoints);

		chckbxMotorSetpoints = new JCheckBox("Motor SetPoints");
		chckbxMotorSetpoints.setBounds(6, 242, 158, 25);
		panel.add(chckbxMotorSetpoints);
		float[] zeros = new float[128];
		setLabelText(zeros);
	}

	private String roundedString(float value, int digits) {
		value *= Math.pow(10, digits);
		value = Math.round(value);
		value /= Math.pow(10, digits);

		String tmp = Float.toString(value);

		while (tmp.substring(tmp.lastIndexOf(".")).length() < digits + 1) {
			tmp += "0";
		}
		return tmp;

	}

	public void setLabelText(float[] values) {
	}
}
