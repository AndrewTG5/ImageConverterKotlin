import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
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
import kotlin.concurrent.thread


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
    MaterialTheme {
        Column {
            var displayImage: ImageBitmap by remember { mutableStateOf( // the currently displayed image, defaults to a blank image
                    ImageBitmap(
                        width = 300,
                        height = 300,
                        config = ImageBitmapConfig.Argb8888,
                        colorSpace = ColorSpaces.Srgb,
                        hasAlpha = true
                    )
                )
            }
            var selectedItem: String by remember { mutableStateOf("Select output format") } // the currently selected output format ("WEBP", "PNG", etc.)
            var outputLocation: String by remember { mutableStateOf("") } // the current output path


            Box( // the box that contains the image, file picker, and clipboard paste button
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable {
                        val parent: Dialog? = null
                        val fileChooser = FileDialog(parent, "Choose an image", FileDialog.LOAD)
                        fileChooser.isVisible = true
                        if (fileChooser.file != null) {
                            handler.read(fileChooser.directory + fileChooser.file)
                            displayImage = handler.getImage()
                        }
                    }
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

            Divider()

            Box( // the box that contains the output format dropdown
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val listItems: Array<String> = arrayOf("JPG", "PNG", "BMP", "WEBP", "TIFF")
                var disabledItem: Int by remember { mutableStateOf(-1) }
                var expanded: Boolean by remember { mutableStateOf(false) }

                Button(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Select output format"
                    )
                    Text(text = selectedItem)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listItems.forEachIndexed { itemIndex, itemValue ->
                        DropdownMenuItem(
                            onClick = { expanded = false; selectedItem = itemValue; disabledItem = itemIndex },
                            enabled = (itemIndex != disabledItem)
                        ) {
                            Text(text = itemValue)
                        }
                    }
                }
            }

            Divider()

            Row( // the row that contains the output path text field and save to button
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Button(
                    onClick = {
                        val fileChooser = FileDialog(ComposeWindow(), "Select output location", FileDialog.SAVE)
                        fileChooser.isVisible = true
                        if (fileChooser.file != null) { outputLocation = fileChooser.directory + fileChooser.file }
                    },
                    enabled = (selectedItem != "Select output format"),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = "Save to")
                }
                OutlinedTextField(

                    value = outputLocation,
                    onValueChange = { outputLocation = it },
                    singleLine = true,


                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            Button( // the convert button
                onClick = { thread { handler.write(outputLocation, selectedItem) } },
                enabled = (selectedItem != "Select output format" && outputLocation != ""),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(50.dp)
            ) {
                Text(text = "Convert")
            }

        }
    }
}
