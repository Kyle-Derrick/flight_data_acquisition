package com.drsn.flights.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drsn.flights.bean.AirlineBean;
import com.drsn.flights.bean.AirportBean;
import com.drsn.flights.bean.FlightBean;
import com.drsn.flights.bean.ProxyBean;
import com.drsn.flights.bean.SpringConfig;
import com.drsn.flights.dao.AirlineDao;
import com.drsn.flights.dao.AirportDao;
import com.drsn.flights.dao.FleetDao;
import com.drsn.flights.dao.FlightDao;
import com.drsn.flights.other.FlightawareRegister;

/**
 * 具体航班数据爬取线程，
 * @author drsnow
 *
 */
@Service
public class FlightsCollectTask extends TaskBasics {
	//缓存的fleet的集合
	private volatile List<String> fleetlist = new ArrayList<String>();
	//当前缓存集合的起始id
	private volatile int id = 1;
	//缓存条数，由用户配置
	private int number;


	//已存在的airline
	private Set<String> existedAirline = Collections.synchronizedSet(new HashSet<String>());
	//已存在的Airport
	private Set<String> existedAirport = Collections.synchronizedSet(new HashSet<String>());


	//当前正在爬的Fleet
	private Map<Thread,String> nowFleet = Collections.synchronizedMap(new HashMap<Thread,String>());
	//当前正在爬的日期
	private Map<Thread,Integer> nowtime = Collections.synchronizedMap(new HashMap<Thread,Integer>());
	
