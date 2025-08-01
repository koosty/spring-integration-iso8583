package com.github.koosty.iso8583;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostilionUtilsTest {
    @Test
    void parseF172022WhenValidSingleEntryThenReturnCorrectMap() {
        // Given a valid single-entry string
        String fieldData = "14MSDN172260953";

        // When parsing the string
        Map<String, String> parsedData = PostilionUtils.parseF172022(fieldData);

        // Then a map with the correct single key-value pair is returned
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("MSDN", "2260953");
        assertEquals(expectedMap, parsedData);
    }

    @Test
    void parseF172022WhenValidMultipleEntriesThenReturnCorrectMap() {
        // Given a valid multi-entry string
        String fieldData = "14MSDN172260953213UssdSessionId253mtn:260962210258:298a2003-cc04-4d13-8947-a0435a3d9205216SENDER_FULL_NAME212John Mostert";

        // When parsing the string
        Map<String, String> parsedData = PostilionUtils.parseF172022(fieldData);

        // Then a map with all the correct key-value pairs is returned
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("MSDN", "2260953");
        expectedMap.put("UssdSessionId", "mtn:260962210258:298a2003-cc04-4d13-8947-a0435a3d9205");
        expectedMap.put("SENDER_FULL_NAME", "John Mostert");
        assertEquals(expectedMap, parsedData);
    }

    @Test
    void parseF172022WhenMultiDigitLengthsThenReturnCorrectMap() {
        // Given a string with multi-digit length indicators (key and value)
        String fieldData = "216SENDER_FULL_NAME212John Mostert";

        // When parsing the string
        Map<String, String> parsedData = PostilionUtils.parseF172022(fieldData);

        // Then a map with the correct single key-value pair is returned
        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("SENDER_FULL_NAME", "John Mostert");
        assertEquals(expectedMap, parsedData);
    }

    @Test
    void parseF172022WhenEmptyStringThenReturnEmptyMap() {
        // Given an empty string
        String fieldData = "";

        // When parsing the string
        Map<String, String> parsedData = PostilionUtils.parseF172022(fieldData);

        // Then an empty map is returned
        assertTrue(parsedData.isEmpty());
    }

    @Test
    void parseF172022WhenNullStringThenReturnEmptyMap() {
        // Given a null string
        String fieldData = null;

        // When parsing the string
        Map<String, String> parsedData = PostilionUtils.parseF172022(fieldData);

        // Then an empty map is returned
        assertTrue(parsedData.isEmpty());
    }

    @Test
    void parseF172022WhenInvalidKeyLengthIndicatorSizeThenThrowException() {
        // Given a malformed string where the first character is not a digit
        String fieldData = "a4MSDN172260953";

        // When parsing, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.parseF172022(fieldData);
        });
        assertTrue(exception.getMessage().contains("Invalid length in prefix"));
    }

    @Test
    void parseF172022WhenKeyDataIsMissingThenThrowException() {
        // Given a string indicating a key length of 4 but only has 2 characters
        String fieldData = "14MS";

        // When parsing, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.parseF172022(fieldData);
        });
        assertTrue(exception.getMessage().contains("Not enough characters for key of length 4"));
    }

    @Test
    void parseF172022WhenValueLengthIndicatorSizeIsMissingThenThrowException() {
        // Given a string with a complete key but no value length indicator size
        String fieldData = "14MSDN";

        // When parsing, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.parseF172022(fieldData);
        });
        assertTrue(exception.getMessage().contains("Missing value length indicator size"));
    }

    @Test
    void parseF172022WhenInvalidValueLengthIndicatorThenThrowException() {
        // Given a string with a non-numeric value length indicator
        String fieldData = "14MSDN1a123";

        // When parsing, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.parseF172022(fieldData);
        });
        assertTrue(exception.getMessage().contains("Invalid length in prefix"));
    }

    @Test
    void parseF172022WhenValueDataIsMissingThenThrowException() {
        // Given a string with a value length of 7 but only 3 characters
        String fieldData = "14MSDN17123";

        // When parsing, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.parseF172022(fieldData);
        });
        assertTrue(exception.getMessage().contains("Not enough characters for value of length 7"));
    }

    // --- Tests for build() method ---

    @Test
    void buildF172022WhenValidMapThenReturnCorrectString() {
        // Given a map with a single key-value pair
        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        keyValuePairs.put("MSDN", "2260953");
        String expectedString = "14MSDN172260953";

        // When building the string
        String builtString = PostilionUtils.buildF172022(keyValuePairs);

        // Then the resulting string matches the expected format
        assertEquals(expectedString, builtString);
    }

    @Test
    void buildF172022WhenValidMapWithMultipleEntriesThenReturnCorrectString() {
        // Given a map with multiple key-value pairs
        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        keyValuePairs.put("MSDN", "2260953");
        keyValuePairs.put("UssdSessionId", "mtn:260962210258:298a2003-cc04-4d13-8947-a0435a3d9205");
        keyValuePairs.put("SENDER_FULL_NAME", "John Mostert");
        String expectedString = "14MSDN172260953213UssdSessionId253mtn:260962210258:298a2003-cc04-4d13-8947-a0435a3d9205216SENDER_FULL_NAME212John Mostert";

        // When building the string
        String builtString = PostilionUtils.buildF172022(keyValuePairs);

        // Then the resulting string matches the expected format
        assertEquals(expectedString, builtString);
    }

    @Test
    void buildF172022WhenEmptyMapThenReturnEmptyString() {
        // Given an empty map
        Map<String, String> keyValuePairs = new LinkedHashMap<>();

        // When building the string
        String builtString = PostilionUtils.buildF172022(keyValuePairs);

        // Then an empty string is returned
        assertEquals("", builtString);
    }

    @Test
    void buildF172022WhenNullMapThenReturnEmptyString() {
        // Given a null map
        Map<String, String> keyValuePairs = null;

        // When building the string
        String builtString = PostilionUtils.buildF172022(keyValuePairs);

        // Then an empty string is returned
        assertEquals("", builtString);
    }

    @Test
    void buildF172022WhenNullKeyInMapThenThrowException() {
        // Given a map with a null key
        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        keyValuePairs.put(null, "someValue");

        // When building, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.buildF172022(keyValuePairs);
        });
        assertTrue(exception.getMessage().contains("Key or value cannot be null"));
    }

    @Test
    void buildF172022WhenNullValueInMapThenThrowException() {
        // Given a map with a null value
        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        keyValuePairs.put("someKey", null);

        // When building, then an IllegalArgumentException is thrown
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PostilionUtils.buildF172022(keyValuePairs);
        });
        assertTrue(exception.getMessage().contains("Key or value cannot be null"));
    }
}