package org.example;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumberInterpreterTest {

    @Test
    void testInterpretNumber() {
        String input1 = "2 10 6 9 30 6 6 4";
        List<String> expected1 = Arrays.asList("2106930664","210693664");
        assertEquals(expected1, NumberInterpreter.interpretNumber(input1));

        String input2 = "2 10 69 30 6 6 4";
        List<String> expected2 = Arrays.asList("2106930664", "21060930664","210693664","2106093664");
        assertEquals(expected2, NumberInterpreter.interpretNumber(input2));
    }
}