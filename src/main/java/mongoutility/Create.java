package mongoutility;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Create {
	
	public void createCollection(MongoClient client, String dbName, String collectionName) {
		MongoDatabase database= client.getDatabase(dbName);
		database.createCollection(collectionName);			
	}
	
   public void insertOne(MongoClient client, String dbName, String collectionName, Map<String, Object> data) {
	   
	   MongoDatabase database= client.getDatabase(dbName);
	   
	   MongoCollection<Document> collection= database.getCollection(collectionName);
	   
	   Document document = new Document(data); 
	   
	   collection.insertOne(document);
   }
   
public void insertMany(MongoClient client, String dbName, String collectionName, List<Map<String, Object>> data) {
	   
	   MongoDatabase database= client.getDatabase(dbName);
	   
	   MongoCollection<Document> collection= database.getCollection(collectionName);
	   
	  // Document document = new Document(data); 
	   
//	   List<Document> values = data.stream().map(doc -> doc).co
	   
	   //collection.insertMany(null, null);
   }
}
