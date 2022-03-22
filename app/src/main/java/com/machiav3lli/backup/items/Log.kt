/*
 * OAndBackupX: open-source apps backup and restore app.
 * Copyright (C) 2020  Antonios Hazim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.machiav3lli.backup.items

import android.content.Context
import com.machiav3lli.backup.handler.LogsHandler
import com.machiav3lli.backup.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDateTime

@Serializable
open class Log {
    // TODO: hg42: add ${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME} useful for stacktraces ?
    @Serializable(with = LocalDateTimeSerializer::class)
    var logDate: LocalDateTime
        private set

    val deviceName: String?

    val sdkCodename: String?

    val cpuArch: String?

    val logText: String?

    constructor(text: String, date: LocalDateTime) {
        this.logDate = date
        this.deviceName = android.os.Build.DEVICE
        this.sdkCodename = android.os.Build.VERSION.RELEASE
        this.cpuArch = android.os.Build.SUPPORTED_ABIS[0]
        this.logText = text
    }

    constructor(logFile: StorageFile) {
        try {
            logFile.inputStream()!!.use { inputStream ->
                val item = fromJson(inputStream.reader().readText())
                this.logDate = item.logDate
                this.deviceName = item.deviceName
                this.sdkCodename = item.sdkCodename
                this.cpuArch = item.cpuArch
                this.logText = item.logText
            }
        } catch (e: FileNotFoundException) {
            throw BackupItem.BrokenBackupException(
                "Cannot open $logFile",
                e
            )
        } catch (e: IOException) {
            throw BackupItem.BrokenBackupException(
                "Cannot read $logFile",
                e
            )
        } catch (e: Throwable) {
            LogsHandler.unhandledException(e, logFile)
            throw BackupItem.BrokenBackupException("Unable to process $logFile. [${e.javaClass.canonicalName}] $e")
        }
    }

    override fun toString(): String {
        return "LogItem{" +
                "logDate=$logDate" +
                ", deviceName='$deviceName'" +
                ", sdkCodename='$sdkCodename'" +
                ", cpuArch='$cpuArch'" +
                ", logText:\n$logText" +
                '}'
    }

    fun delete(context: Context): Boolean? {
        val logFile = LogsHandler(context).getLogFile(this.logDate)
        return logFile?.delete()
    }

    fun toJSON() = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString<Log>(json)
    }
}