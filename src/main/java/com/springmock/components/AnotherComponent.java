package com.springmock.components;

import com.springmock.annotations.Component;
import com.springmock.annotations.Value;

@Component
public class AnotherComponent {


    @Value("HOME")
    public String homeDirectory;

    @Value("number")
    public Integer number;

    public AnotherComponent() {
    }
}
