package org.zephy.zrenderlib

import org.lwjgl.opengl.GL11
import kotlin.math.sin
import kotlin.math.cos

object WorldRenderer : BaseWorldRenderer() {
    override fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long,
        scale: Float,
        renderBackground: Boolean,
        centered: Boolean,
        textShadow: Boolean,
        disableDepth: Boolean,
        maxWidth: Int,
    ) {
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
        RenderUtils.worldEndDraw()
    }

    override fun drawLine(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: Long,
        disableDepth: Boolean,
        lineThickness: Float,
    ) {
        val cameraPos = RenderUtils.getCameraPos()
        RenderUtils
            .pushMatrix()
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)

        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)
            .begin(GL11.GL_LINE_STRIP, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .pos(startX, startY, startZ)
            .pos(endX, endY, endZ)

            .draw()
            .worldEndDraw()
    }

    override fun drawBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float,
        height: Float,
        depth: Float,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()

        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = xPosition - hw
        val x1 = xPosition + hw
        val y0 = yPosition - hh
        val y1 = yPosition + hh
        val z0 = zPosition - hd
        val z1 = zPosition + hd

        RenderUtils
            .pushMatrix()
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)

        if (disableDepth) RenderUtils.disableDepth()
        RenderUtils
            .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)
            .begin(drawMode, VertexFormat.POSITION)
            .colorizeRGBA(color)

        if (wireframe) {
            RenderUtils.pos(x0, y0, z0).pos(x1, y0, z0).pos(x1, y1, z0).pos(x0, y1, z0)
            RenderUtils.pos(x1, y1, z0).pos(x1, y1, z1).pos(x0, y1, z1)
            RenderUtils.pos(x1, y1, z1).pos(x1, y0, z1).pos(x0, y0, z1).pos(x0, y1, z1).pos(x0, y1, z0)
            RenderUtils.pos(x0, y0, z0).pos(x0, y0, z1).pos(x1, y0, z1).pos(x1, y0, z0)
        } else {
            RenderUtils.pos(x0, y0, z0).pos(x1, y0, z0).pos(x1, y1, z0).pos(x0, y1, z0)
            RenderUtils.pos(x0, y1, z1).pos(x0, y0, z1).pos(x1, y0, z1).pos(x1, y1, z1)
            RenderUtils.pos(x0, y1, z1).pos(x0, y0, z1).pos(x0, y0, z0).pos(x0, y1, z0)
            RenderUtils.pos(x1, y1, z0).pos(x1, y0, z0).pos(x1, y0, z1).pos(x1, y1, z1)
            RenderUtils.pos(x0, y1, z1).pos(x0, y1, z0).pos(x1, y1, z0).pos(x1, y1, z1)
            RenderUtils.pos(x1, y0, z1).pos(x0, y0, z1).pos(x0, y0, z0).pos(x1, y0, z0)
        }
        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun drawSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float,
        yScale: Float,
        zScale: Float,
        color: Long,
        segments: Int,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()
        val cache = RenderUtils.getTrigCache(segments)

        RenderUtils
            .pushMatrix()
            .lineWidth(lineThickness)
            .disableCull()
            .enableBlend()
            .blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            .depthMask(false)
            .disableTexture2D()
            .begin(drawMode, VertexFormat.POSITION)
            .colorizeRGBA(color)
            .translate(xPosition - cameraPos.x, yPosition - cameraPos.y, zPosition - cameraPos.z)

        if (disableDepth) RenderUtils.disableDepth()
        if (!wireframe) {
            for (phi in 0 until segments) {
                val sinPhi1 = cache.sinPhi[phi]
                val cosPhi1 = cache.cosPhi[phi]
                val sinPhi2 = cache.sinPhi[phi + 1]
                val cosPhi2 = cache.cosPhi[phi + 1]

                for (theta in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[theta]
                    val sinTheta1 = cache.sinTheta[theta]
                    val cosTheta2 = cache.cosTheta[theta + 1]
                    val sinTheta2 = cache.sinTheta[theta + 1]

                    val x1 = xScale * sinPhi1 * cosTheta1
                    val y1 = yScale * cosPhi1
                    val z1 = zScale * sinPhi1 * sinTheta1

                    val x2 = xScale * sinPhi2 * cosTheta1
                    val y2 = yScale * cosPhi2
                    val z2 = zScale * sinPhi2 * sinTheta1

                    val x3 = xScale * sinPhi2 * cosTheta2
                    val y3 = yScale * cosPhi2
                    val z3 = zScale * sinPhi2 * sinTheta2

                    val x4 = xScale * sinPhi1 * cosTheta2
                    val y4 = yScale * cosPhi1
                    val z4 = zScale * sinPhi1 * sinTheta2

                    RenderUtils
                        .pos(x1, y1, z1)
                        .pos(x2, y2, z2)
                        .pos(x3, y3, z3)
                        .pos(x4, y4, z4)
                }
            }
        } else {
            for (phi in 1 until segments) {
                val sinPhi = cache.sinPhi[phi]
                val cosPhi = cache.cosPhi[phi]
                val y = yScale * cosPhi

                for (theta in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[theta]
                    val sinTheta1 = cache.sinTheta[theta]
                    val cosTheta2 = cache.cosTheta[theta + 1]
                    val sinTheta2 = cache.sinTheta[theta + 1]

                    val x1 = xScale * sinPhi * cosTheta1
                    val z1 = zScale * sinPhi * sinTheta1

                    val x2 = xScale * sinPhi * cosTheta2
                    val z2 = zScale * sinPhi * sinTheta2

                    RenderUtils
                        .pos(x1, y, z1)
                        .pos(x2, y, z2)
                }
            }

            for (theta in 0 until (segments * 2)) {
                val cosTheta = cache.cosTheta[theta]
                val sinTheta = cache.sinTheta[theta]

                for (phi in 0 until segments) {
                    val sinPhi1 = cache.sinPhi[phi]
                    val cosPhi1 = cache.cosPhi[phi]
                    val sinPhi2 = cache.sinPhi[phi + 1]
                    val cosPhi2 = cache.cosPhi[phi + 1]

                    val x1 = xScale * sinPhi1 * cosTheta
                    val y1 = yScale * cosPhi1
                    val z1 = zScale * sinPhi1 * sinTheta

                    val x2 = xScale * sinPhi2 * cosTheta
                    val y2 = yScale * cosPhi2
                    val z2 = zScale * sinPhi2 * sinTheta

                    RenderUtils
                        .pos(x1, y1, z1)
                        .pos(x2, y2, z2)
                }
            }
        }

        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun drawCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float,
        bottomRadius: Float,
        height: Float,
        color: Long,
        segments: Int,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
    ) {
        val drawMode = if (wireframe) GL11.GL_LINE_STRIP else GL11.GL_QUADS
        val cameraPos = RenderUtils.getCameraPos()

        val bottomX = FloatArray(segments + 1)
        val bottomY = yPosition
        val bottomZ = FloatArray(segments + 1)
        val topX = FloatArray(segments + 1)
        val topY = bottomY + height
        val topZ = FloatArray(segments + 1)

        val cache = RenderUtils.getTrigCache(segments)
        for (i in 0..segments) {
            val thetaIndex = (i * 2) % (segments * 2)
            val cosA = cache.cosTheta[thetaIndex]
            val sinA = cache.sinTheta[thetaIndex]

            bottomX[i] = xPosition + bottomRadius * cosA
            bottomZ[i] = zPosition + bottomRadius * sinA
            topX[i] = xPosition + topRadius * cosA
            topZ[i] = zPosition + topRadius * sinA
        }

        RenderUtils
            .pushMatrix()
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

        if (disableDepth) RenderUtils.disableDepth()
        for (i in 0 until segments) {
            val next = (i + 1) % segments

            if (wireframe) {
                RenderUtils
                    .pos(bottomX[i], bottomY, bottomZ[i])
                    .pos(topX[i], topY, topZ[i])
                    .pos(topX[next], topY, topZ[next])
                    .pos(bottomX[next], bottomY, bottomZ[next])
            } else {
                RenderUtils
                    .pos(bottomX[i], bottomY, bottomZ[i])
                    .pos(bottomX[next], bottomY, bottomZ[next])
                    .pos(topX[next], topY, topZ[next])
                    .pos(topX[i], topY, topZ[i])
            }
        }

        val caps = listOf(
            Triple(bottomY, bottomX, bottomZ),
            Triple(topY, topX, topZ)
        )
        val normalsY = listOf(-1f, 1f)
        for (index in caps.indices) {
            val (y, xRing, zRing) = caps[index]
            val normalY = normalsY[index]

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                RenderUtils
                    .pos(xPosition, y, zPosition).normal(0f, normalY, 0f)
                    .pos(xRing[next], y, zRing[next]).normal(0f, normalY, 0f)
                    .pos(xRing[i], y, zRing[i]).normal(0f, normalY, 0f)
                    .pos(xPosition, y, zPosition).normal(0f, normalY, 0f)
            }

            for (i in 0 until segments) {
                val next = (i + 1) % segments
                RenderUtils
                    .pos(xRing[i], y, zRing[i]).normal(0f, normalY, 0f)
                    .pos(xRing[next], y, zRing[next]).normal(0f, normalY, 0f)
            }
        }

        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun drawPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float,
        yScale: Float,
        zScale: Float,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
        lineThickness: Float,
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

        RenderUtils
            .pushMatrix()
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

        if (disableDepth) RenderUtils.disableDepth()
        triangle(tipX, tipY, tipZ, x0, yBase, z0, x1, yBase, z0)
        triangle(tipX, tipY, tipZ, x1, yBase, z0, x1, yBase, z1)
        triangle(tipX, tipY, tipZ, x1, yBase, z1, x0, yBase, z1)
        triangle(tipX, tipY, tipZ, x0, yBase, z1, x0, yBase, z0)

        triangle(x0, yBase, z0, x1, yBase, z0, x1, yBase, z1)
        triangle(x0, yBase, z0, x1, yBase, z1, x0, yBase, z1)

        RenderUtils
            .draw()
            .worldEndDraw()
    }

    override fun drawTracer(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long,
        disableDepth: Boolean,
        lineThickness: Float,
    ) {
        Client.getMinecraft().thePlayer?.let { player ->
            val x1: Double = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
            val y1: Double =
                player.getEyeHeight() + player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
            val z1: Double = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

            val yaw = Math.toRadians(player.rotationYaw.toDouble())
            val pitch = Math.toRadians(player.rotationPitch.toDouble())

            val distance = 0.75
            val vec2X = x1 - sin(yaw) * cos(pitch) * distance
            val vec2Y = y1 - sin(pitch) * distance
            val vec2Z = z1 + cos(yaw) * cos(pitch) * distance

            drawLine(
                vec2X.toFloat(),
                vec2Y.toFloat(),
                vec2Z.toFloat(),
                xPosition,
                yPosition,
                zPosition,
                color,
                disableDepth,
                lineThickness
            )
        }
    }
}
