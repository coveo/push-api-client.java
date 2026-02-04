package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CatalogStreamUploadHandlerTest {
  @Mock private StreamEnabledSource mockSource;
  @Mock private PlatformClient mockPlatformClient;
  @Mock private HttpResponse<String> mockContainerResponse;
  @Mock private HttpResponse<String> mockPushResponse;
  @Mock private StreamUpdate mockStreamUpdate;

  private CatalogStreamUploadHandler handler;
  private AutoCloseable closeable;

  @Before
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    handler = new CatalogStreamUploadHandler(mockSource, mockPlatformClient);
    when(mockSource.getId()).thenReturn("test-source-id");
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void uploadAndPushShouldExecute3StepWorkflowInOrder()
      throws IOException, InterruptedException {
    when(mockContainerResponse.body()).thenReturn("{\"fileId\":\"test-container-id\"}");
    when(mockPlatformClient.createFileContainer()).thenReturn(mockContainerResponse);
    StreamUpdateRecord mockRecord =
        new StreamUpdateRecord(new JsonObject[] {}, new JsonObject[] {}, new JsonObject[] {});
    when(mockStreamUpdate.marshal()).thenReturn(mockRecord);
    when(mockPlatformClient.pushFileContainerContentToStreamSource(
            anyString(), any(FileContainer.class)))
        .thenReturn(mockPushResponse);

    HttpResponse<String> result = handler.uploadAndPush(mockStreamUpdate);

    InOrder inOrder = inOrder(mockPlatformClient);
    inOrder.verify(mockPlatformClient).createFileContainer();
    inOrder
        .verify(mockPlatformClient)
        .uploadContentToFileContainer(any(FileContainer.class), anyString());
    inOrder
        .verify(mockPlatformClient)
        .pushFileContainerContentToStreamSource(eq("test-source-id"), any(FileContainer.class));
    assertEquals(mockPushResponse, result);
  }

  @Test
  public void uploadAndPushShouldReturnPushResponse() throws IOException, InterruptedException {
    when(mockContainerResponse.body()).thenReturn("{\"fileId\":\"test-id\"}");
    when(mockPlatformClient.createFileContainer()).thenReturn(mockContainerResponse);
    StreamUpdateRecord mockRecord =
        new StreamUpdateRecord(new JsonObject[] {}, new JsonObject[] {}, new JsonObject[] {});
    when(mockStreamUpdate.marshal()).thenReturn(mockRecord);
    when(mockPlatformClient.pushFileContainerContentToStreamSource(
            anyString(), any(FileContainer.class)))
        .thenReturn(mockPushResponse);

    HttpResponse<String> result = handler.uploadAndPush(mockStreamUpdate);

    assertSame(mockPushResponse, result);
  }

  @Test(expected = IOException.class)
  public void uploadAndPushShouldPropagateIOExceptionFromCreateFileContainer()
      throws IOException, InterruptedException {
    when(mockPlatformClient.createFileContainer())
        .thenThrow(new IOException("Container creation failed"));

    handler.uploadAndPush(mockStreamUpdate);
  }

  @Test(expected = IOException.class)
  public void uploadAndPushShouldPropagateIOExceptionFromUploadContent()
      throws IOException, InterruptedException {
    when(mockContainerResponse.body()).thenReturn("{\"fileId\":\"test-id\"}");
    when(mockPlatformClient.createFileContainer()).thenReturn(mockContainerResponse);
    StreamUpdateRecord mockRecord =
        new StreamUpdateRecord(new JsonObject[] {}, new JsonObject[] {}, new JsonObject[] {});
    when(mockStreamUpdate.marshal()).thenReturn(mockRecord);
    when(mockPlatformClient.uploadContentToFileContainer(any(FileContainer.class), anyString()))
        .thenThrow(new IOException("Upload failed"));

    handler.uploadAndPush(mockStreamUpdate);
  }

  @Test(expected = IOException.class)
  public void uploadAndPushShouldPropagateIOExceptionFromPush()
      throws IOException, InterruptedException {
    when(mockContainerResponse.body()).thenReturn("{\"fileId\":\"test-id\"}");
    when(mockPlatformClient.createFileContainer()).thenReturn(mockContainerResponse);
    StreamUpdateRecord mockRecord =
        new StreamUpdateRecord(new JsonObject[] {}, new JsonObject[] {}, new JsonObject[] {});
    when(mockStreamUpdate.marshal()).thenReturn(mockRecord);
    when(mockPlatformClient.pushFileContainerContentToStreamSource(
            anyString(), any(FileContainer.class)))
        .thenThrow(new IOException("Push failed"));

    handler.uploadAndPush(mockStreamUpdate);
  }
}
