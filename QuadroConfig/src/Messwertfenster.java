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


public class Messwertfenster extends JFrame {
	JLabel lblMessung;
	public Messwertfenster() {
		setTitle("Messwerte");
		setBounds(100, 100, 433, 703);
		float[] zeros = new float[128];
		lblMessung = new JLabel(" ");
		lblMessung.setVerticalAlignment(SwingConstants.TOP);
		lblMessung.setHorizontalAlignment(SwingConstants.LEFT);
		getContentPane().add(lblMessung, BorderLayout.CENTER);
		setLabelText(zeros);
	}
	
	private String roundedString(float value, int digits) {
		value*= Math.pow(10, digits); 
		value = Math.round(value);
		value /= Math.pow(10, digits); 
		
		String tmp = Float.toString(value);
		
		while (tmp.substring(tmp.lastIndexOf(".")).length() < digits + 1) {
			tmp += "0";
		}
		return tmp;
			
	}
	
	public void setLabelText( float[] values ) {
		lblMessung.setText("<html>"
				+ "<h2>Messung:</h2>"
				+	"<table>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Beschl. X: </b><br>"
				+ 			"<b>Beschl. Y: </b><br>"
				+			"<b>Beschl. Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+				roundedString(values[0],2) + " m/s²<br>"
				+				roundedString( values[1], 2) + " m/s²<br>"
				+				roundedString( values[2], 2) + " m/s²<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Rate X: </b><br>"
				+			"<b>Rate Y: </b><br>"
				+			"<b>Rate Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[3], 2) + " °/s<br>"
				+			roundedString( values[4], 2) + " °/s<br>"
				+			roundedString( values[5], 2) + " °/s<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Magnet X: </b><br>"
				+			"<b>Magnet Y: </b><br>"
				+			"<b>Magnet Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[6], 2) + "?<br>"
				+			roundedString( values[7], 2) + "?<br>"
				+			roundedString( values[8], 2) + "?<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Winkel X: </b><br>"
				+			"<b>Winkel Y: </b><br>"
				+			"<b>Winkel Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[9], 2) + " °<br>"
				+			roundedString( values[10], 2) + " °<br>"
				+			roundedString( values[11], 2) + " °<br>"
				+		"</td>"
				+	"</tr>"
				+		"<tr>"
				+			"<td>"
				+				"<b>Motor 1: </b><br>"
				+				"<b>Motor 2: </b><br>"
				+				"<b>Motor 3: </b><br>"
				+				"<b>Motor 4: </b><br>"
				+			"</td>"
				+			"<td>"
				+				roundedString( values[19], 2) + " %<br>"
				+				roundedString( values[20], 2) + " %<br>"
				+				roundedString( values[21], 2) + " %<br>"
				+				roundedString( values[22], 2) + " %<br>"
				+			"</td>"
				+		"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>RC Signal Roll: </b><br>"
				+			"<b>RC Signal Nick: </b><br>"
				+			"<b>RC Signal Yaw: </b><br>"
				+			"<b>RC Signal Throttle: </b><br>"
				+ 			"<b>RC Signal Enable: </b><br>"
				+ 			"<b>RC Signal Lin Poti: </b><br>"
				+ 			"<b>RC Signal Switch: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[12], 2) + " <br>"
				+			roundedString( values[13], 2) + " <br>"
				+			roundedString( values[14], 2) + " <br>"
				+			roundedString( values[15], 2) + " <br>"
				+			roundedString( values[16], 2) + " <br>"						
				+			roundedString( values[18], 2) + " <br>"
				+			roundedString( values[17], 2) + " <br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>PID X: </b><br>"
				+			"<b>PID Y: </b><br>"
				+			"<b>PID Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[28], 2) + " <br>"
				+			roundedString( values[29], 2) + " <br>"
				+			roundedString( values[30], 2) + " <br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Height: </b><br>"
				+			"<b>rel Height: </b><br>"
				+			"<b>dH: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[24], 2) + " m<br>"
				+			roundedString( values[25], 2) + " m<br>"
				+			roundedString( values[26], 2) + " m<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Temp: </b><br>"
				+			"<b>Akku Voltage: </b><br>"
				+			"<b>CPU Load: </b><br>"
				+		"</td>"
				+		"<td>"
				+			roundedString( values[23], 2) + " °C<br>"
				+			roundedString( values[27], 2) + " V<br>"
				+			roundedString( values[31] * 100, 2) + " %<br>"
				+		"</td>"
				+	"</tr>"
				+"</table>"
				+"</html>");
	}

}
