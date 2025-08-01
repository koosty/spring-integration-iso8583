package com.github.koosty.iso8583;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * A utility class to parse structured data from custom financial messaging fields,
 * like Postilion's Field 127.22.
 *
 * <p>This parser is designed to handle a specific key-value pair format where
 * the length of the key and value fields are indicated by a preceding length indicator,
 * and the length of *that* indicator is also dynamic.</p>
 *
 * <p><b>Key-Value Pair Structure (TLV-like):</b></p>
 * <ul>
 * <li>1 byte: Length of the Key Length Indicator (e.g., '1', '2', '3').</li>
 * <li>N bytes: The Key Length Indicator itself.</li>
 * <li>M bytes: The Key itself.</li>
 * <li>1 byte: Length of the Value Length Indicator (e.g., '1', '2', '3').</li>
 * <li>P bytes: The Value Length Indicator itself.</li>
 * <li>Q bytes: The Value itself.</li>
 * </ul>
 */
public abstract class PostilionUtils {
    /**
     * Parses the raw string data from a field into a map of key-value pairs.
     *
     * @param fieldData The raw string content of the field.
     * @return A {@link Map} where keys and values are the extracted data. A LinkedHashMap is used to preserve insertion order.
     * @throws IllegalArgumentException if the field data is malformed.
     */
    public static Map<String, String> parseF172022(String fieldData) {
        if (fieldData == null || fieldData.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        int currentIndex = 0;

        while (currentIndex < fieldData.length()) {
            try {
                // --- 1. Parse Key ---
                // Read the 1-char length indicator size for the key length.
                char keyLengthIndicatorSizeChar = fieldData.charAt(currentIndex);
                int keyLengthIndicatorSize = Character.getNumericValue(keyLengthIndicatorSizeChar);
                currentIndex++;

                // Read the key length string itself.
                if (currentIndex + keyLengthIndicatorSize > fieldData.length()) {
                    throw new IllegalArgumentException("Malformed data: Not enough characters for key length indicator at index " + currentIndex);
                }
                String keyLengthStr = fieldData.substring(currentIndex, currentIndex + keyLengthIndicatorSize);
                int keyLength = Integer.parseInt(keyLengthStr);
                currentIndex += keyLengthIndicatorSize;

                // Read the key data.
                if (currentIndex + keyLength > fieldData.length()) {
                    throw new IllegalArgumentException("Malformed data: Not enough characters for key of length " + keyLength + " at index " + currentIndex);
                }
                String key = fieldData.substring(currentIndex, currentIndex + keyLength);
                currentIndex += keyLength;

                // --- 2. Parse Value ---
                // Read the 1-char length indicator size for the value length.
                if (currentIndex >= fieldData.length()) {
                    throw new IllegalArgumentException("Malformed data: Missing value length indicator size for key '" + key + "'");
                }
                char valueLengthIndicatorSizeChar = fieldData.charAt(currentIndex);
                int valueLengthIndicatorSize = Character.getNumericValue(valueLengthIndicatorSizeChar);
                currentIndex++;

                // Read the value length string itself.
                if (currentIndex + valueLengthIndicatorSize > fieldData.length()) {
                    throw new IllegalArgumentException("Malformed data: Not enough characters for value length indicator for key '" + key + "'");
                }
                String valueLengthStr = fieldData.substring(currentIndex, currentIndex + valueLengthIndicatorSize);
                int valueLength = Integer.parseInt(valueLengthStr);
                currentIndex += valueLengthIndicatorSize;

                // Read the value data.
                if (currentIndex + valueLength > fieldData.length()) {
                    throw new IllegalArgumentException("Malformed data: Not enough characters for value of length " + valueLength + " for key '" + key + "'");
                }
                String value = fieldData.substring(currentIndex, currentIndex + valueLength);
                currentIndex += valueLength;

                // Store the parsed key-value pair.
                keyValuePairs.put(key, value);

            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Malformed data: Invalid length in prefix. Could not parse number.", e);
            }
        }

        return keyValuePairs;
    }
    /**
     * Converts a map of key-value pairs back into the custom string format.
     *
     * <p>This method reconstructs the string representation based on the defined format:
     * a dynamic-length-of-the-length-field format for both keys and values.</p>
     *
     * @param keyValuePairs The map of key-value pairs to convert. The order of insertion is important,
     * so a {@link LinkedHashMap} is recommended.
     * @return The formatted string representation of the data.
     * @throws IllegalArgumentException if a key or value is null, or if the length of the
     * key or value exceeds the maximum length supported by the format.
     */
    public static String buildF172022(Map<String, String> keyValuePairs) {
        if (keyValuePairs == null || keyValuePairs.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key == null || value == null) {
                throw new IllegalArgumentException("Key or value cannot be null.");
            }

            // --- 1. Build Key ---
            String keyLengthStr = String.valueOf(key.length());
            int keyLengthIndicatorSize = keyLengthStr.length();
            result.append(keyLengthIndicatorSize).append(keyLengthStr).append(key);

            // --- 2. Build Value ---
            String valueLengthStr = String.valueOf(value.length());
            int valueLengthIndicatorSize = valueLengthStr.length();
            result.append(valueLengthIndicatorSize).append(valueLengthStr).append(value);
        }

        return result.toString();
    }
}
