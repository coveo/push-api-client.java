package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import com.google.gson.Gson;

// TODO: LENS-851 - Make public
class StreamService {
    private final StreamEnabledSource source;
    private final PlatformClient platformClient;
    private StreamServiceInternal service;
    private String streamId;
    private DocumentUploadQueue queue;

    /**
     * Creates a service to stream your documents to the provided source by
     * interacting with the Stream API.
     *
     * <p>
     * To perform <a href="https://docs.coveo.com/en/l62e0540">full document
     * updates</a>, use the {@PushService}, since pushing documents with the
     * {@StreamService} is equivalent to triggering a full source rebuild. The
     * {@StreamService} can also be used for an initial catalog upload.
     *
     * @param source The source to which you want to send your documents.
     */
    public StreamService(StreamEnabledSource source) {
        String apiKey = source.getApiKey();
        String organizationId = source.getOrganizationId();
        PlatformUrl platformUrl = source.getPlatformUrl();
        UpdloadStrategy uploader = this.getUploadStrategy();

        this.source = source;
        this.queue = new DocumentUploadQueue(uploader);
        this.platformClient = new PlatformClient(apiKey, organizationId, platformUrl);
        this.service = new StreamServiceInternal(this.source, this.queue, this.platformClient);
    }

    /**
     * Adds documents to the previously specified source.
     * This function will open a stream before uploading documents into it.
     *
     * <p>
     * If called several times, the service will automatically batch documents and
     * create new stream chunks whenever the data payload exceeds the
     * <a href="https://docs.coveo.com/en/lb4a0344#stream-api-limits">batch size limit</a>
     * set for the Stream API.
     *
     * <p>
     * Once there are no more documents to add, it is important to call the {@link StreamService#close} function
     * in order to send any buffered documents and close the open stream.
     * Otherwise, changes will not be reflected in the index.
     *
     * <p>
     * <pre>
     * {@code
     * //...
     * StreamService service = new StreamService(source));
     * for (DocumentBuilder document : fictionalDocumentList) {
     *     service.add(document);
     * }
     * service.close(document);
     * </pre>
     *
     * <p>
     * For more code samples, visit <a href="TODO: LENS-840">Stream data to your catalog source</a>
     *
     * @param document The documentBuilder to add to your source
     * @throws InterruptedException
     * @throws IOException
     */
    public void add(DocumentBuilder document) throws IOException, InterruptedException {
        this.service.add(document);
    }

    /**
     * Sends any buffered documents and <a href="https://docs.coveo.com/en/lb4a0344#step-3-close-the-stream">closes the stream</a>.
     *
     * <p>
     * Upon invoking this method, any indexed items not added through this {@link StreamService} instance will be removed.
     * All documents added from the initialization of the service until the invocation of the {@link StreamService#close} function
     * will completely replace the previous content of the source.
     *
     * <p>
     * When you upload a catalog into a source, it will replace the previous content
     * of the source completely. Expect a 15-minute delay for the removal of the old
     * items from the index.
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws NoOpenStreamException
     */
    public HttpResponse<String> close() throws IOException, InterruptedException, NoOpenStreamException {
        return this.service.close();
    }

    private UpdloadStrategy getUploadStrategy() {
        return (batchUpdate) -> {
            String sourceId = this.getSourceId();
            HttpResponse<String> resFileContainer = this.platformClient.requireStreamChunk(sourceId, this.streamId);
            FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
            String batchUpdateJson = new Gson().toJson(batchUpdate.marshal());
            return this.platformClient.uploadContentToFileContainer(fileContainer,
                    batchUpdateJson);

        };
    }

    private String getSourceId() {
        return this.source.getId();
    }

}
