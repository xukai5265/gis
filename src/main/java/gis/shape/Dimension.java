package gis.shape;


public class Dimension {
	int hoursY;
	int minutesY;
	int secondY;
	int hoursX;
	int minutesX;
	int secondX;
	String mapId;
	
	double x;
	double y;
		
	public Dimension(int hoursX, int minutesX, int secondX, int hoursY, int minutesY, int secondY){
		this.hoursY = hoursY;
		this.minutesY = minutesY;
		this.secondY = secondY;
		this.hoursX = hoursX;
		this.minutesX = minutesX;
		this.secondX = secondX;
		toDecimal();
	}
	
	public void toDecimal(){
		x = hoursX + minutesX / 60 + secondX / 3600;
		y = hoursY + minutesY / 60 + secondY / 3600;
		
	}
	
	public Dimension(double x,double y){
		this.x = x;
		this.y = y;
		toDegree();
	}
	
	public void toDegree(){
		hoursX = (int)x;
		minutesX = (int)((x % 1) * 60);
		secondX = (int)(((x % 1) * 60) % 1) * 60;
		hoursY = (int)y;
		minutesY = (int)((y % 1) * 60);
		secondY = (int)(((y % 1) * 60) % 1) * 60;
	}
	
	
	public String getMapId(){
		String ret = "";
		int outMillionY = (int)(y / 4) + 1 + 96;
		String a = Character.toString((char)outMillionY);
		int outMillionX = (int)(x / 6) + 30 + 1;
		double ii = 0.083333333333333,jj = 0.124999999999999;
		double thousandYtp = (((int)(y / 4) + 1) * 4 - y) / ii ;
		int thY = (int)(thousandYtp * 10000);
		int thousandY = (int)thousandYtp ;
		if (((int)thousandYtp) * 10000 != thY ){
			thousandY += 1;
		}
		int thousandX = (int)((x - (outMillionX - 31) * 6) / jj) + 1;
		ret = a + outMillionX  + "f" + String.format("%03d", thousandY) + String.format("%03d", thousandX);
		return ret;
	}
	
	public static void main(String [] args){
		Dimension d = new Dimension(109.625510,19.928810);
		System.out.println(d.getMapId());
		
		
	}
	
}
