package com.nri.mycharacter.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.nri.mycharacter.utils.drawCircularIndicatorTrack
import com.nri.mycharacter.utils.drawDeterminateCircularIndicator

@Composable
fun ButtonWithText(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            fontSize = fontSize
        )
    }
}

@Composable
fun SwitchWithText(
    text: String,
    checkedValue: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Switch(
            checked = checkedValue,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Text(text = text, style = style)
    }
}

@Preview
@Composable
fun CheckboxPreview() {
    val checked = remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.width(100.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        SwitchWithText(text = "Test", checkedValue = checked.value) {
            checked.value = !checked.value
        }
    }
}

@Composable
fun CounterWithText(
    counter: Int,
    text: String,
    onInc: () -> Unit,
    onDec: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    min: Int = 0,
    max: Int = Int.MAX_VALUE
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "-",
            modifier = Modifier.clickable(onClick = onDec, enabled = counter > min),
            style = style,
            fontSize = 6.em
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(text = counter.toString(), style = style, fontSize = 6.em)
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Text(
            text = "+",
            modifier = Modifier.clickable(onClick = onInc, enabled = counter < max),
            style = style,
            fontSize = 6.em
        )
        Spacer(modifier = Modifier.padding(start = 10.dp))
        Text(text = text, style = style)
    }
}

@Preview
@Composable
fun CounterPreview() {
    val counter = remember { mutableIntStateOf(0) }
    Surface(
        modifier = Modifier.width(100.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        CounterWithText(
            counter = counter.intValue,
            text = "Test",
            onInc = { counter.intValue += 1 },
            onDec = { counter.intValue -= 1 })
    }
}

@Composable
fun <T> DropdownList(
    itemsSupplier: () -> List<T>,
    rowNameTransformation: (T) -> String,
    onSelect: (T) -> Unit,
    selectedItem: T? = null,
    onClear: (T) -> Unit,
    placeholder: String = "Select"
) {
    val expanded = remember { mutableStateOf(false) }
    Column {
        Box(
            modifier = Modifier
                .requiredWidth(300.dp)
                .requiredHeight(50.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(10))
                .border(
                    width = 1.dp,
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(10)
                )
                .clickable { expanded.value = !expanded.value }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedItem == null) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                } else {
                    Text(
                        text = rowNameTransformation.invoke(selectedItem),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (selectedItem == null) {
                    Icon(
                        imageVector = Icons.Outlined.List,
                        contentDescription = "Select",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Sharp.Clear,
                        contentDescription = "Select",
                        tint = Color.Red,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                onClear.invoke(selectedItem)
                                expanded.value = false
                            }
                    )
                }
            }
        }
        if (expanded.value) {
            Spacer(modifier = Modifier.padding(3.dp))
            Box(
                modifier = Modifier
                    .requiredWidth(300.dp)
                    .requiredHeightIn(max = 400.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(5))
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(5)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .verticalScroll(ScrollState(0))
                        .requiredHeightIn(max = 400.dp)
                ) {
                    items(itemsSupplier.invoke()) {item ->
                        Text(
                            text = rowNameTransformation.invoke(item),
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .clickable {
                                    onSelect.invoke(item)
                                    expanded.value = false
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DropdownListPreview() {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    Surface(
        modifier = Modifier
            .width(350.dp)
            .height(500.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        DropdownList(
            itemsSupplier = {
                listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6",
                    "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12",
                    "Item 13", "Item 14", "Item 15", "Item 16", "Item 17", "Item 18",
                    "Item 19", "Item 20")
            },
            rowNameTransformation = { it },
            selectedItem = selectedItem.value,
            onSelect = { selectedItem.value = it },
            onClear = { selectedItem.value = null }
        )
    }
}

@Composable
fun CircularProgressIndicatorWithContent(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.circularColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color = ProgressIndicatorDefaults.circularTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
    content: @Composable BoxScope.() -> Unit
) {
    val coercedProgress = progress.coerceIn(0f, 1f)
    val stroke = with(LocalDensity.current) {
        Stroke(
            width = strokeWidth.toPx(),
            cap = strokeCap,
            join = StrokeJoin.Round
        )
    }
    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier
                .progressSemantics(coercedProgress)
        ) {
            // Start at 12 o'clock
            val startAngle = 270f
            val sweep = coercedProgress * 360f
            drawCircularIndicatorTrack(trackColor, stroke)
            drawDeterminateCircularIndicator(startAngle, sweep, color, stroke)
        }
        content.invoke(this)
    }
}

@Preview
@Composable
fun CircularProgressIndicatorWithContentPreview() {
    Surface(
        modifier = Modifier.width(100.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        CircularProgressIndicatorWithContent(
            progress = 0.33f,
            trackColor = Color.LightGray,
            strokeCap = StrokeCap.Round,
            strokeWidth = 6.dp,
            modifier = Modifier.size(80.dp)
        ) {
            Text(
                text = "33%",
                style = typography.titleMedium
            )
        }
    }
}
