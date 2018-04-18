package gis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class MapIdTest {
	
	public static String toMapId(Double x,Double y){
		String ret = "";
		//String outFile = "";
		//BufferedWriter  bw = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(new File(outFile)), "GBK"));
		return ret;
	}
	
	public static void main(String [] args) throws IOException{
		String poiFile =  "E:/roaddata2/out/anhui/poi/POIout.txt";
		String roadFile =  "E:/roaddata2/out/anhui/road/Rout.txt";
		String mapIdFile = "E:/roaddata2/MapIdout.txt";
		
		//read map id file to hashmap
		ArrayList<String> lines = fileReader(mapIdFile);
		HashMap<String,String> mapIdMap = new HashMap<String,String>();
		for (String line :lines){
			String [] line_sp = line.split(",");
			mapIdMap.put(line_sp[2], line_sp[0]);
		}
		//over
		
		lines = fileReader(poiFile);
		Double rightPercent = 0.0;
		double right = 0.0,total = 0.0;
		for (String line:lines){
			String [] lineSp = line.split(",");
			if (mapIdMap.containsKey(lineSp[4])){
				Dimension d = new Dimension(Double.parseDouble(lineSp[14]),Double.parseDouble(lineSp[15]));
				total += 1.0;
				if (d.getMapId().equals(mapIdMap.get(lineSp[4]))) right += 1.0;
				else{
					System.out.println("________________"+lineSp[14] +"\t" + lineSp[15]);
					System.out.println("________________"+d.getMapId() +"\t"+ mapIdMap.get(lineSp[4]));
				}
			}
		}
		rightPercent = right / total;
		System.out.println("right percent ::"+ rightPercent);
		System.out.println("right number ::"+ right);
		System.out.println("total number ::"+ total);
		//ArrayList<String> outs = null;
		//if (outs != null) fileWriter(outFile,outs);
		//Double x=0.0,y=0.0;
		//String aa = toMapId(x,y);
	}
	
	@SuppressWarnings("null")
	public static ArrayList<String> fileReader(String file) throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		BufferedReader br =  new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"));
		String tmp = br.readLine();
		while(tmp != null){
			ret.add(tmp);
			tmp = br.readLine();
			System.out.println("read one record : " + tmp);
		}
		br.close();
		return ret;
	}
	
	public static int fileWriter(String file, ArrayList<String> content) throws IOException{
		int ret = 0;
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file)), "utf-8"));
		for (String con:content){
			bw.write(con + "\n");
		}
		return ret;
	}
}
