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

import com.soberlemur.potentilla.catalog.parse.UnexpectedTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestRoundTrips {

    private PoParser poParser;
    private PoWriter poWriter;

    @BeforeEach
    public void setUp() {
        poParser = new PoParser();
        poWriter = new PoWriter();
    }

    @Test
    public void testRoundtrip1() throws URISyntaxException, IOException {
        File original = getResource("/roundtrip/sample.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testWordWrappingInMsgId() throws URISyntaxException, IOException {
        File original = getResource("/roundtrip/msgid_wordwrap.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testTab() throws URISyntaxException, IOException {
        File original = getResource("/roundtrip/tab.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testEmptyLineNote() throws URISyntaxException, IOException {
        File original =
                getResource("/roundtrip/translate-toolkit/emptylines_notes.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testMalformedObsoleteUnits() throws URISyntaxException {
        File original =
                getResource("/roundtrip/translate-toolkit/malformed_obsoleteunits.po");
        assertThrows(UnexpectedTokenException.class, () -> TestUtils.testRoundTrip(original));
    }

    @Test
    public void testMalformedUnits() throws URISyntaxException {
        File original =
                getResource("/roundtrip/translate-toolkit/malformed_units.po");
        assertThrows(UnexpectedTokenException.class, () -> TestUtils.testRoundTrip(original));
    }

    @Test
    public void testNonAsciiHeader() throws URISyntaxException, IOException {
        File original =
                getResource("/roundtrip/translate-toolkit/nonascii_header.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testMultilineContext() throws URISyntaxException, IOException {
        File original =
                getResource("/roundtrip/translate-toolkit/multiline_context.po");
        TestUtils.testRoundTrip(original);
    }

    @Test
    public void testContentEndsWithEOL() throws URISyntaxException, IOException {
        File original = getResource("/roundtrip/content_end_with_eol.po");
        TestUtils.testRoundTrip(original);
    }

    private File getResource(String file) throws URISyntaxException {
        return new File(getClass().getResource(file).toURI());
    }

}
