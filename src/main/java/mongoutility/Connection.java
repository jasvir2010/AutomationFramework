package mongoutility;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Connection {

	
	public MongoClient connect(String server, String readPreference, String appName, boolean directConnection, boolean ssl) {
		
		 String conString = String.format("mongodb://%s/?readPreference=%s&appname=%s&directConnection=%b&ssl=%b", server, readPreference, appName, directConnection, ssl);
		 
	     return MongoClients.create(conString);
	}
	
	
	public MongoClient connect(String server, int port,  String username, String password, String authDB) {
		
		 String conString = String.format("mongodb://%s:%s@%s:%n/?authSource=%s", username, password, server, port, authDB);
		 
	     return MongoClients.create(conString);
	}
	
	public com.mongodb.MongoClient connect(String server, int port) {
				
		return new com.mongodb.MongoClient(server, port);
	}
}
