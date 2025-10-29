package org.zephy.zrenderlib

import org.lwjgl.opengl.GL11;
import kotlin.math.sin
import kotlin.math.cos

object WorldRenderer {
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(text: String, xPosition: Float, yPosition: Float, zPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, scale: Float = 1f, renderBackground: Boolean = false, centered: Boolean = false, textShadow: Boolean = true, disableDepth: Boolean = false, maxWidth: Int = 512) {
        drawString(text, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

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
//        !! fix
        val fontRenderer = RenderUtils.getTextRenderer()
        val renderManager = RenderUtils.renderManager

        val x = xPosition - renderManager.viewerPosX
        val y = yPosition - renderManager.viewerPosY
        val z = zPosition - renderManager.viewerPosZ
        val xMultiplier = if (Client.getMinecraft().gameSettings.thirdPersonView == 2) -1 else 1
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
                .begin(7, VertexFormat.POSITION_COLOR)
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

    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(startX: Float, startY: Float, startZ: Float, endX: Float, endY: Float, endZ: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawLine(startX, startY, startZ, endX, endY, endZ, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

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
//        !! fix
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

            .begin(GL11.GL_LINE_STRIP, VertexFormat.POSITION)
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

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCubeRGBA(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCube(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeBoxRGBA(xPosition: Float, yPosition: Float, zPosition: Float, width: Float = 1f, height: Float = 1f, depth: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeBox(xPosition: Float, yPosition: Float, zPosition: Float, width: Float = 1f, height: Float = 1f, depth: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCubeRGBA(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCube(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidBoxRGBA(xPosition: Float, yPosition: Float, zPosition: Float, width: Float = 1f, height: Float = 1f, depth: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe = false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidBox(xPosition: Float, yPosition: Float, zPosition: Float, width: Float = 1f, height: Float = 1f, depth: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawBoxRGBA(xPosition: Float, yPosition: Float, zPosition: Float, width: Float = 1f, height: Float = 1f, depth: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe, lineThickness)
    }

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
//        !! fix
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

            .begin(drawMode, VertexFormat.POSITION)
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

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphereRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 32, disableDepth: Boolean = false) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphere(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 32, disableDepth: Boolean = false) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidSphereRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 32, disableDepth: Boolean = false) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidSphere(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 32, disableDepth: Boolean = false) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphereRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 32, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphere(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 32, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphereRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 32, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphere(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 32, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSphereRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 32, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

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
//        !! fix
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
            .begin(drawMode, VertexFormat.POSITION)
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

    @JvmStatic
    @JvmOverloads
    fun drawSolidConeRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidCone(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeConeRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeCone(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawConeRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawCone(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinder(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, topRadius: Float = 1f, bottomRadius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinder(xPosition: Float, yPosition: Float, zPosition: Float, topRadius: Float = 1f, bottomRadius: Float = 1f, height: Float = 2f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinder(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, topRadius: Float = 1f, bottomRadius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinder(xPosition: Float, yPosition: Float, zPosition: Float, topRadius: Float = 1f, bottomRadius: Float = 1f, height: Float = 2f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, segments: Int = 64, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, true, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinder(xPosition: Float, yPosition: Float, zPosition: Float, radius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawCylinderRGBA(xPosition: Float, yPosition: Float, zPosition: Float, topRadius: Float = 1f, bottomRadius: Float = 1f, height: Float = 2f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, segments: Int = 64, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), segments, disableDepth, wireframe, lineThickness)
    }

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
//        !! fix
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
            .begin(drawMode, VertexFormat.POSITION)
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

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramidRGBA(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramid(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramidRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramid(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, false)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramidRGBA(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true)
    }

    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramid(xPosition: Float, yPosition: Float, zPosition: Float, size: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, true)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramidRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true)
    }

    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramid(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, color: Long = RenderUtils.colorized ?: RenderUtils.WHITE, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, true)
    }

    @JvmStatic
    @JvmOverloads
    fun drawPyramidRGBA(xPosition: Float, yPosition: Float, zPosition: Float, xScale: Float = 1f, yScale: Float = 1f, zScale: Float = 1f, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, wireframe: Boolean = false, lineThickness: Float = 1f) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe)
    }

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
//        !! fix
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
            .begin(drawMode, VertexFormat.POSITION)
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
    fun drawTracerRGBA(partialTicks: Float, xPosition: Float, yPosition: Float, zPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, disableDepth: Boolean = false, lineThickness: Float = 1f) {
        drawTracer(partialTicks, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

    @JvmStatic
    @JvmOverloads
    fun drawTracer(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
//        !! fix
        Client.getMinecraft().thePlayer?.let { player ->
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

                .begin(GL11.GL_LINE_STRIP, VertexFormat.POSITION)
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
