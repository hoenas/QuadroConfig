package LiveGraph;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class LiveLineGraph extends JPanel{
	private float[] valuebuffer;
	
	LiveLineGraph( int buffersize ) {
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(420, 420));
		//valuebuffer = new Float(buffersize);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawString("BLAH", 20, 20);
		g.drawRect(200, 200, 200, 200);
	}
}
