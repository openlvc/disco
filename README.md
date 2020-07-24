DISCO: DIS Combined Offensive
===================
-------------------

Welcome to OpenLVC's Disco project!

Disco is a library and a set of tools for working with and wrangling IEEE-1278 **D**istributed **I**nteractive **S**imulation networks.

The project began as a clean, simple, high-performance DIS library designed to make writing DIS
applications simple. It has since grown to add a number of additional tools including PDU logging,
load testing, bridging, filtering and more. Everything you need to stage your DIS-based offensive!

The project itself can be broken down into the following pieces:

  - **Disco**: The core java-based DIS library with HLA/RPR connectivity support
  - **Disruptor**: Traffic generator for load testing
  - **Distributor**: DIS network bridging, site-to-site connections and filtering
  - **Duplicator**: Record and reply DIS network traffic
  - **Disrespector**: Bridge between DIS and HLA/RPR networks  


Getting Started
----------------
The easiest way to get started is to download one of the binary packages from the distribution site here:

  - http://distribution.openlvc.org/disco (Coming Soon)

If you are a **simulation administrator** then you will find all the launchers inside the `bin` directory.
You can also find additional application-specific docs in the `docs` directory.

_For those looking in the source repo, they are located in `codebase/resources/dist/common/docs`_

If you are a **simulation developer** then you can also download the binary package. To start using Disco
in your application you will need the Disco jar, found in `lib/disco.jar`. All its dependencies are
located in the `lib/` directory. Example code can be found in `examples/` (coming soon).

_For those looking in the source repo, example code is in `codebase/src/java/example`._


Binary Package Structure
-------------------------
The structure of the binary packages is as follow. Anything with a `*` next to it is still in development.

```
disco-1.x.x/
├── README.md                     << this file!
│
├── bin/
│   ├── disruptor.sh / bat        << launch the disruptor load testing tool
│   ├── distributor.sh / bat      << launch the distributor bridging tool
│   ├── duplicator.sh / bat       << launch the duplicator logging tool
│   └── disrespector.sh / bat     << launch the disrespector DIS/HLA bridging tool
│
├── etc/
│   ├── disco.config              << sample disco config (all can be set in code)
│   ├── disruptor.config          << sample disruptor config
│   ├── disruptor.plan            << sample disruptor plan
│   ├── disruptor.plan.5000       << sample disruptor plan with 5000 entities
│   ├── distributor.config        << annotated distributor config
│   ├── distributor-local.config  << sample distributor config for local bridge
│   └── distributor-relay.config  << sample distributor config for remote relay
│
├── lib/
│   ├── disco.jar                 << disco library
│   └── log4j/                    << dependencies
│
├── documentation/
│   ├── Welcome to Disco.md*      << disco programmer's guide
│   ├── Disruptor.md*             << disruptor documentation
│   ├── Distributor.md            << distributor documentation
│   └── javadoc/                  << API documentation
│
├── examples/
│   ├── espdu-emitter*/           << code for example that emits entity state pdus
│   └── warfare*/                 << example code showing entities, fires and dets
│
```

Compiling
----------
Compiling Disco is extremely simple. You are expected to have a valid JDK (8u60+) on your computer and
accessible from the system path.

To generate **releases** you will also need to set the path to it inside `codebase/local.properties` (copy
`build.properties` to `local.properties` as it is Git ignored and will let you define local machine
overrides).

Disco ships with an embedded copy of Ant and shell scripts to run everything. The major targets are:

```
 $ cd codebase
 $ ./ant sandbox        << generates an "exploded" install in dist/disco-x.x.x
 $ ./ant release        << clean, test and general zip file release in dist
 $ ./ant clean          << burninate the peasants
 $ ./ant -projecthelp   << get some more deets
```

The typical way we use it is to run `./ant sandbox` and then cd into `dist/disco-x.x.x` where there is now
effectively a Disco installation.

If you run `./ant -projecthelp` you'll get the following:

```
Buildfile: D:\Developer\workspace\opensource\disco\codebase\build.xml
     [echo] Build Version: disco-1.0.0 (build 0)
 [platform] Operating System platform is: win64

            .___.__
          __| _/|__| ______ ____  ____
         / __ | |  |/  ___// ___\/  _ \
        / /_/ | |  |\___ \\  \__(  ( ) )
        \____ | |__/____  >\___  >____/
             \/         \/     \/
        Open LVC Disco is a high-performance, pure-Java library for working
        with Distributed Interactive Simulaion PDUs and applications.

Main targets:

 build.release  Set the release build flag for this run
 clean          Removes all generated build artefacts
 compile        Compile all the production code
 installer      Create an installer package from the sandbox
 java.compile   Compile the main projection and test modules
 release        Clean, run all test sand generate a standard release package
 release.thin   Generate a standard release package, but skip the tests
 sandbox        Create a sandbox environment to test and validate in
 test           Compile and run the automated test suite
Default target: sandbox
```

Getting Help
-------------
We're here to help!

Really. We are. We try to answer all queries in between the day job, so some patience is appreciated (as are pull requests!) but please ask away. Just open an Issue on the GitHub repo and we'll respond.


License
--------
Disco is released under the Apache Software License v2.
