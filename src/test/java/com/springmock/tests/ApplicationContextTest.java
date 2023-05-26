package com.springmock.tests;

import com.springmock.ApplicationContext;
import com.springmock.components.AnotherComponent;
import com.springmock.components.SomeComponent;
import com.springmock.components.YetOneMoreComponent;
import com.springmock.exceptions.CyclicDependencyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

class ApplicationContextTest {

    static ApplicationContext ctx;

    @BeforeAll
    static void beforeAll() {
        ctx = new ApplicationContext("com.springmock.components");
    }

    @Test
    void getBean() {
        SomeComponent someComponentOne = ctx.getBean(SomeComponent.class);
        SomeComponent someComponentTwo = ctx.getBean(SomeComponent.class);
        Assertions.assertEquals(Objects.hashCode(someComponentOne), Objects.hashCode(someComponentTwo));
    }

    @Test
    void cyclic() {
        Assertions.assertThrows(CyclicDependencyException.class, () -> new ApplicationContext("com.springmock.tests.cyclic"));
    }

    @Test
    void getBeanByName() {
        Object bean = ctx.getBeanByName("SomeComponent");
        Assertions.assertNotNull(bean);
        Assertions.assertSame(bean, ctx.getBean(SomeComponent.class));
    }

    @Test
    void getBeans() {
        Map<Class<?>, Object> beans = ctx.getBeans();
        System.out.println(beans);
        Assertions.assertEquals(beans.size(), 3);
        Assertions.assertEquals(Set.of(SomeComponent.class, AnotherComponent.class, YetOneMoreComponent.class), beans.keySet());
    }

    @Test
    void autowiredField() {
        YetOneMoreComponent yetOneMoreComponent = ctx.getBean(YetOneMoreComponent.class);
        SomeComponent someComponent = yetOneMoreComponent.someComponent;
        Assertions.assertNotNull(someComponent);
        Assertions.assertSame(someComponent, ctx.getBean(SomeComponent.class));

    }

    @Test
    void autowiredConstructor() {
        SomeComponent someComponent = ctx.getBean(SomeComponent.class);
        AnotherComponent anotherComponent = someComponent.anotherComponent;
        Assertions.assertNotNull(anotherComponent);
        Assertions.assertSame(anotherComponent, ctx.getBean(AnotherComponent.class));
    }

    @Test
    void valueString() {
        AnotherComponent anotherComponent = ctx.getBean(AnotherComponent.class);
        Assertions.assertEquals(anotherComponent.homeDirectory, System.getenv("HOME"));
    }

    @Test
    void valueInteger() {
        AnotherComponent anotherComponent = ctx.getBean(AnotherComponent.class);
        Assertions.assertEquals(anotherComponent.number, 1);
    }


}