package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class LiveLineGraph extends Canvas{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color backgroundColor;
	private int max;
	private int offset;
	private boolean useNegativeValues;
	private boolean useRasterY;
	private Color rasterYColor;
	private int rasterLineCountY;
	private BufferStrategy bufferstrategy;
	
	
	public boolean isUseRasterY() {
		return useRasterY;
	}

	public void setUseRasterY(boolean useRasterY) {
		this.useRasterY = useRasterY;
	}

	public Color getRasterYColor() {
		return rasterYColor;
	}

	public void setRastertYColor(Color rasterYColor) {
		this.rasterYColor = rasterYColor;
	}

	public int getRasterLineCountY() {
		return rasterLineCountY;
	}

	public void setRasterLineCountY(int rasterY) {
		this.rasterLineCountY = rasterY;
	}
	
	public LiveLineGraph( Color backgroundColor, int max, int offset ) {
		this.backgroundColor = backgroundColor;
		this.max = max;
		this.offset = offset;
		bufferstrategy = null;
	}
	
	public void addGraph( Dataset dataset ) {
		graphlist.add(dataset);
	}
	
	public void update( float[] values ) {
		
		if( this.isVisible() && bufferstrategy == null ) {
			this.createBufferStrategy(2);
			bufferstrategy = this.getBufferStrategy();
		} else {
		
			Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
			// neue Werte eintragen
			for(int i = 0; i < graphlist.size(); i++) {
				graphlist.get(i).addValue( values[i] );
			}
			// Graph loeschen
			g2.setColor( backgroundColor );
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			// Y Raster zeichnen
			if( useRasterY ) {
				g2.setColor( rasterYColor );
				g2.setPaintMode();
				for(int i = 0; i < rasterLineCountY; i++) {
					g2.drawLine( 0, i * this.getHeight() / rasterLineCountY, this.getWidth(), i * this.getHeight() / rasterLineCountY);
				}
			}
			
			// Fuer jeden Graph Linien zeichnen
			for( Dataset graph : graphlist ) {
				// Farbe einstellen
				g2.setColor( graph.getColor() );
				// Liniendicke
				g2.setStroke(new BasicStroke( graph.getThickness() ));
				// Linien zeichnen
				for( int i = 0; i < graph.getBuffer().length - 1; i++) {
					int y1 = this.getHeight() - (int)((graph.getBuffer()[i] + offset)  * this.getHeight() / max );
					int y2 = this.getHeight() - (int)((graph.getBuffer()[i+1] + offset) * this.getHeight() / max );
					int x1 = i * this.getWidth() / graph.getBuffer().length;
					int x2 = (i+1) * this.getWidth() / graph.getBuffer().length;
					g2.drawLine(x1 ,y1, x2, y2);
				}
			}
			
			bufferstrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}
}

