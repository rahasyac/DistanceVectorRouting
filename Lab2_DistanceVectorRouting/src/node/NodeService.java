package node;

import common.Message;
import common.Utility;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import master.MasterFrame;

/**
 * NodeService is the thread that serves on connection from client
 */
public class NodeService extends Thread{
    
    /**
     * client socket
     */
    private Socket connectionSocket;
    
    /**
     * reference to node server
     */
    private NodeServer nodeServer;
    
    /**
     * output to client
     */
    private ObjectOutputStream outToClient;
    
    /**
     * input from client
     */
    private ObjectInputStream inFromClient;
        
    /**
     * constructor
     * @param connectionSocket
     * @param nodeServer 
     */
    public NodeService(Socket connectionSocket, NodeServer nodeServer) {
        this.connectionSocket = connectionSocket;
        this.nodeServer = nodeServer;
    }
    
    public void run(){
        
        try{   
            
           outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
           inFromClient = new ObjectInputStream(connectionSocket.getInputStream());  
                
            //read message
            Message message = (Message)inFromClient.readObject();
            
            if (message.type == Message.REQUEST_DV){
                
                message.DV = nodeServer.data.DV;
                
                //reply message
                outToClient.writeObject(message);
            }else if (message.type == Message.REQUEST_DV_RT){
                
                message.DV = nodeServer.data.DV;
                message.routingTable = nodeServer.data.routingTable;
                
                //reply message
                outToClient.writeObject(message);
            }else if (message.type == Message.REQUEST_NEIGHBOUR_DVS){
                
                for (int i = 0; i < nodeServer.data.neighbors.size(); i++){
            
                    try {

                        Socket clientSocket = new Socket(nodeServer.data.neighbors.get(i).getAddress(), nodeServer.data.neighbors.get(i).getPort());
                        ObjectOutputStream toServer = new ObjectOutputStream(clientSocket.getOutputStream());    
                        ObjectInputStream fromClient = new ObjectInputStream(clientSocket.getInputStream()); 
                        
                        //send request
                        Message request = new Message();
                        request.type = Message.REQUEST_DV;
                        toServer.writeObject(request);

                        //read message
                        message = (Message)fromClient.readObject();
                        
                        clientSocket.close();

                        //update
                        nodeServer.data.DVNeigbhors.set(i, message.DV);                        

                    } catch (Exception ex) {
                        Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                //reply message
                outToClient.writeObject(message);
                
            }else if (message.type == Message.REQUEST_UPDATE_DV){
             
                if (nodeServer.data.nodeInfo.getNodeNumber() == 3){
                    System.out.println();
                }
                
                int myIndex = nodeServer.data.nodeInfo.getNodeNumber() - 1; //node index
                
                for (int fromIndex = 0; fromIndex < nodeServer.getNumNodes(); fromIndex++){
                    
                    for (int toNode = 0; toNode < nodeServer.getNumNodes(); toNode++){
                                
                        if (fromIndex == toNode){
                            nodeServer.data.DV[fromIndex][toNode] = 0;
                        }else{
                            if (myIndex == fromIndex){
                                //ask its neighbors
                                for (int i = 0; i < nodeServer.data.neighbors.size(); i++){

                                    int neighborIndex = nodeServer.data.neighbors.get(i).getNodeNumber() - 1; //neighbor node row index

                                    nodeServer.data.DV[fromIndex][toNode] = Math.min(nodeServer.data.DV[fromIndex][toNode], 
                                    nodeServer.data.network[fromIndex][neighborIndex] + nodeServer.data.DVNeigbhors.get(i)[neighborIndex][toNode]);                    
                                }
                            }else{

                                //ask its neighbors
                                for (int i = 0; i < nodeServer.data.neighbors.size(); i++){
                                    nodeServer.data.DV[fromIndex][toNode] = Math.min(nodeServer.data.DV[fromIndex][toNode], 
                                        nodeServer.data.DVNeigbhors.get(i)[fromIndex][toNode]);       
                                }
                            }
                        }
                    }
                } 
                
                //reply message
                outToClient.writeObject(message);               

            }else if (message.type == Message.REQUEST_CHANGE_COST){
                
                nodeServer.calculate(message.costs);
                
                //reply message
                outToClient.writeObject(message);   
            }  
            
            
            outToClient.close();
            inFromClient.close();
            connectionSocket.close(); 
           
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
