/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyyun.fm.newspath;
import java.util.ArrayList;  
import java.util.List;  
  
public class CosSimilary {  
  
    List<Integer> vector1 = new ArrayList<Integer>();  
    List<Integer> vector2 = new ArrayList<Integer>();  
  
    public CosSimilary(String string1, String string2) {  
        //把输入字符串中多个空格变为一个  
        String[] vector1String = string1.trim().replaceAll("\\s+", " ").split(" ");  
        String[] vector2String = string2.trim().replaceAll("\\s+", " ").split(" ");  
  
        for (String string : vector1String) {  
            vector1.add(Integer.parseInt(string));  
        }  
        for (String string : vector2String) {  
            vector2.add(Integer.parseInt(string));  
        }  
    }  
  
    // 求余弦相似度  
    public static double sim(List<Integer> list1,List<Integer> list2) {  
        double result = 0;  
        result = pointMulti(list1, list2) / sqrtMulti(list1, list2);  
  
        return result;  
    }
        // 求余弦相似度  
    public double sim() {  
        double result = 0;  
        result = pointMulti(vector1, vector2) / sqrtMulti(vector1, vector2);  
  
        return result;  
    }  
  
    private static double sqrtMulti(List<Integer> vector1, List<Integer> vector2) {  
        double result = 0;  
        result = squares(vector1) * squares(vector2);  
        result = Math.sqrt(result);  
        return result;  
    }  
  
    // 求平方和  
    private static double squares(List<Integer> vector) {  
        double result = 0;  
        for (Integer integer : vector) {  
            result += integer * integer;  
        }  
        return result;  
    }  
  
    // 点乘法  
    private static  double pointMulti(List<Integer> vector1, List<Integer> vector2) {  
        double result = 0;  
        for (int i = 0; i < vector1.size(); i++) {  
            result += vector1.get(i) * vector2.get(i);  
        }  
        return result;  
    }  
      
    public static void main(String[] args) {  
  
        String string = "0 0 1 1 1 1 1 1  0 0 0 0 0 0 0 0 1 1 1 0 0 0";  
        String string2 = "0 0 1 1 1 1 1 1 0 0 0 0 0 0 0 0 1 1 0 0 0 0";  
        CosSimilary computerDecition = new CosSimilary(string,  
                string2);  
        System.out.println(computerDecition.sim());  
    }  
}