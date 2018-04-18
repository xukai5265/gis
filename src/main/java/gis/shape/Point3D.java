package gis.shape;

import gis.shape.sub.Point;
import gis.util.GisUtil;

import java.math.BigDecimal;



public  class Point3D {
	/**
	 * 直角坐标的点向量 通过构造函数可以直接将经纬度转换为三维直角坐标
	 */
	double x;
	double y;
	double z;
	double r;

	public String toString() {
		return String.format(GisUtil.pointString, x, y, z, r);
	}
	public boolean fromString(String s){
		String[] slist=s.split(GisUtil.pointSep);
		if(slist.length<4)
			return false;
		this.x=Double.parseDouble(slist[0]);
		this.y=Double.parseDouble(slist[1]);
		this.z=Double.parseDouble(slist[2]);
		this.r=Double.parseDouble(slist[3]);
		return true;
	}

	public Point3D() {
		x = 0d;
		y = 0d;
		z = 0d;
		r = 0d;
	}

	public Point3D(Point P) {

		x = Math.sin(Math.PI - P.m_RadLa) * Math.cos(P.m_RadLo);
		y = Math.sin(Math.PI - P.m_RadLa) * Math.sin(P.m_RadLo);
		z = Math.cos(Math.PI - P.m_RadLa);
		r = Math.sqrt(x * x + y * y + z * z);
	}

	public Point3D(BigDecimal x1, BigDecimal y1, BigDecimal z1) {
		x = x1.doubleValue();
		y = y1.doubleValue();
		z = z1.doubleValue();
		r = Math.sqrt(x * x + y * y + z * z);
	}

	public Point3D(double x1, double y1, double z1) {
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
	public Point3D add(Point3D p) {
		if (p == null)
			return null;
		// return new point(x.add(p.x), y.add(p.y), z.add(p.z));
		return new Point3D(x + p.x, y + p.y, z + p.z);
	}

	/**
	 * 向量相减
	 * 
	 * @param p
	 *            被减的向量
	 * @return 此向量减p后的结果
	 */
	public Point3D sub(Point3D p) {
		if (p == null)
			return null;
		// return new point(x.subtract(p.x), y.subtract(p.y), z.subtract(p.z));
		return new Point3D(x - p.x, y - p.y, z - p.z);
	}

	/**
	 * 向量相加
	 * 
	 * @param d
	 *            被乘的数
	 * @return 此向量乘p后的结果
	 */
	public Point3D mult(double d) {
		return new Point3D(x * d, y * d, z * d);
	}

	/**
	 * 向量相加
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被乘的数
	 * @return 此向量乘p后的结果
	 */
	public Point3D mult(BigDecimal d) {
		// return new point(x.multiply(d), y.multiply(d), z.multiply(d));
		return new Point3D(x * d.doubleValue(), y * d.doubleValue(), z * d.doubleValue());
	}

	/**
	 * 向量除法
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被除数
	 * @return 此向量除以p后的结果
	 */
	public Point3D div(double d) {
		if (d == 0d)
			return null;
		// return new point(x.divide(BigDecimal.valueOf(d),
		// LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP),
		// y.divide(BigDecimal.valueOf(d), LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP),
		// z.divide(BigDecimal.valueOf(d), LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP));
		return new Point3D(x / d, y / d, z / d);
	}

	/**
	 * 向量除法
	 * 
	 * @param d
	 *            高精度浮点数BigDecimal 被除数
	 * @return 此向量除以p后的结果
	 */
	public Point3D div(BigDecimal d) {
		if (d.compareTo(BigDecimal.ZERO) == 0)
			return null;
		// return new point(x.divide(d, LatLngAngleUtil.Decimals,
		// BigDecimal.ROUND_UP),
		// y.divide(d, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP),
		// z.divide(d, LatLngAngleUtil.Decimals, BigDecimal.ROUND_UP));
		return new Point3D(x / d.doubleValue(), y / d.doubleValue(), z / d.doubleValue());
	}

	/**
	 * 点乘
	 * 
	 * @param p
	 *            点乘的向量p
	 * @return 此向量与p的点乘结果
	 */
	public double dotProduct(Point3D p) {
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

	public Point3D cross(Point3D b) {
		// return new point(y.multiply(b.z).subtract(z.multiply(b.y)),
		// z.multiply(b.x).subtract(x.multiply(b.z)),
		// x.multiply(b.y).subtract(y.multiply(b.x)));
		return new Point3D(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x);
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
	public Point toLatLngPonit() {
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
		return new Point(phi, 90 - theta);
	}
}

