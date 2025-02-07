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
 *                    Asgeir Frimannsson
 */
package com.soberlemur.potentilla;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.nonNull;

public class PoWriter {

    private Charset charset = StandardCharsets.UTF_8;
    private boolean encodeTabs = true;
    private boolean addDefaultHeader = false;

    /**
     * Sets the charset, default is {@link StandardCharsets#UTF_8}
     */
    public PoWriter withCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Sets if tabs should be encoded, default is true
     */
    public PoWriter withTabsEncoding(boolean encodeTabs) {
        this.encodeTabs = encodeTabs;
        return this;
    }

    /**
     * Sets if the writer should add a default header if the header is missing, default is false
     */
    public PoWriter withAddHeaderIfMissing() {
        this.addDefaultHeader = true;
        return this;
    }

    public void write(Catalog catalog, File file) throws IOException {
        write(catalog, Files.newBufferedWriter(file.toPath(), charset));
    }

    public void write(Catalog catalog, OutputStream outputStream) throws IOException {
        write(catalog, new OutputStreamWriter(new BufferedOutputStream(outputStream), charset));
    }

    public void write(Message message, File file) throws IOException {
        write(message, Files.newBufferedWriter(file.toPath(), charset));
    }

    public void write(Message message, OutputStream outputStream) throws IOException {
        write(message, new OutputStreamWriter(new BufferedOutputStream(outputStream), charset));
    }

    public void write(Catalog catalog, Writer writer) throws IOException {
        var header = catalog.header();
        if (nonNull(header)) {
            write(header.toMessage(), writer);
            writer.write('\n');
        } else if (addDefaultHeader) {
            write(HeaderMessage.defaultHeader().toMessage(), writer);
            writer.write('\n');
        }
        boolean first = true;
        for (Message message : catalog) {
            if (first) {
                first = false;
            } else {
                writer.write('\n');
            }
            write(message, writer);
        }
    }

    public void write(Message message, Writer writer) throws IOException {

        for (String comment : message.getComments()) {
            writeComment("# ", comment, writer);
        }

        for (String comment : message.getExtractedComments()) {
            writeComment("#. ", comment, writer);
        }

        for (String sourceRef : message.getSourceReferences()) {
            writeComment("#: ", sourceRef, writer);
        }

        Collection<String> formats = message.getFormats();
        if (!formats.isEmpty()) {
            writer.write("#");
            for (String format : formats) {
                writer.write(", ");
                writer.write(format);
            }
            writer.write('\n');
        }

        if (message.getPrevMsgctx() != null) {
            writeMsgctxt("#| ", message.getPrevMsgctx(), writer);
        }

        if (message.getPrevMsgid() != null) {
            writeMsgid("#| ", message.getPrevMsgid(), writer);
        }

        if (message.getPrevMsgidPlural() != null) {
            writeMsgidPlural("#| ", message.getPrevMsgidPlural(), writer);
        }

        String prefix = message.isObsolete() ? "#~ " : "";
        if (message.getMsgContext() != null) {
            writeMsgctxt(prefix, message.getMsgContext(), writer);
        }

        if (message.isPlural()) {
            writeMsgid(prefix, message.getMsgId(), writer);
            writeMsgidPlural(prefix, message.getMsgidPlural(), writer);
            writeMsgstrPlurals(prefix, message.getMsgstrPlural(), writer);
        } else {
            writeMsgid(prefix, message.getMsgId(), writer);
            writeMsgstr(prefix, message.getMsgstr(), writer);
        }

        writer.flush();
    }

    protected void writeComment(String prefix, String comment, Writer writer) throws IOException {
        String[] lines = comment.split("\n");
        for (String line : lines) {
            writer.write(prefix);
            writer.write(line);
            writer.write('\n');
        }
    }

    /**
     * @param prefix                for obsolete entry
     * @param s                     not null string to output
     * @param writer
     * @param firstLineContextWidth number of characters 'context' (e.g. 'msgid ' or 'msgstr ')
     * @param colWidth              width of each line in characters
     * @param indent                number of characters to indent each line
     * @throws IOException
     */

