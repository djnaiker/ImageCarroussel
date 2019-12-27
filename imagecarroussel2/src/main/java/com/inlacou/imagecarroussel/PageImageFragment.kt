package com.inlacou.imagecarroussel

import android.content.res.Resources
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
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

import com.squareup.picasso.Picasso
import android.view.*
import com.inlacou.imagecarroussel.utilities.Constans.ENABLE_SOUND


class PageImageFragment : Fragment() , TextureView.SurfaceTextureListener,
		MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{


	private var llIndicator: LinearLayout? = null
	private var shadow: ImageView? = null
	private var image: ImageView? = null
	private var mTextureView: TextureView? = null
	private var positionText: TextView? = null
	var onClickListener: ((Int) -> Unit)? = null

	private var mCurrentPage: Int = 0
	private var maxPages: Int = 0
	private var item: ItemElement? = null
	private var showTopShadow: Boolean = true
	private var positionDisplay: PositionDisplayMode = NONE
	private var enableSound: Boolean =false

	//VIDEO
	var mMediaPlayer: MediaPlayer? = null

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
		enableSound = data.getBoolean(ENABLE_SOUND, false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val v = inflater.inflate(R.layout.viewpager_layout_page_image, container, false)
		image = v.findViewById(R.id.image)
		mTextureView = v.findViewById(R.id.video)
		shadow = v.findViewById(R.id.shadow)
		positionText = v.findViewById(R.id.position_text)
		llIndicator = v.findViewById(R.id.indicator)

		mTextureView!!.surfaceTextureListener = this


		when (positionDisplay) {
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

		shadow?.visibility = if (showTopShadow) {
			View.VISIBLE
		} else {
			View.GONE
		}

		image?.setOnClickListener { onClickListener?.invoke(mCurrentPage) }

		val size = Point()
		activity?.windowManager?.defaultDisplay?.getSize(size)

		if(item!!.url != null){
			Picasso.get()
					.load(item!!.url)
					.fit()
					.centerCrop()
					.into(image)
		}

		 if (item!!.type == ItemType.VIDEO) {


			mTextureView!!.visibility = View.VISIBLE
		}

		return v
	}


	private fun setCurrentPageIndicator(position: Int) {
		llIndicator?.removeAllViews()
		(0 until position)
				.forEach { _ -> llIndicator?.addView(newLightCircle()) }
		llIndicator?.addView(newDarkCircle())
		(position + 1 until maxPages)
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
		(iv.layoutParams as ViewGroup.MarginLayoutParams).setMargins(4.dpToPx(), 0, 4.dpToPx(), 0)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			iv.setImageDrawable(context?.resources?.getDrawable(drawableResId, null))
		} else {
			iv.setImageDrawable(context?.resources?.getDrawable(drawableResId))
		}
		return iv
	}

	private fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

	override fun onResume() {
		super.onResume()
		if(mMediaPlayer != null && !mMediaPlayer!!.isPlaying){
			mMediaPlayer!!.start()
		}
	}

	override fun onPause() {
		super.onPause()
		if(mMediaPlayer != null && !mMediaPlayer!!.isPlaying){
			mMediaPlayer!!.pause()
		}
	}

	override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

	override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

	override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
		return true
	}

	override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {

		val surface = Surface(surface)
		mMediaPlayer = MediaPlayer()
		mMediaPlayer!!.setSurface(surface)
		mMediaPlayer!!.setDataSource(item!!.video)


		mMediaPlayer!!.setOnErrorListener(this)
		mMediaPlayer!!.setOnPreparedListener(this)
		mMediaPlayer!!.setOnCompletionListener (this)
		mMediaPlayer!!.prepareAsync()
	}

	override fun onPrepared(mp: MediaPlayer?) {
		image!!.visibility = View.GONE
		if(!mp!!.isPlaying){
			mp.isLooping = true
			if(!enableSound) mp.setVolume(0F,0F)
			mp.start()
		}
	}

	override fun onCompletion(mp: MediaPlayer?) {
		mp!!.start()
	}

	override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
		mp!!.release()
		return false
	}




}

