package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class StreamService {
    private StreamEnabledSource source;
    private String streamId;
    private DocumentUploadQueue queue;

    public StreamService(StreamEnabledSource source) {
        UpdloadStrategy uploader = this.getUploadStrategy();
        this.source = source;
        this.queue = new DocumentUploadQueue(uploader);
    }

    /**
     * Pushes document to the source.
     * If multiple documents are added, the class will ensure documents are
     * automatically batched into chunks that do not exceed API limit size.
     *
     * @param document
     * @throws InterruptedException
     * @throws IOException
     */
    public void add(DocumentBuilder document) throws IOException, InterruptedException {
        if (this.streamId == null) {
            this.streamId = this.getStreamId();
        }
        queue.add(document);
    }

    public HttpResponse<String> close() throws IOException, InterruptedException {
        if (this.streamId == null) {
            throw new java.lang.UnsupportedOperationException("TODO: custom error: No stream was open yet");
        }
        queue.flush();
        PlatformClient platformClient = source.getPlatformClient();
        return platformClient.closeStream(this.streamId);
    }

    private UpdloadStrategy getUploadStrategy() {
        return (batchUpdate) -> {
            PlatformClient platformClient = source.getPlatformClient();
            HttpResponse<String> resFileContainer = platformClient.requireStreamChunk();
            FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
            String batchUpdateJson = new Gson().toJson(batchUpdate.marshal());
            return platformClient.uploadContentToFileContainer(fileContainer,
                    batchUpdateJson);

        };
    }

    private String getStreamId() throws IOException, InterruptedException {
        HttpResponse<String> response = this.source.getPlatformClient().openStream();
        return "TODO: get streamID from response";
    }

}
