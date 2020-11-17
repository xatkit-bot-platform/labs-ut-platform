package com.xatkit.plugins.messenger.platform;

import java.util.*;

//A feature nobody would actually use
/*
 * This machine converts text to look like it was typed fast, occasionally making errors.
 */
public class TextNaturalizer {
    private float mistakeRate;
    private static TextNaturalizer textNaturalizer;
    private final List<char[]> rows;

    private TextNaturalizer() {
        mistakeRate = 0.02f;
        rows = new ArrayList<>();
        char[] row0 = {'`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '='};
        rows.add(row0);
        char[] row1 = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']'};
        rows.add(row1);
        char[] row2 = {'a', 's', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '#'};
        rows.add(row2);
        char[] row3 = {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'};
        rows.add(row3);
        char[] row0s = {'¬', '!', '"', '£', '$', '%', '^', '&', '*', '(', ')', '_', '+'};
        rows.add(row0s);
        char[] row1s = {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', '{', '}'};
        rows.add(row1s);
        char[] row2s = {'A', 'S', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', ':', '@', '~'};
        rows.add(row2s);
        char[] row3s = {'Z', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'};
        rows.add(row3s);
    }

    public static TextNaturalizer get() {
        if (textNaturalizer == null) {
            textNaturalizer = new TextNaturalizer();
        }
        return textNaturalizer;
    }

    private boolean contains(char[] chars, char c) {
        for(int i = 0; i < chars.length; i++) { if (chars[i] == c) return true; }
        return false;
    }

    private int findIndex(char[] chars, char c) {
        for(int i = 0; i < chars.length; i++) { if (chars[i] == c) return i; }
        return -1;
    }

    private char[] findRow(char c) {
        for(char[] row : rows) {
            if (contains(row,c)) return row;
        }
        return null;
    }

    private char mistakeChar(char c) {
        char n = c;
        if (new Random().nextFloat() > mistakeRate) return n;

        char[] row = findRow(c);
        if (row == null) return n;

        int i = findIndex(row,c);
        boolean left = new Random().nextBoolean();

        if (left && i-1 >= 0 && i-1 < row.length) {
            n = row[i-1];
        } else if (!left && i+1 >= 0 && i+1 < row.length) {
            n = row[i+1];
        }

        return n;
    }

    public String naturalize(String text) {
        char[] chars = text.toLowerCase().toCharArray();

        Queue<Character> skippedCharacters = new LinkedList<>();
        StringBuilder sb = new StringBuilder();

        char c;
        for (int i = 0; i < chars.length; i++) {
            c = mistakeChar(chars[i]);
            if (new Random().nextFloat() <= mistakeRate) {continue;}
            if (new Random().nextFloat() <= mistakeRate) {
                skippedCharacters.add(c);
                if (new Random().nextBoolean()) continue;
            }
            if (new Random().nextFloat() > mistakeRate && !skippedCharacters.isEmpty()) {sb.append(skippedCharacters.remove());}
            sb.append(c);
        }

        while (!skippedCharacters.isEmpty()) {sb.append(skippedCharacters.remove());}

        return sb.toString();
    }
}
