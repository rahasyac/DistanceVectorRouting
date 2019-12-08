package common;

import java.util.List;

/**
 * Utility class
 */
public class Utility {
    
    /**
     * get the DV as string format
     * @param numNodes
     * @param DV
     * @return DV String
     */
    public static String DV2String(int numNodes, int[][] DV){
        //display on text area
        String result = "    ";

        //header
        for (int toNode = 0; toNode < numNodes; toNode++){
            result += String.format("%-4d",toNode + 1);
        }
        result += "\n";

        //from nodes
        for (int fromNode = 0; fromNode < numNodes; fromNode++){

            result += String.format("%-4d", fromNode + 1);

            //to nodes
            for (int toNode = 0; toNode < numNodes; toNode++){
                if (DV[fromNode][toNode] >= Configuration.INFINITY){
                   result += String.format("%-4s", "Inf");
                }else{
                    result += String.format("%-4d", DV[fromNode][toNode]);
                }
            }

            result += "\n";
        }
        
        return result;
    }
    
    /**
     * create matrix with infinity value
     * @return matrix
     */
    public static int[][] createMatrix(){
        
        int[][] matrix = new int[Configuration.MAX_NODES][Configuration.MAX_NODES];
        
        for (int i = 0; i < Configuration.MAX_NODES; i++){
            for (int j = 0; j < Configuration.MAX_NODES; j++){
                matrix[i][j] = Configuration.INFINITY;
            }
        }
        return matrix;
    }
    
    /**
     * check if 2 matrices such as DV equal
     * @param matrix1
     * @param matrix2
     * @return true/false 
     */
    public static boolean equals(int[][] matrix1, int[][] matrix2){
        for (int i = 0; i < Configuration.MAX_NODES; i++){
            for (int j = 0; j < Configuration.MAX_NODES; j++){
                if (matrix1[i][j] != matrix2[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * check if 2 list of matrices such as DV equal
     * @param matrixList1
     * @param matrixList2
     * @return true/false
     */
    public static boolean equals(List<int[][]> matrixList1, List<int[][]> matrixList2){
        for (int i = 0; i < matrixList1.size(); i++){
            
            if (!Utility.equals(matrixList1.get(i), matrixList2.get(i))){
                return false;
            }
            
        }
        return true;
    }
}
