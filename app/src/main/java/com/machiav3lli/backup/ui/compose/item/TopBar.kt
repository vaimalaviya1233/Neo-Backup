package com.machiav3lli.backup.ui.compose.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machiav3lli.backup.OABX
import com.machiav3lli.backup.R
import com.machiav3lli.backup.preferences.pref_showInfoLogBar
import com.machiav3lli.backup.ui.compose.icons.Phosphor
import com.machiav3lli.backup.ui.compose.icons.phosphor.MagnifyingGlass
import com.machiav3lli.backup.ui.compose.icons.phosphor.X
import com.machiav3lli.backup.ui.compose.ifThen
import com.machiav3lli.backup.ui.compose.vertical
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Float.max

@Preview
@Composable
fun DefaultPreview() {
    var count by remember { mutableStateOf(0) }
    val busy by remember { OABX.busy }

    val maxCount = 10

    OABX.addInfoText("xxxxxxxxxxxxxxxxx")
    OABX.clearInfoText()
    OABX.setProgress(count, maxCount)

    LaunchedEffect(OABX) {
        while (count < maxCount) {
            OABX.beginBusy()
            delay(3000)
            count += 1
            OABX.endBusy()
            delay(2000)
        }
    }

    TopBar(title = "Progress $busy", modifier = Modifier.background(color = Color.LightGray)) {
        Button(
            onClick = {
                OABX.beginBusy()
            }
        ) {
            Text("+")
        }
        Button(
            onClick = {
                OABX.endBusy()
            }
        ) {
            Text("-")
        }
        Button(
            onClick = {
                count++
                OABX.addInfoText("test $count")
            }
        ) {
            Text("$count")
        }
    }
}

@Preview
@Composable
fun TestPreview() {
    Row(
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            modifier = Modifier
                .vertical()
                .rotate(-90f),
            fontWeight = FontWeight.Bold,
            text = "vertical text"
        )
        Text(text = "horizontal")
    }
}

@Composable
fun GlobalIndicators() {
    val busy by remember { OABX.busy }
    val progress by remember { OABX.progress }

    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        AnimatedVisibility(visible = progress.first) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
                color = MaterialTheme.colorScheme.primary,
                progress = max(0.02f, progress.second)
            )
        }
        AnimatedVisibility(visible = busy > 0) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    actions: @Composable (RowScope.() -> Unit)
) {
    var tempShow by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val infoText = OABX.getInfoText(n = 5, fill = "")
    var lastInfoText by remember { mutableStateOf(infoText) }
    val changed = (infoText != lastInfoText)

    SideEffect {
        if (changed) {
            lastInfoText = infoText
            tempShow = true
            scope.launch {
                delay(5000)
                tempShow = false
            }
        }
    }

    Column {
        TopAppBar(
            modifier = modifier.wrapContentHeight(),
            title = {
                if ((OABX.showInfo || tempShow) && pref_showInfoLogBar.value) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                OABX.showInfo = !OABX.showInfo
                                if (!OABX.showInfo)
                                    tempShow = false
                            }
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append(title)
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Start,
                            fontSize = 11.0.sp,
                            fontWeight = FontWeight(800),
                            modifier = Modifier
                                .absolutePadding(right = 4.dp, bottom = 4.dp)
                                .vertical()
                                .rotate(-90f)
                        )
                        Text(
                            text = infoText,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.0.sp,
                            lineHeight = 11.0.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.extraSmall
                                )
                                .padding(horizontal = 4.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .ifThen(pref_showInfoLogBar.value) {
                                clickable {
                                    OABX.showInfo = !OABX.showInfo
                                }
                            }
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
            actions = actions
        )
        GlobalIndicators()
    }
}

@Composable
fun ExpandableSearchAction(
    query: String,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onClose: () -> Unit,
    onQueryChanged: (String) -> Unit
) {
    val (expanded, onExpanded) = rememberSaveable {
        mutableStateOf(expanded)
    }

    HorizontalExpandingVisibility(
        expanded = expanded,
        expandedView = {
            ExpandedSearchView(
                query = query,
                modifier = modifier,
                onClose = onClose,
                onExpanded = onExpanded,
                onQueryChanged = onQueryChanged
            )
        },
        collapsedView = {
            RoundButton(
                icon = Phosphor.MagnifyingGlass,
                description = stringResource(id = R.string.search),
                onClick = { onExpanded(true) }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSearchView(
    query: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onExpanded: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }
    SideEffect { textFieldFocusRequester.requestFocus() }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(query, TextRange(query.length)))
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onQueryChanged(it.text)
            },
            modifier = Modifier
                .weight(1f)
                .focusRequester(textFieldFocusRequester),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    onExpanded(false)
                    textFieldValue = TextFieldValue("")
                    onQueryChanged("")
                    onClose()
                }) {
                    Icon(
                        imageVector = Phosphor.X,
                        contentDescription = stringResource(id = R.string.dialogCancel)
                    )
                }
            },
            label = { Text(text = stringResource(id = R.string.searchHint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
    }
}