	private int w_sidIndex = 0;
	//登录用户的cookies
	private String[] w_sidList = {"903ceebba06dd483e32c083d7f191fd310e9ee00f3eadb7f137ba34c5b9d31ea",
									"54634fa9a9b541a73865ac466984c18cce4273048b84dbe1b776671b21f087c2",
									"db8a8814239e39dec668d7b50c28809b11189c86a74ce6bbbc0c7b4c67c2f12e",
									"28ce954851ae6f554f8697561e1d6ef26984203ef89d2e37f7dfb807d1a4692b",
									"9a87898574bed4a125b996a303b38a9d620ec63ab923f1119dcddaa5ccd7954f",
									"fd741beccfcd7c8f0959e14613fb6f5255a76e4876207a61b91b998c2ce7071b",
									"03cf87bbf6b7288b7d941fd6e8f3d956303f6a1df19010099adbba5efdd2157a",
									"9071879befc8f0e76d58a9c019d9c93dd96545a7854756c826af0fb69150f61d",
									"09f28312dc7e29b9b828bc1cad399e0986f7a0ad2e62da5a6611f5921d7f3afb",
									"6b5d28ea547ded336a03e381dda6a74bcdb26515befa1303e8fe8596d5eabc68",
									"f6add778a2c31ea8a716b79b3c718e7034de4f840a78624d4aa8bb882f11c3bf",
									"5f3e1baf771fedd6f527ae086a93bdfca18e38908b867b2ec73835241fcc80cc",
									"80048ac6ad5fadb36787ecd924856db5777dc2c7f04a93a3ab24ee92e543a7d3",
									"424706f0246edff51ede5c837d1e4a1fbe4dd1df23b96dcaa0dfac7a6196d929",
									"7e4eb3b152a081825c825aa48102bbd6b791d8633e89ffaf24174aeea8cacc32",
									"56efc1df2596eb311d931a5ae1b61129b087a637c4ebd141b73d3f1619b98697",
									"3e9f905df01be79f726dd89fcc0615f3332111f358c03b52d3039f7b8eff605a",
									"69e73a46fc1ef8cda8c6c2bf02846bf63da98fe2033ada6f987cf3a00d6080d7",
									"4411cff612ed787a2d32e3b3b52b75bc8c19d5432f6b227bde5dab405c7b26e0",
									"c8e44c2e47fefa7fe396f82659a578c07e4ba8d15bf400aadc3b3e1e1f8da62e",
									"8193bed23580546a070f9b46663217215fb924bc5d6645f9ff3fde162612b4bd",
									"dc48097a420d3d9636ea09be5a82cb562fff67604376d8c4a0c401ed40ee9c4a",
									"9af44034ec6848746cc6eee789875fa048c92a6be39de36c0dd9783a615889d4",
									"4f7527e24ace6f0f748a9c918f9e1f9ba64c616d760d1ca6a418abbbe97056e3",
									"009b6b52d06448256ed3fdc4b353281ee2265bef61c07ba2a3e9d60620ef19fc",
									"ea709516f5e9b9f3eb37c935152de6e02be4f3633a079636c3edb875b80507cb",
									"1781e68706dbbc045404678a35f39373cc66924c456c88d74f1f580e6d59b0bb",
									"232b9b2a3c08df39b4b881ae0fe5d75c27586807579fac0ddcb72147e36145e4",
									"835501e9080799160e9b3cd768d4e92f137768dcaa70578ad230fa4c49aadf66",
									"abf395997e993b3dc90240c34fde1dd4d66054ec5c1c2cbe9e2f2c44da836d32",
									"7bd633dd6f2c84e25d309def028c395ca7ecc48a4b5e2ed63ba44c066dc8282c",
									"709cc61356505b3a79a92db4a37e003ac4a318b77d42e60389e1b7f3da269152",
									"0b514c5c4487744af06eb1dda1ee613fa99cd246a7e4084262c268e8a16542cb",
									"1a4850cc0f91bada9725f4c45a59d30e12684e4ed52a33fbef1cb396b94c2e4f",
									"a570a92833cda6f734928bcfcfd86a8dc30ac1a5939af39a1bbf7c3a18049e95",
									"17e47b48db3bf07cba18ac184daae9eb9a7ad1dbce1fea158ed8fbb52a32e82d",
									"44967611aaf1fdf3cb4a8cee67c94744b46838f93c3398b065b272a0b2cee8f7",
									"e57559f2c84b89e2aefd9b73a0db9aeea67d56b26e9dedc6c1b4d7324ab70fb8",
									"84f615f18183bb6f86eaa732179d723f226ea7c44af9c3ffaae68417c7139f37",
									"ec3c72397d11e1eeb9425ec21eeadee06965d53ae681431135f41015948fbd3f",
									"d4be0bde69131857890d24ffae97ac2e80525ab3d380c8fb82f0898d329b1599",
									"b6f837a57838fd6ba094a48cff178a60c236e31b08de98233d101f87f456a9d1",
									"e7944afc0cf488ec22f85bd18b8d8bc76615e02dfcf377db2dd86f4935bce22f",
									"baedf3b7ba427b663bef044547b19b56b9578b0d92899c34f5415e599a96a2cf",
									"69bedc9cbf01b1dd9530b7b1ed38bf196b2f70376a512f838fcaa628b58f1745",
									"f9ce4f74c4521e51ec4e2807dc5361194912d33b97b630ee487c9129a6795d35",
									"fbcfc0f4c91f61f6952a1f3fa3cc4c7a9cc77322663708c44508a0cba4995dac",
									"640ad9bde35c191f4bd3fa51b819d9a02fdcbb068693d71950d739fbdf1d36c7",
									"2f5350a2ac0131d51e0f296ee91ab15692f12988527ef582f00e7ac84976d17b",
									"a4710dbb00b268bfbaa7d80c14b09b3fe197c3c06e49f5f383342794f0213fff",
									"eab413d6db675841a80b7595e5929c310f0772083f9eaa01966572c9dd6bc188",
									"8f1cc8e86d9c2c6ae8c40aabbe9a449bc182b172e261cfb28aae5de3bf5495b9",
									"e7cd331e83e960c3314f071d24c366e95af7ff31583a2ff68ff977a324852982",
									"1a133638a90d646b3cd1d96a254b5b53e09dc5abd4f53009dff9cb6a45231033",
									"0880bcb08f86962c1b2ab1966f5491ff5afc1b2c41096d725a830d3d20698d4d",
									"b4b90233905e71c3dc7560b7b069956734a5e53b9b514959f6736313f621c8eb",
									"3f318ce2956e81e71275a8b8dc85accae59d8e7fea44652415a08eb7468a4965",
									"efc2a3125f624099fd36a962acf1d4901655f26d14b42752bb7876d3b51fe95e",
									"4990cd49c99fd986f1c7c6d58c54eef19b35c2d5ed567b117acda84fbfa2a0c9",
									"4f8384392768a6bfafcdc3cbd1d2c209ae416d4fa19454f788a05e974890354d",
									"b39826f3b28dec3c0b2bc58b00d0073f9d2c8667c7fdc6a778cc69dd590400b9",
									"14ef63ac89dc0f9fd204095960c711d5db08fe0680f53a84924e1f651a8012ae",
									"62415b64296c976edfc146616eeaad818332dac7c9a51d2a52723f4a93227677",
									"3e5e09fb2d033eb8a101ca3a7eed7c1d23467a7c7b42074935646703f7eab863",
									"10535e10c5948b1c8a13e203cc8410ea59a2e00649a81a113d748feca3587bae",
									"7ed3e1c5d50ca06e45a539c6d88ea91601d38f8eeafb705d087eef5ad62f8a17",
									"51fb303eba24d71c67ff8a95260aedf792435a9e7ad84842ab8c9bb1a3a9e5eb",
									"9633d511730fc88de4aacb89789b795f3a8639e335059ad62649c506bcb09b45",
									"a8d19ff8b6d6c46ff3b266a51fc306c3e15f6684d37b765825a302bd31d6e918",
									"efe5c201afa85b2354ef38c7705df5e9b43d7240bef0432c89a349704fa8d8ca",
									"63b111465af9850389dad51512b224638cb2bd4fa08e5c5ce6d5f4c5625f6a72",
									"27bf706041d002fdd99bf3577e752d9cf35cd79a90cb5cbec5873825aa09a495",
									"c50d3b7d09e4fdcc42700b8c53d95110e862cc6d1ccefee303b43c1d0fc606cf",
									"2a7ae55ece419576fb0cd861fc034e4ed0129f7f8895ad05e2f61450f74cad43",
									"c1af7d473ef82fc94790a272af1a58abe9fe48a3863adf382eb23bd58359eafb",
									"6a84685e723c0e24d9e57fcc487d75ebe7b449a74822a7a919954df46f7bf5d1",
									"d3d49a051fe60e72bbfe334d3962eb558393d1db5204c2bac1da6969999725c2",
									"010d5b5934df83d75d9845729354354b9357b29ff0814207250cc53782f887e8",
									"355c18ecfd881ad165f129fee5e69937e004476cb1349f5c56d3adb9f9b875f5",
									"ef8c24ab12e57fff45826cebadf22744defbdfe8e87d35aacdd4adfe1e9718c4",
									"bded8adb23c64a35394caaa6f37cd0a74e81398f21ab2feab132690623987894",
									"8293252f3d6ffa573d3ffa256727e87119c0b6f316d70f8cf1890d29492fe01f",
									"fb8d129eb35394ec241a8f5447d67e84980e1534877937bd1ad2752dc05cde08",
									"250ce7976cb135b206e474aa0649f0a1e20258d834995031a0d1c55ed8ccd3ea",
									"baeb5768ee9a766e742c7c5b940609e12d5c2afcd28aefd1095b8dd2dab1561c",
									"9bd5587acf4ddaa04a4115fbf9a12e233f1297e02ef991e94d2b1508054050e8",
									"aec640bf3a68899dc0a24168f5f0c7b82032b8a3b3b7f812ad68aa7e9861d7c6",
									"b6ab3ad7cca12242ccc1f92624e29e6d43ff61b6cd49d1eed2a75acdb73f65a2",
									"2d36e0938e822f38efdc85ff5a5e1ef7d7b41f6e20196c0cecc292124c6a831f",
									"880851c0ee1787e4ee7685b19f61fbb566c8a161c92951308885039d70da9357",
									"75ce854e07bb2a05967434cbe3533fb97449999feab727095222c1fc5226a4a0",
									"32d59218902bba456863fc458c060a6a5a83306066bc4ef4a08d85ab6cbbe689",
									"aca0a458409ca1e9ab85a2d88674bce8e5b1d9cef0380c7286fd7adfd2123667",
									"aab4f28632479c9b7ee53c0017466b3416b6ecce76335bc8eaa297c130355b9e",
									"1b72aa06aab5a8097f076bbca7c140654583752774e56b95ad3460816c521e03",
									"efe5fd7cb24c31608b76c1691afa7864c8d7a44e384bb09025b6a4ef88641fd1",
									"5bb96d83f695c404c330375c3619b7f30a1ab3d6b3f77c7fb31defddf819cce3",
									"47ef89d7221a705006df12cca299567850d3d12206dee87e6e2f647b96ad5ab6",
									"c91171c4c545af1f542c077658afb4233a3bba4ac40844dce58609e2e9f2cf91"};

	
	@Autowired
	private FlightDao flightDao;
	@Autowired
	private AirlineDao airlineDao;
	@Autowired
	private AirportDao airportDao;
	@Autowired
	private FleetDao fleetDao;
	@Autowired
	private ProxyFromSqlTask proxyFromSqlTask;

