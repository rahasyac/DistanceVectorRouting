package node;

import common.Configuration;
import common.NodeInfo;
import common.Utility;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * NodeServer is a thread that is TCP socket server
 * It represents a node in the system
 * that stores its DV
 */
public class NodeServer extends Thread{
    
    NodeData data = new NodeData();
    
    /**
     * number of nodes in the simulation
     */
    private int numNodes;
    
    public NodeServer(int numNodes, NodeInfo nodeInfo, int[][] network, List<NodeInfo> neighbors){       
        
        this.numNodes = numNodes;
        data.nodeInfo = nodeInfo;
        data.neighbors = neighbors;
        //setup data for node
        calculate(network);       
    }
    
    /**
     * setup data for node
     * calculate distance vector
     */
    public void calculate(int[][] network){
        
        data.network = network;
        data.DVNeigbhors = new ArrayList<>();
        
        for (int i = 0; i < numNodes; i++){
            for (int j = 0; j < numNodes; j++){
                data.DV[i][j] = Configuration.INFINITY;
            }
        }
        
        //cost to itself
        data.DV[data.nodeInfo.getNodeNumber() - 1][data.nodeInfo.getNodeNumber() - 1] = 0;
        
        //create distance vector
        for (int toNode = 0; toNode < numNodes; toNode++){
            //set the cost 
            if (data.network[data.nodeInfo.getNodeNumber() - 1][toNode] != 0){
                data.DV[data.nodeInfo.getNodeNumber() - 1][toNode] = data.network[data.nodeInfo.getNodeNumber() - 1][toNode];
            }
        }
        
        for (int i = 0; i < data.neighbors.size(); i++){
            data.DVNeigbhors.add(Utility.createMatrix());
        }
        
        //debug
        //System.out.println(Utility.DV2String(numNodes, data.network));
        //System.out.println(Utility.DV2String(numNodes, data.DV));
    }
    
    public void run(){
        
        System.out.println("Node " + data.nodeInfo.getNodeNumber() + " started...");
        
        try {
            ServerSocket serverSocket = new ServerSocket(data.nodeInfo.getPort());
            serverSocket.setSoTimeout(0);
            while (true) {

                Socket connectionSocket = serverSocket.accept();

                (new NodeService(connectionSocket, this)).start();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not start Node " + data.nodeInfo.getNodeNumber());
        }
    }

    /**
     * get number of nodes in simulation
     * @number of nodes in simulation
     */    
    public int getNumNodes() {
        return numNodes;
    }
    
    
}
