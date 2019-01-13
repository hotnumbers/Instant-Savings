package com.example.tianxingwang.instasavings.client.data;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QueryByExtractorRequest {
    
    /**
     * A unique id for this request
     */
    final UUID requestId = UUID.randomUUID();
    
    /**
     * The extractor you wish to query
     */
    UUID extractorId;
    
    /**
     * If a URL-only extractor
     */
    String url;
    
    /**
     * If interaction extractor
     */
    Map<String, Object> input;


    public UUID getRequestId(){

        return requestId;
    }

    public QueryByExtractorRequest( UUID extractorId, String url){



        /**
         * The extractor you wish to query
         */
        this. extractorId= extractorId;

        /**
         * If a URL-only extractor
         */
        this.url=url;

        /**
         * If interaction extractor
         */
        this.input =  input;

    }


}