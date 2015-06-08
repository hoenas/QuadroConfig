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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 258, 331);
		float[] zeros = new float[128];
		lblMessung = new JLabel(" ");
		lblMessung.setVerticalAlignment(SwingConstants.TOP);
		lblMessung.setHorizontalAlignment(SwingConstants.LEFT);
		getContentPane().add(lblMessung, BorderLayout.CENTER);
		setLabelText(zeros);
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
				+				String.valueOf( values[0]) + " m/s²<br>"
				+				String.valueOf( values[1]) + " m/s²<br>"
				+				String.valueOf( values[2]) + " m/s²<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Rate X: </b><br>"
				+			"<b>Rate Y: </b><br>"
				+			"<b>Rate Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[3]) + " °/s<br>"
				+			String.valueOf( values[4]) + " °/s<br>"
				+			String.valueOf( values[5]) + " °/s<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Magnet X: </b><br>"
				+			"<b>Magnet Y: </b><br>"
				+			"<b>Magnet Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[6]) + "?<br>"
				+			String.valueOf( values[7]) + "?<br>"
				+			String.valueOf( values[8]) + "?<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Winkel X: </b><br>"
				+			"<b>Winkel Y: </b><br>"
				+			"<b>Winkel Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[9]) + " °<br>"
				+			String.valueOf( values[10]) + " °<br>"
				+			String.valueOf( values[11]) + " °<br>"
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
				+				String.valueOf( values[19]) + " %<br>"
				+				String.valueOf( values[20]) + " %<br>"
				+				String.valueOf( values[21]) + " %<br>"
				+				String.valueOf( values[22]) + " %<br>"
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
				+			String.valueOf( values[12]) + " <br>"
				+			String.valueOf( values[13]) + " <br>"
				+			String.valueOf( values[14]) + " <br>"
				+			String.valueOf( values[15]) + " <br>"
				+			String.valueOf( values[16]) + " <br>"						
				+			String.valueOf( values[18]) + " <br>"
				+			String.valueOf( values[17]) + " <br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>PID X: </b><br>"
				+			"<b>PID Y: </b><br>"
				+			"<b>PID Z: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[28]) + " <br>"
				+			String.valueOf( values[29]) + " <br>"
				+			String.valueOf( values[30]) + " <br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Height: </b><br>"
				+			"<b>rel Height: </b><br>"
				+			"<b>dH: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[24]) + " m<br>"
				+			String.valueOf( values[25]) + " m<br>"
				+			String.valueOf( values[26]) + " m<br>"
				+		"</td>"
				+	"</tr>"
				+	"<tr>"
				+		"<td>"
				+			"<b>Temp: </b><br>"
				+			"<b>Akku Voltage: </b><br>"
				+		"</td>"
				+		"<td>"
				+			String.valueOf( values[23]) + " °C<br>"
				+			String.valueOf( values[27]) + " V<br>"
				+		"</td>"
				+	"</tr>"
				+"</table>"
				+"</html>");
	}

}
