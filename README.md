# glidepreloaderexample



// activity 


 private var fullRequest: GlideRequest<Drawable>? = null
    private var thumbRequest: GlideRequest<Drawable>? = null
    lateinit var preloadSizeProvider: ViewPreloadSizeProvider<MixList>
    private val PRELOAD_AHEAD_ITEMS = 5
    private var backgroundThumbnailFetcher: GlidePreloadUtils.BackgroundThumbnailFetcher? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    
    // oncreate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       
       // for preloader
        val glideRequests = GlideApp.with(this)
        fullRequest = glideRequests
                .asDrawable()
                .centerCrop()
                .placeholder(R.drawable.image_pholder)

        thumbRequest = glideRequests
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(Picasso_handler.SQUARE_THUMB_SIZE)
                .transition(withCrossFade())


        preloadSizeProvider = ViewPreloadSizeProvider<MixList>()

        backgroundThread = HandlerThread("BackgroundThumbnailHandlerThread")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.getLooper())


        adp.updatePreloader(fullRequest, thumbRequest, preloadSizeProvider)
        
        val preloader = RecyclerViewPreloader(GlideApp.with(this), adp, preloadSizeProvider, PRELOAD_AHEAD_ITEMS)
        fmFeedRv.addOnScrollListener(preloader)
     
  
    }
    
    // after api
    
      if (backgroundThumbnailFetcher != null) {
                    backgroundThumbnailFetcher?.cancel()
                }

                backgroundThumbnailFetcher = GlidePreloadUtils.BackgroundThumbnailFetcher(mContext, feedLists)
                backgroundHandler?.post(backgroundThumbnailFetcher)

    
    // ondestroy
    
    override fun onDestroyView() {

        if (backgroundThumbnailFetcher != null) {
            backgroundThumbnailFetcher?.cancel()
            backgroundThumbnailFetcher = null
            backgroundThread?.quit()
            backgroundThread = null
        }



        super.onDestroyView()
    }

//
adapter 

 var fullRequest: GlideRequest<Drawable> ? = null
    var thumbRequest: GlideRequest<Drawable> ? = null
    var preloadSizeProvider: ViewPreloadSizeProvider<MixList> ? = null

 fun updatePreloader(fullRequest : GlideRequest<Drawable>?,  thumbRequest: GlideRequest<Drawable>?,  preloadSizeProvider : ViewPreloadSizeProvider<MixList>?){

        this.fullRequest = fullRequest
        this.thumbRequest = thumbRequest
        this.preloadSizeProvider = preloadSizeProvider


    }


override fun getPreloadItems(position: Int): MutableList<MixList> {

        return feedItemList.subList(position, position + 1)

    }

    override fun getPreloadRequestBuilder(item: MixList): RequestBuilder<*>? {
        return fullRequest?.thumbnail(thumbRequest?.load(item))?.load(item)

    }


// display

  fullRequest.load(pagerItems.get(position).getPreview_url())
                            .thumbnail(thumbRequest.load(pagerItems.get(position).getPreview_url()))
                            .into(pImg);
                            
                            



  

