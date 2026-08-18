package com._864zeros.clearstreak.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com._864zeros.clearstreak.ui.theme.OIACharcoal
import com._864zeros.clearstreak.ui.theme.OIAMuted
import com._864zeros.clearstreak.ui.theme.OIASage
import com._864zeros.clearstreak.ui.theme.OIATaupe
import com._864zeros.clearstreak.ui.theme.OIAWarmWhite

@Composable
fun OIATextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(it) } },
        placeholder = { Text(placeholder, color = OIAMuted) },
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        textStyle = TextStyle(fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OIASage,
            unfocusedBorderColor = OIATaupe.copy(alpha = 0.6f),
            focusedContainerColor = OIAWarmWhite,
            unfocusedContainerColor = OIAWarmWhite
        )
    )
}
