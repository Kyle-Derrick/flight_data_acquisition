package com.drsn.flights.other;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

/**
 * Flightaware网站新用户注册工具模块
 * @author drsnow
 *
 */
public class FlightawareRegister {

	private static final String nameRoot = "dr";
	
	public static String register() {
		String name = nameRoot + new Date().getTime();
		name += new Random().nextInt(100);
		String w_sid = null;
		try {
            Connection conn = Jsoup.connect("https://zh.flightaware.com/account/join");
            conn.method(Method.POST);
            Map<String, String> data = new HashMap<String, String>();
            data.put("name", "");
            data.put("name_first", name);
            data.put("name_last", name);
            data.put("email", name + "@aqewf.asw");
            data.put("flightaware_username", name);
            data.put("flightaware_password", name);
            data.put("password_reenter", name);
            data.put("use_terms", "1");
            data.put("minimum_age", "1");
            conn.data(data);
            conn.followRedirects(false);
            Response response;
            response = conn.execute();
            Map<String, String> getCookies = response.cookies();
            String cookie = getCookies.toString();
            cookie = cookie.substring(cookie.indexOf("{")+1, cookie.lastIndexOf("}"));
            w_sid = cookie.split(",")[0];
//            cookie = cookie.replaceAll(",", ";");
//            System.out.println(cookie);
            System.out.println(cookie.split(",")[0]);

//            for (String x : cookie.split(",")) {
//            	System.out.println(x);
//			}
//            System.out.println(response.body());
        } catch (IOException e) {
//            e.printStackTrace();
        }
		return w_sid;
	}
}
