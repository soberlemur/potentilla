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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Message implementation
 *
 * @author Steve Ebersole
 */
public class Message {

    public static final String FUZZY = "fuzzy";

    private String domain;
    private String msgctxt;
    private String msgid;
    private String msgidPlural;
    private String msgstr;
    private final List<String> msgstrPlural = new ArrayList<>();

    private String prevMsgctx;
    private String prevMsgid;
    private String prevMsgidPlural;

    private final Collection<String> comments = new ArrayList<>();
    private final Collection<String> extractedComments = new ArrayList<>();
    private final List<String> sourceRefs = new ArrayList<>();
    private final Set<String> formats = new LinkedHashSet<>();

    private boolean obsolete;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMsgContext() {
        return msgctxt;
    }

    public void setMsgContext(String msgctxt) {
        this.msgctxt = msgctxt;
    }

    public String getPrevMsgctx() {
        return prevMsgctx;
    }

    public void setPrevMsgctx(String prevMsgctx) {
        this.prevMsgctx = prevMsgctx;
    }

    public String getMsgId() {
        return msgid;
    }

    public void setMsgId(String msgid) {
        this.msgid = msgid;
    }

    public String getPrevMsgid() {
        return prevMsgid;
    }

    public void setPrevMsgid(String prevMsgid) {
        this.prevMsgid = prevMsgid;
    }

    public String getMsgidPlural() {
        return msgidPlural;
    }

    public void setMsgidPlural(String msgidPlural) {
        this.msgidPlural = msgidPlural;
    }

    public String getPrevMsgidPlural() {
        return prevMsgidPlural;
    }

    public void setPrevMsgidPlural(String prevMsgidPlural) {
        this.prevMsgidPlural = prevMsgidPlural;
    }

    public String getMsgstr() {
        return msgstr;
    }

    public void setMsgstr(String msgstr) {
        this.msgstr = requireNonNull(msgstr, "msgstr cannot be null");
        clearPlurals();
    }

    private void clearPlurals() {
        this.msgstrPlural.clear();
    }

    public void addMsgstrPlural(String msgstr, int position) {
        msgstrPlural.add(position, requireNonNull(msgstr, "msgstr cannot be null"));
    }

    public void markFuzzy() {
        formats.add(FUZZY);
    }

    public boolean isFuzzy() {
        if (this.getMsgstr() != null) {
            if (this.getMsgstr().isEmpty()) {
                return false;
            }
        }

        return formats.contains(FUZZY);
    }

    public void markObsolete() {
        this.obsolete = true;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public void addExtractedComment(String comment) {
        extractedComments.add(comment);
    }

    public void addSourceReference(String ref) {
        sourceRefs.add(ref);
    }

    public void addSourceReference(String file, int line) {
        addSourceReference(file + ':' + line);
    }

    public void addFormat(String format) {
        formats.add(format);
    }

    public List<String> getMsgstrPlural() {
        return msgstrPlural;
    }

    public boolean isPlural() {
        return msgidPlural != null;
    }

    public List<String> getSourceReferences() {
        return sourceRefs;
    }

    public Collection<String> getComments() {
        return comments;
    }

    public Collection<String> getExtractedComments() {
        return extractedComments;
    }

    public Collection<String> getFormats() {
        return formats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Message(msgctxt ").append(StringUtil.quote(msgctxt)).
          append(", msgid ").append(StringUtil.quote(getMsgId())).
          append(", msgstr \"").append(getMsgstr());
        if (!getComments().isEmpty()) {
            sb.append("\", transComments \"").append(getComments());
        }
        if (!getExtractedComments().isEmpty()) {
            sb.append("\", extComments \"").append(getExtractedComments());
        }
        if (!getFormats().isEmpty()) {
            sb.append("\", flags \"").append(getFormats());
        }
        if (!getSourceReferences().isEmpty()) {
            sb.append("\", references \"").append(getSourceReferences());
        }
        // TODO should include fuzzy, obsolete, plurals, domain, prevMsg
        sb.append("\")");
        return sb.toString();
    }

}
