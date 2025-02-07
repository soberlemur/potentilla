package com.soberlemur.potentilla.parse;

import com.soberlemur.potentilla.Message;
import com.soberlemur.potentilla.catalog.parse.MessageStreamParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMessageStreamParser {

    File poFile;

    @BeforeEach
    public void setup() throws URISyntaxException {
        poFile = new File(getClass().getResource("/valid/sample.po").toURI());
    }

    @Test
    public void testIteratingThroughABasicFile() throws Throwable {
        Message message;
        MessageStreamParser parser = new MessageStreamParser(poFile);

        assertTrue(parser.hasNext());
        message = parser.next();
        assertEquals("", message.getMsgId());
        assertTrue(message.isFuzzy());

        assertTrue(parser.hasNext());
        message = parser.next();
        assertNotEquals("", message.getMsgId());
        assertFalse(message.isFuzzy());

        parser.next();
        parser.next();

        message = parser.next();
        assertTrue(message.isObsolete());

        parser.next();
        parser.next(); // last message

        assertFalse(parser.hasNext());
    }

    String msgString = "msgid \"hello world!\"\n" +
            "msgstr \"hei verden!\"\n";

    @Test
    public void testParseFromReader() throws Throwable {

        MessageStreamParser parser = new MessageStreamParser(
                new StringReader(msgString));

        Message message = parser.next();

        assertEquals(message.getMsgId(), "hello world!");
        assertEquals(message.getMsgstr(), "hei verden!");

    }

    @Test
    public void testParseFromInputStream() throws Throwable {
        MessageStreamParser parser = new MessageStreamParser(
                new ByteArrayInputStream(msgString.getBytes("UTF-8")));

        Message message = parser.next();

        assertEquals(message.getMsgId(), "hello world!");
        assertEquals(message.getMsgstr(), "hei verden!");
    }

    @Test
    public void testParseFromInputStreamWithUtf16Charset() throws Throwable {
        MessageStreamParser parser =
                new MessageStreamParser(
                        new ByteArrayInputStream(msgString.getBytes("UTF-16")),
                        Charset.forName("UTF-16"));

        Message message = parser.next();

        assertEquals(message.getMsgId(), "hello world!");
        assertEquals(message.getMsgstr(), "hei verden!");
    }

}
