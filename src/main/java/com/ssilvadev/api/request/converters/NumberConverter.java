package com.ssilvadev.api.request.converters;

public class NumberConverter {
    public static boolean isNumeric(String strNumber) {
        if (strNumber == null || strNumber.isEmpty()) {
            return false;
        }

        String number = strNumber.replace(",", ".");

        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

    public static Double convertToDouble(String strNumber) throws IllegalArgumentException {
        if (strNumber == null || strNumber.isEmpty()) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }

        return Double.parseDouble(strNumber);
    }
}
