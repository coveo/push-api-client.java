import com.coveo.pushapiclient.PushSource;

import java.io.IOException;
import java.net.http.HttpResponse;

public class DeleteOneDocument {
    public static void main(String[] args) {
        URL sourceUrl = new URL("https://api.cloud.coveo.com/push/v1/organizations/org_id/sources/source_id");
        PushSource source = new PushSource("my_api_key", sourceUrl);
        String documentId = "https://my.document.uri";
        Boolean deleteChildren = true;

        try {
            HttpResponse<String> response = source.deleteDocument(documentId, deleteChildren);
            System.out.println(String.format("Delete document status: %s", response.statusCode()));
            System.out.println(String.format("Delete document response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
