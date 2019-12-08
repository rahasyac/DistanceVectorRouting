package node;

import common.Configuration;
import common.NodeInfo;
import java.util.ArrayList;
import master.MasterFrame;
import java.util.List;

/**
 * data of node
 */
public class NodeData {    
   
    //address/port of this node
    NodeInfo nodeInfo;
    
    /**
     * initial cost of network
     */
    int[][] network;
    
    /**
     * Distance Vector table
     */
    int[][] DV = new int[Configuration.MAX_NODES][Configuration.MAX_NODES];
    
    /**
     * neighbors addresses/ports
     */
    List<NodeInfo> neighbors;
    
    /**
     * Distance Vector tables of neighbors
     */
    List<int[][]> DVNeigbhors;
    
    /**
     * routing table
     */
    int[][] routingTable = new int[Configuration.MAX_NODES][Configuration.MAX_NODES];
}
