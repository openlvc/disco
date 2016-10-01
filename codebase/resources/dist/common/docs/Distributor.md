Disco Distributor
===================
Welcome to the Disco Distributor, provided by the OpenLVC project.

The Distributor is a DIS network bridging and filtering tool.

It is flexible enough to support simple DIS-to-DIS local network bridging, or to create complex network topologies involving multiple relays and long haul connections. At each point, filters can be defined to control what the data that is let through to each connection, whether for security purposes or system performance management.

What does it do?
------------------------
Common use cases include:

  - Bridge between two DIS networks running on different configurations
  - Set up DIS network partitions to reduce or limit traffic between them
  - Enable site-to-site DIS networks that span broadcast domains (or the globe!)
  - Filter traffic between any of the configured networks


How the Distributor Works
--------------------------
The Distributor follows a very simple concept. Within it are a configurable set of links. Every PDU received by those links is reflected to every other link.


```
                                                     __  _    __  _    __  _
+--- Distributor --------------------------------+  [__]|=|  [__]|=|  [__]|=|  
|    +-------------+                             |  /::/|_|  /::/|_|  /::/|_|
|    |             <-----------+----------+      |   |        |        |    
|    | reflector   +---------->|      dis |      |---+--------+--------+----->
|    |             |           +----------+      |             192.168.0.1/24
|    +-^-+--^-+----+                             |
|      | |  | |                                  |      .-,(  ),-.
|      | |  | +--------------->+----------+      |     .-(          )-.
|      | |  +------------------|      wan | -----|--> (    internet    )
|      | |                     +----------+      |     '-(          ).-'
|      | |                                       |         '-.( ).-'
|      | +-------------------->+----------+      |                 
|      +-----------------------|      dis |      |--------------+----------+
|                              +----------+      | 10.0.0.x/24  |          | 
+------------------------------------------------+          __  _      __  _
                                                           [__]|=|    [__]|=|
                                                           /::/|_|    /::/|_|

```

Links can connect to different networks, or can in some cases generate traffic themselves. Some exist just to serve the internal maintenance of the distributor. Each link has a mode which defines how it connects to a network. The following types are currently provided:

  - `dis` Connects to a local DIS broadcast or multicast network
  - `wan` Connects to a remote relay over a point-to-point connection
  - `relay` Accepts incoming connections from `wan` links and patches them into the distributors network
  - `pulse` Sends an ESPDU on an interval to act as a "test tone"
  - `logging` For debugging, simply logs a message for each PDU it receives



Using the Distributor
------------------------
Launchers are in the `bin` directory. To start the Distributor, simply launch `distributor.sh` and optionally specify a configuration file:

```
$ bin/distributor --config-file etc/distributor.config
```

The config file mentioned above will be loaded if none is specified. When the distributor starts, you should see the following output:

```bash
[tim@example:disco-1.0.0]$ bin/distributor.sh
12:44:12.795 [main] INFO  distributor:
12:44:12.796 [main] INFO  distributor:         ___      __       _ __          __
12:44:12.796 [main] INFO  distributor:    ____/ (_)____/ /______(_) /_  __  __/ /_____  _____
12:44:12.796 [main] INFO  distributor:   / __  / / ___/ __/ ___/ / __ \/ / / / __/ __ \/ ___/
12:44:12.796 [main] INFO  distributor:  / /_/ / (__  ) /_/ /  / / /_/ / /_/ / /_/ /_/ / /
12:44:12.796 [main] INFO  distributor:  \__,_/_/____/\__/_/  /_/_.___/\__,_/\__/\____/_/
12:44:12.797 [main] INFO  distributor:
12:44:12.797 [main] INFO  distributor: Welcome to the Distributor - DIS Bridging and Filtering
12:44:12.798 [main] INFO  distributor: Version 1.0.0 (build 0)
12:44:12.800 [main] INFO  distributor:
12:44:12.801 [main] INFO  distributor: 2 links configured: [local, longhaul]
12:44:12.801 [main] INFO  distributor:   local    [down] { DIS, address:BROADCAST, port:3000, nic:SITE_LOCAL }
12:44:12.801 [main] INFO  distributor:   longhaul [down] { WAN, address:virpac.openlvc.org, port:4919, transport:tcp }
12:44:12.802 [main] INFO  distributor:
12:44:12.802 [main] INFO  distributor: Starting Reflector
12:44:12.802 [main] INFO  distributor: Bringing all links up:
12:44:12.826 [main] INFO  distributor:   local    [  up] { DIS, address:/192.168.1.255, port:3000, nic:eth1 }
12:44:12.914 [main] INFO  distributor:   longhaul [  up] { WAN, address:test.openlvc.org/123.123.123.123, port:4919, transport:tcp }
12:44:12.914 [main] INFO  distributor:
```


