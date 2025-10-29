package org.zephy.zrenderlib.legacy

import gg.essential.universal.UMinecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11;
import kotlin.math.sin
import kotlin.math.cos

object WorldRenderer {
    /**
     * Renders floating lines of text in the world
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        drawString(text, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    /**
     * Renders floating lines of text in the world
     *
     * @param text the text as a string
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     * @param maxWidth useless in legacy, included for modern parity
     */
    @JvmStatic
    @JvmOverloads
    fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
        maxWidth: Int = 512,
    ) {
        val fontRenderer = RenderUtils.getFontRenderer()
        val renderManager = RenderUtils.renderManager

        val x = xPosition - renderManager.viewerPosX
        val y = yPosition - renderManager.viewerPosY
        val z = zPosition - renderManager.viewerPosZ
        val xMultiplier = if (RenderUtils.mc.gameSettings.thirdPersonView == 2) -1 else 1
        val adjustedScale = (scale * 0.05).toFloat()

        RenderUtils
            .pushMatrix()
            .colorize_01(1f, 1f, 1f, 0.5f)
            .disableCull()
        GL11.glNormal3f(0f, 1f, 0f)
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .translate(x, y, z)
            .rotate(-RenderUtils.renderManager.playerViewY, 0f, 1f, 0f)
            .rotate(
                RenderUtils.renderManager.playerViewX * xMultiplier,
                1f,
                0f,
                0f,
            )
            .scale(-adjustedScale, -adjustedScale, adjustedScale)
            .disableLighting()
            .depthMask(false)
            .enableBlend()
            .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)

        val textWidth = fontRenderer.getStringWidth(text)
        val j = textWidth / 2f
        if (renderBackground) {
            RenderUtils
                .disableTexture2D()
                .begin(7, DefaultVertexFormats.POSITION_COLOR)
                .colorize_01(0.0f, 0.0f, 0.0f, 0.25f)
                .pos((-j - 1).toDouble(), (-1).toDouble(), 0.0)
                .pos((-j - 1).toDouble(), 8.toDouble(), 0.0)
                .pos((j + 1).toDouble(), 8.toDouble(), 0.0)
                .pos((j + 1).toDouble(), (-1).toDouble(), 0.0)
                .draw()
                .enableTexture2D()
        }

        fontRenderer.drawString(
            text,
            if (centered) -j else 0f,
            0f,
            RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
            textShadow
        )
        RenderUtils
            .colorize_01(1f, 1f, 1f, 1f)
            .depthMask(true)
            .enableBlend()
            .enableCull()
            .popMatrix()
        if (disableDepth) RenderUtils.enableDepth()
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the line through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, startZ, endX, endY, endZ, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to disable depth testing
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawLine(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val cameraPos = RenderUtils.getCameraPos()

        RenderUtils.pushMatrix()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils.lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

            .begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)
            .pos(startX, startY, startZ)
            .pos(endX, endY, endZ)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = false)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = false)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()

        RenderUtils.pushMatrix()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
            .translate(xPosition - width / 2 - cameraPos.x, yPosition - cameraPos.y - 0.5f, zPosition - depth / 2 - cameraPos.z)

            .begin(drawMode, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)
            .pos(0f, 0f, 0f)
            .pos(width, 0f, 0f)
            .pos(width, height, 0f)
            .pos(0f, height, 0f)

            .pos(0f, height, depth)
            .pos(0f, 0f, depth)
            .pos(width, 0f, depth)
            .pos(width, height, depth)

            .pos(0f, height, depth)
            .pos(0f, 0f, depth)
            .pos(0f, 0f, 0f)
            .pos(0f, height, 0f)

            .pos(width, height, 0f)
            .pos(width, 0f, 0f)
            .pos(width, 0f, depth)
            .pos(width, height, depth)

            .pos(0f, height, depth)
            .pos(0f, height, 0f)
            .pos(width, height, 0f)
            .pos(width, height, depth)

            .pos(width, 0f, depth)
            .pos(0f, 0f, depth)
            .pos(0f, 0f, 0f)
            .pos(width, 0f, 0f)
            .draw()

            .resetColor()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()

        RenderUtils.pushMatrix()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .begin(drawMode, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)
            .translate(xPosition - cameraPos.x, yPosition - cameraPos.y, zPosition - cameraPos.z)

        for (phi in 0 until segments) {
            for (theta in 0 until (segments * 2)) {
                val x1 = (xScale * sin(Math.PI * phi / segments) * cos(2.0 * Math.PI * theta / (segments * 2))).toFloat()
                val y1 = (yScale * cos(Math.PI * phi / segments)).toFloat()
                val z1 = (zScale * sin(Math.PI * phi / segments) * sin(2.0 * Math.PI * theta / (segments * 2))).toFloat()

                val x2 = (xScale * sin(Math.PI * (phi + 1) / segments) * cos(2.0 * Math.PI * theta / (segments * 2))).toFloat()
                val y2 = (yScale * cos(Math.PI * (phi + 1) / segments)).toFloat()
                val z2 = (zScale * sin(Math.PI * (phi + 1) / segments) * sin(2.0 * Math.PI * theta / (segments * 2))).toFloat()

                val x3 = (xScale * sin(Math.PI * (phi + 1) / segments) * cos(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()
                val y3 = (yScale * cos(Math.PI * (phi + 1) / segments)).toFloat()
                val z3 = (zScale * sin(Math.PI * (phi + 1) / segments) * sin(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()

                val x4 = (xScale * sin(Math.PI * phi / segments) * cos(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()
                val y4 = (yScale * cos(Math.PI * phi / segments)).toFloat()
                val z4 = (zScale * sin(Math.PI * phi / segments) * sin(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()

                RenderUtils
                    .pos(x1, y1, z1)
                    .pos(x2, y2, z2)
                    .pos(x3, y3, z3)
                    .pos(x4, y4, z4)
            }
        }

        RenderUtils
            .draw()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()
        val angleStep = 2f * Math.PI / segments
        val topY = yPosition + height

        val bottomX = FloatArray(segments + 1)
        val bottomZ = FloatArray(segments + 1)
        val topX = FloatArray(segments + 1)
        val topZ = FloatArray(segments + 1)

        for (i in 0..segments) {
            val angle = angleStep * i
            val cosA = cos(angle).toFloat()
            val sinA = sin(angle).toFloat()

            bottomX[i] = xPosition + bottomRadius * cosA
            bottomZ[i] = zPosition + bottomRadius * sinA
            topX[i] = xPosition + topRadius * cosA
            topZ[i] = zPosition + topRadius * sinA
        }

        RenderUtils.pushMatrix()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .translate(
                xPosition - cameraPos.x,
                yPosition - cameraPos.y,
                zPosition - cameraPos.z
            )
            .begin(drawMode, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)

        for (i in 0 until segments) {
            val next = (i + 1) % segments

            if (wireframe) {
                RenderUtils
                    .pos(bottomX[i], yPosition, bottomZ[i])
                    .pos(topX[i], topY, topZ[i])
                    .pos(topX[next], topY, topZ[next])
                    .pos(bottomX[next], yPosition, bottomZ[next])
            } else {
                RenderUtils
                    .pos(bottomX[i], yPosition, bottomZ[i])
                    .pos(bottomX[next], yPosition, bottomZ[next])
                    .pos(topX[next], topY, topZ[next])
                    .pos(topX[i], topY, topZ[i])
            }
        }

        val caps = listOf(
            Triple(yPosition, bottomX, bottomZ),
            Triple(topY, topX, topZ)
        )
        for (index in caps.indices) {
            val (y, xRing, zRing) = caps[index]

            for (i in 0 until segments) {
                val next = (i + 1) % segments

                if (wireframe) {
                    RenderUtils
                        .pos(xPosition, y, zPosition)
                        .pos(xRing[i], y, zRing[i])
                        .pos(xRing[next], y, zRing[next])
                } else {
                RenderUtils
                    .pos(xPosition, y, zPosition)
                    .pos(xRing[next], y, zRing[next])
                    .pos(xRing[i], y, zRing[i])
                    .pos(xPosition, y, zPosition)
                }
            }
            RenderUtils.pos(xPosition, y, zPosition)
        }

        RenderUtils
            .draw()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, false)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the radius of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, true)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the pyramid through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_TRIANGLES
        val cameraPos = RenderUtils.getCameraPos()

        val halfX = xScale / 2f
        val halfZ = zScale / 2f

        val x0 = -halfX
        val x1 = halfX
        val z0 = -halfZ
        val z1 = halfZ

        val yBase = 0f
        val yTip = yScale

        val tipX = 0f
        val tipY = yTip
        val tipZ = 0f

        fun triangle(ax: Float, ay: Float, az: Float, bx: Float, by: Float, bz: Float, cx: Float, cy: Float, cz: Float) {
            RenderUtils
                .pos(ax, ay, az)
                .pos(bx, by, bz)
                .pos(cx, cy, cz)
        }

        RenderUtils.pushMatrix()
        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .translate(
                xPosition - cameraPos.x,
                yPosition - cameraPos.y,
                zPosition - cameraPos.z
            )
            .begin(drawMode, DefaultVertexFormats.POSITION)
            .colorizeRGBA(color)

        triangle(tipX, tipY, tipZ, x0, yBase, z0, x1, yBase, z0)
        triangle(tipX, tipY, tipZ, x1, yBase, z0, x1, yBase, z1)
        triangle(tipX, tipY, tipZ, x1, yBase, z1, x0, yBase, z1)
        triangle(tipX, tipY, tipZ, x0, yBase, z1, x0, yBase, z0)

        triangle(x0, yBase, z0, x1, yBase, z0, x1, yBase, z1)
        triangle(x0, yBase, z0, x1, yBase, z1, x0, yBase, z1)

        RenderUtils
            .draw()
            .enableCull()
            .disableBlend()
            .depthMask(true)
            .enableTexture2D()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    @JvmStatic
    @JvmOverloads
    fun drawTracerRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawTracer(xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTracer(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val drawMode = GL11.GL_LINE_STRIP

        val mc = UMinecraft.getMinecraft()
        mc.thePlayer?.let { player ->
            val newXPosition = xPosition - player.posX
            val newYPosition = yPosition - player.posY
            val newZPosition = zPosition - player.posZ

            RenderUtils.pushMatrix()
            if (disableDepth) RenderUtils.disableDepth()
            RenderUtils
                .lineWidth(lineThickness)
                .disableCull()
                .enableBlend()
                .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                .depthMask(false)
                .disableTexture2D()
                .enableLineSmooth()

                .begin(drawMode, DefaultVertexFormats.POSITION)
                .colorizeRGBA(color)
                .pos(newXPosition, newYPosition, newZPosition)
                .pos(0f, player.getEyeHeight(), 0f)
                .draw()

                .enableCull()
                .disableBlend()
                .depthMask(true)
                .enableTexture2D()
                .disableLineSmooth()
                .resetLineWidth()
                .enableDepth()
                .popMatrix()
        }
    }
}
