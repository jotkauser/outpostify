package ovh.motylek.outpostify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.components.LoadingButton
import ovh.motylek.outpostify.ui.viewmodels.AddAccountViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ovh.motylek.outpostify.ui.common.components.ErrorDialog
import ovh.motylek.outpostify.ui.routes.Parcels

@Composable
fun AddAccountScreen(
    navController: NavController,
    viewModel: AddAccountViewModel = koinViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()
    val buttonLoading by viewModel.buttonLoading.collectAsStateWithLifecycle()
    val showSmsField by viewModel.showSmsField.collectAsStateWithLifecycle()
    val smsCode by viewModel.smsCode.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        if (showSmsField) {
            OutlinedTextField(
                value = smsCode,
                onValueChange = { viewModel.setSmsCode(it) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                label = { Text(text = stringResource(R.string.AddAccount_SmsCode)) },
            )
            Spacer(modifier = Modifier.weight(1f))
            LoadingButton(
                isLoading = buttonLoading,
                enabled = smsCode.length == 6,
                onClick = {
                    coroutineScope.launch {
                        val status = viewModel.acceptSmsCode()
                        if (status.first) {
                            navController.navigate(Parcels(status.second)) {
                                launchSingleTop = true
                            }
                        }
                    }
                },
            ) { Text(text = stringResource(R.string.Generic_Next)) }

            BackHandler {
                viewModel.goBack()
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    prefix = { Text(text = "+48 ") },
                    onValueChange = { viewModel.setPhoneNumber(it) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal
                    ),
                    label = { Text(text = stringResource(R.string.AddAccount_PhoneNumber)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 4.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 4.dp
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            LoadingButton(
                isLoading = buttonLoading,
                enabled = phoneNumber.length == 9,
                onClick = {
                    viewModel.acceptPhoneNumber()
                },
            ) { Text(text = stringResource(R.string.Generic_Next)) }
        }
    }
    if (exception != null) {
        ErrorDialog(
            exception!!,
            onDismiss = {
                viewModel.clearException()
            }
        )
    }
}