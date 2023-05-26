package com.springmock.tests.cyclic;

import com.springmock.annotations.Autowired;
import com.springmock.annotations.Component;

@Component
public class One {
    @Autowired
    Two two;
}
