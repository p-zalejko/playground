# Jooq code generation using Testcontainers and Flyway

It's a solution inspired by the https://github.com/jOOQ/jOOQ/issues/6551#issuecomment-908494321 . It leverages:
- `groovy-maven-plugin` for starting PostgreSQL using Testcontainers
- `flyway-maven-plugin` for applying schema changes
- `jooq-codegen-maven` for generating jooq code

As a result, it allows for (re)generating Jooq code based on a database launched by TestContainers. Schema changes are shipped by Flyway. Everything happens on-the-fly. Just execute `mvn clean verify`.