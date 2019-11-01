package com.drsn.flights.other;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 校园网断线重登录模块
 * 间断周期性测试网络是否连通，若未连通则登录；
 * @author drsnow
 *
 */
public class HeavyLogin {
	
	static HeavyLogin t = new HeavyLogin();
	public static void main(String[] args){
		while (true) {
			try {
				Document baidu = Jsoup.connect("https://www.baidu.com/")
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
						.timeout(5000)
						.get();
				if (baidu.title().equals("login")) {
					throw new Exception();
				}
				Thread.sleep(10000);
			} catch (Exception e) {
				System.out.println("network break ,reconnect!");
				BufferedWriter writer = null;
				Socket socket = null;
				try {
					socket = new Socket("10.10.11.12", 80);
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					writer.write("POST /webauth.do?wlanuserip=172.17.6.250&wlanacname=XF_BRAS&mac=fa:06:11:ac:00:00&vlan=0&rand=a8023424234f30 HTTP/1.1\nHost: 10.10.11.12\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\nAccept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2\nAccept-Encoding: gzip, deflate\nReferer: http://10.10.11.12/webauth.do?wlanuserip=172.17.6.250&wlanacname=XF_BRAS&mac=fa:06:11:ac:00:00&vlan=0&rand=a8023424234f30\nContent-Type: application/x-www-form-urlencoded\nContent-Length: 294\nCookie: portalUserCookie=WFlHWV9TMTczMDExMzNAU0NJVEN8OTgzMzY0RFJTTiw6MTUyNTIyMTI0NzE3NA==; JSESSIONID=1DEA47BD7C7D2B9BBD3C2C78B0AED699\nConnection: keep-alive\nUpgrade-Insecure-Requests: 1\nPragma: no-cache\nCache-Control: no-cache\n\nloginType=&auth_type=0&isBindMac=0&pageid=1&templatetype=3&listbindmac=0&isRemind=0&loginTimes=&groupId=&url=&notice_pic_loop1=%2Fportal%2Fuploads%2Fgeneral%2Fdemo1%2Fimage%2Flogo.jpg&notice_pic_loop2=%2Fportal%2Fuploads%2Fgeneral%2Fdemo1%2Fimage%2Fbanner.jpg&userId=drsnow&passwd=983364DRSN%2C");
					writer.flush();
				} catch (IOException e1) {
				}finally{
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException e1) {}
					}
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e1) {}
					}
				}
			}
		}
	}
}
