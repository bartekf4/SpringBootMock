package com.springmock.components;

import com.springmock.annotations.Autowired;
import com.springmock.annotations.Component;
import com.springmock.annotations.MultiValue;

import java.util.List;

@Component
public class SomeComponent {

    @MultiValue("numbers")
    public List<Integer> numbers;
    public AnotherComponent anotherComponent;

    public SomeComponent(@Autowired AnotherComponent anotherComponent) {
        this.anotherComponent = anotherComponent;
    }

    public SomeComponent() {
    }

}
