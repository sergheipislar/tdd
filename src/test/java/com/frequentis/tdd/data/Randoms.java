package com.frequentis.tdd.data;

import java.util.Random;

public final class Randoms {
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String FIGURES = "0123456789";
    private static final int DEFAULT_NUMBER = 5;

    public static Long randomLong(){
        return Long.parseLong(randomNumeric());
    }

    public static String randomNumeric(){
        return randomNumeric(DEFAULT_NUMBER);
    }

    public static String randomNumeric(final int number){
        return generateRandomFromString(number, FIGURES);
    }

    public static String randomAlphabetic(){
        return randomAlphabetic(DEFAULT_NUMBER);
    }

    public static String randomAlphabetic(final int number){
        return generateRandomFromString(number, LETTERS);
    }

    public static String randomAlphabetic(final String prefix){
        return prefix + randomAlphabetic(DEFAULT_NUMBER);
    }

    public static String randomAlphabetic(final int number, final String prefix){
        return prefix + randomAlphabetic(number);
    }

    public static String randomAlphanumeric(final int number){
        return generateRandomFromString(number, LETTERS+FIGURES);
    }

    public static String randomAlphanumeric(final String prefix){
        return prefix + randomAlphanumeric(DEFAULT_NUMBER);
    }

    public static String randomAlphanumeric(final int number, final String prefix){
        return prefix + randomAlphanumeric(number);
    }

    private static String generateRandomFromString(final int number, final String sequence) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< number; i++){
            sb.append(sequence.charAt(random.nextInt(sequence.length())));
        }

        return sb.toString();
    }
}
