package gis.test;

import gis.shape.*;
import gis.shape.sub.Pair;
import gis.shape.sub.Point;

public class Demo {
    public static void main(String[] args) {
        // 道路信息
        String line = "268505,X304|漷马路,5|0,,,595656,4,55,2,142.0,,,70,1,0402,3,191550,200609,3,110112,110112,0,5,LINESTRING (116.78984 39.77766;116.79087 39.77817;116.79124 39.77834)";
        Road2 r = new Road2(line);

        String[] temp = line.split(",");
        // 假如经度位置在 2
        // 假如维度位置在 3
        double jd = Double.parseDouble(".5");
        double wd = Double.parseDouble(".6");
        // 投影信息
        // 经度纬度在道路信息字符串中，把line 按照逗号切分，需要确定经纬度的位置
        Point p = new Point(jd,wd);
        Point3D p1 = new Point3D(p);
        Pair pair = r.getPointAndDistance(p, p1, 0);
        String shadow =pair.key.m_Longitude+","+pair.key.m_Latitude+","+pair.value1;

        // 基本信息（疑问： 基本信息String s 是哪里来的）
        String s = "10019768,poi_point,加油站,,,545407,4,汽车,40,加油站,4080,加油站(代表),,,114.99174,36.04101,2,13872609,R,410000,河南省,410900,濮阳市,410922,清丰县,Point(114.99164 36.04109)";
        POIPoint poiPoint = new POIPoint();
        poiPoint.fromString(s);
        String baseInfo = poiPoint.toString();
        // 将投影信息替换到基本信息中
        baseInfo = baseInfo.replace("0.0,0.0,0.0",shadow);
        // 最终结果
        String result = baseInfo+"`"+r.toString();
        System.out.println("最终结果："+result);
    }
}
