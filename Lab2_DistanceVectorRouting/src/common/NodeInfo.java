package common;

/**
 * Node information
 */
public class NodeInfo {
    
    /**
     * node number
     */
    private int nodeNumber;
    
    /**
     * Server address
     */
    private String address;
    
    /**
     * port
     */
    private int port;

    /**
     * constructor
     * 
     * @param nodeNumber nodeNumber
     * @param address address
     * @param port port
     */
    public NodeInfo(int nodeNumber, String address, int port) {
        this.nodeNumber = nodeNumber;
        this.address = address;
        this.port = port;
    }

    /**
     * get address
     * @return  address
     */
    public String getAddress() {
        return address;
    }

    /**
     * port
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * get node number
     * @return node number
     */
    public int getNodeNumber() {
        return nodeNumber;
    }
    
    
            
}
