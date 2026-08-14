package com.nuvexa.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

private data class OnboardingPage(val icon: ImageVector, val titleRes: Int, val bodyRes: Int)

private val pages = listOf(
    OnboardingPage(Icons.Filled.GridView, R.string.onboarding_title_1, R.string.onboarding_body_1),
    OnboardingPage(Icons.Filled.PhoneAndroid, R.string.onboarding_title_2, R.string.onboarding_body_2),
    OnboardingPage(Icons.Filled.Lock, R.string.onboarding_title_3, R.string.onboarding_body_3),
    OnboardingPage(Icons.Filled.Search, R.string.onboarding_title_4, R.string.onboarding_body_4),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    fun finish() = viewModel.completeOnboarding(onFinished)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(spacing.l),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = ::finish) {
                    Text(stringResource(R.string.action_skip))
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.l),
                horizontalArrangement = Arrangement.Center,
            ) {
                pages.indices.forEach { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = spacing.xs)
                            .size(if (selected) 10.dp else 8.dp)
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape,
                            ),
                    )
                }
            }

            PrimaryButton(
                text = stringResource(
                    if (pagerState.currentPage == pages.lastIndex) R.string.action_get_started else R.string.action_continue,
                ),
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        finish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(0.5f)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(spacing.xxl))
        Text(
            text = stringResource(page.titleRes),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        androidx.compose.foundation.layout.Spacer(Modifier.height(spacing.m))
        Text(
            text = stringResource(page.bodyRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
