package com.springmock.tests.annotations;


import com.springmock.annotations.MultiValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestClass {

    @MultiValue("numbers")
    int[] primitiveArray;

    @MultiValue("numbers")
    Integer[] wrapperArray;

    @MultiValue("numbers")
    String[] stringArray;

    @MultiValue("numbers")
    Exception[] invalidArray;

    @MultiValue("numbersDouble")
    List<Double> wrapperList;

    @MultiValue("numbersSet")
    Set<Long> wrapperSet;

    @MultiValue("boolMap")
    Map<String, Boolean> wrapperMap;


}
