package com.drsn.flights.other;

import java.util.List;

import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.drsn.flights.bean.AirportBean;
import com.drsn.flights.bean.SpringConfig;

/**
 * 机场所在地信息更新模块，数据来源于百度经纬度查询api
 * @author drsnow
 *
 */
public class AirportLocationCrawl {
	SessionFactory factory;
	Configuration configuration;
	ServiceRegistry serviceRegistry;
	Session session;
	Transaction transaction;


	public void init(){
//		configuration = new Configuration().configure();
//		serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//		factory = configuration.buildSessionFactory(serviceRegistry);
		factory = SpringConfig.getContext().getBean(SessionFactory.class);
		session = factory.openSession();
		transaction = session.beginTransaction();
		
	}

	public void destory(){
		transaction.commit();
		session.close();
		factory.close();
	}
	
	public void doWork(){
		List<AirportBean> list = (List<AirportBean>)session.createQuery("FROM AirportBean").list();
		for (int i = 0; i < list.size(); i++) {
			update(list.get(i));
			session.update(list.get(i));
			session.flush();
			session.clear();
			System.out.println(i);
			System.out.println(list.get(i));
		}
	}
    
    public void update(AirportBean bean) {
		try {
			
			Document document = Jsoup.connect("http://api.map.baidu.com/geocoder/v2/?location="+bean.getLatitude()+","+bean.getLongitude()+"&output=json&pois=1&ak=NYTo6rTqbb7B4lD1a9Qan7WiGG4WO1FQ").get();
			System.out.println(document.body().text());
			JSONObject jsonObject = JSONObject.fromObject(document.body().text());
			if (jsonObject.optInt("status") == 0) {
				JSONObject addressComponent = jsonObject.optJSONObject("result").optJSONObject("addressComponent");
				String country = addressComponent.optString("country");
				String province = addressComponent.optString("province");
				String city = addressComponent.optString("city");
				bean.setCountry(country);
				bean.setProvince(province);
				bean.setCity(city);
				System.out.println(country + "\n" + province + "\n" + city);
				
			}
			Thread.sleep(20);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
        AirportLocationCrawl client = new AirportLocationCrawl();
        client.init();
        client.doWork();
        client.destory();
    }
}
