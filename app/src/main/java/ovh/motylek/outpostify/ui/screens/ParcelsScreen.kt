package ovh.motylek.outpostify.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ovh.motylek.outpostify.ui.routes.Parcels

@Composable
fun ParcelsScreen(
    args: Parcels
) {
    Text("Da dashboard!!!! Userid: ${args.userId}")
}