package com.server.pk;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public final class server {

	private final static String FILEPATH = "C:\\";
	private final static String SERVERSTRING = "Server: Jastonex/0.1";

	private static final Map<String, String> mimeMap = new HashMap<String, String>() {{
		put("html", "text/html"); 
		put("css", "text/css"); 
		put("js", "application/js");
		put("jpg", "image/jpg");
		put("jpeg", "image/jpeg");
		put("png", "image/png");
	}};

	private static void respond_header(String code, String mime, int length, DataOutputStream out) throws Exception {
		System.out.println(" (" + code + ") ");
		out.writeBytes("HTTP/1.1 " + code + " OK\r\n");
		out.writeBytes("Content-Type: " + mimeMap.get(mime) + "\r\n");
		out.writeBytes("Content-Length: " + length + "\r\n"); 
		out.writeBytes(SERVERSTRING);
		out.writeBytes("\r\n\r\n");
	}

	private static void content_setter(String inString, DataOutputStream out) throws Exception {
		String method = inString.substring(0, inString.indexOf("/")-1);
		String file = inString.substring(inString.indexOf("/")+1, inString.lastIndexOf("/")-5);
		
		if(file.equals(""))
			file = "index.html";	
		
		String mime = file.substring(file.indexOf(".")+1);		

		if(file. contains(";") || file.contains("*")){
			System.out.println(" (Error: Bad string)");
			return;
		}

		if(method.equals("GET")) {
			try {
				// Open file
				byte[] fileBytes = null;
				InputStream input_stream = new FileInputStream(FILEPATH+file);
				fileBytes = new byte[input_stream.available()];
				input_stream.read(fileBytes);

				respond_header("200", mime, fileBytes.length, out); // Send header
				out.write(fileBytes); // Write content of file

			} 
			catch(FileNotFoundException e) {
				try {
					byte[] fileBytes = null;
					InputStream is = new FileInputStream(FILEPATH+"404.html");
					fileBytes = new byte[is.available()];
					is.read(fileBytes);
					respond_header("404", "html", fileBytes.length, out);
					out.write(fileBytes);
				} 
				catch(FileNotFoundException e2) {
					String responseString = "404: File Not Found";
					respond_header("404", "html", responseString.length(), out);
					out.write(responseString.getBytes());
				}
			}
		} 
		else if(method.equals("POST")) {

		} 
		else if(method.equals("HEAD")) {
			respond_header("200", "html", 0, out);
		} 
		else {
			respond_header("505", "html", 0, out);
		}
	}

	public static class connection implements Runnable {

		protected Socket socket = null;

		BufferedReader in;
		DataOutputStream out;
		String inString;

		public connection(Socket connectionSocket) throws Exception {
			this.socket = connectionSocket;
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new DataOutputStream(this.socket.getOutputStream());

			this.inString = this.in.readLine();

			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = "[" + sdf.format(cal.getTime()) + "] ";
			System.out.print(time + this.socket.getInetAddress().toString() + " " + this.inString);			
		}

		public void run() {
			try{
				if(this.inString != null)
					content_setter(this.inString, this.out);

				this.out.flush();
				this.out.close();
				this.in.close();

			} catch (Exception e) { 
				System.out.println("Error:");				
			}
		}
	}
}
