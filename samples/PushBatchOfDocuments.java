import com.coveo.pushapiclient.BatchUpdate;
import com.coveo.pushapiclient.DocumentBuilder;
import com.coveo.pushapiclient.Source;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class PushBatchOfDocuments {
    public static void main(String[] args) {
        Source source = new Source("my_api_key", "my_org_id");

        DocumentBuilder firstDocumentToAdd = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
        DocumentBuilder secondDocumentToAdd = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");

        DocumentBuilder firstDocumentToDelete = new DocumentBuilder("https://my.document.uri?ref=3", "My document to delete");

        ArrayList<DocumentBuilder> listOfDocumentsToAddOrUpdate = new ArrayList<>() {{
            add(firstDocumentToAdd);
            add(secondDocumentToAdd);
        }};

        ArrayList<DocumentBuilder> listOfDocumentsToDelete = new ArrayList<>() {{
            add(firstDocumentToDelete);
        }};

        BatchUpdate batchUpdate = new BatchUpdate(listOfDocumentsToAddOrUpdate, listOfDocumentsToDelete);

        try {
            HttpResponse<String> response = source.batchUpdateDocuments("my_source_id", batchUpdate);
            System.out.println(String.format("Batch status: %s", response.statusCode()));
            System.out.println(String.format("Batch response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
