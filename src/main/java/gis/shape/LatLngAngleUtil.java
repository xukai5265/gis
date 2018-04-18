package gis.shape;
import java.math.BigDecimal;

public class LatLngAngleUtil {
	static double RC = 6378137;
	static double RJ = RC;
	// static double RJ=6356725;
	static final int Decimals = 50;
	static final int latlonLength = 7;
	static final String pointString = "%.8f:%.8f:%.8f:%.8f";
	static final String pointSep=":";
	static final String LatLngPointString = "%.6f:%.6f";
	static final String LatLngPointSep=":";
	

	public static void main(String[] args) {
		LatLngPoint A = new LatLngPoint(105.41228, 28.83402);
		LatLngPoint B = new LatLngPoint(105.4124, 28.83406);
		System.out.println(A.m_Longitude + "," + A.m_Latitude);
		System.out.println(B.m_Longitude + "," + B.m_Latitude);
		// System.out.println(getAngle(A, B));
		System.out.println(getDistance(A, B));
		// LatLngPoint C = new LatLngPoint(0.5 * (A.m_Longitude +
		// B.m_Longitude), 0.5 * (A.m_Latitude + B.m_Latitude));
		LatLngPoint D;
		LatLngPoint C = new LatLngPoint(105.412317, 28.833657);
		System.out.println(C.m_Longitude + "," + C.m_Latitude);
		// System.out.println(D.m_Longitude + "," + D.m_Latitude);
		// System.out.println(getDistance(C, D));
		D = getProjectedPoing1(A, B, C);
		System.out.println(D.m_Longitude + "," + D.m_Latitude);
		System.out.println("AB: " + getDistance(A, B));
		System.out.println("AB: " + (getDistance(B, D) - getDistance(A, D)));
		System.out.println("DA: " + getDistance(A, D));
		System.out.println("DB: " + getDistance(B, D));
		System.out.println("CD: " + getDistance(C, D));
		System.out.printf("DirctDis: %.6f\n", getDirctDis(A, B, C));

		D = getProjectedPoing2(A, B, C);
		System.out.println(D.m_Longitude + "," + D.m_Latitude);
		System.out.println("AB: " + getDistance(A, B));
		System.out.println("AB: " + (getDistance(B, D) - getDistance(A, D)));
		System.out.println("DA: " + getDistance(A, D));
		System.out.println("DB: " + getDistance(B, D));
		System.out.println("CD: " + getDistance(C, D));
		System.out.printf("DirctDis: %.6f\n", getDirctDis(A, B, C));

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
	public static LatLngPoint getProjectedPoing1(LatLngPoint A, LatLngPoint B, LatLngPoint C) {
		point a = new point(A);
		point b = new point(B);
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

	public static LatLngPoint getProjectedPoing1(point A, point B, LatLngPoint C) {
		point c = new point(C);
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

	public static LatLngPoint getProjectedPoing1(point A, point B, point C) {
		point a1 = A;
		point a2 = B;
		point a3 = C;
		point b1 = a1;
		// point b2 = a2.sub(
		// b1.mult(a2.dotProduct(b1).divide(b1.dotProduct(b1),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP)));
		// return b1.mult(a3.dotProduct(b1).divide(b1.dotProduct(b1),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP))
		// .add(b2.mult(
		// a3.dotProduct(b2).divide(b2.dotProduct(b2), LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP)))
		// .toLatLngPonit();
		point b2 = a2.sub(b1.mult(a2.dotProduct(b1) / b1.dotProduct(b1)));
		return b1.mult(a3.dotProduct(b1) / b1.dotProduct(b1)).add(b2.mult(a3.dotProduct(b2) / b2.dotProduct(b2)))
				.toLatLngPonit();
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

	public static LatLngPoint getProjectedPoing2(LatLngPoint A, LatLngPoint B, LatLngPoint C) {
		point a = new point(A);
		point b = new point(B);
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

	public static LatLngPoint getProjectedPoing2(point A, point B, LatLngPoint C) {
		point c = new point(C);
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

	public static LatLngPoint getProjectedPoing2(point A, point B, point C) {
		point a = A;
		point b = B;
		point c = C;
		point n = a.cross(b);
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
	public static LatLngPoint getProjectedPoing3(point n, LatLngPoint C) {
		point c = new point(C);
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
	public static LatLngPoint getProjectedPoing3(point n, point C) {
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
	public static double getDirctDis(LatLngPoint A, LatLngPoint B, LatLngPoint C) {
		point a = new point(A);
		point b = new point(B);
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
	public static double getDirctDis(point A, point B, LatLngPoint C) {
		LatLngPoint d = getProjectedPoing1(A, B, C);
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
	public static LatLngPoint getLatLngPoint(LatLngPoint A, double distance, double angle) {

		double dx = distance * Math.sin(Math.toRadians(angle));
		double dy = distance * Math.cos(Math.toRadians(angle));

		// double bjd = (dx / A.Ed + A.m_RadLo) * 180. / Math.PI;
		// double bwd = (dy / A.Ec + A.m_RadLa) * 180. / Math.PI;

		double bjd = new BigDecimal((dx / A.Ed + A.m_RadLo) * 180. / Math.PI).setScale(7, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		double bwd = new BigDecimal((dy / A.Ec + A.m_RadLa) * 180. / Math.PI).setScale(7, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return new LatLngPoint(bjd, bwd);
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
	public static double getDistance(LatLngPoint A, LatLngPoint B) {
		double radLat1 = A.m_RadLa;
		double radLat2 = B.m_RadLa;
		double a = radLat1 - radLat2;
		double b = A.m_RadLo - B.m_RadLo;
		double s = 2 * Math.asin(Math.sqrt(
				Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * LatLngPoint.Rc;
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
	public static double getAngle(LatLngPoint A, LatLngPoint B) {
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

class point {
	/**
	 * 直角坐标的点向量 通过构造函数可以直接将经纬度转换为三维直角坐标
	 */
	double x;
	double y;
	double z;
	double r;

	public String toString() {
		return String.format(LatLngAngleUtil.pointString, x, y, z, r);
	}
	public boolean fromString(String s){
		String[] slist=s.split(LatLngAngleUtil.pointSep);
		if(slist.length<4)
			return false;
		this.x=Double.parseDouble(slist[0]);
		this.y=Double.parseDouble(slist[1]);
		this.z=Double.parseDouble(slist[2]);
		this.r=Double.parseDouble(slist[3]);
		return true;
	}

	public point() {
		// x = new BigDecimal(0d);
		// y = new BigDecimal(0d);
		// z = new BigDecimal(0d);
		// r = new BigDecimal(0d);
		x = 0d;
		y = 0d;
		z = 0d;
		r = 0d;
	}

	public point(LatLngPoint P) {
		// x = BigDecimal.valueOf(Math.sin(Math.PI - P.m_RadLa) *
		// Math.cos(P.m_RadLo));
		// y = BigDecimal.valueOf(Math.sin(Math.PI - P.m_RadLa) *
		// Math.sin(P.m_RadLo));
		// z = BigDecimal.valueOf(Math.cos(Math.PI - P.m_RadLa));
		// r =
		// BigDecimal.valueOf(Math.sqrt(x.pow(2).add(y.pow(2).add(z.pow(2))).doubleValue()));
		// x = LatLngPoint.Rc * Math.sin(Math.PI - P.m_RadLa) *
		// Math.cos(P.m_RadLo);
		// y = LatLngPoint.Rc * Math.sin(Math.PI - P.m_RadLa) *
		// Math.sin(P.m_RadLo);
		// z = LatLngPoint.Rc * Math.cos(Math.PI - P.m_RadLa);
		// r = LatLngPoint.Rc * Math.sqrt(x * x + y * y + z * z);
		x = Math.sin(Math.PI - P.m_RadLa) * Math.cos(P.m_RadLo);
		y = Math.sin(Math.PI - P.m_RadLa) * Math.sin(P.m_RadLo);
		z = Math.cos(Math.PI - P.m_RadLa);
		r = Math.sqrt(x * x + y * y + z * z);
	}

	public point(BigDecimal x1, BigDecimal y1, BigDecimal z1) {
		// x = x1;
		// y = y1;
		// z = z1;
		// r =
		// BigDecimal.valueOf(Math.sqrt(x.pow(2).add(y.pow(2).add(z.pow(2))).doubleValue()));
		x = x1.doubleValue();
		y = y1.doubleValue();
		z = z1.doubleValue();
		r = Math.sqrt(x * x + y * y + z * z);
	}

	public point(double x1, double y1, double z1) {
		x = x1;
		y = y1;
		z = z1;
		r = Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * 向量相加
	 * 
	 * @param p
	 *            被加的向量
	 * @return 此向量加p后的结果
	 */
	public point add(point p) {
		if (p == null)
			return null;
		// return new point(x.add(p.x), y.add(p.y), z.add(p.z));
		return new point(x + p.x, y + p.y, z + p.z);
	}

	/**
	 * 向量相减
	 * 
	 * @param p
	 *            被减的向量
	 * @return 此向量减p后的结果
	 */
	public point sub(point p) {
		if (p == null)
			return null;
		// return new point(x.subtract(p.x), y.subtract(p.y), z.subtract(p.z));
		return new point(x - p.x, y - p.y, z - p.z);
	}

	/**
	 * 向量相加
	 * 
	 * @param d
	 *            被乘的数
	 * @return 此向量乘p后的结果
	 */
	public point mult(double d) {
		// return new point(x.multiply(BigDecimal.valueOf(d)),
		// y.multiply(BigDecimal.valueOf(d)),
		// z.multiply(BigDecimal.valueOf(d)));
		return new point(x * d, y * d, z * d);
	}

	/**
	 * 向量相加
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被乘的数
	 * @return 此向量乘p后的结果
	 */
	public point mult(BigDecimal d) {
		// return new point(x.multiply(d), y.multiply(d), z.multiply(d));
		return new point(x * d.doubleValue(), y * d.doubleValue(), z * d.doubleValue());
	}

	/**
	 * 向量除法
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被除数
	 * @return 此向量除以p后的结果
	 */
	public point div(double d) {
		if (d == 0d)
			return null;
		// return new point(x.divide(BigDecimal.valueOf(d),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP),
		// y.divide(BigDecimal.valueOf(d), LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP),
		// z.divide(BigDecimal.valueOf(d), LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP));
		return new point(x / d, y / d, z / d);
	}

	/**
	 * 向量除法
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被除数
	 * @return 此向量除以p后的结果
	 */
	public point div(BigDecimal d) {
		if (d.compareTo(BigDecimal.ZERO) == 0)
			return null;
		// return new point(x.divide(d, LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP),
		// y.divide(d, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP),
		// z.divide(d, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP));
		return new point(x / d.doubleValue(), y / d.doubleValue(), z / d.doubleValue());
	}

	/**
	 * 点乘
	 * 
	 * @param p
	 *            点乘的向量p
	 * @return 此向量与p的点乘结果
	 */
	public double dotProduct(point p) {
		// return x.multiply(p.x).add(y.multiply(p.y).add(z.multiply(p.z)));
		return x * p.x + y * p.y + z * p.z;
	}

	/**
	 * 叉乘
	 * 
	 * @param p
	 *            叉乘的向量p
	 * @return 此向量与p的叉乘结果
	 */

	public point cross(point b) {
		// return new point(y.multiply(b.z).subtract(z.multiply(b.y)),
		// z.multiply(b.x).subtract(x.multiply(b.z)),
		// x.multiply(b.y).subtract(y.multiply(b.x)));
		return new point(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x);
	}

	/**
	 * 向量标准化，使向量的长度为1
	 * 
	 * @return 标准化是否成功
	 */
	public boolean standard() {
		// if (r.compareTo(BigDecimal.ZERO) == 0)
		// return false;
		// else if (r.compareTo(BigDecimal.ONE) == 0)
		// return true;
		if (r == 0)
			return false;
		else if (r == 1)
			return true;
		// x = x.divide(r, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP);
		// y = y.divide(r, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP);
		// z = z.divide(r, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP);
		// r = BigDecimal.ONE;
		// return true;
		x /= r;
		y /= r;
		z /= r;
		r = 1d;
		return true;
	}

	/**
	 * 转化为经纬度点
	 * 
	 * @return 转化为经纬度点的结果
	 */
	public LatLngPoint toLatLngPonit() {
		double theta, phi;
		// if (r.compareTo(BigDecimal.ZERO) == 0)
		// return null;
		if (r == 0)
			return null;
		theta = (Math.acos(z / r) * 180.0 / Math.PI + 90) % 90;
		theta = Double.parseDouble(String.format("%.7f", theta));
		phi = (Math.atan2(y, x) * 180.0 / Math.PI) % 180;
		phi = Double.parseDouble(String.format("%.6f", phi));
		// theta = (Math.acos(z.divide(r, LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP).doubleValue()) * 180. / Math.PI
		// + 90d) % 90;
		// theta = Double.parseDouble(String.format("%.6f", theta));
		// phi = (Math.atan(y.divide(x, LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP).doubleValue()) * 180. / Math.PI
		// + 180d) % 180;
		// phi = Double.parseDouble(String.format("%.6f", phi));
		return new LatLngPoint(phi, 90 - theta);
	}
}

class pair {
	public LatLngPoint key;
	public double value1;
	public double value2;
	public String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public pair(LatLngPoint p, double d) {
		key = p;
		value1 = d;
	}

	public pair(LatLngPoint p, double d, double v2) {
		key = p;
		value1 = d;
		value2 = v2;
	}

	public pair(LatLngPoint p, double d, double v2, String t) {
		key = p;
		value1 = d;
		value2 = v2;
		type = t;
	}

	public LatLngPoint getKey() {
		return key;
	}

	public void setKey(LatLngPoint key) {
		this.key = key;
	}

	public double getValue() {
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

class LatLngPoint {
	final static double Rc = LatLngAngleUtil.RC;
	final static double Rj = LatLngAngleUtil.RJ;
	// double m_LoDeg,m_LoMin,m_LoSec;
	// double m_LaDeg,m_LaMin,m_LaSec;
	double m_Longitude, m_Latitude;
	double m_RadLo, m_RadLa;
	double Ec;
	double Ed;

	public LatLngPoint(double longitude, double latitude) {
		// m_LoDeg=(int)longitude;
		// m_LoMin=(int)((longitude-m_LoDeg)*60);
		// m_LoSec=(longitude-m_LoDeg-m_LoMin/60.)*3600;
		//
		// m_LaDeg=(int)latitude;
		// m_LaMin=(int)((latitude-m_LaDeg)*60);
		// m_LaSec=(latitude-m_LaDeg-m_LaMin/60.)*3600;

		m_Longitude = longitude;
		m_Latitude = latitude;
		m_RadLo = longitude * Math.PI / 180.;
		m_RadLa = latitude * Math.PI / 180.;
		Ec = Rj + (Rc - Rj) * (90. - m_Latitude) / 90.;
		Ed = Ec * Math.cos(m_RadLa);
	}

	public String getString() {
		return String.format(LatLngAngleUtil.LatLngPointString, m_Longitude, m_Latitude);
	}
}
