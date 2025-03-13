package com.ssilvadev.annotationsAndExceptions.controllers;

import com.ssilvadev.annotationsAndExceptions.math.SimpleMath;
import com.ssilvadev.annotationsAndExceptions.request.converters.NumberConverter;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/math")
public class MathController {

    private SimpleMath math = new SimpleMath();

    @RequestMapping("/sum/{numberOne}/{numberTwo}")
    public Double sum(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) throws Exception {

        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/sub/{numberOne}/{numberTwo}")
    public Double sub(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {

        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.sub(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/mult/{numberOne}/{numberTwo}")
    public Double mult(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {

        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.mult(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/div/{numberOne}/{numberTwo}")
    public Double div(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) throws Exception {

        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }

        if (NumberConverter.convertToDouble(numberOne) <= 0 || NumberConverter.convertToDouble(numberTwo) <= 0) {
            throw new UnsupportedOperationException("Please set a numeric greater than zero");
        }
        return math.div(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/average/{numberOne}/{numberTwo}")
    public Double average(
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo) {

        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.average(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo)) / 2;
    }

    @RequestMapping("/squareroot/{numberOne}")
    public Double squareroot(
            @PathVariable("numberOne") String number) {

        if (!NumberConverter.isNumeric(number)) {
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.squareroot(NumberConverter.convertToDouble(number));
    }

}
