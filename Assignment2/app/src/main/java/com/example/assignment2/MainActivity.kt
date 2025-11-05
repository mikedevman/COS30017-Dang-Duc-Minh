package com.example.assignment2

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.parcelize.Parcelize
import android.widget.LinearLayout
import android.util.Log

// data class for a guitar, making it parcelable to pass between activities
@Parcelize
data class Guitar(
    val name: String,
    val price: Int,
    @DrawableRes val imageResId: Int,
    val description: String,
    var rating: Float,
    val accessories: List<String>,
    var borrowedDate: Long = 0L
) : Parcelable

class MainActivity : AppCompatActivity() {

    private var credits = Credits(999999)
    private lateinit var userButton: Button
    private lateinit var guitars: MutableList<Guitar>
    private var borrowHistory = mutableListOf<Guitar>()
    private var currentRegion = "USA"
    private var selectedCardIndex = 0

    companion object {
        private const val TAG = "MainActivity"
    }

    // handles the result from checkoutactivity
    private val checkoutLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedGuitar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra("UPDATED_GUITAR_EXTRA", Guitar::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra<Guitar>("UPDATED_GUITAR_EXTRA")
            }
            val newBalance = result.data?.getIntExtra("NEW_BALANCE_EXTRA", -1)

            if (updatedGuitar != null && newBalance != null && newBalance != -1) {
                credits.balance = newBalance
                borrowHistory.add(updatedGuitar)

                val indexGuitar = guitars.indexOfFirst { it.name == updatedGuitar.name }
                if (indexGuitar != -1) {
                    guitars[indexGuitar] = updatedGuitar
                    findViewById<RatingBar>(getRatingBarId(indexGuitar)).rating = updatedGuitar.rating
                }
            }
        }
    }

    // saves the app's state before it's destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("GUITARS_STATE", ArrayList(guitars))
        outState.putParcelableArrayList("HISTORY_STATE", ArrayList(borrowHistory))
        outState.putInt("CREDITS_STATE", credits.balance)
        outState.putString("REGION_STATE", currentRegion)
        outState.putInt("SELECTED_CARD_STATE", selectedCardIndex)
    }

    // called when the activity is first created or recreated
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if state is being restored from an intent (e.g., location change)
        if (intent.hasExtra("GUITARS_STATE")) {
            currentRegion = intent.getStringExtra("SELECTED_REGION") ?: "USA"
            selectedCardIndex = intent.getIntExtra("SELECTED_CARD_STATE", 0)
            intent.getParcelableArrayListExtra<Guitar>("GUITARS_STATE")?.let {
                guitars = it.toMutableList()
            }
            intent.getParcelableArrayListExtra<Guitar>("HISTORY_STATE")?.let {
                borrowHistory = it.toMutableList()
            }
            // check if state is being restored from a saved instance state (e.g., rotation)
        } else if (savedInstanceState != null) {
            credits.balance = savedInstanceState.getInt("CREDITS_STATE")
            currentRegion = savedInstanceState.getString("REGION_STATE", "USA")
            selectedCardIndex = savedInstanceState.getInt("SELECTED_CARD_STATE")
            savedInstanceState.getParcelableArrayList<Guitar>("GUITARS_STATE")?.let {
                guitars = it.toMutableList()
            }
            savedInstanceState.getParcelableArrayList<Guitar>("HISTORY_STATE")?.let {
                borrowHistory = it.toMutableList()
            }
            // handle initial app launch
        } else {
            intent.getStringExtra("SELECTED_REGION")?.let {
                currentRegion = it
            }
        }

        updateLayoutForRegion()
    }

    // sets the correct layout file based on region and orientation, then initializes the UI
    private fun updateLayoutForRegion() {
        val orientation = resources.configuration.orientation
        val layoutId = when {
            currentRegion == "USA" && orientation == Configuration.ORIENTATION_PORTRAIT -> R.layout.vertical_view_usa
            currentRegion == "USA" && orientation == Configuration.ORIENTATION_LANDSCAPE -> R.layout.horizontal_view_usa
            currentRegion == "UAE" && orientation == Configuration.ORIENTATION_PORTRAIT -> R.layout.vertical_view_uae
            else -> R.layout.horizontal_view_uae
        }
        setContentView(layoutId)

        userButton = findViewById(R.id.userButton)
        userButton.setOnClickListener {
            showUserInfoDialog()
        }

        windowInsets()
        setupLocationSpinner()
        guitarCards()
    }

    // creates and displays the dialog with user credits and borrow history
    private fun showUserInfoDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog.setContentView(R.layout.dialog_user_horizontal)
            dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            dialog.setContentView(R.layout.dialog_user_vertical)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val creditText = dialog.findViewById<TextView>(R.id.dialogCreditText)
        val historyText = dialog.findViewById<TextView>(R.id.dialogHistoryText)
        val closeButton = dialog.findViewById<Button>(R.id.dialogCloseButton)

        if (currentRegion == "USA") {
            closeButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4B0082"))
        } else if (currentRegion == "UAE") {
            closeButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3F51B5"))
        }

        creditText.text = "${credits.balance} Credits"

        if (borrowHistory.isEmpty()) {
            historyText.text = "No items borrowed yet."
        } else {
            val history = SpannableStringBuilder()
            val dateFormat = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

            borrowHistory.forEachIndexed { index, guitar ->
                val name = "${index + 1}. ${guitar.name}\n"
                val time = "  - Borrowed: ${dateFormat.format(Date(guitar.borrowedDate))}\n"
                val rating = "  - Rating: ${if (guitar.rating > 0) guitar.rating else "Not Rated"}\n"
                val accessories = "  - Accessories: ${guitar.accessories.joinToString(", ")}\n"

                history.append(name)
                history.append(time)
                history.append(rating)
                history.append(accessories)
                if (index < borrowHistory.size - 1) {
                    history.append("\n")
                }
            }
            historyText.text = history
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // helper function to get ratingbar id based on guitar index
    private fun getRatingBarId(index: Int): Int {
        return when (index) {
            0 -> R.id.ratingBlack1
            1 -> R.id.ratingCory
            2 -> R.id.ratingSrv
            3 -> R.id.ratingGovan
            else -> -1
        }
    }

    // initializes guitar data and sets up listeners for all cards and buttons
    private fun guitarCards() {
        // initialize the guitars list only once
        if (!::guitars.isInitialized) {
            guitars = mutableListOf(
                Guitar(
                    "Fender John Mayer 'Black1' Stratocaster",
                    38990,
                    R.drawable.black1,
                    "Inspired by John Mayer’s legendary tone, the Black1 delivers smooth vintage warmth, expressive mids, and the soulful sound that defined his iconic blues style.",
                    0f,
                    listOf("Fender Hard Case", "Ernie Ball Silver Slinky Electric Guitar String Set")
                ),
                Guitar(
                    "Fender Cory Wong Stratocaster",
                    2359,
                    R.drawable.cory,
                    "Designed with funk master Cory Wong, this Stratocaster captures his signature clean, percussive tone with tight response and effortless playability.",
                    0f,
                    listOf("Fender Gig Bag", "Cory Wong Compressor")
                ),
                Guitar(
                    "Fender SRV Stratocaster",
                    7450,
                    R.drawable.srv,
                    "Modeled after Stevie Ray Vaughan’s iconic Strat, this guitar channels his bold Texas blues energy with thick sustain and fiery tone.",
                    0f,
                    listOf("Fender Hard Case", "SRV Strap")
                ),
                Guitar(
                    "Charvel Guthrie Govan San Dimas",
                    3699,
                    R.drawable.govan,
                    "Created with virtuoso Guthrie Govan, the San Dimas combines modern precision, tonal flexibility, and exceptional comfort for ultimate performance.",
                    0f,
                    listOf("Charvel Gig Bag")
                )
            )
        }

        val buttonIds = listOf(R.id.borrowBlack1, R.id.borrowCory, R.id.borrowSrv, R.id.borrowGovan)
        val ratingBarIds = listOf(R.id.ratingBlack1, R.id.ratingCory, R.id.ratingSrv, R.id.ratingGovan)
        val orientation = resources.configuration.orientation
        val isHorizontal = orientation == Configuration.ORIENTATION_LANDSCAPE
        val cardIds = listOf(R.id.black1, R.id.cory, R.id.srv, R.id.govan)

        guitars.forEachIndexed { index, guitar ->
            findViewById<Button>(buttonIds[index]).setOnClickListener {
                // in landscape, only allow borrowing the selected card
                if (isHorizontal && selectedCardIndex != index) {
                    return@setOnClickListener
                }
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("GUITAR_EXTRA", guitar)
                    putExtra("USER_BALANCE_EXTRA", credits.balance)
                    putExtra("SELECTED_REGION", currentRegion)
                }
                checkoutLauncher.launch(intent)
            }

            val ratingBar = findViewById<RatingBar>(ratingBarIds[index])
            ratingBar.rating = guitar.rating
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    guitars[index].rating = rating
                }
            }
        }

        if (isHorizontal) {
            // in landscape, clicking a card selects it
            cardIds.forEachIndexed { index, cardId ->
                findViewById<View>(cardId).setOnClickListener {
                    selectedCardIndex = index
                    updateCardSelection()
                }
            }
            cardSelection()
        } else {
            // in portrait, set up button-based scrolling
            val cards = listOf<View>(
                findViewById(R.id.black1),
                findViewById(R.id.cory),
                findViewById(R.id.srv),
                findViewById(R.id.govan)
            )
            when (findViewById<View>(R.id.mainScroll)) {
                is HorizontalScrollView -> horizontalScrolling(cards)
                is ScrollView -> verticalScrolling(cards)
            }
        }
    }

    // handles the logic for the horizontal card carousel in portrait mode
    private fun horizontalScrolling(cards: List<View>) {
        val mainScroll: HorizontalScrollView = findViewById(R.id.mainScroll)
        val nextButton: Button = findViewById(R.id.nextButton)
        val prevButton: Button = findViewById(R.id.prevButton)

        // disable direct touch scrolling
        mainScroll.setOnTouchListener { _, _ -> true }

        fun scrollToCard(index: Int) {
            val card = cards.getOrNull(index)
            card?.let {
                // calculates the scroll position needed to center the card
                val cardCenter = it.left + (it.width / 2)
                val scrollCenter = mainScroll.width / 2
                mainScroll.smoothScrollTo(cardCenter - scrollCenter, 0)
            }
        }

        // scrolls to the currently selected card after the layout is drawn
        mainScroll.post { scrollToCard(selectedCardIndex) }

        nextButton.setOnClickListener {
            if (selectedCardIndex < cards.size - 1) {
                selectedCardIndex++
                scrollToCard(selectedCardIndex)
            }
        }

        prevButton.setOnClickListener {
            if (selectedCardIndex > 0) {
                selectedCardIndex--
                scrollToCard(selectedCardIndex)
            }
        }
    }

    // handles the logic for vertical card scrolling in portrait mode
    private fun verticalScrolling(cards: List<View>) {
        val mainScroll: ScrollView = findViewById(R.id.mainScroll)
        val nextButton: Button = findViewById(R.id.nextButton)
        val prevButton: Button = findViewById(R.id.prevButton)

        // disable direct touch scrolling
        mainScroll.setOnTouchListener { _, _ -> true }

        fun scrollToCard(index: Int) {
            val card = cards.getOrNull(index)
            card?.let {
                mainScroll.smoothScrollTo(0, it.top)
            }
        }

        mainScroll.post { scrollToCard(selectedCardIndex) }

        nextButton.setOnClickListener {
            if (selectedCardIndex < cards.size - 1) {
                selectedCardIndex++
                scrollToCard(selectedCardIndex)
            }
        }

        prevButton.setOnClickListener {
            if (selectedCardIndex > 0) {
                selectedCardIndex--
                scrollToCard(selectedCardIndex)
            }
        }
    }

    // handles next/previous button logic for landscape mode card selection
    private fun cardSelection() {
        updateCardSelection()
        val nextButton: Button? = findViewById(R.id.nextButton)
        val prevButton: Button? = findViewById(R.id.prevButton)

        nextButton?.setOnClickListener {
            if (selectedCardIndex < guitars.size - 1) {
                selectedCardIndex++
                updateCardSelection()
            }
        }

        prevButton?.setOnClickListener {
            if (selectedCardIndex > 0) {
                selectedCardIndex--
                updateCardSelection()
            }
        }
    }

    // visually updates the selected card in landscape with a border
    private fun updateCardSelection() {
        val cardIds = listOf(R.id.black1, R.id.cory, R.id.srv, R.id.govan)
        val selectedDrawable = if (currentRegion == "USA") R.drawable.selected_card_usa else R.drawable.selected_card_uae

        cardIds.forEachIndexed { index, cardId ->
            val cardView = findViewById<View>(cardId)
            if (index == selectedCardIndex) {
                cardView.setBackgroundResource(selectedDrawable)
            } else {
                cardView.setBackgroundColor(Color.parseColor("#CCFFFFFF"))
            }
        }
    }

    // sets up the spinner for changing location
    private fun setupLocationSpinner() {
        val spinner: Spinner = findViewById(R.id.location_spinner)
        val locations = arrayOf("USA", "UAE")
        val adapter = ArrayAdapter(this, R.layout.spinner_item_layout, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.setSelection(locations.indexOf(currentRegion))

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRegion = parent.getItemAtPosition(position).toString()
                // restart the activity with new state if the region changes
                if (selectedRegion != currentRegion) {
                    val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                        putExtra("SELECTED_REGION", selectedRegion)
                        putExtra("SELECTED_CARD_STATE", selectedCardIndex)
                        putParcelableArrayListExtra("GUITARS_STATE", ArrayList(guitars))
                        putParcelableArrayListExtra("HISTORY_STATE", ArrayList(borrowHistory))
                    }
                    startActivity(intent)
                    finish()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // handles window insets for edge-to-edge display
    private fun windowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
