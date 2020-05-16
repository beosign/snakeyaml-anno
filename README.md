[![Build Status](https://travis-ci.org/beosign/snakeyaml-anno.svg?branch=development)](https://travis-ci.org/beosign/snakeyaml-anno)

[![codecov](https://codecov.io/gh/beosign/snakeyaml-anno/branch/development/graph/badge.svg)](https://codecov.io/gh/beosign/snakeyaml-anno?branch=development)


# snakeyaml-anno
Parse YAML files by using annotation in POJOS - based on SnakeYaml **1.25** by Sergey Pariev, https://github.com/spariev/snakeyaml/.

## Compatibility starting with 1.0.0
<table class="tg">
  <tr>
    <th class="tg-us36">SnakeyamlAnno</th>
    <th class="tg-us36" colspan="7">SnakeYaml</th>
  </tr>
  <tr>
    <td class="tg-us36"></td>
    <td class="tg-us36">1.23</td>
    <td class="tg-us36">1.24</td>
    <td class="tg-us36">1.25</td>
  </tr>
  <tr>
    <td class="tg-us36">1.0.0<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">1.1.0<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
</table>

## Compatibility before 1.0.0

<table class="tg">
  <tr>
    <th class="tg-us36">SnakeyamlAnno</th>
    <th class="tg-us36" colspan="7">SnakeYaml</th>
  </tr>
  <tr>
    <td class="tg-us36"></td>
    <td class="tg-us36">1.17</td>
    <td class="tg-us36">1.18</td>
    <td class="tg-us36">1.19</td>
    <td class="tg-us36">1.20</td>
    <td class="tg-us36">1.21</td>
    <td class="tg-us36">1.22</td>
    <td class="tg-us36">1.23</td>
    <td class="tg-us36">1.24</td>
  </tr>
  <tr>
    <td class="tg-us36">0.3.0<br></td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
  </tr>
  <tr>
    <td class="tg-us36">0.4.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.5.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.6.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.7.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.8.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
  <tr>
    <td class="tg-us36">0.9.0</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">NO</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
    <td class="tg-us36">YES</td>
  </tr>
</table>

## Usage
You must use the `AnnotationAwareConstructor` when parsing:

```java
Yaml yaml = new Yaml(new AnnotationAwareConstructor(MyRoot.class));
```

You must use the `AnnotationAwareRepresenter` when dumping:

```java
Yaml yaml = new Yaml(new AnnotationAwareRepresenter());
```

## Quick Overview

* Property name mapping
* Converter
* Custom Constructor
* Instantiator
* Case insensitive parsing
* Allow parsing of single value for Collection property
* Allow parsing of list at root without tags
* Ignore parsing errors in a subtree
* Skipping properties
* Ordering properties
* Allow to collect unmapped properties during construction (aka Any-Setter)
* Allow to dump a map flattened (aka Any-Getter)

## Feature Details

This section covers the details of the feature including examples how to use them. 

### Property name mapping
If the properties of your POJO and in your yaml do not match regarding the names, you can supply a mapping by using the `YamlProperty` annotation.

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
    @YamlProperty(key = "name")
    private String firstName;
    private String lastName;
}
```
   
It is also possible to annotate the getter instead:

```java 
 public class Person {     
    private String firstName;
    private String lastName;
  
    @YamlProperty(key = "name")
    public String getFirstName() {...}
}
```


### Converter
You can apply a converter to a field or getter. This feature is especially useful for converting enum values as Snakeyaml as of version 1.17 supports only basic converting, e.g. the string in the yaml file must match the enum constant definition in Java (uppercase).
The following example shows how to convert `m` to the enum constant `MALE`:

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
   
    @YamlProperty(converter=GenderConverter.class)
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

As of version 0.4.0, conversion is also implemented for dumping. The interface has changed; the `convertToModel` method now takes an `Object` as parameter instead of `String` or `Node`.

### Custom Constructor
The converter example above has shown how to apply a different logic to parse a node into a Java object.

However, imagine that a property of type `Gender` would be more than once. When using a converter, you would have to annotate each property of type `Gender`.

So it could make sense to tell the parser to apply a custom logic when it encounters a property of a certain type like `Gender`. This is where the concept of a _CustomConstructor_ comes in handy.

#### Register Custom Constructor by class annotation 
Given the same classes as in the converter example, instead of defining a `Converter` and annotating each property of type `Gender` with it, you can annotate the enum `Gender` with the `YamlConstructBy` annotation:


```java
@YamlConstructBy(GenderCustomConstructor.class)
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

This instructs the parser to create a Java object from a node of type `Gender` using the given `GenderCustomConverter` class, which must implement the `CustomConstructor<T>` interface and could be defined as follows:

```java
public class GenderCustomConstructor implements CustomConstructor<Gender> {
    @Override
    public Gender construct(Node node, Function<? super Node, ? extends Gender> defaultConstructor) throws ConstructorException {
        String val = (String) NodeUtil.getValue(node); // contains 'm' or 'f'
    
        for (Gender g : Gender.values()) {
           if (g.getAbbr().equals(value)) {
              return g;
           }
        }
        
        // try default way of parsing the enum
        return defaultConstructor.apply(node); // if string contains "MALE" or "FEMALE", this is also ok
}

```

The custom constructor can make use of the default way of constructing the passed in node by using the passed in `defaultConstructor` instance.

#### Register Custom Constructor by code
It may be the case that you want to parse a yaml node into a Java object whose type is not defined by your application, but instead comes from a thrid party library or Java itself, so it is not possible to put an annotation on that class.

In this case, you can register a custom constructor using `AnnotationAwareConstructor.registerCustomConstructor`:

```java
   annotationAwareConstructor.registerCustomConstructor(Gender.class, GenderCustomConstructor.class);
   Yaml yaml = new Yaml(annotationAwareConstructor);
```

For more control over the registered constructors, you can also modify `AnnotationAwareConstructor.getConstructByMap`.

#### Custom Constructor Inheritance
If a custom constructor is registered for a type `S`, and a node is of type `T` with `T extends/implements S`, then the custom constructor will also be used. So a custom constructor on a type will also be used for any subtypes.

For example, if a custom constructor is registered for type `Number`, then it will be called for properties of type `Number`, but also for properties of type `Integer` and `Double`. If for let's say `Integer` the custom converter should not be applied, then one has to register the `DefaultCustomConstructor`:  `annotationAwareConstructor.registerCustomConstructor(Integer.class, DefaultCustomConstructor.class)`.

#### Custom Constructor Resolution
Because a custom constructor can be registered via annotation or programmatically, it is possible that there are two definitions for a given (super-)type.

The type hierarchy is relevant. Given a node of type `T`, the exact way for finding the `YamlConstructBy` instance (and thus the custom constructor class) is as follows:

1. Start with `T`, then walk the superclass hierarchy of `T`, then all interfaces of `T`.
1. For each class/interface `S super T`, check first if there is an entry in the `getConstructByMap()` for `S` and if so, return the `YamlConstructBy` from the map
1. Check if `S` is annotated with `YamlConstructBy`, and if so, return the `YamlConstructBy` from the annotation.
1. If there is no match for `S`, proceed with the next class/interface in the hierarchy
1. If no match was found after walking the whole class hierarchy of `T`, no custom constructor is used.

#### Register Custom Constructor on property

It is also possible to register a custom constructor on a per-property-basis. Use this instead of a `Converter` if you need more than a simple conversion mechanism. 

#### Use a Custom Constructor to create specific instances

Using this approach you can mimic the "auto type detection" feature that has been removed.

YAML uses the concept of _tags_ to provide type information. However, to keep the YAML file as simple and concise as possible, it may be desirable to omit a tag declaration if the concrete type to use can already be deducted from the properties. Suppose you have the following interface:

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

For the following YAML, the first object in the list must be of type `Dog` and the second of type `Cat`.

```javascript
pets:
- name: Santas Little Helper
  nrOfBarksPerDay: 20
- name: Snowball
  miceCaughtCounter: 20
```

In order to create correctly typed instances of `Animal` you can use a Custom Constructor that inspects the properties of the mapping node:

```java
    public static class AnimalConstructor implements CustomConstructor<Animal> {

        @Override
        public Animal construct(Node node, Function<? super Node, ? extends Animal> defaultConstructor) throws YAMLException {
            MappingNode mappingNode = (MappingNode) node;
            if (NodeUtil.getPropertyToValueMap(mappingNode).containsKey("nrOfBarksPerDay")) {
                mappingNode.setType(Dog.class);
            } else {
                mappingNode.setType(Cat.class);
            }
            return defaultConstructor.apply(node);
        }

    }
```


### Instantiator

Snakeyaml (as of version 1.23 or earlier) allows in some ways to customize the instantiaton process of Java objects from (mapping) nodes. For example, an object does not need to have a no-arg constructor, because in the yaml file, you can provide parameters to existing constructors, see [Immutable Instances](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-immutable-instances):

```
!!org.yaml.snakeyaml.immutable.Point [1.17, 3.14]  
```

Another built-in possibility is to create objects using the [Compact Object Notation](https://bitbucket.org/asomov/snakeyaml/wiki/CompactObjectNotation.md) feature. Example for a class with 1 parameter, and setting two (String-only!)  properties:

```
package.Name(argument1, property1=value1, property2=value2)
```

However, there is no way to use a **static method** to construct an object, or to make use of an dependency injection framework like **CDI** (exception: [Spring](https://bitbucket.org/asomov/snakeyaml/wiki/Documentation#markdown-header-spring)).

#### Instantiator types
There are three different types of instantiators in SnakeyamlAnno:
* **DefaultInstantiator**

  Represents the Snakeyaml default way of instantiating objects; its method signature thus matches `org.yaml.snakeyaml.constructor.BaseConstructor.newInstance(Class<?>, Node, boolean)`
* **GlobalInstantiator**

  Used for creation of **every** object that needs to be created during parsing, thus *global*; can make use of the DefaultInstantiator to create an object 
* **CustomInstantiator**

  Used for creation of an object of a certain type; can make use of the GlobalInstantiator or the DefaultInstantiator to create an object

#### Default Instantiator
This is just an interface with the signatature that matches the default instantiation method of SnakeYaml. An instance is passed to the Global Instantiator and to the Custom Instantiator so they can always fall back to the SnakeYaml default mechanism.  

#### Global Instantiator
There must always be a global instantiator; by default, it is the `DefaultGlobalInstantiator` that just uses the `DefaultInstantiator`.
You can register your own *Global Instantiator* by implementing the `GlobalInstantiator` interface and register it on the `AnnotationAwareConstructor` by using the corresponding setter right after constructor creation. The effect is that each time an object has to be created for a node, your specialized `GlobalInstantiator`'s `createInstance` method is called. Example:

```java
public class CdiGlobalInstantiator implements GlobalInstantiator {

    @Override
    public Object createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator) throws InstantiationException {
       if (isValidBean(node.getType())) {
          // a CDI bean has been detected, so provide an instance via CDI
          return getBean(node.getType());
       }
       // node type does not correspond to a CDI bean, so use the default instantiation logic
       return defaultInstantiator.createInstance(ancestor, node, tryDefault);
    }
    
    private <T> T getBean(Class<T> type) {
        return CDI.current.select(type).get();
    }

    private boolean isValidBean(Class<?> type) {
        return !CDI.current.select(type).isUnsatisfied() && !CDI.current.select(type).isAmbiguous();
    }
    

}

...
   // before parsing:
   annotationAwareConstructor.setGlobalInstantiator(new CdiGlobalInstantiator());
...   
   
```

The passed in `defaultInstantiator` can be used to apply the normal instantiation logic. This means, `org.yaml.snakeyaml.constructor.BaseConstructor.newInstance(Class<?>, Node, boolean)` is called.


#### CustomInstantiator
You can (independent of a `GlobalInstantiator`) also register a `CustomInstantiator` on a per-type basis. This takes precedence over the GlobalInstantiator. Such an instantiator can either be registered using an Annotation or the programmatic API. It has access to both the DefaultInstantiator and the GlobalInstantiator in order to fall back to their instance creation logic. If there is both an annotation and a programmatic registration present, the programmatic registration takes precedence.

You have to implement the `CustomInstantiator<T>` interface:

```java
public class PersonInstantiator implements CustomInstantiator<Person> {
  @Override
  Person createInstance(Node node, boolean tryDefault, Class<?> ancestor, DefaultInstantiator defaultInstantiator, GlobalInstantiator globalInstantiator) throws InstantiationException {
     // create instance using the global instantiator
     Person person = globalInstantiator.createInstance(node, tryDefault, ancestor, defaultInstantiator);
     // any custom initialization for example
     person.init();
     return person;
  }
```

##### Annotation
You can use the `@YamlInstantiateBy` annotation to define a `CustomInstantiator`:

```java
@YamlInstantiateBy(PersonInstantiator.class)
public class Person { ... }
```

##### Programmatic
The programmatic counterpart is:

```java
annotationAwareConstructor.registerCustomInstantiator(Person.class, PersonInstantiator.class);
```

#### Instantiation logic priority and overriding it
The following order is applied for a type `T` to determine how to create an instance:

1. If present, use the `CustomInstantiator` for `T`
1. Use the `GlobalInstantiator`; by default, this is the `DefaultGlobalInstantiator` that uses the Snakeyaml default logic
1. Use the Snakeyaml default logic


In order to override an instantiator that is registered by an annotation, you can use one of the following:
If you want to "remove" a CustomInstantiator's logic for a given type `T`, while still using a GlobalInstantiator's logic, use:

```java
annotationAwareConstructor.registerGlobalInstantiator(T.class)
```

If you also want to disable any GlobalInstantiator logic for `T`, use

```java
annotationAwareConstructor.registerDefaultInstantiator(T.class)
```

This behaves as if there were no instantiator present at all for `T`.

### Case insensitive parsing
A flag can be passed so that parsing is possible even if the keys in the yaml file do not match the case of the java property where it sould be parsed into. To enable it, use `AnnotationAwareConstructor constructor = new AnnotationAwareConstructor(Person.class, true)`.
So for example, all of the following variants can be parsed using the same Person class (see above):

```javascript
  Name: Homer
  nAME: Marge
  NaMe: Bart
  name: Lisa
```

In the very unlikely case that a Java Bean class contains two properties that differ only in case, the result which property is used is undetermined.

### Allow parsing of single value for Collection property
If you have a collection based property, you have to provide a list in SnakeYaml, otherwise the value cannot be parsed. Example:

```java 
 public class Person {     
    private String name;
   
    private List<Integer> favoriteNumbers;
    private List<Person> children;

}
```

The corresponding parseable yaml could look like this:

```javascript
name: Homer
favoriteNumbers: [42]
children: [{name: bart}]
```

Even though there is only one favorite number, you have to provide the "42" as list item. With snakeyaml-anno, it is possible to provide the following yaml - the constructed nodes will be automatically put into a singleton list, letting the parse process succeed.

```javascript
name: Homer
favoriteNumbers: 42
children: {name: bart}
```
### Allow parsing of list at root without tags
Usually, use can supply an explicit tag at the root of a yaml document to declare the root type to use. As an alternative, you can supply the root type to a yaml constructor object.
If dealing with lists at the root, the only type information you can supply is `List`, but not `List<MyClass>` for example. Therefore, the following yaml is parsed as `List<Map<String,Object>>`:

```javascript
- id: 1
  name: One
- id: 2
  name: Two
```

There is a [Snakeyaml ticket](https://bitbucket.org/asomov/snakeyaml/issues/387/support-for-generic-types-when-serializing) that should address this problem, however, there is no solution yet.

If you would like to parse it as a `List<MyClass>` instead, you have to define the type on each list item:
 
```javascript
- !MyClass 
  id: 1
  name: One
- !MyClass 
  id: 2
  name: Two
```

Because this leads to a yaml that is cluttered with type explicit information, SnakeyamlAnno comes with a special `Constructor` for these cases: The `AnnotationAwareListConstructor` enables to omit the explicit types while still parsing the list items as items of the type that is given to it:


```java
// Parse a list where each list item is of type MyClass instead of Map<String, Object>
annotationAwareListConstructor = new AnnotationAwareListConstructor(MyClass.class);
Yaml yaml = new Yaml(annotationAwareListConstructor);

List<MyClass> = yaml.load(...);
```

Now the list items are of type `MyClass` instead of just `Map<String, Object>`.

### Ignore parsing errors
In a complex hierarchy it may be desirable to ignore parse errors in a given subtree and still return the parsed objects higher up the tree. In case of an exception, the unparsable object will simply remain `null`. To allow the parsing process to skip unparsable parts instead of aborting, you can use `ignoreExceptions = true` on a property or a getter:

```java 
 public class Person {     
    private String firstName;
    private String lastName;
    
    @YamlProperty(ignoreExceptions = true)
    private Gender gender;
  
}
```

So in case the gender property cannot be parsed, you still get a parsed Person object, just with the gender property being `null`.

### Skipping properties

#### Skip dumping of empty properties globally
By default, empty / null properties (see remarks for `SkipIfNull`and `SkipfIfEmpty` below) will be skipped during dumping. You can change that by

```java
Yaml yaml = new Yaml(new AnnotationAwareRepresenter(false)); // supply "false" flag
```

When deactivating the global skipping of empty properties, one can determine on a per-property-basis if a property is to be dumped.

**If global skipping of empty properties is activated (by default), then `skipAtDump` and `skipAtDumpIf` will have no effect**.

#### Skip properties locally

It is possible to skip properties during load or dump. In order to skip a property during load, thus preventing snakeyaml to override a model value with the value read from the yaml file that is being loaded, annotate the property with `skipAtLoad`:

```java 
public class Person {

   @YamlProperty(skipAtLoad = true)     
   private String name;
}
```

In order to prevent dumping of a property, use `skipAtDump`:
 
```java 
public class Person {

   @YamlProperty(skipAtDump = true)     
   private String name;
}
```

You can also skip dumping conditionally by implementing the `SkipAtDumpPredicate` interface. The only method to implement is `skip`. One of the parameters passed into this method is the property value, so you can make decisions whether to skip a property based on its value.
You can use your implementation by using the `skipAtDumpIf` member:
 
```java 
public class Person {

   @YamlProperty(skipAtDumpIf = SkipIfNull.class)     
   private String name;
}
```

**Be aware** that if `skipAtDump` is also supplied and set to true, it will take precedence over the ` skipAtDumpIf`! `skipAtDump` and `skipAtDumpIf` will have **no effect** if empty properties are skipped globally (that's the default, see above).

Predefined are two classes: `SkipIfNull` and `SkipIfEmpty`. The first one skips a property if it is `null`, the latter one skips a property if it is of type `Map`, `Collection` or `String` and the property is empty (empty map/collection or String of length 0).


### Ordering properties
It is possible to order properties during the dump process by providing a value for the `order` property:

```java 
public class Person {

   @YamlProperty(order = 5)     
   private String first;

     
   private String between;

   @YamlProperty(order = -5)     
   private String last;
}
```

This will dump the properties in the order:

1. first
2. between
3. last

The default value for `order` is 0. A higher value means that the property is dumped before a property with a lower value. So in order to dump a property at the beginning, you can provide a positive value. To make sure that a property is dumped at the end, you can provide a negative value. The order of properties with the same `order` value is unspecified.

### Collect unmapped properties
`Since 1.1.0`

It is possible to allow unmapped properties, i.e. those properties in a yaml file that do not have a corresponding writable property in the target Java object, to be processed differently. So instead of throwing an exception, a fallback method annotated with `@YamlAnySetter` will be called. This method must be **public** and take exactly two parameters, the first of type `String`, the second of type `Object`. The first parameter contains the key where no matching writable property could be found; the second parameter contains the value. So by this it is possible to collect all unmapped properties in a map.

The idea is taken from Jackson's `@JsonAnySetter` annotation, see [Jackson Annotations](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations) and [Jackson-Any-Setter](https://www.logicbig.com/tutorials/misc/jackson/jackson-any-setter.html).

**Caveat:** If the flag `skipMissingProperties` is set, **this annotation will have no effect**! If there is more than one method that is annotated within the Java class, it is unspecified which method will be called. If the annotated method 
does not have the required parameters, an exception will be thrown.

The annotated method may reside in the class itself or in any of its superclasses or interfaces. It may be static. If there are several annotated methods in the class hierarchy, the method within the class takes precedence over the ones in its superclasses and over the ones in its interfaces.

 
#### Example
 
Given the following Java Class:
 
```java 
public class Person {
   private String first;
   private String last;

   public String getFirst() { return this.first; }
   public void setFirst(String first) { this.first = first; }
   
   public String getLast() { return this.last; }
   public void setLast(String last) { this.last = last; }     
   
   @YamlAnySetter
   public void anySetter(String key, Object value) {
       System.out.println("Unknown property: " + key + " with value " + value);
   }     
}
```

And this yaml:

```javascript
!Person
first: Homer
last: Simpson
age: 42
wife: !Person
  first: Marge
  last: Simpson 
  haircolor: blue
children:
- name: Bart
- name: Lisa
- name: Maggie
```

Then the parsing process will succeed, and the anySetter method will be called for:
* age: 42
* wife: A Person instance
* haircolor: blue
* children: A List<Map<String, Object>> containing each child as list item where each list item is a map

### Flatten unmapped properties
`Since 1.1.0`

To reverse the function of the `@YamlAnySetter` feature when dumping, you can annotate a property of type `Map` with `@YamlAnyGetter`. This will cause the map to be _flattened_ when dumped, so as if the map entries were direct properties of the object containing the map.

#### Example
 
Given the following Java Class:
 
```java 
public class Person {
   private String first = "Homer;
   private String last = "Simpson";
   private Map<String,Object> map = new HashMap<>() {
      {
         put("age", 42);
         put("type", "type1");
      }
   };
   

   public String getFirst() { return this.first; }
   public String getLast() { return this.last; }
      
   @YamlAnyGetter
   public Map<String,Object> map() {
      return map; 
   }     
}
```

Then the dumped yaml will look like that:


```javascript
!Person
first: Homer
last: Simpson
age: 42
type: type1
```

#### Remarks
The property annotated with `@YamlAnyGetter` **must** be of type `Map` or a subtype thereof. Otherwise the dumping process will fail with a `YamlException`.

If a key of the map has the same name as a property in the object that contains the map, the dumper will output the property twice.