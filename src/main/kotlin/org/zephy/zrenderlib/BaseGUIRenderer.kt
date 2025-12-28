package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//#if MC<12100
//$$import net.minecraft.client.renderer.texture.DynamicTexture
//#else
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.texture.NativeImageBackedTexture
//#endif

abstract class BaseGUIRenderer {
    @JvmOverloads
    fun drawStringWithShadowRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(
            //#if MC>=12100
            drawContext,
            //#endif
            text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset
        )
    }

    @JvmOverloads
    fun drawStringWithShadow(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(
            //#if MC>=12100
            drawContext,
            //#endif
            text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset
        )
    }

    @JvmOverloads
    fun drawStringRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    ) {
        drawString(
            //#if MC>=12100
            drawContext,
            //#endif
            text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset
        )
    }

    abstract fun drawString(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        text: String,
        xPosition: Float,
        yPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        textScale: Float = 1f,
        renderBackground: Boolean = false,
        textShadow: Boolean = false,
        maxWidth: Int = 512,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawLineRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        drawLine(
            //#if MC>=12100
            drawContext,
            //#endif
            startX, startY, endX, endY, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), lineThickness, zOffset
        )
    }

    @JvmOverloads
    fun drawLine(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
        zOffset: Float = 0f,
    ) {
        val dx = endX - startX
        val dy = endY - startY
        val len = sqrt(dx * dx + dy * dy)
        val halfThickness = lineThickness / 2f
        val offsetX = if (len > 0) -dy / len * halfThickness else 0f
        val offsetY = if (len > 0) dx / len * halfThickness else 0f

        val vertexList = listOf(
            Pair(startX + offsetX, startY + offsetY),
            Pair(endX + offsetX, endY + offsetY),
            Pair(endX - offsetX, endY - offsetY),
            Pair(startX - offsetX, startY - offsetY)
        )
        _drawLine(
            //#if MC>=12100
            drawContext,
            //#endif
            vertexList, color, zOffset
        )
    }

    abstract fun _drawLine(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawSquareRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    @JvmOverloads
    fun drawSquare(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        drawRect(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, size, size, color, zOffset
        )
    }

    @JvmOverloads
    fun drawRectRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawRect(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    @JvmOverloads
    fun drawRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val x1 = xPosition
        val x2 = xPosition + width
        val y1 = yPosition
        val y2 = yPosition + height
        val vertexList = listOf(
            Pair(x1, y1),
            Pair(x1, y2),
            Pair(x2, y2),
            Pair(x2, y1),
        )

        _drawRect(
            //#if MC>=12100
            drawContext,
            //#endif
            vertexList, color, zOffset
        )
    }

    abstract fun _drawRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawRoundedRectRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float,
        height: Float,
        radius: Float = 4f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        drawRoundedRect(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, width, height, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), flatCorners, segments, zOffset
        )
    }

    @JvmOverloads
    fun drawRoundedRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        radius: Float = 4f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(),
        segments: Int = 16,
        zOffset: Float = 0f,
    ) {
        val x1 = xPosition
        val y1 = yPosition
        val x2 = xPosition + width
        val y2 = yPosition + height

        val centerX = (x1 + x2) / 2f
        val centerY = (y1 + y2) / 2f
        val clampedRadius = radius.coerceAtMost(minOf(width, height) / 2f)
        val flatCornersSet = flatCorners.toSet()

        val edgeVertices = mutableListOf<Pair<Float, Float>>()
        fun addCornerVertices(cx: Float, cy: Float, r: Float, startAngle: Float, endAngle: Float, segs: Int) {
            val angleStep = (endAngle - startAngle) / segs
            for (i in 1..segs) {
                val angle = Math.toRadians((startAngle + angleStep * i).toDouble())
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                edgeVertices.add(Pair(x, y))
            }
        }

        edgeVertices.add(Pair(x2 - clampedRadius, y1))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y1))
        } else {
            addCornerVertices(x2 - clampedRadius, y1 + clampedRadius, clampedRadius, 270f, 360f, segments)
            edgeVertices.add(Pair(x2, y1 + clampedRadius))
        }

        edgeVertices.add(Pair(x2, y2 - clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_RIGHT in flatCornersSet) {
            edgeVertices.add(Pair(x2, y2))
        } else {
            addCornerVertices(x2 - clampedRadius, y2 - clampedRadius, clampedRadius, 0f, 90f, segments)
            edgeVertices.add(Pair(x2 - clampedRadius, y2))
        }

        edgeVertices.add(Pair(x1 + clampedRadius, y2))
        if (RenderUtils.FlattenRoundedRectCorner.BOTTOM_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y2))
        } else {
            addCornerVertices(x1 + clampedRadius, y2 - clampedRadius, clampedRadius, 90f, 180f, segments)
            edgeVertices.add(Pair(x1, y2 - clampedRadius))
        }

        edgeVertices.add(Pair(x1, y1 + clampedRadius))
        if (RenderUtils.FlattenRoundedRectCorner.TOP_LEFT in flatCornersSet) {
            edgeVertices.add(Pair(x1, y1))
        } else {
            addCornerVertices(x1 + clampedRadius, y1 + clampedRadius, clampedRadius, 180f, 270f, segments)
            edgeVertices.add(Pair(x1 + clampedRadius, y1))
        }
        edgeVertices.add(Pair(x2 - clampedRadius, y1))

        val vertexList = mutableListOf<Pair<Float, Float>>()
        for (i in 0 until edgeVertices.size - 1) {
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[i + 1])
            vertexList.add(edgeVertices[i])
        }

        _drawRoundedRect(
            //#if MC>=12100
            drawContext,
            //#endif
            x1, y1, x2, y2, vertexList, color, zOffset
        )
    }

    abstract fun _drawRoundedRect(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawSimpleGradientRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startRed: Int = 255,
        startGreen: Int = 255,
        startBlue: Int = 255,
        startAlpha: Int = 255,
        endRed: Int = 0,
        endGreen: Int = 0,
        endBlue: Int = 0,
        endAlpha: Int = 255,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val startColor = RenderUtils.RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RenderUtils.RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(
            //#if MC>=12100
            drawContext,
            //#endif
            x, y, width, height, startColor, endColor, direction, zOffset
        )
    }

    @JvmOverloads
    fun drawSimpleGradient(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        startColor: Long = RenderUtils.WHITE,
        endColor: Long = RenderUtils.BLACK,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val gradientColors = RenderUtils.getGradientColors(direction, startColor, endColor)
        drawGradient(
            //#if MC>=12100
            drawContext,
            //#endif
            x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, direction, zOffset
        )
    }

    @JvmOverloads
    fun drawGradient(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeftColor: Long = RenderUtils.WHITE,
        topRightColor: Long = RenderUtils.WHITE,
        bottomLeftColor: Long = RenderUtils.BLACK,
        bottomRightColor: Long = RenderUtils.BLACK,
        direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT,
        zOffset: Float = 0f,
    ) {
        val x2 = x + width
        val y2 = y + height

        // This is really bad, but it works
        val vertexAndColorList = when (direction) {
            RenderUtils.GradientDirection.TOP_TO_BOTTOM,
            RenderUtils.GradientDirection.RIGHT_TO_LEFT,
            RenderUtils.GradientDirection.TOP_RIGHT_TO_BOTTOM_LEFT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x2, y, topRightColor),
                )
            }

            RenderUtils.GradientDirection.BOTTOM_TO_TOP,
            RenderUtils.GradientDirection.BOTTOM_RIGHT_TO_TOP_LEFT -> {
                listOf(
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                )
            }

            RenderUtils.GradientDirection.LEFT_TO_RIGHT,
            RenderUtils.GradientDirection.BOTTOM_LEFT_TO_TOP_RIGHT -> {
                listOf(
                    Triple(x, y, topLeftColor),
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                )
            }

            RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT -> {
                listOf(
                    Triple(x2, y, topRightColor),
                    Triple(x2, y2, bottomRightColor),
                    Triple(x, y2, bottomLeftColor),
                    Triple(x, y, topLeftColor),
                )
            }
        }

        _drawGradient(
            //#if MC>=12100
            drawContext,
            //#endif
            vertexAndColorList, zOffset
        )
    }

    abstract fun _drawGradient(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        vertexAndColorList: List<Triple<Float, Float, Long>>,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawSimpleCircleRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f, zOffset
        )
    }

    @JvmOverloads
    fun drawSimpleCircle(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        zOffset: Float = 0f,
    ) {
        drawCircle(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f, zOffset
        )
    }

    @JvmOverloads
    fun drawCircleRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        drawCircle(
            //#if MC>=12100
            drawContext,
            //#endif
            xPosition, yPosition, xScale, yScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset
        )
    }

    @JvmOverloads
    fun drawCircle(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        edges: Int = 32,
        rotationDegrees: Float = 0f,
        xRotationOffset: Float = 0f,
        yRotationOffset: Float = 0f,
        zOffset: Float = 0f,
    ) {
        val angleStep = 2f * PI.toFloat() / edges
        val rotationRadians = rotationDegrees * PI.toFloat() / 180f
        val cos = cos(rotationRadians)
        val sin = sin(rotationRadians)

        val centerX = xPosition + xRotationOffset
        val centerY = yPosition + yRotationOffset

        val vertexList = mutableListOf<Pair<Float, Float>>()

        val edgeVertices = Array(edges) { i ->
            val angle = i * angleStep
            val baseX = cos(angle) * xScale
            val baseY = sin(angle) * yScale
            val relX = baseX - xRotationOffset
            val relY = baseY - yRotationOffset
            val x = relX * cos - relY * sin + xRotationOffset + xPosition
            val y = relX * sin + relY * cos + yRotationOffset + yPosition
            Pair(x, y)
        }

        for (i in 0 until edges) {
            val nextIndex = (i + 1) % edges
            vertexList.add(Pair(centerX, centerY))
            vertexList.add(edgeVertices[i])
            vertexList.add(edgeVertices[nextIndex])
            vertexList.add(edgeVertices[i])
        }

        val minX = xPosition - xScale
        val maxX = xPosition + xScale
        val minY = yPosition - yScale
        val maxY = yPosition + yScale
        _drawCircle(
            //#if MC>=12100
            drawContext,
            //#endif
            minX, maxX,
            minY, maxY,
            vertexList, color, zOffset
        )
    }

    abstract fun _drawCircle(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        vertexList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawImageRGBA(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) {
        drawImage(
            //#if MC>=12100
            drawContext,
            //#endif
            image, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    @JvmOverloads
    fun drawImage(
        //#if MC>=12100
        drawContext: DrawContext,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) {
        val texture = image.getTexture() ?: throw IllegalStateException("Image is null.")
        val (drawWidth, drawHeight) = image.getImageSize(width, height)

        val vertexList = listOf(
            Pair(xPosition, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition + drawHeight),
            Pair(xPosition + drawWidth, yPosition),
            Pair(xPosition, yPosition)
        )
        val uvList = listOf(
            Pair(0f, 1f),
            Pair(1f, 1f),
            Pair(1f, 0f),
            Pair(0f, 0f)
        )

        _drawImage(
            //#if MC>=12100
            drawContext,
            image,
            //#endif
            texture, vertexList, uvList, color, zOffset
        )
    }

    abstract fun _drawImage(
        //#if MC>=12100
        drawContext: DrawContext,
        image: Image,
        //#endif
        texture: DynamicTexture,
        vertexList: List<Pair<Float, Float>>,
        uvList: List<Pair<Float, Float>>,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    )
}
//#endif