Configuring the Distributor
----------------------------
Some example distributor configuration files can be found in the `etc` directory. The main `etc/distributor.config` file is heavily annotated.

  - `etc/distributor.config` Default config file, heavily annotated
  - `etc/distributor.relay.config` Basic sample relay configuration
  - `etc/distributor.local.config` Connects to local DIS network and relay from above

### Config File Structure
There are two major important parts to the configuration files: 1) the list of links to load, 2) the configuration of each link.

At the top of the file you will find the line:

```
distributor.links = local, longhaul
```

This is a comma-separated list of link names. Note that you just make the names up. We will fill out the configuration underneath them. In this case, we are loading two links; one called `local` and the other called `longhaul`.

### Link Configuration
All properties for a specific link are searched for under the prefix `distributor.<name>` where `<name>` is whatever you specified in the links list. All configuration properties for that specific link should carry the prefix to associated them with it.

Each link has a `mode` (from list above) which then tells it the type of link to load and in turn defines the other relevant properties that should be looked for.

As an example, consider the following:

```
# Link Settings
distributor.links = local, longhaul

# Local connection to DIS broadcast network
distributor.local.mode           = dis
distributor.local.dis.address    = BROADCAST
distributor.local.dis.port       = 3000
distributor.local.dis.nic        = SITE_LOCAL
distributor.local.dis.exerciseId = <any>
distributor.local.dis.siteId     = <random>
distributor.local.dis.appId      = <random>
distributor.local.dis.loglevel   = ERROR
distributor.local.dis.logtofile  = on
distributor.local.dis.logfile    = logs/distributor.<name>.log
distributor.local.filtering.in   = <none>
distributor.local.filtering.out  = <none>

# Long haul connection to the relay
distributor.longhaul.mode                 = wan
distributor.longhaul.wan.relay            = virpac.openlvc.org
distributor.longhaul.wan.port             = 4919
distributor.longhaul.wan.transport        = tcp
distributor.longhaul.wan.bundling         = false
distributor.longhaul.wan.bundling.maxSize = 1400b
distributor.longhaul.wan.bundling.maxTime = 30

```

Notice that all the properties for the link called `longhaul` are prefixed with `distributor.longhaul`. In the example above, the `mode` of that connection is set to `wan`, with all the subsequent properties relevant to that mode.

The exact configuration options available for each link mode are laid out in the default, annotated config: `etc/distributor.config`.


### Special Configuration Symbols
A number of the configuration properties can take direct values or symbolic names. This section outlines the various types of symbolic names used and their meaning.

##### Log Levels
Log level configuration defines the threshold for printed messages. The supported values in order of least verbosity are `OFF`, `FATAL`, `WARN`, `INFO`, `DEBUG` and `TRACE`.


##### Network Address Configuration
Where a network address needs to be entered, you can provide either a direct IP address, a DNS name or the special symbol `BROADCAST` which will resolve to the broadcast address of whatever NIC is being used.


##### Network Interface Configuration
Where you need to specify a netwokr interface, you can do so again via either an IP address or DNS name. This will choose the nic to which that address/name is bound.

