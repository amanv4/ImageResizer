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

package com.t8rin.imagetoolbox.feature.image_stacking.domain

import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.domain.model.Position

data class StackImage(
    val uri: String,
    val alpha: Float,
    val blendingMode: BlendingMode,
    val position: Position,
    val scale: Scale
) {
    enum class Scale {
        None, Fill, Fit, FitWidth, FitHeight, Crop
    }
}

fun String.toStackImage() = StackImage(
    uri = this,
    alpha = 1f,
    blendingMode = BlendingMode.SrcOver,
    position = Position.TopLeft,
    scale = StackImage.Scale.Fit
)