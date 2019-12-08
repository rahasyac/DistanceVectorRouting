package master;

import common.Configuration;
import common.Message;
import common.NodeInfo;
import common.Utility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import node.NodeServer;

/**
 *refrence: https://github.com/adamvh/VectorRouting
 * Master frame
 * This class is frame to control the simulation system with GUI
 * It also creates the nodes and load file to initialize the system
 */
public class MasterFrame extends javax.swing.JFrame {
   
    /**
     * input file name
     */
    private String inputFilename = "network.txt";
    
    /**
     * number of nodes
     */
    private int numNodes;
    
    /**
     * initial cost of network
     */
    private int[][] network;
    
    /**
     * node information
     */
    private List<NodeInfo> nodeInfoList = new ArrayList<>();
    
    /**
     * node servers
     */
    private List<NodeServer> nodeThreadList = new ArrayList<>();
    
    /**
     * output text areas
     */
    private final JTextArea[] outputs;
    
    /**
     * node DVs
     */
    private List<int[][]> nodeDVList = new ArrayList<>();
    
    /**
     * number of steps to move to stable state
     */
    private int numSteps = 0;
    
    /**
     * is stable
     */
    private boolean isStable = false;
    
    /**
     * Creates new form MasterFrame
     */
    public MasterFrame() {
        initComponents();
        
        setLocationRelativeTo(null); //center frame
        
        try {
            //default file
            loadNetworkFile();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Could not open file " + inputFilename);
        }
        
        outputs = new JTextArea[]{
            txtOutputNode1, txtOutputNode2, txtOutputNode3, txtOutputNode4,
            txtOutputNode5, txtOutputNode6                
        };
    }
    
    /**
     * start to run simulation
     * initialize the system
     * 1. create Nodes (TCP Socket Server)
     * 2. send initial data to them
     * 3. send message to them to retrieve the data (DV table, Routing table)
     */
    private void startSimulation(){
        
        nodeInfoList = new ArrayList<>();
        
        //initialize the node information
        for (int i = 0; i < numNodes; i++){
            nodeInfoList.add(new NodeInfo(i + 1, Configuration.NODE_ADDRESS, Configuration.BASIC_PORT + i + 1));
        }
        
        //create neighbors
        for (int i = 0; i < numNodes; i++){
            
            //neighbors of node i 
            List<NodeInfo> neighbors = new ArrayList<>();
            
            for (int j = 0; j < numNodes; j++){
                if (network[i][j] != 0){
                    neighbors.add(nodeInfoList.get(j));
                }
            }
            
            //create node server
            NodeServer server = new NodeServer(numNodes, nodeInfoList.get(i), network, neighbors);
         
            //add to list
            nodeThreadList.add(server);
        }
        
        //start nodes
        for (int i = 0; i < numNodes; i++){
            nodeThreadList.get(i).start();
        }
        
        try {
            //wait for 1 second
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            //ignore
        }
        
        //retrieve DV and routing table
        requestDV_RT();
        
        //display
        displayDV_RT();
    }
    
    /**
     * class that retrieves DV
     */
    private class RequestThread extends Thread{
        
        //DV from node
        int[][] DV;
        
        //node info
        NodeInfo info;
        
        //constructor
        RequestThread(NodeInfo info){
            this.info = info;
        }
        
