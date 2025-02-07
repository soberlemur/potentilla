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
package com.soberlemur.potentilla;

import com.soberlemur.potentilla.catalog.parse.UnexpectedTokenException;

import static java.util.Objects.isNull;

public final class StringUtil {

    private StringUtil() {
        //util
    }

    public static String addEscapes(String s) {
        s = s.replace("\r", "\\r");
        s = s.replace("\n", "\\n");
        return s = s.replace("\"", "\\\"");
    }

    public static boolean isBlank(String input) {
        return input == null || input.trim().length() <= 0;
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }

    /**
     * @return the input string if it is not null; otherwise, returns the specified default value.
     */
    public static String defaultString(String input, String defaultValue) {
        if (isNull(input)) {
            return defaultValue;
        }
        return input;
    }

    public static String removeEscapes(String s) {
        StringBuilder result = new StringBuilder();

        if (s.length() == 1) {
            // if it's a single character in the stream, only the backslash
            // can cause problems
            if (s.charAt(0) != '\\') {
                return s;
            }
            throw new UnexpectedTokenException("Unexpected token '\\' ", -1);
        }

        char[] chars = s.toCharArray();

        for (int i = 1; i < chars.length; i++) {

            char prev = chars[i - 1];
            char current = chars[i];

            if (prev == '\\') {
                switch (current) {
                case '\\':
                    result.append('\\');
                    chars[i] = ' '; // to avoid double quoting
                    break;
                case 'r':
                    result.append('\r');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 't':
                    result.append('\t');
                    break;
                case '\"':
                    result.append('\"');
                    break;
                default:
                    throw new UnexpectedTokenException("Invalid escape sequence: " + prev + current, -1);
                }
            } else { // prev is not '\\'
                if (i == 1) {
                    result.append(prev);
                }
                if (current != '\\') {
                    result.append(current);
                }
            }
        }
        return result.toString();
    }

    public static String quote(String s) {
        if (isNull(s)) {
            return "null";
        }
        return '\"' + s + '\"';
    }
}
