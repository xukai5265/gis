package gis.util;

import gis.shape.Node;
import gis.shape.Road1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Traverse {
	//已经验证过的2016年二季度四维图新数据的省份列表,"aomen"  道路有问题，没有处理{}	"anhui","beijing","chongqing","fujian","gansu"
	//	,"guangdong1","guangdong2","guangzhou", "guangxi", "guizhou",	"hainan","hebei", "heilongjiang", 
	//	"henan", 
//	public static String[] provinceList = {"anhui","beijing","chongqing","fujian","gansu"
//		,"guangdong1","guangdong2","guangzhou", "guangxi", "guizhou",	"hainan","hebei", "heilongjiang", 
//		"henan", "hubei", "hunan", "jiangsu1", "jiangsu2", "jiangxi", "jilin", "liaoning","neimenggu", 
//		"ningxia","qinghai", "shan3xi", "shandong1", "shandong2","shanghai", "shanxi", "sichuan1", 
//		"sichuan2","tianjin","xianggang", "xinjiang", "xizang", "yunnan", "zhejiang1", "zhejiang2"};
//	
	public static String[] provinceList = {"hainan"};
	public static void toSingleFile() throws Exception {
		String[] gisTypeList = {"R"}; //R
		BufferedWriter bw;
		for (String gisType:gisTypeList){
			String outFile = "E:/roaddata2/singleFileOut/FNameall.txt";
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFile)), "UTF-8"));
			BufferedReader br;
			for (String province : provinceList) {
				String TypeFile = "E:/roaddata2/out/" + province + "/other/FName" + province + "out.txt";
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(TypeFile)), "UTF-8"));
				String line = br.readLine();
				while (line != null) {
					bw.write(line);
					bw.write("\n");
					line = br.readLine();
				}
				bw.flush();
				br.close();
			}
			bw.close();
		}
	}
	
	/**
	 * 将行政区划的名称整理到一起
	 * 
	 * @throws Exception
	 */
	public static void toSingleANameFile() throws Exception {
		BufferedWriter bw;
		String outFile = "E:/roaddata2/singleFileOut/FNameall.txt";
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				new File(outFile)), "utf-8"));
		BufferedReader br;

		for (String province : provinceList) {	
			String TypeFile = "E:/roaddata2/out/" + province + "/other/FName" + province + "out.txt";
			ArrayList<String> lines = FileUtil.readLines(TypeFile);
			//System.out.println(lines);
			
			for(String line:lines){
				bw.write(line+"\n");
				//bw.write();
				System.out.println(line);
			}
			bw.flush();
		}
		bw.close();
	}
	
	
	/**
	 * 将行政区划的名称和代码整理到一起
	 * 
	 * @throws Exception
	 */
	public static void toSingleAdminCodeNameFile() throws Exception {
		BufferedWriter bw;
		String outFile = "E:/roaddata2/singleFileOut/ANameall.txt";
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				new File(outFile)), "utf-8"));
		BufferedReader br;
		int count =0;
		for (String province : provinceList) {
			String TypeFile = "E:/roaddata2/out/" + province + "/other/Admin" + province + "out.txt";
			ArrayList<String> lines = FileUtil.readLines(TypeFile);
			
			for(String line:lines){
				bw.write(line+"\n");
				//bw.write();
				System.out.println(line);
				count ++;
			}
			bw.flush();
		}
		bw.close();
		System.out.println(count+" : total");

	}
	
	/*
	 * 网格测试
	 * 
	 */
	public static void gridComputer(){
		
		double x=118.491280,y=39.867485;
		//118.491280,39.867485
		double xx = (x - (long) x) * 1000 - (x - (long) x) * 1000 % 5;
		double yy = (y - (long) y) * 1000 - (y - (long) y) * 1000 % 5;
		String sxx = String.format("%03.0f", xx);
		String syy = String.format("%03.0f", yy);
		System.out.println("sxx:" + sxx	);
		System.out.println("syy:" + syy	);
		System.out.println("xx:" + xx	);
		System.out.println("yy:" + yy	);
		
		System.out.println("xx:" + ((x - (long) x) * 1000) );
		System.out.println("yy:" + ((y - (long) y) * 1000) );
		System.out.println("xx:" + ((x - (long) x) * 1000 % 5) );
		System.out.println("yy:" + ((y - (long) y) * 1000 % 5) );
	}
	
	
	public static void search() throws Exception {
		String[] gisTypeList = {"POI"}; //R
		BufferedWriter bw;
		for (String gisType:gisTypeList){
			String outFile = "E:/roaddata2/singleFileOut/" + gisType +"all.txt";
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFile)), "UTF-8"));
			BufferedReader br;
			for (String province : provinceList) {
				String TypeFile = "E:/roaddata2/out/" + province + "/poi/" + gisType + "out.txt";
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(TypeFile)), "UTF-8"));
				String line = br.readLine();
				while (line != null) {
					//String[] kindType = line.trim().split(",");
					//if (kindType.length != 7) continue;
					//bw.write(line);
					String[] lineSeg = line.trim().split(",");
					line = br.readLine();
				}
				br.close();
			}
			bw.close();
		}
	}
	//计算与当前节点关联的节点
	public static void getAdjacentNode() throws Exception {
		BufferedReader br;
		HashMap<String,Road1> roadMap = new HashMap<String, Road1>();
		HashMap<String,Node> nodeMap = new HashMap<String, Node>();
		int count = 0;
		int allCount = 0;
		for (String province : provinceList) {
			String outFile = "E:/roaddata2/out/" + province + "/road/NAdjacentOut.txt";
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outFile)), "UTF-8"));
			String nodeFile = "E:/roaddata2/out/" + province + "/road/Nout.txt";
			ArrayList<String> lines = FileUtil.readLines(nodeFile);
			for (String line:lines){
				String[] seg = line.split(",");
				if(!nodeMap.containsKey(seg[1])){
					nodeMap.put(seg[1], new Node(line));
				}
			}
			String roadFile = "E:/roaddata2/out/" + province + "/road/Rout.txt";
			lines = FileUtil.readLines(roadFile);
			for (String line:lines){
				String[] seg = line.split(Separtor.lay1Sep);
				if(!roadMap.containsKey(seg[0])){
					roadMap.put(seg[0], new Road1(line));
				}
			}
		
		
			for (String nodeId : nodeMap.keySet()) {
				Node n = nodeMap.get(nodeId);
				HashSet<String> links = n.getNodeLID();
				HashSet<String> allNodes = new HashSet<String>();
				for (String link : links) {
					if (roadMap.containsKey(link)) {
//						System.out.println(roadMap.get(link).toString());
						allNodes.add(roadMap.get(link).getEnodeID());
						allNodes.add(roadMap.get(link).getSnodeID());
					} else {
						System.out.println("can't find this link:" + link);
						System.out.println("can't find this nodeId:" + nodeId);
						System.out.println("can't find this links:" + links);
						// System.out.println(roadMap.get(link).toString());
						bw.write("\n");
					}
				}
				allNodes.remove(nodeId);
				String nodeStr = "";
				for (String node : allNodes) {
					nodeStr += node + Separtor.lay2Sep;
				}
				if (nodeStr.endsWith(Separtor.lay2Sep)){
					nodeStr = nodeStr.substring(0, nodeStr.lastIndexOf(Separtor.lay2Sep));
				}
//				String newNodeLID = "";
//				for(String s:n.getNodeLID()){
//					newNodeLID += s + Separtor.lay2Sep;
//				}
//				if (newNodeLID.endsWith(Separtor.lay2Sep)){
//					newNodeLID = newNodeLID.substring(0, newNodeLID.lastIndexOf(Separtor.lay2Sep));
//				}
				n.setAdjacentNode(nodeStr);
//				n.setnodeLID(newNodeLID);
				//System.out.println(n.toStr().replaceAll("\\|", Separtor.lay2Sep));
				//n.toStr().replaceAll("\\|", Separtor.lay2Sep);
				bw.write(n.toStr().replaceAll("\\|", Separtor.lay2Sep));
				count +=1;
				allCount += 1;
				bw.write("\n");
			}
			System.out.println(province + " : " + count+ " @@@");
			count = 0;
			nodeMap.clear();
			roadMap.clear();
			bw.flush();
			bw.close();
		}
		System.out.println("all count :"+allCount);
	}
	
	
	public static void main(String[] args) throws Exception {
		//toSingleFile();
		//gridComputer();
//		getAdjacentNode();
		//toSingleANameFile();
		getAdjacentNode();
		//toSingleAdminCodeNameFile();
	}
	
}
