package com.ssilvadev.api.math;

public class SimpleMath {

    public Double sum(Double numberOne, Double numberTwo) {

        return numberOne + numberTwo;
    }

    public Double sub(Double numberOne, Double numberTwo) {

        return numberOne - numberTwo;
    }

    public Double mult(Double numberOne, Double numberTwo) {

        return numberOne * numberTwo;
    }

    public Double div(Double numberOne, Double numberTwo) throws Exception {

        if (numberOne <= 0 || numberTwo <= 0) {
            throw new UnsupportedOperationException("Please set a numeric greater than zero");
        }

        return numberOne / numberTwo;
    }

    public Double average(Double numberOne, Double numberTwo) {

        return (numberOne + numberTwo) / 2;
    }

    public Double squareroot(Double number) {

        return Math.sqrt(number);
    }
}
