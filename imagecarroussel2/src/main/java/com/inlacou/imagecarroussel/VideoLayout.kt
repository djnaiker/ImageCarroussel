package com.inlacou.imagecarroussel

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.TypedArray
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.webkit.URLUtil
import android.widget.FrameLayout

import java.io.IOException
import java.util.HashMap

class VideoLayout : FrameLayout, TextureView.SurfaceTextureListener {

    private var FILE_NAME: String? = null
    private var VIDEO_GRAVITY: Int = 0
    var videoSurface: TextureView? = null
        private set
    private var mVideoWidth: Float = 0.toFloat()
    private var mVideoHeight: Float = 0.toFloat()
    private val TAG = "VideoLayout"
    var mediaPlayer: MediaPlayer? = null
        private set
    private var isUrl: Boolean = false
    private var IS_LOOP = true

    enum class VGravity {
        start,
        end,
        centerCrop,
        none;

        val value: Int
            get() {
                when (this) {
                    end -> return 1
                    none -> return 3
                    start -> return 0
                    centerCrop -> return 2
                    else -> return 2
                }
            }
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VideoLayout, 0, 0)
        try {
            FILE_NAME = a.getString(R.styleable.VideoLayout_path_or_url)
            VIDEO_GRAVITY = a.getInteger(R.styleable.VideoLayout_video_gravity, 2)
            IS_LOOP = a.getBoolean(R.styleable.VideoLayout_loop, true)
        } finally {
            a.recycle()
        }

        isUrl = URLUtil.isValidUrl(FILE_NAME)
        initViews()
        addView(videoSurface)
        setListeners()


        if (VIDEO_GRAVITY != 3) {
            calculateVideoSize()
            surfaceSetup()
        }
    }


    private fun initViews() {
        videoSurface = TextureView(context)
    }

    private fun setListeners() {
        videoSurface!!.surfaceTextureListener = this
    }

    private fun calculateVideoSize() {
        try {
            val metaRetriever = MediaMetadataRetriever()
            if (isUrl)
                metaRetriever.setDataSource(FILE_NAME, HashMap())
            else {
                val afd = context.assets.openFd(FILE_NAME!!)
                metaRetriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            val height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            mVideoHeight = java.lang.Float.parseFloat(height)
            mVideoWidth = java.lang.Float.parseFloat(width)
            metaRetriever.release()
        } catch (e: IOException) {
            Log.d(TAG, e.message)
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            Log.d(TAG, e.message)
            e.printStackTrace()
        }

    }

    private fun updateTextureViewSize(viewWidth: Int, viewHeight: Int) {
        var scaleX = 1.0f
        var scaleY = 1.0f

        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth
            scaleY = mVideoHeight / viewHeight
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            scaleY = viewWidth / mVideoWidth
            scaleX = viewHeight / mVideoHeight
        } else if (viewWidth > mVideoWidth) {
            scaleY = viewWidth / mVideoWidth / (viewHeight / mVideoHeight)
        } else if (viewHeight > mVideoHeight) {
            scaleX = viewHeight / mVideoHeight / (viewWidth / mVideoWidth)
        }

        val pivotPointX = if (VIDEO_GRAVITY == 0) 0 else if (VIDEO_GRAVITY == 1) viewWidth else viewWidth / 2
        val pivotPointY = viewHeight / 2

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, pivotPointX.toFloat(), pivotPointY.toFloat())

        videoSurface!!.setTransform(matrix)
        videoSurface!!.layoutParams = FrameLayout.LayoutParams(viewWidth, viewHeight)
    }


    private fun surfaceSetup() {
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels
        updateTextureViewSize(screenWidth, screenHeight)
    }

    private fun surfaceAvaibleWorkers(surfaceTexture: SurfaceTexture) {
        val surface = Surface(surfaceTexture)

        try {
            mediaPlayer = MediaPlayer()
            if (isUrl)
                mediaPlayer!!.setDataSource(FILE_NAME)
            else {
                val afd = context.assets.openFd(FILE_NAME!!)
                mediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            mediaPlayer!!.setVolume(0f, 0f)
            mediaPlayer!!.setSurface(surface)
            mediaPlayer!!.isLooping = IS_LOOP
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener { it.start() }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    private fun changeVideo() {
        try {
            onDestroyVideoLayout()
            mediaPlayer = MediaPlayer()
            if (isUrl)
                mediaPlayer!!.setDataSource(FILE_NAME)
            else {
                val afd = context.assets.openFd(FILE_NAME!!)
                mediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            mediaPlayer!!.setVolume(0f, 0f)
            mediaPlayer!!.isLooping = IS_LOOP
            mediaPlayer!!.setSurface(Surface(videoSurface!!.surfaceTexture))
            mediaPlayer!!.setSurface(Surface(videoSurface))
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener { it.start() }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        surfaceAvaibleWorkers(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    fun onDestroyVideoLayout() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            } catch (e: IllegalStateException) {
                Log.d(TAG, e.message)
            }

        }
    }

    fun onResumeVideoLayout() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying)
            try {
                mediaPlayer!!.start()
            } catch (ignored: IllegalStateException) {

            }

    }

    fun onPauseVideoLayout() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying)
            try {
                mediaPlayer!!.pause()
            } catch (ignored: IllegalStateException) {

            }

    }

    fun setPathOrUrl(FILE_NAME: String) {
        this.FILE_NAME = FILE_NAME

        isUrl = URLUtil.isValidUrl(FILE_NAME)

        if (videoSurface == null) {
            initViews()
            addView(videoSurface)
            setListeners()
        }

        if (VIDEO_GRAVITY != 3) {
            calculateVideoSize()
            surfaceSetup()
        }

        if (videoSurface != null) {
            changeVideo()
        }
    }

    fun setIsLoop(IS_LOOP: Boolean) {
        this.IS_LOOP = IS_LOOP
    }

    fun setGravity(gravity: VGravity) {
        VIDEO_GRAVITY = gravity.value
    }

}
