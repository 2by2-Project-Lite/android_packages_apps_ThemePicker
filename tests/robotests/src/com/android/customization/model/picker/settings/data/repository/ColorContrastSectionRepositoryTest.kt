/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.customization.model.picker.settings.data.repository

import androidx.test.filters.SmallTest
import com.android.customization.picker.settings.data.repository.ColorContrastSectionRepository
import com.android.wallpaper.testing.FakeUiModeManager
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@SmallTest
@RunWith(RobolectricTestRunner::class)
class ColorContrastSectionRepositoryTest {
    private val uiModeManager = FakeUiModeManager()
    private lateinit var underTest: ColorContrastSectionRepository

    private lateinit var bgDispatcher: TestCoroutineDispatcher

    @Before
    fun setUp() {
        bgDispatcher = TestCoroutineDispatcher()
        underTest = ColorContrastSectionRepository(uiModeManager, bgDispatcher)
    }

    @Test
    fun creationSucceeds() {
        assertThat(underTest).isNotNull()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun contrastFlowEmitsValues() = runBlockingTest {
        val nextContrastValues = listOf(0.5f, 0.7f, 0.8f)
        // Set up a flow to collect all contrast values
        val flowCollector = mutableListOf<Float>()
        // Start collecting values from the flow
        val job = launch { underTest.contrast.collect { flowCollector.add(it) } }

        nextContrastValues.forEach { uiModeManager.setContrast(it) }

        // Ignore the first contrast value from constructing the repository
        val collectedValues = flowCollector.drop(1)
        assertThat(collectedValues).containsExactlyElementsIn(nextContrastValues)
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        bgDispatcher.cleanupTestCoroutines()
    }
}
