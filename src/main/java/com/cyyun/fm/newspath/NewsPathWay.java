/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyyun.fm.newspath;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author pzj
 */
public class NewsPathWay
{
    
    public static void main(String[] args)
    {
        
 
        NewsPathWay npwff=new NewsPathWay();
        npwff.getNewsPathTreeVersion_2();
    }

    public String npwNoUrl = "";// 不保存url的传播路径图；
    
	// 从相似文数据中产生传播路径图
	public String createNPTFromSimilarArticles(Map<String, List<ArticleInfo>> similarArticles) {
		if (similarArticles == null || similarArticles.isEmpty()) {
			return StringUtils.EMPTY;
		}

		// 1.遍历map中的每个group组；
		// 2.处理每个组的数据，注意要保留原来的处理方法，不要搞乱；
		// 3.原来是每篇文章都做了相似文处理，现在则不需要；找到调整的位置；
		Iterator<Map.Entry<String, List<ArticleInfo>>> it = similarArticles.entrySet().iterator();
		NewsPathVerion2 npv = new NewsPathVerion2();
		while (it.hasNext()) {
			Map.Entry<String, List<ArticleInfo>> entry = it.next();

			List<ArticleInfo> articles = entry.getValue();

			// 先找到代表的标题;
			String curTitle = "";
			for (ArticleInfo article : articles) {
				String tmpTitle = article.getTitle();
				if (tmpTitle != null && tmpTitle.length() > 0) {
					curTitle = tmpTitle.replaceAll("\\s", "").replace("\\", "").replace("\"", "").replace(",", "");
					break;
				}
			}

			// 这个相似文中所有的标题竟然都为空，不处理
			if (curTitle == null || curTitle.length() == 0) {
				continue;
			}
			// System.out.println(curTitle + "\t的转发数据\n");
			for (ArticleInfo article : articles) {
				// System.out.println(article.getWebsiteName() + "\t" +
				// article.getSourceSite());
				processArticle(article, curTitle, npv);
			}

		}

		this.npwNoUrl = createNewsPathWayVersion2(npv);// 产生第一版的图

		return npwNoUrl;
	}
    
	private void processArticle(ArticleInfo article, String similarTitle, NewsPathVerion2 npv) {
		NewsInfo ni = new NewsInfo();
		ni.childs = new HashMap<String, NewsInfo>();
		ni.flag = false;
		ni.parentSite = article.getSourceSite();
		ni.partNews = "";
		ni.siteName = article.getSite();
		ni.sourceSite = article.getSourceSite();
		ni.time = article.getTime();
		ni.title = article.getTitle();
		// 标题处理[如果后续换图形模板可删掉
		//StringBuilder builder = new StringBuilder();

		// 处理掉中间的换行
		if (ni.title != null) {
			ni.title = ni.title.replaceAll("\\s", "").replace("\\", "");
		}

		// if (StringUtils.isNotBlank(ni.title) && ni.title.length() > 15) {
		// builder.append(ni.title.substring(0, 15));
		// ni.title = builder.toString();
		// }

		ni.url = article.getUrl();

		if (ni.sourceSite == null) {
			ni.sourceSite = "";
		}

		// 过滤掉非法数据
		if (ni.siteName == null || ni.time == null || ni.title == null) {
			return;
		}

		ni.sourceSite = ni.sourceSite.replaceAll("\\s", "");

		if (StringUtils.isNotBlank(ni.parentSite))
			ni.parentSite = ni.parentSite.replaceAll("\\s", "").replace("\\", "");;

		ni.siteName = ni.siteName.replaceAll("\\s", "").replace("\\", "");;

		if (ni.sourceSite != null && (ni.sourceSite.contains("\"") || ni.sourceSite.contains("\\"))) {
			ni.sourceSite = ni.sourceSite.replace("\"", "").replace("\\", "");// 替换会影响jason的特殊字符
		}
		if (ni.parentSite != null && (ni.parentSite.contains("\"") || ni.parentSite.contains("\\"))) {
			ni.parentSite = ni.parentSite.replace("\"", "").replace("\\", "");// 替换会影响jason的特殊字符
		}
		if (ni.siteName != null && (ni.siteName.contains("\"") || ni.siteName.contains("\\"))) {
			ni.siteName = ni.siteName.replace("\"", "").replace("\\", "");// 替换会影响jason的特殊字符
		}

		if (ni.time != null && (ni.time.contains("\"") || ni.time.contains("\\"))) {
			ni.time = ni.time.replace("\"", "").replace("\\", "");// 替换会影响jason的特殊字符
		}

		if (ni.title != null && (ni.title.contains("\"") || ni.title.contains("\\"))) {
			ni.title = ni.title.replace("\"", "").replace("\\", "");// 替换会影响jason的特殊字符
		}

		insertNewsInfo2Path2(ni, similarTitle, npv);

	}
    
