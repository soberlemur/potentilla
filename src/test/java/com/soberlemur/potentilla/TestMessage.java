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

import com.soberlemur.potentilla.catalog.parse.MessageStreamParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMessage {

    @Test
    public void testIsFuzzyWhenEmptyMsgstr() {
        Message msg = new Message();
        msg.setMsgId("hello world!");
        assertFalse(msg.isFuzzy());
        msg.setMsgstr("");
        assertFalse(msg.isFuzzy());

        msg.markFuzzy();
        assertFalse(msg.isFuzzy(), "Empty msgstr should not produce fuzzy");
    }

    @Test
    public void testIsFuzzyWhenMarkedAsFuzzy() {
        Message msg = new Message();
        msg.setMsgId("hello world!");
        msg.setMsgstr("hei verden");
        msg.markFuzzy();
        assertTrue(msg.isFuzzy());
    }

    @Test
    public void testFlagsAreWorkingAsExpected() throws Throwable {
        File poFile = getResource("/flags.po");

        MessageStreamParser parser = new MessageStreamParser(poFile);

        Message msg = parser.next();
        assertTrue(msg.isFuzzy());
        assertTrue(msg.getFormats().contains("no-c-format"));

        msg = parser.next();
        assertTrue(msg.isFuzzy());
        assertTrue(msg.getFormats().contains("no-c-format"));

        msg = parser.next();
        assertTrue(msg.isFuzzy());
        assertTrue(msg.getFormats().contains("no-c-format"));

        msg = parser.next();
        assertTrue(msg.isFuzzy());
        assertTrue(msg.getFormats().contains("no-c-format"));

    }

    private File getResource(String file) throws URISyntaxException {
        return new File(getClass().getResource(file).toURI());
    }

}
