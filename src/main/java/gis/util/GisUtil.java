package gis.util;

import gis.shape.sub.Point;
import gis.shape.Point3D;

import java.math.BigDecimal;
import java.util.ArrayList;


public class GisUtil {
	public static double RC = 6378137; //长半轴
	public static double RJ = RC;   //短半轴
	//static double RJ = 6356755.00;   //短半轴
	// static double RJ=6356725;
	static final int Decimals = 50;
	static final int latlonLength = 7;
	public static final String pointString = "%.8f:%.8f:%.8f:%.8f";
	public static final String pointSep=":";
	public static final String LatLngPointString = "%.6f:%.6f";
	public static final String LatLngPointSep=":";
	

	public static void main(String[] args) {
		utilTest();
	}
	
	
	
	/*
	 *计算距离角度公式测试 
	 */
	public static void utilTest() {
		ArrayList<Point> pl1 = new ArrayList<Point>();
		ArrayList<Point> pl2 = new ArrayList<Point>(); 
		//Point A = new Point(105.41228, 28.83402);
		Point A = new Point(105.40228, 28.85402);
		//Point B = new Point(105.4124, 28.83406);
		Point B = new Point(105.46228, 28.85402);
		System.out.println("A点的经纬度" + A.m_Longitude + "," + A.m_Latitude);
		System.out.println("B点的经纬度" + B.m_Longitude + "," + B.m_Latitude);
		
		System.out.println(getDistance(A, B));
		// LatLngPoint C = new LatLngPoint(0.5 * (A.m_Longitude + B.m_Longitude), 0.5 * (A.m_Latitude + B.m_Latitude));
		Point D;
		//Point C = new Point(105.412317, 28.833657);
		Point C = new Point(106.492317, 28.823657);
		System.out.println("C点的经纬度"+C.m_Longitude + "," + C.m_Latitude);
		// System.out.println(D.m_Longitude + "," + D.m_Latitude);
		// System.out.println(getDistance(C, D));
		D = getProjectedPoing1(A, B, C);
		System.out.println("AB的正北方向夹角" + getAngle(A, B));
		System.out.println("BC的正北方向夹角" + getAngle(B, C));
		System.out.println("CD的正北方向夹角" + getAngle(C, D));
		
		System.out.println("D点的经纬度"+D.m_Longitude + "," + D.m_Latitude);
		System.out.println("AB: " + getDistance(A, B));
		System.out.println("AB: " + (getDistance(B, D) - getDistance(A, D)));
		System.out.println("DA: " + getDistance(A, D));
		System.out.println("DB: " + getDistance(B, D));
		System.out.println("CD: " + getDistance(C, D));
		System.out.printf("C点到D点的距离: %.6f\n", getDirctDis(A, B, C));
		pl1.add(A);pl1.add(B);pl1.add(C);pl1.add(D);
		System.out.println("ABCD轨迹：" + pointsToLocus(pl1));
		
		D = getProjectedPoing2(A, B, C);
		System.out.println("D点的经纬度"+D.m_Longitude + "," + D.m_Latitude);
		System.out.println("AB: " + getDistance(A, B));
		System.out.println("AB: " + (getDistance(B, D) - getDistance(A, D)));
		System.out.println("DA: " + getDistance(A, D));
		System.out.println("DB: " + getDistance(B, D));
		System.out.println("CD: " + getDistance(C, D));
		System.out.printf("DirctDis: %.6f\n", getDirctDis(A, B, C));
		pl2.add(A);pl2.add(B);pl2.add(C);pl2.add(D);
		System.out.println("ABCD轨迹2：" + pointsToLocus(pl2));

	}
	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用施密特正交化方法中，计算第三个方向向量C的方法计算投影向量
	 * 
	 * @param A
	 *            道路起点A
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */
	
