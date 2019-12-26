package com.inlacou.imagecarrousselapp

import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.FloatingActionButton
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

class NormalActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_normal)
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		val imagecarroussel = findViewById<ImageCarousel>(R.id.imagecarroussel)

		val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
		StrictMode.setThreadPolicy(policy)

		val urls = ArrayList<ItemElement>()

		urls.add(ItemElement("https://vignette3.wikia.nocookie.net/pokemon/images/b/b4/393Piplup_Pokemon_Ranger_Guardian_Signs.png/revision/latest?cb=20150109224144", "http://int.bvapps.es/amc/test1.mp4"))
		urls.add(ItemElement("https://pro-rankedboost.netdna-ssl.com/wp-content/uploads/2016/08/Togepi-Pokemon-Go.png"))
		urls.add(ItemElement("http://assets.pokemon.com/assets/cms2/img/pokedex/full//748.png"))
		urls.add(ItemElement("https://vignette3.wikia.nocookie.net/pokemon/images/b/b4/393Piplup_Pokemon_Ranger_Guardian_Signs.png/revision/latest?cb=20150109224144"))
		urls.add(ItemElement("https://vignette.wikia.nocookie.net/es.pokemon/images/4/4f/Torchic.png/revision/latest?cb=20140612153748"))
		urls.add(ItemElement("https://vignette.wikia.nocookie.net/es.pokemon/images/4/43/Bulbasaur.png/revision/latest?cb=20170120032346"))
		urls.add(ItemElement("https://assets.pokemon.com/assets/cms2/img/pokedex/full//133.png"))

		imagecarroussel.model = ImageCarouselMdl(
				fragmentManager = supportFragmentManager,
				urls = urls,
				positionDisplay = PositionDisplayMode.CIRCLES,
				autoSwipe = AutoSwipeMode(active = false),
				showTopShadow = false,
				pagePaddingRight = 60,
				pageMargin = 15,
				onItemClick = {
					System.out.println("OnItemClick")
					false
				},
				onPageShown = {
					System.out.println("onPageShown")
				})

		setSupportActionBar(toolbar)

		val fab = findViewById<FloatingActionButton>(R.id.fab)
		fab.setOnClickListener { view ->
			imagecarroussel.switchAutoSwipeStatus()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_scrolling, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId


		return if (id == R.id.action_settings) {
			true
		} else super.onOptionsItemSelected(item)
	}
}
