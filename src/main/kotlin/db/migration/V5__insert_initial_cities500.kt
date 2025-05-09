package com.tometrics.api.db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.PreparedStatement


@Suppress("unused", "ClassName")
class V5__insert_initial_cities500 : BaseJavaMigration() {
    override fun migrate(context: Context) {
        val connection = context.connection
        val insertSql = """
            INSERT INTO geoname_cities_500 (
                geonameid, name, asciiname, alternatenames, 
                latitude, longitude, feature_class, feature_code,
                country_code, cc2, admin1_code, admin2_code, 
                admin3_code, admin4_code, population, elevation,
                dem, timezone, modification_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val statement = connection.prepareStatement(insertSql)

        // Read the cities500.txt file from resources
        val inputStream = javaClass.classLoader.getResourceAsStream("cities500.txt")

        if (inputStream == null) {
            throw RuntimeException("Could not find cities500.txt in resources")
        }

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String?
            var batchCount = 0
            val batchSize = 1000 // Process in batches for better performance

            while (reader.readLine().also { line = it } != null) {
                line?.let { processLine(it, statement, connection) }

                batchCount++
                if (batchCount % batchSize == 0) {
                    statement.executeBatch()
                    statement.clearBatch()
                }
            }

            // Execute any remaining batch
            statement.executeBatch()
        }

        statement.close()
    }

    private fun processLine(line: String, statement: PreparedStatement, connection: Connection) {
        // Split the line by tab character
        val fields = line.split("\t")

        if (fields.size < 19) {
            println("Warning: Skipping line with insufficient fields: $line")
            return
        }

        try {
            // Set values for each column
            statement.setInt(1, fields[0].toInt()) // geonameid
            statement.setString(2, fields[1]) // name
            statement.setString(3, fields[2]) // asciiname
            statement.setString(4, fields[3]) // alternatenames
            statement.setDouble(5, fields[4].toDouble()) // latitude
            statement.setDouble(6, fields[5].toDouble()) // longitude
            statement.setString(7, fields[6]) // feature_class
            statement.setString(8, fields[7]) // feature_code
            statement.setString(9, fields[8]) // country_code
            statement.setString(10, fields[9]) // cc2
            statement.setString(11, fields[10]) // admin1_code
            statement.setString(12, fields[11]) // admin2_code
            statement.setString(13, fields[12]) // admin3_code
            statement.setString(14, fields[13]) // admin4_code
            statement.setLong(15, fields[14].toLongOrNull() ?: 0) // population

            // Handle nullable fields
            val elevation = fields[15].toIntOrNull()
            if (elevation != null) {
                statement.setInt(16, elevation)
            } else {
                statement.setNull(16, java.sql.Types.INTEGER)
            }

            val dem = fields[16].toIntOrNull()
            if (dem != null) {
                statement.setInt(17, dem)
            } else {
                statement.setNull(17, java.sql.Types.INTEGER)
            }

            statement.setString(18, fields[17]) // timezone
            statement.setString(19, fields[18]) // modification_date

            statement.addBatch()
        } catch (e: Exception) {
            println("Error processing line: $line")
            println("Exception: ${e.message}")
            // Continue with the next line
        }
    }
}
