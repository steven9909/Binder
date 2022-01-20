package repository

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.util.*
import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.Permission
import resultCatching

class GoogleDriveRepository(private val driveService: Drive) {

    fun createFolder(folderName: String) = resultCatching {
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.id = null
        fileMetadata.mimeType = "application/vnd.google-apps.folder"

        val result = driveService.files().create(fileMetadata)
            .setFields("name")
            .setFields("id")
            .execute()

        val userPermission = Permission()
            .setType("anyone")
            .setRole("reader")

        driveService.permissions().create(result.id, userPermission).execute()

        result.id
    }

    fun doesFolderExist(folderName: String) = resultCatching {
        val result = driveService.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder'")
            .setSpaces("drive")
            .execute()
        for (file in result.files) {
            if (file.name == folderName) {
                return@resultCatching file.id
            }
        }
        return@resultCatching null
    }

    fun uploadFile(folderId: String?, mimeType: String?, localFile: java.io.File) = resultCatching {
        val root: List<String> = if (folderId == null) {
            Collections.singletonList("root")
        } else {
            Collections.singletonList(folderId)
        }
        val metadata = File()
            .setParents(root)
            .setMimeType(mimeType)
            .setName(localFile.name)

        val fileContent = FileContent(mimeType, localFile)

        val file = driveService.files()
            .create(metadata, fileContent)
            .setFields("id")
            .setFields("webViewLink")
            .execute()

        val userPermission = Permission()
            .setType("anyone")
            .setRole("reader")

        val permission = driveService.permissions().create(file.id, userPermission)
            .setFields("name")
            .execute()

        file.webViewLink
    }

}