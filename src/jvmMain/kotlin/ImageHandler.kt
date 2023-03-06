import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.BmpWriter
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
}