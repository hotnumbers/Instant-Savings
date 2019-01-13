package com.example.tianxingwang.instasavings.client;

import com.google.common.util.concurrent.Service;

import java.util.function.Consumer;

import importio.client.data.QueryByExtractorRequest;
import importio.client.data.QueryMessage;

/**
 * The central import.io client interface.
 */
public interface ImportIoClient extends Service {

    /**
     * Request a real-time extraction of data.
     * This may create multiple pages of data.
     * Each page will be returned in a {@link QueryMessage}.
     * These pages are linked back the the request by the
     * {@link QueryByExtractorRequest#requestId}
     * 
     * @param input
     */
    void queryByExtractor(QueryByExtractorRequest input);

    /**
     * Add a listener for the messages passed back from import.io
     * 
     * If these are long running operations you should carry them out on another thread.
     * 
     * @param consumer
     */
    void addMessageListener(Consumer<QueryMessage> consumer);
    
    /**
     * Remove a listener for the messages passed back from import.io
     * 
     * @param consumer
     */
    void removeMessageListener(Consumer<QueryMessage> consumer);
    
    /**
     * Set the API key that is used to access import.io
     * 
     * @param apiKey
     */
    void setApiKey(String apiKey);
    
}