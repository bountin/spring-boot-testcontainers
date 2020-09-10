package org.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.get
import org.springframework.test.context.ContextConfiguration
import java.sql.Connection
import java.sql.DriverManager
import java.util.stream.Stream

@SpringBootTest(classes = [TestApplication::class])
@ContextConfiguration(initializers = [PostgreSQLInitializer::class])
class PostgreSQLInitializerTest {

    @Autowired
    private lateinit var env: ConfigurableEnvironment

    @ParameterizedTest
    @ArgumentsSource(JdbcPropertyArgumentsProvider::class)
    fun `initializer adds jdbc properties`(propertyKey: String, expectedValue: String) {
        assertThat(env[propertyKey]).isEqualTo(expectedValue)
    }

    @Test
    fun `initializer adds JDBC url property`() {
        assertThat(env["jdbc.url"]).matches("^jdbc:postgresql://[a-zA-Z0-9.]+:\\d+/$TEST_DATABASE_NAME.*$")
    }

    @Test
    fun `started container accepts database connection`() {
        val connection = DriverManager.getConnection(
                env[JDBC_URL],
                env[JDBC_USERNAME],
                env[JDBC_PASSWORD]
        )

        assertThat(connection.getSingleResult<Int>("SELECT 42")).isEqualTo(42)
    }
}

private class JdbcPropertyArgumentsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
                arguments(JDBC_USERNAME, TEST_USERNAME),
                arguments(JDBC_PASSWORD, TEST_PASSWORD),
                arguments(JDBC_DRIVER_CLASS_NAME, "org.postgresql.Driver"),
        )
    }
}

@SpringBootApplication
internal class TestApplication

private inline fun <reified T> Connection.getSingleResult(sql: String): T =
        with(createStatement().executeQuery(sql)) {
            next()
            getObject(1, T::class.java)
        }
