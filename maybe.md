//隐藏键盘
val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
imm.hideSoftInputFromWindow(editTextEvent.windowToken,0)

<translate xmlns:android="http://schemas.android.com/apk/res/android"
android:duration="@android:integer/config_shortAnimTime"
android:interpolator="@android:anim/decelerate_interpolator"
android:fromXDelta="100%"
android:toXDelta="0%">
</translate>