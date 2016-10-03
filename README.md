[![Build Status](https://travis-ci.org/beosign/snakeyaml-anno.svg?branch=development)](https://travis-ci.org/beosign/snakeyaml-anno)

[![Code Coverage](https://codecov.io/gh/beosign/snakeyaml-anno/branch/development/graph/badge.svg)](https://codecov.io/gh/beosign/snakeyaml-anno?branch=develop)

# snakeyaml-anno
Parse YAML files by using annotation in POJOS - based on SnakeYaml.

## Features Overview
* Property name mapping
* Converting
* Ignore parsing errors in a subtree
* Auto type detection

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

### Ignore parsing errors
In a complex hierarchy it may be desirable to ignore parse errors in a given subtree and still return the parsed objects higher up the tree. In case of an exception, the unparsable object will simply remain ```null```. To allow the parsing process to skip unparsable parts instead of aborting, you can use ```ignoreExceptions = true``` on a property or a getter:

```java 
 public class Person {     
    private String firstName;
    private String lastName;
    
    @Property(ignoreExceptions = true)
    private Gender gender;
  
}
```

So in case the gender property cannot be parsed, you still get a parsed Person object, just with the gender property being ```null```.

### Auto type detection
YAML uses the concept of _Tags_ to provide type information. However, to keep the YAML file as simple and concise as possible, it may be desirable to omit a tag declaration if the concrete type to use can already be deducted from the properties. Suppose you have the following interface:

```java 
public interface Animal {     
    String getName();  
}
```

And two implementations:

```java 
public class Dog implements Animal {     
   private int nrOfBarksPerDay;  
   ...
}
```

```java 
public class Cat implements Animal {     
   private int miceCaughtCounter;  
   ...
}
```

And the container:
```java 
public class Person {     
   private List<Animal> pets;  
   ...
}
```

For the following YAML, the first object in the list must be of type ```Dog``` and the second of type ```Cat```.

```javascript
pets:
- name: Santas Little Helper
  nrOfBarksPerDay: 20
- name: Snowball
  miceCaughtCounter: 20
```

To tell YAML to autodetect the types, you have to annotate the ```Animal``` interface with ```@Type(substitutionTypes = { Dog.class, Cat.class })```.

So you must provide possible subtypes because classpath scanning of classes implementing a given interface is not yet implemented.
If no valid substitution class if found, the default SnakeYaml algorithm for choosing the type will be used. If multiple substitution types are possible, the first possible type (determined by the order of the classes in ```substitutionTypes```) is chosen.

You can also provide your own ```SubstitutionTypeSelector``` implementation and set it with ```@Type(substitutionTypes = { Dog.class, Cat.class }, substitutionTypeSelector = MyTypeSelector.class)```.  ```MyTypeSelector``` must implement ```SubstitutionTypeSelector``` and must have a no-arg constructor.
 