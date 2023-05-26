package com.springmock;

import com.springmock.annotations.Autowired;
import com.springmock.annotations.Component;
import com.springmock.annotations.MultiValue;
import com.springmock.annotations.Value;
import com.springmock.annotations.resolvers.MultiValueResolver;
import com.springmock.annotations.resolvers.ValueResolver;
import com.springmock.exceptions.UnableToCreateBeanException;
import com.springmock.exceptions.UnableToSetValueException;
import static java.util.Comparator.comparingLong;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * <p>The {@link ApplicationContext} class is responsible for creating and managing the beans of the application. </p>
 * <p>It uses the {@link Component} annotation to scan the package specified in its constructor to discover the beans
 * and then creates them using the constructor with the most dependencies that can be satisfied.</p>
 * <p>The {@link ApplicationContext} injects dependencies into fields annotated with the {@link Autowired} annotation.
 * Finally, it sets the values of fields annotated with the {@link Value} and {@link MultiValue} annotation with the
 * specified value of environment variable.</p>
 */

public class ApplicationContext {
    private final String packageName;
    private final Map<Class<?>, Object> beans;


    /**
     * Creates a new {@link ApplicationContext} that scans the package of the class.
     */
    public ApplicationContext() {
        this(ApplicationContext.class.getPackageName());
    }


    /**
     * Creates a new {@link ApplicationContext} that scans the specified package.
     *
     * @param packageName the package to scan for beans
     */
    public ApplicationContext(String packageName) {
        this.packageName = packageName;
        List<Class<?>> orderedBeans = scan();
        this.beans = createBeans(orderedBeans);
        populate();
    }

    /**
     * Gets the dependencies of the specified bean.
     *
     * @param clazz the bean
     * @param beans the list of beans in the application
     * @return the list of dependencies of the bean
     */
    static Set<Class<?>> getDependenciesOfBean(Class<?> clazz, Set<Class<?>> beans) {
        Set<Class<?>> dependencies = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> fieldType = field.getType();
                if (beans.contains(fieldType)) {
                    dependencies.add(fieldType);
                }
            }
        }
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) { //skip default constructor
                continue;
            }
            for (Parameter parameter : constructor.getParameters()) {
                Class<?> parameterType = parameter.getType();
                if (beans.contains(parameterType)) {
                    dependencies.add(parameterType);
                }
            }
        }
        return dependencies;
    }

    /**
     * Gets the bean of the specified type.
     *
     * @param <T>   the type parameter
     * @param clazz the type of the bean to get
     * @return the bean object
     */
    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }

    /**
     * Gets the map of beans that have been created.
     *
     * @return the map of beans
     */
    public Map<Class<?>, Object> getBeans() {
        return beans;
    }

    /**
     * Gets the bean with the specified name.
     *
     * @param name the name of the bean to get
     * @return the bean
     */

    public Object getBeanByName(String name) {
        return beans.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(o -> o.getKey().getSimpleName(),
                                Map.Entry::getValue))
                .get(name);
    }

    /**
     * Gets the number of parameters of the specified constructor that can be injected.
     *
     * @param constructor the constructor
     * @return the number of parameters that can be injected
     */
    private long getNumberParamsThatCanBeInjected(Constructor<?> constructor, List<Class<?>> beansList) {
        return Arrays.stream(constructor.getParameters())
                .filter(o -> beansList.contains(o.getType())).count();
    }

    /**
     * Creates the beans by finding the constructor with the most dependencies that can be satisfied and then creating
     * an instance of the bean using that constructor.
     *
     * @return Map of beans, where K is
     */
    private Map<Class<?>, Object> createBeans(List<Class<?>> orderedBeans) {
        //this map is needed to keep the beans that will be used in constructor of another bean via injection,
        //because it is not possible to get the this.beans since it is null
        Map<Class<?>, Object> beans = new HashMap<>();
        orderedBeans
                .forEach(clazz -> {
                    try {
                        Constructor<?> constructor = Arrays.stream(clazz.getConstructors())
                                .max(comparingLong(ctor -> getNumberParamsThatCanBeInjected(ctor, orderedBeans)))
                                .orElseThrow();
                        Object[] params = Arrays.stream(constructor.getParameters())
                                .map(Parameter::getType)
                                .map(beans::get)
                                .toArray();
                        beans.put(clazz, constructor.newInstance(params));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new UnableToCreateBeanException(e.getMessage());
                    }
                });
        return beans;

    }


    /**
     * Scans the package specified in the constructor for classes annotated with {@link Component}.
     *
     * @return A list of classes annotated with {@link Component} sorted topologically {@link TopologicalSort} according
     * to their dependencies.
     */
    private List<Class<?>> scan() {
        Reflections reflections = new Reflections(this.packageName);
        Set<Class<?>> components = reflections.getTypesAnnotatedWith(Component.class);
        Map<Class<?>, Set<Class<?>>> graph = components.stream()
                .collect(Collectors.toMap(Function.identity(), x -> getDependenciesOfBean(x, components)));
        return TopologicalSort.getSorted(graph);
    }

    /**
     * Populates the fields of the beans with the appropriate values or dependencies. This method will initialize the
     * fields annotated with the {@link Autowired}, {@link Value} and {@link MultiValue} annotations.
     */
    private void populate() {
        populateAutowiredFields();
        populateValuesFields();
        populateMultiValuesFields();
    }

    /**
     * Populates the dependencies of the beans by injecting the beans into the fields annotated with the
     * {@link Autowired} annotation.
     */
    private void populateAutowiredFields() {
        populateFields(field -> beans.get(field.getType()), (beanClass, field) -> field.isAnnotationPresent(Autowired.class) && beanClass != field.getType());
    }

    /**
     * Sets the values of fields annotated with the {@link Value} annotation with the specified value of environment
     * variable.
     */
    private void populateValuesFields() {
        populateFields(ValueResolver::resolve, (beanClass, field) -> field.isAnnotationPresent(Value.class));
    }

    /**
     * Sets the values of fields annotated with the {@link MultiValue} annotation with the specified value of
     * environment variable.
     */
    private void populateMultiValuesFields() {
        populateFields(MultiValueResolver::resolve, (beanClass, field) -> field.isAnnotationPresent(MultiValue.class));
    }


    /**
     * Populate the fields of the beans that meet the given condition.
     *
     * @param valueResolver A function that takes a field as input and returns the value that should be set.
     * @param condition     A BiPredicate that takes the bean class and a field as input and returns true if the field
     *                      should be processed, and false otherwise.
     */
    private void populateFields(Function<Field, Object> valueResolver, BiPredicate<Class<?>, Field> condition) {
        for (var entry : beans.entrySet()) {
            Class<?> beanClass = entry.getKey();
            Object bean = entry.getValue();
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (condition.test(beanClass, field)) {
                    Object value = valueResolver.apply(field);
                    try {
                        field.setAccessible(true);
                        field.set(bean, value);
                    } catch (IllegalAccessException e) {
                        throw new UnableToSetValueException(e.getMessage());
                    }
                }
            }
        }
    }
}


