package com.soberlemur.potentilla.catalog.parse;
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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCatalogLexer
{

    @Test
    public void testAscii() throws Exception
    {
        checkCharset("ascii.po", "ASCII");
    }

    @Test
    public void testBig5() throws Exception
    {
        checkCharset("big5.po", "BIG5");
    }

    @Test
    public void testCharset() throws Exception
    {
        checkCharset("charset.po", "ASCII");
    }

    @Test
    public void testUnspecified() throws Exception
    {
        checkCharset("unspecified.po", "UTF-8");
    }

    @Test
    public void testUtf8() throws Exception
    {
        checkCharset("utf8.po", "UTF-8");
    }

    private void checkCharset(String testfile, String expected)
            throws IOException, UnsupportedEncodingException
    {
        InputStream in1 = getClass().getResourceAsStream(testfile);
        String charset1 = CatalogLexer.readGettextCharset(in1);
        assertEquals(expected, charset1);
    }

}
