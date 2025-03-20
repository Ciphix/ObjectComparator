# Mendix Object Comparator module

Welcome to the Mendix Deep Object Assert module. This module can be used in [Mendix](http://www.mendix.com) apps to assert equality of two objects, especially for the purpose of unit testing.

![Object Comparator logo][1]

## Related resources
* Object Comparator on [GitHub](https://github.com/Ciphix/ObjectComparator)
* UnitTesting on [Mendix App Store](https://appstore.home.mendix.com/link/app/390/Mendix/UnitTesting)
* Community Commons Function Library on [Mendix App Store](https://appstore.home.mendix.com/link/app/170/Mendix/Community-Commons-Function-Library)

# Table of Contents

* [Getting Started](#getting-started)
* [Application](#application)
	- [Input](#input)
	- [Output](#output)
	- [Scenarios](#scenarios)
* [Development Notes](#development-notes)

# Getting started
1. The *Object Comparator* module can be downloaded from within the Mendix Business Modeler in the Mendix Appstore into any model that is build with Mendix 9.13.1+.
2. In order to apply the Object Comparator in unit tests, be sure to download and configure the UnitTesting and Community Commons modules.

# Application
Once the ObjectComparator module is imported in your Mendix model, the ObjectComparator Java action can be used in microflows. 

## Input
The input of the ObjectComparator Java action consists of two Mendix objects that are created or retrieved within a microflow:
* The actual object: the Mendix object that is compared with the expected object. Within unit tests, this object is usually returned by another action or microflow.
* The expected object: the object with the expected values. Within unit tests, this object is usually specified by the user. 
* Boolean IncludeAssociatedObjects: if set to True, associated objected will also be evaluated for equality. If set to False, only the input object itself will be included in evaluation.

## Output
A boolean is returned, which is the result of the assertion of equality.
* True is returned if the actual object equals the expected object.
* False is returned if the actual object differs from the actual object. If false is returned, the differences between the Mendix objects are listed in the Mendix Modeler console with lognode 'DeepObjectAssert'. 

## Logging
* The ObjectComparator Log node is available for more advanced logging. If set to Debug or Trace level, more information will be printed about the evaluation of objects.

## Scenarios
The following sceanrios are tested to prove equality:
* The Mendix objects are of the same types (e.g. compare entity Apple with entity Pear)
* One of the input objects are not empty
* A member of an object is not empty when the other is set
* A member of an object has a different value (rounding errors with number types will cause the test to fail, so account for that in the Microflow logic)
* A reference(set) is not set
* This also holds for associated objects as the action recursively checks the associated objects for equality

Please note that associated objects are only included in the test if one-to-many references are evaluated from parent to child and not from child to parent. For instance, if a Car entity is associated with multiple Tyre entities (1 - * association), the Tyre is parent and the Car is the child. If the Tyre is one of the input objects in the Deep Object Assert, the Car object is also evaluated.

The following members are automatically excluded from the test:
* Object members of the AutoNumber member type
* Object members of the HashString member type
* Object members with the following member names:
	- changedDate
	- createdDate
	- System.changedBy
	- System.owner
	- CreationDate


 [1]: docs/logo.png
