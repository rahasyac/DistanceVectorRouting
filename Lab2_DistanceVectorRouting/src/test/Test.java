package test;

import common.Message;
import common.Utility;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import master.MasterFrame;

/**
 *
 */
public class Test {
    
    public static void main(String[] args){
        
        for (int i = 0; i < 20; i++){
            
            try {

                Socket clientSocket = new Socket("127.0.0.1", 1235);
                ObjectOutputStream toServer = new ObjectOutputStream(clientSocket.getOutputStream());    
                ObjectInputStream fromClient = new ObjectInputStream(clientSocket.getInputStream()); 

                //send request
                Message request = new Message();
                request.type = Message.REQUEST_UPDATE_DV;
                toServer.writeObject(request);

                //read message
//                Message message = (Message)fromClient.readObject();

                clientSocket.close();

//                System.out.println(Utility.DV2String(5, message.DV));

            } catch (Exception ex) {
                Logger.getLogger(MasterFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
