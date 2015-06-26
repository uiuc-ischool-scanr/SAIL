/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class VisServerHandler implements HttpHandler {
	
	String root;

	public VisServerHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public VisServerHandler(String root) {
		// TODO Auto-generated constructor stub
		try {
			this.root = new File(root).getCanonicalFile().getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.root = "E:\\Code\\Java\\OnlineModelLearning\\output\\visualization";
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		// TODO Auto-generated method stub
		URI uri = t.getRequestURI();
        System.out.println(root + uri.getPath());
        File file = new File(root + uri.getPath()).getCanonicalFile();
        if (!file.getPath().startsWith(root)) {
          // Suspected path traversal attack: reject with 403 error.
          String response = "403 (Forbidden)\n";
          t.sendResponseHeaders(403, response.length());
          OutputStream os = t.getResponseBody();
          os.write(response.getBytes());
          os.close();
        } else if (!file.isFile()) {
          // Object does not exist or is not a file: reject with 404 error.
          String response = "404 (Not Found)\n";
          t.sendResponseHeaders(404, response.length());
          OutputStream os = t.getResponseBody();
          os.write(response.getBytes());
          os.close();
        } else {
          // Object exists and is a file: accept with response code 200.
          t.sendResponseHeaders(200, 0);
          OutputStream os = t.getResponseBody();
          FileInputStream fs = new FileInputStream(file);
          final byte[] buffer = new byte[0x10000];
          int count = 0;
          while ((count = fs.read(buffer)) >= 0) {
            os.write(buffer,0,count);
          }
          fs.close();
          os.close();
        }

	}

}
