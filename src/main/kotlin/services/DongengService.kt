package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DongengRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDongengRepository
import java.io.File
import java.util.*

class DongengService(private val dongengRepository: IDongengRepository) {
    // Mengambil semua data dongeng
    suspend fun getAllDongengs(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val dongengs = dongengRepository.getDongengs(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar dongeng",
            mapOf(Pair("dongengs", dongengs))
        )
        call.respond(response)
    }

    // Mengambil data dongeng berdasarkan id
    suspend fun getDongengById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dongeng tidak boleh kosong!")

        val dongeng = dongengRepository.getDongengById(id)
            ?: throw AppException(404, "Data dongeng tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data dongeng",
            mapOf(Pair("dongeng", dongeng))
        )
        call.respond(response)
    }

    // Ambil data request
    private suspend fun getDongengRequest(call: ApplicationCall): DongengRequest {
        // Buat object penampung
        val dongengReq = DongengRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                // Ambil request berupa teks
                is PartData.FormItem -> {
                    when (part.name) {
                        "judul" -> dongengReq.judul = part.value.trim()
                        "sinopsis" -> dongengReq.sinopsis = part.value
                        "pesan" -> dongengReq.pesan = part.value
                        "asal" -> dongengReq.asal = part.value.trim()
                    }
                }

                // Upload file
                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/dongengs/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs() // pastikan folder ada

                    part.provider().copyAndClose(file.writeChannel())
                    dongengReq.pathGambar = filePath
                }

                else -> {}
            }

            part.dispose()
        }

        return dongengReq
    }

    // Validasi request data dari pengguna
    private fun validateDongengRequest(dongengReq: DongengRequest) {
        val validatorHelper = ValidatorHelper(dongengReq.toMap())
        validatorHelper.required("judul", "Judul tidak boleh kosong")
        validatorHelper.required("sinopsis", "Sinopsis tidak boleh kosong")
        validatorHelper.required("pesan", "Pesan tidak boleh kosong")
        validatorHelper.required("asal", "Asal tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(dongengReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar dongeng gagal diupload!")
        }
    }

    // Menambahkan data dongeng
    suspend fun createDongeng(call: ApplicationCall) {
        // Ambil data request
        val dongengReq = getDongengRequest(call)

        // Validasi request
        validateDongengRequest(dongengReq)

        // Periksa dongeng dengan judul yang sama
        val existDongeng = dongengRepository.getDongengByJudul(dongengReq.judul)
        if (existDongeng != null) {
            val tmpFile = File(dongengReq.pathGambar)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            throw AppException(409, "Dongeng dengan judul ini sudah terdaftar!")
        }

        val dongengId = dongengRepository.addDongeng(
            dongengReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data dongeng",
            mapOf(Pair("dongengId", dongengId))
        )
        call.respond(response)
    }

    // Mengubah data dongeng
    suspend fun updateDongeng(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dongeng tidak boleh kosong!")

        val oldDongeng = dongengRepository.getDongengById(id)
            ?: throw AppException(404, "Data dongeng tidak tersedia!")

        // Ambil data request
        val dongengReq = getDongengRequest(call)

        if (dongengReq.pathGambar.isEmpty()) {
            dongengReq.pathGambar = oldDongeng.pathGambar
        }

        // Validasi request
        validateDongengRequest(dongengReq)

        // Periksa dongeng dengan judul yang sama jika judul diubah
        if (dongengReq.judul != oldDongeng.judul) {
            val existDongeng = dongengRepository.getDongengByJudul(dongengReq.judul)
            if (existDongeng != null) {
                val tmpFile = File(dongengReq.pathGambar)
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }
                throw AppException(409, "Dongeng dengan judul ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika mengupload file baru
        if (dongengReq.pathGambar != oldDongeng.pathGambar) {
            val oldFile = File(oldDongeng.pathGambar)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val isUpdated = dongengRepository.updateDongeng(
            id, dongengReq.toEntity()
        )
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data dongeng!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data dongeng",
            null
        )
        call.respond(response)
    }

    // Menghapus data dongeng
    suspend fun deleteDongeng(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dongeng tidak boleh kosong!")

        val oldDongeng = dongengRepository.getDongengById(id)
            ?: throw AppException(404, "Data dongeng tidak tersedia!")

        val oldFile = File(oldDongeng.pathGambar)

        val isDeleted = dongengRepository.removeDongeng(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data dongeng!")
        }

        // Hapus data gambar jika data dongeng sudah dihapus
        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data dongeng",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar dongeng
    suspend fun getDongengImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val dongeng = dongengRepository.getDongengById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(dongeng.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}
