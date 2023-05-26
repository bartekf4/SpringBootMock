package com.springmock.tests.annotations;

import com.springmock.annotations.resolvers.MultiValueResolver;
import com.springmock.exceptions.IllegalTypeException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiValueResolverTest {

    @Test
    public void resolve_arrayPrimitives() throws NoSuchFieldException {

        @NotNull Field field = TestClass.class.getDeclaredField("primitiveArray");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof int[]);
        int[] array = (int[]) result;
        assertArrayEquals(new int[]{1, 2, 3}, array);
    }

    @Test
    public void resolve_arrayWrappers() throws NoSuchFieldException {
        @NotNull Field field = TestClass.class.getDeclaredField("wrapperArray");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof Integer[]);
        Integer[] wrapperArray = (Integer[]) result;
        assertArrayEquals(new Integer[]{1, 2, 3}, wrapperArray);
    }

    @Test
    public void resolve_arrayStrings() throws NoSuchFieldException {
        @NotNull Field field = TestClass.class.getDeclaredField("stringArray");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof String[]);
        String[] wrapperArray = (String[]) result;
        assertArrayEquals(new String[]{"1", "2", "3"}, wrapperArray);
    }

    @Test
    public void resolve_arrayInvalidType() throws NoSuchFieldException {
        @NotNull Field field = TestClass.class.getDeclaredField("invalidArray");
        try {
            MultiValueResolver.resolve(field);
        } catch (IllegalTypeException ignored) {
        }

    }


    @Test
    public void resolve_listWrappers() throws NoSuchFieldException {
        Field field = TestClass.class.getDeclaredField("wrapperList");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof List);
        List<Double> wrapperList = (List<Double>) result;
        assertEquals(Arrays.asList(1.5, 2.5, 3.5), wrapperList);
    }

    @Test
    public void testResolve_Set() throws NoSuchFieldException {
        Field field = TestClass.class.getDeclaredField("wrapperSet");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof Set);
        Set<Long> set = (Set<Long>) result;
        assertEquals(new HashSet<>(Arrays.asList(1L, 2L, 3L)), set);
    }

    @Test
    public void testResolve_Map() throws NoSuchFieldException {
        Field field = TestClass.class.getDeclaredField("wrapperMap");
        Object result = MultiValueResolver.resolve(field);
        assertTrue(result instanceof Map);
        Map<String, Boolean> map = (Map<String, Boolean>) result;
        assertEquals(new HashMap<>(
                Map.of(
                        "true", true,
                        "false", false
                )), map);


    }

}