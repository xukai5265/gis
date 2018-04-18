package gis.test;

import gis.util.GisUtil;

/**
 * Created by kaixu on 2018/4/18.
 */
public class Point {
    public final static double Rc = GisUtil.RC;
    public final static double Rj = GisUtil.RJ;
    // double m_LoDeg,m_LoMin,m_LoSec;
    // double m_LaDeg,m_LaMin,m_LaSec;
    public double m_Longitude;
    public double m_Latitude;
    public double m_RadLo; //弧度
    public double m_RadLa;
    public double Ec;
    public double Ed;

    public Point(double longitude, double latitude) {
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

    public boolean equals(Point p){
        if (this.m_Latitude == p.m_Latitude && this.m_Longitude == p.m_Longitude){
            return true;
        }else{
            return false;
        }
    }

    public String getString() {
//        return String.format(GisUtil.LatLngPointString, m_Longitude, m_Latitude);
        return "";
    }
}
