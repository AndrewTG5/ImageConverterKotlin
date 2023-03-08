import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.BmpWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.nio.TiffWriter
import com.sksamuel.scrimage.webp.WebpWriter
import org.jetbrains.skia.Image
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
        val writer = BmpWriter()
        image!!.bytes(writer)
        return Image.makeFromEncoded(image!!.bytes(writer)).toComposeImageBitmap()
    }

    /**
     * Writes the current image to the specified path and format
     * @param path The path to write the image to.
     * @param format The format to write the image in.
     */
    fun write(path: String, format:String) {
        when (format) {
            "PNG" -> image!!.output(PngWriter.NoCompression, path)
            "JPEG" -> image!!.output(JpegWriter.Default, path)
            "WEBP" -> image!!.output(WebpWriter.MAX_LOSSLESS_COMPRESSION, path)
            "BMP" -> image!!.output(BmpWriter(), path)
            "TIFF" -> image!!.output(TiffWriter(), path)
        }
    }
}