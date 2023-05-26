# Spring Mock

Spring Mock is a project that aims to implement a simplified version of Spring's dependency injection container using
the Reflection API. This implementation supports singleton beans, field injection, and annotation-based configuration
similar to Spring's `@Component`, `@Autowired`, and `@Value` annotations.

## Features

- **Custom Annotations:** The project provides a set of custom annotations similar to Spring's annotations. These
  include
    - `@Component`, `@Autowired`, and `@Value`.
    - `ApplicationContext`: The ApplicationContext class is responsible for managing the dependency injection
      container. It
      can
      be instantiated with a default constructor or a constructor accepting a string package name for scanning the
      specified
      package or the entire classpath.
- **Component Scanning:** The ApplicationContext scans the specified package or the whole classpath (if the default
  constructor is used) to find classes marked with the `@Component` annotation. It assumes that all
  components should have a default constructor and only supports field injection.
- **Dependency Resolution:** For each component, the ApplicationContext identifies its dependencies by looking for fields
  marked with the `@Autowired` annotation. It then builds the correct sequence of component
  initialization, ensuring that dependencies are initialized first. If a cyclic dependency is detected, an exception is
  thrown.
- **Value Injection:** The `@Value` annotation is used to inject values from environment variables into fields of a class.
  The
  value is treated as the value of the corresponding environment variable. Supported types include String, primitive
  types, wrapper types, and their arrays. If the value cannot be cast to the specified type, an exception is thrown.
- **Constructor Injection:** The project optionally supports constructor injection alongside field injection. If a
  class has a single non-default constructor, the ApplicationContext attempts to find suitable dependencies for the
  constructor parameters.
- **MultiValue Annotation:** An optional `@MultiValue` annotation can be used to parse collection types from
  environment variable values. Supported collection types include arrays, Lists, Sets, and Maps of wrapper types or
  Strings.

