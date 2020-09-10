package org.example

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource

class PostgreSQLInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {
        val CONTAINER = PostgreSQLContainer().also { it.start() }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        applicationContext.environment.propertySources.addFirst(getPropertySource())
    }

    private fun getPropertySource(): MapPropertySource = with(CONTAINER) {
        MapPropertySource("testcontainer-postgres", mapOf(
                JDBC_DRIVER_CLASS_NAME to driverClassName,
                JDBC_URL to jdbcUrl,
                JDBC_USERNAME to username,
                JDBC_PASSWORD to password,
        ))
    }
}

internal const val JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName"
internal const val JDBC_URL = "jdbc.url"
internal const val JDBC_USERNAME = "jdbc.username"
internal const val JDBC_PASSWORD = "jdbc.password"
