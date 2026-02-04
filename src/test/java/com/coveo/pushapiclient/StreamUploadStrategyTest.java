package com.coveo.pushapiclient;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StreamUploadStrategyTest {

  @Mock private StreamEnabledSource source;
  @Mock private PlatformClient platformClient;
  @Mock private HttpResponse<String> createResponse;
  @Mock private HttpResponse<String> pushResponse;
  @Mock private StreamUpdate streamUpdate;

  private StreamUploadStrategy strategy;
  private AutoCloseable closeable;

  @Before
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    strategy = new StreamUploadStrategy(source, platformClient);
  }

  @After
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void testApply_ExecutesFullWorkflow() throws IOException, InterruptedException {
    // Setup
    String fileContainerId = "test-container-123";
    String fileContainerJson = "{\"fileId\":\"" + fileContainerId + "\"}";
    when(createResponse.body()).thenReturn(fileContainerJson);
    when(platformClient.createFileContainer()).thenReturn(createResponse);
    when(streamUpdate.marshal()).thenReturn(new StreamUpdateRecord(null, null, null));
    when(source.getId()).thenReturn("source-123");
    when(platformClient.pushFileContainerContentToStreamSource(anyString(), any()))
        .thenReturn(pushResponse);

    // Execute
    HttpResponse<String> result = strategy.apply(streamUpdate);

    // Verify
    verify(platformClient).createFileContainer();
    verify(platformClient).uploadContentToFileContainer(any(FileContainer.class), anyString());
    verify(platformClient).pushFileContainerContentToStreamSource(eq("source-123"), any());
    assertEquals(pushResponse, result);
  }

  @Test
  public void testApply_VerifiesCorrectOperationOrder() throws IOException, InterruptedException {
    // Setup
    when(createResponse.body()).thenReturn("{\"fileId\":\"container-1\"}");
    when(platformClient.createFileContainer()).thenReturn(createResponse);
    when(streamUpdate.marshal()).thenReturn(new StreamUpdateRecord(null, null, null));
    when(source.getId()).thenReturn("source-id");
    when(platformClient.pushFileContainerContentToStreamSource(anyString(), any()))
        .thenReturn(pushResponse);

    // Execute
    strategy.apply(streamUpdate);

    // Verify order
    InOrder inOrder = inOrder(platformClient);
    inOrder.verify(platformClient).createFileContainer();
    inOrder.verify(platformClient).uploadContentToFileContainer(any(FileContainer.class), anyString());
    inOrder
        .verify(platformClient)
        .pushFileContainerContentToStreamSource(eq("source-id"), any(FileContainer.class));
  }

  @Test
  public void testApply_UsesCorrectSourceId() throws IOException, InterruptedException {
    // Setup
    String expectedSourceId = "my-catalog-source";
    when(createResponse.body()).thenReturn("{\"fileId\":\"container-1\"}");
    when(platformClient.createFileContainer()).thenReturn(createResponse);
    when(streamUpdate.marshal()).thenReturn(new StreamUpdateRecord(null, null, null));
    when(source.getId()).thenReturn(expectedSourceId);
    when(platformClient.pushFileContainerContentToStreamSource(anyString(), any()))
        .thenReturn(pushResponse);

    // Execute
    strategy.apply(streamUpdate);

    // Verify
    verify(platformClient)
        .pushFileContainerContentToStreamSource(eq(expectedSourceId), any(FileContainer.class));
  }

  @Test
  public void testApply_MarshallesStreamUpdateCorrectly() throws IOException, InterruptedException {
    // Setup
    StreamUpdateRecord expectedRecord = new StreamUpdateRecord(null, null, null);
    when(createResponse.body()).thenReturn("{\"fileId\":\"container-1\"}");
    when(platformClient.createFileContainer()).thenReturn(createResponse);
    when(streamUpdate.marshal()).thenReturn(expectedRecord);
    when(source.getId()).thenReturn("source-id");
    when(platformClient.pushFileContainerContentToStreamSource(anyString(), any()))
        .thenReturn(pushResponse);

    // Execute
    strategy.apply(streamUpdate);

    // Verify streamUpdate.marshal() was called
    verify(streamUpdate).marshal();
  }

  @Test
  public void testApply_ReturnsCorrectHttpResponse() throws IOException, InterruptedException {
    // Setup
    HttpResponse<String> expectedResponse = pushResponse;
    when(createResponse.body()).thenReturn("{\"fileId\":\"container-1\"}");
    when(platformClient.createFileContainer()).thenReturn(createResponse);
    when(streamUpdate.marshal()).thenReturn(new StreamUpdateRecord(null, null, null));
    when(source.getId()).thenReturn("source-id");
    when(platformClient.pushFileContainerContentToStreamSource(anyString(), any()))
        .thenReturn(expectedResponse);

    // Execute
    HttpResponse<String> result = strategy.apply(streamUpdate);

    // Verify
    assertSame(expectedResponse, result);
  }
}
