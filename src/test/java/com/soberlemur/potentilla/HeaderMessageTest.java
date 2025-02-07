package com.soberlemur.potentilla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * This file is part of the Choco Black PDF Tools project
 * Created on 07/02/25
 * Copyright 2025 by Sober Lemur S.r.l. (info@soberlemur.com).
 *
 * You are not permitted to distribute it in any form unless explicit
 * consent is given by Sober Lemur S.r.l.
 * You are not permitted to modify it.
 *
 * Choco Black PDF Tools is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
        assertEquals(1, message.getComments().size());
    }
}