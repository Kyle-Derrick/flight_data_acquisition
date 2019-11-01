package com.drsn.flights.other;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.drsn.flights.bean.AirportBean;
import com.drsn.flights.bean.SpringConfig;

/**
 * 机场名信息更新模块，数据来源于安能全球机场代码网站http://airport.anseo.cn/
 * @author drsnow
 *
 */
public class AirportNameCrawl {
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
		try {
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}
		session.close();
		factory.close();
	}
	
	public void doWork(){
		List<AirportBean> list = (List<AirportBean>)session.createQuery("FROM AirportBean").list();
		for (int i = 0; i < list.size(); i++) {
			update(list.get(i));
			session.update(list.get(i));
			if (i%10 == 0) {
				
				try {
					transaction.commit();
					transaction = session.beginTransaction();
				} catch (Exception e) {
					transaction.rollback();
				}
			}
			System.out.println(i);
			System.out.println(list.get(i));
		}
	}
    
    public void update(AirportBean bean) {
		try {

			Document document = Jsoup.connect("http://airport.anseo.cn/search/?q="+bean.getIcao())
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
//					.timeout(15000)
					.get();
			Element element = document.selectFirst("div[class='aw-mod aw-question-detail aw-item']");
			if (element != null) {
				String name = element.selectFirst("#d-h1").text();
				System.out.println(name);
				bean.setName(name);
			}
			Thread.sleep(20);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
        AirportNameCrawl client = new AirportNameCrawl();
        client.init();
        client.doWork();
        client.destory();
    }
}
