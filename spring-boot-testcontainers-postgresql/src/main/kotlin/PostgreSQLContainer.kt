package org.example

import org.rnorth.visibleassertions.VisibleAssertions
import org.slf4j.LoggerFactory.getLogger
import org.testcontainers.containers.output.Slf4jLogConsumer
import java.util.*
import kotlin.system.measureTimeMillis
import org.testcontainers.containers.PostgreSQLContainer as BasePostgreSQLContainer

/**
 * Flavored postgres container with startup improvements:
 *  - tmpfs for data directory
 */
class PostgreSQLContainer : BasePostgreSQLContainer<PostgreSQLContainer>() {

    override fun doStart() {
        val startupMillis = measureTimeMillis {
            super.doStart()
        }

        VisibleAssertions.info("Started PostgreSQL testcontainer in $startupMillis milliseconds")
        VisibleAssertions.info("PostgreSQL is available at $jdbcUrl ($username/$password)")
    }

    init {
        withUsername(TEST_USERNAME)
        withPassword(TEST_PASSWORD)
        withDatabaseName(TEST_DATABASE_NAME)

        withReuse(true)
        withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw,size=1g"))
        withLogConsumer(Slf4jLogConsumer(getLogger(this::class.java)))
    }

}

internal const val TEST_USERNAME = "user"
internal const val TEST_PASSWORD = "password"
internal const val TEST_DATABASE_NAME = "test-db"

