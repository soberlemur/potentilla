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

/**
 * Possible header values <a href="https://www.gnu.org/software/gettext/manual/html_node/Header-Entry.html">...</a>
 *
 * @author Andrea Vacondio
 */
public enum Header {
    PROJECT_ID_VERSION("Project-Id-Version"),
    REPORT_MSGID_BUGS_TO("Report-Msgid-Bugs-To"),
    POT_CREATION_DATE("POT-Creation-Date"),
    PO_REVISION_DATE("PO-Revision-Date"),
    LAST_TRANSLATOR("Last-Translator"),
    LANGUAGE_TEAM("Language-Team"),
    LANGUAGE("Language"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_TRANSFER_ENCODING("Content-Transfer-Encoding"),
    PLURAL_FORMS("Plural-Forms");

    private final String value;

    Header(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
