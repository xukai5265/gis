package gis.mr;

import com.hadoop.mapreduce.LzoTextInputFormat;
import gis.shape.POIPoint;
import gis.shape.Point3D;
import gis.shape.Road2;
import gis.shape.sub.Pair;
import gis.shape.sub.Point;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.LzopCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 压缩格式：  基本信息+投影信息+道路信息
 */
public class DataFormat3 extends Configured implements Tool {

    public static class RoadMap extends Mapper<LongWritable, Text, Text, Text> {
        Text k = new Text();
        Text v = new Text();

        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {
            String pid = "";
            String line = value.toString();
            String[] fields = line.split(",");
            String result = "";
            pid = fields[0];
            result = line;
            this.k.set(pid);
            this.v.set(result);
            context.write(this.k, this.v);
        }
    }

    public static class PoiMap extends Mapper<LongWritable, Text, Text, Text> {
        Text k = new Text();
        Text v = new Text();

        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
                throws IOException, InterruptedException {
            String result = "";

            String pid = "";
            String line = value.toString();
            String[] fields = line.split(",");
            pid = fields[17];

            POIPoint poiPoint = new POIPoint();
            poiPoint.fromString(line);
            String jwd = fields[(fields.length - 1)].replace("Point(", "").replace(")", "");

            result = poiPoint.toString() + "`" + jwd;
            this.k.set(pid);
            this.v.set(result);
            context.write(this.k, this.v);
        }
    }

    public static class JoinReducer extends Reducer<Text, Text, Text, NullWritable> {

        protected void reduce(Text pid, Iterable<Text> values, Reducer<Text, Text, Text, NullWritable>.Context context)
                throws IOException, InterruptedException {
            List<String> list1 = new ArrayList();
            List<String> list2 = new ArrayList();
            Iterator ite = values.iterator();
            Text t;
            while (ite.hasNext()) {
                t = (Text) ite.next();
                String line = t.toString();
                if (line.contains("poi_point"))
                    list1.add(line);
                else {
                    list2.add(line);
                }
            }
            for (String line1 : list1) {
                for (String line2 : list2) {
                    String[] tmp = line1.split("`");
                    String basisInfo = tmp[0];
                    String[] a = basisInfo.split(",");
                    String a18 = a[18];

                    String[] b = line2.split(",");
                    String b0 = b[0];
                    if (a18.equals(b0)) {
                        String jwd = tmp[1];
                        Road2 road2 = new Road2(line2);
                        String road = road2.toString();
                        String[] jwdArray = jwd.split(" ");
                        Point p = new Point(Double.parseDouble(jwdArray[0]), Double.parseDouble(jwdArray[1]));
                        Point3D p1 = new Point3D(p);
                        Pair pair = road2.getPointAndDistance(p, p1, 0.0D);
                        String shadow = pair.key.m_Longitude + "," + pair.key.m_Latitude + "," + pair.value1;
                        basisInfo = basisInfo.replace("0.0,0.0,0.0", shadow);
                        String res = basisInfo + "`" + road;
                        Text text = new Text();
                        text.set(res);
                        context.write(text, NullWritable.get());
                    }
                }
            }
        }
    }

    public int run(String[] strings) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, strings).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: Data Sort <in> <out>");
            System.exit(3);
        }
        Job job = new Job(conf, "DataFormat&Join");
        job.setInputFormatClass(LzoTextInputFormat.class);
        // 设置reduce 输出压缩算法
        FileOutputFormat.setCompressOutput(job, true);  //job使用压缩
        // LzoCodec -> ".lzo_deflate"   LzopCodec -> .lzo
        FileOutputFormat.setOutputCompressorClass(job, LzopCodec.class); //设置压缩格式
        job.setJarByClass(DataFormat3.class);
//        job.setNumReduceTasks(10);
        job.setMapperClass(PoiMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(JoinReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        MultipleInputs.addInputPath(job, new Path(otherArgs[0]), LzoTextInputFormat.class, PoiMap.class);
        MultipleInputs.addInputPath(job, new Path(otherArgs[1]), LzoTextInputFormat.class, RoadMap.class);

        FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            new IllegalArgumentException("Usage: <inpath> <outpath>");
            return;
        }
        ToolRunner.run(new Configuration(), new DataFormat3(), args);
    }
}

