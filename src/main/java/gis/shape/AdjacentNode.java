package gis.shape;

import gis.util.Separtor;

import java.util.HashSet;



public class AdjacentNode extends Node {

	String adjacentNode;
	public AdjacentNode(String st,String adjacent) {
		super(st);
		this.adjacentNode = adjacent;
	}
	
	public String getAdjacentNode(){
		return adjacentNode;
	}
	
	public HashSet<String> getAdjNodeHS(){
		HashSet<String> adjNodeHS = new HashSet<String>();
		String [] adjNode = adjacentNode.split(Separtor.lay2Sep);
		for (String s:adjNode){
			if(!s.equals("")){
				adjNodeHS.add(s);
			}
		}
		return adjNodeHS;
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
}