	private void insertNewsInfo2Path2(NewsInfo ni, String similarTitle, NewsPathVerion2 npv) {
		ArrayList<String> newsTitles = npv.newsTitles;

		boolean success = false;
		if (newsTitles.contains(similarTitle)) {
			HashMap<String, NewsInfo> siteInfos = npv.newsTitlesInfo.get(similarTitle);
			success = addNewsInfo2Res(ni, siteInfos);
		} else {
			// 当不包含时，则需要新建相关数据
			npv.newsTitles.add(similarTitle);
			HashMap<String, NewsInfo> siteInfos = new HashMap<String, NewsInfo>();
			success = addNewsInfo2Res(ni, siteInfos);
			npv.newsTitlesInfo.put(similarTitle, siteInfos);
		}

		// 当添加数据成功时，则有可能要调整当前标题的最早时间
		if (success == true && ni.time != null) {
			// 比较ni.time和npv中该节点最早时间的
			if (npv.titleEarlyTimes.containsKey(similarTitle)) {
				String time1 = npv.titleEarlyTimes.get(similarTitle);
				if (time1 == null || time1.length() == 0) {
					npv.titleEarlyTimes.put(similarTitle, ni.time);
				} else {
					if (ni.time.length() > 0) {
						// 本条新闻的时间比之前的早
						long time1_ts = DateFormat.parse(time1).getTime();
						long cur_time_ts = DateFormat.parse(ni.time).getTime();
						if (cur_time_ts < time1_ts) {
							npv.titleEarlyTimes.put(similarTitle, ni.time);
						}
					}
				}

			} else {
				npv.titleEarlyTimes.put(similarTitle, ni.time);
			}
		}
	}
	
