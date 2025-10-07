package com.example.cos20031

class MainFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment)
    }

    fun showText(firstName: String, lastName: String){
        val bottomFragment = supportFragmentManager.findFragmentById(R.id.fragment_bottom) as BottomFragment
        bottomFragment.showText(firstName, lastName)
    }
}