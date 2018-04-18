package gis.shape;

import java.util.Arrays;
import java.util.List;

public class POIPoint {
	final static String sep = ",";
	final static String MARK = "poi_point";
	final static String roadSEP = "`";
	final static double MAX_DISTANCE = 100;
	public String poi_ID;
	public String poi_name;
	public String short_name;
	public String poi_address;
	public String mapID;
	public String kind_1;
	public String kind_1_name;
	public String kind_2;
	public String kind_2_name;
	public String kind_3;
	public String kind_3_name;
	public String AdminCode;
	public String telephone;
	public String zip_code;
	public double lng;  //显示坐标
	public double lat;
	public String importance;
	public String road_ID;
	public String side;
	public String prov_ID;
	public String prov_name;
	public String city_ID;
	public String city_name;
	public double p_lng;
	public double p_lat;
	public double dis;
	public String c_side;
	public LatLngPoint point;
	public Road2 road;

	public POIPoint() {
		this.poi_ID = "";
		this.poi_name = "";
		this.short_name = "";
		this.poi_address = "";
		this.mapID = "";
		this.kind_1 = "";
		this.kind_1_name = "";
		this.kind_2 = "";
		this.kind_2_name = "";
		this.kind_3 = "";
		this.kind_3_name = "";
		this.AdminCode = "";
		this.telephone = "";
		this.zip_code = "";
		this.lng = 0;
		this.lat = 0;
		this.importance = "";
		this.road_ID = "";
		this.side = "";
		this.prov_ID = "";
		this.prov_name = "";
		this.city_ID = "";
		this.city_name = "";
		this.p_lng = 0d;
		this.p_lat = 0d;
		this.dis = 0d;
		this.c_side = "";
		this.point = new LatLngPoint(0, 0);
		this.road = new Road2();
	}

	public String toString() {
		String tmp = this.poi_ID + POIPoint.sep + POIPoint.MARK + POIPoint.sep + this.poi_name + POIPoint.sep
				+ this.short_name + POIPoint.sep + this.poi_address + POIPoint.sep + this.mapID + POIPoint.sep
				+ this.kind_1 + POIPoint.sep + this.kind_1_name + POIPoint.sep + this.kind_2 + POIPoint.sep
				+ this.kind_2_name + POIPoint.sep + this.kind_3 + POIPoint.sep + this.kind_3_name + POIPoint.sep
				+ this.AdminCode + POIPoint.sep + this.telephone + POIPoint.sep + this.zip_code + POIPoint.sep
				+ this.lng + POIPoint.sep + this.lat + POIPoint.sep + this.importance + POIPoint.sep + this.road_ID
				+ POIPoint.sep + this.side + POIPoint.sep + this.prov_ID + POIPoint.sep + this.prov_name + POIPoint.sep
				+ this.city_ID + POIPoint.sep + this.city_name + POIPoint.sep + this.p_lng + POIPoint.sep + this.p_lat
				+ POIPoint.sep + this.dis + POIPoint.sep + this.c_side;
		return tmp;
	}

	public boolean fromString(String s) {
		String[] seq = s.split(",",26);
		if (seq.length < 26)
			return false;
		//类型过滤
		this.kind_1 = seq[6];
		String [] list = {"1","2","3","4","5","6","7","8","A"};
		List<String> j = Arrays.asList(list);
		if(j.contains(this.kind_1)){
			this.poi_ID = seq[0];
			this.poi_name = seq[2];
			this.short_name = seq[3];
			this.poi_address = seq[4];
			this.kind_1_name = seq[7];
			this.kind_2 = seq[8];
			this.kind_2_name = seq[9];
			this.kind_3 = seq[10];
			this.kind_3_name = seq[11];
			this.AdminCode = seq[23];
			this.telephone = seq[13];
			this.zip_code = seq[12];
			this.lng = Double.parseDouble(seq[14]);
			this.lat = Double.parseDouble(seq[15]);
			this.importance = seq[16];
			this.road_ID = seq[17];
			this.side = seq[18];
			this.prov_ID = seq[19];
			this.prov_name = seq[20];
			this.city_ID = seq[21];
			this.city_name = seq[22];
			this.p_lng = 0d; //投影经度
			this.p_lat = 0d; //投影纬度
			this.dis = 0d;   //投影点到道路的距离
			this.c_side = seq[18];
			String xy = seq[25];
			String xys = xy.substring(xy.indexOf("(") +1, xy.indexOf(")"));
			String [] tmp = xys.trim().split(" ");
			this.point = new LatLngPoint(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]));
			Dimension d = new Dimension(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]));
			this.mapID = d.getMapId();
			return true;
		}
		return false;
	}



	public static void main(String[] args) {
		String s = "10019768,poi_point,加油站,,,545407,4,汽车,40,加油站,4080,加油站(代表),,,114.99174,36.04101,2,13872609,R,410000,河南省,410900,濮阳市,410922,清丰县,Point(114.99164 36.04109)";
		POIPoint p = new POIPoint();
		p.fromString(s);
		System.out.println(p.toString());
	}

}
