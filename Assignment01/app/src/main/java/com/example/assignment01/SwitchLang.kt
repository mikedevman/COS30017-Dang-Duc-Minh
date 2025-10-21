package com.example.assignment01

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class SwitchLang(
    private val titleText: TextView,
    private val tileScore: TextView,
    private val holdTitle: TextView,
    private val climbBtn: Button,
    private val fallBtn: Button,
    private val resetBtn: Button,
    private val startStopBtn: Button,
    private val langLabel: TextView,
    private val switchLangBtn: ImageView
) {
    fun update(isEnglish: Boolean, sessionActive: Boolean) {
        if (isEnglish) {
            titleText.text = "Bouldering Score Counter"
            tileScore.text = "Score"
            holdTitle.text = "Hold: "
            climbBtn.text = "↑\nClimb"
            fallBtn.text = "↓\nFall"
            resetBtn.text = "Reset"
            startStopBtn.text = if (sessionActive) "END SESSION" else "START SESSION"
            langLabel.text = "Change to Vietnamese"
            switchLangBtn.setImageResource(R.drawable.vn_flag)
        } else {
            titleText.text = "Bộ Đếm Điểm Leo Núi"
            tileScore.text = "Điểm"
            holdTitle.text = "Khối Đá: "
            climbBtn.text = "↑\nLeo"
            fallBtn.text = "↓\nNgã"
            resetBtn.text = "Đặt Lại"
            startStopBtn.text = if (sessionActive) "KẾT THÚC" else "BẮT ĐẦU"
            langLabel.text = "Chuyển sang Tiếng Anh"
            switchLangBtn.setImageResource(R.drawable.en_flag)
        }
    }
}
