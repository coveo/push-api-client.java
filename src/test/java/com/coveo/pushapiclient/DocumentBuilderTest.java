package com.coveo.pushapiclient;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DocumentBuilderTest {

    private DocumentBuilder docBuilder;

    @Before
    public void setUp() {
        docBuilder = new DocumentBuilder("the_uri", "the_title");
    }

    @Test
    public void testWithData() {
        docBuilder.withData("this is searchable");
        assertEquals(
                "withData should marshal correctly",
                "this is searchable",
                docBuilder.marshalJsonObject().get("data").getAsString()
        );
    }

    @Test
    public void testWithDatePlainDateObject() {
        Date d = new Date();
        DateTime dt = new DateTime(d);
        docBuilder.withDate(d);
        assertEquals(
                "withDate with a plain java date object should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("date").getAsString()
        );
    }

    @Test
    public void testWithDateLong() {
        docBuilder.withDate(12345l);
        DateTime dt = new DateTime(12345l);
        assertEquals(
                "withDate with a long should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("date").getAsString()
        );
    }

    @Test
    public void testWithDateJodatime() {
        DateTime dt = new DateTime();
        docBuilder.withDate(dt);
        assertEquals(
                "withDate with jodatime should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("date").getAsString()
        );
    }

    @Test
    public void testWithDateString() {
        DateTime dt = new DateTime("2015-01-01");
        docBuilder.withDate("2015-01-01");
        assertEquals(
                "withDate with string date should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("date").getAsString()
        );
    }

    @Test
    public void withModifiedDatePlainDateObject() {
        Date d = new Date();
        DateTime dt = new DateTime(d);
        docBuilder.withModifiedDate(d);
        assertEquals(
                "withModifiedDate with a plain java date object should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("modifiedDate").getAsString()
        );
    }

    @Test
    public void testWithModifiedDateLong() {
        docBuilder.withModifiedDate(12345l);
        DateTime dt = new DateTime(12345l);
        assertEquals(
                "withDate with a long should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("modifiedDate").getAsString()
        );
    }

    @Test
    public void testWithModifiedDateJodatime() {
        DateTime dt = new DateTime();
        docBuilder.withModifiedDate(dt);
        assertEquals(
                "withDate with jodatime should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("modifiedDate").getAsString()
        );
    }

    public void testWithModifiedDateString() {
        DateTime dt = new DateTime("2015-01-01");
        docBuilder.withModifiedDate("2015-01-01");
        assertEquals(
                "withDate with string date should marshal correctly",
                dt.toString(ISODateTimeFormat.dateTime()),
                docBuilder.marshalJsonObject().get("modifiedDate").getAsString()
        );
    }

    @Test
    public void testWithPermanentId() {
        docBuilder.withPermanentId("the_permanent_id");
        assertEquals(
                "with permanentId should marshal correctly",
                "the_permanent_id",
                docBuilder.marshalJsonObject().get("permanentId").getAsString()
        );
    }

    @Test
    public void testWithPermanentIdGeneration() {
        docBuilder = new DocumentBuilder("https://foo.com", "bar");
        assertEquals(
                "permanentId should be generated automatically if not set",
                "aa2e0510b66edff7f05e2b30d4f1b3a4b5481c06b69f41751c54675c5afb",
                docBuilder.marshalJsonObject().get("permanentId").getAsString()
        );
    }

    @Test
    public void testWithCompressedBinaryData() {
        String encoded = Base64.getEncoder().encodeToString("binary data encoded".getBytes(StandardCharsets.UTF_8));
        docBuilder.withCompressedBinaryData(new CompressedBinaryData(encoded, CompressionType.UNCOMPRESSED));
        String decoded = new String(Base64.getDecoder().decode(docBuilder.marshalJsonObject().get("compressedBinaryData").getAsString().getBytes(StandardCharsets.UTF_8)));

        assertEquals("withCompressedBinaryData should marshal correctly", "binary data encoded", decoded);
    }

    @Test
    public void testWithFileExtension() {
        docBuilder.withFileExtension(".txt");

        assertEquals(
                "withFileExtension should marshal correctly",
                ".txt",
                docBuilder.marshalJsonObject().get("fileExtension").getAsString()
        );
    }

    @Test(expected = RuntimeException.class)
    public void testWithInvalidFileExtension() {
        docBuilder.withFileExtension("this should blow up");
    }

    @Test
    public void testWithParentID() {
        docBuilder.withParentID("the_parent_id");

        assertEquals(
                "withParentId should marshal correctly",
                "the_parent_id",
                docBuilder.marshalJsonObject().get("parentId").getAsString()
        );
    }

    @Test
    public void testWithClickableUri() {
        docBuilder.withClickableUri("the click uri");

        assertEquals("withClickableUri should marshal correctly",
                "the click uri",
                docBuilder.marshalJsonObject().get("clickableUri").getAsString()
        );
    }

    @Test
    public void testWithAuthor() {
        docBuilder.withAuthor("the author");

        assertEquals("withAuthor should marshal correctly",
                "the author",
                docBuilder.marshalJsonObject().get("author").getAsString()
        );
    }

    @Test
    public void testWithMetadataValue() {
        docBuilder.withMetadataValue("the_key", "the_value");
        assertEquals(
                "withMetadataValue should marshal correctly",
                "the_value",
                docBuilder.marshalJsonObject().get("the_key").getAsString()
        );
        assertNull(
                "withMetadataValue should remove metadata key when marshaling",
                docBuilder.marshalJsonObject().get("metadata")
        );
    }

    @Test(expected = RuntimeException.class)
    public void testWithInvalidMetadataValue() {
        docBuilder.withMetadataValue("data", "this should blow up");
    }

    @Test
    public void testWithMetadata() {
        docBuilder.withMetadata(new HashMap<>() {{
            put("my_field_1", "1");
            put("my_field_2", false);
            put("my_field_3", 1234);
            put("my_field_4", new String[]{"a", "b", "c"});
        }});
        assertEquals(
                "1",
                docBuilder.marshalJsonObject().get("my_field_1").getAsString()
        );
        assertEquals(
                false,
                docBuilder.marshalJsonObject().get("my_field_2").getAsBoolean()
        );
        assertEquals(
                1234,
                docBuilder.marshalJsonObject().get("my_field_3").getAsInt()
        );
        assertEquals(
                "a",
                docBuilder.marshalJsonObject().get("my_field_4").getAsJsonArray().get(0).getAsString()
        );
        assertEquals(
                "b",
                docBuilder.marshalJsonObject().get("my_field_4").getAsJsonArray().get(1).getAsString()
        );
        assertEquals(
                "c",
                docBuilder.marshalJsonObject().get("my_field_4").getAsJsonArray().get(2).getAsString()
        );

    }

    @Test
    public void testWithSingleAllowedPermissions() {
        docBuilder.withAllowedPermissions(new UserSecurityIdentityBuilder("bob@anonymous.com"));

        assertEquals(
                "bob@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("allowedPermissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
    }

    @Test
    public void testWithMultipleAllowedPermissions() {
        docBuilder.withAllowedPermissions(new UserSecurityIdentityBuilder(new String[]{"bob@anonymous.com", "john@anonymous.com"}));

        assertEquals(
                "bob@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("allowedPermissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
        assertEquals(
                "john@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("allowedPermissions")
                        .getAsJsonArray()
                        .get(1)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
    }

    @Test
    public void testWithSingleDeniedPermissions() {
        docBuilder.withDeniedPermissions(new UserSecurityIdentityBuilder("bob@anonymous.com"));

        assertEquals(
                "bob@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("deniedPermissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
    }

    @Test
    public void testWithMultipleDeniedPermissions() {
        docBuilder.withDeniedPermissions(new UserSecurityIdentityBuilder(new String[]{"bob@anonymous.com", "john@anonymous.com"}));

        assertEquals(
                "bob@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("deniedPermissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
        assertEquals(
                "john@anonymous.com",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("deniedPermissions")
                        .getAsJsonArray()
                        .get(1)
                        .getAsJsonObject()
                        .get("identity").getAsString()
        );
    }

    @Test
    public void withAllowAnonymousUsers() {
        docBuilder.withAllowAnonymousUsers(false);
        assertFalse(
                "withAllowAnonymousUser should marshal correctly",
                docBuilder.marshalJsonObject()
                        .get("permissions")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("allowAnonymous")
                        .getAsBoolean());
    }

    @Test
    public void marshal() {
        String marshaled = docBuilder.marshal();
        assertTrue(
                "marshal should return a valid JSON string",
                docBuilder.marshal().contains("the_title")
        );
    }

}
