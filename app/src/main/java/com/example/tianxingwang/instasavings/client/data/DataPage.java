package com.example.tianxingwang.instasavings.client.data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DataPage {
    
    /**
     * Represents a group of data on a page
     * i.e. an independent list
     * 
     * Often these groups are just merged into a 
     * single table.
     */
    @Value
    public static class DataGroup {
        
        /**
         * The list of "rows" - each "cell" can be multivalued
         * Example:
         * 
         * {
         *  "Colour": [{
         *      "text": "Brown"
         *  }],
         *  "Large Columns": [{
         *      "src": "http://owlkingdom.com/img/start/Tall.png"
         *  }],
         *  "Price": [{
         *      "text": "$599"
         *  }],
         *  "Product Item": [{
         *      "text": "Something happened when he was a kid."
         *  }],
         *  "Product Title": [{
         *      "href": "http://owlkingdom.com/tall.html",
         *      "text": "Tall"
         *  }],
         *  "Size": [{
         *      "text": "Large"
         *  }]
         * }
         * 
         */
        List<Map<String, List<Map<String, Object>>>> group;
    }
    
    @Value
    public static class PageData {
        
        /**
         * Status code of outer window
         */
        int statusCode;
        
        /**
         * When the page was rendered
         */
        Instant timestamp;
    }
    
    List<DataGroup> data;
    
    /**
     * Page information
     */
    PageData pageData;
    
    /**
     * The configuration used to do the extraction 
     */
    UUID runtimeConfigId;
    
    /**
     * An internal error identifier
     */
    String errorCode;
    
    /**
     * If there was an issue, a human readable error message
     */
    String errorMessage;

    /**
     * A temporary URL to an image of the screen at point of extraction
     */
    String screenCapture; 
    
    /**
     * If one of multiple pages of data, which page (one-indexed)
     */
    int pageNumber;

}
