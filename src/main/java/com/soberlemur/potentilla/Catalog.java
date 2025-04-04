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
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * Models a catalog
 *
 * @author Steve Ebersole
 */
public class Catalog implements Iterable<Message> {
    private final LinkedHashMap<MessageKey, Message> messages = new LinkedHashMap<>();
    private HeaderMessage header;

    /**
     * Is this a POT (template)? Or a PO?
     */
    private boolean template;

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public boolean isTemplate() {
        return template;
    }

    public void add(Message message) {
        requireNonNull(message, "Cannot insert a null message");
        if (isNull(header) && isNull(message.getMsgContext()) && "".equals(message.getMsgId())) {
            this.header = HeaderMessage.from(message);
        } else {
            messages.put(new MessageKey(message), message);
        }
    }

    /**
     * Removes from the catalog the message with the given key
     *
     * @return true if the key is not null and a message was removed
     */
    public boolean remove(MessageKey key) {
        return nonNull(key) && nonNull(messages.remove(key));
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
            this.messages.forEach((k, m) -> {
                if (!template.contains(k)) {
                    m.markObsolete();
                }
            });
            //add new entries from the pot
            template.messages.forEach(this.messages::putIfAbsent);
        }
    }

    /**
     * @return this instance with a default header added if not already there
     */
    public Catalog withDefaultHeader() {
        if (isNull(header)) {
            this.header = HeaderMessage.defaultHeader();
        }
        return this;
    }

    /**
     * @return this instance with template set to true
     */
    public Catalog asTemplate() {
        this.template = true;
        return this;
    }

    @Override
    public Iterator<Message> iterator() {
        return messages.values().iterator();
    }
}
