package com.springmock.components;

import com.springmock.annotations.Autowired;
import com.springmock.annotations.Component;

@Component
public class YetOneMoreComponent {

    @Autowired
    public SomeComponent someComponent;
}
