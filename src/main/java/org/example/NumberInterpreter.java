package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

public class NumberInterpreter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a sequence of numbers separated by a space:");
        String input = scanner.nextLine();

        List<String> interpretedNumbers = interpretNumber(input);

        if ( interpretedNumbers.isEmpty()){
            System.out.println("Input Error");
        } else {
            IntStream.range(0, interpretedNumbers.size())
                    .forEach(index -> {
                        String e = interpretedNumbers.get(index);
                        if (isValidGreekNumber(e)) {
                            System.out.println("Interpretation " + index + ": " + e + " [phone number: VALID]");
                        } else {
                            System.out.println("Interpretation " + index + ": " + e + " [phone number: INVALID]");
                        }
                    });
        }
    }
    public static List<String> interpretNumber(String input) {
        List<String> interpretations = new ArrayList<>();
        int numOfInterpretations = 1;
        interpretations.add("");

        String[] numbers = input.split(" ");

        for (int i=0; i<numbers.length; i++){
            if (checkSize(numbers[i])) {
                int num = Integer.parseInt(numbers[i]);
                // before 20 there can be no ambiguity of what user said and what the NLU model understood because each number has a unique name.
                // After 20, each number can have 2 interpretations. e.g. 25 can either be 25 or 20 5 ( both are spoken the same ).
                if (num > 19 && num < 100) {
                    if (num % 10 == 0) { // e.g. 30 5 would become 305 or 35
                        if(i == (numbers.length - 1)){  // if it is the last number on the list, just add it
                            appendAmbiguousRows(interpretations,0,interpretations.size(),numbers[i]);
                        }else{
                            if (numbers[i + 1].length() == 1 && !Objects.equals(numbers[i + 1], "0")) {
                                numOfInterpretations = numOfInterpretations * 2;
                                addAmbiguousRows(interpretations,2);
                                String str1 = numbers[i].concat(numbers[i+1]);
                                String str2 = numbers[i].substring(0,1).concat(numbers[i+1]);
                                appendAmbiguousRows(interpretations,0,interpretations.size()/2,str1);
                                appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size(),str2);
                                i++;
                            }else{
                                appendAmbiguousRows(interpretations,0,interpretations.size(),numbers[i]);
                            }
                        }
                    } else { // e.g. 15 4 would become 154 or 1054
                        numOfInterpretations = numOfInterpretations * 2;
                        addAmbiguousRows(interpretations,2);
                        for (int k = 0; k < numOfInterpretations; k++) {
                            if (k < numOfInterpretations / 2) {
                                interpretations.set(k, interpretations.get(k).concat(numbers[i]));
                            } else {
                                String part_1 = Integer.toString((num / 10) * 10);
                                String part_2 = Integer.toString((num % 10));
                                interpretations.set(k, interpretations.get(k).concat(part_1).concat(part_2));
                            }
                        }
                    }
                } else if (num >= 100) {
                    if (num % 100 == 0 && num % 10 == 0) { // e.g. 200
                        if(i == (numbers.length - 1)){ // if it is the last number on the list, just add it
                            appendAmbiguousRows(interpretations,0,interpretations.size(),numbers[i]);
                        } else {
                            if (numbers[i + 1].length() == 1) {
                                // e.g. 200 5 would become 2005 or 205
                                numOfInterpretations = numOfInterpretations * 2;
                                addAmbiguousRows(interpretations,2);
                                String str1 = numbers[i].concat(numbers[i+1]);
                                String str2 = numbers[i].substring(0,2).concat(numbers[i+1]);
                                appendAmbiguousRows(interpretations,0,interpretations.size()/2,str1);
                                appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size(),str2);
                                i++;
                            } else if (numbers[i + 1].length() == 2) {
                                // e.g. 700 24 would become 70024 or 7204 or 724 or 700204
                                numOfInterpretations = numOfInterpretations * 4;
                                addAmbiguousRows(interpretations,4);
                                String str1 = numbers[i].concat(numbers[i+1]); // 70024
                                String str2 = numbers[i].substring(0,1).concat(numbers[i+1]); // 724
                                String str3 = numbers[i].substring(0,1).concat(numbers[i+1].substring(0,1).concat("0").concat(numbers[i+1].substring(1,2))); // 7204
                                String str4 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(0,1)).concat("0").concat(numbers[i].substring(1,2)); // 700204
                                appendAmbiguousRows(interpretations,0,interpretations.size()/4,str1);
                                appendAmbiguousRows(interpretations,interpretations.size()/4,interpretations.size()/2,str2);
                                appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size() * 3/4,str3);
                                appendAmbiguousRows(interpretations,interpretations.size() * 3/4,interpretations.size(),str4);
                                i++;
                            }
                        }
                    } else if (num % 100 != 0 && num % 10 == 0) { // e.g. 240
                        if(i == (numbers.length - 1)){
                            // e.g. 39 49 240 ( last number of the numbers array)
                            numOfInterpretations = numOfInterpretations * 2;
                            addAmbiguousRows(interpretations,2);
                            String str1 = numbers[i]; // 240
                            String str2 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,3)); // 20040
                            appendAmbiguousRows(interpretations,0,interpretations.size()/2,str1);
                            appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size(),str2);
                        } else {
                            if (numbers[i + 1].length() == 1) {
                                // e.g. 240 5
                                numOfInterpretations = numOfInterpretations * 4;
                                addAmbiguousRows(interpretations,4);
                                String str1 = numbers[i].concat(numbers[i+1]); // 2405
                                String str2 = numbers[i].substring(0,2).concat(numbers[i+1]); // 245
                                String str3 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,2).concat(numbers[i+1])); // 20045
                                String str4 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,3).concat(numbers[i+1])); // 200405
                                appendAmbiguousRows(interpretations,0,interpretations.size()/4,str1);
                                appendAmbiguousRows(interpretations,interpretations.size()/4,interpretations.size()/2,str2);
                                appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size() * 3/4,str3);
                                appendAmbiguousRows(interpretations,interpretations.size() * 3/4,interpretations.size(),str4);
                                i++;
                            }else{
                                // e.g. 240 55
                                numOfInterpretations = numOfInterpretations * 2;
                                addAmbiguousRows(interpretations,2);
                                String str1 = numbers[i]; // 24055
                                String str2 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,3)); //2004055
                                appendAmbiguousRows(interpretations,0,interpretations.size()/2,str1);
                                appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size(),str2);
                            }
                        }
                    } else if (num % 10 != 0) {
                        // e.g. 243
                        numOfInterpretations = numOfInterpretations * 4;
                        addAmbiguousRows(interpretations,4);
                        String str1 = numbers[i]; // 243
                        String str2 = numbers[i].substring(0,2).concat("0").concat(numbers[i].substring(2,3)); // 2403
                        String str3 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,3)); // 20043
                        String str4 = numbers[i].substring(0,1).concat("00").concat(numbers[i].substring(1,2).concat("0")).concat(numbers[i].substring(2,3)); //200403
                        appendAmbiguousRows(interpretations,0,interpretations.size()/4,str1);
                        appendAmbiguousRows(interpretations,interpretations.size()/4,interpretations.size()/2,str2);
                        appendAmbiguousRows(interpretations,interpretations.size()/2,interpretations.size() * 3/4,str3);
                        appendAmbiguousRows(interpretations,interpretations.size() * 3/4,interpretations.size(),str4);
                    }
                } else {
                    appendAmbiguousRows(interpretations,0,interpretations.size(),numbers[i]);
                }
            } else {
                System.out.println("Invalid input: " + numbers[i] + " is not a valid number (should be integers, up to three digits).");
                return new ArrayList<>();
            }
        }

        return interpretations;
    }

    private static boolean checkSize(String number) {
        return number.length() <= 3 && number.matches("\\d+");
    }

    private static boolean isValidGreekNumber(String number) {
        if (number.length() == 10 && (number.startsWith("2") || number.startsWith("69"))){
            return true;
        } else if (number.length() == 14 && (number.startsWith("00302") || number.startsWith("003069"))){
            return true;
        } else {
            return false;
        }
    }

    private static void addAmbiguousRows(List<String> originalList,int multiplier) {
        int originalSize = originalList.size();
        for (int j=0; j< multiplier-1; j++) {
            for (int i = 0; i < originalSize; i++) {
                String element = originalList.get(i);
                originalList.add(element);
            }
        }
    }

    private static void appendAmbiguousRows(List<String> originalList,int start,int end,String appendedString){
        for (int i=start; i < end; i++){
            originalList.set(i, originalList.get(i).concat(appendedString));
        }
    }
}
