package ovh.motylek.outpostify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AddAccountScreen(
    viewModel: AddAccountViewModel = koinViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = "+48",
                onValueChange = {},
                enabled = false,
                modifier = Modifier.width(70.dp).padding(top = 8.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )

            )
            OutlinedTextField(
                value = phoneNumber,
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
            isLoading = false,
            onClick = {},
        ) { Text(text = stringResource(R.string.Generic_Next)) }
    }
}