	/**
	 * 获取新的fleet集合
	 */
	private void addFleetList() {
		fleetlist.addAll(fleetDao.getFleetList(id, number));
	}
	/**
	 * 从缓存集合获取fleet，如果没有了，就调用addFleetList获取新的，如果数据库里没有，则返回null
	 * @return 返回获取的fleet，若没有则返回null
	 */
	private synchronized String getFleet(){
		if (fleetlist.size()<=0) {
			addFleetList();
			if (fleetlist.size()<200) {
				if (id == 1&&fleetlist.size() == 0) {
					return null;
				}else if (id == 1&&fleetlist.size() > 0) {
				}else if (id >= 1&&fleetlist.size() == 0) {
					id = 1;
					addFleetList();
					id += number;
				}else {
					id = 1;
				}
			}else {
				id+=number;				
			}
		}
		String result = fleetlist.get(0);
		fleetlist.remove(0);
		return result;
	}

	/**
	 * 线程执行任务
	 */
	@Override
	public void doWork() {
		int i =0;
			String fleet;

			Thread thisThread = Thread.currentThread();
			if (nowFleet.containsKey(thisThread)) {
				fleet = nowFleet.get(thisThread);
				System.out.println("断线重启后继续flightCollect： " + fleet);
			}else {
				fleet = getFleet();
				if (fleet == null) {
					return;
				}		
			}
			ProxyBean proxy = null;
			
			String rUrl = "https://zh.flightaware.com";
			while(getIs()) {
				String pUrl = rUrl + "/live/flight/";
				try {
					proxy = proxyFromSqlTask.getProxy();
					if (proxy == null) {
						continue;
					}
					nowFleet.put(thisThread, fleet);
					String w_sid = getW_sid();
					//解析指定网页，
					Document document = Jsoup.connect(pUrl+fleet+"/history/2000")
							.cookie("w_sid", w_sid)
							.proxy(proxy.getIp(), proxy.getPort())
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
							.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
							.timeout(15000)
							.get();
					//检验登录信息是否有效,如果无效就新注册一个并替换
					Element element = document.selectFirst(".topMenuList");
					if (element == null || element.selectFirst("#mainDropDownLink") == null) {
						replaceWsid(w_sid);
						continue;
					}
					//选择符合条件的dom
						Elements flights = document.select("tr[class~=(smallActiverow[1|2] rowClickTarget)]");
						Integer t = 99999999;
						if (nowtime.containsKey(thisThread)) {
							t = nowtime.get(thisThread);
						}
						
						for (Element flight : flights) {
							if (!getIs()) {
								return;
							}
							String p = flight.attr("data-target");
							Integer time = Integer.valueOf(p.split("/")[5]);
							if (time >= SpringConfig.getCfg().getStartTime() && time <= SpringConfig.getCfg().getStopTime() && time <= t) {
								if (t == 99999999) {
									System.out.println("断线重启后继续time,flightCollect： " + time);
								}
								//爬取主要信息

								nowtime.put(thisThread, time);
								collectMain(rUrl + p);
							}
						}
						
						nowFleet.remove(thisThread);
						nowtime.remove(thisThread);
					return;
				} catch (IOException e) {
//					proxyFromSqlTask.deleteProxy(proxy);
					i++;
					if (i >= 10) {
						i = 0;
						System.err.println("Error:collect fleet"+fleet+"\t"+e.getMessage());
					}
				}catch (NullPointerException e) {
					nowFleet.remove(thisThread);
					nowtime.remove(thisThread);
					return ;
				}
			}
	}
	/**
	 * 详细信息爬取
	 * @param url 传入爬去的网页url
	 */
	private void collectMain(String url){
		ProxyBean proxy = null;
		int i = 0;
		while (getIs()) {
			try{
				proxy = proxyFromSqlTask.getProxy();
				if (proxy == null) {
					continue;
				}
				Document document = Jsoup.connect(url)
						.proxy(proxy.getIp(), proxy.getPort())
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
						.timeout(15000)
						.get();
				//选择符合条件的dom
				Elements d1 = document.select("script[type!=*]");
				Collections.reverse(d1);
				for (Element element : d1) {
					if (element.html().startsWith("var trackpollBootstrap = ")) {
		//				System.out.println(element.html());
						
							doJson(element.html(),url);
						return;
					}
				}
			}catch (IOException e){
//				proxyFromSqlTask.deleteProxy(proxy);
				i++;
				if (i >= 10) {
					i = 0;
					System.err.println("Error:collect"+url+"\t"+e.getMessage());					
				}
			}catch (NullPointerException e) {
				return ;
			}catch (Exception e) {
			}
				
		}
	}
	
