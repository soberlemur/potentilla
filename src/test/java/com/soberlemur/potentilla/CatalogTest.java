package com.soberlemur.potentilla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Created on 06/03/25
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
class CatalogTest {

    @Test
    void updateFromTemplate_marksObsoleteMessages() {
        var existingCatalog = new Catalog();
        var msg = new Message();
        msg.setMsgId("id1");
        msg.setMsgContext("context1");
        msg.setMsgstr("translation");

        var msg2 = new Message();
        msg2.setMsgId("id2");
        msg2.setMsgContext("context2");
        msg2.setMsgstr("another translation");

        existingCatalog.add(msg);
        existingCatalog.add(msg2);

        var templateCatalog = new Catalog(true);
        var tmsg2 = new Message();
        tmsg2.setMsgId("id2");
        tmsg2.setMsgContext("context2");
        var tmsg3 = new Message();
        tmsg3.setMsgId("id3");
        templateCatalog.add(tmsg2);
        templateCatalog.add(tmsg3);

        existingCatalog.updateFromTemplate(templateCatalog);

        assertTrue(msg.isObsolete());
        assertTrue(existingCatalog.contains(null, "id3"));
    }

    @Test
    void updateFromTemplate_noChangesIfTemplateFlagIsFalse() {
        var existingCatalog = new Catalog();
        var msg = new Message();
        msg.setMsgId("id1");
        msg.setMsgContext("context1");
        msg.setMsgstr("translation");

        var msg2 = new Message();
        msg2.setMsgId("id2");
        msg2.setMsgContext("context2");
        msg2.setMsgstr("another translation");

        existingCatalog.add(msg);
        existingCatalog.add(msg2);

        var templateCatalog = new Catalog(false);
        var tmsg2 = new Message();
        tmsg2.setMsgId("id2");
        tmsg2.setMsgContext("context2");
        var tmsg3 = new Message();
        tmsg3.setMsgId("id3");
        templateCatalog.add(tmsg2);
        templateCatalog.add(tmsg3);

        existingCatalog.updateFromTemplate(templateCatalog);

        assertFalse(msg.isObsolete());
        assertFalse(existingCatalog.contains(null, "id3"));
    }

    @Test
    public void nullMessage() {
        var catalog = new Catalog();
        assertThrows(NullPointerException.class, () -> catalog.add(null));
    }

    @Test
    public void removeNullMessage() {
        var catalog = new Catalog();
        var msg = new Message();
        msg.setMsgId("id1");
        msg.setMsgContext("context1");
        msg.setMsgstr("translation");
        catalog.add(msg);
        assertFalse(catalog.remove(null));
        assertFalse(catalog.remove(new MessageKey("Chuck", "Norris")));
        assertTrue(catalog.remove(new MessageKey(msg)));
        assertEquals(0, catalog.size());

    }

}