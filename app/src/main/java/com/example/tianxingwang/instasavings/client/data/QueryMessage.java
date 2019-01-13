package com.example.tianxingwang.instasavings.client.data;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QueryMessage {
    
    public static enum MessageType {
        
        /**
         * Returning a page of data from the query.
         * May be the final message for a request.
         */
        DATA,
        
        /**
         * Indicates there is no more data.
         * Always the final message for a request.
         */
        END,
        
        /**
         * Indicates there is insufficient credit
         * on your account
         */
        INSUFFICIENT_CREDIT,
    }
    
    /**
     * The requestId from the linked request
     */
    UUID requestId;
    
    /**
     * The type of message back
     */
    MessageType messageType;
    
    /**
     * True if the message is the final message
     * for the {@link #requestId}
     */
    boolean finalMessage;
    
    /**
     * A page of data 
     */
    DataPage data;


    public UUID getRequestId(){
     return requestId;
    }

    public boolean isFinalMessage(){
        return isFinalMessage();
    }

    public MessageType getMessageType(){
        return messageType;
    }


    public DataPage getData(){
        return data;
    }







}