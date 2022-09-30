package com.machiav3lli.backup.ui.compose.icons.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.machiav3lli.backup.ui.compose.icons.Icon

val Icon.IcLabel: ImageVector
    get() {
        if (_icLabel != null) {
            return _icLabel!!
        }
        _icLabel = Builder(
            name = "IcLabel", defaultWidth = 32.0.dp, defaultHeight = 32.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.63f, 5.84f)
                curveTo(17.27f, 5.33f, 16.67f, 5.0f, 16.0f, 5.0f)
                lineTo(5.0f, 5.01f)
                curveTo(3.9f, 5.01f, 3.0f, 5.9f, 3.0f, 7.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 1.99f, 2.0f, 1.99f)
                lineTo(16.0f, 19.0f)
                curveToRelative(0.67f, 0.0f, 1.27f, -0.33f, 1.63f, -0.84f)
                lineTo(22.0f, 12.0f)
                lineToRelative(-4.37f, -6.16f)
                close()
                moveTo(16.0f, 17.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(11.0f)
                lineToRelative(3.55f, 5.0f)
                lineTo(16.0f, 17.0f)
                close()
            }
        }
            .build()
        return _icLabel!!
    }

private var _icLabel: ImageVector? = null