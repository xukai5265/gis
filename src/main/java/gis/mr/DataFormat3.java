package gis.mr;

import com.hadoop.mapreduce.LzoTextInputFormat;
import gis.shape.POIPoint;
import gis.shape.Point3D;
import gis.shape.Road2;
import gis.shape.sub.Pair;
import gis.shape.sub.Point;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Iterator;

/**
 * 压缩格式：  基本信息+投影信息+道路信息
 */
public class DataFormat3 extends Configured implements Tool {
    public int run(String[] strings) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, strings).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: Data Sort <in> <out>");
            System.exit(3);
        }
        Job job = new Job(conf, "DataFormat&Join");
        job.setInputFormatClass(LzoTextInputFormat.class);
        job.setJarByClass(DataFormat3.class);
        //设置Map和Reduce处理类
        job.setMapperClass(DataFormat3.PoiMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(DataFormat3.JoinReducer.class);
        //设置输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        //设置输入和输出目录（MultipleInputs.addInputPath 可以实现不同Map 处理不同的路径）
        MultipleInputs.addInputPath(job, new Path(otherArgs[0]), LzoTextInputFormat.class, PoiMap.class);
        MultipleInputs.addInputPath(job, new Path(otherArgs[1]), LzoTextInputFormat.class, RoadMap.class);
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    /**
     * 基本信息Map
     */
    public static class PoiMap extends Mapper<LongWritable, Text, Text, Text> {
        Text k = new Text();
        Text v = new Text();

        //实现map函数
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String result = "";
            // key
            String pid = "";
            String line = value.toString();
            String[] fields = line.split(",");
            pid = fields[17];

            // 从基本信息中截取出经纬度信息
            POIPoint poiPoint = new POIPoint();
            poiPoint.fromString(line);
            String jwd = fields[fields.length - 1].replace("Point(", "").replace(")", "");
            // 将基本信息与经纬度信息拼接
            result = poiPoint.toString() + "`" + jwd;
            k.set(pid);
            v.set(result);
            context.write(k, v);
        }
    }

    /**
     * 道路信息 Map
     */
    public static class RoadMap extends Mapper<LongWritable, Text, Text, Text> {
        Text k = new Text();
        Text v = new Text();

        //实现map函数

        /**
         * 提取出key 值，剩下的信息不做任何处理，直接传入到reduce中处理
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String pid = "";
            String line = value.toString();
            String[] fields = line.split(",");
            String result = "";
            pid = fields[0];
            result = line;
            k.set(pid);
            v.set(result);
            context.write(k, v);
        }
    }

    /**
     * reducer
     */
    public static class JoinReducer extends Reducer<Text, Text, Text, NullWritable> {
        // 最终结果
        String res = "";
        // 经纬度
        String jwd = "";
        // 投影
        String shadow = "";
        // 基本信息
        String basisInfo = "";
        // 未处理的基本信息
        String basisLine = "";
        // 道路信息
        String road = "";

        @Override
        protected void reduce(Text pid, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            Iterator<Text> ite = values.iterator();
            while (ite.hasNext()) {
                Text t = ite.next();
                String line = t.toString();
                if (line.contains("poi_point")) {
                    basisLine = line;
                } else {
                    road = line;
                }
            }

            if (StringUtils.isNotEmpty(basisLine) && StringUtils.isNotEmpty(road)) {
                String[] tmp = basisLine.split("`");
                basisInfo = tmp[0];// 基本信息
                String[] a = basisInfo.split(",");
                String[] b = road.split(",");
                String a18 = a[18];
                String b0 = b[0];
                // join 条件
                if (a18.equals(b0)) {
                    jwd = tmp[1];
                    Road2 road2 = new Road2(road);
                    road = road2.toString(); // 道路信息
                    String[] jwdArray = jwd.split(" ");
                    Point p = new Point(Double.parseDouble(jwdArray[0]), Double.parseDouble(jwdArray[1]));
                    Point3D p1 = new Point3D(p);
                    Pair pair = road2.getPointAndDistance(p, p1, 0);
                    shadow = pair.key.m_Longitude + "," + pair.key.m_Latitude + "," + pair.value1;
                    basisInfo = basisInfo.replace("0.0,0.0,0.0", shadow);
                    res = basisInfo + "`" + road;
                    Text text = new Text();
                    text.set(res);
                    context.write(text, NullWritable.get());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            new IllegalArgumentException("Usage: <inpath> <outpath>");
            return;
        }
        ToolRunner.run(new Configuration(), new DataFormat3(), args);
    }
}

