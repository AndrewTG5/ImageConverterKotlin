import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import java.io.File
import java.util.*
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
        Column(
            Modifier.fillMaxHeight(),
        ) {
            var displayImage: ImageBitmap by remember {
                mutableStateOf( // the currently displayed image, defaults to a blank image
                    ImageBitmap(
                        width = 300,
                        height = 300,
                        config = ImageBitmapConfig.Argb8888,
                        colorSpace = ColorSpaces.Srgb,
                        hasAlpha = true
                    )
                )
            }
            var originalFilename: String by remember { mutableStateOf("") } // the filename of the currently loaded image
            var selectedFormat: String by remember { mutableStateOf("Select output format") } // the currently selected output format ("WEBP", "PNG", etc.)
            var width: String by remember { mutableStateOf("") } // the width set in the ui
            var height: String by remember { mutableStateOf("") } // the height set in the ui
            var outputLocation: String by remember { mutableStateOf("") } // the current output path
            var showFilePicker: Boolean by remember { mutableStateOf(false) } // whether the file picker is visible or not
            var showDirPicker: Boolean by remember { mutableStateOf(false) } // whether the directory picker is visible or not
            var showDialog: Boolean by remember { mutableStateOf(false) } // whether the dialog is visible or not

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Access denied") },
                    text = { Text("Unable to write to $outputLocation") },
                    confirmButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            FilePicker(
                show = showFilePicker,
                fileExtensions = listOf("jpg", "png", "jpeg", "bmp", "webp", "tiff")
            ) { file ->
                showFilePicker = false
                if (file != null) {
                    handler.read(file.path)
                    originalFilename = File(file.path).nameWithoutExtension
                    width = handler.getWidth()
                    height = handler.getHeight()
                    displayImage = handler.getImage()
                }
            }

            DirectoryPicker(showDirPicker) { path ->
                showDirPicker = false
                if (path != null) {
                    outputLocation = path
                }
            }

            Box( // the box that contains the image, file picker, and clipboard paste button
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { showFilePicker = true }
            ) {
                if (originalFilename == "") {
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

            Row {
                OutlinedTextField(
                    label = { Text(text = "Width") },
                    value = width,
                    placeholder = { Text(text = handler.getWidth()) },
                    onValueChange = { width = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(240.dp)
                        .padding(start = 8.dp)
                )
                OutlinedTextField(
                    label = { Text(text = "Height") },
                    value = height,
                    placeholder = { Text(text = handler.getHeight()) },
                    onValueChange = { height = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .width(240.dp)
                        .padding(start = 8.dp)
                )
            }

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
                    Text(text = selectedFormat)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listItems.forEachIndexed { itemIndex, itemValue ->
                        DropdownMenuItem(
                            onClick = { expanded = false; selectedFormat = itemValue; disabledItem = itemIndex },
                            enabled = (itemIndex != disabledItem)
                        ) {
                            Text(text = itemValue)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // push following elements to the bottom of the screen
            Divider()
            Row( // the row that contains the output path text field and save to button
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Button( // the save to button, opens a file dialog
                    onClick = { showDirPicker = true },
                    enabled = (selectedFormat != "Select output format"),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = "Save to")
                }
                OutlinedTextField( // the output path text field
                    value = outputLocation,
                    onValueChange = { outputLocation = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            var idle: Boolean by remember { mutableStateOf(true) } // disables the convert button when the program is converting an image
            Button( // the convert button
                onClick = {
                    idle = false
                    thread {
                        try {
                            handler.write("$outputLocation/$originalFilename.${selectedFormat.lowercase(Locale.getDefault())}", selectedFormat, width, height)
                        } catch (e: java.nio.file.AccessDeniedException) {
                            showDialog = true
                        }
                        idle = true
                    }
                },
                enabled = (selectedFormat != "Select output format" && outputLocation != "" && idle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp, 8.dp, 8.dp)
                    .height(50.dp)
            ) {
                Text(text = "Convert")
            }
        }
    }
}
