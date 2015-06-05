package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.List;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;

import javax.swing.JPanel;

public class LiveLineGraph extends JPanel{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color backgroundColor;
	private int max;
	private boolean useRasterX;
	private Color rasterXColor;
	private int rasterLineCountX;
	private boolean useRasterY;
	private Color rasterYColor;
	private int rasterLineCountY;

	
	public boolean isUseRasterX() {
		return useRasterX;
	}

	public void setUseRasterX(boolean useRasterX) {
		this.useRasterX = useRasterX;
	}

	public Color getRasterXColor() {
		return rasterXColor;
	}

	public void setRasterXColor(Color rasterXColor) {
		this.rasterXColor = rasterXColor;
	}

	public int getRasterLineCountX() {
		return rasterLineCountX;
	}

	public void setRasterLineCountX(int rasterLineCountX) {
		this.rasterLineCountX = rasterLineCountX;
	}

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
	
	public LiveLineGraph( Color backgroundColor, int max ) {
		this.backgroundColor = backgroundColor;
		this.max = max;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	public void addGraph( Dataset dataset ) {
		graphlist.add(dataset);
	}
	
	public void update( float[] values ) {
		Graphics2D g2 = (Graphics2D)this.getGraphics();
		// neue Werte eintragen
		for(int i = 0; i < graphlist.size(); i++) {
			graphlist.get(i).addValue( values[i] );
		}
		// Graph löschen
		g2.setColor( backgroundColor );
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		// X Raster zeichnen
		if( useRasterX ) {
			g2.setXORMode( rasterXColor );
			for(int i = 0; i < rasterLineCountX; i++) {
				g2.drawLine( 0, i * this.getHeight() / rasterLineCountX, this.getHeight(), i * this.getHeight() / rasterLineCountX);
			}
		}
		// Y Raster zeichnen
		if( useRasterY ) {
			g2.setColor( rasterYColor );
			g2.setPaintMode();
			for(int i = 0; i < rasterLineCountY; i++) {
				g2.drawLine( 0, i * this.getHeight() / rasterLineCountX, this.getHeight(), i * this.getHeight() / rasterLineCountX);
			}
		}
		// Für jeden Graph Linien zeichnen
		for( Dataset graph : graphlist ) {
			// Farbe einstellen
			g2.setColor( graph.getColor() );
			// Liniendicke
			g2.setStroke(new BasicStroke( graph.getThickness() ));
			// Linien zeichnen
			for( int i = 0; i < graph.getBuffer().length - 1; i++) {
				int y1 = this.getHeight() - (int)(graph.getBuffer()[i] * (this.getHeight() / max) );
				int y2 = this.getHeight() - (int)(graph.getBuffer()[i+1] * (this.getHeight() / max) );
				int x1 = i * this.getWidth() / graph.getBuffer().length;
				int x2 = (i+1) * this.getWidth() / graph.getBuffer().length;
				g2.drawLine(x1 ,y1, x2, y2);
			}
		}
	}
}
