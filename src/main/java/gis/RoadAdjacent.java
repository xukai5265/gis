package gis;

import gis.shape.*;
import gis.shape.sub.Pair;
import gis.shape.sub.Point;
import gis.util.FileUtil;
import gis.util.GisUtil;
import gis.util.Separtor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class RoadAdjacent {
//	private static final Logger log = LoggerFactory.getLogger(RoadAdjacent.class);

	/**
	 * 获取相邻的link的id是有方向性的，只会沿着link的行驶方向搜索。
	 * 所以参数eNodeId未必是end节点，如果道路验证行驶方向前进，那么eNodeId就是end节点，否则是start节点。
	 * @param adjNodeMap
	 * 		存放一个省份中所有Node的HashMap
	 * @param EndNodeId
	 * 		存放需要关联的link的终点id
	 * @return
	 * 		返回与当前道路相连的3层link(只搜索主节点连接的link，不含交叉点内link)
	 */		
	public static String getRelated3LayerLink(HashMap<String,AdjacentNode> adjNodeMap, String sNodeId ,
											  String eNodeId, String linkId){
		
		HashSet<String> retLinks = new HashSet<String>();
		String RelatedLink = "";
		int loop = 0;
		HashSet<String> searchNode = new HashSet<String>();
		HashSet<String> searchedNode = new HashSet<String>();
		HashSet<String> searchedMainNode = new HashSet<String>();
		HashSet<String> mainNodes = new HashSet<String>();
		//HashSet<String> allLink = new HashSet<String>();

		searchedNode.add(sNodeId);
		searchNode.add(eNodeId);
		System.out.println(searchNode);
		if (adjNodeMap.get(eNodeId).getCrossFlag() == 3 || adjNodeMap.get(eNodeId).getCrossFlag() == 1){
			searchedMainNode.add(adjNodeMap.get(eNodeId).getMainNodeId());
		}
		//allLink.add(linkId);
		
		HashSet<String> tmpLinks = new HashSet<String>(); //临时存放当前循环生成的link
		while(loop < 3){ //循环过程可以总结为一边搜索node，一边收割毗邻的link
			HashSet<String> tmpNodes = new HashSet<String>(); //临时存放当前循环生成的node
			HashSet<String> tmpMainNodes = new HashSet<String>(); //临时存放当前循环生成的node
			for (String key : searchNode){
				//如果节点的crossflag为0或者1，直接获取相邻的link或者相邻的node
				System.out.println("loop num:"+loop);
				if(adjNodeMap.containsKey(key)){
					System.out.println("all key:"+key);
					if (adjNodeMap.get(key).getCrossFlag() == 0 || adjNodeMap.get(key).getCrossFlag() == 2){
						tmpNodes.addAll(adjNodeMap.get(key).getAdjNodeHS());
						tmpLinks.addAll(adjNodeMap.get(key).getNodeLID());
						//tmpNodes.removeAll(searchedNode);
					}else{
						String mainNode = adjNodeMap.get(key).getMainNodeId();
						tmpLinks.addAll(adjNodeMap.get(mainNode).getCrossLid());
						tmpMainNodes.addAll(getAdjoinNode(adjNodeMap,mainNode)); //所谓的mainNode就是crossflag为0,2,3的点
						tmpNodes.addAll(getAdjoinNode(adjNodeMap,mainNode));
						//tmpNodes.remove(mainNode);
						searchedNode.addAll(adjNodeMap.get(mainNode).getSubNodeId());
						searchedNode.add(mainNode);
						//System.out.println("getAdjoinNode :"+getAdjoinNode(adjNodeMap,mainNode));
						//System.out.println("tmpMainNodes :"+getAdjoinMainNode(adjNodeMap,mainNode));
						
					}
					System.out.println("tmpNodes key:"+tmpNodes);
					System.out.println("tmpLinks key:"+tmpLinks);

				}else{
					System.out.println("error key:"+key);
				}
			}
			tmpNodes.removeAll(searchedNode);
			searchedNode.addAll(tmpNodes);
			searchNode = tmpNodes;
			System.out.println("loop num "+loop+"'s searchNode out is :" + searchNode);
			tmpMainNodes.removeAll(searchedMainNode); //当前搜索出来的主节点去除掉已经搜索过的主节点
			searchedMainNode.addAll(tmpMainNodes); //加入到已经搜索过的序列
			searchNode.addAll(tmpMainNodes); //加入到下次搜索的队列中
			
			loop++;
			if(searchNode.size() == 0){
				break;
			}
		}
		//retLinks.add(toString(tmpLinks,"|"));
		return toString(tmpLinks,"|");
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
	public static String get1LayerTopology(HashMap<String,AdjacentNode> adjNodeMap,String sNodeId ,
			String eNodeId,String linkId,HashMap<String,Road1> roadMap){
		String ret = "";	
		HashSet<String> links= adjNodeMap.get(eNodeId).getNodeLID();
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
	 * 获取相邻的link的id是有方向性的，只会沿着link的行驶方向搜索。
	 * 所以参数eNodeId未必是end节点，如果道路验证行驶方向前进，那么eNodeId就是end节点，否则是start节点。
	 * @param adjNodeMap
	 * 		存放一个省份中所有Node的HashMap
	 * @param EndNodeId
	 * 		存放需要关联的link的终点id
	 * @return
	 * 		返回与当前道路相连的link(只搜索主节点连接的link，不含交叉点内link)
	 */		
	public static String getRelatedLink(HashMap<String,AdjacentNode> adjNodeMap,String sNodeId ,
			String eNodeId,String roadId){
		
		HashSet<String> retLinks = new HashSet<String>();
		String RelatedLink = "";
		int loop = 0;
		HashSet<String> searchNode = new HashSet<String>();
		HashSet<String> searchedNode = new HashSet<String>();
		HashSet<String> searchedMainNode = new HashSet<String>();
		HashSet<String> mainNodes = new HashSet<String>();
		//HashSet<String> allLink = new HashSet<String>();

		searchedNode.add(sNodeId);
		searchNode.add(eNodeId);
		System.out.println(searchNode);
		if (adjNodeMap.get(eNodeId).getCrossFlag() == 3 || adjNodeMap.get(eNodeId).getCrossFlag() == 1){
			searchedMainNode.add(adjNodeMap.get(eNodeId).getMainNodeId());
		}
		//allLink.add(linkId);
		
		HashSet<String> tmpLinks = new HashSet<String>(); //临时存放当前循环生成的link
		while(loop < 1){ //循环过程可以总结为一边搜索node，一边收割毗邻的link
			HashSet<String> tmpNodes = new HashSet<String>(); //临时存放当前循环生成的node
			HashSet<String> tmpMainNodes = new HashSet<String>(); //临时存放当前循环生成的node
			for (String key : searchNode){
				//如果节点的crossflag为0或者1，直接获取相邻的link或者相邻的node
				System.out.println("loop num:"+loop);
				if(adjNodeMap.containsKey(key)){
					System.out.println("all key:"+key);
					if (adjNodeMap.get(key).getCrossFlag() == 0 || adjNodeMap.get(key).getCrossFlag() == 2){
						tmpNodes.addAll(adjNodeMap.get(key).getAdjNodeHS());
						tmpLinks.addAll(adjNodeMap.get(key).getNodeLID());
						//tmpNodes.removeAll(searchedNode);
					}else{
						String mainNode = adjNodeMap.get(key).getMainNodeId();
						tmpLinks.addAll(adjNodeMap.get(mainNode).getCrossLid());
						tmpMainNodes.addAll(getAdjoinNode(adjNodeMap,mainNode)); //所谓的mainNode就是crossflag为0,2,3的点
						tmpNodes.addAll(getAdjoinNode(adjNodeMap,mainNode));
						//tmpNodes.remove(mainNode);
						searchedNode.addAll(adjNodeMap.get(mainNode).getSubNodeId());
						searchedNode.add(mainNode);
						//System.out.println("getAdjoinNode :"+getAdjoinNode(adjNodeMap,mainNode));
						//System.out.println("tmpMainNodes :"+getAdjoinMainNode(adjNodeMap,mainNode));
						
					}
					System.out.println("tmpNodes key:"+tmpNodes);
					System.out.println("tmpLinks key:"+tmpLinks);

				}else{
					System.out.println("error key:"+key);
				}
			}
			tmpNodes.removeAll(searchedNode);
			searchedNode.addAll(tmpNodes);
			searchNode = tmpNodes;
			System.out.println("loop num "+loop+"'s searchNode out is :" + searchNode);
			tmpMainNodes.removeAll(searchedMainNode); //当前搜索出来的主节点去除掉已经搜索过的主节点
			searchedMainNode.addAll(tmpMainNodes); //加入到已经搜索过的序列
			searchNode.addAll(tmpMainNodes); //加入到下次搜索的队列中
			
			loop++;
			if(searchNode.size() == 0){
				break;
			}
		}
		//retLinks.add(toString(tmpLinks,"|"));
		tmpLinks.remove(roadId);
		return toString(tmpLinks,"|");
	}
	
	/**
	 * genAdjRoad函数的备份函数
	 * @throws IOException
	 */
	public static void genAdjRoadBak() throws IOException{
		String nodeFile = "E:/roaddata2/out/hebei/road/NAdjacentOut.txt";
		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
		ArrayList<String> lines = FileUtil.readLines(nodeFile);
//		//String anode = "575404,2222696,1,10ff,0,0,0,0,0,,0,0,2791040|2793679,2222562|2213552|,POINT (114.57912 38.06371)";
//		//lines.add(anode);
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
			//System.out.println(tmp.toStr());
		}
		String roadFile = "E:/roaddata2/out/hebei/road/Rout.txt";
		HashMap<String,AdjacentNode> roadMap = new HashMap<String,AdjacentNode>();
		lines = FileUtil.readLines(roadFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File("E:/roaddata2/out/hebei/road/RAdjout.txt")), "UTF-8"));

		for (String line:lines){
		//String line = "87932107,|北外环路,|Ｇ１０４,,,j50f030019,2,55,2,271.0,,600,,1,0202,2,2120083,74242844,LINESTRING (116.34512 37.50894, 116.34359 37.5094, 116.34223 37.50978),";
			Road1 r = new Road1(line);

			String linkedId;
			if (r.getDirection() == 3){
				linkedId= getRelatedLink(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id);
			}else{
				linkedId= getRelatedLink(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id);
			}
			System.out.println(r.toStr(linkedId));
			linkedId =Double.toString(nodeMap.get(r.getSnodeID()).getPoint().m_Longitude) + Separtor.lay2Sep +
					Double.toString(nodeMap.get(r.getSnodeID()).getPoint().m_Latitude) + Separtor.lay1Sep + 
					Double.toString(nodeMap.get(r.getEnodeID()).getPoint().m_Longitude) + Separtor.lay2Sep +
					Double.toString(nodeMap.get(r.getEnodeID()).getPoint().m_Latitude) + Separtor.lay1Sep +
					linkedId;

			bw.write(r.toStr(linkedId));
			bw.write("\n");
			bw.flush();
		}
		
	}
	
	//
	/**
	 * 原始名称genAdjRoad()
	 * 测试函数
	 * 功能为调用函数生成道路及道路的相邻点，主要添加了可以生成道路的第一名称
	 * 从mif格式的数据转换过来为第一道数据，此函数执行第二道数据计算，即，找出道路的相邻道路；第三道为建立拓扑结构，完成即可上线使用
	 * @throws IOException
	 */
	public static void genAdjRoad() throws IOException{
		String nodeFile = "E:/roaddata2/out/hebei/road/NAdjacentOut.txt";
		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
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
		String outFile = "E:/roaddata2/test_out/hebei/road/RAdjout.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outFile)), "UTF-8"));

		String roadFile = "E:/roaddata2/out/hebei/road/Rout.txt";
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
//		int count = 0;
		for (String line:lines){
//			String line = "87932107,|北外环路,|Ｇ１０４,,,j50f030019,2,55,2,271.0,,600,,1,0202,2,2120083,74242844,LINESTRING (116.34512 37.50894, 116.34359 37.5094, 116.34223 37.50978),";
			Road1 r = new Road1(line);
			String linkedIdS2E = "",linkedIdE2S = "";
		
			if (r.getDirection() == 3){
				linkedIdE2S= get1LayerTopology(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id,roadHM);
				
			}else if(r.getDirection() == 2){
				//System.out.println(r.toStr());
				linkedIdS2E= get1LayerTopology(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id,roadHM);
			}else{
				linkedIdS2E= get1LayerTopology(nodeMap,r.getSnodeID(),r.getEnodeID(),r.id,roadHM);
				linkedIdE2S= get1LayerTopology(nodeMap,r.getEnodeID(),r.getSnodeID(),r.id,roadHM);
			}
			
			String invertRoad = "";
			if(r.isUpDownWardSplit()){
				invertRoad = getInvertedRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
			}else if(r.isUpDownWardSubRoad()){
				invertRoad = getInvertedSubRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
			}
			String linkedId = linkedIdS2E + Separtor.lay1Sep +linkedIdE2S + Separtor.lay1Sep + invertRoad;
			
					// + Separtor.lay1Sep + mainSubRoad;
			if(linkedId.equals("")){
				linkedId = Separtor.lay1Sep;
				System.out.println("___________----------------------___________");
			}
			bw.write(r.toRMStr(linkedId).replaceAll("\\|", Separtor.lay2Sep));
			bw.write("\n");
			bw.flush();
		}
	}
	
	public static void test3layerOld() throws IOException{
		String outFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOut.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outFile)), "UTF-8"));
		String outErrFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOutErr.txt";
		BufferedWriter bwErr = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outErrFile)), "UTF-8"));
		
		String roadFile = "E:/roaddata2/test_out/hebei/road/Radjout.txt";
		ArrayList<String>  lines = FileUtil.readLines(roadFile);
		HashMap<String,HashSet<String>> roadAdjHM = new HashMap<String,HashSet<String>>();
		for(String line:lines){
			HashSet<String> ids = new HashSet<String>();
			String  [] seg = line.split(Separtor.lay1Sep);
			if(seg[15].equals("")){
				roadAdjHM.put(seg[0], ids);
			}else{
				String [] tmpStr = seg[15].split(Separtor.lay2Sep);
				for(String s:tmpStr){
					ids.add(s);
				}
				roadAdjHM.put(seg[0], ids);
			}
		}
		int ii = 0;
		for (String line:lines){
//			if(ii > 2){
//				break;
//			}
			String [] seg= line.split(Separtor.lay1Sep);
			if (seg[15].equals("")){
				System.out.println("if (seg[15].equals('')) seg[15] is :"+seg[15]);
				bw.write(line);
				bw.write("\n");
				continue; 
			}
			String [] roadId = seg[15].split(Separtor.lay2Sep);
			HashSet<String> searchRoadIdHS = new HashSet<String>();
			for (String s: roadId){
				searchRoadIdHS.add(s);
			}
			System.out.println("searchRoadIdHS is :"+searchRoadIdHS);

			String roadAll = seg[15].replaceAll(Separtor.lay2Sep, Separtor.lay3Sep) + Separtor.lay2Sep;
			int times = 0;
			
			HashSet<String> tmpRoadIdHS = new HashSet<String>();
			HashSet<String> searchedRoadIdHS = new HashSet<String>();
			//searchedRoadIdHS.addAll(searchRoadIdHS);
			while(times < 2){
				String layerRoad = "";
				searchedRoadIdHS.addAll(searchRoadIdHS);

				for(String road:searchRoadIdHS){
					System.out.println("searchRoadIdHS is :"+searchRoadIdHS);
					System.out.println("times is :"+times);

					if (roadAdjHM.containsKey(road)){
						HashSet<String> roadIds = roadAdjHM.get(road);
						if(roadIds.size()>0){
							for(String s: roadIds){
								if(!searchedRoadIdHS.contains(s)){
									layerRoad += s + Separtor.lay3Sep;
								}
							}
						}
						tmpRoadIdHS.addAll(roadIds);
					}else{
						bwErr.write("road id adjHM get fail" + road);
						bwErr.write("\n");
					}
				}
				if(tmpRoadIdHS.size() == 0 && times==0){
					if (layerRoad.endsWith(Separtor.lay3Sep)){
						layerRoad.substring(0, layerRoad.lastIndexOf(Separtor.lay3Sep));
					}
					roadAll += layerRoad + Separtor.lay2Sep + Separtor.lay2Sep;
					break;
				}else{
					
					tmpRoadIdHS.removeAll(searchRoadIdHS);
					searchRoadIdHS.clear();
					searchRoadIdHS.addAll(tmpRoadIdHS);
					tmpRoadIdHS.clear();
				}
				if (layerRoad.endsWith(Separtor.lay3Sep)){
					layerRoad = layerRoad.substring(0, layerRoad.lastIndexOf(Separtor.lay3Sep));
				}
				roadAll += layerRoad + Separtor.lay2Sep;
				System.out.println("roadAll += layerRoad + Separtor.lay2Sep; is :"+layerRoad);

				times ++;
 			}
			if (roadAll.endsWith(Separtor.lay2Sep)){
				roadAll = roadAll.substring(0, roadAll.lastIndexOf(Separtor.lay2Sep));
			}
			String outStr = "";
			for (int i = 0 ; i < seg.length; i++){
				if(i == 15){
					outStr += roadAll + Separtor.lay1Sep;
				}else{
					outStr += seg[i] + Separtor.lay1Sep;
				}
			}
			if (outStr.endsWith(Separtor.lay1Sep)){
				outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
			}
			
			bw.write(outStr);
			bw.write("\n");
			bw.flush();
			ii++;
		}
		
	}
	
	public static HashSet<String> getNextLayer(String rid,HashMap<String,HashSet<String>> roadS2EAdjHM,
			HashMap<String,HashSet<String>> roadE2SAdjHM, HashSet<String> roads, BufferedWriter bwErr) throws IOException{
		HashSet<String> ret = new HashSet<String>();
		for(String road : roads){
			if(!roadE2SAdjHM.containsKey(rid) || !roadS2EAdjHM.containsKey(rid)){
				bwErr.write("!roadE2SAdjHM.containsKey(rid) || !roadS2EAdjHM.containsKey(rid)" + rid);
				bwErr.write("\n");
			}else{
				if(roadE2SAdjHM.get(road).contains(rid)){
					ret.addAll(roadS2EAdjHM.get(road));
				}else if(roadS2EAdjHM.get(road).contains(rid)){
					ret.addAll(roadE2SAdjHM.get(road));
				}else{
					ret.addAll(roadE2SAdjHM.get(road));
					ret.addAll(roadS2EAdjHM.get(road));
				}
			}
		}
		return ret;
	}
	
	/**
	 * 获取道路的N层拓扑关系，
	 * 
	 * @param rid
	 * @param roadS2EAdjHM
	 * @param roadE2SAdjHM
	 * @param roads
	 * @param bwErr
	 * @param N 需要拓扑道路的层数
	 * @return
	 * @throws IOException
	 */
	public static String getNlayer(String rid,HashMap<String,HashSet<String>> roadS2EAdjHM,
			HashMap<String,HashSet<String>> roadE2SAdjHM, HashSet<String> roads, 
			BufferedWriter bwErr,int N) throws IOException{
		String ret = "";
		int i = 0;
		if(roads.size()==0){
			System.out.println("roads.size()==0 : "+roads);
			return "";
		}
		for(String s : roads){
			ret += s + Separtor.lay3Sep;
		}
		if(ret.endsWith(Separtor.lay3Sep)){
			ret = ret.substring(0, ret.lastIndexOf(Separtor.lay3Sep));
		}
		ret += Separtor.lay2Sep;
		HashSet<String> searchedHS = new HashSet<String>();
		searchedHS.addAll(roads);
		while (i < N){
			System.out.println("roads " +i+ " times : "+roads);
			HashSet<String> tmpRoadOut = getNextLayer(rid,roadS2EAdjHM,roadE2SAdjHM,roads,bwErr);
			tmpRoadOut.removeAll(searchedHS);
			System.out.println("tmpRoadOut " +i+ " times : "+tmpRoadOut);
			if(tmpRoadOut.size()!=0){
				for(String s : tmpRoadOut){
					ret += s + Separtor.lay3Sep;
				}
			}
			
			if(ret.endsWith(Separtor.lay3Sep)){
				ret = ret.substring(0, ret.lastIndexOf(Separtor.lay3Sep));
			}
			ret += Separtor.lay2Sep;
			roads = tmpRoadOut;
			searchedHS.addAll(tmpRoadOut);
			i++;
		}
		if(ret.endsWith(Separtor.lay2Sep)){
			ret = ret.substring(0, ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	
	
	/**
	 * 开发道路三层拓扑结构
	 * 
	 * @throws IOException
	 */
	public static void test3layer() throws IOException{
		String outFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOut.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outFile)), "UTF-8"));
		String outErrFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOutErr.txt";
		BufferedWriter bwErr = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(outErrFile)), "UTF-8"));
		
		//读取关联道路文件
		String roadFile = "E:/roaddata2/test_out/hebei/road/Radjout.txt";
		ArrayList<String>  lines = FileUtil.readLines(roadFile);
		HashMap<String,HashSet<String>> roadAdjHM = new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> roadS2EAdjHM = new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> roadE2SAdjHM = new HashMap<String,HashSet<String>>();
		for(String line:lines){
			HashSet<String> ids = new HashSet<String>();
			String  [] seg = line.split(Separtor.lay1Sep);
			if(seg[15].equals("")){
				roadS2EAdjHM.put(seg[0], ids);
			}else{
				String [] tmpStr = seg[15].split(Separtor.lay2Sep);
				for(String s:tmpStr){
					ids.add(s);
				}
				roadS2EAdjHM.put(seg[0], ids);
			}

			//ids.clear();  //ids如果清空，字典中存储的数组也成空的了
			HashSet<String> idsE2S = new HashSet<String>(); 
			if(seg[16].equals("")){
				roadE2SAdjHM.put(seg[0], idsE2S);
			}else{
				String [] tmpStr = seg[16].split(Separtor.lay2Sep);
				for(String s:tmpStr){
					idsE2S.add(s);
				}
				roadE2SAdjHM.put(seg[0], idsE2S);
			}
		}
		int N = 2;
		for (String line:lines){
			String [] seg= line.split(Separtor.lay1Sep);
//			if(!seg[0].equals("12469068")){
//				continue;
//			}
			int dirct = Integer.parseInt(seg[14]);
			String outStr = "";
			if(dirct == 3){
				String rid =seg[0];
				HashSet<String> roads = roadE2SAdjHM.get(rid);
				String roadE2SAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 16){
						outStr += roadE2SAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}else if(dirct ==2){
				String rid =seg[0];
				HashSet<String> roads = roadS2EAdjHM.get(rid);
				String roadS2EAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 15){
						outStr += roadS2EAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}else{
				String rid =seg[0];
				HashSet<String> roads = roadS2EAdjHM.get(rid);
				String roadS2EAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				roads = roadE2SAdjHM.get(rid);
				String roadE2SAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 15){
						outStr += roadS2EAll + Separtor.lay1Sep;
					}else if(i==16){
						outStr += roadE2SAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}
			bw.flush();

//			if (seg[15].equals("")){
//				System.out.println("if (seg[15].equals('')) seg[15] is :"+seg[15]);
//				bw.write(line);
//				bw.write("\n");
//				continue;
//			}
//			String [] roadId = seg[15].split(Separtor.lay2Sep);
//			HashSet<String> searchRoadIdHS = new HashSet<String>();
//			for (String s: roadId){
//				searchRoadIdHS.add(s);
//			}
//			System.out.println("searchRoadIdHS is :"+searchRoadIdHS);
//
//			String roadAll = seg[15].replaceAll(Separtor.lay2Sep, Separtor.lay3Sep) + Separtor.lay2Sep;
//			int times = 0;
//			
//			HashSet<String> tmpRoadIdHS = new HashSet<String>();
//			HashSet<String> searchedRoadIdHS = new HashSet<String>();
//			//searchedRoadIdHS.addAll(searchRoadIdHS);
//			while(times < 2){
//				String layerRoad = "";
//				searchedRoadIdHS.addAll(searchRoadIdHS);
//
//				for(String road:searchRoadIdHS){
//					System.out.println("searchRoadIdHS is :"+searchRoadIdHS);
//					System.out.println("times is :"+times);
//
//					if (roadAdjHM.containsKey(road)){
//						HashSet<String> roadIds = roadAdjHM.get(road);
//						if(roadIds.size()>0){
//							for(String s: roadIds){
//								if(!searchedRoadIdHS.contains(s)){
//									layerRoad += s + Separtor.lay3Sep;
//								}
//							}
//						}
//						tmpRoadIdHS.addAll(roadIds);
//					}else{
//						bwErr.write("road id adjHM get fail" + road);
//						bwErr.write("\n");
//					}
//				}
//				if(tmpRoadIdHS.size() == 0 && times==0){
//					if (layerRoad.endsWith(Separtor.lay3Sep)){
//						layerRoad.substring(0, layerRoad.lastIndexOf(Separtor.lay3Sep));
//					}
//					roadAll += layerRoad + Separtor.lay2Sep + Separtor.lay2Sep;
//					break;
//				}else{
//					
//					tmpRoadIdHS.removeAll(searchRoadIdHS);
//					searchRoadIdHS.clear();
//					searchRoadIdHS.addAll(tmpRoadIdHS);
//					tmpRoadIdHS.clear();
//				}
//				if (layerRoad.endsWith(Separtor.lay3Sep)){
//					layerRoad = layerRoad.substring(0, layerRoad.lastIndexOf(Separtor.lay3Sep));
//				}
//				roadAll += layerRoad + Separtor.lay2Sep;
//				System.out.println("roadAll += layerRoad + Separtor.lay2Sep; is :"+layerRoad);
//
//				times ++;
// 			}
//			if (roadAll.endsWith(Separtor.lay2Sep)){
//				roadAll = roadAll.substring(0, roadAll.lastIndexOf(Separtor.lay2Sep));
//			}
//			String outStr = "";
//			for (int i = 0 ; i < seg.length; i++){
//				if(i == 15){
//					outStr += roadAll + Separtor.lay1Sep;
//				}else{
//					outStr += seg[i] + Separtor.lay1Sep;
//				}
//			}
//			if (outStr.endsWith(Separtor.lay1Sep)){
//				outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
//			}
//			
//			bw.write(outStr);
//			bw.write("\n");
//			bw.flush();
//			ii++;
		}
		
	}
	
	public static BufferedWriter getBWriter(String outErrFile) throws IOException{
		return new BufferedWriter(new OutputStreamWriter( 
				new FileOutputStream(new File(outErrFile)), "UTF-8"));
	}
	
	/*
	 * 获取无名称的加油站信息
	 * 
	 */
	public static  ArrayList<String> readOilStation(HashMap<String,String> roadMap,String poiFile) throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
