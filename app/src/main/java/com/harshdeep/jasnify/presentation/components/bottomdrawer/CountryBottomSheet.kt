package com.harshdeep.jasnify.presentation.components.bottomdrawer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.harshdeep.jasnify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryBottomSheet(
    initialSelection: SelectableItem,
    onItemSelected: (SelectableItem) -> Unit,
    onDismiss: () -> Unit,
    onProgress: ((Float) -> Unit)? = null
) {
    val countryCodes = remember {
        listOf(
            SelectableItem("+91", "India", "IN"),
            SelectableItem("+1", "United States", "US"),
            SelectableItem("+44", "United Kingdom", "GB"),
            SelectableItem("+81", "Japan", "JP"),
            SelectableItem("+86", "China", "CN"),
            SelectableItem("+49", "Germany", "DE"),
            SelectableItem("+33", "France", "FR"),
            SelectableItem("+7", "Russia", "RU"),
            SelectableItem("+55", "Brazil", "BR"),
            SelectableItem("+61", "Australia", "AU"),
            SelectableItem("+60", "Malaysia", "MY"),
            SelectableItem("+39", "Italy", "IT"),
            SelectableItem("+34", "Spain", "ES"),
            SelectableItem("+62", "Indonesia", "ID"),
            SelectableItem("+20", "Egypt", "EG"),
            SelectableItem("+90", "Turkey", "TR"),
            SelectableItem("+52", "Mexico", "MX"),
            SelectableItem("+1", "Canada", "CA"),
            SelectableItem("+27", "South Africa", "ZA"),
            SelectableItem("+82", "South Korea", "KR"),
            SelectableItem("+65", "Singapore", "SG"),
            SelectableItem("+64", "New Zealand", "NZ"),
            SelectableItem("+47", "Norway", "NO"),
            SelectableItem("+46", "Sweden", "SE"),
            SelectableItem("+31", "Netherlands", "NL"),
            SelectableItem("+358", "Finland", "FI"),
            SelectableItem("+41", "Switzerland", "CH"),
            SelectableItem("+351", "Portugal", "PT"),
            SelectableItem("+353", "Ireland", "IE"),
            SelectableItem("+48", "Poland", "PL"),
            SelectableItem("+380", "Ukraine", "UA"),
            SelectableItem("+36", "Hungary", "HU"),
            SelectableItem("+43", "Austria", "AT"),
            SelectableItem("+420", "Czech Republic", "CZ"),
            SelectableItem("+66", "Thailand", "TH"),
            SelectableItem("+852", "Hong Kong", "HK"),
            SelectableItem("+880", "Bangladesh", "BD"),
            SelectableItem("+372", "Estonia", "EE"),
            SelectableItem("+370", "Lithuania", "LT"),
            SelectableItem("+375", "Belarus", "BY"),
            SelectableItem("+359", "Bulgaria", "BG"),
            SelectableItem("+354", "Iceland", "IS"),
            SelectableItem("+92", "Pakistan", "PK"),
            SelectableItem("+95", "Myanmar", "MM"),
            SelectableItem("+94", "Sri Lanka", "LK"),
            SelectableItem("+503", "El Salvador", "SV"),
            SelectableItem("+226", "Burkina Faso", "BF"),
            SelectableItem("+211", "South Sudan", "SS"),
            SelectableItem("+263", "Zimbabwe", "ZW")

        )
    }

    SelectableListBottomSheet(
        heading = "Select country code",
        items = countryCodes,
        initialSelectedItem = initialSelection,
        onItemSelected = onItemSelected,
        onDismiss = onDismiss,
        onProgress = onProgress,
        selectButtonText = "Select",
        sheetHeight = 512.dp
    )
}
