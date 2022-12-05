# Otter

DB Migration Tool for Kotlin. Inspired by [harmonica](https://github.com/KenjiOhtsuka/harmonica).

[Specs](https://www.notion.so/goodgoodman/Otter-9dd4f8307c27415a8d7d2ccf2dee2768)

## V1 Roadmap

- [x] New Syntax
- [x] Type Support
- [ ] Alter
- [ ] Sequence
- [ ] Create user guide
- [ ] Down
- [ ] Target
- [ ] SEEDING
- [ ] Gradle Plugin - generate
- [ ] Gradle Plugin - migrate
- [ ] Gradle Plugin - rollback
- [ ] Gradle Plugin - check

## How-To

### Spring

Add otter dependencies and task within your application.

```kotlin
// build.gradle.kts
/** ... */
dependencies {
    /** ... */
    implementation("io.github.goodgoodjm:otter-spring-boot-starter:0.0.22")
    implementation(kotlin("script-runtime")) // for migration files of resources
    /** ... */
}

// This task is needed to boot jar file
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    requiresUnpack("**/kotlin-compiler-embeddable-*.jar")
}
/** ... */
```

Add otter variables to your application's properties.

```yaml
# application.yml
otter:
  driverClassName: ${spring.datasource.driver-class-name}
  url: ${spring.datasource.url}
  username: ${spring.datasource.username}
  password: ${spring.datasource.password}
  migrationPath: migrations
  showSql: true
```

Make your migration files at `migrationPath`.

```kotlin
// resources/migrations/M2022071016263342.kts

import io.github.goodgoodjm.otter.core.Migration
import io.github.goodgoodjm.otter.core.dsl.Constraint
import io.github.goodgoodjm.otter.core.dsl.and

object : Migration() {
    override fun up() {
        createTable("person") {
            "id" - int() constraints PRIMARY and AUTO_INCREMENT
            "name" - varchar(30)
            "message" - varchar()
            "lat" - long() constraints UNIQUE
            "nullable" - bool() constraints NULLABLE
        }

        createTable("post") {
            "id" - int() constraints PRIMARY and AUTO_INCREMENT
            "person_id" - int() foreignKey "person(id)"
        }
    }

    override fun down() {
        dropTable("person")
        dropTable("post")
    }
}
```