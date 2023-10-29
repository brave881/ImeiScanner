package com.example.imeiscanner.ui.fragments

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.example.imeiscanner.models.PhoneDataModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EditFragmentTest {

    private lateinit var scenario: FragmentScenario<EditFragment>

    @Mock
    private lateinit var bundle: Bundle

    @Mock
    private lateinit var phoneDataModel: PhoneDataModel

    @Mock
    private lateinit var imei1: EditText

    @Mock
    private lateinit var imei2: EditText

    @Mock
    private lateinit var serialNumber: EditText

    @Mock
    private lateinit var battery: EditText

    @Mock
    private lateinit var price: EditText

    @Mock
    private lateinit var memory: EditText

    @Mock
    private lateinit var name: EditText

    @Mock
    private lateinit var dateView: TextView

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_AppCompat)
        `when`(bundle.getSerializable(POSITION_ITEM)).thenReturn(phoneDataModel)
    }

    @Test
    fun testInstallItemsToEditTextsWhenFragmentResultListenerTriggeredThenEditTextsAndTextViewsPopulated() {
        scenario.onFragment { fragment ->
            fragment.installItemsToEditTexts()

            verify(imei1).setText(phoneDataModel.phone_imei1)
            verify(imei2).setText(phoneDataModel.phone_imei2)
            verify(serialNumber).setText(phoneDataModel.phone_serial_number)
            verify(battery).setText(phoneDataModel.phone_battery_info)
            verify(price).setText(phoneDataModel.phone_price)
            verify(memory).setText(phoneDataModel.phone_memory)
            verify(name).setText(phoneDataModel.phone_name)
            verify(dateView).setText(phoneDataModel.phone_added_date)

            assertEquals(fragment.phoneId, phoneDataModel.id)
            assertEquals(fragment.favouriteState, phoneDataModel.favourite_state)
        }
    }
}