	/**
	 * 解析json并将数据存入数据库
	 * @param info 含json的js代码
	 * @param url 当前页面url
	 * @throws Exception 抛出Execption
	 */
	private void doJson(String info,String url)throws Exception {
		if (info.contains("\"unknown\":true")) {
			return;
		}
			//提取主要json数据
			String result = Pattern.compile("^(var trackpollBootstrap = \\{)[^\\{]*\\{[^\\{]*").matcher(info).replaceAll("");
			result = Pattern.compile("(\\}\\};)$").matcher(result).replaceAll("");
			//去除不要的activityLog部分也就是历史列表部分数据
			result = Pattern.compile("\"activityLog\":\\{((?!additionalLogRowsAvailable).)*additionalLogRowsAvailable\":(false|true)\\},")
					.matcher(result).replaceAll("");
			result = Pattern.compile(",\"links\":\\{((?!\\}).)*\\}").matcher(result).replaceAll("");
			//取出航线信息并从json数据中去除
			Matcher matcher = Pattern.compile("\"track\":\\[\\{(.(?!\\}\\]).)*\\}\\],").matcher(result);
			if (matcher.find()) {
				Pattern.compile("(^(\"track\":)|,$)").matcher(matcher.group()).replaceAll("");
			}
			result = matcher.replaceAll("");
			//取出相关缩略图信息并从json数据中去除
			matcher = Pattern.compile("\"relatedThumbnails\":\\[\\{((?!\\}\\]).)*\\}\\],").matcher(result);
			if (matcher.find()) {
				Pattern.compile("(^(\"relatedThumbnails\":)|,$)").matcher(matcher.group()).replaceAll("");
			}
			result = matcher.replaceAll("");
			
			//解析json数据并存入数据库
	        JSONObject jsonObject = JSONObject.fromObject(result);
	        JSONObject airlineJson = jsonObject.optJSONObject("airline");
	        JSONObject destinationJson = jsonObject.optJSONObject("destination");
	        JSONObject originJson = jsonObject.optJSONObject("origin");

        	//判断航空公司数据是否存在，存在就存入数据库
	        if (airlineJson != null) {
	        	String icao = airlineJson.optString("icao");
	        	if (!containsExistedAirline(icao)) {
	        		AirlineBean airlineBean = new AirlineBean();
	        		airlineBean.setFullName(airlineJson.optString("fullName"));
	        		airlineBean.setShortName(airlineJson.optString("shortName"));
	        		airlineBean.setIcao(airlineJson.optString("icao"));
	        		airlineBean.setIata(airlineJson.optString("iata"));
	        		airlineBean.setCallsign(airlineJson.optString("callsign"));
	        		airlineBean.setUrl(airlineJson.optString("url"));
	        		try {
	        			airlineDao.saveAirline(airlineBean);
		        		addExistedAirline(icao);
	        		} catch (Exception e) {}
				}
			}
	        //判断起飞机场数据是否存在，存在就存入数据库
	        if (originJson != null) {
	        	String iata = originJson.optString("iata");
	        	if (!containsExistedAirport(iata)) {
		        	AirportBean airportBean = new AirportBean();
		        	airportBean.setTz(originJson.optString("TZ"));
		        	airportBean.setIata(originJson.optString("iata"));
		        	airportBean.setIcao(originJson.optString("icao"));
		        	airportBean.setFriendlyName(originJson.optString("friendlyName"));
		        	airportBean.setFriendlyLocation(originJson.optString("friendlyLocation"));
		        	//判断经纬度数据是否存在，存在就存入数据库
		        	JSONArray array = originJson.optJSONArray("coord");
		        	if(array != null){
		        		airportBean.setLongitude(array.optDouble(0));
		        		airportBean.setLatitude(array.optDouble(1));
		        	}
	        		try {
			        	airportDao.saveAirport(airportBean);
		        		addExistedAirport(iata);
	        		}catch(Exception e){}
	        	}
			}
	        //判断降落机场数据是否存在，存在就存入数据库
	        if (destinationJson != null) {
	        	String iata = destinationJson.optString("iata");
	        	if (!containsExistedAirport(iata)) {
		        	AirportBean airportdBean = new AirportBean();
		        	airportdBean.setTz(destinationJson.optString("TZ"));
		        	airportdBean.setIata(destinationJson.optString("iata"));
		        	airportdBean.setIcao(destinationJson.optString("icao"));
		        	airportdBean.setFriendlyName(destinationJson.optString("friendlyName"));
		        	airportdBean.setFriendlyLocation(destinationJson.optString("friendlyLocation"));
		        	//判断经纬度数据是否存在，存在就存入数据库
		        	JSONArray array = destinationJson.optJSONArray("coord");
		        	if(array != null){
		        		airportdBean.setLongitude(array.optDouble(0));
		        		airportdBean.setLatitude(array.optDouble(1));
		        	}
	        		try {
	        			airportDao.saveAirport(airportdBean);
		        		addExistedAirport(iata);
	        		}catch(Exception e){}
	        	}
			}
	        
	        FlightBean flightBean = new FlightBean();
	        flightBean.setFlightId(jsonObject.optString("flightId"));
	        flightBean.setIataIdent(jsonObject.optString("iataIdent"));
	        flightBean.setIdent(jsonObject.optString("ident"));
	        flightBean.setFriendlyIdent(jsonObject.optString("friendlyIdent"));
	        flightBean.setAirlineIcao(airlineJson.optString("icao"));
        	//判断飞机数据是否存在，存在就存入数据库
	        JSONObject aircraftObject = jsonObject.optJSONObject("aircraft");
	        if(aircraftObject != null){
	        	flightBean.setFlighType(aircraftObject.optString("type"));
	        	flightBean.setFlighTypeFriendlyType(aircraftObject.optString("friendlyType"));
	        }
	        if (originJson != null) {
	        	flightBean.setOrigin(originJson.optString("icao"));
	        }
	        if (destinationJson != null) {
	        	flightBean.setDestination(destinationJson.optString("icao"));
	        }
        	//判断起飞降落时间等数据是否存在，存在就存入数据库
	        JSONObject takeoffTimesObject = jsonObject.optJSONObject("takeoffTimes");
	        if(takeoffTimesObject != null){
		        flightBean.setTakeoffTimesActual(takeoffTimesObject.optInt("actual"));
		        flightBean.setTakeoffTimesScheduled(takeoffTimesObject.optInt("scheduled"));
			}
	        JSONObject landingTimesObject = jsonObject.optJSONObject("landingTimes");
	        if(landingTimesObject != null){
		        flightBean.setLandingTimesActual(landingTimesObject.optInt("actual"));
		        flightBean.setLandingTimesScheduled(landingTimesObject.optInt("scheduled"));
	        }
	        JSONObject gateDepartureTimesObject = jsonObject.optJSONObject("gateDepartureTimes");
	        if (gateDepartureTimesObject != null) {
		        flightBean.setGateDepartureTimesActual(gateDepartureTimesObject.optInt("actual"));
		        flightBean.setGateDepartureTimesScheduled(gateDepartureTimesObject.optInt("scheduled"));
	        }
	        JSONObject gateArrivalTimesObject = jsonObject.optJSONObject("gateArrivalTimes");
	        if (gateArrivalTimesObject != null) {
		        flightBean.setGateArrivalTimesActual(gateArrivalTimesObject.optInt("actual"));
		        flightBean.setGateArrivalTimesScheduled(gateArrivalTimesObject.optInt("scheduled"));
	        }
	        JSONObject averageDelaysObject = jsonObject.optJSONObject("averageDelays");
	        if (averageDelaysObject != null) {
		        flightBean.setAverageDelaysDeparture(averageDelaysObject.optInt("departure"));
		        flightBean.setAverageDelaysArrival(averageDelaysObject.optInt("arrival"));
	        }
	        JSONObject distanceObject = jsonObject.optJSONObject("distance");
	        if (distanceObject != null) {
	        	flightBean.setDistanceActual(distanceObject.optInt("actual"));
	        }
	        JSONObject flightPlanObject = jsonObject.optJSONObject("flightPlan");
	        if (flightPlanObject != null) {
		        flightBean.setDirectDistance(flightPlanObject.optInt("directDistance"));
		        flightBean.setSpeed(flightPlanObject.optInt("speed"));
		        flightBean.setAltitude(flightPlanObject.optInt("altitude"));
		        JSONObject fuelBurnObject = flightPlanObject.optJSONObject("fuelBurn");
		        if (fuelBurnObject != null) {
			        flightBean.setFuelBurnGallons(fuelBurnObject.optInt("gallons"));
			        flightBean.setFuelBurnPounds(fuelBurnObject.optInt("pounds"));
		        }
	        }
	        
        	flightBean.setCancelled(jsonObject.optBoolean("cancelled"));
	        
	        try{
	        	flightDao.saveFlight(flightBean);
	        }catch(Exception e){}
	        
			
	}

