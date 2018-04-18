package gis;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class Grid {
	private int x, y;
	private List<Line> line = new ArrayList<Line>();
	private List<Polygon> polygons = new ArrayList<Polygon>();
	
	
	
	
	public List<Polygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(List<Polygon> polygons) {
		this.polygons = polygons;
	}

	public Grid(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public List<Line> getLine() {
		return line;
	}

	public void setLine(List<Line> line) {
		this.line = line;
	}


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
	
	

}


