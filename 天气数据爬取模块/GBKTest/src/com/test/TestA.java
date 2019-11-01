package com.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class TestA extends DaoBases{
	String rootUrl = "http://www.tianqihoubao.com";
	static List<String[]> errorList = new ArrayList<>();

	public void crawlPinyin(List<String[]> list) {
		try {
			String url = rootUrl + "/lishi/";
			Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
			Elements elements = document.selectFirst(".citychk").select("dd a");
			for (Element element : elements) {
				String city = element.text();
				String pinyin = element.attr("href").replace("/lishi/", "").replace(".html", "");
				String[] l = {city, pinyin};
				list.add(l);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void crawlTianqi(String[] l,int date) {
		boolean x = true;
		while(x){
			x = false;
			String url = rootUrl + "/lishi/" + l[1] + "/month/" + date + ".html";
			try {
				Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
				Element element = document.selectFirst("table");
				Elements tr = element.select("tr");
				tr.remove(0);
				List<TianqiBean> list = new ArrayList<TianqiBean>();
				
				Transaction transaction = session.beginTransaction();
				
				for (Element elementTr : tr) {
					TianqiBean bean = new TianqiBean();
					String dateString = elementTr.child(0).text();
					String[] tianqiString = elementTr.child(1).text().replaceAll(" ", "").split("/");
					String[] wenduString = elementTr.child(2).text().replaceAll(" ", "").split("/");
					String[] fengxiangString = elementTr.child(3).text().replaceAll(" ", "").split("/");
					bean.setCity(l[0]);
					bean.setDate(getTime(dateString));
					bean.setTianqiDay(tianqiString[0]);
					bean.setTianqiNight(tianqiString[1]);
					bean.setWenduDay(wenduString[0]);
					bean.setWenduNight(wenduString[1]);
					bean.setFengxiangDay(fengxiangString[0]);
					bean.setFengxiangNight(fengxiangString[1]);
					System.out.println(bean);
					session.save(bean);
				}
				
				try {
					transaction.commit();
				} catch (Exception e) {
					transaction.rollback();
					errorList.add(l);
				}
				
			}catch(NullPointerException e){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {}
				x = true;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				x = true;
			}
		}
	}
	
	public int getTime(String day) {
		int time = 0;
		try {
			long date = new SimpleDateFormat("yyyy年MM月dd日").parse(day).getTime() / 1000;
			time = (int) date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
	
	@Test
	public void doing() {
		init();
		session = factory.openSession();

		List<String[]> pingyinList = new ArrayList<>();
		crawlPinyin(pingyinList);
		double i = 1;
		double l = pingyinList.size();
		for (String[] strings : pingyinList) {
			crawlTianqi(strings, 201802);
			crawlTianqi(strings, 201803);
			crawlTianqi(strings, 201804);
			System.out.println("进度：" + (int)((i/l)*100) + "%");
			i++;
		}
		
		destory();
		
	}
	
	private static Session session = null;
	
	
	static TestA tA = new TestA();
	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
		     public void run() {
		    	 for (String[] strings : errorList) {
		    		 System.err.println("errorList:");
		    		 System.err.println(strings[0] + "\t" + strings[1]);
		    	 }
		     }
		});
		
		tA.doing();
	}
	
}
