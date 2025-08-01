package com.example.posapp.ui.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.posapp.data.repository.ConfigRepository
import com.example.posapp.utils.PermissionUtils
import java.util.UUID

enum class PrinterType {
    BLUETOOTH, USB
}

data class PrinterDevice(
    val name: String,
    val address: String,
    val type: PrinterType
)

fun hasBluetoothConnectPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}



fun getAllPrinters(context: Context): List<PrinterDevice> {
    val printers = mutableListOf<PrinterDevice>()

    // Bluetooth printers
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val bluetoothAdapter = bluetoothManager?.adapter
    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled &&
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || hasBluetoothConnectPermission(context))
    ) {
        try {
            bluetoothAdapter.bondedDevices?.forEach { device ->
                if (device.bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.IMAGING ||
                    device.name?.contains("printer", ignoreCase = true) == true
                ) {
                    printers.add(
                        PrinterDevice(
                            name = device.name ?: "Unknown BT Printer",
                            address = device.address ?: "Unknown",
                            type = PrinterType.BLUETOOTH
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            //
        }
    }

    // USB printers
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val usbDevices = usbManager.deviceList
    usbDevices.values.forEach { device ->
        val isPrinter = device.deviceClass == UsbConstants.USB_CLASS_PRINTER ||
                (0 until device.interfaceCount).any {
                    device.getInterface(it).interfaceClass == UsbConstants.USB_CLASS_PRINTER
                }
        if (isPrinter) {
            printers.add(
                PrinterDevice(
                    name = device.productName ?: "Unknown USB Printer",
                    address = "Vendor:${device.vendorId}, Product:${device.productId}",
                    type = PrinterType.USB
                )
            )
        }
    }

    return printers
}


@Composable
fun PrinterSelectionScreen(languageCode: String, onFinishSetup: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity

    var printers by remember { mutableStateOf<List<PrinterDevice>>(emptyList()) }
    var selectedPrinter by remember { mutableStateOf<PrinterDevice?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val hasPermission = hasBluetoothConnectPermission(context)

    val strings = remember(languageCode) {
        mapOf(
            "title" to mapOf(
                "fr" to "Sélectionnez l’imprimante par défaut",
                "es" to "Seleccione la impresora predeterminada",
                "ar" to "اختر الطابعة الاساسية",
                "en" to "Select Default Printer"
            ),
            "noPrinter" to mapOf(
                "fr" to "Aucune imprimante détectée.",
                "es" to "No se detectó ninguna impresora.",
                "ar" to "لم يتم اكتشاف أي طابعة.",
                "en" to "No printer detected."
            ),
            "noPermission" to mapOf(
                "fr" to "Permission Bluetooth non accordée.",
                "es" to "Permiso de Bluetooth no concedido.",
                "ar" to "لم يتم منح إذن البلوتوث.",
                "en" to "Bluetooth permission not granted."
            ),
            "finish" to mapOf(
                "fr" to "Terminer la configuration",
                "es" to "Finalizar configuración",
                "ar" to "إنهاء الإعداد",
                "en" to "Finish Setup"
            ),
            "selectError" to mapOf(
                "fr" to "Veuillez sélectionner une imprimante",
                "es" to "Por favor seleccione una impresora",
                "ar" to "يرجى اختيار طابعة",
                "en" to "Please select a printer"
            ),
            "printTest" to mapOf(
                "fr" to "Impression de test",
                "es" to "Impresión de prueba",
                "ar" to "طباعة اختبار",
                "en" to "Test Print"
            )
        )
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            printers = getAllPrinters(context)
        } else {
            PermissionUtils.requestBluetoothPermissions(activity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(strings["title"]?.get(languageCode) ?: "Select Default Printer", style = MaterialTheme.typography.headlineSmall)

        if (!hasPermission) {
            Text(strings["noPermission"]?.get(languageCode) ?: "", color = MaterialTheme.colorScheme.error)
        } else if (printers.isEmpty()) {
            Text(strings["noPrinter"]?.get(languageCode) ?: "")
        } else {
            printers.forEach { printer ->
                val isSelected = selectedPrinter?.address == printer.address
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPrinter = printer }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${printer.name} (${printer.type.name})")
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }

        Button(
            onClick = {
                if (selectedPrinter != null && hasPermission) {
                    val config = ConfigRepository.loadSetupConfig(context)
                    if (config != null) {
                        val updated = config.copy(
                            defaultPrinterName = selectedPrinter!!.name,
                            defaultPrinterAddress = selectedPrinter!!.address
                        )
                        ConfigRepository.saveSetupConfig(context, updated)
                        onFinishSetup()
                    } else {
                        errorMessage = "Configuration non trouvée"
                    }
                } else {
                    errorMessage = strings["selectError"]?.get(languageCode)
                }
            },
            enabled = selectedPrinter != null && hasPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings["finish"]?.get(languageCode) ?: "")
        }

        Button(
            onClick = {
                selectedPrinter?.let {
                    when (it.type) {
                        PrinterType.BLUETOOTH -> printTestBluetooth(context, it)
                        PrinterType.USB -> printTestUsb(context, it)
                    }
                }
            },
            enabled = selectedPrinter != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(strings["printTest"]?.get(languageCode) ?: "")
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

fun printTestBluetooth(context: Context, printer: PrinterDevice) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val adapter = bluetoothManager.adapter

    // Vérifier la permission BLUETOOTH_CONNECT
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (!hasPermission) {
        // Toast.makeText(context, "Permission Bluetooth requise", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val device = adapter?.bondedDevices?.firstOrNull { it.address == printer.address }
        if (device == null) {
            // Toast.makeText(context, "Imprimante Bluetooth non trouvée", Toast.LENGTH_SHORT).show()
            return
        }

        val uuid = device.uuids?.firstOrNull()?.uuid ?: UUID.randomUUID()
        val socket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()

        val output = socket.outputStream
        val text = "***** Test Print *****\nPOS App Config OK\n\n\n\n"
        val bytes = text.toByteArray(Charsets.UTF_8)

        output.write(bytes)
        output.flush()

        socket.close()
    } catch (e: SecurityException) {
        e.printStackTrace()
        //
    } catch (e: Exception) {
        e.printStackTrace()
        //
    }
}


fun printTestUsb(context: Context, printer: PrinterDevice) {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val device = usbManager.deviceList.values.firstOrNull {
        "Vendor:${it.vendorId}, Product:${it.productId}" == printer.address
    } ?: return

    if (!usbManager.hasPermission(device)) return

    val connection = usbManager.openDevice(device)
    val usbInterface = (0 until device.interfaceCount)
        .map { device.getInterface(it) }
        .firstOrNull { it.interfaceClass == UsbConstants.USB_CLASS_PRINTER }

    val endpoint = usbInterface?.let { iface ->
        (0 until iface.endpointCount)
            .mapNotNull { index -> iface.getEndpoint(index) }
            .firstOrNull { it.type == UsbConstants.USB_ENDPOINT_XFER_BULK }
    }

    if (connection != null && usbInterface != null && endpoint != null) {
        connection.claimInterface(usbInterface, true)
        val data = "***** Test Print *****\nPOS App Config OK\n\n\n\n".toByteArray()
        connection.bulkTransfer(endpoint, data, data.size, 1000)
        connection.releaseInterface(usbInterface)
        connection.close()
    }
}


