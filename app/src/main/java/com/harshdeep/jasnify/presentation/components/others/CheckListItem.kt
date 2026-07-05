package com.harshdeep.jasnify.presentation.components.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshdeep.jasnify.R
import com.harshdeep.jasnify.presentation.components.buttons.CustomChecker
import com.harshdeep.jasnify.theme.*
import java.util.UUID

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isChecked: Boolean = false
)

@Composable
fun ChecklistItem(
    item: ChecklistItem,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Drag Handle
        Icon(
            painter = painterResource(id = R.drawable.ic_drag),
            contentDescription = "Drag Handle",
            tint = ContentPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))

        // Custom Checker
        CustomChecker(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(Modifier.width(12.dp))

        // Borderless Text Field wrapper
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = item.text,
                onValueChange = onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = JasnifyTheme.typography.headingMedium.merge(
                    TextStyle(
                        color = if (item.isChecked) ContentSecondary else ContentPrimary,
                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    if (item.text.isEmpty()) {
                        Text(
                            text = "Write here",
                            color = ContentSecondary,
                            style = JasnifyTheme.typography.headingMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            )
        }

        // Delete icon
        if (item.text.isNotEmpty() || isFocused) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cross),
                    contentDescription = "Delete Item",
                    tint = ContentPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}





// --------------------------------------------- Preview ------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CheckListBarPreview() {
    var items by remember {
        mutableStateOf(
            listOf(
                ChecklistItem(id = "1", text = "Design the Jasnify core theme", isChecked = false),
                ChecklistItem(id = "2", text = "Create custom composable widgets", isChecked = true),
                ChecklistItem(id = "3", text = "", isChecked = false)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items.forEach { item ->
            val focusRequester = remember { FocusRequester() }
            ChecklistItem(
                item = item,
                focusRequester = focusRequester,
                onTextChanged = { newText ->
                    items = items.map { if (it.id == item.id) it.copy(text = newText) else it }
                },
                onCheckedChange = { isChecked ->
                    items = items.map { if (it.id == item.id) it.copy(isChecked = isChecked) else it }
                },
                onRemove = {
                    items = items.filter { it.id != item.id }
                }
            )
        }
    }
}