//		String poiFile = "E:/roaddata2/out/hebei/road/Radjout.txt";
//		String outFile = "E:/roaddata2/out/hebei/road/oil_station_out.txt";
		
		BufferedWriter bw = getBWriter(poiFile);
		ArrayList<String>  lines = FileUtil.readLines(poiFile);
		for (String line: lines){
			String [] seg= line.split(",");
			if (roadMap.containsKey(seg[17])&&!roadMap.get(seg[17]).equals("")){
				ret.add(line);
			}
		}
		return ret;
	}
	
	public static HashMap<String,String> getRoad(String roadFile) throws IOException{
		HashMap<String, String> roadMap = new HashMap<String,String>();
		
		ArrayList<String> lines = FileUtil.readLines(roadFile);
		for (String line:lines){
			String [] seg= line.split(",");
			if (!roadMap.containsKey(seg[0])){
				roadMap.put(seg[0], seg[1] +Separtor.lay2Sep+ seg[2]);
			}
		}
		return roadMap;
	}
	
	public static void isHighRoadOilStation() throws IOException{
		String outFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOut.txt";
		String oilFile = "E:/roaddata2/out/hebei/road/poiOut.txt";
		BufferedWriter bw = getBWriter(outFile); 

		String outErrFile = "E:/roaddata2/test_out/hebei/road/RAdj3layerOutErr.txt";
		BufferedWriter bwErr = getBWriter(outErrFile);
		
		String roadFile = "E:/roaddata2/test_out/hebei/road/Radjout.txt";
		ArrayList<String>  lines = FileUtil.readLines(roadFile);
		
		ArrayList<String> oilStations = readOilStation(getRoad(roadFile),oilFile);
//		for(String oilS:oilStations){
//			String []seg =oilS.split(",");
//			
//		}
		
		HashMap<String,HashSet<String>> roadAdjHM = new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> roadS2EAdjHM = new HashMap<String,HashSet<String>>();
		HashMap<String,HashSet<String>> roadE2SAdjHM = new HashMap<String,HashSet<String>>();
		for(String line:lines){
			HashSet<String> ids = new HashSet<String>();
			String [] seg = line.split(Separtor.lay1Sep);
			if(seg[15].equals("")){
				roadS2EAdjHM.put(seg[0], ids);
			}else{
				String [] tmpStr = seg[15].split(Separtor.lay2Sep);
				for(String s:tmpStr){
					ids.add(s);
				}
				roadS2EAdjHM.put(seg[0], ids);
			}

			//ids.clear();  //ids如果清空，字典中存储的数组也成空的了
			HashSet<String> idsE2S = new HashSet<String>();
			if(seg[16].equals("")){
				roadE2SAdjHM.put(seg[0], idsE2S);
			}else{
				String [] tmpStr = seg[16].split(Separtor.lay2Sep);
				for(String s:tmpStr){
					idsE2S.add(s);
				}
				roadE2SAdjHM.put(seg[0], idsE2S);
			}
		}
		int N = 2;
		for (String line:lines){
			String [] seg= line.split(Separtor.lay1Sep);
//			if(!seg[0].equals("12469068")){
//				continue;
//			}
			int dirct = Integer.parseInt(seg[14]);
			String outStr = "";
			if(dirct == 3){
				String rid =seg[0];
				HashSet<String> roads = roadE2SAdjHM.get(rid);
				String roadE2SAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 16){
						outStr += roadE2SAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}else if(dirct ==2){
				String rid =seg[0];
				HashSet<String> roads = roadS2EAdjHM.get(rid);
				String roadS2EAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 15){
						outStr += roadS2EAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}else{
				String rid =seg[0];
				HashSet<String> roads = roadS2EAdjHM.get(rid);
				String roadS2EAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				roads = roadE2SAdjHM.get(rid);
				String roadE2SAll = getNlayer( rid, roadS2EAdjHM, roadE2SAdjHM, roads,  bwErr, N);
				for (int i = 0 ; i < seg.length; i++){
					if(i == 15){
						outStr += roadS2EAll + Separtor.lay1Sep;
					}else if(i==16){
						outStr += roadE2SAll + Separtor.lay1Sep;
					}else{
						outStr += seg[i] + Separtor.lay1Sep;
					}
				}
				if (outStr.endsWith(Separtor.lay1Sep)){
					outStr = outStr.substring(0, outStr.lastIndexOf(Separtor.lay1Sep));
				}
				bw.write(outStr);
				bw.write("\n");
			}
			bw.flush();
		}
		
	}
	
	
	/**
	 * @throws IOException 
	 * 
	 * 
	 */
