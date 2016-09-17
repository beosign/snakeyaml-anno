[![Build Status](https://travis-ci.org/beosign/snakeyaml-anno.svg?branch=development)](https://travis-ci.org/beosign/snakeyaml-anno)

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

