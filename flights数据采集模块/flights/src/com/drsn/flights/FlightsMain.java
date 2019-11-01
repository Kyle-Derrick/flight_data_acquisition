package com.drsn.flights;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.drsn.flights.bean.SpringConfig;
import com.drsn.flights.other.AirportLocationCrawl;
import com.drsn.flights.other.AirportNameCrawl;
import com.drsn.flights.other.CloseCmdSend;

/**
 * 该程序主类
 * @author drsnow
 */
public class FlightsMain {

	/**
	 * 程序主函数
	 * @param args 。。。
	 */
	public static void main(String[] args) {
		FlightsMain m = new FlightsMain();
		//初始化spring
		m.springConfigInit();
		
		while(true){
			Scanner sc = new Scanner(System.in);
			System.out.println("请选择 Please choose：");
			System.out.println("1.开始采集数据 Start collecting data");
			System.out.println("2.停止采集数据 Stop collecting data");
			System.out.println("3.机场名更新 Airport name update");
			System.out.println("4.机场所在地更新 Airport location update");
			System.out.println("其他键退出 Other keys exit");
			int i = sc.nextInt();
			switch (i) {
			case 1:
	
				//调用线程管理器开启爬虫线程
				SpringConfig.getTaskManager().startTask();
				//关闭命令监听
				m.closeAccept();
				
				return;
	
			case 2:
	
				System.out.println("请输入ip:端口号 Please enter the ip: port number");
				System.out.println("关闭本机可直接回车  Close the machine and return to the car directly：");
				sc = new Scanner(System.in);
				String ip = sc.nextLine();
				if (ip.length()<=0) {
					CloseCmdSend.close("127.0.0.1", SpringConfig.getCfg().getAcceptPort());
				}else {
					try {
						CloseCmdSend.close(ip.split(":")[0], Integer.valueOf(ip.split(":")[1]));
					} catch (Exception e) {
						System.err.println("ip或端口输入不正确 Incorrect IP or port input！");
					}
				}
				
				break;
	
			case 3:
				AirportNameCrawl.main(args);
				break;
	
			case 4:
				AirportLocationCrawl.main(args);
				break;
				
			default:
				return;
			}
		}
		
	}
	
	/**
	 * 初始化spring
	 */
	private void springConfigInit(){
		ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");
		System.out.println(context.getBean("sessionFactory"));
		//将ApplicationContext存入SpringConfig中
		SpringConfig.setContext(context);
		
	}
	
	/**
	 * 关闭命令监听
	 */
	private void closeAccept() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		BufferedReader reader = null;
		try {
			//在CfgBean中配置的端口上开启关闭命令监听
			serverSocket= new ServerSocket(SpringConfig.getCfg().getAcceptPort());
			boolean is = true;
			while (is) {
				socket = serverSocket.accept();
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String info = null;
				while ((info = reader.readLine()) != null){
					if (info.equals("exit")) {
						is = false;
						break;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("关闭命令监听程序启动失败,可能已被占用！");
		}finally{
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {}
			}

			//调用线程管理器关闭爬虫线程
			SpringConfig.getTaskManager().stopTask();
			((AbstractApplicationContext) SpringConfig.getContext()).close();
			
			System.out.println("程序关闭");
			System.exit(0);
		}
	}
	
	/**
	 * 关闭命令发送，
	 */
//	private void closeCmdSend(){
//		Socket socket = null;
//		BufferedWriter writer = null;
//		try {
//			socket = new Socket("127.0.0.1",9833);
//			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//			writer.write("exit");
//			writer.flush();
//		} catch (IOException e) {
//			System.err.println("关闭命令发送失败！");
//		}finally{
//			if (socket != null) {
//				try {
//					socket.close();
//				} catch (IOException e) {}
//			}
//			if (writer != null) {
//				try {
//					writer.close();
//				} catch (IOException e) {}
//			}
//		}
//	}

}
