package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Dongeng

@Serializable
data class DongengRequest(
    var judul: String = "",
    var sinopsis: String = "",
    var pesan: String = "",
    var asal: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "judul" to judul,
            "sinopsis" to sinopsis,
            "pesan" to pesan,
            "asal" to asal,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Dongeng {
        return Dongeng(
            judul = judul,
            sinopsis = sinopsis,
            pesan = pesan,
            asal = asal,
            pathGambar = pathGambar,
        )
    }
}
