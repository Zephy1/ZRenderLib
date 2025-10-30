package org.zephy.zrenderlib

//#if MC == 10809 || MC >= 12100
import java.awt.Color

//#if MC>12100
import net.minecraft.client.gui.DrawContext
//#endif

abstract class BaseGUIRenderer {
    @JvmOverloads
    fun drawStringWithShadowRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f
    ) {
        drawString(
            //#if MC>12100
            drawContext,
            //#endif
            text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, true, maxWidth, zOffset
        )
    }

    @JvmOverloads
    fun drawStringWithShadow(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        text: String, xPosition: Float, yPosition: Float, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, textScale: Float = 1f, renderBackground: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f
    ) {
        drawString(
            //#if MC>12100
            drawContext,
            //#endif
            text, xPosition, yPosition, color, textScale, renderBackground, true, maxWidth, zOffset
        )
    }

    @JvmOverloads
    fun drawStringRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        text: String, xPosition: Float, yPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, textScale: Float = 1f, renderBackground: Boolean = false, textShadow: Boolean = false, maxWidth: Int = 512, zOffset: Float = 0f
    ) {
        drawString(
            //#if MC>12100
            drawContext,
            //#endif
            text, xPosition, yPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), textScale, renderBackground, textShadow, maxWidth, zOffset
        )
    }

    abstract fun drawString(
        //#if MC>12100
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
        zOffset: Float = 0f
    )

    @JvmOverloads
    fun drawLineRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        startX: Float, startY: Float, endX: Float, endY: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, lineThickness: Float = 1f, zOffset: Float = 0f
    ) {
        drawLine(
            //#if MC>12100
            drawContext,
            //#endif
            startX, startY, endX, endY, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), lineThickness, zOffset
        )
    }

    abstract fun drawLine(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        lineThickness: Float = 1f,
        zOffset: Float = 0f
    )

    @JvmOverloads
    fun drawSquareRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f
    ) {
        drawRect(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    @JvmOverloads
    fun drawSquare(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, zOffset: Float = 0f
    ) {
        drawRect(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, size, size, color, zOffset
        )
    }

    @JvmOverloads
    fun drawRectRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, width: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f
    ) {
        drawRect(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    abstract fun drawRect(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawRoundedRectRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, width: Float, height: Float, radius: Float = 4f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, flatCorners: List<RenderUtils.FlattenRoundedRectCorner> = emptyList(), segments: Int = 16, zOffset: Float = 0f
    ) {
        drawRoundedRect(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, width, height, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), flatCorners, segments, zOffset
        )
    }

    abstract fun drawRoundedRect(
        //#if MC>12100
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
    )

    @JvmOverloads
    fun drawSimpleGradient(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        x: Float, y: Float, width: Float, height: Float, startColor: Color, endColor: Color, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f
    ) {
        val startColorLong = RenderUtils.RGBAColor(startColor.red, startColor.green, startColor.blue, startColor.alpha).getLong()
        val endColorLong = RenderUtils.RGBAColor(endColor.red, endColor.green, endColor.blue, endColor.alpha).getLong()
        drawSimpleGradient(
            //#if MC>12100
            drawContext,
            //#endif
            x, y, width, height, startColorLong, endColorLong, direction, zOffset
        )
    }

    @JvmOverloads
    fun drawSimpleGradientRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        x: Float, y: Float, width: Float, height: Float, startRed: Int = 255, startGreen: Int = 255, startBlue: Int = 255, startAlpha: Int = 255, endRed: Int = 0, endGreen: Int = 0, endBlue: Int = 0, endAlpha: Int = 255, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f
    ) {
        val startColor = RenderUtils.RGBAColor(startRed, startGreen, startBlue, startAlpha).getLong()
        val endColor = RenderUtils.RGBAColor(endRed, endGreen, endBlue, endAlpha).getLong()
        drawSimpleGradient(
            //#if MC>12100
            drawContext,
            //#endif
            x, y, width, height, startColor, endColor, direction, zOffset
        )
    }

    @JvmOverloads
    fun drawSimpleGradient(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        x: Float, y: Float, width: Float, height: Float, startColor: Long = RenderUtils.WHITE, endColor: Long = RenderUtils.BLACK, direction: RenderUtils.GradientDirection = RenderUtils.GradientDirection.TOP_LEFT_TO_BOTTOM_RIGHT, zOffset: Float = 0f
    ) {
        val gradientColors = RenderUtils.getGradientColors(direction, startColor, endColor)
        drawGradient(
            //#if MC>12100
            drawContext,
            //#endif
            x, y, width, height, gradientColors.topLeft, gradientColors.topRight, gradientColors.bottomLeft, gradientColors.bottomRight, zOffset
        )
    }

    abstract fun drawGradient(
        //#if MC>12100
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
        zOffset: Float = 0f,
    )

    @JvmOverloads
    fun drawSimpleCircleRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, radius: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, zOffset: Float = 0f
    ) {
        drawCircle(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, 0f, 0f, 0f, zOffset
        )
    }

    @JvmOverloads
    fun drawSimpleCircle(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, radius: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, edges: Int = 32, zOffset: Float = 0f
    ) {
        drawCircle(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, radius, radius, color, edges, 0f, 0f, 0f, zOffset
        )
    }

    @JvmOverloads
    fun drawCircleRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        xPosition: Float, yPosition: Float, xScale: Float = 1f, yScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, edges: Int = 32, rotationDegrees: Float = 0f, xRotationOffset: Float = 0f, yRotationOffset: Float = 0f, zOffset: Float = 0f
    ) {
        drawCircle(
            //#if MC>12100
            drawContext,
            //#endif
            xPosition, yPosition, xScale, yScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), edges, rotationDegrees, xRotationOffset, yRotationOffset, zOffset
        )
    }

    abstract fun drawCircle(
        //#if MC>12100
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
    )

    @JvmOverloads
    fun drawImageRGBA(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        image: Image, xPosition: Float, yPosition: Float, width: Float? = null, height: Float? = null, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, zOffset: Float = 0f
    ) {
        drawImage(
            //#if MC>12100
            drawContext,
            //#endif
            image, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset
        )
    }

    abstract fun drawImage(
        //#if MC>12100
        drawContext: DrawContext,
        //#endif
        image: Image,
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f
    )
}
//#endif
