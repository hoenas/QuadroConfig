package LiveGraph;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class LiveCompass extends Canvas{
	private java.util.List<Dataset> graphlist = new ArrayList<Dataset>();
	private Color color;
	private Color backgroundColor;
	private boolean useRaster;
	private Color rasterColor;
	private BufferStrategy bufferstrategy;
	private int rasterWidth;
	private BasicStroke stroke;
	
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
	
	public LiveCompass( Color backgroundColor, Color foregroundColor, Color rasterColor, boolean useRaster, int rasterWidth) {
		this.backgroundColor = backgroundColor;
		this.color = foregroundColor;
		this.rasterColor = rasterColor;
		this.useRaster = useRaster;
		this.rasterWidth = rasterWidth;
		stroke = new BasicStroke( rasterWidth );	
	}
	
	public void addGraph( Dataset dataset ) {
		graphlist.add(dataset);
	}
	
	public void update( int angle) {
		Graphics2D g2 = (Graphics2D) bufferstrategy.getDrawGraphics();
		// Graph loeschen
		g2.setColor( backgroundColor );
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// kuenstlichen Horizont zeichnen		
		g2.setColor( color );		
		g2.fillArc(0, 0, this.getWidth(), this.getHeight(), 88 - angle, 4);
		
		// Umrandung zeichnen
		if( useRaster ) {
			g2.setStroke( stroke );
			g2.setColor( rasterColor );
			g2.drawArc(0, 0, this.getWidth(), this.getHeight(), 0, 360);
			for(int i = 0; i < 30; i++) {
				g2.drawArc(10, 10, this.getWidth() - 20, this.getHeight() - 20, 12*i , 1);
			}
			/*
			g2.drawString("N", this.getWidth() / 2	, 10);
			g2.drawString("O", this.getWidth() - 20	, this.getHeight() / 2);
			g2.drawString("S", this.getWidth() / 2	, this.getHeight() - 20);
			g2.drawString("W", 10					, this.getHeight() / 2);
			*/
		}
		
		bufferstrategy.show();
		Toolkit.getDefaultToolkit().sync();
	}


	public void setRasterWidth(int rasterWidth) {
		this.rasterWidth = rasterWidth;
		this.stroke = new BasicStroke( rasterWidth );
	}
}
