package com.coveo.pushapiclient;


import com.google.gson.Gson;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;

class StringSubscriber implements Flow.Subscriber<ByteBuffer> {

    HttpResponse.BodySubscriber<String> wrapped;

    public StringSubscriber(HttpResponse.BodySubscriber<String> wrapped) {
        this.wrapped = wrapped;
    }

    public static Map toMap(Optional<HttpRequest.BodyPublisher> bodyPublisher) {
        return bodyPublisher.map(p -> {
            var bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
            var flowSubscriber = new StringSubscriber(bodySubscriber);
            p.subscribe(flowSubscriber);
            String requestBodyAsString = bodySubscriber.getBody().toCompletableFuture().join();
            return new Gson().fromJson(requestBodyAsString, Map.class);
        }).get();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        wrapped.onSubscribe(subscription);
    }

    @Override
    public void onNext(ByteBuffer item) {
        wrapped.onNext(List.of(item));
    }

    @Override
    public void onError(Throwable throwable) {
        wrapped.onError(throwable);
    }

    @Override
    public void onComplete() {
        wrapped.onComplete();
    }
}

