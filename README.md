# McDb

A database pool library for Spigot servers.

*Copyright (c) 2023 Caleb L. Power. All rights reserved.*

## Database Support

This plugin currently supports the following database servers.

- MariaDB
- MySQL

## Building

You can build this software with Gradle, provided you have the proper build
environment installed. We recommend OpenJDK 21. The Gradle Wrapper has been
included in this project. Although there is discourse on the subject, it has
been included to ensure the proper Gradle version is adhered to and to make it
easier to build the software.

On *NIX-like systems, you can use the following to build the project.

```bash
./gradlew build publishToMavenLocal
```

Likewise, on *doze systems, you can use the following.

```bash
gradlew.bat build publishToMavenLocal
```

The above should yield the plugin JAR at `build/libs/McDbDriver.jar`.

## Installation

This plugin was designed for Spigot v1.20.1. You'll want to ensure that you're
using Java 21 LTS. Like normal, just put `McDbDriver.jar` in your `plugins`
folder and start the server. On first launch, a default `config.json` file will
be created and the plugin will automatically disable itself. You can configure
the plugin two different ways.

## Configuration via Config File

This method is recommended if you have multiple plugins that use the same
database.

| Parameter             | Type    | Description                                                                         |
|:----------------------|:--------|:------------------------------------------------------------------------------------|
| .databases[].label    | string  | The key that you'll use in your software to retrieve a connection to this database. |
| .databases[].location | string  | Formatted `ip:port/db`, this specifies the location of the database.                |
| .databases[].username | string  | The username that corresponds with your database account credentials.               |
| .databases[].password | string  | The password that corresponds with your database account credentials.               |
| .databases[].isSecure | boolean | True if and only if a secure connection to the database should be established.      |

## Configuration at Runtime

This method is recommended if you'd like to manage your database with a single
plugin. You can use the `addDatabase()` at runtime to add a database to the pool
and `removeDatabase()` when you're finished with it. Please consult the Javadocs
for usage.

## Usage

You will first want to get an `McDbAPi` instance via the static method
`McDbApi.getInstance()`. At that point you can retrieve a database with the
`connect()` method. You will need to specify the label associated with your
database. Note that one database can have multiple labels. Please consult the
Javadocs for more information.

Don't forget that this library needs to be either `provided` (for Maven) or set
to `compileOnly` (for Gradle).
