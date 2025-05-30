/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.zip.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.dispatchers.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.feature.zip.domain.ZipManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

internal class AndroidZipManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ZipManager {

    override suspend fun zip(
        files: List<String>,
        onProgress: () -> Unit
    ): String = withContext(defaultDispatcher) {
        shareProvider.cacheData(
            writeData = { writeable ->
                ZipOutputStream(writeable.outputStream()).use { output ->
                    files.forEach { file ->
                        withContext(ioDispatcher) {
                            context.contentResolver.openInputStream(file.toUri()).use { input ->
                                BufferedInputStream(input).use { origin ->
                                    val entry = ZipEntry(file.toUri().getFilename())
                                    output.putNextEntry(entry)
                                    origin.copyTo(output, 1024)
                                }
                            }
                        }
                        onProgress()
                    }
                }
            },
            filename = files.firstOrNull()?.toUri()?.getFilename() ?: "temp.zip"
        ) ?: throw IllegalArgumentException("Cached to null file")
    }

    private fun Uri.getFilename(): String = DocumentFile.fromSingleUri(context, this)?.name ?: ""

}