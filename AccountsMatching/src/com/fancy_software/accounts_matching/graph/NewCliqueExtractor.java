package com.fancy_software.accounts_matching.graph;

import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.*;


public class NewCliqueExtractor {

    /*public static int[][] toMatrix(List<AccountVector> graph){

    }*/

    /*
    * Description of the algorithm and theory you can see here
    * http://www.datalaundering.com/download/cliques.pdf
    *
    * Names of some methods don't look obvious that is why it will be a good idea
    * to check the link above ;)
    * */

    public static List<Integer> P1_2(int[][] matrix) {
        int i = 0;
        List<Integer> CLIQUE = new LinkedList<>();
        int[] F = new int[matrix.length];
        for(int j = 0; j < matrix.length; j++) F[j] = getWeight(matrix,j);
        return P3(matrix, CLIQUE, F, i);
    }

    public static List<Integer> P3(int[][] matrix, List<Integer> CLIQUE, int[] F1, int j) {
        int[] F =F1;
        for(int i = 0; i < F.length; i++) {
            if (F[i] == 0) {
                List<Integer> result = new LinkedList<>(CLIQUE);
                result.add(F[i]);
                return result;
            }
            else return P3A(matrix, CLIQUE, F, j);
        }
        return CLIQUE; //I thought that wasn't necessary but IDEA thinks different
    }

    public static List<Integer> P3A(int[][] matrix, List<Integer> CLIQUE, int[] F1, int j) {
        Boolean flag = true;
        int[] F = F1;
        int amNZ = getNZ(F);    //amount of non-zero elements
        for(int i = 0; i < F.length; i++) {
            if(F[i] != 0 && F[i] != (amNZ - 1)) {
                flag = false;
            }
        }
        if(flag) {
            List<Integer> result = new LinkedList<>(CLIQUE);
            for(Integer i : F) {
                if (i != 0) {
                    result.add(i);
                    i = 0;
                }
            }
            return result;
        }
        else return P4(matrix, CLIQUE, F, j);
    }

    public static List<Integer> P4(int[][] matrix, List<Integer> CLIQUE, int[] F, int j) {
        if(getNZ(F) == 0) return P4A(matrix, CLIQUE, F, j); else {System.out.println("Error in NewCliqueExtractor::P3A"); return null;}
    }

    public static  List<Integer> P4A(int[][] matrix, List<Integer> CLIQUE, int[] F, int j) {
        int i = --j;
        if(i == -1) {
            System.out.println("Algorithm finished at step P4A");
            return null;
        }
        else return P5(matrix, CLIQUE, F, j);
    }

    public static List<Integer> P5(int[][] matrix, List<Integer> CLIQUE, int[] F, int j) {
        List<Integer> CLIQUE1 = CLIQUE;
        int maxIndex = 0;
        int maxVal = 0;
        for(int i = 0; i < F.length; i++) {
            if(F[i] > maxVal) maxIndex = i;
        }
        CLIQUE1.add(maxIndex);
        return P6_7_8(matrix, CLIQUE1, F, j, maxIndex);
    }

    public static List<Integer> P6_7_8(int[][] matrix, List<Integer> CLIQUE, int[] F, int j, int index) {
        int[][] matrix1 = new int[matrix.length - 1][matrix.length - 1];

        for(int i = 0; i < matrix1.length; i++) {   //removing added to clique node from graph
            for(int k = 0; k < matrix1.length; k++) {
                if(i < index) {
                    if(k < index) matrix1[i][k] = matrix[i][k]; else matrix1[i][k] = matrix[i][k + 1];
                }
                else {
                    if (k < index) matrix1[i][k] = matrix[i + 1][k]; else matrix1[i][k] = matrix[i + 1][k + 1];
                }
            }
        }

        int[] F1 = new int[matrix1.length];

        for(int i = 0; i < matrix1.length; i++) {   //getting new weights
            F1[i] = getWeight(matrix1, i);
        }

        int i = j + 1;  //new generation of the graph; end of P6_P7 next part is P8

        /*
        * problems with understanding P9
        * */

         return P9();
    }

    //weight function for every node
    public static int getWeight(int[][] matrix, int index) {
        int result = 0;
        for(int i = 0; i < matrix.length; i++)
            if(matrix[index][i] == 1) {
                result++;
            }
        return result;
    }

    //returns amount of non-zero elements
    public static int getNZ(int[] arr) {
        int result = 0;
        for(int i = 0; i < arr.length; i++) if(arr[i] != 0) result++;
        return result;
    }

}
