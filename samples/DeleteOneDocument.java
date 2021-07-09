import com.coveo.pushapiclient.Source;

import java.io.IOException;
import java.net.http.HttpResponse;

public class DeleteOneDocument {
    public static void main(String[] args) {
        Source source = new Source("my_api_key", "my_org_id");
        String documentId = "https://my.document.uri";
        Boolean deleteChildren = true;

        try {
            HttpResponse<String> response = source.deleteDocument("my_source_id", documentId, deleteChildren);
            System.out.println(String.format("Delete document status: %s", response.statusCode()));
            System.out.println(String.format("Delete document response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
