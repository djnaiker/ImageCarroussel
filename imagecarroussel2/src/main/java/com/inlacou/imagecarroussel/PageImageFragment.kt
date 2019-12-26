package com.inlacou.imagecarroussel

import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.inlacou.imagecarroussel.model.ItemElement
import com.inlacou.imagecarroussel.types.ItemType
import com.inlacou.imagecarroussel.utilities.Constans.CURRENT_PAGE
import com.inlacou.imagecarroussel.utilities.Constans.MAX_PAGES
import com.inlacou.imagecarroussel.utilities.Constans.POSITION_DISPLAY
import com.inlacou.imagecarroussel.utilities.Constans.SHOW_TOP_SHADOW
import com.inlacou.imagecarroussel.utilities.Constans.URL
import com.inlacou.imagecarroussel.types.PositionDisplayMode.*
import com.inlacou.imagecarroussel.types.PositionDisplayMode

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import android.os.StrictMode.setThreadPolicy



/**
 * Created by inlacou on 3/05/16.
 */
class PageImageFragment : Fragment() {

	private var llIndicator: LinearLayout? = null
	private var shadow: ImageView? = null
	private var image: ImageView? = null
	private var video: FrameLayout? = null
	private var positionText: TextView? = null
	var onClickListener: ((Int) -> Unit)? = null

	private var mCurrentPage: Int = 0
	private var maxPages: Int = 0
	private var item: ItemElement? = null
	private var showTopShadow: Boolean = true
	private var positionDisplay: PositionDisplayMode = NONE

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		/** Getting the arguments to the Bundle object  */
		val data = arguments

		/** Getting integer data of the key current_page from the bundle  */
		maxPages = data!!.getInt(MAX_PAGES, 0)
		mCurrentPage = data.getInt(CURRENT_PAGE, 0)
		item = data.getSerializable(URL) as ItemElement?
		showTopShadow = data.getBoolean(SHOW_TOP_SHADOW, true)
		positionDisplay = values()[data.getInt(POSITION_DISPLAY)]
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val v = inflater.inflate(R.layout.viewpager_layout_page_image, container, false)
		image = v.findViewById(R.id.image)
		video = v.findViewById(R.id.video)
		shadow = v.findViewById(R.id.shadow)
		positionText = v.findViewById(R.id.position_text)
		llIndicator = v.findViewById(R.id.indicator)

		when(positionDisplay){
			TEXT -> {
				llIndicator?.visibility = View.GONE
				positionText?.visibility = View.VISIBLE
				positionText?.text = "${mCurrentPage + 1}/$maxPages"
			}
			CIRCLES -> {
				positionText?.visibility = View.VISIBLE
				positionText?.visibility = View.GONE
				setCurrentPageIndicator(mCurrentPage)
			}
			NONE -> {
				llIndicator?.visibility = View.GONE
				positionText?.visibility = View.GONE
			}
		}

		shadow?.visibility = if(showTopShadow){
			View.VISIBLE
		}else{
			View.GONE
		}

		image?.setOnClickListener { onClickListener?.invoke(mCurrentPage) }

		val size = Point()
		activity?.windowManager?.defaultDisplay?.getSize(size)

		if(item!!.type == ItemType.IMAGE){
			Picasso.get()
					.load(item!!.url)
					.fit()
					.centerCrop()
					.into(image)
		}else if(item!!.type == ItemType.VIDEO){

			image!!.visibility = View.GONE
			var videoLayout = VideoLayout(context!!)
			videoLayout.setGravity(VideoLayout.VGravity.centerCrop)
			videoLayout.setIsLoop(true)
			videoLayout.setPathOrUrl(item!!.video!!)
			video!!.addView(videoLayout)
		}

		return v
	}


	private fun setCurrentPageIndicator(position: Int){
		llIndicator?.removeAllViews()
		(0 until position)
				.forEach { _ -> llIndicator?.addView(newLightCircle()) }
		llIndicator?.addView(newDarkCircle())
		(position+1 until maxPages)
				.forEach { _ -> llIndicator?.addView(newLightCircle()) }
	}

	private fun newDarkCircle(): ImageView {
		return newCircle(R.drawable.carousel_indicator_active)
	}

	private fun newLightCircle(): ImageView {
		return newCircle(R.drawable.carousel_indicator_inactive)
	}

	private fun newCircle(drawableResId: Int): ImageView {
		val iv = ImageView(context)
		iv.layoutParams = LinearLayout.LayoutParams(12.dpToPx(), 12.dpToPx())
		(iv.layoutParams as ViewGroup.MarginLayoutParams).setMargins(4.dpToPx(),0,4.dpToPx(),0)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			iv.setImageDrawable(context?.resources?.getDrawable(drawableResId, null))
		}else{
			iv.setImageDrawable(context?.resources?.getDrawable(drawableResId))
		}
		return iv
	}

	private fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
}
