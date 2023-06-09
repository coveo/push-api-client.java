package com.coveo.pushapiclient;

import java.util.HashMap;

public class Document {
  /**
   * The metadata key-value pairs for a given document.
   *
   * <p>Each metadata in the document must be unique.
   *
   * <p>Metadata are case-insensitive (e.g., the Push API considers mykey, MyKey, myKey, MYKEY, etc.
   * as identical).
   *
   * <p>See https://docs.coveo.com/en/115 for more information.
   */
  public final HashMap<String, Object> metadata;

  /**
   * The list of permission sets for this item.
   *
   * <p>This is useful when item based security is required (i.e., when security isn't configured at
   * the source level).
   *
   * <p>See https://docs.coveo.com/en/107 for more information.
   */
  public DocumentPermissions[] permissions;

  /**
   * The Uniform Resource Identifier (URI) that uniquely identifies the document in a Coveo index.
   *
   * <p>Examples: - `http://www.example.com/` - `file://folder/text.txt`
   */
  public String uri;

  /** The documentId of the document. */
  public String documentId;

  /** The title of the document. */
  public String title;

  /** The clickable URI associated with the document. */
  public String clickableUri;

  /** The author of the document. */
  public String author;

  /**
   * The date of the document, represented as an ISO string.
   *
   * <p>Optional, will default to indexation date.
   */
  public String date;

  /**
   * The modified date of the document, represented as an ISO string.
   *
   * <p>Optional, will default to indexation date.
   */
  public String modifiedDate;

  /**
   * The permanent identifier of a document that does not change over time.
   *
   * <p>Optional, will be derived from the document URI.
   */
  public String permanentId;

  /**
   * The unique identifier (URI) of the parent item.
   *
   * <p>Specifying a value for this key creates a relationship between the attachment item (child)
   * and its parent item.
   *
   * <p>This value also ensures that a parent and all of its attachments will be routed in the same
   * index slice.
   */
  public String parentId;

  /**
   * The textual (non-binary) content of the item.
   *
   * <p>Whenever you're pushing a compressed binary item (such as XML/HTML, PDF, Word, or binary),
   * you should use the CompressedBinaryData or CompressedBinaryDataFileId attribute instead,
   * depending on the content size.
   *
   * <p>Accepts 5 MB or less of uncompressed textual data.
   *
   * <p>See https://docs.coveo.com/en/73 for more information.
   *
   * <p>Example: `This is a simple string that will be used for searchability as well as to generate
   * excerpt and summaries for the document.`
   */
  public String data;

  /**
   * The original binary item content, compressed using one of the supported compression types
   * (Deflate, GZip, LZMA, Uncompressed, or ZLib), and then Base64 encoded.
   *
   * <p>You can use this parameter when you're pushing a compressed binary item (such as XML/HTML,
   * PDF, Word, or binary) whose size is less than 5 MB.
   *
   * <p>Whenever you're pushing an item whose size is 5 MB or more, use the
   * CompressedBinaryDataFileId property instead.
   *
   * <p>If you're pushing less than 5 MB of textual (non-binary) content, you can use the data
   * property instead.
   *
   * <p>See https://docs.coveo.com/en/73 for more information.
   */
  public CompressedBinaryData compressedBinaryData;

  /**
   * The fileId from the content that has been uploaded to the S3 via a FileContainer. The file is
   * compressed using one of the supported compression types (Deflate, GZip, LZMA, Uncompressed, or
   * ZLib).
   *
   * <p>You can use this parameter when you're pushing a compressed binary item (such as XML/HTML,
   * PDF, Word, or binary) whose size is greater than 5 MB.
   *
   * <p>Whenever you're pushing an item whose size is less than 5 MB, use the CompressedBinaryData
   * property instead.
   *
   * <p>If you're pushing less than 5 MB of textual (non-binary) content, you can use the data
   * property instead.
   *
   * <p>See https://docs.coveo.com/en/73 for more information.
   */
  public String compressedBinaryDataFileId;

  /**
   * The file extension of the data you're pushing.
   *
   * <p>This is useful when pushing a compressed item. The converter uses this information to
   * identify how to correctly process the item.
   *
   * <p>Values must include the preceding . character.
   *
   * <p>Example: `.html`
   */
  public String fileExtension;

  public Document() {
    this.permissions = new DocumentPermissions[] {new DocumentPermissions()};
    this.metadata = new HashMap<String, Object>();
  }
}
