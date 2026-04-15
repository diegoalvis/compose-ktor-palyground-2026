package simple_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.savedstate.savedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import simple_tracker.ui.theme.PlygroundTheme

class ScoreActivity : ComponentActivity() {
    private val viewmodel: ScoreViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPaddings ->
                    val score = viewmodel.score.collectAsStateWithLifecycle().value

                    Column(
                        modifier =
                            Modifier
                                .padding(innerPaddings)
                                .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "Score: $score")
                        Button(
                            onClick = { viewmodel.increment() }
                        ) {
                            Text(text = "Increment")
                        }
                        Button(
                            onClick = { viewmodel.decrement() }
                        ) {
                            Text(text = "Decrement")
                        }
                        Spacer(modifier = Modifier.weight(1f))

                    }
                }
            }
        }
    }
}

class ScoreViewmodel() : ViewModel() {

    private val _score = MutableStateFlow(1)
    val score = _score.asStateFlow()


    fun increment() {
        _score.update {
            if (it < 100) it + 1 else it
        }
    }

    fun decrement() {
        _score.update {
            if (it > 0) it - 1 else it
        }
    }
}
