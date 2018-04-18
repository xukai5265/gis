package gis;

import java.awt.Polygon;

public class Line {
	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}

	private String lineName;
	private String lineNo;
	private Polygon polygon;
	private int x;
	private int y;

	public Line(String lineName, String lineNo, Polygon polygon,int x,int y) {
		super();
		this.lineName = lineName;
		this.lineNo = lineNo;
		this.polygon = polygon;
		this.x = x;
		this.y = y;
	}
	
	

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

}
