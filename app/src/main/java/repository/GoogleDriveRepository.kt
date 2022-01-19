package repository

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.util.*
import com.google.api.client.http.FileContent
import resultCatching

class GoogleDriveRepository(private val driveService: Drive) {

    fun createFolder(folderName: String, folderId: String) = resultCatching {
        val fileMetadata = File()
        fileMetadata.name = folderName
        fileMetadata.id = folderId
        fileMetadata.mimeType = "application/vnd.google-apps.folder"

        val result = driveService.files().create(fileMetadata)
            .setFields("id")
            .execute()
        result.id == folderId
    }

    fun doesFolderExist(folderId: String) = resultCatching {
        val result = driveService.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder'")
            .setSpaces("drive")
            .execute()
        for (file in result.files) {
            if (file.id == folderId) {
                true
            }
        }
        false
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

        driveService.files().create(
            metadata,
            fileContent
        ).execute()
    }

}