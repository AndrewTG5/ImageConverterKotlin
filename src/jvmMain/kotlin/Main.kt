import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dialog
import java.awt.FileDialog


val handler = ImageHandler()

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource("favicon.ico"),
        title = "Image Converter",
        resizable = false,
        state = WindowState(size = DpSize(500.dp, 800.dp))
    ) {
        App()
    }
}

@Composable
@Preview
fun App() {

    var displayImage: ImageBitmap by remember {
        mutableStateOf(
            ImageBitmap(
                width = 300,
                height = 300,
                config = ImageBitmapConfig.Argb8888,
                colorSpace = ColorSpaces.Srgb,
                hasAlpha = true
            )
        )
    }

    MaterialTheme {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { if (handleBoxClick()) displayImage = handler.getImage() }
                    .border(BorderStroke(4.dp, Color.Black))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Drop image here or click for file picker")
                    Button(
                        enabled = false,
                        onClick = {},
                    ) {
                        Text(text = "Paste from clipboard")
                    }
                }
                Image(
                    painter = BitmapPainter(displayImage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
    }
}

/**
 * Handles the click event for the image box. Opens a file chooser and reads the selected file into ImageHandler.
 */
fun handleBoxClick():Boolean {
    val parent: Dialog? = null

    val fileChooser = FileDialog(parent, "Choose an image", FileDialog.LOAD)
    fileChooser.isVisible = true
    if (fileChooser.file == null) return false

    handler.read(fileChooser.directory + fileChooser.file)
    return true
}
