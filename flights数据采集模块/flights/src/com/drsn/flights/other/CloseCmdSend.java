package com.drsn.flights.other;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 关闭命令发送模块，
 * @author drsnow
 *
 */
public class CloseCmdSend {

	public static void close(String ip, Integer port) {
		Socket socket = null;
		BufferedWriter writer = null;
		try {
			socket = new Socket(ip,port);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("exit");
			writer.flush();
		} catch (IOException e) {
			System.err.println("发送关闭命令时出错，error with close commod send program!");
		}finally{
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {}
			}
		}
	}

}
