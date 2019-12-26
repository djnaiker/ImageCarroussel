package com.inlacou.imagecarrousselapp

import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.inlacou.imagecarroussel.model.AutoSwipeMode

import com.inlacou.imagecarroussel.ImageCarousel
import com.inlacou.imagecarroussel.model.ImageCarouselMdl
import com.inlacou.imagecarroussel.model.ItemElement
import com.inlacou.imagecarroussel.types.PositionDisplayMode

import java.util.ArrayList

class CollapsingActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_collapsing_toolbar)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
		collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
		collapsingToolbarLayout.isTitleEnabled = true
		collapsingToolbarLayout.title = "Collapsing"
		val imagecarroussel = findViewById<ImageCarousel>(R.id.imagecarroussel)

		val urls = ArrayList<ItemElement>()



		urls.add(ItemElement("https://i.pinimg.com/originals/fa/37/c2/fa37c29afdfb42efad366e45fbe10033.png"))
		urls.add(ItemElement("https://pro-rankedboost.netdna-ssl.com/wp-content/uploads/2016/08/Togepi-Pokemon-Go.png"))
		urls.add(ItemElement("http://assets.pokemon.com/assets/cms2/img/pokedex/full//748.png"))
		urls.add(ItemElement("https://vignette3.wikia.nocookie.net/pokemon/images/b/b4/393Piplup_Pokemon_Ranger_Guardian_Signs.png/revision/latest?cb=20150109224144"))

		imagecarroussel.model = ImageCarouselMdl(
				fragmentManager = supportFragmentManager,
				urls = urls,
				positionDisplay = PositionDisplayMode.TEXT,
				autoSwipe = AutoSwipeMode(active = true),
				showTopShadow = false)

		setSupportActionBar(toolbar)

		val fab = findViewById<FloatingActionButton>(R.id.fab)
		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_scrolling, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		val id = item.itemId


		return if (id == R.id.action_settings) {
			true
		} else super.onOptionsItemSelected(item)
	}
}