//	public static void  unifyRoadT() throws IOException{
//		String nodeFile = "E:/roaddata2/out/hebei/road/NAdjacentOut.txt";
//		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
//		ArrayList<String> lines = FileUtil.readLines(nodeFile);	
//		for (String line:lines){
//			String [] seg = line.split(",");
//			String NodeLine = "";
//			for(int i=0;i<15;i++){
//				if (i != 13){
//					NodeLine += seg[i] +",";
//				}
//			}
//			NodeLine = NodeLine.substring(0, NodeLine.lastIndexOf(","));
//			AdjacentNode tmp = new AdjacentNode(NodeLine,seg[13]);
//			nodeMap.put(tmp.getId(), tmp);
//		}
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream(new File("E:/roaddata2/test_out/hebei/road/RAdjout.txt")), "UTF-8"));
//
//		String roadFile = "E:/roaddata2/out/hebei/road/Rout.txt";
//		HashMap<String,AdjacentNode> roadMap = new HashMap<String,AdjacentNode>();
//		lines = FileUtil.readLines(roadFile);
//		HashMap<String,Road1> roadHM = new HashMap<String,Road1>();
//		HashMap<String,HashSet<Road1>> roadHMByMapId = new HashMap<String,HashSet<Road1>>();
//		for(String line:lines){
//			Road1 r = new Road1(line);
//			roadHM.put(r.getId(), r);
//			String rMapId = r.getMapId();
//			if (!roadHMByMapId.containsKey(rMapId)){
//				HashSet<Road1> tmpHS = new HashSet<Road1>();
//				tmpHS.add(r);
//				roadHMByMapId.put(r.getMapId(), tmpHS);
//			}else{
//				roadHMByMapId.get(rMapId).add(r);
//			}
//		}
//		for (String line:lines){
//			Road1 r = new Road1(line);
//			
//			
//		}
//	}
	
	/**
	 * 相同名字道路统一编码
	 * 如果按照道路的编号，以及道路的名称顺序处理道路名称，则不需要编号，每次需要判断道路是不是同一条道路的时候，只需要将
	 */
