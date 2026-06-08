package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SBI Net Banking", appName)
  }

  @Test
  fun `test viewModel initialization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val app = context as android.app.Application
    val model = com.example.viewmodel.BankViewModel(app)
    assertNotNull(model)
  }
}
