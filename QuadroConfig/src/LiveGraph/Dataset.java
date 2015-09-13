package LiveGraph;

import java.awt.Color;

public class Dataset {
	private String title;
	private Color color;
	private int thickness;
	private int historyLength;
	private float[] buffer;
	private boolean visible;
	
	public Dataset( String title, Color color, int thickness, int historyLength ) {
		this.title = title;
		this.color = color;
		this.thickness = thickness;
		this.historyLength = historyLength;
		this.buffer = new float[historyLength];
		
		this.visible = true;
		
		for(int i = 0; i < buffer.length; i++) {
			buffer[i] = 0.0f;
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getThickness() {
		// TODO: Dicke implementieren
		return thickness;
	}
	
	public float[] getBuffer() {
		return buffer;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible( boolean visible ) {
		this.visible = visible;
	}
	
	public void addValue( float value ) {
		for(int i = 0; i < buffer.length - 1; i++) {
			buffer[i] = buffer[i+1];
		}
		buffer[buffer.length-1] = value;
	}
}
