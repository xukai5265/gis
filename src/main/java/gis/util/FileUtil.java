package gis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
	
	/**
	 * @author lipeng
	 * 
	 * 读取文件内容，返回arrayList<String> 
	 * @param file
	 * 		要读取的文件路径
	 */
	public static ArrayList<String> readLines(String file) throws IOException{
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"));
		String line = br.readLine();
		while (line != null) {
			lines.add(line);
			line = br.readLine();
		}
		br.close();
		return lines;
	}
	
	/**
	 * @author lipeng
	 * 
	 * 读取文件内容，返回arrayList<String> 
	 * @param file
	 * 		要读取的文件路径
	 */
	public static void writeLines(String file,ArrayList<String> lines) throws IOException{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file)), "UTF-8"));
		for (String line:lines){
			bw.write(line);
		}
	}
	
	/**
	 * @author lipeng
	 * 
	 * 判断文件是否存在，否则重建
	 * @param outDirName
	 * 		判断的文件夹路径
	 */
	public static void deleteAndMake(String outDirName){
		File outDir = new File(outDirName);
		if (!outDir.exists()) {
			outDir.mkdirs();
		}else{
			outDir.delete();
			outDir.mkdirs();
		}
	}
	
	/**
	 * @author lipeng
	 * 
	 * 对一个集合排序
	 * @param _values
	 * 		输入需要排序的集合
	 */
	public static final String FIELD_SEPERATOR = ",";
	public static ArrayList<String> collectionSort(List<String> _values){
	  List<String> values = new ArrayList<String>();
	  for(String value : _values){
		  values.add(value);
	  }
	  //do sort first
	  Collections.sort(values, new Comparator<String>() {
		  //sort by time from small to big
		  public int compare(String arg0, String arg1) {
			  String[] args0 = arg0.split(FIELD_SEPERATOR);
			  String[] args1 = arg1.split(FIELD_SEPERATOR);
			  long time0 = Long.parseLong(arg0.split(FIELD_SEPERATOR)[2]);
			  long time1 = Long.parseLong(arg1.split(FIELD_SEPERATOR)[2]);
			  if(time0<time1){
				  return -1;
			  }
			  else if(time0 == time1){
				  return 0;
			  }
			  else{
				  return 1;
			  }
		  }
	  });	  
	  return (ArrayList<String>) values;
	}
	
	public static void main(String [] args){
		
	}
}
