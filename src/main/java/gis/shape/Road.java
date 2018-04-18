package gis.shape;

import gis.shape.sub.Pair;
import gis.shape.sub.Point;
import gis.util.GisUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;



public class Road {
	public final static double MAX_RANGE = 35.0;
	public final static String listSep = ";";
	public final static String Sep = "#";
	public final static String MARK = "road_data";
	public final static String gridString = "%03.0f%03.0f";
	public final static double MIN_DISTANCE = 500;
	public String ID;
	public String pathName;
	public String routeKind;
	public String otherName;
	public String usedName;
	public String kind;
	public int toll;
	public String useFeeType;
	public int spdLmtS2E;
	public int spdLmtE2S;
	public String mapID;
	public double width;
	public double length;
	public ArrayList<Point> pointList = new ArrayList<Point>();
	public ArrayList<Point3D> pList = new ArrayList<Point3D>(); //
	public ArrayList<Point3D> nList = new ArrayList<Point3D>(); //nlist中存放一个link中的当前点与上一个点的单位化的法向量
	public ArrayList<Double> l = new ArrayList<Double>(); //

	/**
	 * 
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
				return new Pair(null, Double.MAX_VALUE, 0, "C");
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
		if (i > 0) {
			d = GisUtil.getProjectedPoing3(nList.get(i - 1), p1);
			double da = GisUtil.getDistance(pointList.get(i - 1), d);
			double db = GisUtil.getDistance(pointList.get(i), d);
			double ab = l.get(i - 1);
			if (Math.abs(ab - da - db) <= 0.1) {
				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					t = "A";
					dis = diss;
					rep = d;
					index = i;
				}
			}
		}
		if (i < pointList.size() - 1) {
			d = GisUtil.getProjectedPoing3(nList.get(i), p1);
			double da = GisUtil.getDistance(pointList.get(i), d);
			double db = GisUtil.getDistance(pointList.get(i + 1), d);
			double ab = l.get(index);
			if (Math.abs(ab - da - db) <= 0.1) {
				double diss = GisUtil.getDistance(d, p);
				if (dis > diss) {
					t = "A";
					dis = diss;
					rep = d;
					index = i + 1;
				}
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
	 * 
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
		String[] seq = s.split(",");
		if (seq.length < 16)
			return false;
		this.ID = seq[0];
		this.pathName = seq[2];
		this.routeKind = seq[3];
		this.otherName = seq[4];
		this.usedName = seq[5];
		this.mapID = seq[6];
		this.kind = seq[7];
		this.width = Double.parseDouble(seq[8]) / 10.0;
		this.toll = Integer.parseInt(seq[9]);
		this.length = Double.parseDouble(seq[10]);
		this.useFeeType = seq[11];
		if (!seq[12].equals(""))
			this.spdLmtS2E = Integer.parseInt(seq[12]);
		else
			this.spdLmtS2E = 0;
		if (!seq[13].equals(""))
			this.spdLmtE2S = Integer.parseInt(seq[13]);
		else
			this.spdLmtE2S = 0;
		for (String ss : seq[14].split(Road.listSep)) {
			String[] tmp = ss.split(GisUtil.LatLngPointSep);
			this.pointList.add(new Point(Double.parseDouble(tmp[0]), Double.parseDouble(tmp[1])));
		} //pointList 存放经纬度点
		for (String ss : seq[15].split(Road.listSep)) {
			Point3D p = new Point3D();
			p.fromString(ss);
			this.pList.add(p);
		} //
		for (String ss : seq[16].split(Road.listSep)) {
			Point3D p = new Point3D();
			p.fromString(ss);
			this.nList.add(p);
		}
		for (String ss : seq[17].split(Road.listSep)) {
			this.l.add(Double.parseDouble(ss));
		}
		return true;
	}

	public String toString() {
		return this.getDetailString() + Road.Sep + this.getPointStrings();
	}

	public String getDetailString() {
		String tmp = "";
		tmp += this.ID + Road.Sep;
		tmp += Road.MARK + Road.Sep;
		tmp += this.pathName + Road.Sep;
		tmp += this.routeKind + Road.Sep;
		tmp += this.otherName + Road.Sep;
		tmp += this.usedName + Road.Sep;
		tmp += this.mapID + Road.Sep;
		tmp += this.kind + Road.Sep;
		tmp += String.format("%.0f", (this.width * 10)) + Road.Sep;
		tmp += this.toll + Road.Sep;
		tmp += this.length + Road.Sep;
		tmp += this.useFeeType + Road.Sep;
		tmp += this.spdLmtS2E + Road.Sep;
		tmp += this.spdLmtE2S;

		return tmp;

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
		tmp = tmp.substring(0, tmp.length() - 1) + Road.Sep;
		for (Point3D p : this.pList) {
			tmp += p.toString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1) + Road.Sep;
		for (Point3D p : this.nList) {
			tmp += p.toString() + Road.listSep;
		}
		tmp = tmp.substring(0, tmp.length() - 1) + Road.Sep;
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
		String line = "8589508,|广砚高速公路|广昆高速公路,|Ｇ８０,,,f48f004024,0,55,1,21.0,,,1000,104.934190:23.745650;104.934300:23.745680;104.934390:23.745700,-0.10377370:0.38907576:-0.91534205:1.00000000;-0.10377457:0.38907602:-0.91534184:1.00000000;-0.10377527:0.38907616:-0.91534170:1.00000000,0.34651760:0.87679339:0.33340502:1.00000000;0.26031658:0.89885485:0.35255530:1.00000000,11.695433;9.436971";
		Road r = new Road();
		r.fromSting(line);
		System.out.println(r.toString());
		for (String s : r.getGridkeys()) {
			System.out.println(s);
		}
	}
}
