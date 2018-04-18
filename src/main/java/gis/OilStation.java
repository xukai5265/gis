package gis;

import gis.shape.AdjacentNode;
import gis.shape.OilStationRoad;
import gis.shape.Road1;
import gis.util.FileUtil;
import gis.util.Separtor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class OilStation {
	public static String path = "E:/roaddata2/";
	public static String pathOut = path+"out/";
	public static String pathMif = path+"mif/";
	public static String pathTest = path+"test_out/";
	
	//10月28日检查过，与16年四维数据中的省份排序一致
	//"aomen"
//	public static String[] provinceList = {"anhui","beijing","chongqing","fujian","gansu"
//	,"guangdong1","guangdong2",	"guangxi",	"guangzhou", "guizhou","hebei", "heilongjiang", 
//	"henan","hubei", "hunan", "jiangsu1", "jiangsu2", "jiangxi", "jilin", "liaoning","neimenggu", 
//	"ningxia","qinghai", "shan3xi", "shandong1", "shandong2","shanghai", "shanxi", "sichuan1", 
//	"sichuan2","tianjin","xianggang", "xinjiang", "xizang", "yunnan", "zhejiang1", "zhejiang2",	"hainan"}; 
//	
	public static String[] provinceList = {"hainan"};
	
	public static String getPoiRoadInfo(String roadId,HashMap<String,OilStationRoad> allRoadInfo){
		String  ret = "";
		OilStationRoad nowRoad = allRoadInfo.get(roadId);
		if(nowRoad == null){
			
			System.out.println("**************");
			System.out.println(roadId);
		}
		HashSet<String> E2S = nowRoad.getLinkedIdE2S();
		HashSet<String> S2E = nowRoad.getLinkedIdS2E();
		if(!nowRoad.getFirstName().equals("")){
			return nowRoad.getFirstName();
		}
		if(E2S.size()==0 || S2E.size()==0){
			return nowRoad.getFirstName();
		}
		
		String S2Eout = "";
		HashSet<String> searchedRoads = new HashSet<String>();
		HashSet<String> searchRoads = new HashSet<String>();
		searchRoads.addAll(S2E);
		for(int i = 0; i< 3; i++){
			HashSet<String> tmp = new HashSet<String>();
			int flag = 0;
			for(String r:searchRoads){
				if(!searchedRoads.contains(r)){
					OilStationRoad now =  allRoadInfo.get(r);
					if(Integer.parseInt(now.kind) <= 2){
						S2Eout = now.getFirstName();
						flag =1;
						break;
					}
					tmp.addAll(now.getLinkedIdS2E());
					searchedRoads.add(r);
				}
			}
			if(flag == 1)break;
			searchRoads.clear();
			searchRoads.addAll(tmp);
		}
		
		
		String E2Sout = "";
		searchedRoads.clear();;
		searchRoads.clear();;
		searchRoads.addAll(E2S);
		for(int i = 0; i< 3; i++){
			HashSet<String> tmp = new HashSet<String>();
			int flag = 0;
			for(String r:searchRoads){
				if(!searchedRoads.contains(r)){
					OilStationRoad now =  allRoadInfo.get(r);
					if(Integer.parseInt(now.kind) <= 2){
						E2Sout = now.getFirstName();
						flag =1;
						break;
					}
					tmp.addAll(now.getLinkedIdE2S());
					searchedRoads.add(r);
				}
			}
			if(flag == 1)break;
			searchRoads.clear();
			searchRoads.addAll(tmp);
		}
		
		if(E2Sout.equals(S2Eout)){
			return E2Sout;
		}
		
		return ret;
	}
	
	public static HashMap<String,OilStationRoad> getRoadInfo(String provin) throws IOException{
		HashMap<String,OilStationRoad>  ret= new HashMap<String,OilStationRoad>();
		String poiPath = pathOut + provin + "/" + "road/RAdjout.txt";
		ArrayList<String> roadLines = FileUtil.readLines(poiPath);
		for(String line:roadLines){
			OilStationRoad road =new OilStationRoad(line);
			ret.put(road.getId(), road);
		}
		return ret;
	}
	
	//遍历各个省份poi
	public static void traversePoi() throws IOException{
		int count = 0;
		int allCount = 0;
		for(String provin:provinceList){
			String poiPath = pathOut + provin + "/" + "poi/POIout.txt";
			String poiOilPath = pathOut + provin + "/" + "poi/POIOilout.txt";
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(poiOilPath)), "UTF-8"));
			ArrayList<String> poiFile = FileUtil.readLines(poiPath);
			for (String line:poiFile){
				String[] seg = line.split(Separtor.lay1Sep);
				//对每个poi的道路信息进行道路名称分析
				HashMap<String,OilStationRoad> allRoadInfo = getRoadInfo(provin);
				//System.out.println(line);
				//if(!"4455919".equals(seg[0])){continue;}
				if(!seg[7].equals("40")){continue;}
				System.out.println(seg[7]);
				String roadInfo = getPoiRoadInfo(seg[17],allRoadInfo);
				bw.write(roadInfo);
				bw.write("\n");
				count ++;
				allCount ++;
				bw.flush();
			}
			bw.close();
			System.out.println(provin + ": "+ count);
		}
		System.out.println("all count: "+ allCount);
	}
	
	
	/**
	 * 函数功能：向前拓扑一层道路
	 * 
	 * @param adjNodeMap
	 * @param sNodeId
	 * @param eNodeId
	 * @param roadId
	 * @return
	 */
	public static String get1LayerTopology(HashMap<String,AdjacentNode> adjNodeMap, String sNodeId ,
										   String eNodeId, String linkId, HashMap<String,Road1> roadMap){
		String ret = "";	
		HashSet<String> links= adjNodeMap.get(eNodeId).getNodeLID();
		
		//从相邻图幅返回信息
		if(!adjNodeMap.get(eNodeId).getAdjoinNId().equals("")){
			//获取邻接图幅的临界点的相连link
			if(adjNodeMap.containsKey(adjNodeMap.get(eNodeId).getAdjoinNId())){
				HashSet<String> tmplinks =adjNodeMap.get(adjNodeMap.get(eNodeId).getAdjoinNId()).getNodeLID();
				links.addAll(tmplinks);
			}
			for(String link:links){
				if (link.equals(linkId)){
					continue;
				}
				Road1 nowRoad = roadMap.get(link);
				if(nowRoad == null){
					System.out.println("nowRoad  is null");
					System.out.println(link);
				}
				try{
				if(nowRoad.getDirection()==3){
					if(adjNodeMap.get(nowRoad.getEnodeID()).getPoint().equals(adjNodeMap.get(eNodeId).getPoint())){
						ret+= link + Separtor.lay2Sep;
					}
				}else if(nowRoad.getDirection()==2){
					if(adjNodeMap.get(nowRoad.getSnodeID()).getPoint().equals(adjNodeMap.get(eNodeId).getPoint())){
						ret+= link + Separtor.lay2Sep;
					}
				}else{
					ret += link + Separtor.lay2Sep;
				}
				}catch(Exception e){
					//System.out.println("nowRoad.getDirection()"+nowRoad.getDirection());
					System.out.println("nowRoad.toStr()"+nowRoad);
				}
			}
			if(ret.lastIndexOf(Separtor.lay2Sep) >= 0){
				ret = ret.substring(0, ret.lastIndexOf(Separtor.lay2Sep));
			}
			return ret;
		}
		
		for(String link:links){
			if (link.equals(linkId)){
				continue;
			}
			Road1 nowRoad = roadMap.get(link);
			if(nowRoad.getDirection()==3){
				if(nowRoad.getEnodeID().equals(eNodeId)){
					ret+= link + Separtor.lay2Sep;
				}
			}else if(nowRoad.getDirection()==2){
				if(nowRoad.getSnodeID().equals(eNodeId)){
					ret+= link + Separtor.lay2Sep;
				}
			}else{
				ret += link + Separtor.lay2Sep;
			}
		}
		if(ret.lastIndexOf(Separtor.lay2Sep) >= 0){
			ret = ret.substring(0, ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	
	
	/**
	 * 函数功能：向后拓扑一层道路
	 * 
	 * @param adjNodeMap
	 * @param sNodeId
	 * @param eNodeId
	 * @param roadId
	 * @return
	 */
	public static String get1LayerTopologyBack(HashMap<String,AdjacentNode> adjNodeMap,String sNodeId ,
			String eNodeId,String linkId,HashMap<String,Road1> roadMap){
		String ret = "";	
		HashSet<String> links= adjNodeMap.get(sNodeId).getNodeLID();
		
		//从相邻图幅返回信息
		String adjoinNid = adjNodeMap.get(sNodeId).getAdjoinNId();
		if(!adjoinNid.equals("")){
			//获取邻接图幅的临界点的相连link
			if(adjNodeMap.containsKey(adjoinNid)){
				HashSet<String> tmplinks = adjNodeMap.get(adjoinNid).getNodeLID();
				links.addAll(tmplinks);
			}
			for(String link:links){
				if (link.equals(linkId)){
					continue;
				}
				Road1 nowRoad = roadMap.get(link);
				if(nowRoad == null){
					System.out.println("nowRoad  is null");
					System.out.println(link);
				}
//				try{
				if(nowRoad.getDirection()==3){
					if(adjNodeMap.get(nowRoad.getSnodeID()).getPoint().equals(adjNodeMap.get(sNodeId).getPoint())){
						//                    _____________                              ____________
						ret+= link + Separtor.lay2Sep;
					}
				}else if(nowRoad.getDirection()==2){
					if(adjNodeMap.get(nowRoad.getEnodeID()).getPoint().equals(adjNodeMap.get(sNodeId).getPoint())){
						ret+= link + Separtor.lay2Sep;
					}
				}else{
					ret += link + Separtor.lay2Sep;
				}
//				}catch(Exception e){
//					//System.out.println("nowRoad.getDirection()"+nowRoad.getDirection());
//					System.out.println("nowRoad.toStr()"+nowRoad);
//				}
			}
			if(ret.lastIndexOf(Separtor.lay2Sep) >= 0){
				ret = ret.substring(0, ret.lastIndexOf(Separtor.lay2Sep));
			}
			return ret;
		}
		
		for(String link:links){
			if (link.equals(linkId)){
				continue;
			}
			Road1 nowRoad = roadMap.get(link);
			if(nowRoad.getDirection()==3){
				if(nowRoad.getSnodeID().equals(sNodeId)){
					ret+= link + Separtor.lay2Sep;
				}
			}else if(nowRoad.getDirection()==2){
				if(nowRoad.getEnodeID().equals(sNodeId)){
					ret+= link + Separtor.lay2Sep;
				}
			}else{
				ret += link + Separtor.lay2Sep;
			}
		}
		if(ret.lastIndexOf(Separtor.lay2Sep) >= 0){
			ret = ret.substring(0, ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	
	
	/**
	 * 原始名称genAdjRoad()
	 * 测试函数
	 * 功能为调用函数生成道路及道路的相邻点，主要添加了可以生成道路的第一名称
	 * 从mif格式的数据转换过来为第一道数据，此函数执行第二道数据计算，即，找出道路的相邻道路；第三道为建立拓扑结构，完成即可上线使用
	 * @throws IOException
	 */
	public static void genAdjRoad() throws IOException{
		int count = 0;
		int allCount = 0;
		for(String provin:provinceList){
		System.out.println(provin);
		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
//		String nodeFile = "E:/roaddata2/out/hebei/road/NAdjacentOut.txt";
		String nodeFile = "E:/roaddata2/out/"+provin+"/road/NAdjacentOut.txt";
		ArrayList<String> lines = FileUtil.readLines(nodeFile);
		for (String line:lines){
			String [] seg = line.split(",");
			String NodeLine = "";
			for(int i=0;i<15;i++){
				if (i != 13){
					NodeLine += seg[i] +",";
				}
			}
			NodeLine = NodeLine.substring(0, NodeLine.lastIndexOf(","));
			AdjacentNode tmp = new AdjacentNode(NodeLine,seg[13]);
			nodeMap.put(tmp.getId(), tmp);
		}
		String outFile = "E:/roaddata2/out/" + provin + "/road/RAdjout.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outFile)), "UTF-8"));

		String roadFile = "E:/roaddata2/out/"+ provin +"/road/Rout.txt";
		HashMap<String,AdjacentNode> roadMap = new HashMap<String,AdjacentNode>();
		lines = FileUtil.readLines(roadFile);
		HashMap<String,Road1> roadHM = new HashMap<String,Road1>();
		HashMap<String,HashSet<Road1>> roadHMByMapId = new HashMap<String,HashSet<Road1>>();
		for(String line:lines){
			Road1 r = new Road1(line);
			roadHM.put(r.getId(), r);
			String rMapId = r.getMapId();
			if (!roadHMByMapId.containsKey(rMapId)){
				HashSet<Road1> tmpHS = new HashSet<Road1>();
				tmpHS.add(r);
				roadHMByMapId.put(r.getMapId(), tmpHS);
			}else{
				roadHMByMapId.get(rMapId).add(r);
			}
		}
		for (String line:lines){
//			String line = "87932107,|北外环路,|Ｇ１０４,,,j50f030019,2,55,2,271.0,,600,,1,0202,2,2120083,74242844,LINESTRING (116.34512 37.50894, 116.34359 37.5094, 116.34223 37.50978),";
//			if(count > 20){break;}
			count += 1;
			allCount += 1;
			Road1 r = new Road1(line);
			String linkedIdS2E = "",linkedIdE2S = "";
			//if(!r.getId().equals("12570721")){continue;}
			//System.out.println(line);
		
			if (r.getDirection() == 3){
				linkedIdE2S= get1LayerTopology(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id,roadHM);
				linkedIdS2E= get1LayerTopologyBack(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id,roadHM);
				
			}else if(r.getDirection() == 2){
				//System.out.println(r.toStr());
				linkedIdS2E= get1LayerTopology(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id,roadHM);
				linkedIdE2S= get1LayerTopologyBack(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id,roadHM);
			}else{
				linkedIdS2E= get1LayerTopology(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id,roadHM);
				linkedIdE2S= get1LayerTopology(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id,roadHM);
			}
			
			String invertRoad = "";
//			if(r.isUpDownWardSplit()){
//				invertRoad = getInvertedRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
//			}else if(r.isUpDownWardSubRoad()){
//				invertRoad = getInvertedSubRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
//			}
			String linkedId = linkedIdS2E + Separtor.lay1Sep +linkedIdE2S + Separtor.lay1Sep + invertRoad;
			
					// + Separtor.lay1Sep + mainSubRoad;
			if(linkedId.equals("")){
				linkedId = Separtor.lay1Sep;
				System.out.println("___________----------------------___________");
			}
			bw.write(r.toRMStr(linkedId));
//					.replaceAll("\\|", Separtor.lay2Sep));
//			if (linkedIdS2E.equals("")||linkedIdE2S.equals("")){
//				
//			System.out.println(line);
//			System.out.println("**out** : " + r.toRMStr(linkedId).replaceAll("\\|", Separtor.lay2Sep));
//			}
			bw.write("\n");
			bw.flush();
		}
		System.out.println("province: "+provin+", count:"+count);
		count = 0;
		}
		System.out.println("all province: , count:"+allCount);
		
	}
	
	
	public static void main(String [] args) throws IOException{
		//genAdjRoad();
		traversePoi();
	}
	
}
