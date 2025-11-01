package org.zephy.zrenderlib

//#if MC==10809 || MC>=12100
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

    abstract fun drawLine(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f
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

    abstract fun drawBox(
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

    abstract fun drawSphere(
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

    abstract fun drawCylinder(
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

    abstract fun drawPyramid(
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

    abstract fun drawTracer(
        partialTicks: Float,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    )
}
//#endif