//	public static void unifyRoad(String rId, HashMap<String,Road1> roadHM,
//			HashMap<String,HashSet<Road1>> roadHMByMapId,  HashMap<String,AdjacentNode> nodeMap){
//		HashMap<String,HashSet<String>> nameRoadHM = new HashMap<String, HashSet<String>>(); 
//		for(String r:roadHM.keySet()){
////			if (roadHM.get(r).getKindHS()){
////				
////			}
//		}
//	}
	
	/**
	 * 返回反向道路的id列表
	 * 方向相反，道路名称相同，有交集部分
	 * T代表对该函数进行测试
	 * @return
	 * @throws IOException 
	 */
	public static void getInvertedRoadT() throws IOException{
		String nodeFile = "E:/roaddata2/out/hebei/road/NAdjacentOut.txt";
		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
		ArrayList<String> lines = FileUtil.readLines(nodeFile);
//		//String anode = "575404,2222696,1,10ff,0,0,0,0,0,,0,0,2791040|2793679,2222562|2213552|,POINT (114.57912 38.06371)";
//		//lines.add(anode);
		
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
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File("E:/roaddata2/test_out/hebei/road/RAdjout.txt")), "UTF-8"));

		String roadFile = "E:/roaddata2/out/hebei/road/Rout.txt";
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
		double matchpercent = 0.0;
		int total_02_road = 0;
		int match_road = 0;

		for (String line:lines){
			Road1 r = new Road1(line);
//			if(!r.getId().equals("12469059")){
//				continue;
//			}
//			System.out.println(r.toStr());
			String invertRoad ="";
			if(r.isUpDownWardSplit()){
				invertRoad =getInvertedRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
			}else if(r.isUpDownWardSubRoad()){
				invertRoad =getInvertedSubRoad(r.getId(),roadHM,roadHMByMapId,nodeMap);
			}else{
				invertRoad = "";
			}
			//bw.write(invertRoad);
			if (r.isUpDownWardSplit() || r.isUpDownWardSubRoad()){
				total_02_road +=1;
			}
			if(!invertRoad.equals("")){
				match_road +=1;
			}
			bw.write(r.toStr(invertRoad));
			bw.write("\n");
			bw.flush();
			invertRoad = "";
			//System.out.println(invertRoad);
			System.out.println(" percent of match road" +"\t"+ match_road +"\t"+ total_02_road);

		}
	}
	
	public static String  getInvertedSubRoad(String rId, HashMap<String,Road1> roadHM,
			HashMap<String,HashSet<Road1>> roadHMByMapId,HashMap<String,AdjacentNode> nodeMap){
		String ret = "";
		//获取道路所在的mapid，在同一个mapid中查找道路的反向道路
		String mapId = roadHM.get(rId).getMapId();
		Road1 nowRoad = roadHM.get(rId);
		HashSet<Road1> roadIdS = roadHMByMapId.get(mapId);
		int rejectPointCount = 0;
		double nowRoadAngle;
		if(nowRoad.getDirection() == 3){
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getEnodeID()).getPoint(),
					nodeMap.get(nowRoad.getSnodeID()).getPoint());
		}else{
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getSnodeID()).getPoint(),
						nodeMap.get(nowRoad.getEnodeID()).getPoint());
		}
		ArrayList<Point> nowRoadPointList = nowRoad.pointList;
