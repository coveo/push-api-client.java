package com.coveo.pushapiclient;

/**
 * Marker interface for content that can be uploaded via an UploadStrategy.
 * Implemented by BatchUpdate and StreamUpdate.
 */
public interface UploadContent {
  /**
   * Serializes this content to a record suitable for JSON marshalling.
   */
  Object marshal();
}