	/**
	 * 获取一个登录身份Cookie
	 * @return 返回登录信息cookie
	 */
	private synchronized String getW_sid() {
		if (w_sidIndex >=w_sidList.length) {
			w_sidIndex = 0;
		}
		return w_sidList[w_sidIndex++];
	}
	/**
	 * 调用FlightawareRegister工具类新注册一个账号并替换传入的登录身份cookie
	 * @param w_sid 传入登录身份cookie
	 */
	private void replaceWsid(String w_sid) {
		String newW_sid = FlightawareRegister.register();
		for (int i = 0; i < w_sidList.length; i++) {
			if (w_sidList[i].equals(w_sid)) {
				w_sidList[i] = newW_sid;
			}
		}
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	public FlightDao getFlightDao() {
		return flightDao;
	}
	public void setFlightDao(FlightDao flightDao) {
		this.flightDao = flightDao;
	}
	public AirlineDao getAirlineDao() {
		return airlineDao;
	}
	public void setAirlineDao(AirlineDao airlineDao) {
		this.airlineDao = airlineDao;
	}
	public AirportDao getAirportDao() {
		return airportDao;
	}
	public void setAirportDao(AirportDao airportDao) {
		this.airportDao = airportDao;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public FleetDao getFleetDao() {
		return fleetDao;
	}
	public void setFleetDao(FleetDao fleetDao) {
		this.fleetDao = fleetDao;
	}
	
	public ProxyFromSqlTask getProxyFromSqlTask() {
		return proxyFromSqlTask;
	}
	public void setProxyFromSqlTask(ProxyFromSqlTask proxyFromSqlTask) {
		this.proxyFromSqlTask = proxyFromSqlTask;
	}
	public boolean containsExistedAirline(String airlineIcao) {
		return existedAirline.contains(airlineIcao);
	}
	public void addExistedAirline(String airlineIcao) {
		this.existedAirline.add(airlineIcao);
	}
	public void addExistedAirlineAll(Collection<? extends String> airlineIcao) {
		this.existedAirline.addAll(airlineIcao);
	}
	public boolean containsExistedAirport(String airportIata) {
		return existedAirport.contains(airportIata);
	}
	public void addExistedAirport(String airportIata) {
		this.existedAirport.add(airportIata);
	}
	public void addExistedAirportAll(Collection<? extends String> airportIata) {
		this.existedAirport.addAll(airportIata);
	}
	
	public String getNowFleet(Thread thread) {
		return nowFleet.get(thread);
	}
	public void setNowFleet(Thread thread, String flight) {
		this.nowFleet.put(thread, flight);
	}
	public void delNowFleet(Thread thread) {
		this.nowFleet.remove(thread);
	}
	public Integer getNowtime(Thread thread) {
		return nowtime.get(thread);
	}
	public void setNowtime(Thread thread, Integer time) {
		this.nowtime.put(thread, time);
	}
	public void delNowtime(Thread thread) {
		this.nowtime.remove(thread);
	}
	
	
	
}
