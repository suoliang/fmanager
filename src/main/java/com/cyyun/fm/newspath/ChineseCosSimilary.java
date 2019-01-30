/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cyyun.fm.newspath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pzj
 */
//计算两个中文文本的余弦相似度:不采用分词，直接使用汉字
public class ChineseCosSimilary
{
    public static double chineseSimilary(String s1,String s2)
    {
        //首先获得所有的字
        if(s1==null || s2==null )
            return 0.0;
        HashMap<Character,Integer> hm1=new HashMap<Character,Integer>();
        HashMap<Character,Integer> hm2=new HashMap<Character,Integer>();
        
        //统计s1的数据
        for(int i=0;i<s1.length();i++)
        {
            Character ch=s1.charAt(i);
            
            if(!isChinese(ch) || (ch>='0' && ch<='9') || (ch>='a' && ch<='z') || (ch>='A' && ch<='Z'))
                continue;
//            System.out.print(ch+" ");
            if(hm1.containsKey(ch))
            {
                hm1.put(ch, hm1.get(ch)+1);
            }
            else
                hm1.put(ch, 1);
        }
//        System.out.println();
        
        //统计s2的数据
        for(int i=0;i<s2.length();i++)
        {
            Character ch=s2.charAt(i);
            if(!isChinese(ch) || (ch>='0' && ch<='9') || (ch>='a' && ch<='z') || (ch>='A' && ch<='Z'))
                continue;    
//            System.out.print(ch+" ");
            if(hm2.containsKey(ch))
            {
                hm2.put(ch, hm2.get(ch)+1);
            }
            else
                hm2.put(ch, 1);
        }        
//        System.out.println();
        //遍历hm1和hm2，构造出向量
        ArrayList<Integer> list1=new ArrayList<Integer>();
        ArrayList<Integer> list2=new ArrayList<Integer>();
        Iterator<Map.Entry<Character, Integer>> it = hm1.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Character, Integer> entry=it.next();
            Character ch=entry.getKey();
            list1.add(1);
            if(hm2.containsKey(ch))
                list2.add(1);
            else
                list2.add(0);
            
        }
        
        it = hm2.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Character, Integer> entry=it.next();
            Character ch=entry.getKey();
            if(!hm1.containsKey(ch))
            {
                list2.add(1);
                list1.add(0);
            }            
        } 
//        System.out.println(list1);
//        System.out.println(list2);
        double grade=CosSimilary.sim(list1,list2);
//        System.out.println("相似度评分："+grade);
        return grade;
    }
    private static  boolean isChinese(Character ch)
    {
        Character.UnicodeBlock ub1 = Character.UnicodeBlock.of(ch);
        if (ub1 == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub1 == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub1 == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
        {
            return true;
        }
        return false;
    }    
    public static void main(String[] args)
    {
        String t1="阜阳推广互联网+养老金待遇认证 足不出户领取资格";
        String t2="阜阳市推广互联网+养老金待遇领取自助认证服务";
        System.out.println(chineseSimilary(t1,t2));
    }
}
