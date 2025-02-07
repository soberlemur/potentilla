package com.soberlemur.potentilla;
/*
 * Copyright (c) 2007, Red Hat Middleware, LLC. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, v. 2.1. This program is distributed in the
 * hope that it will be useful, but WITHOUT A WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License, v.2.1 along with this
 * distribution; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEscapes {

    private PoParser poParser;
    private PoWriter poWriter;

    @BeforeEach
    public void setUp() {
        poParser = new PoParser();
        poWriter = new PoWriter();
    }

    @Test
    public void testEscapesCommentRoundtrip() throws Throwable {
        File original = getResource("/valid/escapes_comment.po");
        testEscapesRoundtrip(original);
    }

    @Test
    public void testEscapesFuzzyCommentRoundtrip() throws Throwable {
        File original = getResource("/valid/escapes_comment_fuzzy.po");
        testEscapesRoundtrip(original);
    }

    @Test
    public void testCRInMsgidAndMsgStrRoundtrip() throws Throwable {
        File original = getResource("/valid/escapes_cr_in_msgid_and_msgstr.po");
        testEscapesRoundtrip(original);
    }

    @Test
    public void testBackslashRoundtrip() throws Throwable {
        File original = getResource("/valid/escapes_backslash.po");
        testEscapesRoundtrip(original);
    }

    @Test
    public void testBackslash() throws Throwable {
        File original = getResource("/valid/escapes_backslash.po");
        Message msg = poParser.parseMessage(original);
        assertEquals("\\\\", msg.getMsgId());
        assertEquals("\\\\", msg.getMsgstr());
    }

    private void testEscapesRoundtrip(File f) throws Throwable {
        String output = escapesProcess(f);
        String originalString =
                TestUtils.readToStringFromMsgcat(f, true);
        assertEquals(originalString, output);
    }

    private String escapesProcess(File original) throws Throwable {
        Catalog originalCatalog = poParser.parseCatalog(original);
        StringWriter outputWriter = new StringWriter();
        poWriter.write(originalCatalog, outputWriter);
        outputWriter.flush();
        return outputWriter.toString();
    }

    private File getResource(String file) throws URISyntaxException {
        return new File(getClass().getResource(file).toURI());
    }

}
