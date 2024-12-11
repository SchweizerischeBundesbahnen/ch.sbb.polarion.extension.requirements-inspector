[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=bugs)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_ch.sbb.polarion.extension.requirements-inspector)

# Polarion ALM extension to analyze WI quality

This Extension provides the possibility to inspect WI descriptions and writes the feedback to the Custom Field "Smell Description" of each inspected WI.
The inspector is implemented in [scripts](https://github.com/SchweizerischeBundesbahnen/python-requirements-inspector/).

## Build

This extension can be produced using maven:

```bash
mvn clean package
```

## Installation to Polarion

To install the extension to Polarion `ch.sbb.polarion.extension.requirements-inspector-<version>.jar`
should be copied to `<polarion_home>/polarion/extensions/ch.sbb.polarion.extension.requirements-inspector/eclipse/plugins`
It can be done manually or automated using maven build:

```bash
mvn clean install -P install-to-local-polarion
```

For automated installation with maven env variable `POLARION_HOME` should be defined and point to folder where Polarion is installed.

Changes only take effect after restart of Polarion.

## Polarion configuration

### Scheduler

This Extension must be added as Job to the Scheduler like this:

    <job cronExpression="0 0 0 * * ?" id="requirementsInspection" name="[your task name]" scope="project:[your project]">
        <types>[WI Type ID], [WI Type ID], ...</types>
        <addMissingLanguage>[true/false]</addMissingLanguage>
        <filter>[additional filter query]</filter>
        <inspectTitle>[true/false]</inspectTitle>
        <inspectFields>[field ID], [field ID], ...</inspectFields>
    </job>

All values in brackets needs to be changed to your use case.

The following Parameters are available:

- **types**: Comma-seperated type ids of workitems to be inspected.
- **addMissingLanguage** (Optional): If set to true it updates the language field if it wasn't set and the plugin could detect the language. default false.
- **filter** (Optional): An filter query that will be added with AND to the existing query to filter for specific workitems.
- **inspectTitle** (Optional): If set to true the plugin will inspect the title (currently only Processword check) and doesn't check if set to false. If not defined the default value is true.
- **inspectFields** (Optional): Comma-seperated ids of customfields that should be inspected. If not defined the default is empty.

## Requirements-Inspector Configuration

### python_requirements_inspector as Service

This extension supports using `python_requirements_inspector` as a service. More details can be found [here](https://github.com/SchweizerischeBundesbahnen/polarion-requirements-inspector-service/).
This feature can be used by setting the following property in file `<POLARION_HOME>/etc/polarion.properties`:

```properties
ch.sbb.polarion.extension.requirements-inspector.requirements.inspector.service=http://localhost:9081
```

NOTE: If the requirements inspector service is running on the host machine's localhost and docker compose is not used, the following option can be added to docker polarion run:

```bash
docker run ... --add-host host.docker.internal:host-gateway ...
```

The service will be available under `http://host.docker.internal:9081` in the container as shown in the property above.

### Debug option

Debug logging can be switched on (`true` value) and off (`false` value) with help of following property in file `polarion.properties`:

```properties
ch.sbb.polarion.extension.requirements-inspector.debug=true
```

## Usage

### Custom Fields

The Workitems types that shall be inspected need Custom Fields for the results. If not defined beforehand they will be added as String Fields after inspection and can be defined with the correct types afterward. Following Custom Fields can
be added:

| ID                 | Name                | Type    |
|--------------------|---------------------|---------|
| smellDescription   | Smell Description   | Text    |
| smellPassive       | Smell Passive       | Integer |
| smellComplex       | Smell Complex       | Integer |
| smellComparative   | Smell Comparative   | Integer |
| smellWeakword      | Smell Weakword      | Integer |
| missingProcessword | Missing Processword | Boolean |

Also, the Language field should exist.

Wait for your scheduled time or start it manually by:

1. Open Monitor
2. Select the Task with `[your task name]`
3. Press Button "Execute now"
