package com.coveo.pushapiclient;

import java.util.Objects;

/**
 * The original binary item content, compressed using one of the supported compression types
 * (Deflate, GZip, LZMA, Uncompressed, or ZLib), and then Base64 encoded.
 *
 * <p>You can use this parameter when you're pushing a compressed binary item (such as XML/HTML,
 * PDF, Word, or binary) whose size is less than 5 MB.
 *
 * <p>Whenever you're pushing an item whose size is 5 MB or more, use the CompressedBinaryDataFileId
 * property instead.
 *
 * <p>If you're pushing less than 5 MB of textual (non-binary) content, you can use the data
 * property instead.
 *
 * <p>See https://docs.coveo.com/en/73 for more information.
 */
public class CompressedBinaryData {

  private final String data;
  private final CompressionType compressionType;

  /**
   * @param data The base64 encoded binary data. Example: `eJxzrUjMLchJBQAK4ALN`
   * @param compressionType The compression type that was applied to your document.
   */
  public CompressedBinaryData(String data, CompressionType compressionType) {
    this.data = data;
    this.compressionType = compressionType;
  }

  public String getData() {
    return data;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  @Override
  public String toString() {
    return "CompressedBinaryData["
        + "data='"
        + data
        + '\''
        + ", compressionType="
        + compressionType
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    CompressedBinaryData that = (CompressedBinaryData) obj;
    return Objects.equals(data, that.data) && compressionType == that.compressionType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, compressionType);
  }
}
