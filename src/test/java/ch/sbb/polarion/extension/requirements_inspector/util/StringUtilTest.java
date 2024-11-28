package ch.sbb.polarion.extension.requirements_inspector.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilTest {

    final String TEST_STRING = "test,test1,test2,test3";

    @Test
    void testRemoveSeparatorChars() {
        String expectedString = "test test1 test2 test3";

        String resultString = StringUtil.removeSeparatorChars(TEST_STRING, Consts.SEPARATOR);

        assertEquals(expectedString, resultString);
    }

    @Test
    void testReplaceSeparatorChars() {
        String expectedString = "test;test1;test2;test3";

        String resultString = StringUtil.replaceSeparatorChars(TEST_STRING, Consts.SEPARATOR, ";");

        assertEquals(expectedString, resultString);
    }

    @Test
    void testStringToList() {
        String[] expectedStrings = TEST_STRING.split(Consts.SEPARATOR);

        List<String> resultList = StringUtil.stringToList(TEST_STRING, Consts.SEPARATOR);

        for (int i = 0; i < resultList.size(); i++) {
            assertEquals(expectedStrings[i], resultList.get(i));
        }
    }

}
