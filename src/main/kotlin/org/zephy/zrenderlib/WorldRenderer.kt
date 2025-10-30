package org.zephy.zrenderlib

//#if MC>12100
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import java.awt.Color
import org.zephy.zrenderlib.RenderUtils.tempNormal
import org.zephy.zrenderlib.RenderUtils.setAndNormalize

object WorldRenderer : BaseWorldRenderer() {
    @JvmStatic
    fun getLineRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.LINES_ESP() else RenderLayers.LINES()

    @JvmStatic
    fun getQuadRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.QUADS_ESP() else RenderLayers.QUADS()

    @JvmStatic
    fun getTriangleStripRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLE_STRIP_ESP() else RenderLayers.TRIANGLE_STRIP()

    @JvmStatic
    fun getTriangleRenderLayer(disableDepth: Boolean) = if (disableDepth) RenderLayers.TRIANGLES_ESP() else RenderLayers.TRIANGLES()

    override fun drawString(text: String, xPosition: Float, yPosition: Float, zPosition: Float, color: Long, scale: Float, renderBackground: Boolean, centered: Boolean, textShadow: Boolean, disableDepth: Boolean, maxWidth: Int) {
        drawString(Text.of(text), xPosition, yPosition, zPosition, color, scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(text: Text, xPosition: Float, yPosition: Float, zPosition: Float, red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, scale: Float = 1f, renderBackground: Boolean = false, centered: Boolean = false, textShadow: Boolean = true, disableDepth: Boolean = false, maxWidth: Int = 512) {
        drawString(text, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), scale, renderBackground, centered, textShadow, disableDepth, maxWidth)
    }

    @JvmStatic
    @JvmOverloads
    fun drawString(
        text: Text,
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
        val (lines, width, height) = RenderUtils.splitText(text, maxWidth)
        val fontRenderer = RenderUtils.getTextRenderer()
        val camera = Client.getMinecraft().gameRenderer.camera
        val cameraPos = camera.pos
        val vertexConsumers = Client.getMinecraft().bufferBuilders.entityVertexConsumers

        val matrix = Matrix4f()
        val adjustedScale = (scale * 0.05).toFloat()
        val xShift = -width / 2
        val yShift = -height / 2
        var yOffset = 0
        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            Color(0, 0, 0, 0).rgb
        }

        RenderUtils.baseStartDraw()
        for (line in lines) {
            matrix
                .translate(
                    (xPosition - cameraPos.getX()).toFloat(),
                    (yPosition - cameraPos.getY() + yOffset * adjustedScale).toFloat(),
                    (zPosition - cameraPos.getZ()).toFloat(),
                )
                .rotate(Client.getMinecraft().gameRenderer.camera.rotation)
                .scale(adjustedScale, -adjustedScale, adjustedScale)

            val centerShift = if (centered) {
                xShift + (fontRenderer.getWidth(line) / 2f)
            } else {
                0f
            }

            fontRenderer.draw(
                line,
                xShift - centerShift,
                yShift + yOffset,
                RenderUtils.ARGBColor.fromLongRGBA(color).getLong().toInt(),
                textShadow,
                matrix,
                vertexConsumers,
                if (disableDepth) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL,
                backgroundColorInt,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
            )

            yOffset += fontRenderer.fontHeight + 1
        }
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
        val renderLayer = getLineRenderLayer(disableDepth)
        tempNormal.setAndNormalize(endX - startX, endY - startY, endZ - startZ)
        RenderUtils
            .baseStartDraw()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorizeRGBA(color)
            .pos(startX, startY, startZ).normal(tempNormal.x, tempNormal.y, tempNormal.z)
            .pos(endX, endY, endZ).normal(tempNormal.x, tempNormal.y, tempNormal.z)

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
        val renderLayer = when {
            !wireframe -> getTriangleStripRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = xPosition - hw
        val x1 = xPosition + hw
        val y0 = yPosition - hh
        val y1 = yPosition + hh
        val z0 = zPosition - hd
        val z1 = zPosition + hd

        val vertexes = when {
            wireframe -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x1, y0, z0),
            )
            else -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
            )
        }

        RenderUtils
            .baseStartDraw()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorizeRGBA(color)

        for (i in 0 until vertexes.size - if (wireframe) 1 else 0) {
            val p1 = vertexes[i]
            RenderUtils.pos(p1.x, p1.y, p1.z)
            if (wireframe) {
                val p2 = vertexes[i + 1]
                tempNormal.setAndNormalize(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z)
                RenderUtils
                    .normal(tempNormal.x, tempNormal.y, tempNormal.z)
                    .pos(p2.x, p2.y, p2.z)
                    .normal(tempNormal.x, tempNormal.y, tempNormal.z)
            }
        }

        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
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
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }
        val cache = RenderUtils.getTrigCache(segments)

        RenderUtils
            .baseStartDraw()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorizeRGBA(color)

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

                    val x1 = xPosition + xScale * sinPhi1 * cosTheta1
                    val y1 = yPosition + yScale * cosPhi1
                    val z1 = zPosition + zScale * sinPhi1 * sinTheta1

                    val x2 = xPosition + xScale * sinPhi2 * cosTheta1
                    val y2 = yPosition + yScale * cosPhi2
                    val z2 = zPosition + zScale * sinPhi2 * sinTheta1

                    val x3 = xPosition + xScale * sinPhi2 * cosTheta2
                    val y3 = yPosition + yScale * cosPhi2
                    val z3 = zPosition + zScale * sinPhi2 * sinTheta2

                    val x4 = xPosition + xScale * sinPhi1 * cosTheta2
                    val y4 = yPosition + yScale * cosPhi1
                    val z4 = zPosition + zScale * sinPhi1 * sinTheta2

                    tempNormal.setAndNormalize(x1 - xPosition, y1 - yPosition, z1 - zPosition)
                    RenderUtils.pos(x1, y1, z1).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                    tempNormal.setAndNormalize(x2 - xPosition, y2 - yPosition, z2 - zPosition)
                    RenderUtils.pos(x2, y2, z2).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                    tempNormal.setAndNormalize(x3 - xPosition, y3 - yPosition, z3 - zPosition)
                    RenderUtils.pos(x3, y3, z3).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                    tempNormal.setAndNormalize(x4 - xPosition, y4 - yPosition, z4 - zPosition)
                    RenderUtils.pos(x4, y4, z4).normal(tempNormal.x, tempNormal.y, tempNormal.z)
                }
            }
        } else {
            for (phi in 1 until segments) {
                val sinPhi = cache.sinPhi[phi]
                val cosPhi = cache.cosPhi[phi]
                val y = yPosition + yScale * cosPhi

                for (theta in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[theta]
                    val sinTheta1 = cache.sinTheta[theta]
                    val cosTheta2 = cache.cosTheta[theta + 1]
                    val sinTheta2 = cache.sinTheta[theta + 1]

                    val x1 = xPosition + xScale * sinPhi * cosTheta1
                    val z1 = zPosition + zScale * sinPhi * sinTheta1

                    val x2 = xPosition + xScale * sinPhi * cosTheta2
                    val z2 = zPosition + zScale * sinPhi * sinTheta2

                    tempNormal.setAndNormalize(x1 - xPosition, y - yPosition, z1 - zPosition)
                    RenderUtils.pos(x1, y, z1).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                    tempNormal.setAndNormalize(x2 - xPosition, y - yPosition, z2 - zPosition)
                    RenderUtils.pos(x2, y, z2).normal(tempNormal.x, tempNormal.y, tempNormal.z)
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

                    val x1 = xPosition + xScale * sinPhi1 * cosTheta
                    val y1 = yPosition + yScale * cosPhi1
                    val z1 = zPosition + zScale * sinPhi1 * sinTheta

                    val x2 = xPosition + xScale * sinPhi2 * cosTheta
                    val y2 = yPosition + yScale * cosPhi2
                    val z2 = zPosition + zScale * sinPhi2 * sinTheta

                    tempNormal.setAndNormalize(x1 - xPosition, y1 - yPosition, z1 - zPosition)
                    RenderUtils.pos(x1, y1, z1).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                    tempNormal.setAndNormalize(x2 - xPosition, y2 - yPosition, z2 - zPosition)
                    RenderUtils.pos(x2, y2, z2).normal(tempNormal.x, tempNormal.y, tempNormal.z)
                }
            }
        }

        RenderUtils
            .draw()
            .worldEndDraw()
    }

    // Normals are a bit wrong here, fine enough
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
        val renderLayer = when {
            !wireframe -> getQuadRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

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
            .baseStartDraw()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorizeRGBA(color)

        for (i in 0 until segments) {
            val next = (i + 1) % segments

            if (wireframe) {
                tempNormal.setAndNormalize(bottomX[i] - xPosition, 0f, bottomZ[i] - zPosition)
                RenderUtils.pos(bottomX[i], bottomY, bottomZ[i]).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                tempNormal.setAndNormalize(topX[i] - xPosition, 0f, topZ[i] - zPosition)
                RenderUtils.pos(topX[i], topY, topZ[i]).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                tempNormal.setAndNormalize(topX[next] - xPosition, 0f, topZ[next] - zPosition)
                RenderUtils.pos(topX[next], topY, topZ[next]).normal(tempNormal.x, tempNormal.y, tempNormal.z)

                tempNormal.setAndNormalize(bottomX[next] - xPosition, 0f, bottomZ[next] - zPosition)
                RenderUtils.pos(bottomX[next], bottomY, bottomZ[next]).normal(tempNormal.x, tempNormal.y, tempNormal.z)
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

    // Normals are a bit wrong here, fine enough
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
        val renderLayer = when {
            !wireframe -> getTriangleRenderLayer(disableDepth)
            else -> getLineRenderLayer(disableDepth)
        }

        val halfX = xScale / 2f
        val halfZ = zScale / 2f

        val x0 = xPosition - halfX
        val x1 = xPosition + halfX
        val z0 = zPosition - halfZ
        val z1 = zPosition + halfZ

        val yBase = yPosition
        val yTip = yPosition + yScale

        val tipX = xPosition
        val tipY = yTip
        val tipZ = zPosition

        fun triangle(ax: Float, ay: Float, az: Float, bx: Float, by: Float, bz: Float, cx: Float, cy: Float, cz: Float) {
            tempNormal.setAndNormalize(
                (bx - ax) * (cy - ay) - (cx - ax) * (by - ay),
                (bz - az) * (cy - ay) - (cz - az) * (by - ay),
                (cx - ax) * (by - ay) - (bx - ax) * (cy - ay),
            )
            RenderUtils
                .pos(ax, ay, az).normal(tempNormal.x, tempNormal.y, tempNormal.z)
                .pos(bx, by, bz).normal(tempNormal.x, tempNormal.y, tempNormal.z)
                .pos(cx, cy, cz).normal(tempNormal.x, tempNormal.y, tempNormal.z)
        }

        RenderUtils
            .baseStartDraw()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorizeRGBA(color)

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
        val mc = Client.getMinecraft()
        mc.player?.let { player ->
            val x1: Double = player.lastX + (player.x - player.lastX) * partialTicks
            val y1: Double = player.getEyeHeight(player.pose) + player.lastY + (player.y - player.lastY) * partialTicks
            val z1: Double = player.lastZ + (player.z - player.lastZ) * partialTicks

            val vec2 = Vec3d(0.0, 0.0, 75.0)
                .rotateX(-Math.toRadians(mc.gameRenderer.camera.pitch.toDouble()).toFloat())
                .rotateY(-Math.toRadians(mc.gameRenderer.camera.yaw.toDouble()).toFloat())
                .add(x1, y1, z1)

            drawLine(vec2.x.toFloat(), vec2.y.toFloat(), vec2.z.toFloat(), xPosition, yPosition, zPosition, color, disableDepth, lineThickness)
        }
    }
}
//#endif
