[![Build Status](https://travis-ci.org/beosign/snakeyaml-anno.svg?branch=development)](https://travis-ci.org/beosign/snakeyaml-anno)

[![codecov](https://codecov.io/gh/beosign/snakeyaml-anno/branch/development/graph/badge.svg)](https://codecov.io/gh/beosign/snakeyaml-anno?branch=development)


# snakeyaml-anno
Parse YAML files by using annotation in POJOS - based on SnakeYaml **1.19** by Sergey Pariev, https://github.com/spariev/snakeyaml/.

## Compatibility

<table class="tg">
  <tr>
    <th class="tg-us36">snakeyaml-anno | SnakeYaml<br></th>
    <th class="tg-us36">1.17</th>
    <th class="tg-us36">1.18<br></th>
    <th class="tg-us36">1.19</th>
    <th class="tg-us36">1.20</th>
  </tr>
  <tr>
    <td class="tg-us36">0.3.0<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
  </tr>
  <tr>
    <td class="tg-us36">0.4.0</td>
    <td class="tg-us36">NO<br></td>
    <td class="tg-us36">NO<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.5.0</td>
    <td class="tg-us36">NO<br></td>
    <td class="tg-us36">NO<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
</table>

## Usage
You must use the ```AnnotationAwareConstructor``` when parsing:

```java
Yaml yaml = new Yaml(new AnnotationAwareConstructor(MyRoot.class));
```

You must use the ```AnnotationAwareRepresenter``` when dumping:

```java
Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
```

## Features

### Quick Overview

* Property name mapping
* Converting
* Case insensitive parsing
* Ignore parsing errors in a subtree
* Auto type detection
* Skipping properties
* Ordering properties

### Feature Details

This section covers the details of the feature including examples how to use them. 

#### Property name mapping
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

#### Converting
You can apply a converter to a field or getter. This feature is especially useful for converting enum values as Snakeyaml as of version 1.17 supports only basic converting, e.g. the string in the yaml file must match the enum constant definition in Java (uppercase).
The following example shows how to convert ``m`` to the enum constant ``MALE``:

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
    public Gender convertToModel(String value) {
        for (Gender g : Gender.values()) {
            if (g.getAbbr().equals(value)) {
                 return g;
            }
        }
        
        return null;
    }
}
```

As of version 0.4.0, conversion is also implemented for dumping. The interface has changed; the ``convertToModel`` method now takes a ``String`` as parameter instead of ``Node``.

#### Case insensitive parsing
A flag can be passed so that parsing is possible even if the keys in the yaml file do not match the case of the java property where it sould be parsed into. To enable it, use ``AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class, true)``.
So for example, all of the following variants can be parsed using the same Person class (see above):

```javascript
  Name: Homer
  nAME: Marge
  NaMe: Bart
  name: Lisa
```

In the very unlikely case that a Java Bean class contains two properties that differ only in case, the result which property is used is undetermined.

#### Ignore parsing errors
In a complex hierarchy it may be desirable to ignore parse errors in a given subtree and still return the parsed objects higher up the tree. In case of an exception, the unparsable object will simply remain ``null``. To allow the parsing process to skip unparsable parts instead of aborting, you can use ``ignoreExceptions = true`` on a property or a getter:

```java 
 public class Person {     
    private String firstName;
    private String lastName;
    
    @Property(ignoreExceptions = true)
    private Gender gender;
  
}
```

So in case the gender property cannot be parsed, you still get a parsed Person object, just with the gender property being ``null``.

#### Auto type detection
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

For the following YAML, the first object in the list must be of type ``Dog`` and the second of type ``Cat``.

```javascript
pets:
- name: Santas Little Helper
  nrOfBarksPerDay: 20
- name: Snowball
  miceCaughtCounter: 20
```

You have two ways of telling YAML to autodetect the types: annotation based or programmatic. In either way, use must provide possible subtypes for a given supertype because classpath scanning of classes implementing a given interface is not yet implemented.
If no valid substitution class if found, the default SnakeYaml algorithm for choosing the type will be used. If multiple substitution types are possible, the first possible type (determined by the order of the classes in ``substitutionTypes``) is chosen. You can also provide your own ``SubstitutionTypeSelector`` implementation and set it with ``@Type(substitutionTypes = { Dog.class, Cat.class }, substitutionTypeSelector = MyTypeSelector.class)``.  ``MyTypeSelector`` must implement ``SubstitutionTypeSelector`` and must have a no-arg constructor.

##### Annotation-based
To tell YAML to autodetect the types, you have to annotate the ``Animal`` interface with ``@Type(substitutionTypes = { Dog.class, Cat.class })``. Optionally, you can register a ``SubstitutionTypeSelector``.

##### Programmatic
Before loading, you can register a mapping from an interface to ``@Type`` annotation instance. Optionally, you can modify and/or remove any mappings that have already been determined by evaluating the annotations.

```java
 AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(BaseClassOrInterface.class);

// optionally override given entries that have been inserted by evaluating annotations
constructor.getTypesMap().clear();

// register type programmatically
// this has the same effect as putting an annotation of the form @Type(substitutionTypes = {Concrete1.class, Concrete2.class} on BaseClassOrInterface class
constructor.getTypesMap().put(BaseClassOrInterface.class, new TypeImpl(new Class<?>[] { Concrete1.class, Concrete2.class }));

Yaml yaml = new Yaml(constructor);
```

#### Skipping properties
It is possible to skip properties during load or dump. In order to skip a property during load, thus preventing snakeyaml to override a model value with the value read from the yaml file that is being loaded, annotate the property with ``skipAtLoad``:

```java 
public class Person {

   @Property(skipAtLoad = true)     
   private String name;
}
```

In order to prevent dumping of a property, use ``skipAtDump``:
 
```java 
public class Person {

   @Property(skipAtDump = true)     
   private String name;
}
```

You can also skip dumping conditionally by implementing the ``SkipAtDumpPredicate`` interface. The only method to implement is ``skip``. One of the parameters passed into this method is the property value, so you can make decisions whether to skip a property based on its value.
You can use your implementation by using the ``skipAtDumpIf`` member:
 
```java 
public class Person {

   @Property(skipAtDumpIf = SkipIfNull.class)     
   private String name;
}
```

**Be aware** that if ``skipAtDump`` is also supplied and set to true, it will take precedence over the `` skipAtDumpIf``!

Predefined are two classes: ``SkipIfNull`` and ``SkipIfEmpty``. The first one skips a property if it is ``null``, the latter one skips a property if it is of type ``Map``, ``Collection`` or ``String`` and the property is empty (empty map/collection or String of length 0).

#### Ordering properties
It is possible to order properties during the dump process by providing a value for the ``order`` property:

```java 
public class Person {

   @Property(order = 5)     
   private String first;

     
   private String between;

   @Property(order = -5)     
   private String last;
}
```

This will dump the properties in the order:

1. first
2. between
3. last

The default value for ``order`` is 0. A higher value means that the property is dumped before a property with a lower value. So in order to dump a property at the beginning, you can provide a positive value. To make sure that a property is dumped at the end, you can provide a negative value. The order of properties with the same ``order`` value is unspecified.

 