Alternatively, you can specify one of the following symbols:

  - `LOOPBACK`: `127.0.0.1`
  - `LINK_LOCAL`: `169.254.1.0` - `169.254.254.255` inclusive
  - `SITE_LOCAL`: Common LAN / Local Site addresses:
    - `10.0.0.0/8 (255.0.0.0)`
    - `172.16.0.0/12 (255.240.0.0)`
    - `192.168.0.0/16 (255.255.0.0)`  
  - `GLOBAL` - Any routable address that is not one of the above

The first NIC found to have an address that matches whatever you specified will be used.


##### DIS Exercise IDs
For incoming packet filtering, the DIS exercise ID setting can be specified as either a number, or as `<any>` (will take from any exercise).

##### DIS Site/App IDs
Site and Application IDs can be directly specified, or the symbol `<random>` can be used, in which case Disco will randomly assign a value from within the supported range.

##### File Path Configuration
File path configurations should use `/`as the directory separator and can include the `<name>` symbol, which will be substitued for the name of the link.

Filtering
---------------
Filtering can be applied to all PDUs processed by the distributor in one of two directions:

  - **Inbound**: `Link->Reflector` - Filter out packets received by the native souce, preventing them from being passed to the reflector for forwarding. Prevents Reflector ever receiving them.
  - **Outbound**: `Reflector->Link` - Filter out packets being passed from the reflector to a link. Prevents a single link receiving non-matching packets.

In configuration, filtering is applied at the top level of a link much like the `mode` parameter:

```
distributor.[link].filtering.in  = entity.id == 1.1.*.1.2.3.4
distributor.[link].filtering.out = entity.domain == Air or entity.domain == Land
```
 > The special value of `<none>` can be used to signal no filtering, or the clause can be left out


### Filtering Clauses
A filtering clause has the standard format of `[field] [operator] [value]`

  - Field: The field to query on (see below)
  - Operator: The type of filter we are applying
	  - Equals: `==`
	  - Does not Equal: `!=`
  - Value: The value to check for. Can include wildcards

A filtering clause is a query string kind of like what is present in Wireshark. You can include a number of clauses inside a query string, linked together via `and` or `or` declarations.

#### Types, Values and Wildcards
The value part of a clause can include wild cards. How these are applied depends on the type of field that is being interrogated:

  - **String**: `*Tim*` - match any string with "Tim" in it
  - **ID**: `1-7-*` - match any entity ID with a site id of `1` and an app id of `7`
  - **Enumeration**: `1.1.*.1.2.3.4` - match an enum with any country code and the other values as specified
	  - Alternate format: `1 1 * 1 2 3 4`
	  - Alternate format: `1-1-*-1-2-3-4`
  - **Special** - Choose from a pre-defined set of values. Treated as string, with valid values limited to those in the set. Wildcards not supported.

Note that for IDs and Enumerations you must specifiy the full format (three digits for IDs and seven for enumerations).

#### Combining Clauses
You can link multiple clauses together with `and` or `or` clauses:

  - `and`, `&&` - must match both clauses before and after
  - `or`, `||` - must match at least one of the clauses before and after
  - `()` - group clauses so that they are assessed as a single block


### Available Fields
The list below describes all the fields that can be filtered on and the valid types/values for them:

  - **PDU Fields**
	  - `pdu.type` - EntityState, Fire, Detonation, Signal, Transmitter, Designator, Emission, Data, SetData, ... (see `PduType.java`)
	  - `pdu.version` - Number (1-7)
	  - `pdu.exerciseId` - Number
	  - `pdu.siteId` - Number
	  - `pdu.appId` - Number
	  - `pdu.family` - Entity, Warfare, Logistics, Radio, SimManagement, Emission
  - **Entity State Fields**
	  - `entity.id` - id
	  - `entity.type` - enumeration
	  - `entity.marking` - string
	  - `entity.kind` - Platform, Lifeform, Munition, Radio, Emitter
	  - `entity.domain` - Land, Surface, Air, Subsurface, Space
	  - `entity.force` - Friendly, Opposing, Neutral, Other



