package com.coveo.pushapiclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.coveo.pushapiclient.exceptions.NoOpenStreamException;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StreamService.class)
public class StreamServiceTest {
    StreamService service;

    DocumentUploadQueue queueMock;
    PlatformClient platformClientMock;
    HttpResponse<String> httpResponseMock;

    private DocumentBuilder documentA;
    private DocumentBuilder documentB;

    @Before
    public void setUp() throws Exception {
        this.queueMock = PowerMockito.mock(DocumentUploadQueue.class);
        this.platformClientMock = PowerMockito.mock(PlatformClient.class);
        this.httpResponseMock = PowerMockito.mock(HttpResponse.class);

        PowerMockito
                .whenNew(PlatformClient.class)
                .withAnyArguments()
                .thenReturn(this.platformClientMock);
        PowerMockito
                .whenNew(DocumentUploadQueue.class)
                .withAnyArguments()
                .thenReturn(this.queueMock);

        PowerMockito
                .when(this.httpResponseMock.body())
                .thenReturn("{\"streamId\": \"stream-id\"}");
        PowerMockito
                .when(this.platformClientMock.openStream("my-source-id"))
                .thenReturn(this.httpResponseMock);

        documentA = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
        documentB = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");

        URL sourceUrl = new URL(
                "https://api.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/stream/open");
        CatalogSource source = new CatalogSource("api_key", sourceUrl);
        service = new StreamService(source);
    }

    @Test
    public void testAddShouldOpenANewStream() throws IOException, InterruptedException {
        service.add(documentA);
        service.add(documentB);

        verify(this.platformClientMock, times(1)).openStream("my-source-id");
    }

    @Test
    public void testAddShouldAddDocumentToQueue() throws IOException, InterruptedException {
        service.add(documentA);
        service.add(documentB);

        verify(this.queueMock, times(1)).add(documentA);
        verify(this.queueMock, times(1)).add(documentB);
    }

    @Test
    public void testCloseShouldCloseOpenStream() throws IOException, InterruptedException, NoOpenStreamException {
        service.add(documentA);
        service.close();

        verify(this.platformClientMock, times(1)).closeStream("my-source-id",
                "stream-id");
    }

    @Test
    public void testCloseShouldFlushBufferedDocuments()
            throws IOException, InterruptedException, NoOpenStreamException {
        service.add(documentA);
        service.close();

        verify(this.queueMock, times(1)).flush();
    }

    @Test(expected = NoOpenStreamException.class)
    public void givenNoOpenStream_whenClose_thenShouldThrow()
            throws IOException, InterruptedException, NoOpenStreamException {
        service.close();
    }

}