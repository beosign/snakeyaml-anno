[![Build Status](https://travis-ci.org/beosign/snakeyaml-anno.svg?branch=development)](https://travis-ci.org/beosign/snakeyaml-anno)

[![Code Coverage](https://img.shields.io/codecov/c/github/pvorb/property-providers/develop.svg)](https://codecov.io/gh/beosign/snakeyaml-anno?branch=develop)

# snakeyaml-anno
Parse YAML files using Snakeyaml and annotations in POJOS.

## Features

### Property name mapping
If the properties of your POJO and in your yaml do not match regarding the names, you can supply a mapping by using the ```Property``` annotation.

Suppose you have the following yaml:

```javascript
- name: Homer
  lastname: Simpson
- name: Marge
  lastname: Simpson
```

And this is your POJO

```java
public class Person {
    private String firstName;
    private String lastName;
}
```

In order to be able to parse the yaml file, you can do the following:

```java
public class Person {
    @Property(key = "name")
    private String firstName;
    private String lastName;
}
```
   
It is also possible to annotate the getter instead:

```java 
 public class Person {     
    private String firstName;
    private String lastName;
  
    @Property(key = "name")
    public String getFirstName() {...}
}
```

### Converting
You can apply a converter to a field or getter. This feature is especially useful for converting enum values as Snakeyaml as of version 1.17 supports only basic converting, e.g. the string in the yaml file must match the enum constant definition in Java (uppercase).
The following example shows how to convert ```m``` to the enum constant ```MALE```:

```javascript
- name: Homer
  gender: m
- name: Marge
  gender: f
```

```java
public enum Gender {
    MALE("m"),
    FEMALE("f");

    private String abbr;

    private Gender(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbr() {
        return abbr;
    }
}
```

```java 
 public class Person {     
    private String name;
   
    @Property(converter=GenderConverter.class)
    private Gender gender;
}
```

```java 
public class GenderConverter implements Converter<Gender> {

    @Override
    public String convertToYaml(Gender modelValue) {
        return modelValue.getAbbr();
    }

    @Override
    public Gender convertToModel(Node yamlNode) {
        if (yamlNode instanceof ScalarNode) {
            for (Gender g : Gender.values()) {
                if (g.getAbbr().equals(((ScalarNode) yamlNode).getValue())) {
                    return g;
                }
            }
        }
        return null;
    }
}
```