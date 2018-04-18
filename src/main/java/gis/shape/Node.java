package gis.shape;

import gis.shape.sub.Point;
import gis.util.Separtor;

import java.util.HashSet;


public class Node {
	String mapId;
	String Id;
	int kindNum; 	//种别代码数
	String kind;
	int crossFlag; 	//路口种别代码 0:不是路口的节点 1:复合路口的子点，2：单一路口 3：复合路口主点
	String  lightFlag; 	//红绿灯标示 0：无  1：有
	String crossLid; 	//路口连接的link，用"|"分隔 种别代码是2或3时有效
	String mainNodeID;
	public String subNodeID; 	//路口子点号码（ M） （路口标识为 3 时有效）
	public String subNodeID2;
	String adjoinMID; 	//邻接点图幅号
	String adjoinNID; 	//邻接图幅的邻接点ID
	String nodeLID; 	//与node相连接的link
	String adjacentNode;
	Point nodePoint;
	
	public void setAdjacentNode(String adjacentNode){
		this.adjacentNode = adjacentNode;
	}
	
	public void setnodeLID(String nodeLID){
		this.nodeLID = nodeLID;
	}
	
	public Node(String st){
		this.fromSting(st);
	}
	
	public Point getPoint(){
		return this.nodePoint;
	}
	
	public String getId(){
		return this.Id;
	}
	
	public String getmapId(){
		return this.mapId;
	}
	
	public String getAdjoinNID(){
		return this.adjoinNID;
	}
	public HashSet<String> getNodeLID(){
		HashSet<String> ret = new HashSet<String>();
		
		String [] ids = {};
		if(this.nodeLID.indexOf(';')!=-1){
			ids =this.nodeLID.split(Separtor.lay2Sep);
		}else if(this.nodeLID.indexOf('|')!=-1){
			ids = this.nodeLID.split("\\|");
		}else{
			ret.add(this.nodeLID);
			return ret;
		}
		for (String id:ids){
			ret.add(id);
		}
		return ret;
	}
	/**
	 * @return
	 * 返回毗邻node的节点id
	 */
	public String getAdjoinNId(){
		return this.adjoinNID;
	}
	
//	public HashSet<String> getAdjoinNId(){
//		HashSet<String> ret = new HashSet<String>();
//		String [] ids = this.adjoinNID.split("\\|");
//		for (String id:ids){
//			ret.add(id);
//		}
//		return ret;
//	}
	
	public int getKindNum(){
		return kindNum;
	}
	public HashSet<String> getKind(){
		HashSet<String> ret = new HashSet<String>();
		String [] ids = this.kind.split("\\|");
		for (String id:ids){
			ret.add(id);
		}
		return ret;
	}
	public int getCrossFlag(){
		return crossFlag;
		
	}
	public HashSet<String> getCrossLid(){
		HashSet<String> ret = new HashSet<String>();
		String [] ids = crossLid.split(Separtor.lay2Sep);
		for (String id:ids){
			ret.add(id);
		}
		return ret;
	}
	
	public boolean fromSting(String s) {
		String[] seq = s.split(",");
		if (seq.length < 14){
			return false;
		}
		this.mapId = seq[0];
		this.Id = seq[1];
		this.kindNum = Integer.parseInt(seq[2]);
		this.kind = seq[3];
		this.crossFlag = Integer.parseInt(seq[4]); //路口标示
		this.lightFlag = seq[5];
		this.crossLid = seq[6];
		this.mainNodeID = seq[7];
		this.subNodeID = seq[8];
		this.subNodeID2 = seq[9];
		this.adjoinMID = seq[10];
		this.adjoinNID = seq[11];
		this.nodeLID = seq[12];
		try{
			String xy = seq[13].substring(seq[13].indexOf("(") +1 , seq[13].indexOf(")"));
			double x = Double.parseDouble(xy.split(" ")[0]);
			double y = Double.parseDouble(xy.split(" ")[1]);
			this.nodePoint = new Point(x,y);
		}catch(Exception e){
			System.out.println(s);
		}
		return true;
	}
	
	public String getMainNodeId(){
		return mainNodeID;
	}
	public HashSet<String> getSubNodeId(){
		HashSet<String> subNodeSet = new HashSet<String>();
		String subNodes = subNodeID;
		if (!subNodeID2.isEmpty()){
			subNodes +="|" + subNodeID2 ;
		}
		String [] strtmp = subNodes.split("\\|");
		for (String s:strtmp){
			subNodeSet.add(s);
		}
		return subNodeSet;
	}
	public String toStr() {
		String ret ="";
		ret += this.mapId +",";
		ret += this.Id + ",";
		ret += this.kindNum + ",";
		ret += this.kind + ",";
		ret += this.crossFlag + ",";
		ret += this.lightFlag + ",";
		ret += this.crossLid + ",";
		ret += this.mainNodeID + ",";
		ret += this.subNodeID + ",";
		ret += this.subNodeID2 + ",";
		ret += this.adjoinMID + ",";
		ret += this.adjoinNID + ",";
		ret += this.nodeLID + ",";
		ret += this.adjacentNode + ",";
		
		ret += "POINT (" +this.nodePoint.m_Longitude+" "+this.nodePoint.m_Latitude + ")";
		return ret;
	}
//	"MapID","ID", "Kind_num", "Kind", "Cross_flag", "Light_flag", "Cross_LID" , "mainNodeID" , 	"subNodeID" ,  "subNodeID2" , "Adjoin_MID" , "Adjoin_NID" ,  "Node_LID", "the_geom"
	public static void main(String []args){
		String node1 = "595603,10000181602,1,1f00,0,0,0,0,0,,595613,20000181602,2879194||2879194,POINT (116.41697 39.41667),";
		String[] seq  = node1.split(",");
		String a = seq[13].substring(seq[13].indexOf("(")+1, seq[13].indexOf(")"));
		Node n = new Node(node1);
		System.out.println(n.nodeLID);
		String [] b  =n.nodeLID.split("\\|\\|");
		for (String s:b){
		System.out.print(s+",");}
	}
	
}