//		System.out.println("road: "+rId + "\t"+nowRoadAngle);
//		System.out.println("now road and angle: "+rId+ "\t"+nowRoadAngle);
//		System.out.println("now road and name: "+rId+ "\t"+nowRoad.getName());
//		System.out.println("now road and isUpDownWardSplit: "+rId+ "\t"+nowRoad.isUpDownWardSplit());

		for(Road1 r:roadIdS){
			double rAngle;
			if(r.getDirection() == 3){
				rAngle = GisUtil.getAngle(nodeMap.get(r.getEnodeID()).getPoint(),
					nodeMap.get(r.getSnodeID()).getPoint());
			}
			else{
				rAngle = GisUtil.getAngle(nodeMap.get(r.getSnodeID()).getPoint(),
						nodeMap.get(r.getEnodeID()).getPoint());
			}
			
			ArrayList<Point> rPointList = r.pointList;

			if(nowRoad.getName().equals(r.getFirstName()) && r.isUpDownWardSubRoad()){// < 1
//				System.out.println("name= road and angle: " + r.getId() + "\t" + rAngle + "\t" + Math.abs((Math.abs(rAngle - nowRoadAngle) - 180)));
//				System.out.println("name= road and name: " + r.getId() + "\t" + nowRoad.getName() + "\t" +(nowRoad.getName().equals(r.getName())));
//				System.out.println("name= road and isUpDownWardSplit: " + r.getId()+ "\t" + r.isUpDownWardSplit());
			}
			if (!nowRoad.getFirstName().equals("")) {
				if (r.isUpDownWardSubRoad()
						&& nowRoad.getFirstName().equals(r.getFirstName())
						&& Math.abs(Math.abs(rAngle - nowRoadAngle) - 180) < 90) {
					// System.out.println("road 内循环 and angle: "+r.getId()+  "\t"+rAngle);
					// System.out.println("road 内循环 and name: "+r.getId()+  "\t"+nowRoad.getName());
					// System.out.println("road 内循环 and isUpDownWardSplit: "+r.getId()+  "\t"+r.isUpDownWardSplit());
					// 当前道路向道路r投影
					for (Point p : nowRoadPointList) {
						Pair tmpPair = r.getPointAndDistance(p, new Point3D(p), 0);
						// System.out.println("tmpPair: "+tmpPair.toString());
						// System.out.println("road 内循环 tmpPair.value1 : "+r.getId()+  "\t"+tmpPair.value1);
						// System.out.println("road 内循环 tmpPair.type : "+r.getId()+  "\t"+tmpPair.type);
						// System.out.println("road 内循环 tmpPair.value2 index : "+r.getId()+  "\t"+tmpPair.value2);
						// System.out.println("road 内循环 tmpPair.value2 point : "+r.getId()+  "\t"+tmpPair.key.toString());

						if (tmpPair.type.equals("A") && tmpPair.value1 < 100) {
							rejectPointCount += 1;
						}
					}
					// 道路r向当前道路投影
					for (Point p : r.pointList) {
						Pair tmpPair = nowRoad.getPointAndDistance(p,
								new Point3D(p), 0);
						// System.out.println("tmpPair: "+tmpPair.toString());
						// System.out.println("road 内循环 tmpPair.value1 : "+r.getId()+  "\t"+tmpPair.value1);
						// System.out.println("road 内循环 tmpPair.type : "+r.getId()+  "\t"+tmpPair.type);
						// System.out.println("road 内循环 tmpPair.value2 index : "+r.getId()+  "\t"+tmpPair.value2);
						// System.out.println("road 内循环 tmpPair.value2 point : "+r.getId()+  "\t"+tmpPair.key.toString());

						if (tmpPair.type.equals("A") && tmpPair.value1 < 100) {
							rejectPointCount += 1;
						}
					}
				} else {
					if (r.isUpDownWardSubRoad()
							&& nowRoad.getFirstName().equals(r.getFirstName())
							&& Math.abs(Math.abs(rAngle - nowRoadAngle) - 180) < 10) {
						// System.out.println("road 内循环 and angle: "+r.getId()+  "\t"+rAngle);
						// System.out.println("road 内循环 and name: "+r.getId()+  "\t"+nowRoad.getName());
						// System.out.println("road 内循环 and isUpDownWardSplit: "+r.getId()+  "\t"+r.isUpDownWardSplit());
						// 当前道路向道路r投影
						for (Point p : nowRoadPointList) {
							Pair tmpPair = r.getPointAndDistance(p, new Point3D(p), 0);
							// System.out.println("tmpPair: "+tmpPair.toString());
							// System.out.println("road 内循环 tmpPair.value1 : "+r.getId()+  "\t"+tmpPair.value1);
							// System.out.println("road 内循环 tmpPair.type : "+r.getId()+  "\t"+tmpPair.type);
							// System.out.println("road 内循环 tmpPair.value2 index : "+r.getId()+  "\t"+tmpPair.value2);
							// System.out.println("road 内循环 tmpPair.value2 point : "+r.getId()+  "\t"+tmpPair.key.toString());

							if (tmpPair.type.equals("A")
									&& tmpPair.value1 < 40) {
								rejectPointCount += 1;
							}
						}
						// 道路r向当前道路投影
						for (Point p : r.pointList) {
							Pair tmpPair = nowRoad.getPointAndDistance(p, new Point3D(p), 0);
							// System.out.println("tmpPair: "+tmpPair.toString());
							// System.out.println("road 内循环 tmpPair.value1 : "+r.getId()+  "\t"+tmpPair.value1);
							// System.out.println("road 内循环 tmpPair.type : "+r.getId()+  "\t"+tmpPair.type);
							// System.out.println("road 内循环 tmpPair.value2 index : "+r.getId()+  "\t"+tmpPair.value2);
							// System.out.println("road 内循环 tmpPair.value2 point : "+r.getId()+  "\t"+tmpPair.key.toString());

							if (tmpPair.type.equals("A") && tmpPair.value1 < 40) {
								rejectPointCount += 1;
							}
						}
					}
				}
				if (rejectPointCount >= 1) {
					ret += r.id + Separtor.lay2Sep;
					// System.out.println("road and angle: "+r.getId()+  "\t"+rAngle);
					// System.out.println("road and name: "+r.getId()+  "\t"+nowRoad.getName());
					// System.out.println("road and isUpDownWardSplit: "+r.getId()+  "\t"+r.isUpDownWardSplit());

				}
				rejectPointCount = 0;
			}
		}
		if (ret.endsWith(Separtor.lay2Sep)){
			
			ret = ret.substring(0,ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	
	/**
	 * 
	 * 
	 * @param rId
	 * @param roadHM
	 * @param roadHMByMapId
	 * @param nodeMap
	 * @return
	 */
	public static String getInvertedRoad(String rId, HashMap<String,Road1> roadHM,
			HashMap<String,HashSet<Road1>> roadHMByMapId,HashMap<String,AdjacentNode> nodeMap){
		String ret = "";
		//获取道路所在的mapid，在同一个mapid中查找道路的反向道路
		String mapId = roadHM.get(rId).getMapId();
		Road1 nowRoad = roadHM.get(rId);
		HashSet<Road1> roadIdS = roadHMByMapId.get(mapId);
		int rejectPointCount = 0;
		double nowRoadAngle;
		if(nowRoad.getDirection() == 3){
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getEnodeID()).getPoint(),
					nodeMap.get(nowRoad.getSnodeID()).getPoint());
		}else{
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getSnodeID()).getPoint(),
						nodeMap.get(nowRoad.getEnodeID()).getPoint());
		}
		ArrayList<Point> nowRoadPointList = nowRoad.pointList;
