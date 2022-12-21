package mongoutility;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Read {

	public void readAll(MongoClient client, String dbName, String tableName) {

		MongoDatabase database = client.getDatabase(dbName);

		MongoCollection<Document> collection = database.getCollection(tableName);

		List<Document> resultList = collection.find().into(new ArrayList<>());
		for (Document result : resultList) {
			System.out.println(result.toJson());
		}

		Consumer<Document> printConsumer = document -> System.out.println(document.toJson());
		collection.find().forEach(printConsumer);
	}

	public void readLimit(MongoClient client, String dbName, String tableName, String key, Object value, int limit) {

		MongoDatabase database = client.getDatabase(dbName);

		MongoCollection<Document> collection = database.getCollection(tableName);

		FindIterable<Document> result = collection.find(eq(key, value));
		
		result = result.limit(limit);
        
	}
	
	public void readData(MongoClient client, String dbName, String tableName, Map<String, Object>searchMap) {

		MongoDatabase database = client.getDatabase(dbName);

		MongoCollection<Document> collection = database.getCollection(tableName);

		Document searchQuery = new Document();
		searchQuery.putAll(searchMap);
		MongoCursor<Document> cursor = collection.find(searchQuery).cursor();

		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
	}
}