        public void run(){
            try {

                Socket clientSocket = new Socket(info.getAddress(), info.getPort());
                ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());       

                //send request
                Message message = new Message();
                message.type = Message.REQUEST_DV_RT;
                outToServer.writeObject(message);

                //read response
                message = (Message)inFromClient.readObject();

                clientSocket.close();

                DV = message.DV;

            } catch (Exception ex) {
                Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * display
     */
    private void displayDV_RT(){
        for (int i = 0; i < numNodes; i++){
            outputs[i].setText(Utility.DV2String(numNodes, nodeDVList.get(i)));
        }
    }
    
    /**
     * request distance vector and routing table
     */
    private void requestDV_RT(){
        
        nodeDVList = new ArrayList<>();
        
        //request threads
        List<RequestThread> requestThreads = new ArrayList<>();
        
        //step 1
        for (int i = 0; i < numNodes; i++){
            
            RequestThread thread = new RequestThread(nodeInfoList.get(i));
            thread.start();
            requestThreads.add(thread);
        }
        for (int i = 0; i < numNodes; i++){
            try {
                requestThreads.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for (int i = 0; i < numNodes; i++){
            nodeDVList.add(requestThreads.get(i).DV);
        }
    }
    
    /**
     * load network from file
     */
    private void loadNetworkFile() throws FileNotFoundException{
        
        network = new int[Configuration.MAX_NODES][Configuration.MAX_NODES];
        
        //open for 
        Scanner input = new Scanner(new File(inputFilename));
        
        while (input.hasNextInt()){
            
            //from node, to node
            int fromNode = input.nextInt();
            int toNode = input.nextInt();
            int cost = input.nextInt();
            
            network[fromNode - 1][toNode - 1] = cost;
            network[toNode - 1][fromNode - 1] = cost;
            
            //calculate number of nodes
            numNodes = Math.max(numNodes, Math.max(fromNode, toNode));
        }
        
        //close scanner
        input.close();
        
        //System.out.println(Utility.DV2String(numNodes, network));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStableState = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutputNode3 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOutputNode6 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOutputNode4 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtOutputNode2 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtOutputNode1 = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtOutputNode5 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtToNode = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtFromNode = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtCost = new javax.swing.JTextField();
        btnChangeCost = new javax.swing.JButton();
        btnStart = new javax.swing.JButton();
        btnRun2End = new javax.swing.JButton();
        btnRunStep = new javax.swing.JButton();
        txtSimulationResult = new javax.swing.JLabel();
        lblRunningTime = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuOpenNetwork = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuItemExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Master");

        lblStableState.setText("---");

        txtOutputNode3.setEditable(false);
        txtOutputNode3.setColumns(20);
        txtOutputNode3.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode3.setRows(5);
        jScrollPane1.setViewportView(txtOutputNode3);

        txtOutputNode6.setEditable(false);
        txtOutputNode6.setColumns(20);
        txtOutputNode6.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode6.setRows(5);
        jScrollPane2.setViewportView(txtOutputNode6);

        txtOutputNode4.setEditable(false);
        txtOutputNode4.setColumns(20);
        txtOutputNode4.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode4.setRows(5);
        jScrollPane3.setViewportView(txtOutputNode4);

        txtOutputNode2.setEditable(false);
        txtOutputNode2.setColumns(20);
        txtOutputNode2.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode2.setRows(5);
        jScrollPane4.setViewportView(txtOutputNode2);

        txtOutputNode1.setEditable(false);
        txtOutputNode1.setColumns(20);
        txtOutputNode1.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode1.setRows(5);
        jScrollPane5.setViewportView(txtOutputNode1);

        txtOutputNode5.setEditable(false);
        txtOutputNode5.setColumns(20);
        txtOutputNode5.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtOutputNode5.setRows(5);
        jScrollPane6.setViewportView(txtOutputNode5);

        jLabel1.setText("Node 1:");

        jLabel2.setText("Node 2:");

        jLabel3.setText("Node 3:");

        jLabel4.setText("Node 4:");

        jLabel5.setText("Node 5:");

        jLabel6.setText("Node 6:");

        jLabel7.setText("From Node");

        txtToNode.setEditable(false);

        jLabel8.setText("To Node");

        txtFromNode.setEditable(false);

        jLabel9.setText("Cost");

        txtCost.setEditable(false);

        btnChangeCost.setText("Change Cost");
        btnChangeCost.setEnabled(false);
        btnChangeCost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeCostActionPerformed(evt);
            }
        });

        btnStart.setText("Start");
        btnStart.setEnabled(false);
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnRun2End.setText("Run to End");
        btnRun2End.setEnabled(false);
        btnRun2End.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRun2EndActionPerformed(evt);
            }
        });

        btnRunStep.setEnabled(false);
        btnRunStep.setLabel("Run One Step");
        btnRunStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunStepActionPerformed(evt);
            }
        });

        txtSimulationResult.setText("--------");

        mnuFile.setText("File");

        mnuOpenNetwork.setText("Open Network file");
        mnuOpenNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenNetworkActionPerformed(evt);
            }
        });
        mnuFile.add(mnuOpenNetwork);
        mnuFile.add(jSeparator1);

        mnuItemExit.setText("Exit");
        mnuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemExit);

        jMenuBar1.add(mnuFile);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFromNode, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(159, 159, 159))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtToNode, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(34, 34, 34)
                                                .addComponent(jLabel9)
                                                .addGap(4, 4, 4)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(37, 37, 37)
                                                .addComponent(btnChangeCost, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(36, 36, 36)
                                                .addComponent(jLabel3)))
                                        .addGap(5, 5, 5)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(126, 126, 126))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane2)
                                        .addGap(18, 18, 18))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(btnRunStep, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(btnRun2End, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(403, 403, 403))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblStableState)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRunningTime)
                            .addComponent(txtSimulationResult))
                        .addGap(328, 328, 328))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblStableState)
                            .addComponent(txtSimulationResult))
                        .addGap(5, 5, 5)
                        .addComponent(lblRunningTime)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtToNode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtFromNode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangeCost))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStart, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(btnRun2End, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRunStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuItemExitActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
       
        //initialize
        startSimulation();
        
        btnRun2End.setEnabled(true);
        btnRunStep.setEnabled(true);
        txtFromNode.setEditable(true);
        txtToNode.setEditable(true);
        txtCost.setEditable(true);
        btnChangeCost.setEnabled(true);
        
        btnStart.setEnabled(false);
    }//GEN-LAST:event_btnStartActionPerformed

    /**
     * run one step
     */
    private void runOneStep(){
        
         //request threads
        List<Thread> requestThreads = new ArrayList<>();
        
        //step 1
        for (int i = 0; i < numNodes; i++){
            
            final NodeInfo info = nodeInfoList.get(i);
            
            Thread thread = new Thread(){
                public void run(){
                
                    try {
                        //System.out.println(nodeInfoList.get(i).getAddress() + " : " + nodeInfoList.get(i).getPort());
                        Socket clientSocket = new Socket(info.getAddress(), info.getPort());
                        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());    
                        ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());       

                        //send request
                        Message message = new Message();
                        message.type = Message.REQUEST_NEIGHBOUR_DVS;
                        outToServer.writeObject(message);

                        //read response
                        message = (Message)inFromClient.readObject();

                        clientSocket.close();

                    } catch (Exception ex) {
                        //System.out.println("Error at " + nodeInfoList.get(i).getAddress() + " : " + nodeInfoList.get(i).getPort());
                        Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            thread.start();
            requestThreads.add(thread);
        }
        for (int i = 0; i < numNodes; i++){
            try {
                requestThreads.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        requestThreads.clear();
        
        //step 2
        for (int i = 0; i < numNodes; i++){
            
            final NodeInfo info = nodeInfoList.get(i);
            
            Thread thread = new Thread(){
                public void run(){
                    try {

                        Socket clientSocket = new Socket(info.getAddress(), info.getPort());
                        ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());    
                        ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());  

                        //send request
                        Message message = new Message();
                        message.type = Message.REQUEST_UPDATE_DV;
                        outToServer.writeObject(message);

                        //read response
                        message = (Message)inFromClient.readObject();

                        clientSocket.close();

                    } catch (Exception ex) {
                        Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            thread.start();
            requestThreads.add(thread);
        }
        
        for (int i = 0; i < numNodes; i++){
            try {
                requestThreads.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        requestThreads.clear();
        
        //step 3
        //retrieve DV and routing table
        requestDV_RT();
    }
    
    /**
     * run in3 steps
     * 1 - ask all nodes to request the DVs of neighbors
     * wait for 1 second
     * 2 - ask it to calculate
     * wait for 1 second
     * 3- display DV and routing table
     * @param evt 
     */
    private void btnRunStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunStepActionPerformed
        
        btnRunStep.setEnabled(false);
        btnRun2End.setEnabled(false);
        
        if (isStable){
            JOptionPane.showMessageDialog(this, "The system have been in stable already");
        }else{
            
            List<int[][]> temNodeDVList = nodeDVList;
            
            numSteps++;
       
            runOneStep();

            //show it
            displayDV_RT(); 

            txtSimulationResult.setText("Step: " + numSteps);
            
            //check stable
            if (Utility.equals(temNodeDVList, nodeDVList)){
                isStable = true;
                lblStableState.setText("The system has been in stable state");
            }
        }
        
        btnRun2End.setEnabled(true);
        btnRunStep.setEnabled(true);
    }//GEN-LAST:event_btnRunStepActionPerformed

    private void mnuOpenNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenNetworkActionPerformed
        
        //choose file
        JFileChooser jfc = new JFileChooser();

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            File selectedFile = jfc.getSelectedFile();
            inputFilename = selectedFile.getAbsolutePath();

            btnStart.setEnabled(true);            
            mnuOpenNetwork.setEnabled(false); //allow once only
        }        
    }//GEN-LAST:event_mnuOpenNetworkActionPerformed

    /**
     * run to end (stable state)
     * @param evt 
     */
    private void btnRun2EndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRun2EndActionPerformed
        
        btnRunStep.setEnabled(false);
        btnRun2End.setEnabled(false);
        
        if (isStable){
            JOptionPane.showMessageDialog(this, "The system have been in stable already");
        }
        
        //start time
        long startTime = System.currentTimeMillis();
        
        while (!isStable){
            
            List<int[][]> temNodeDVList = nodeDVList;
            
            numSteps++;
       
            runOneStep();            

            txtSimulationResult.setText("Step: " + numSteps);
            
            //check stable
            if (Utility.equals(temNodeDVList, nodeDVList)){
                isStable = true;
                lblStableState.setText("The system has been in stable state");
            }
        }   
        
        //start time
        long endTime = System.currentTimeMillis();
        
        lblRunningTime.setText("Elapsed time (ms): " + (endTime - startTime));
        
        //show it
        displayDV_RT(); 
        
        btnRun2End.setEnabled(true);
        btnRunStep.setEnabled(true);
    }//GEN-LAST:event_btnRun2EndActionPerformed

    /**
     * change cost
     * @param evt 
     */
    private void btnChangeCostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeCostActionPerformed
        
        btnRunStep.setEnabled(false);
        btnRun2End.setEnabled(false);
        btnChangeCost.setEnabled(false);
        
        try{
            int fromNode = Integer.parseInt(txtFromNode.getText()) - 1;
            int toNode = Integer.parseInt(txtToNode.getText()) - 1;
            int cost = Integer.parseInt(txtCost.getText());
            
            isStable = false;
            lblStableState.setText("");
            
            //set new cost
            network[fromNode][toNode] = cost;
            network[toNode][fromNode] = cost;
            
            //request threads
           List<Thread> requestThreads = new ArrayList<>();

           //step 1
           for (int i = 0; i < numNodes; i++){

               final NodeInfo info = nodeInfoList.get(i);

               Thread thread = new Thread(){
                   public void run(){

                       try {
                           //System.out.println(nodeInfoList.get(i).getAddress() + " : " + nodeInfoList.get(i).getPort());
                           Socket clientSocket = new Socket(info.getAddress(), info.getPort());
                           ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());    
                           ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());       

                           //send request
                           Message message = new Message();
                           message.type = Message.REQUEST_CHANGE_COST;
                           message.costs = network;
                           outToServer.writeObject(message);

                           //read response
                           message = (Message)inFromClient.readObject();

                           clientSocket.close();

                       } catch (Exception ex) {
                           //System.out.println("Error at " + nodeInfoList.get(i).getAddress() + " : " + nodeInfoList.get(i).getPort());
                           Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
               };
               thread.start();
               requestThreads.add(thread);
            }
            for (int i = 0; i < numNodes; i++){
                try {
                    requestThreads.get(i).join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            requestThreads.clear();
            
            //run ONE step

            List<int[][]> temNodeDVList = nodeDVList;
            
            numSteps++;

            runOneStep();            

            txtSimulationResult.setText("Step: " + numSteps);
            
            displayDV_RT();

            //check stable
            if (Utility.equals(temNodeDVList, nodeDVList)){
                isStable = true;
                lblStableState.setText("The system has been in stable state");
            }
          
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
        
        btnRunStep.setEnabled(true);
        btnRun2End.setEnabled(true);
        btnChangeCost.setEnabled(true);

    }//GEN-LAST:event_btnChangeCostActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MasterFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MasterFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MasterFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MasterFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MasterFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeCost;
    private javax.swing.JButton btnRun2End;
    private javax.swing.JButton btnRunStep;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lblRunningTime;
    private javax.swing.JLabel lblStableState;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuItemExit;
    private javax.swing.JMenuItem mnuOpenNetwork;
    private javax.swing.JTextField txtCost;
    private javax.swing.JTextField txtFromNode;
    private javax.swing.JTextArea txtOutputNode1;
    private javax.swing.JTextArea txtOutputNode2;
    private javax.swing.JTextArea txtOutputNode3;
    private javax.swing.JTextArea txtOutputNode4;
    private javax.swing.JTextArea txtOutputNode5;
    private javax.swing.JTextArea txtOutputNode6;
    private javax.swing.JLabel txtSimulationResult;
    private javax.swing.JTextField txtToNode;
    // End of variables declaration//GEN-END:variables
}

//refrence
//https://github.com/adamvh/VectorRouting
