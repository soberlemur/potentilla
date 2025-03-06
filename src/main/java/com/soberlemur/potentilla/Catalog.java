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
 * Red Hat Author(s): Steve Ebersole
 */
package com.soberlemur.potentilla;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * Models a catalog
 *
 * @author Steve Ebersole
 */
public class Catalog implements Iterable<Message> {
    // todo : segment by domain?
    private final LinkedHashMap<MessageKey, Message> messages = new LinkedHashMap<>();
    private HeaderMessage header;

    /**
     * Is this a POT (template)? Or a PO?
     */
    private boolean template;

    public Catalog(boolean template) {
        this.template = template;
    }

    public Catalog() {
        this(false);
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public boolean isTemplate() {
        return template;
    }

    public void add(Message message) {
        if (isNull(header) && isNull(message.getMsgContext()) && "".equals(message.getMsgId())) {
            this.header = HeaderMessage.from(message);
        } else {
            messages.put(new MessageKey(message), message);
        }
    }

    /**
     * @return the header or null if not found
     */
    public HeaderMessage header() {
        return header;
    }

    public Message get(String msgctxt, String msgid) {
        return get(new MessageKey(msgctxt, msgid));
    }

    public Message get(MessageKey key) {
        return messages.get(key);
    }

    public boolean contains(String msgctxt, String msgid) {
        return contains(new MessageKey(msgctxt, msgid));
    }

    public boolean contains(MessageKey key) {
        return messages.containsKey(key);
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public int size() {
        return messages.size();
    }

    public Stream<Message> stream() {
        return messages.values().stream();
    }

    public void updateFromTemplate(Catalog template) {
        if (!this.template && template.template) {
            //make obsolete what is not in the pot
            this.messages.forEach((k,m)->{
                if(!template.contains(k)) {
                    m.markObsolete();
                }
            });
            //add new entries from the pot
            template.messages.forEach(this.messages::putIfAbsent);
        }
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.values().iterator();
    }
}
