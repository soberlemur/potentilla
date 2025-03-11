package com.soberlemur.potentilla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Created on 07/02/25
 * Copyright 2025 by Sober Lemur S.r.l. (info@soberlemur.com).
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
 * Author(s): Andrea Vacondio
 */

class HeaderMessageTest {

    @Test
    public void testValidPo() throws Throwable {
        Catalog catalog = new PoParser().parseCatalog(HeaderMessageTest.class.getClassLoader().getResourceAsStream("valid/sample.po"), false);
        var header = catalog.header();
        assertEquals("http://bugs.kde.org", header.getValue(Header.REPORT_MSGID_BUGS_TO));
        assertEquals("PACKAGE VERSION", header.getValue(Header.PROJECT_ID_VERSION));
        assertEquals("LANGUAGE <kde-i18n-doc@kde.org>", header.getValue(Header.LANGUAGE_TEAM));
    }

    @Test
    public void testCustomAttribute() {
        var header = new HeaderMessage();
        header.setValue("X-Generator", "Poedit 3.4.2");
        assertEquals("Poedit 3.4.2", header.getValue("X-Generator"));
    }

    @Test
    public void testToMessage() throws Throwable {
        Catalog catalog = new PoParser().parseCatalog(HeaderMessageTest.class.getClassLoader().getResourceAsStream("valid/sample.po"), false);
        var header = catalog.header();
        header.addComment("Some comment");
        header.updateRevisionDate();
        var message = header.toMessage();
        assertEquals("", message.getMsgId());
        assertEquals(4, message.getComments().size());
    }
}