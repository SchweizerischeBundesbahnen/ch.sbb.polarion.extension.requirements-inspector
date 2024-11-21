package ch.sbb.polarion.extension.requirements_inspector.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for string manipulation operations.
 */
@UtilityClass
public final class StringUtil {

    /**
     * Removes separator characters from a given string and replaces them with spaces.
     *
     * @param string the string to process
     * @param sep    the separator characters to remove
     * @return the processed string with separators replaced by spaces
     */
    public String removeSeparatorChars(String string, String sep) {
        return replaceSeparatorChars(string, sep, " ");
    }

    /**
     * Replaces separator characters in a given string with a specified replacement string.
     *
     * @param string      the string to process
     * @param sep         the separator characters to replace
     * @param replacement the string to replace the separators with
     * @return the processed string with separators replaced by the specified replacement
     */
    public String replaceSeparatorChars(String string, String sep, String replacement) {
        return string.replaceAll(sep, replacement);
    }

    /**
     * Converts a string into a list of strings by splitting it at a given separator.
     *
     * @param string the string to convert
     * @param sep    the separator to split the string
     * @return a list of strings after splitting the input string
     */
    public List<String> stringToList(String string, String sep) {
        return Stream.of(string.split(sep))
                .map(String::trim)
                .toList();
    }

}