Example Configurations
----------------------
This second contains some example configurations to give you a picture of how you might set up a Distributor(s) to support a use case.

### Local DIS Port-to-Port Bridge
This requires a single Distributor instance running on a computer that has network access to both DIS domains.

They might be separated on ports, broadcast domain, multicast addresses or some combination of these. In the example below, one network is broadcast while the other is multicast.

```
distributor.links = networkA, networkB

# Broadcast Network
distributor.networkA.mode           = dis
distributor.networkA.dis.address    = BROADCAST    << broadcast
distributor.networkA.dis.port       = 3000         << different port
distributor.networkA.dis.nic        = SITE_LOCAL
distributor.networkA.dis.exerciseId = <any>
distributor.networkA.dis.siteId     = <random>
distributor.networkA.dis.appId      = <random>
distributor.networkA.dis.loglevel   = ERROR
distributor.networkA.dis.logtofile  = on
distributor.networkA.dis.logfile    = logs/distributor.<name>.log
distributor.networkA.filtering.in   = <none>
distributor.networkA.filtering.out  = <none>

# Multicast Network
distributor.networkB.mode           = dis
distributor.networkB.dis.address    = 239.17.1.1   << multicast
distributor.networkB.dis.port       = 3333         << different port
distributor.networkB.dis.nic        = SITE_LOCAL
distributor.networkB.dis.exerciseId = <any>
distributor.networkB.dis.siteId     = <random>
distributor.networkB.dis.appId      = <random>
distributor.networkB.dis.loglevel   = ERROR
distributor.networkB.dis.logtofile  = on
distributor.networkB.dis.logfile    = logs/distributor.<name>.log
distributor.networkB.filtering.in   = <none>
distributor.networkB.filtering.out  = <none>

```


### Site-to-Site Long Haul
Long haul communications requires a computer that is point-to-point accessible on the port `4919` (configurable) to be the relay.

An instance of the distributor will run at each site, sitting on the local DIS network. It will also have a WAN link loaded that is pointed at the relay. To bridge two networks you will likely run three instances. One at each network, and the relay somewhere accessible*

_Computer running in cloud at `example.openlvc.org`:_
```
distributor.links = backbone
distributor.backbone.mode            = relay
distributor.backbone.relay.address   = GLOBAL
distributor.backbone.relay.port      = 4919
distributor.backbone.relay.transport = tcp

```
The relay link exists to accept new connections and turn them into WAN links. When remote connections with WAN links connect to the relay, a new transient WAN link will be created and loaded into the relay distributor instance. 

_Computer running at one of the sites:_
```
distributor.links = local, longhaul

# Connect to local DIS network
distributor.local.mode           = dis
distributor.local.dis.address    = BROADCAST
distributor.local.dis.port       = 3000
distributor.local.dis.nic        = SITE_LOCAL
distributor.local.dis.exerciseId = <any>
distributor.local.dis.siteId     = <random>
distributor.local.dis.appId      = <random>
distributor.local.dis.loglevel   = ERROR
distributor.local.dis.logtofile  = on
distributor.local.dis.logfile    = logs/distributor.<name>.log

# Connect into the backbone
distributor.longhaul.mode                 = wan
distributor.longhaul.wan.relay            = virpac.openlvc.org
distributor.longhaul.wan.port             = 4919
distributor.longhaul.wan.transport        = tcp
distributor.longhaul.wan.bundling         = false
distributor.longhaul.wan.bundling.maxSize = 1400b
distributor.longhaul.wan.bundling.maxTime = 30


```

 > *The relay could run inside one of the local Distributor instances if that computer were point-to-point accessible to the other.


License
========
The full Disco library is distributed under the Apache Software License v2.  
