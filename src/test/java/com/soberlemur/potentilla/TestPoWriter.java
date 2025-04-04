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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPoWriter {

    private String serializeMsg(Message msg, boolean encodeTabs) throws IOException {
        Catalog catalog = new Catalog().asTemplate();
        catalog.add(msg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new PoWriter().withTabsEncoding(encodeTabs).withCharset(StandardCharsets.UTF_8).write(catalog, bos);
        bos.close();
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testDefaultMsgStrShouldBeEmpty() throws IOException {
        Message msg = new Message();
        msg.setMsgId("id");

        String str = serializeMsg(msg, true);
        Pattern pattern =
                Pattern.compile("^msgstr\\s+\"\"$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(str);
        assertTrue(matcher.find());
        assertFalse(matcher.find());
    }

    @Test
    public void testDefaultPluralStrShouldBeEmpty() throws IOException {
        Message msg = new Message();
        msg.setMsgId("id");
        msg.setMsgidPlural("ids");

        String str = serializeMsg(msg, true);
        Pattern pattern =
                Pattern.compile("^msgstr\\[0\\]\\s+\"\"$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(str);
        assertTrue(matcher.find());
        assertFalse(matcher.find());
    }

    @Test
    public void tabShouldNotBeEncoded() throws IOException {
        Message msg = new Message();
        msg.setMsgId("msgid\tmsgid");
        msg.setMsgstr("msgstr\tmsgstr");

        String str = serializeMsg(msg, false);
        assertThat(str).contains("msgid \"msgid\tmsgid\"");
        assertThat(str).contains("msgstr \"msgstr\tmsgstr\"");
    }

    @Test
    public void tabShouldBeEncoded() throws IOException {
        Message msg = new Message();
        msg.setMsgId("msgid\tmsgid");
        msg.setMsgstr("msgstr\tmsgstr");

        String str = serializeMsg(msg, true);
        assertThat(str).contains("msgid \"msgid\\tmsgid\"");
        assertThat(str).contains("msgstr \"msgstr\\tmsgstr\"");
    }
}