//		System.out.println("road: "+rId + "\t"+nowRoadAngle);
//		System.out.println("now road and angle: "+rId+ "\t"+nowRoadAngle);
//		System.out.println("now road and name: "+rId+ "\t"+nowRoad.getName());
//		System.out.println("now road and isUpDownWardSplit: "+rId+ "\t"+nowRoad.isUpDownWardSplit());

		for (Road1 r : roadIdS) {
			double rAngle;
			if (r.getDirection() == 3) {
				rAngle = GisUtil.getAngle(nodeMap.get(r.getEnodeID())
						.getPoint(), nodeMap.get(r.getSnodeID()).getPoint());
			} else {
				rAngle = GisUtil.getAngle(nodeMap.get(r.getSnodeID())
						.getPoint(), nodeMap.get(r.getEnodeID()).getPoint());
			}

			ArrayList<Point> rPointList = r.pointList;

			if (!nowRoad.getFirstName().equals("")) {
				if (r.isUpDownWardSplit() && nowRoad.getFirstName().equals(r.getFirstName())
						&& Math.abs(Math.abs(rAngle - nowRoadAngle) - 180) < 90) {
					for (Point p : nowRoadPointList) {
						Pair tmpPair = r.getPointAndDistance(p, new Point3D(p), 0);
						if (tmpPair.type.equals("A") && tmpPair.value1 < 100) {
							rejectPointCount += 1;
						}
					}
					for (Point p : r.pointList) {
						Pair tmpPair = nowRoad.getPointAndDistance(p, new Point3D(p), 0);
						if (tmpPair.type.equals("A") && tmpPair.value1 < 100) {
							rejectPointCount += 1;
						}
					}
				}
			} else {
				if (r.isUpDownWardSplit() && nowRoad.getFirstName().equals(r.getFirstName())
						&& Math.abs(Math.abs(rAngle - nowRoadAngle) - 180) < 10) {
					for (Point p : nowRoadPointList) {
						Pair tmpPair = r.getPointAndDistance(p, new Point3D(p), 0);
						if (tmpPair.type.equals("A") && tmpPair.value1 < 40) {
							rejectPointCount += 1;
						}
					}
					for (Point p : r.pointList) {
						Pair tmpPair = nowRoad.getPointAndDistance(p, new Point3D(p), 0);
						if (tmpPair.type.equals("A") && tmpPair.value1 < 40) {
							rejectPointCount += 1;
						}
					}
				}
			}
			// System.out.println("road 内循环 rejectPointCount : "+r.getId()+ "\t"+rejectPointCount);
			if (rejectPointCount >= 1) {
				ret += r.id + Separtor.lay2Sep;
			}
			rejectPointCount = 0;
		}
			

		if (ret.endsWith(Separtor.lay2Sep)){
			
			ret = ret.substring(0,ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	
	/**
	 * 获取主辅路信息
	 * @return
	 */
	public static String getMainSubRoad(String rId,HashMap<String,Road1> roadHM,
			HashMap<String,HashSet<Road1>> roadHMByMapId,HashMap<String,AdjacentNode> nodeMap){
		String ret = "";
		String mapId = roadHM.get(rId).getMapId();
		Road1 nowRoad = roadHM.get(rId);
		HashSet<Road1> roadIdS = roadHMByMapId.get(mapId);
		int rejectPointCount = 0;
		double nowRoadAngle;
		if(nowRoad.getDirection() ==3){
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getEnodeID()).getPoint(), 
					nodeMap.get(nowRoad.getSnodeID()).getPoint());
		}else{
			nowRoadAngle = GisUtil.getAngle(nodeMap.get(nowRoad.getSnodeID()).getPoint(), 
						nodeMap.get(nowRoad.getEnodeID()).getPoint());
		}
		 ArrayList<Point> nowRoadPointList = nowRoad.pointList; 
		for(Road1 r:roadIdS){
			double rAngle;
			if(r.getDirection() == 3){
				rAngle= GisUtil.getAngle(nodeMap.get(r.getEnodeID()).getPoint(), 
					nodeMap.get(r.getSnodeID()).getPoint());
			}else{
				rAngle = GisUtil.getAngle(nodeMap.get(r.getSnodeID()).getPoint(), 
						nodeMap.get(r.getEnodeID()).getPoint());
			}
			ArrayList<Point> rPointList = r.pointList;
			if(r.isUpDownWardSplit() 
					&& nowRoad.getName().equals(r.getName())
					&&Math.abs(rAngle - nowRoadAngle) < 1){
				for(Point p:nowRoadPointList){
					Pair tmpPair= r.getPointAndDistance(p,new Point3D(p),0);
					if(tmpPair.key!=null&&tmpPair.value1<35){
						rejectPointCount += 1;
					}
				}
				if (rejectPointCount > 2){
					ret += r.id + Separtor.lay2Sep;
				}
			}
		}
		//如果是等于0会怎样？
		if(ret.lastIndexOf(Separtor.lay2Sep) > 0){
			ret = ret.substring(0,ret.lastIndexOf(Separtor.lay2Sep));
		}
		return ret;
	}
	/**
	 * 生成相应的道路
	 * @throws IOException
	 */
	public static void genCorrespondentRoad() throws IOException{
		String roadFile = "E:/roaddata2/out/hebei/road/Rout.txt";
		HashMap<String,AdjacentNode> nodeMap = new HashMap<String,AdjacentNode>();
		ArrayList<String> lines = FileUtil.readLines(roadFile);
//		//String anode = "575404,2222696,1,10ff,0,0,0,0,0,,0,0,2791040|2793679,2222562|2213552|,POINT (114.57912 38.06371)";
//		//lines.add(anode);
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
		
		for(String mId:roadHMByMapId.keySet()){
			HashSet<Road1> mapData = roadHMByMapId.get(mId);
			HashSet<String> oppositeRoad = new HashSet<String>();
			for(Road1 r1: mapData){
				if (r1.isUpDownWardSplit()){
					for(Road1 r2: mapData){
						if (r1.isUpDownWardSplit()&&r1.getName().equals(r2.getName())){
							if(r1.isOppositeRoadOf(r2)){
								oppositeRoad.add(r2.getId());
							}
						}
					}
				}
			}
		}
		
		String roadFile2 = "E:/roaddata2/out/hebei/road/Rout.txt";
		HashMap<String,AdjacentNode> roadMap = new HashMap<String,AdjacentNode>();
		lines = FileUtil.readLines(roadFile2);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File("E:/roaddata2/out/hebei/road/RAdjout.txt")), "UTF-8"));

		for (String line:lines){
			//String line = "87932107,|北外环路,|Ｇ１０４,,,j50f030019,2,55,2,271.0,,600,,1,0202,2,2120083,74242844,LINESTRING (116.34512 37.50894, 116.34359 37.5094, 116.34223 37.50978),";
			Road1 r = new Road1(line);
			String linkedId = getRelated3LayerLink(nodeMap,r.getSnodeID(),r.getEnodeID(),"12469082");
			System.out.println(r.toStr(linkedId));
			bw.write(r.toStr(linkedId));
			bw.write("\n");
			bw.flush();
		}
	}
	
	
	/**
	 *  获取当前主节点的关联节点
	 * 
	 * @param NodeMap
	 * @param mainNode
	 * @return
	 */
	public static HashSet<String> getAdjoinNode(HashMap<String,AdjacentNode> NodeMap,String mainNode){
		HashSet<String> ret = new HashSet<String>();
		HashSet<String> subNodes = NodeMap.get(mainNode).getSubNodeId();
		HashSet<String> tmp =new HashSet<String>();
		AdjacentNode adjNode = NodeMap.get(mainNode);
		System.out.println("in [getAdjoinNode] subNodes is: " + subNodes);
		System.out.println("in [getAdjoinNode] mainNode is: " + mainNode);
		for (String subNode:subNodes){
			AdjacentNode nowNode = NodeMap.get(subNode);
			tmp.addAll(nowNode.getAdjNodeHS());
		}
		for (String nodeId:tmp){
			int tmpCrossFlag = NodeMap.get(nodeId).getCrossFlag();
			if ( tmpCrossFlag == 0 ||tmpCrossFlag== 2 ||tmpCrossFlag== 3){
				ret.add(nodeId);
			}
		}
		//ret.addAll(adjNode.getAdjNodeHS());
		for(String s:adjNode.getAdjNodeHS()){
			int tmpCrossFlag = NodeMap.get(s).getCrossFlag(); 
			if ( tmpCrossFlag == 0 ||tmpCrossFlag== 2 ||tmpCrossFlag== 3){
				ret.add(s);
			}
			
		}
		//ret.removeAll(adjNode.getSubNodeId());
		ret.remove(mainNode);
		System.out.println("in [getAdjoinNode] ret is: " + ret);
		return ret;
	}
	
	/**
	 *  获取当前主节点的关联主节点
	 * 
	 * @param NodeMap
	 * @param mainNode
	 * @return
	 */
	public static HashSet<String> getAdjoinMainNode(HashMap<String,AdjacentNode> NodeMap,String mainNode){
		HashSet<String> ret = new HashSet<String>();
		HashSet<String> subNodes = NodeMap.get(mainNode).getSubNodeId();
		HashSet<String> tmp =new HashSet<String>();
		for (String subNode:subNodes){
			AdjacentNode nowNode = NodeMap.get(subNode);
			tmp.addAll(nowNode.getAdjNodeHS());
		}
		for (String nodeId:tmp){
			int tmpCrossFlag = NodeMap.get(nodeId).getCrossFlag();
			if ( tmpCrossFlag== 0 || tmpCrossFlag == 2 || tmpCrossFlag == 3){
				ret.add(nodeId);
			}
		}
		return ret;
	}
	
	public static String toString(HashSet<String> hs,String separator){
		String ret = "";
		if (hs.size()==0){
			return ret;
		}
		for (String s:hs){
			ret += s + separator;
		}
		
		if (ret.equals(separator)){
			return "";
		}else{
			return ret.substring(0, ret.lastIndexOf(separator));
		}
	}
	
//	//原始代码
//	public static String toString(HashSet<String> hs,String separator){
//		String ret = "";
//		for (String s:hs){
//			ret += s + separator;
//		}
//		if (ret.length() < separator.length()){return ret;}
//		return ret.substring(0, ret.length()-separator.length());
//	}
	
	public static void main(String [] args) throws IOException{
		
		genAdjRoad();
		test3layer();
		//getInvertedRoadT();
		//String a = "abc";
		//String b = new String("abc");
		//System.out.println(b.equals(a));
		
//		
//		String a = "aa:b";
//		String [] b= a.split(":");
//		System.out.println(b.length);
		//System.out.println();
	}
}
