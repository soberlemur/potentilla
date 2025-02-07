package com.soberlemur.potentilla;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.soberlemur.potentilla.Header.CONTENT_TRANSFER_ENCODING;
import static com.soberlemur.potentilla.Header.CONTENT_TYPE;
import static com.soberlemur.potentilla.Header.LANGUAGE_TEAM;
import static com.soberlemur.potentilla.Header.LAST_TRANSLATOR;
import static com.soberlemur.potentilla.Header.POT_CREATION_DATE;
import static com.soberlemur.potentilla.Header.PO_REVISION_DATE;
import static com.soberlemur.potentilla.Header.PROJECT_ID_VERSION;
import static com.soberlemur.potentilla.Header.REPORT_MSGID_BUGS_TO;
import static com.soberlemur.potentilla.StringUtil.defaultString;
import static com.soberlemur.potentilla.StringUtil.isNotBlank;
import static java.util.Optional.ofNullable;

/**
 * @author Andrea Vacondio
 */
public class HeaderMessage {
    private static final Logger LOG = LoggerFactory.getLogger(HeaderMessage.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mmZ").withZone(ZoneId.systemDefault());

    private final Map<String, String> entries = new LinkedHashMap<>();
    private final List<String> comments = new ArrayList<>();

    HeaderMessage() {
        //hide
    }

    public void setValue(String key, String value) {
        if (isNotBlank(key)) {
            entries.put(key, defaultString(value, ""));
        }
    }

    public void setValue(Header header, String value) {
        if (Objects.nonNull(header)) {
            setValue(header.value(), value);
        }
    }

    public String getValue(Header header) {
        return entries.get(header.value());
    }

    public String getValue(String key) {
        return entries.get(key);
    }

    public Map<String, String> entries() {
        return Collections.unmodifiableMap(entries);
    }

    public void updateRevisionDate() {
        setValue(PO_REVISION_DATE, FORMATTER.format(Instant.now()));
    }

    public void addComment(String s) {
        this.comments.add(s);
    }

    public Message toMessage() {
        Message header = new Message();
        header.setMsgId("");
        header.markFuzzy();
        header.setMsgstr(entries.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue() + "\n").reduce("", String::concat));
        this.comments.forEach(header::addComment);
        return header;
    }

    public static HeaderMessage from(Message message) {
        String[] entries = ofNullable(message).map(Message::getMsgstr).map(str -> str.split("\n")).orElseGet(() -> new String[0]);
        var header = new HeaderMessage();
        for (String entry : entries) {
            if (isNotBlank(entry)) {
                String[] keyval = entry.split("\\:", 2);
                if (keyval.length == 2) {
                    header.entries.put(keyval[0].trim(), keyval[1].trim());
                } else {
                    LOG.warn("Header entry is not key:value pair [{}]. It will be ignored.", entry);
                }
            } else {
                LOG.warn("Ignoring black header entry");
            }
        }
        return header;
    }

    public static HeaderMessage defaultHeader() {
        var now = FORMATTER.format(Instant.now());
        var header = new HeaderMessage();
        header.setValue(PROJECT_ID_VERSION, "PACKAGE VERSION");
        header.setValue(REPORT_MSGID_BUGS_TO, "");
        header.setValue(POT_CREATION_DATE, now);
        header.setValue(PO_REVISION_DATE, now);
        header.setValue(LAST_TRANSLATOR, "FULL NAME <EMAIL@ADDRESS>");
        header.setValue(LANGUAGE_TEAM, "LANGUAGE <LL@li.org>");
        header.setValue(LAST_TRANSLATOR, "FULL NAME <EMAIL@ADDRESS>");
        header.setValue(CONTENT_TYPE, "text/plain; charset=UTF-8");
        header.setValue("MIME-Version", "1.0");
        header.setValue(CONTENT_TRANSFER_ENCODING, "8bit");
        header.addComment("SOME DESCRIPTIVE TITLE.");
        header.addComment("Copyright (C) YEAR THE PACKAGE'S COPYRIGHT HOLDER");
        header.addComment("This file is distributed under the same license as the PACKAGE package.");
        header.addComment("FIRST AUTHOR <EMAIL@ADDRESS>, YEAR.");
        header.addComment("");
        return header;
    }

}
