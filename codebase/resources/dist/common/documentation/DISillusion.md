Disco DISillusion
===================
Welcome to Disco DISillusion, provided by the OpenLVC project.

DISillusion is a tool for generating somewhat believable "real" DIS network
traffic.

It allows quick generation of traffic for entities following set paths in order
to test network loads and representation of entities at known locations in
3rd party systems which handle DIS traffic.

What does it do?
------------------------
Common use cases include:

  - load testing for systems which handle DIS traffic
  - verification of representations of DIS traffic in third party systems

How DISillusion Works
--------------------------
DISillusion reads a simple JSON formatted configuration file which it uses to
determine the locations of entities along simple movement paths.

It then outputs DIS traffic which simulates a "real" DIS application outputting
positional information for those entities.

Using the DISillusion
------------------------
Launchers are in the `bin` directory. To start DISillusion, simply launch
`dislillusion.sh` and optionally specify a
configuration file:

```
$ bin/dislillusion --config-file etc/dislillusion.config
```

The config file mentioned above will be loaded if none is specified. When DISlillusion starts, you
should see the following output:

```bash
[tim@example:disco-1.0.0]$ bin/dislillusion.sh
11:55:12.113 [main] INFO  disillusion:
11:55:12.114 [main] INFO  disillusion:  ___ ___ ___
11:55:12.114 [main] INFO  disillusion: |   \_ _/ __)
11:55:12.114 [main] INFO  disillusion: | |) | |\__ \ o  ) )     _ o  _   _
11:55:12.115 [main] INFO  disillusion: |___/___|___/ ( ( ( (_( (  ( (_) ) )
11:55:12.115 [main] INFO  disillusion:                         _)
11:55:12.115 [main] INFO  disillusion:
11:55:12.115 [main] INFO  disillusion: Welcome to DISillusion - a tool for creating the illusion of 'real' DIS traffic
11:55:12.115 [main] INFO  disillusion:
11:55:12.972 [main] INFO  disco:        Welcome to Open LVC Disco
11:55:12.972 [main] INFO  disco:         .___.__
11:55:12.972 [main] INFO  disco:       __| _/|__| ______ ____  ____
11:55:12.972 [main] INFO  disco:      / __ | |  |/  ___// ___\/  _ \
11:55:12.972 [main] INFO  disco:     / /_/ | |  |\___ \\  \__(  ( ) )
11:55:12.973 [main] INFO  disco:     \____ | |__/____  >\___  >____/
11:55:12.973 [main] INFO  disco:          \/         \/     \/
11:55:12.973 [main] INFO  disco:
11:55:12.973 [main] INFO  disco: Version: 1.0.0 (build 9)
11:55:12.973 [main] INFO  disco:
11:55:13.037 [main] INFO  disco: Connecting broadcast socket - /192.168.1.135:3000 (interface: name:eth9 (Intel(R) Ethernet Connection (2) I219-V))
```


Configuring DISillusion
----------------------------
DISillusion uses two configuration files - one to configure underlying DIS network parameters
and so on, the other to describe the entities and motion paths for the session of generated
DIS traffic.

Some example distributor configuration files can be found in the `etc` directory. The main `etc/dislillusion.config` file is heavily annotated.

  - `etc/dislillusion.config` Default config file, heavily annotated
  - `etc/dislillusion.session` Entity and motion path session config file, (JSON formatted)

