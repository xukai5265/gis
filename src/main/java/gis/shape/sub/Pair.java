package gis.shape.sub;
public class Pair {
	public Point key;
	public double value1;
	public double value2;
	public String type;

	public String getType() {
		return type;
	}
	public String toString(){
		return "Point: " + key.toString()+"\t" 
				+ "value1: " + value1
				+ "value2: " + value2
				+ "type: " + type
				;
		
	}

	public void setType(String type) {
		this.type = type;
	}

	public Pair(Point p, double d) {
		key = p;
		value1 = d;
	}

	public Pair(Point p, double d, double v2) {
		key = p;
		value1 = d;
		value2 = v2;
	}

	public Pair(Point p, double d, double v2, String t) {
		key = p;
		value1 = d;
		value2 = v2;
		type = t;
	}

	public Point getKey() {
		return key;
	}

	public void setKey(Point key) {
		this.key = key;
	}

	public double getValue1() {
		return value1;
	}

	public void setValue(double value) {
		this.value1 = value;
	}

	public double getValue2() {
		return value2;
	}

	public void setValue2(double value2) {
		this.value2 = value2;
	}
}