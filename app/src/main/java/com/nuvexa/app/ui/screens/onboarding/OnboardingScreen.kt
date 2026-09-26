package com.nuvexa.app.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nuvexa.app.R
import com.nuvexa.app.ui.components.PrimaryButton
import com.nuvexa.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: ImageVector,
    val titleRes: Int,
    val bodyRes: Int,
)

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
    val scheme = MaterialTheme.colorScheme

    fun finish() = viewModel.completeOnboarding(onFinished)

    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            scheme.primaryContainer.copy(alpha = 0.54f),
                            scheme.background,
                            scheme.secondaryContainer.copy(alpha = 0.20f),
                            scheme.background,
                        ),
                    ),
                )
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.l),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = ::finish) {
                        Text(
                            text = stringResource(R.string.action_skip),
                            color = scheme.onSurfaceVariant,
                        )
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
                                .size(if (selected) 22.dp else 8.dp, 8.dp)
                                .background(
                                    color = if (selected) {
                                        scheme.primary
                                    } else {
                                        scheme.outline.copy(alpha = 0.54f)
                                    },
                                    shape = CircleShape,
                                ),
                        )
                    }
                }

                PrimaryButton(
                    text = stringResource(
                        if (pagerState.currentPage == pages.lastIndex) {
                            R.string.action_get_started
                        } else {
                            R.string.action_continue
                        },
                    ),
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            finish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val spacing = LocalSpacing.current
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = scheme.surface.copy(alpha = 0.94f),
            contentColor = scheme.onSurface,
            border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.22f)),
            shadowElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    shape = CircleShape,
                    color = scheme.primaryContainer,
                    contentColor = scheme.onPrimaryContainer,
                    border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.18f)),
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(spacing.xl)
                            .size(58.dp),
                        tint = scheme.primary,
                    )
                }

                androidx.compose.foundation.layout.Spacer(Modifier.height(spacing.xxl))

                Text(
                    text = stringResource(page.titleRes),
                    style = MaterialTheme.typography.headlineLarge,
                    color = scheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                androidx.compose.foundation.layout.Spacer(Modifier.height(spacing.m))

                Text(
                    text = stringResource(page.bodyRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