### Entity and Motion Path Config File Structure
The `dislillusion.session` configuration is in the JSON format, a simple human readable text based
data format. If you are unfamiliar with the JSON format [this article](https://en.wikipedia.org/wiki/JSON) may help.

There are three main sections: `entities`, `locations` and `paths`, which sit inside the root level
JSON object, like so:

```
{
    "entities":
    {
        ...entity type definitions here...
    },
    "locations":
    {
        ...well-known location definitions here...
    },
    "paths":
    [
        ...path and motion definitions here...
    ]
}
```

#### `entities` Section

The `entities` section is a convenient way to define the various types of entities which will be
involved in the session.

Each entity type entry consists of a name for the entity type, and a corresponding DIS enumeration.

Entity types defined here can be referred to later in the `paths` configuration section to
make the maintenance of the configuration file simpler.

The name of the entity may be any string value - short but meaningful names are encouraged.

The DIS enumeration may be given as a JSON array of integers, or a delimited string, as can
be seen in the following simple example:

```
{
    "entities":
    {
        "chinook": [1, 2, 225, 23, 1, 9, 0],
        "bushmaster": "1.1.13.3.2.1.0",
        "land": [1, 1],
        "air": "1.2",
    },
    ...
}
```
**NOTE:** In order to facilitate faciltate copy/pasting from a variety of source mapping file
formats, when expressing a DIS enumeration as a string, any non-numeric character is treated as a
delimiter.

This means that the following  DIS enumeration strings are all equivalent:
 - `"1 1 13 3 2 1 0"`
 - `"1.1.13.3.2.1.0"`
 - `"1,1,13,3,2,1,0"`
 - `"1-1-13-3-2-1-0"`
 - `"1 1.13,3-2x1?0"`

**NOTE:** While DIS enumerations consist of 7 digits, values can be left out (as seen in the above example). "Missing" values are interpreted as having a 0 value.

#### `locations` Section

The `locations` section is a convenient way to define "well known locations" which will be
involved in the session.

Each location entry consists of a name for the location, and a corresponding
latitude/longitude/altitude definition.

Locations defined here can be referred to later in the `paths` configuration section to
make the maintenance of the configuration file simpler.

The name of the location may be any string value - short but meaningful names are encouraged.

The location's position must be given as a JSON object with the keys.…
 - `lat`: latitude in decimal degrees
 - `lon`: latitude in decimal degrees
 - `alt`: altitude above mean sea level in meters

as can be seen in the following simple example:

```
{
	"locations":
	{
        "perth": {"lat": -31.9522, "lon": 115.8589, "alt": 0},
        "new york": {"lat": 40.6611, "lon": -73.9438, "alt": 0},
        "orbit-center": {"lat": -31.9522, "lon": 115.8589, "alt": 5000}
	},
    ...
}
```
**NOTE:** The `alt` value may be omitted, in which case it is interpreted as being 0m
above ground level.

**NOTE:** The `lat` and `lon` values should _never_ be omitted, but if either are missing 0°
will be used for the missing value(s).

### `paths` Section
The `paths` section defines the groups of entities and kinds of motion they will perform during
the session.

The `paths` section is a JSON array, with each entry specifying a path and the entities which will
follow that path.

```
{
	"paths":
	[
		{
			"entityType":[1, 1, 225, 1, 2, 3, 4],
			"count":5,
			"spacing":20.0,
			"path":
			{
				"type":"line",
				"start": {"lat": -31.9522, "lon": 115.8589, "alt": 1000},
				"heading": 45.0,
				"distance":500.0,
				"speed":16.6666,
			},
		},
		{
			"entityType":"chinook",
			"count":10,
			"spacing":100.0,
			"path":
			{
				"type": "circle",
				"center": "orbit-center",
				"radius": 500.0,
				"speed": 16.6666,
			},
		},
		{
			"entityType": "bushmaster",
			"count": 10,
			"spacing": 10.0,
			"path":
			{
				"type": "polygon",
				"center": {"lat": -31.9522, "lon": 115.8589, "alt": 0},
				"sides": 5,
				"radius": 500.0,
				"orientation": 0.0
				"speed": 16.6666,
			},
		},
	]
    ...
}
```

All `paths` entries *must* have the following parameters:
 - `entityType`: either a DIS enumeration or an entity name defined in the `entities` configuration section. If a DIS enumeration is specified, it may be given as an array of integer values or a
 string, just as in the `entities` section.
 - `count`: the number of entities to follow this path
 - `spacing`: the spacing (in meters) between the entities on this path
 - `path`: the definition of the path itself

The individual paths themselves are defined in a `path` entry - the configuration of the path
depends on what type of path is chosen. Currently there are 3 types of path:
 - `line`: entities endlessly ping-pong back and forth between a start point and destination point
 - `circle`: entities endlessly follow a circular path
 - `polygon`: entities endlessly follow a polygonal path (triangle, square, pentagon etc).

#### `line` Path Configuration
A `line` path has the following parameters:
 - `type`: *must* be `line` (this is how the type of the path is identified)
 - `start`: either a JSON Object defining a latitude/longitude/altitude or an location name defined
 in the `locations` configuration section.
 - `heading`: the compass heading to proceed in from the starting point
 - `distance`: the the distance to travel from the starting point
 - `speed`: the speed of the entities along the path, in meters per second.

#### `circle` Path Configuration
A `circle` path has the following parameters:
 - `type`: *must* be `circle` (this is how the type of the path is identified)
 - `center`: either a JSON Object defining a latitude/longitude/altitude or an location name
 defined in the `locations` configuration section.
 - `radius`: the radius of the circular path, in meters
 - `speed`: the speed of the entities along the path, in meters per second.

#### `polygon` Path Configuration
A `polygon` path has the following parameters:
 - `type`: *must* be `polygon` (this is how the type of the path is identified)
 - `center`: either a JSON Object defining a latitude/longitude/altitude or an location name
 defined in the `locations` configuration section.
 - `radius`: the radius of the circular path, in meters
 - `sides`: the number of sides of the polygon
 - `orientation`: the offset, in degrees, from compass North (generally 0, unless you really need
 to ensure a particular orientation for the polygon)
 - `speed`: the speed of the entities along the path, in meters per second.

License
========
The full Disco library is distributed under the Apache Software License v2.
