package gis.shape;

import gis.shape.sub.Pair;
import gis.shape.sub.Point;
import gis.util.GisUtil;
import gis.util.Separtor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Road2 {
	public final static double MAX_RANGE = 35.0;
	public final static String listSep = ";";
	public final static String Sep = ",";
	public final static String MARK = "road_data";
	public final static String gridString = "%03.0f%03.0f";
	public final static double MIN_DISTANCE = 500;
	public String id;
	public String pathName;
	public String route_Kind;
	public String otherName;
	public String usedName;
	public String kind;
	public int toll;
	public String useFeeType;
	public int spdLmtS2E;
	public int spdLmtE2S;
	public String mapId;
	public double width;
	public double length;
	public int Kind_num;
	public String  Kind;
	int  direction;
	String  SnodeID;
	String   EnodeID;
	String pointStr;
	String firstName;
	
	/*
	 * pointList //经纬度点
	 */
	
	public ArrayList<Point> pointList = new ArrayList<Point>(); //经纬度点
	public ArrayList<Point3D> pList = new ArrayList<Point3D>(); //3D球面点
	public ArrayList<Point3D> nList = new ArrayList<Point3D>(); //两个点连线与球心形成的面的法向量
	public ArrayList<Double> l = new ArrayList<Double>();     //

	public String getName(){
		return this.pathName;
	}
	
	public String getFirstName(){
		return this.firstName;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public void setFirstName(){
		String allName = "";//Integer
		HashMap<String,Integer> nameWeightHM = new HashMap<String,Integer >();
		if(this.pathName.equals("") &&this.route_Kind.equals("")){
			this.firstName = "";
			return ;
		}else if(this.pathName.equals("")){
			allName = this.route_Kind;
		}else if(this.route_Kind.equals("")){
			allName = this.pathName;
		}else{
			allName = this.pathName + Separtor.lay2Sep + this.route_Kind;
		}
		String [] allNameArr = allName.split(Separtor.lay2Sep);
		for(String s:allNameArr){
			if(s.equals("")){
				continue;
			}else{
				String [] nameWeight = s.split(Separtor.lay3Sep);
				if(nameWeight.length == 2){
					nameWeightHM.put(nameWeight[0], Integer.parseInt(nameWeight[1]));
				}
			}
		}
		if(nameWeightHM.size() == 0){
			this.firstName = "";
			return ;
		}
		int min = 100;
		String maxName = "";
		for(String key:nameWeightHM.keySet()){
			if (nameWeightHM.get(key).intValue() < min){
				maxName = key;
				min = nameWeightHM.get(key).intValue();
			}
		}
		this.firstName = maxName;
	}
	
	
	
	public int getDirection(){
		return direction;
	}
	/**
	 * 获取当前道路的link的方向
	 * 但是需要测试一下，
	 * @return
	 */
	public double getDirectAngle(){
		return GisUtil.getAngle(this.pointList.get(0),this.pointList.get(pointList.size()-1));
	}
	
	/**
	 * 判断一条道路是不是另一条的道路的反向道路
	 * 条件：
	 * 1、起始点终止点的连线方向相反
	 * 2、一条道路上的点到另一条道路的距离部分相等。不会大于35米，当前这样定时为了可以解决道路匹配的问题。
	 * 3、道路名称相同
	 * @param r2
	 * @return 
	 */
	public boolean isOppositeRoadOf(Road2 r2){
		ArrayList<Point> pl = this.pointList;
		Point a = this.pointList.get(0);
		
		for (Point p:r2.pointList){
			Pair angleDis = getAngleAndDistance(p);
			if (angleDis.type.equals("1")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断道路是不是上下行分离的道路
	 * @return
	 */
	public boolean isUpDownWardSplit(){
		HashSet<String> kinds = getKindHS();
		if (kinds.contains("02")){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 辅路判断
	 * @return
	 */
	public boolean isUpDownWardSubRoad(){
		if (this.kind.endsWith("0a")){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 辅路判断
	 * @return
	 */
	public boolean isSubRoad(){
		HashSet<String> kinds = getKindHS();
		if (kinds.contains("0a")){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 获取道路特征数组
	 * 
	 * @return
	 */
	public HashSet<String> getKindHS(){
		HashSet<String> ret = new HashSet<String>();
		String [] kinds = this.Kind.split("\\|");
		for(String k: kinds){
			if(k.length()==4){
				k = k.substring(2,4);
				ret.add(k);
			}
		}
		return ret;
	}
	
	/**
	 * 获取道路等级特征数组
	 * 
	 * @return
	 */
	public HashSet<String> getRoadLevelHS(){
		HashSet<String> ret = new HashSet<String>();
		String [] kinds = Kind.split("\\|");
		for(String k: kinds){
			if(k.length()==4){
				k = k.substring(0,2);
				ret.add(k);
			}
		}
		return ret;
	}
	
	
	public String getId(){
		return this.id;
	}
	
	public String getMapId(){
		return this.mapId;
	}
	
	public String getSnodeID(){
		return this.SnodeID;
	}
	
	public String getEnodeID(){
		return this.EnodeID;
	}
	
	public String getPointStr(){
		return this.pointStr;
	}
	
	public String getPointStrFromHS(){
		String ret = "LINESTRING (";
		for(Point p:this.pointList){
			ret += p.m_Longitude +" "+p.m_Latitude +", ";
		}
		ret = ret.substring(0,ret.lastIndexOf(","));
		ret += ")";
		return ret;
	}
	
	/**
	 * 计算点到道路的投影点，以及点到道路的垂直距离
	 * @param p
	 * @param p1
	 * @param MIN_DISTANCE
	 *            0 所有 -1 所有and不取两端端点
	 * @return
	 */
	public Pair getPointAndDistance(Point p, Point3D p1, double MIN_DISTANCE) {
		if (this.pointList.size() == 0)
			return new Pair(new Point(0, 0), 0, 0, "D");
		String t = "";
		if (MIN_DISTANCE > 0d) {
			double d1 = GisUtil.getDistance(pointList.get(0), p);
			double d2 = GisUtil.getDistance(pointList.get(pointList.size() - 1), p);
			if (d1 - this.width - this.length > MIN_DISTANCE && d2 - this.width - this.length > MIN_DISTANCE) {
				return new Pair(null, Double.MAX_VALUE, 0, "C");// pair(点  距离  索引  类型)
			}
		}
		Point rep = null;
		double dis = Double.MAX_VALUE;
		Point d = null;
		int index = 0;
		t = "B";
		for (int i = 0; i < pointList.size(); i++) {
			double diss = GisUtil.getDistance(p, pointList.get(i));
			if (diss < dis) {
				dis = diss;
				index = i;
				rep = pointList.get(i);
			}
		}

		int i = index;
//		System.out.println("road1 内部： index of i is :"+i);
		if (i > 0) {
			d = GisUtil.getProjectedPoing3(nList.get(i - 1), p1);
			double da = GisUtil.getDistance(pointList.get(i - 1), d);
			double db = GisUtil.getDistance(pointList.get(i), d);
			double ab = l.get(i - 1);
//			System.out.println("road1 内部 if (i > 0)： Math.abs(ab - da - db) :"+Math.abs(ab - da - db));
//			System.out.println("road1 内部 if (i > 0)： p :" + p.getString());
//			System.out.println("road1 内部 if (i > 0)： i+1 :" + pointList.get(i - 1).getString());
//			System.out.println("road1 内部 if (i > 0)： i :" + pointList.get(i).getString());
//			System.out.println("road1 内部 if (i > 0)： d :" + d.getString());
//			System.out.println("road1 内部if (i > 0)： GisUtil.getDistance(pointList.get(i - 1), p) :"+GisUtil.getDistance(pointList.get(i - 1), p));
//			System.out.println("road1 内部if (i > 0)： GisUtil.getDistance(pointList.get(i), p) :"+GisUtil.getDistance(pointList.get(i), p));
			if (Math.abs(ab - da - db) <= 0.1) {
//				System.out.println("road1 内部if (i > 0)： GisUtil.getDistance(d, p) :"+GisUtil.getDistance(d, p));
//				System.out.println("road1 内部if (i > 0)： dis :"+dis);
				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					dis = diss;
					rep = d;
					index = i;
				}
				t = "A";
			}
		}
//		System.out.println("road1 内部： index of pointList.size is :"+pointList.size());
//		System.out.println("road1 内部： index of i is :"+i);

		if (i < pointList.size() - 1) {
			d = GisUtil.getProjectedPoing3(nList.get(i), p1);
			double da = GisUtil.getDistance(pointList.get(i), d);
			double db = GisUtil.getDistance(pointList.get(i + 1), d);
			double ab = l.get(index);
//			System.out.println("road1 内部if (i < pointList.size() - 1)： Math.abs(ab - da - db) :" + Math.abs(ab - da - db));
//			System.out.println("road1 内部 if (i < pointList.size() - 1)： p :" + p.getString());
//			System.out.println("road1 内部 if (i < pointList.size() - 1)： i+1 :" + pointList.get(i + 1).getString());
//			System.out.println("road1 内部 if (i < pointList.size() - 1)： i :" + pointList.get(i).getString());
//			System.out.println("road1 内部 if (i < pointList.size() - 1)： d :" + d.getString());

			if (Math.abs(ab - da - db) <= 0.1) {
//				System.out.println("road1 内部if if (i < pointList.size() - 1)： GisUtil.getDistance(d, p) :" + GisUtil.getDistance(d, p));
//				System.out.println("road1 内部if if (i < pointList.size() - 1)： dis :" + dis);

				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					dis = diss;
					rep = d;
					index = i + 1;
				}
				t = "A";
			}
		}
		if (MIN_DISTANCE == -1) {
			if (i == 0) {
				d = GisUtil.getProjectedPoing3(nList.get(i), p1);
				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					t = "A";
					dis = diss;
					rep = d;
					index = i;
				}
			} else if (i == pointList.size() - 1) {
				d = GisUtil.getProjectedPoing3(nList.get(i - 1), p1);
				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					t = "A";
					dis = diss;
					rep = d;
					index = i;
				}
			}
		}

		return new Pair(rep, dis, index, t);// pair(点  距离  索引  类型)
	}

	/**
	 * @param p
	 * @param minDis
	 *            0所有 -1 匹配所有点，在道路外的不再拉回到两端端点上
	 * @return 0 点在路上 1 在路右侧 -1 在路左侧
	 *         pair(key投影点，value1离路距离，value2在路的左右侧，type匹配模式) 匹配模式：A 匹配到路网的道路段上 B
	 *         匹配到路网的点上 C 距离过远 >MIN_DISTANCE
	 */
	public Pair getSideAndDistance(Point p, double minDis) {
		// 0 搜索所有点，不进行剪枝 其他值则为 最短距离
		Point3D pp = new Point3D(p);
		Pair pai;
		pai = this.getPointAndDistance(p, pp, minDis);
		Point p1 = pai.getKey();
		double angle = 0d;
		if (p.m_Latitude == p1.m_Latitude && p.m_Longitude == p1.m_Longitude) {
			angle = 0d;
		} else {
			int index = (int) pai.getValue2();
			double angle1;
			double angle2;
			if (index == 0) {
				angle1 = GisUtil.getAngle(this.pointList.get(0), p);
				angle2 = GisUtil.getAngle(this.pointList.get(0), this.pointList.get(1));
			} else {
				angle1 = GisUtil.getAngle(this.pointList.get(index - 1), p);
				angle2 = GisUtil.getAngle(this.pointList.get(index - 1), this.pointList.get(index));
			}
			if (angle2 % 180 == angle1 % 180) {
				angle = 0;
			} else if (angle2 > 180) {
				if (angle1 < angle2 && angle1 > angle2 - 180)
					angle = -1;
				else
					angle = 1;
			} else {
				if (angle1 > angle2 && angle1 < angle2 + 180)
					angle = 1;
				else
					angle = -1;
			}
		}
		pai.setValue2(angle);
		return pai;
	}

	/**
	 * 
	 * @param p 待计算的点
	 * @return 返回投影点、点p到投影点距离、投影点在道路上点的下标、道路与正北方向的夹角(对应下标位置的两点与正北方向的夹角)
	 */
	public Pair getAngleAndDistance(Point p) {
		Point3D p1 = new Point3D(p);
		Pair pai = this.getPointAndDistance(p, p1, 500);
		int index = (int) pai.getValue2(); //value是索引
		String c = pai.getType(); //c是类型，
		if (!c.equals("C")) { 
			double angle;
			if (index == 0)
				angle = GisUtil.getAngle(pointList.get(0), pointList.get(1));
			else
				angle = GisUtil.getAngle(pointList.get(index - 1), pointList.get(index));
			pai.setValue2(angle);
		}
		return pai;
	}

	public boolean fromSting(String s) {
		String[] seq = s.split(",",19);
		if (seq.length < 19)
			return false;
		this.id = seq[0];
		this.pathName = seq[1];
		this.route_Kind = seq[2];
		this.otherName = seq[3];
		this.usedName = seq[4];
		this.mapId = seq[5];
		this.kind = seq[6];
		this.width = Double.parseDouble(seq[7]) / 10.0;
		this.toll = Integer.parseInt(seq[8]);
		this.length = Double.parseDouble(seq[9]);
		this.useFeeType = seq[10];
		if (!seq[11].equals(""))
			this.spdLmtS2E = Integer.parseInt(seq[11]);
		else
			this.spdLmtS2E = 0;
		if (!seq[12].equals(""))
			this.spdLmtE2S = Integer.parseInt(seq[12]);
		else
			this.spdLmtE2S = 0;
		this.Kind_num = Integer.parseInt(seq[13]);
		this.Kind = seq[14];
		this.direction = Integer.parseInt(seq[15]);
		this.SnodeID = seq[16];
		this.EnodeID = seq[17];
		setFirstName();
		for (String ss : seq[18].split(Road.listSep)) {
			String[] tmp = ss.split(GisUtil.LatLngPointSep);
			this.pointList.add(new Point(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1])));
		}
		for (String ss : seq[19].split(Road.listSep)) {
			Point3D p = new Point3D();
			p.fromString(ss);
			this.pList.add(p);
		}
		for (String ss : seq[20].split(Road.listSep)) {
			Point3D p = new Point3D();
			p.fromString(ss);
			this.nList.add(p);
		}
		for (String ss : seq[21].split(Road.listSep)) {
			this.l.add(Double.parseDouble(ss));
		}
		return true;
	}
	
	public boolean fromBeginString(String s) {
		String[] seq = s.split(",",24);
		if (seq.length < 24)
			return false;
		this.id = seq[0];
		this.pathName = seq[1];
		this.route_Kind = seq[2];
		this.otherName = seq[3];
		this.usedName = seq[4];
		this.kind = seq[6];
		this.width = Double.parseDouble(seq[7])/ 10.0;;
		this.toll = Integer.parseInt(seq[8]);
		this.length = Double.parseDouble(seq[9]);
		this.useFeeType = seq[10];
		if (!seq[11].equals(""))
			this.spdLmtS2E = Integer.parseInt(seq[11]) *10;
		else
			this.spdLmtS2E = 0;
		if (!seq[12].equals(""))
			this.spdLmtE2S = Integer.parseInt(seq[12]) *10;
		else
			this.spdLmtE2S = 0;
		/**
		this.Kind_num = Integer.parseInt(seq[13]);
		this.Kind = seq[14];
		this.direction = Integer.parseInt(seq[15]);
		this.SnodeID = seq[16];
		this.EnodeID = seq[17];
		this.pointStr = seq[18];
		**/
		add2PointList(seq[23]);
		return true;
	}
	
	
	public void add2PointList(String content){
		String xys = content.substring(content.indexOf("(") +1, content.indexOf(")"));
		String [] xyArr = xys.split(";");
		for (String xy :xyArr){
			String [] tmp = xy.trim().split(" ");
			//添加mapid
			if(this.mapId==null || "".equals(this.mapId)) {
				Dimension d = new Dimension(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]));
				this.mapId = d.getMapId();
			}
			Point point = new Point(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]));
			this.pointList.add(point);
			this.pList.add(new Point3D(new Point(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1]))));
		}
		for (int i = 1; i < this.pointList.size(); i++) {
			double dis = GisUtil.getDistance(this.pointList.get(i - 1),
					this.pointList.get(i));
			this.l.add(dis); //l中存放的是当前点和上一个点的距离
			Point3D n = this.pList.get(i - 1).cross(this.pList.get(i));
			n.standard();
			this.nList.add(n); //nlist中存放一个link中的当前点与上一个点的单位化的法向量
		}
		/**
		//计算点到道路的投影点和距离
		//116.79074 39.7781
		//Point p = new Point(116.79074,39.7781);
		Point p = new Point(116.79066,39.77826);
		Point3D p1 = new Point3D(p);
		Pair pair = this.getPointAndDistance(p, p1, 0);
		System.out.println(pair.key.m_Longitude+","+pair.key.m_Latitude+","+pair.value1);
		**/
	}
	
	
	public Road2(){}
	public Road2(String s){
		fromBeginString(s);
		//setFirstName();
	}

	public String toString() {
		return this.getDetailString() + Separtor.lay1Sep + this.getPointStrings();
	}

	public String getDetailString() {
		String tmp = "";
		tmp += this.id + Separtor.lay1Sep;
		tmp += Road.MARK + Separtor.lay1Sep;
		tmp += this.pathName + Separtor.lay1Sep;
		tmp += this.route_Kind + Separtor.lay1Sep;
		tmp += this.otherName + Separtor.lay1Sep;
		tmp += this.usedName + Separtor.lay1Sep;
		tmp += this.mapId + Separtor.lay1Sep;
		tmp += this.kind + Separtor.lay1Sep;
		tmp += String.format("%.0f", (this.width * 10)) + Separtor.lay1Sep;
		tmp += this.toll + Separtor.lay1Sep;
		tmp += this.length + Separtor.lay1Sep;
		tmp += this.useFeeType + Separtor.lay1Sep;
		tmp += this.spdLmtS2E + Separtor.lay1Sep;
		tmp += this.spdLmtE2S;
		return tmp;
	}
	
	public String getRMDetailString() {
		String tmp = "";
		tmp += this.id + Separtor.lay1Sep;
		//tmp += Road.MARK + Separtor.lay1Sep;
		tmp += this.pathName + Separtor.lay1Sep;
		tmp += this.route_Kind + Separtor.lay1Sep;
		tmp += this.otherName + Separtor.lay1Sep;
//		tmp += this.usedName + Separtor.lay1Sep;
		tmp += this.firstName + Separtor.lay1Sep;
		tmp += this.mapId + Separtor.lay1Sep;
		tmp += this.kind + Separtor.lay1Sep;
		tmp += String.format("%.0f", (this.width * 10)) + Separtor.lay1Sep;
		tmp += this.toll + Separtor.lay1Sep;
		tmp += this.length + Separtor.lay1Sep;
		tmp += this.useFeeType + Separtor.lay1Sep;
		tmp += this.spdLmtS2E + Separtor.lay1Sep;
		tmp += this.spdLmtE2S + Separtor.lay1Sep;
		//tmp += this.Kind_num + Separtor.lay1Sep;
		tmp += this.Kind + Separtor.lay1Sep;
		tmp += this.direction + Separtor.lay1Sep;
		//tmp += this.SnodeID + Separtor.lay1Sep;
		//tmp += this.EnodeID + Separtor.lay1Sep; 起始点终止点在路网匹配中已经没有任何意义
		return tmp;	
	}	
	
	public String toStr(){
		String ret = getDetailString();
		ret+= this.getPointStrFromHS() + Separtor.lay1Sep;
		return ret;
	}

	public String toStr(String s){
		String ret = getDetailString();
		ret+= s + Separtor.lay1Sep;
		ret+= this.getPointStrFromHS() + Separtor.lay1Sep;
		return ret;
	}
	
	/**
	 * 单独为生成RoadMatch所使用的toString函数
	 * @param s
	 * @return
	 */
	public String toRMStr(String s){
		String ret = getRMDetailString();
		ret+= s + Separtor.lay1Sep;
		ret+= this.getPointStrFromHS() + Separtor.lay1Sep;
		return ret;
	}
	
	public String getLatLngString() {
		String tmp = "";
		for (Point p : this.pointList) {
			tmp += p.getString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;

	}

	public String getPointStrings() {
		String tmp = "";
		for (Point p : this.pointList) {
			tmp += p.getString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1) + Separtor.lay1Sep;
		for (Point3D p : this.pList) {
			tmp += p.toString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1) + Separtor.lay1Sep;
		for (Point3D p : this.nList) {
			tmp += p.toString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1) + Separtor.lay1Sep;
		for (double d : this.l) {
			tmp += String.format("%.6f%s", d, Road.listSep);
		}
		tmp = tmp.substring(0, tmp.length() - 1);
		return tmp;
	}

	public HashSet<String> getGridkeys() {
		HashSet<String> set = new HashSet<String>();
		for (Point p : this.pointList) {
			String key = String.format(Road.gridString, (p.m_Longitude - (int) p.m_Longitude) * 1000,
					(p.m_Latitude - (int) p.m_Latitude) * 1000);
			set.add(key);
		}
		return set;
	}

	public static void main(String[] args) throws IOException {
		/*String line = "12570761,|京港澳高速公路,|Ｇ４,,|石安高速公路|京珠高速公路,j50f035005,0,130,1,419.0,,1200,,2,"
				+ "0002|000c,2,2090381,10675850,LINESTRING (114.58896 37.1067, 114.58887 37.10627, "
				+ "114.58854 37.10467, 114.58852 37.10454, 114.58849 37.10445, 114.58846 37.10427, "
				+ "114.58845 37.10422, 114.58837 37.10387, 114.58815 37.10299),";*/
		//String line = "1000182,福西街,0,,,605624,8,30,2,3.0,,15,15,1,0814,1,688392,694229,5,110113,110113,0,7,LINESTRING (116.61725 40.21235;116.61726 40.21238)";
		/*BufferedReader br = new BufferedReader(new FileReader(new File("E:\\chenlly\\data\\road.txt"))); 
		String line = ""; 
		while((line = br.readLine()) != null){ 
			Road2 r = new Road2(line);
			System.out.println(r.toString());
		}*/
		String line = "268505,X304|漷马路,5|0,,,595656,4,55,2,142.0,,,70,1,0402,3,191550,200609,3,110112,110112,0,5,LINESTRING (116.78984 39.77766;116.79087 39.77817;116.79124 39.77834)";
		Road2 r = new Road2(line);
		System.out.println(r.toString());
	}
}