    //从文章数据链表获得传播路径，第一种版本的传播路径(不区分文章)
    //只需要构造出每篇文章的ArticleInfo数据，并将它们放到一个ArrayList中，其他的
    public void getNewsPathTreeVersion_1()
    {
        ArrayList<ArticleInfo> articles=this.initArticlesFromFile();
        if(articles==null || articles.isEmpty())
            return ;
        HashMap<String,NewsInfo> res=getNewsInfos(articles);
        String newsPath=this.outputNewsPathWay(res, articles.get(0).title);
        FileManager.write(newsPath, "newsPathVersion_1.txt", "utf-8", false);
    }
    //从文章数据链表获得传播路径，第二个版本的传播路径(根据事件中的每篇文章的数据，来构造成最终的传播路径)
    public void getNewsPathTreeVersion_2()
    {
        ArrayList<ArticleInfo> articles=this.initArticlesFromFile();
        if(articles==null || articles.isEmpty())
            return ;
        NewsPathVerion2 npv=this.getNewsPathVersion2FromArticles(articles);
        String newsPath=createNewsPathWayVersion2(npv);
        FileManager.write(newsPath, "newsPathVersion_2.txt", "utf-8", false);
    }    

     
    public ArrayList<ArticleInfo> initArticlesFromFile()
    {
        ArrayList<ArticleInfo> articles=new ArrayList<ArticleInfo>();
        String fileF = System.getProperty("user.dir") + "/newsInfoVersion_2.txt";        

        String temp = "", tmp = "";
        try
        {
            FileInputStream fis = new FileInputStream(fileF);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            FileManager.write("", "result.txt", "utf-8", false);
        //文件数据一行为一个数据，每个字段以tab隔开，顺序为：     
        //title time    url siteName sourceSite

            while (true)
            {
                temp = br.readLine();
                if (temp == null)
                {
                    break;
                }
                temp = temp.trim();
                if (temp.length() == 0)
                {
                    continue;
                }
                String[] list=temp.split("\t");
                if(list.length<5)
                    continue;
                

                ArticleInfo ai = new ArticleInfo();
                ai.time = formatSeparate(list[1]);
                ai.title = list[0];
                ai.url=list[2];
                ai.site = list[3];
                ai.sourceSite = list[4];

                if (ai.site != null && ai.site.contains(" "))
                {
                    ai.site = ai.site.substring(0, ai.site.indexOf(" "));
                }
                articles.add(ai);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } 
        return articles;
    }
    public HashMap<String,NewsInfo> getNewsInfos(ArrayList<ArticleInfo> articles)
    {
        HashMap<String,NewsInfo> res=new HashMap<String,NewsInfo>();
        for(ArticleInfo article:articles)
        {
            NewsInfo ni = new NewsInfo();

            ni.time = formatSeparate(article.time);
            ni.title = article.title;
            ni.siteName=article.site;
            ni.sourceSite = article.sourceSite;
            ni.parentSite = article.sourceSite;

            if (ni.siteName != null && ni.siteName.contains(" "))
            {
                ni.siteName = ni.siteName.substring(0, ni.siteName.indexOf(" "));
            }
            if (ni.siteName != null && ni.siteName.length() > 0)
            {
                try
                {
                    //如果要更新站点的发文数据，则需要时间满足条件，并且是当前最小的时间
                    if(!res.containsKey(ni.siteName))
                    {
                        res.put(ni.siteName, ni);
                    }
                    else
                    {
                        //已经包含则需要满足一定的条件
                        String siteTime=res.get(ni.siteName).time;
                        if(siteTime==null || siteTime.length()<2)
                        {
                            System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                            res.put(ni.siteName, ni);
                        }
                        else
                        {
                            long siteTimeTs=DateFormat.parse(siteTime).getTime();
                            long curTimeTs=DateFormat.parse(ni.time).getTime();
                            if( curTimeTs<=siteTimeTs)
                            {
                                System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                                res.put(ni.siteName, ni);
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
    public NewsPathVerion2 getNewsPathVersion2FromArticles(ArrayList<ArticleInfo> articles)
    {
        NewsPathVerion2 npv=new NewsPathVerion2();
        for(ArticleInfo article:articles)
        {
            NewsInfo ni=new NewsInfo();
            ni.childs=new HashMap<String,NewsInfo>();
            ni.flag=false;
            ni.parentSite=article.sourceSite;
            ni.partNews="";
            ni.siteName=article.site;
            ni.sourceSite=article.sourceSite;
            ni.time=article.time;
            ni.title=article.title;
            ni.url=article.url;
            
            if(ni.siteName==null)
            {
               ni.siteName="";
            }
            
            if(ni.title==null || ni.time ==null )
                continue;
            
            insertNewsInfo2Path2(ni,npv);
        }
        return npv;
    }    

    public String createNewsPathWayVersion2(NewsPathVerion2 npv)
    {
        ArrayList<String> newsTitles=npv.newsTitles;
        HashMap<String,String> everyPath=new HashMap<String,String>();
        
        //对于每一个标题，都产生出传播路径
        for(String newsTitle:newsTitles)
        {
            String newsPath=this.outputNewsPathWay(npv.newsTitlesInfo.get(newsTitle), newsTitle);
            if(newsPath==null || newsPath.trim().length()==0)
                continue;
            
            int index1=newsPath.indexOf("【");
            int index2=newsPath.indexOf("】",index1);
            if(index1+("2016-02-03 12:20:59").length()+2>=index2)
            {
                npv.titleEarlyTimes.put(newsTitle, newsPath.substring(index1+1, index2));
            }
            everyPath.put(newsTitle, newsPath.replace("根节点", newsTitle.replace("\r", "").replace("\n", "").replace("\"", "")));
        }
        
        
        String pathStr = "{\"name\":\"根节点\",\"value\":\"\",\"children\":[";
        Iterator<Map.Entry<String, String>> it1 = sortResultByTimeStr(npv.titleEarlyTimes).iterator();
        int seq=0;
        while (it1.hasNext())
        {
            Map.Entry<String, String> entry = it1.next();
            System.out.println(entry.getKey() + "\t" + entry.getValue());
            
            //不包含这个节点的传播路径数据
            if(!everyPath.containsKey(entry.getKey()))
                continue;
            
            String tmpStr=everyPath.get(entry.getKey());
            if(tmpStr==null || tmpStr.length()==0)
                continue;
            
            if(seq!=0)
                pathStr+=",";
            pathStr+=tmpStr;
            seq++;
        }
        pathStr+="]}";
        return pathStr.replace("\r", "").replace("\n", "");
    } 
    public ArrayList<Map.Entry<String, String>> sortResultByTimeStr(Map<String, String> res)
    {
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(res.entrySet());

        Collections.sort(list, new Comparator<Object>()
        {
            public int compare(Object e1, Object e2)
            {
                String time1=((Map.Entry<String, String>)e1).getValue();
                String time2=((Map.Entry<String, String>)e2).getValue();
                if (time1 == null)
                {
                    return -1;
                }
                
                if(time2 == null)
                    return 1;
                try
                {
                    Long v1 = DateFormat.parse(time1).getTime();
                    Long v2 = DateFormat.parse(time2).getTime();
                    if (v1 - v2 > 0)
                    {
                        return 1;
                    } else
                    {
                        if (v1 - v2 < 0)
                        {
                            return -1;
                        }
                    }
                }
                catch(Exception e)
                {
                    return 0;
                }

                return 0;
            }
        });
        return list;
    }      
    private void insertNewsInfo2Path2(NewsInfo ni,NewsPathVerion2 npv)
    {           
        ArrayList<String> newsTitles=npv.newsTitles;
        boolean existSimilar=false;
        String similarTitle=null;
        for(String newsTitle:newsTitles)
        {
            double score=ChineseCosSimilary.chineseSimilary(newsTitle,ni.title);
            if(score>0.8)
            {
                existSimilar=true;
                similarTitle=newsTitle;
                break;
            }
        }
        
        boolean success=false;
        if(existSimilar==true)
        {
            HashMap<String,NewsInfo> siteInfos=npv.newsTitlesInfo.get(similarTitle);
            success=addNewsInfo2Res( ni, siteInfos); 
        }
        else
        {
            //当不包含时，则需要新建相关数据
            npv.newsTitles.add(ni.title);
            HashMap<String,NewsInfo> siteInfos=new HashMap<String,NewsInfo>();  
            success=addNewsInfo2Res( ni, siteInfos);        
            npv.newsTitlesInfo.put(ni.title, siteInfos);
        }
        
        //当添加数据成功时，则有可能要调整当前标题的最早时间
        if(success==true && ni.time!=null)
        {
            //比较ni.time和npv中该节点最早时间的
            if(npv.titleEarlyTimes.containsKey(similarTitle))
            {
                String time1=npv.titleEarlyTimes.get(similarTitle);
                if(time1==null || time1.length()==0)
                {
                    npv.titleEarlyTimes.put(similarTitle, ni.time);
                }
                else if(ni.time.length()>0)
                {
                    //本条新闻的时间比之前的早
                    long time1_ts=DateFormat.parse(time1).getTime();
                    long cur_time_ts=DateFormat.parse(ni.time).getTime();
                    if(cur_time_ts<time1_ts)
                    {
                        npv.titleEarlyTimes.put(similarTitle, ni.time);
                    }
                }
                
            }
            else
            {
                npv.titleEarlyTimes.put(ni.title, ni.time);
            }
        }
    }   
    private boolean addNewsInfo2Res(NewsInfo ni,HashMap<String, NewsInfo> res)
    {
        //首先要确定当前的这条新闻的时间是符合要求的：为空，或者在时间范围内
        //如果要更新站点的发文数据，则需要时间满足条件，并且是当前最小的时间
        //当前的这个时间也要符合条件
        if(ni.time==null ||ni.time.length()==0)
        {
            if(!res.containsKey(ni.siteName))
            {
                res.put(ni.siteName, ni);
                return true;
            }
        }
        else
        {
            try
            {

                long curTimeTs=DateFormat.parse(ni.time).getTime();

                if(!res.containsKey(ni.siteName))
                {
                    res.put(ni.siteName, ni);

                }
                else
                {
                    //已经包含则需要满足一定的条件
                    String siteTime=res.get(ni.siteName).time;
                    if(siteTime==null || siteTime.length()<2)
                    {
                        System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                        res.put(ni.siteName, ni);
                    }
                    else
                    {
                        long siteTimeTs=DateFormat.parse(siteTime).getTime();

                        if(curTimeTs<=siteTimeTs)
                        {
                            System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                            res.put(ni.siteName, ni);
                        }
                    }
                }
                return true;

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }    


     public void outPutNewsPathWayFromExcel(String newsTitle)
    {

        /*
         初始化如下字段：
         public String title;
         public String time;
         public String siteName;
         public String sourceSite;//该新闻是从哪儿转载过来的，与parentSite为同一个字段
         public String parentSite;//该新闻是从哪儿转载过来的            
         */
        
        
        //这里主要是要构造出这个网站数据
        HashMap<String, NewsInfo> res=new HashMap<String, NewsInfo>();
        


        String fileF = System.getProperty("user.dir") + "/newsInfo.txt";        

        String temp = "", tmp = "";
        try
        {
            FileInputStream fis = new FileInputStream(fileF);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            FileManager.write("", "result.txt", "utf-8", false);
        //文件数据一行为一个数据，每个字段以tab隔开，顺序为：
        //title siteName    sourceSite time     partNews            
            int all = 0;
            while (true)
            {
                temp = br.readLine();
                if (temp == null)
                {
                    break;
                }
                temp = temp.trim();
                if (temp.length() == 0)
                {
                    continue;
                }
                String[] list=temp.split("\t");
                if(list.length<4)
                    continue;
                

                NewsInfo ni = new NewsInfo();

                ni.time = formatSeparate(list[3]);
                ni.title = list[0];
                ni.siteName=list[1];
                ni.sourceSite = list[2];
                ni.parentSite = list[2];

                if (ni.siteName != null && ni.siteName.contains(" "))
                {
                    ni.siteName = ni.siteName.substring(0, ni.siteName.indexOf(" "));
                }
                if (ni.siteName != null && ni.siteName.length() > 0)
                {
                    try
                    {
                        //如果要更新站点的发文数据，则需要时间满足条件，并且是当前最小的时间
                        if(!res.containsKey(ni.siteName))
                        {
                            res.put(ni.siteName, ni);
                        }
                        else
                        {
                            //已经包含则需要满足一定的条件
                            String siteTime=res.get(ni.siteName).time;
                            if(siteTime==null || siteTime.length()<2)
                            {
                                System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                                res.put(ni.siteName, ni);
                            }
                            else
                            {
                                long siteTimeTs=DateFormat.parse(siteTime).getTime();
                                long curTimeTs=DateFormat.parse(ni.time).getTime();
                                if( curTimeTs<=siteTimeTs)
                                {
                                    System.out.println("res中的时间:"+siteTime+",\t本条新闻的时间："+ni.time);
                                    res.put(ni.siteName, ni);
                                }
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            outputNewsPathWay(res,newsTitle);

        } catch (Exception e)
        {

            e.printStackTrace();
        }
    }   
    
    private static String formatSeparate(String date)
    {
        if(date==null)
            return null;
        date = date.replaceAll("年|月|/|\\.", "-");
        date = date.replaceAll("日 {0,4}", " ");
        date = date.replaceAll("分$", "");
        date = date.replaceAll("时$", "");
        date = date.replaceAll("时|分", ":");
        return date.replace("秒", "").trim();
    }


    //输出所整理出的结果
    private String outputNewsPathWay(HashMap<String, NewsInfo> res, String word)
    {
    	String url="";
        if (res == null || res.isEmpty())
        {
            return "" ;
        }
        Iterator<Map.Entry<String, NewsInfo>> it = res.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, NewsInfo> entry = it.next();
            NewsInfo ni = entry.getValue();
            System.out.println(ni.siteName + "\t" + ni.parentSite + "\t" + ni.time);
            String parent = ni.parentSite;  
            String curSite = ni.siteName;
            if (ni.parentSite==null) {//没有父站点的节点，点击节点跳转url取自身的url
            	url = ni.url;
            }
            if(parent!=null && curSite!=null && parent.trim().equals(curSite.trim()))
            {
                ni.parentSite="";
                ni.sourceSite="";
            }                      
        }

        HashMap<String,NewsInfo> addRes=new HashMap<String,NewsInfo>();
        NewsInfo treeRoot = new NewsInfo();
        treeRoot.title = word;
        treeRoot.flag=true;
        treeRoot.siteName= "根节点";
        treeRoot.childs =new  HashMap<String,NewsInfo>();
        
        boolean hasMultiChilds=false;

        ArrayList<Map.Entry<String, NewsInfo>> resList= sortResult(res);
        it = resList.iterator();
        while (it.hasNext())
        {
            Map.Entry<String, NewsInfo> entry = it.next();
            NewsInfo ni = entry.getValue();

            //处理每个节点，把每个节点加到父节点的childs中；
            //如果不包含父节点则在百度去搜索该网站的数据（暂时不处理，直接作为根的孩子节点）
            //如果没有父节点，则直接作为根的孩子节点
            String parent = ni.parentSite;
            ni.flag=true;
            String curSite = ni.siteName;
            if(parent!=null && curSite!=null && parent.trim().equals(curSite.trim()))
                parent="";
            while (true)
            {
                if (!res.containsKey(parent) && !addRes.containsKey(parent))
                {
                    if(parent!=null && parent.length()>0)
                    {
                        NewsInfo node = new NewsInfo();
                        node.title = word;
                        node.time=getNewsInfoFromMaps(curSite,res,addRes).time;
                        node.flag = true;
                        node.siteName = parent;
                        node.url = entry.getValue().url;
                        node.childs = new HashMap<String, NewsInfo>();        
                        
                        node.childs.put(curSite, getNewsInfoFromMaps(curSite,res,addRes));
                        if (node.sourceSite==null) {//没有父站点的节点，点击节点跳转url取自身的url
                        	url = node.url;
                        }
                        treeRoot.childs.put(parent, node);
                        res.get(curSite).flag=true;
                        addRes.put(parent, node);
                    }
                    else
                    {
                        treeRoot.childs.put(curSite, getNewsInfoFromMaps(curSite,res,addRes));
                        res.get(curSite).flag=true;                        
                    }
                    break;
                }

                NewsInfo pNi =  getNewsInfoFromMaps(parent,res,addRes);
                if (pNi.childs == null)
                {
                    pNi.childs =new  HashMap<String,NewsInfo>();
                }
                

                pNi.childs.put(curSite, getNewsInfoFromMaps(curSite,res,addRes));
                if(pNi.flag==true)
                    break;                     
                pNi.flag=true;
           
                curSite = parent;
                parent = pNi.parentSite;
                
                
                //标记是否有多个孩子
                if(pNi.childs.size()>1)
                {
                    hasMultiChilds=true;
                }

            }
        }
        
        res.putAll(addRes);
        
        resList= sortResult(treeRoot.childs);
        if(!resList.isEmpty())
        {
            treeRoot.time=resList.get(0).getValue().time;
            
            //有某个节点有多个孩子，则可以换行
            if(treeRoot.childs.size()>1 || hasMultiChilds)
            {
                treeRoot.siteName+="\\r\\n";
            }
            treeRoot.siteName+=resList.get(0).getValue().siteName;
        }
        if (treeRoot.getUrl()==null) {
        	treeRoot.setUrl(url);
		}
        String treePath=preOrderTree(treeRoot, 0, res);
        
        System.out.println(treePath);
        return treePath;
    } 

    public ArrayList<Map.Entry<String, NewsInfo>> sortResult(Map<String, NewsInfo> res)
    {
        ArrayList<Map.Entry<String, NewsInfo>> list = new ArrayList<Map.Entry<String, NewsInfo>>(res.entrySet());

        Collections.sort(list, new Comparator<Object>()
        {
            public int compare(Object e1, Object e2)
            {
                Long v1 = DateFormat.parse(((Map.Entry<String, NewsInfo>) e1).getValue().time).getTime();
                Long v2 = DateFormat.parse(((Map.Entry<String, NewsInfo>) e2).getValue().time).getTime();
                if (v1 - v2 > 0)
                {
                    return 1;
                } else
                {
                    if (v1 - v2 < 0)
                    {
                        return -1;
                    }
                }

                return 0;
            }
        });
        return list;
    }

    private NewsInfo getNewsInfoFromMaps(String site, HashMap<String, NewsInfo> res1, HashMap<String, NewsInfo> res2)
    {
        if (res1.containsKey(site))
        {
            return res1.get(site);
        }
        if (res2.containsKey(site))
        {
            return res2.get(site);
        }
        return null;
    }

    //将这个棵树表示出来：先根遍历
    private String preOrderTree(NewsInfo ni, int level, HashMap<String, NewsInfo> res)
    {
        if (ni == null)
        {
            return "";
        }

        if (ni.childs == null || ni.childs.isEmpty() || level > 3)
        {
            return "{\"name\":\"" + ni.siteName + "【" + ni.time + "】\",\"value\":\"" + ni.url + "\"}";
        } else
        {
            String str = "{\"name\":\"" + ni.siteName + "【" + ni.time + "】\",\"value\":\"" + ni.url + "\",\"children\":[";

            Iterator<Map.Entry<String, NewsInfo>> it = sortResult(ni.childs).iterator();

            int seq = 0;
            while (it.hasNext())
            {
                String site = it.next().getKey();

                seq++;
                if (seq > 1)
                {
                    str += ",";
                }
                str += preOrderTree(res.get(site), level + 1, res);
            }

            str += "]}";
            return str;
        }

    }

    public class NewsInfo
    {

        public String title;
        public String time;
        public String siteName;
        public String sourceSite;//该新闻是从哪儿转载过来的，与parentSite为同一个字段
        public String parentSite;//该新闻是从哪儿转载过来的
        public String url;//该新闻的url
        public String partNews = "";//保存正文开始的15个字符
        public HashMap<String, NewsInfo> childs;
        public boolean flag = false;//标记该节点是否处理
        
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUrl() {
			return url;
		}
		
        
    }
    public static class ArticleInfo
    {
        public String title=null;
        public String url=null;
        public String time=null;
        public String site=null;
        public String sourceSite=null;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getSite() {
			return site;
		}
		public void setSite(String site) {
			this.site = site;
		}
		public String getSourceSite() {
			return sourceSite;
		}
		public void setSourceSite(String sourceSite) {
			this.sourceSite = sourceSite;
		}
        
    }   
    public class NewsPathVerion2
    {
        public ArrayList<String> newsTitles;//保存好已有的新闻标题
        public HashMap<String,HashMap<String,NewsInfo>> newsTitlesInfo;//记录好新闻标题所对应的所有站点信息
        public HashMap<String,String> titleEarlyTimes;
        public NewsPathVerion2()
        {
            newsTitles = new ArrayList<String>();
            newsTitlesInfo = new HashMap<String, HashMap<String, NewsInfo>>();
            titleEarlyTimes = new HashMap<String, String>();
        }
    }    
}
