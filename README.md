[![GPL3](https://img.shields.io/badge/license-GPL3-%23a42e2b.svg)](https://www.gnu.org/licenses/gpl-3.0.en.html)
[![Travis branch](https://img.shields.io/travis/sanhozay/CLGen/master.svg?label=master)](https://travis-ci.org/sanhozay/CLGen)
[![Travis branch](https://img.shields.io/travis/sanhozay/CLGen/develop.svg?label=develop)](https://travis-ci.org/sanhozay/CLGen)

# CLGen

CLGen is a tool for generating Flightgear checklists. Input is in the form of a
domain-specific language (DSL) that describes checklist items and lists. CLGen
parses the DSL and generates XML files that can be used in Flightgear.

CLGen also provides rudimentary support for reverse-engineering existing
checklist definition files into CLGen source and generating PDF and DOT
summaries of existing checklists.

## Background

Flightgear checklists are defined in XML files. A typical checklist looks
something like this:

    <!-- Landing Lights OFF -->
    <item>
      <name>Landing Lights</name>
      <value>OFF</value>
      <condition>
        <less-than>
          <property>systems/electrical/outputs/landing-lights</property>
          <value>6.0</value>
        </less-than>
      </condition>
      <binding>
        <command>property-assign</command>
        <property>controls/switches/landing-lights</property>
        <value>0</value>
      </binding>
      <marker>
        <x-m>0.2543</x-m>
        <y-m>0.6453</y-m>
        <z-m>0.2983</z-m>
        <scale>2</scale>
      </marker>
    </item>

While this is not particularly difficult to define, it is quite verbose. The
verbosity is compounded by the requirement that landing lights should be off
during startup and taxi, on before takeoff, off after takeoff, on before landing
and off during taxi and parking. That means five repetitions of the XML block
shown above, with minor changes for the on and off states.

Other items may not be so repetitive, but most items will be checked more than
once. The end result is series of quite long XML descriptions that are tedious
to write and awkward to review and change. Moving the landing light switch
within the cockpit, for example, involves changing the marker in five places.

## Overview of Checklist Definition Language (the DSL)

CLGen uses a domain specific language organized into two sections:

### a. Checklist Items

A checklist item defines an item that can appear in one or more checklists. It
defines the title, the states, the bindings to transition to those states and a
marker for use in tutorials. Note the simple use of aliases to simplify the
conditions and bindings:

    item("Landing Lights") {
        v = "systems/electrical/outputs/landing-lights";
        s = "controls/switches/landing-lights";
        state("OFF", v <= 6.0) s = 0;
        state("ON" , v > 6.0) s = 1;
        marker(0.2543, 0.6453, 0.2983, 2);
    }

The alias `v` holds the property that indicates the output volts. The alias
`s` represents the landing light switch. These are aliases rather than
variables, and provide a shorthand for referring to a property.

Looking at the `OFF` state in more detail, the condition is that the output
volts are less than or equal 6.0 and the binding to satisfy the `OFF` state is
to assign zero to the switch property.

For quick prototyping of simple checklists, items are optional but they are
required to define interactive checklists with conditions and bindings.

### b. Checklists

Checklists are defined in terms of items and states.

    checklist("Before Starting Engines") {
        check("Landing Lights", "OFF");
    }

    checklist("Before Takeoff") {
        check("Landing Lights", "ON");
    }

These are much easier to review and reorganize than large XML blocks.  Changing
the item definition applies to all generated XML blocks throughout a set of
checklists.

A real checklist usually has multiple checks:

    checklist("Initial Climb") {
        check("Flaps", "UP");
        check("Boost Pump", "OFF");
        check("Landing Light", "OFF");
    }

## Running the Program

The easiest way to run CLGen is by downloading a release distribution from
<a href="https://github.com/sanhozay/CLGen/releases">CLGen/releases</a>.

1. Download the distribution zip file and unpack it
2. Add the distribution directory to your path
3. Run the `clgen` script, passing an input file as an argument

On Linux/Mac OS:

    $ clgen mychecklists.clg

On Windows:

    > clgen mychecklists.clg

The output files are created in the same directory as the input file, so it 
makes sense to create a separate directory to hold your checklist project.

Existing files are overwritten without confirmation. Be careful about
generating checklist files directly into your aircraft source directory if you
have existing checklists.

### Reverse-Engineering Checklists

To reverse-engineer an existing checklist into CLG format, pass the name of the
checklist XML file as the argument to CLGen. If the file extension is `.xml` it
will reverse-engineer, otherwise it will treat the file as CLG source.

    $ clgen checklists.xml

The XML file can be a single checklist XML file, containing `<checklist>`
elements, or it can be a wrapper XML with included checklists like this one:

    <PropertyList>
      <checklist include="before-starting-engines.xml"/>
      <checklist include="start-engines.xml"/>
    </PropertyList>

The included files are followed by CLGen and must be in the relative paths
specified in the include attribute.

The products of reverse-engineering are:

 * `checklists.clg` - can be used as the basis for further maintenance
 * `checklists.pdf` - shows roughly how the checklist will look in Flightgear
 * `checklists.dot` - provides an overview of the checklists

 Note that conditions and bindings are not currently parsed from the XML and do
 not appear in the generated CLG file. Be careful when reverse-engineering that
 you do not overwrite CLG files that you have been working on.

## Compiling the Program

Compiling from source is only necessary if you are interested in looking at or
changing the source code or using the development version.

### Prequisites

Java 1.8 JDK

### Building with Gradle

1. Clone or download the source project.
2. Run `gradlew` (`gradlew.bat` on Windows)

    ./gradlew

When running gradlew (the Gradle wrapper) for the first time, it will
download the correct version of gradle to build the project and download
the required libraries for CLGen during the build.

Subsequent builds are much faster and should look like this:

    $ ./gradlew
    :generateGrammarSource UP-TO-DATE
    :compileJava UP-TO-DATE
    :compileGroovy NO-SOURCE
    :processResources UP-TO-DATE
    :classes UP-TO-DATE
    :jar UP-TO-DATE
    :assemble UP-TO-DATE
    :generateTestGrammarSource NO-SOURCE
    :compileTestJava NO-SOURCE
    :compileTestGroovy UP-TO-DATE
    :processTestResources NO-SOURCE
    :testClasses UP-TO-DATE
    :test UP-TO-DATE
    :check UP-TO-DATE
    :build UP-TO-DATE
    
    BUILD SUCCESSFUL
    
    Total time: 1.916 secs

The `clgen` or `clgen.bat` scripts in the project root directory can be used to run
the program after building successfully, for example:

On Linux/Mac OS:

    $ clgen mychecklists.clg

On Windows:

    > clgen mychecklists.clg

## Frequently Asked Questions

### How do I use the checklists in my aircraft?

You need to include the `checklists.xml` file into your aircraft's `-set.xml`
file. Assuming you created the files in a `Checklists` directory in your
aircraft:

    <sim>
      <checklists include="Checklists/checklists.xml"/>
      ...
    </sim>

### Does CLGen support Nasal command bindings?

No. Nasal bindings in checklists are problematic. The binding usually mimics a
cockpit control, e.g. the landing lights switch. If Nasal is associated with the
checklist binding, it's probably associated with the cockpit control too. That
means the same code in two places.

The solution is to create a Nasal script file in the aircraft's Nasal directory
and add a custom fgcommand to it using `addcommand`. This command can then be
used from both the cockpit control and the checklist.

### What is checklists.dot and how can I view it?

The .dot file is a visualization of the checklists structure and can be
rendered to an image using a tool like Graphviz.

    $ dot -o checklists.png -Tpng checklists.dot

### How can I create multiple checklist XML files with a wrapper?

By default, CLGen creates a single XML output file called `checklists.xml`. To
create multiple files, add a wrapper directive in the project definition of
your CLGen source file:

    project("MyProject") {
        author("My Name");
        wrapper(true);
    }

Running CLGen will now create a wrapper XML file called `checklists.xml` that
includes separate XML files, one for each checklist. These included XML files
are named based on the name of the checklist. So a checklist "Before Starting
Engines" will create an XML file called `before-starting-engines.xml`.

### Can I get syntax highlighting in my CLGen source files?

Yes. If you use Vim. Refer to the README in the `support` directory.

If you would like to add support for another editor, pull requests are welcome.

### Can I create a license header using something other than GPL2?

Yes, GPL2 is the default license added when you include an author directive in
your GLGen source. You can override this by including a file called `license.vm`
in your working directory during generation. Note that this doesn't have to be the
same directory as your CLGen source file.

The following placeholders are supported and should be self-explanatory:

    $title
    $version
    $author
    $year

So, for example, you could use a `CC BY-NC` license by creating `license.vm` with
the following content:

```
$title
  
Copyright (c) $year $author
  
This work is licensed under the Creative Commons Attribution-NonCommercial
4.0 International License. To view a copy of this license, visit
http://creativecommons.org/licenses/by-nc/4.0/ or send a letter to Creative
Commons, PO Box 1866, Mountain View, CA 94042, USA.
  
Generated by CLGen $version.
```

## Checklist Definition Language

The language is designed to feel familiar to Flightgear developers who are
familiar with Nasal and is very similar in structure to brace languages like C,
C++, C# and Java.

### Comments

Comments are introduced with `#` and are active until line end:

    # This is a comment

### Whitespace

Whitespace is generally ignored. The intention is that a brace-langauge style is
used for formatting, e.g. Nasal or C++ style. Text editors with syntax
highlighting and auto-indentation of brace languages work reasonably well 
with the CLGen DSL.

### Keywords

Keywords cannot be used as aliases. CLGen is case sensitive, so upper-case
versions of keywords can be used as aliases.

`author`  
`check`  
`checklist`  
`fgcommand`  
`item`  
`marker`  
`project`  
`state`  
`text`  
`wrapper`  

### Overall Structure

The input file may begin with a project definition. The title of the project is
used as a title in PDF, XML and DOT output. Adding an author definition
automatically adds a GPL2 header to the generated XML files with the author
as copyright holder.

    project("My Checklists" {
        author("Richard Senior");
        wrapper(false);
    }

The project definition can omit the author and wrapper definitions:

    project("My Checklists");

The default for the wrapper is false, which means CLGen will create a single XML
file containing all your checklists. If you prefer multiple XML files, one for
each checklist, set wrapper to true, e.g.

    project("My Checklists")
        wrapper(true);

Therafter, global declarations, items and checklists can appear in any order but
declarations must be made before they are used in an item. You can put your
checklists above your items or below, it doesn't matter.

Globals can appear anywhere outside an item definition. It usually makes sense
to define globals near the top of the file, before any item declarations. Use of
upper case for global names is recommended, but not mandatory.

    checklist("Before Starting Engines") {
        ...
    }

    AUTO = "sim/checklists/auto/active";

    item("Parking Brake") {
        ...
    }

    item("Navigation Lights") {
        ...
    }

Different items with the same title are not allowed. The comparison is case
sensitive.

### Items

Items are introduced with the `item` keyword.
    
    item("Item Title") {
        ...
    }

The item title appears in the checklist dialog.

When prototyping checklists, items are optional, but as soon as the first
item is defined, CLGen expects all checklist checks to be defined using
items. Reverse engineering a checklists XML file is a quick way to add
items in preparation for defining conditions and bindings.

### Aliases

Items can define aliases to refer to Flightgear properties. These aliases may
look like variables but they are simply aliases to Flightgear properties.
Aliases must be defined before they are used and their scope is limited to the
item block in which they are defined.

    item("Item Title") {
        v = "systems/electrical/outputs/landing-lights";
        # 'v' can be used in the rest of this item block ...
    }

Alias names must start with a letter or underscore. The remaining characters
must be letters, numbers, underscore or hyphens. These are valid identifiers:

    beacon, engine0, fuel_pump, outputVolts, power-button, _switch

Redefinition of an alias within an item is not allowed.

Global aliases are alias definitions defined outside the scope of an item.
They can be used in any item but must be defined before use.

### Types

When aliases are first defined, they are untyped. CLGen infers types based on
bindings and conditions and will warn about inconsistent usage. The `value` tags
in the output files respect types inferred from conditions and assignments.

For example:

    p = "some/property";
    state("OFF", p == 0) p = 0; 
    state("OFF", p == 0.0) p = 0.0; 
    state("OFF", !p) p = false; 
    state("OFF", p == "no") p = "no"; 

Integers and floating-point numbers are carried through to the output but
the `value` tag has no type attribute.

    <binding>
      <command>property-assign</command>
      <property>some/property</property>
      <value>0.0</value>
    </binding>

Booleans and strings are carried through to the output and the `value` tag 
does have a type attribute, e.g.

    <binding>
      <command>property-assign</command>
      <property>some/property</property>
      <value type="bool">false</value>
    </binding>

### States

To be useful in a checklist, items must have at least one state. The state has
an optional condition (which is used to indicate completeness in the Flightgear
checklist dialog), and optional bindings (which are tied to the action button in
the Flightgear checklist dialog):

    state("OFF", [condition]) [bindings]

The condition is a boolean expression, consisting of the following operators:

`!`  
`==`  
`>`  
`<`  
`<=`  
`>=`  
`!=`  
`&&`  
`||`  

The usual precedence and associativity rules apply. Refer to documentation for
C, C++, Java, etc.

Duplicate state names are not allowed. The comparison is case sensitive but
it is recommended that uppercase is always used for state names.

Expressions are always defined in terms of variables, e.g.

    !v
    v == 0
    v1 != 0 && v2 != 0

Bindings are optional:

    state("OFF", v == 0);

Bindings can be simple statements, e.g.

    state("OFF", v == 0) v = 0;

Compound bindings can have zero or more binding statements:

    state("OFF", v == 0) {
        v1 = 0;
        v2 = 0;
    }

Bindings can also reference fgcommands using the `fgcommand` keyword:

    state("OFF", v1 == 0 && v2 == 0) {
        fgcommand("parking-brake-off");
    }

Additional properties can be passed to `fgcommand` as follows:

    fgcommand("property-interpolate", property=p, value=0, time=1.0);

Sometimes conditional bindings are useful:

    variant = "sim/aero";
    pitchSelect = "autopilot/internal/pitch-select";
    state("CHECK", pitchSelect > 0) {
        if (variant == "pup100")
            pitchSelect = 5.0;
        if (variant != "pup100")
            pitchSelect = 6.0;
    }

Conditional bindings can contain multiple bindings if required, e.g. on a
multiple-engine aircraft:

    if (variant == "777-200") {
        t0 = 0.8;
        t1 = 0.8;
    }
    if (variant == "777-300") {
        t0 = 0.85;
        t1 = 0.85;
    }

One `marker` is allowed per item and takes the following form:

    marker(0.1234, -2.3456, 10.2983, 2.0);

The arguments are x, y and z coordinates, followed by the marker scale.

### Checklists

Checklists and checks are simple structures:

    checklist("Before Starting Engines") {
        check("Parking Brake", "OFF");
        check("Navigation Lights", "ON");
    }

Default behaviour is to create a single XML file called `checklists.xml` in the
same directory as the input file. See "Frequently Asked Questions" above for a
description of how to change this behaviour.

When using a wrapper, each checklist block creates a separate XML file. The 
title of the file is derived from the title. So, in the example above, `clgen`
creates a file called `before-starting-engines.xml`.

Duplicate checklist titles are not allowed. This comparison is not case
sensitive, so "Parking" and "parking" are considered the same checklist
title.

Checks can also include additional values. These are displayed in the Flightgear
checklist dialog as additional lines below the state name.

    check("Suction", "OK", "(minimum 3 inches)");
    
To create blank lines and subtitles, a checklist can include text elements.
There is no need to create dummy items.

    text();
    text("This is a subtitle:");

These can be used to enhance the formatting of the Flightgear checklist
dialog and are handled by CLGen's PDF and DOT output formats.
