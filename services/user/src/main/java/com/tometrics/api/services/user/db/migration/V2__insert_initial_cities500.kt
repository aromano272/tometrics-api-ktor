package com.tometrics.api.services.user.db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Types

@Suppress("unused", "ClassName")
class V2__insert_initial_cities500 : BaseJavaMigration() {

    private lateinit var iso2ToCountryName: Map<String, String>
    private lateinit var admin1CodeToNameAndAscii: Map<String, Pair<String, String>>
    private lateinit var admin2CodeToNameAndAscii: Map<String, Pair<String, String>>


    override fun migrate(context: Context) {
        val connection = context.connection
        val insertSql = """
            INSERT INTO geoname_cities_500 (
                geonameid, name, asciiname, alternatenames, 
                latitude, longitude, feature_class, feature_code,
                country, country_code, cc2, admin1, asciiadmin1, 
                admin2, asciiadmin2, admin3_code, admin4_code, 
                population, elevation, dem, timezone, modification_date
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val statement = connection.prepareStatement(insertSql)

        iso2ToCountryName = parseCountries()
        admin1CodeToNameAndAscii = parseAdmin("geolocation/admin1CodesASCII.txt")
        admin2CodeToNameAndAscii = parseAdmin("geolocation/admin2Codes.txt")

        // Read the cities500.txt file from resources
        val citiesInputStream = javaClass.classLoader.getResourceAsStream("geolocation/cities500.txt")

        if (citiesInputStream == null) {
            throw RuntimeException("Could not find cities500.txt in resources")
        }

        BufferedReader(InputStreamReader(citiesInputStream)).use { reader ->
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

    private fun parseCountries(): Map<String, String> {
        // Read the countryInfo.txt file from resources
        val countriesInputStream = javaClass.classLoader.getResourceAsStream("geolocation/countryInfo.txt")

        if (countriesInputStream == null) {
            throw RuntimeException("Could not find countryInfo.txt in resources")
        }

        val countries = mutableMapOf<String, String>()

        BufferedReader(InputStreamReader(countriesInputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val fields = line?.split("\t") ?: continue
                val iso2 = fields[0]
                val name = fields[4]
                countries[iso2] = name
            }
        }

        return countries
    }

    private fun parseAdmin(filename: String): Map<String, Pair<String, String>> {
        val inputStream = javaClass.classLoader.getResourceAsStream(filename)

        if (inputStream == null) {
            throw RuntimeException("Could not find $filename in resources")
        }

        val data = mutableMapOf<String, Pair<String, String>>()

        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val fields = line?.split("\t") ?: continue
                val code = fields[0]
                val name = fields[1]
                val ascii = fields[2]
                data[code] = name to ascii
            }
        }

        return data
    }

    private fun processLine(line: String, statement: PreparedStatement, connection: Connection) {
        // Split the line by tab character
        val fields = line.split("\t")

        if (fields.size < 19) {
            println("Warning: Skipping line with insufficient fields: $line")
            return
        }

        try {
            val iso2 = fields[8]
            val country = iso2ToCountryName[iso2]!!
            val admin1Code = "$iso2.${fields[10]}"
            val admin2Code = "$admin1Code.${fields[11]}"
            val pair1 = admin1CodeToNameAndAscii[admin1Code]
            val admin1 = pair1?.first
            val asciiadmin1 = pair1?.second
            val pair2 = admin2CodeToNameAndAscii[admin2Code]
            val admin2 = pair2?.first
            val asciiadmin2 = pair2?.second

            // Set values for each column
            statement.setInt(1, fields[0].toInt()) // geonameid
            statement.setString(2, fields[1]) // name
            statement.setString(3, fields[2]) // asciiname
            statement.setString(4, fields[3]) // alternatenames
            statement.setDouble(5, fields[4].toDouble()) // latitude
            statement.setDouble(6, fields[5].toDouble()) // longitude
            statement.setString(7, fields[6]) // feature_class
            statement.setString(8, fields[7]) // feature_code
            statement.setString(9, country)
            statement.setString(10, fields[8]) // country_code
            statement.setString(11, fields[9]) // cc2
            statement.setString(12, admin1)
            statement.setString(13, asciiadmin1)
            statement.setString(14, admin2)
            statement.setString(15, asciiadmin2)
            statement.setString(16, fields[12]) // admin3_code
            statement.setString(17, fields[13]) // admin4_code
            statement.setLong(18, fields[14].toLongOrNull() ?: 0) // population

            // Handle nullable fields
            val elevation = fields[15].toIntOrNull()
            if (elevation != null) {
                statement.setInt(19, elevation)
            } else {
                statement.setNull(19, Types.INTEGER)
            }

            val dem = fields[16].toIntOrNull()
            if (dem != null) {
                statement.setInt(20, dem)
            } else {
                statement.setNull(20, Types.INTEGER)
            }

            statement.setString(21, fields[17]) // timezone
            statement.setString(22, fields[18]) // modification_date

            statement.addBatch()
        } catch (e: Exception) {
            println("Error processing line: $line")
            println("Exception: ${e.message}")
            // Continue with the next line
        }
    }
}