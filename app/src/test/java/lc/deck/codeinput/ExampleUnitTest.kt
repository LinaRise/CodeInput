package lc.deck.codeinput


import android.app.Activity
import lc.deck.codeinput.ui._global.custom.OtpEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController


@RunWith(RobolectricTestRunner::class)
class OtpViewTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity


    private var otpEditText: OtpEditText? = null

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
        val builder = Robolectric.buildAttributeSet()
        otpEditText = OtpEditText(activity, builder.build())
    }

    @Test
    fun `should display empty string by default`() {
        assertEquals("", otpEditText?.text)
    }

    @Test
    fun `should display 4 digits for 4 maxLength string after setting 4 digits`() {
        otpEditText?.maxLength = 4
        otpEditText?.text = "1234"
        assertEquals("1234", otpEditText?.text.toString())
    }

    @Test
    fun `should display 4 digits for 4 maxLength string after setting 6 digits`() {
        otpEditText?.maxLength = 4
        otpEditText?.text = "123456"
        assertEquals("1234", otpEditText?.text.toString())
    }

    @Test
    fun `should display 3 digits for 4 maxLength string after setting 3 digits`() {
        otpEditText?.maxLength = 4
        otpEditText?.text = "123"
        assertEquals("123", otpEditText?.text.toString())
    }

}