package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
//#if MC<12100
//$$import javax.vecmath.Vector3f
//$$import javax.vecmath.Vector3d
//$$import kotlin.math.cos
//$$import kotlin.math.sin
//#else
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f
import org.zephy.zrenderlib.RenderUtils.setAndNormalize
import org.zephy.zrenderlib.RenderUtils.tempNormal
//#endif

abstract class BaseWorldRenderer {
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

    abstract fun drawString(
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
        maxWidth: Int = 512
    )

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

    private fun getLinePositions(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        lineThickness: Float = 1f,
    ): List<RenderUtils.WorldPositionVertex> {
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        //#if MC<12100
        //$$val vectorCopy = null
        //#else
        val vectorCopy = Vector3f(tempNormal.setAndNormalize(endX - startX, endY - startY, endZ - startZ))
        //#endif

        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(startX, startY, startZ, vectorCopy, lineThickness))
        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(endX, endY, endZ, vectorCopy, lineThickness))

//        //#if MC<=12110
//        //$$vertexAndNormalList.add(RenderUtils.WorldPositionVertex(startX, startY, startZ, vectorCopy, lineThickness))
//        //$$vertexAndNormalList.add(RenderUtils.WorldPositionVertex(endX, endY, endZ, vectorCopy, lineThickness))
//        //#else
//        val cameraPos = RenderUtils.getCameraPos()
//        val toCamera = Vector3f(
//            cameraPos.x.toFloat() - startX,
//            cameraPos.y.toFloat() - startY,
//            cameraPos.z.toFloat() - startZ
//        )
//
//        val perpendicular = Vector3f()
//        vectorCopy.cross(toCamera, perpendicular)
//        perpendicular.normalize()
//        perpendicular.mul(lineThickness * 0.5f)
//
//        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(startX - perpendicular.x, startY - perpendicular.y, startZ - perpendicular.z, vectorCopy, lineThickness))
//        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(startX + perpendicular.x, startY + perpendicular.y, startZ + perpendicular.z, vectorCopy, lineThickness))
//        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(endX + perpendicular.x, endY + perpendicular.y, endZ + perpendicular.z, vectorCopy, lineThickness))
//        vertexAndNormalList.add(RenderUtils.WorldPositionVertex(endX - perpendicular.x, endY - perpendicular.y, endZ - perpendicular.z, vectorCopy, lineThickness))
//        //#endif

        return vertexAndNormalList
    }

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
        val vertexAndNormalList = getLinePositions(startX, startY, startZ, endX, endY, endZ, lineThickness)
        _drawLine(vertexAndNormalList, color, disableDepth)
    }

    abstract fun _drawLine(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
    )

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
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
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

        //#if MC<12100
        //$$val vectorCopy = null
        //#endif

        for (i in 0 until vertexes.size) {
            val p1 = vertexes[i]
            if (wireframe) {
                val p2 = vertexes[(i + 1) % vertexes.size]
                //#if MC>=12100
                val vectorCopy = Vector3f(tempNormal.setAndNormalize(p1, p2))
                //#endif
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p2.x, p2.y, p2.z, vectorCopy, lineThickness))
            } else {
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, null, lineThickness))
            }
        }

        _drawBox(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawBox(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

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
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val cache = RenderUtils.getTrigCache(segments)

        //#if MC<12100
        //$$val vectorCopy = null
        //#endif

        if (wireframe) {
            for (lat in 1 until segments) {
                val sinPhi = cache.sinPhi[lat]
                val cosPhi = cache.cosPhi[lat]
                val y = yPosition + yScale * cosPhi
                for (lon in 0 until (segments * 2)) {
                    val cosTheta1 = cache.cosTheta[lon]
                    val sinTheta1 = cache.sinTheta[lon]
                    val cosTheta2 = cache.cosTheta[lon + 1]
                    val sinTheta2 = cache.sinTheta[lon + 1]

                    val x1 = xPosition + xScale * sinPhi * cosTheta1
                    val z1 = zPosition + zScale * sinPhi * sinTheta1

                    val x2 = xPosition + xScale * sinPhi * cosTheta2
                    val z2 = zPosition + zScale * sinPhi * sinTheta2

                    //#if MC>=12100
                    val vectorCopy = Vector3f(tempNormal.setAndNormalize(x2 - x1, 0f, z2 - z1))
                    //#endif
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y, z1, vectorCopy, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y, z2, vectorCopy, lineThickness))
                }
            }

            for (lon in 0 until (segments * 2)) {
                val cosTheta = cache.cosTheta[lon]
                val sinTheta = cache.sinTheta[lon]

                for (lat in 0 until segments) {
                    val sinPhi1 = cache.sinPhi[lat]
                    val cosPhi1 = cache.cosPhi[lat]
                    val sinPhi2 = cache.sinPhi[lat + 1]
                    val cosPhi2 = cache.cosPhi[lat + 1]

                    val x1 = xPosition + xScale * sinPhi1 * cosTheta
                    val y1 = yPosition + yScale * cosPhi1
                    val z1 = zPosition + zScale * sinPhi1 * sinTheta

                    val x2 = xPosition + xScale * sinPhi2 * cosTheta
                    val y2 = yPosition + yScale * cosPhi2
                    val z2 = zPosition + zScale * sinPhi2 * sinTheta

                    //#if MC>=12100
                    val vectorCopy = Vector3f(tempNormal.setAndNormalize(x2 - x1, y2 - y1, z2 - z1))
                    //#endif

                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y1, z1, vectorCopy, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y2, z2, vectorCopy, lineThickness))
                }
            }
        } else {
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

                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x1, y1, z1, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x2, y2, z2, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x3, y3, z3, null, lineThickness))
                    vertexAndNormalList.add(RenderUtils.WorldPositionVertex(x4, y4, z4, null, lineThickness))
                }
            }
        }

        _drawSphere(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawSphere(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

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

    @JvmOverloads
    fun drawSimpleCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

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
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()

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

        //#if MC<12100
        //$$val vectorCopy = null
        //#endif

        if (wireframe) {
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                //#if MC>=12100
                var vectorCopy = Vector3f(tempNormal.setAndNormalize(topX[next] - topX[i], 0f, topZ[next] - topZ[i]))
                //#endif
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], vectorCopy, lineThickness))

                //#if MC>=12100
                vectorCopy = Vector3f(tempNormal.setAndNormalize(bottomX[next] - bottomX[i], 0f, bottomZ[next] - bottomZ[i]))
                //#endif
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], vectorCopy, lineThickness))

                //#if MC>=12100
                vectorCopy = Vector3f(tempNormal.setAndNormalize(topX[i] - bottomX[i], topY - bottomY, topZ[i] - bottomZ[i]))
                //#endif
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
            }
        } else {
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], null, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], null, lineThickness))
            }
        }

        if (bottomRadius > 0f) {
            //#if MC>=12100
            val vectorCopy = Vector3f(0f, -1f, 0f)
            //#endif
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, bottomY, zPosition, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[next], bottomY, bottomZ[next], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(bottomX[i], bottomY, bottomZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, bottomY, zPosition, vectorCopy, lineThickness))
            }
        }


        if (topRadius > 0f) {
            //#if MC>=12100
            val vectorCopy = Vector3f(0f, 1f, 0f)
            //#endif
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, topY, zPosition, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[next], topY, topZ[next], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(topX[i], topY, topZ[i], vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(xPosition, topY, zPosition, vectorCopy, lineThickness))
            }
        }

        _drawCylinder(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawCylinder(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

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
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true, lineThickness)
    }

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
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, true, lineThickness)
    }

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
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, true, lineThickness)
    }

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
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, true, lineThickness)
    }

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
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, wireframe, lineThickness)
    }

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
        val vertexAndNormalList = mutableListOf<RenderUtils.WorldPositionVertex>()
        val halfX = xScale / 2f
        val halfZ = zScale / 2f

        val x0 = xPosition - halfX
        val x1 = xPosition + halfX
        val y0 = yPosition
        val y1 = yPosition + yScale
        val z0 = zPosition - halfZ
        val z1 = zPosition + halfZ

        val apex = Vector3f(xPosition, y1, zPosition)
        val base00 = Vector3f(x0, y0, z0)
        val base10 = Vector3f(x1, y0, z0)
        val base11 = Vector3f(x1, y0, z1)
        val base01 = Vector3f(x0, y0, z1)

        val vertexes = if (wireframe) {
            listOf(
                base00, base10, base11, base01, base00,
                base00, apex,
                base10, apex,
                base11, apex,
                base01, apex,
            )
        } else {
            listOf(
                base00, base10, base11,
                base00, base11, base01,

                apex, base00, base10,
                apex, base10, base11,
                apex, base11, base01,
                apex, base01, base00,
            )
        }

        //#if MC<12100
        //$$val vectorCopy = null
        //#endif

        for (i in 0 until vertexes.size - if (wireframe) 1 else 0) {
            val p1 = vertexes[i]
            if (wireframe) {
                val p2 = vertexes[i + 1]
                //#if MC>=12100
                val vectorCopy = Vector3f(tempNormal.setAndNormalize(p1, p2))
                //#endif
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, vectorCopy, lineThickness))
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p2.x, p2.y, p2.z, vectorCopy, lineThickness))
            } else {
                vertexAndNormalList.add(RenderUtils.WorldPositionVertex(p1.x, p1.y, p1.z, null, lineThickness))
            }
        }

        _drawPyramid(vertexAndNormalList, color, disableDepth, wireframe)
    }

    abstract fun _drawPyramid(
        vertexAndNormalList: List<RenderUtils.WorldPositionVertex>,
        color: Long,
        disableDepth: Boolean,
        wireframe: Boolean,
    )

    @JvmOverloads
    fun drawTracerRGBA(
        partialTicks: Float,
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
        drawTracer(partialTicks, xPosition, yPosition, zPosition, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), disableDepth, lineThickness)
    }

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
        val mc = Client.getMinecraft()
        //#if MC<12100
        //$$mc.thePlayer?.let { player ->
            //$$val x1: Double = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
            //$$val y1: Double =  player.getEyeHeight() + player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
            //$$val z1: Double = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks
            //$$val yawDeg = player.rotationYaw.toDouble()
            //$$val pitchDeg = player.rotationPitch.toDouble()
        //#else
        mc.player?.let { player ->
            val x1: Double = player.xo + (player.x - player.xo) * partialTicks
            val y1: Double = player.getEyeHeight(player.pose) + player.yo + (player.y - player.yo) * partialTicks
            val z1: Double = player.zo + (player.z - player.zo) * partialTicks
            val camera = RenderUtils.getCamera()
            val yawDeg = camera.yRot().toDouble()
            val pitchDeg = camera.xRot().toDouble()
            //#endif

            val yawRad = Math.toRadians(yawDeg)
            val pitchRad = Math.toRadians(pitchDeg)

            val distance = 75.0
            //#if MC<12100
            //$$val startPos = Vector3d(
            //$$    x1 - sin(yawRad) * cos(pitchRad) * distance,
            //$$    y1 - sin(pitchRad) * distance,
            //$$    z1 + cos(yawRad) * cos(pitchRad) * distance
            //$$)
            //#else
            val startPos = Vec3(0.0, 0.0, distance)
                .xRot(-pitchRad.toFloat())
                .yRot(-yawRad.toFloat())
                .add(x1, y1, z1)
            //#endif

            _drawTracer(partialTicks, startPos.x.toFloat(), startPos.y.toFloat(), startPos.z.toFloat(), xPosition, yPosition, zPosition, color, disableDepth, lineThickness)
        }
    }

    abstract fun _drawTracer(
        partialTicks: Float,
        startPosX: Float,
        startPosY: Float,
        startPosZ: Float,
        endPosX: Float,
        endPosY: Float,
        endPosZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    )
}
//#endif
