import com.coveo.pushclient.BatchUpdate;
import com.coveo.document.DeleteDocument;
import com.coveo.document.DocumentBuilder;
import com.coveo.source.SourceClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PushBatchOfDocuments {
    public static void main(String[] args) {
        SourceClient sourceClient = new SourceClient("my_api_key", "my_org_id");

        DocumentBuilder firstDocumentToAdd = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
        DocumentBuilder secondDocumentToAdd = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");

        ArrayList<DocumentBuilder> listOfDocumentsToAddOrUpdate = new ArrayList<>() {{
            add(firstDocumentToAdd);
            add(secondDocumentToAdd);
        }};

        DeleteDocument del1 = new DeleteDocument("123");
        List<DeleteDocument> delList1 = new ArrayList<>();
        delList1.add(del1);

        BatchUpdate batchUpdate = new BatchUpdate(listOfDocumentsToAddOrUpdate, delList1);

        try {
            HttpResponse<String> response = sourceClient.batchUpdateDocuments("my_source_id", batchUpdate);
            System.out.println(String.format("Batch status: %s", response.statusCode()));
            System.out.println(String.format("Batch response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
