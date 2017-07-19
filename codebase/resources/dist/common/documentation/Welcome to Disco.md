Disco Distributor
===================
Welcome to Disco, provided by the Open LVC Project and licensed under the terms of the Apache Software License v2.

Disco is a library an suite of tools deisgned to support the development and deployment of Distributed Interactive Simulation (DIS) applications. It consists of:

  - **Disco**:       High Performance Java API for working with DIS applications
  - **Distributor**: Bridge to connect multiple sites together over a WAN
  - **Duplicator**:  Capture and replay sessions of DIS network traffic
  - **Disruptor**:   DIS network load tester; flood the network with traffic to performance test

Disco ships with two primary major components depending on your use:

  - **Application Launchers**: A set of Batch files and Shell scripts are contained in the `bin` directory
  - **Java JAR Library**:      If you are wrigin an application, the JAR you can link to is in `lib` (as are dependencies)

Additoinal documentation for each specific application can be found in other documents in the `docs` directory.


Structure of Disco Distribution
--------------------------------
TBA

```
 [RTI_HOME]
 |-- LICENSE.portico
 |-- README               (This file)
 |-- README-examples      (More information about the example federates)
 |-- SOURCE_CODE          (Details about where to get the source code)
 |-- examples
     `-- java
         `-- hla13        (The Java HLA v1.3 example federate)
         `-- ieee1516e    (The Java IEEE-1516e example federate)
     `-- cpp
         `-- hla13        (The C++ HLA v1.3 example federate)
         `-- ieee1516e    (The C++ IEEE-1516e example federate)
 |-- include
     `-- hla13            (HLA v1.3 headers)
     `-- dlc13            (DLC v1.3 headers) [*nix only]
     `-- ieee1516e        (IEEE-1516e headers)
 |-- lib
     |-- portico.jar      (The main Portico jar file)
     `-- gcc4             (GCC libraries for C++ interfaces)  [*nix only]
     `-- vc8              (VC8 libraries for C++ interfaces)  [windows only]
     `-- vc9              (VC9 libraries for C++ interfaces)  [windows only]
     `-- vc10             (VC10 libraries for C++ interfaces) [windows only]
 |-- bin
     |-- wanrounter       (Launchers for WAN Router bat/sh)
     `-- vc8              (VC8 DLLs)                          [windows only]
     `-- vc9              (VC9 DLLs)                          [windows only]
     `-- vc10             (VC10 DLLs)                         [windows only]
```



Running Disco Applications
---------------------------

Disco ships as a set of Java JAR files containing the compiled form of all the software, and a set of application launchers as shell scripts or batch files. 



License
========
The full Disco library is distributed under the Apache Software License v2.  
