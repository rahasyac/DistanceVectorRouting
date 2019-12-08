package common;

import java.io.Serializable;

/**
 * Message is sent/received by TCP socket
 */
public class Message implements Serializable{
    
    //request to send the distance vector table
    public static final int REQUEST_DV = 1;
    
    //request to send the distance vector table AND routing table
    public static final int REQUEST_DV_RT = 2;
    
    //request to retrieve neighbor DVs
    public static final int REQUEST_NEIGHBOUR_DVS = 3;
    
    //request to update DV based on neighbor DVs
    public static final int REQUEST_UPDATE_DV = 4;
    
    //request to change cost, recalculate the DV....
    public static final int REQUEST_CHANGE_COST = 5;
    
    /**
     * type of request
     */
    public int type;
    
     /**
     * Distance Vector table
     */
    public int[][] DV;
    
    /**
     * routing table
     */
    public int[][] routingTable;
    
     /**
     * costs
     */
    public int[][] costs;
    
}
