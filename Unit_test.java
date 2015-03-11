package com.server.pk;

import java.net.ServerSocket;
import java.net.Socket;

import com.server.pk.server.connection;

public class unit_test {

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(2000);

		for(;;) {
			Socket connectionSocket = serverSocket.accept();

			new Thread(new connection(connectionSocket)).start();	
		}
	}
}