    protected void writeString(String prefix, String s, Writer writer, int firstLineContextWidth, int colWidth, int indent) throws IOException {
        // This is for obsolete entry processing. When the first line
        // is not empty, it doesn't need to output "#~".
        boolean firstline = true;

        writer.write('\"');

        // check if we should output a empty first line
        int firstLineEnd = s.indexOf('\n');
        if ((firstLineEnd != -1 && firstLineEnd > (colWidth - firstLineContextWidth - 4)) || s.length() > (colWidth - firstLineContextWidth - 4)) {
            firstline = false;
            writer.write('\"');
            writer.write('\n');
            if (prefix.isEmpty()) {
                writer.write('\"');
            }
        }

        StringBuilder currentLine = new StringBuilder(100);

        int lastSpacePos = 0;

        for (int i = 0; i < s.length(); i++) {
            char currentChar = s.charAt(i);

            switch (currentChar) {
            case '\n':
                currentLine.append('\\');
                currentLine.append('n');
                if (i != s.length() - 1) {

                    if (!prefix.isEmpty() && !firstline) {
                        writer.write(prefix);
                        writer.write('\"');
                        writer.write(currentLine.toString());
                        writer.write('\"');
                        writer.write('\n');
                    }

                    if (!prefix.isEmpty() && firstline) {
                        writer.write(currentLine.toString());
                        writer.write('\"');
                        writer.write('\n');
                        firstline = false;
                    }

                    if (prefix.isEmpty()) {
                        writer.write(currentLine.toString());
                        writer.write('\"');
                        writer.write('\n');
                        writer.write('\"');
                    }

                    lastSpacePos = 0;
                    currentLine.delete(0, currentLine.length());
                }
                break;
            case '\\':
                currentLine.append(currentChar);
                currentLine.append(currentChar);
                break;
            case '\r':
                currentLine.append('\\');
                currentLine.append('r');
                break;
            case '\t':
                if (encodeTabs) {
                    currentLine.append('\\');
                    currentLine.append('t');
                } else {
                    currentLine.append(currentChar);
                }
                break;
            case '"':
                currentLine.append('\\');
                currentLine.append(currentChar);
                break;
            case ':':
            case '.':
            case '/':
            case '-':
            case '=':
            case ' ':
                lastSpacePos = currentLine.length();
                currentLine.append(currentChar);
                break;
            default:
                currentLine.append(currentChar);
            }

            if (currentLine.length() > colWidth - 4 && lastSpacePos != 0) {
                if (!prefix.isEmpty() && !firstline) {
                    writer.write(prefix);
                    writer.write('\"');
                    writer.write(currentLine.substring(0, lastSpacePos + 1));
                    writer.write('\"');
                    writer.write('\n');
                }

                if (!prefix.isEmpty() && firstline) {
                    writer.write(currentLine.substring(0, lastSpacePos + 1));
                    writer.write('\"');
                    writer.write('\n');
                    firstline = false;
                }

                if (prefix.isEmpty()) {
                    writer.write(currentLine.substring(0, lastSpacePos + 1));
                    writer.write('\"');
                    writer.write('\n');
                    writer.write('\"');
                }
                currentLine.delete(0, lastSpacePos + 1);
                lastSpacePos = 0;
            }
        }

        if (!prefix.isEmpty() && !firstline) {
            writer.write(prefix);
            writer.write('\"');
            writer.write(currentLine.toString());

            writer.write('\"');
            writer.write('\n');
        }

        if (!prefix.isEmpty() && firstline) {
            writer.write(currentLine.toString());
            writer.write('\"');
            writer.write('\n');
            firstline = false;
        }

        if (prefix.isEmpty()) {
            writer.write(currentLine.toString());
            writer.write('\"');
            writer.write('\n');
        }
    }

    protected void writeString(String prefix, String s, Writer writer, int firstLineContextWidth) throws IOException {
        writeString(prefix, s, writer, firstLineContextWidth, 80, 0);
    }

    protected void writeMsgctxt(String prefix, String ctxt, Writer writer) throws IOException {
        String msgSpace = "msgctxt ";
        writer.write(prefix + msgSpace);
        writeString(prefix, ctxt, writer, msgSpace.length());
    }

    protected void writeMsgid(String prefix, String msgid, Writer writer) throws IOException {
        String msgSpace = "msgid ";
        writer.write(prefix + msgSpace);
        writeString(prefix, msgid, writer, msgSpace.length());
    }

    protected void writeMsgidPlural(String prefix, String msgidPlural, Writer writer) throws IOException {
        String msgSpace = "msgid_plural ";
        writer.write(prefix + msgSpace);
        writeString(prefix, msgidPlural, writer, msgSpace.length());
    }

    protected void writeMsgstr(String prefix, String msgstr, Writer writer) throws IOException {
        if (msgstr == null) {
            msgstr = "";
        }
        String msgSpace = "msgstr ";
        writer.write(prefix + msgSpace);
        writeString(prefix, msgstr, writer, msgSpace.length());
    }

    protected void writeMsgStrPlural(String prefix, String msgstr, int i, Writer writer) throws IOException {
        String msgSpace = "msgstr[" + i + "] ";
        writer.write(prefix + msgSpace);
        writeString(prefix, msgstr, writer, msgSpace.length());
    }

    protected void writeMsgstrPlurals(String prefix, List<String> msgstrPlurals, Writer writer) throws IOException {
        if (msgstrPlurals.isEmpty()) {
            writeMsgStrPlural(prefix, "", 0, writer);
        } else {
            int i = 0;
            for (String msgstr : msgstrPlurals) {
                writeMsgStrPlural(prefix, msgstr, i, writer);
                i++;
            }
        }
    }

}
