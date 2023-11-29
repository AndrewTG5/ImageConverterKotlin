import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.BmpWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.nio.TiffWriter
import com.sksamuel.scrimage.webp.WebpWriter
import java.io.File

/**
 * Handles the reading and writing of images and their data.
 */
class ImageHandler {

    private var image: ImmutableImage? = null

    /**
     * Reads the image specified by path.
     * @param path The image to read.
     */
    fun read(path: String) {
        val file = File(path)
        image = ImmutableImage.loader().fromFile(file)
    }

    /**
     * Returns the currently loaded image as an ImageBitmap for use in the UI.
     * @return The current image as an ImageBitmap.
     */
    fun getImage(): ImageBitmap {
        if (image == null) return ImageBitmap(1, 1)
        return image!!.awt().toComposeImageBitmap()
    }

    /**
     * Writes the current image to the specified path and format
     * @param path The path to write the image to.
     * @param format The format to write the image in.
     */
    fun write(path: String, format:String, width: String, height: String) {
        val localImage = image!!.scaleTo(width.toInt(), height.toInt())
        when (format) {
            "PNG" -> localImage.output(PngWriter.NoCompression, path)
            "JPG" -> localImage.output(JpegWriter.Default, path)
            "WEBP" -> localImage.output(WebpWriter.MAX_LOSSLESS_COMPRESSION, path)
            "BMP" -> localImage.output(BmpWriter(), path)
            "TIFF" -> localImage.output(TiffWriter(), path)
        }
    }

    /**
     * Returns the current image's width.
     * @return The current image's width.
     */
    fun getWidth(): String {
        if (image == null) return ""
        return image!!.width.toString()
    }

    /**
     * Returns the current image's height.
     * @return The current image's height.
     */
    fun getHeight(): String {
        if (image == null) return ""
        return image!!.height.toString()
    }
}