package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class LiveArtificialHorizon extends Canvas{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color color;
	private Color backgroundColor;
	private boolean useRaster;
	private Color rasterColor;
	private BufferStrategy bufferstrategy;
	
	public boolean isUseRaster() {
		return useRaster;
	}

	public void setUseRaster(boolean useRaster) {
		this.useRaster = useRaster;
	}

	public Color getRasterColor() {
		return rasterColor;
	}

	public void setRastertColor(Color rasterColor) {
		this.rasterColor = rasterColor;
	}
	
	public void setBufferStrategy() {
		this.createBufferStrategy(2);
		bufferstrategy = this.getBufferStrategy();
	}
	
	public LiveArtificialHorizon( Color backgroundColor, int max ) {
		this.backgroundColor = backgroundColor;
	}
	
	public void addGraph( Dataset dataset ) {
		graphlist.add(dataset);
	}
	
	public void update( float[] values ) {
		Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
		// neue Werte eintragen
		for(int i = 0; i < graphlist.size(); i++) {
			graphlist.get(i).addValue( values[i] );
		}
		// Graph loeschen
		g2.setColor( backgroundColor );
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// kuenstlichen Horizont zeichnen
		
		bufferstrategy.show();
		Toolkit.getDefaultToolkit().sync();
	}
}
