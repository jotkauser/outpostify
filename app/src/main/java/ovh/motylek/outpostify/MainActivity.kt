package ovh.motylek.outpostify

import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ovh.motylek.outpostify.ui.Navigation
import ovh.motylek.outpostify.ui.theme.OutPostifyTheme
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            exception.printStackTrace()
            val intent = Intent(this, CrashActivity::class.java)
            intent.putExtra("exception", exception.stackTraceToString())
            startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
        setContent {
            OutPostifyTheme {

                Navigation()
            }
        }
    }
}