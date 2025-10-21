package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R // Assuming R.drawable.ic_google is a placeholder icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    initialSelection: SelectableItem,
    onItemSelected: (SelectableItem) -> Unit,
    onDismiss: () -> Unit
) {
    val currencyCodes = remember {
        listOf(
            SelectableItem("INR", "Indian Rupee", "IN"),
            SelectableItem("USD", "United States Dollar", "US"),
            SelectableItem("GBP", "British Pound", "GB"),
            SelectableItem("JPY", "Japanese Yen", "JP"),
            SelectableItem("AUD", "Australian Dollar", "AU"),
            SelectableItem("LKR", "Sri Lankan Rupee", "LK"),
            SelectableItem("CAD", "Canadian Dollar", "CA"),
            SelectableItem("CHF", "Swiss Franc", "CH"),
            SelectableItem("AED", "UAE Dirham", "AE"),
            SelectableItem("CNY", "Chinese Yuan", "CN"),
            SelectableItem("SGD", "Singapore Dollar", "SG"),
            SelectableItem("SEK", "Swedish Krona", "SE"),
            SelectableItem("KRW", "South Korean Won", "KR"),
            SelectableItem("EUR", "Euro", "EU"),
            SelectableItem("NZD", "New Zealand Dollar", "NZ"),
            SelectableItem("ZAR", "South African Rand", "ZA"),
            SelectableItem("RUB", "Russian Ruble", "RU"),
            SelectableItem("BRL", "Brazilian Real", "BR"),
            SelectableItem("MXN", "Mexican Peso", "MX"),
            SelectableItem("TRY", "Turkish Lira", "TR"),
            SelectableItem("HKD", "Hong Kong Dollar", "HK"),
            SelectableItem("THB", "Thai Baht", "TH"),
            SelectableItem("MYR", "Malaysian Ringgit", "MY"),
            SelectableItem("IDR", "Indonesian Rupiah", "ID"),
            SelectableItem("VND", "Vietnamese Dong", "VN"),
            SelectableItem("PKR", "Pakistani Rupee", "PK"),
            SelectableItem("BDT", "Bangladeshi Taka", "BD"),
            SelectableItem("SAR", "Saudi Riyal", "SA"),
            SelectableItem("EGP", "Egyptian Pound", "EG"),
            SelectableItem("NGN", "Nigerian Naira", "NG"),
            SelectableItem("ILS", "Israeli Shekel", "IL"),
            SelectableItem("PLN", "Polish Zloty", "PL"),
            SelectableItem("CZK", "Czech Koruna", "CZ"),
            SelectableItem("HUF", "Hungarian Forint", "HU"),
            SelectableItem("NOK", "Norwegian Krone", "NO"),
            SelectableItem("DKK", "Danish Krone", "DK"),
            SelectableItem("RON", "Romanian Leu", "RO"),
            SelectableItem("CLP", "Chilean Peso", "CL"),
            SelectableItem("ARS", "Argentine Peso", "AR"),
            SelectableItem("COP", "Colombian Peso", "CO"),
            SelectableItem("PEN", "Peruvian Sol", "PE"),
            SelectableItem("UAH", "Ukrainian Hryvnia", "UA"),
            SelectableItem("KZT", "Kazakhstani Tenge", "KZ"),
            SelectableItem("QAR", "Qatari Riyal", "QA"),
            SelectableItem("KWD", "Kuwaiti Dinar", "KW"),
            SelectableItem("OMR", "Omani Rial", "OM"),
            SelectableItem("BHD", "Bahraini Dinar", "BH"),
            SelectableItem("KES", "Kenyan Shilling", "KE"),
            SelectableItem("MAD", "Moroccan Dirham", "MA"),
            SelectableItem("TWD", "New Taiwan Dollar", "TW"),
            SelectableItem("PHP", "Philippine Peso", "PH")

        )
    }

    SelectableListBottomSheet(
        heading = "Select currency",
        items = currencyCodes,
        initialSelectedItem = initialSelection,
        onItemSelected = onItemSelected,
        onDismiss = onDismiss,
        selectButtonText = "Select",
        sheetHeight = 512.dp
    )
}