	public static String pointsToLocus(ArrayList<Point> pl){
		String ret = "";
		for (Point p :pl){
			ret += p.m_Longitude + "," + p.m_Latitude + ";";
		}
		return ret;
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用施密特正交化方法中，计算第三个方向向量C的方法计算投影向量
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */
	public static Point getProjectedPoing1(Point A, Point B, Point C) {
		Point3D a = new Point3D(A);
		Point3D b = new Point3D(B);
		return getProjectedPoing1(a, b, C);
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用施密特正交化方法中，计算第三个方向向量C的方法计算投影向量
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */

	public static Point getProjectedPoing1(Point3D A, Point3D B, Point C) {
		Point3D c = new Point3D(C);
		return getProjectedPoing1(A, B, c);
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用施密特正交化方法中，计算第三个方向向量C的方法计算投影向量
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */

	public static Point getProjectedPoing1(Point3D A, Point3D B, Point3D C) {
		Point3D a1 = A;
		Point3D a2 = B;
		Point3D a3 = C;
		Point3D b1 = a1;
		// point b2 = a2.sub(
		// b1.mult(a2.dotProduct(b1).divide(b1.dotProduct(b1),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP)));
		// return b1.mult(a3.dotProduct(b1).divide(b1.dotProduct(b1),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP)) 
		//.add(b2.mult( a3.dotProduct(b2).divide(b2.dotProduct(b2), 
		//LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP))).toLatLngPonit();
		Point3D b2 = a2.sub(b1.mult(a2.dotProduct(b1) / b1.dotProduct(b1)));
		return b1.mult(a3.dotProduct(b1) / b1.dotProduct(b1)).add(b2.mult(a3.dotProduct(b2) / 
				b2.dotProduct(b2))).toLatLngPonit();
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用向量的方法计算
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */

	public static Point getProjectedPoing2(Point A, Point B, Point C) {
		Point3D a = new Point3D(A);
		Point3D b = new Point3D(B);
		return getProjectedPoing2(a, b, C);
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用向量的方法计算
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */

	public static Point getProjectedPoing2(Point3D A, Point3D B, Point C) {
		Point3D c = new Point3D(C);
		return getProjectedPoing1(A, B, c);
	}

	/**
	 * 求C点在道路连线AB两点上映射点D的经纬度 使用向量的方法计算
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return C点在AB所在大圆的映射点
	 */

	public static Point getProjectedPoing2(Point3D A, Point3D B, Point3D C) {
		Point3D a = A;
		Point3D b = B;
		Point3D c = C;
		Point3D n = a.cross(b);
		n.standard();
		return c.sub(n.mult(n.dotProduct(c))).toLatLngPonit();
	}

	/**
	 * 计算向量C在法向量为n的平面上(过0点)的投影
	 * 
	 * @param n
	 *            平面的法向量
	 * @param C
	 *            待匹配点C
	 * @return C点在AB所在大圆的映射点
	 */
	public static Point getProjectedPoing3(Point3D n, Point C) {
		Point3D c = new Point3D(C);
		return c.sub(n.mult(n.dotProduct(c))).toLatLngPonit();
	}

	/**
	 * 计算向量C在法向量为n的平面上(过0点)的投影
	 * 
	 * @param n
	 *            平面的法向量
	 * @param C
	 *            待匹配点C
	 * @return C点在AB所在大圆的映射点
	 */
	public static Point getProjectedPoing3(Point3D n, Point3D C) {
		return C.sub(n.mult(n.dotProduct(C))).toLatLngPonit();
	}

	/**
	 * 求点C到道路AB所在弧的垂直距离，即C点与其在AB弧上映射点D的距离
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return CD点的距离
	 */
	public static double getDirctDis(Point A, Point B, Point C) {
		Point3D a = new Point3D(A);
		Point3D b = new Point3D(B);
		return getDirctDis(a, b, C);
	}

	/**
	 * 求点C到道路AB所在弧的垂直距离，即C点与其在AB弧上映射点D的距离
	 * 这里没有进行优化，只是调用前面的方法获取点D的经纬度，然后根据经纬度计算公式计算出距离
	 * 
	 * @param A
	 *            道路起点A
	 * @param B
	 *            道路终点B
	 * @param C
	 *            待匹配的点C
	 * @return CD点的距离
	 */
	public static double getDirctDis(Point3D A, Point3D B, Point C) {
		Point d = getProjectedPoing1(A, B, C);
		return getDistance(C, d);
	}

	/**
	 * 求B点经纬度
	 * 
	 * @param A
	 *            已知点的经纬度，
	 * @param distance
	 *            AB两地的距离 单位 m
	 * @param angle
	 *            AB连线与正北方向的夹角（0~360）
	 * @return B点的经纬度
	 */
	public static Point getLatLngPoint(Point A, double distance, double angle) {

		double dx = distance * Math.sin(Math.toRadians(angle));
		double dy = distance * Math.cos(Math.toRadians(angle));

		double bjd = new BigDecimal((dx / A.Ed + A.m_RadLo) * 180. / Math.PI).setScale(7, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double bwd = new BigDecimal((dy / A.Ec + A.m_RadLa) * 180. / Math.PI).setScale(7, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return new Point(bjd, bwd);
	}

	/**
	 * 获取AB弧线的长度
	 * 
	 * @param A
	 *            A点的经纬度
	 * @param B
	 *            B点的经纬度
	 * @return AB弧线的长度
	 */
	public static double getDistance(Point A, Point B) {
		double radLat1 = A.m_RadLa;
		double radLat2 = B.m_RadLa;
		double a = radLat1 - radLat2;
		double b = A.m_RadLo - B.m_RadLo;
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * Point.Rc;
		// s = Math.round(s * 10000) / 10000.0;
		return s;
	}

	/**
	 * 获取AB连线与正北方向的角度
	 * 
	 * @param A
	 *            A点的经纬度
	 * @param B
	 *            B点的经纬度
	 * @return AB连线与正北方向的角度（0~360）
	 */
	public static double getAngle(Point A, Point B) {
		double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
		double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
		double angle = 0.0;
		angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
		double dLo = B.m_Longitude - A.m_Longitude;
		double dLa = B.m_Latitude - A.m_Latitude;
		if (dLo > 0 && dLa <= 0) {
			angle = (90. - angle) + 90;
		} else if (dLo <= 0 && dLa < 0) {
			angle = angle + 180.;
		} else if (dLo < 0 && dLa >= 0) {
			angle = (90. - angle) + 270;
		}
		return angle;
	}

}