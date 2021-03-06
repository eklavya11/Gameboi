package dev.rayzr.gameboi.render

import dev.rayzr.gameboi.game.Match
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class RenderContext(val match: Match, private val width: Int, height: Int) {
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics2D
        get() = image.createGraphics()

    var lastMessage: Message? = null

    fun clear() {
        graphics.clearRect(0, 0, image.width, image.height)
    }

    /*fun renderText(text: String, x: Int, y: Int, size: Int = 35) {
        graphics.run {
            font = RenderUtils.font.deriveFont(size.toFloat())
            drawString(text, x, y)
        }
    }*/

    fun renderCenteredText(text: String, x: Int = width / 2, y: Int = 65, size: Int = 40, outlineWidth: Int = 2) {
        graphics.run {
            font = RenderUtils.font.deriveFont(size.toFloat())

            val bounds = font.getStringBounds(text, fontRenderContext)

            val realX = x - (bounds.width / 2).toInt()
            val realY = y - (bounds.height / 2).toInt()

            // Outline effect
            if (outlineWidth > 0) {
                color = Color.black
                drawString(text, realX - outlineWidth, realY - outlineWidth)
                drawString(text, realX - outlineWidth, realY + outlineWidth)
                drawString(text, realX + outlineWidth, realY + outlineWidth)
                drawString(text, realX + outlineWidth, realY - outlineWidth)
            }

            // Solid middle
            color = Color.white
            drawString(text, realX, realY)
        }
    }

    fun draw(embedDescription: String? = null, callback: (Message) -> Unit = {}) {
        val builder = EmbedBuilder().setImage("attachment://render.png")
                .setFooter("${match.game.name} || Players: ${match.players.joinToString(", ") { it.user.name }}")
                .setColor(0x353940)

        if (embedDescription != null) {
            builder.setDescription(embedDescription)
        }

        val embed = builder.build()

        match.channel.sendFile(toBytes(), "render.png").embed(embed).queue {
            lastMessage?.delete()?.queue()

            lastMessage = it
            callback.invoke(it)
        }

        match.bumpTimeout()
    }

    private fun toBytes(): ByteArray {
        val outputStream = ByteArrayOutputStream()

        ImageIO.write(image, "png", outputStream)

        return outputStream.toByteArray()
    }
}