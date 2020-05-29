package com.applogist.linkresolver_sample

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.applogist.linkresolver.LinkResolver
import com.applogist.linkresolver.LinkResolverError
import com.applogist.linkresolver.LinkResolverListener
import com.applogist.linkresolver.MetaData
import com.applogist.linkresolver_sample.databinding.ItemLinkPreviewBinding
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val items = arrayListOf<TestModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(application)

        recyclerView.addItemDecoration(SpacesItemDecoration(ConvertUtils.dp2px(10f)))

        testLinks500.forEach {
            items.add(TestModel("Lorem iprum dolar sit amet ${it} test here https://www.google.com \n"))
        }

        LastAdapter(items, BR.data)
            .map<TestModel>(
                Type<ItemLinkPreviewBinding>(R.layout.item_link_preview)
                    .onBind {
                        LinkResolver(
                            it.binding.data!!.url,
                            object : LinkResolverListener {
                                override fun onSuccess(metaData: MetaData) {
                                    LogUtils.d(it.adapterPosition, metaData.userValue)

                                    if(it.adapterPosition != metaData.userValue){
                                        return
                                    }

                                    if (metaData.image.isNotEmpty()) {

                                        val multi = MultiTransformation(
                                            CenterCrop(),
                                            RoundedCornersTransformation(
                                                ConvertUtils.dp2px(3f),
                                                0,
                                                RoundedCornersTransformation.CornerType.LEFT
                                            )
                                        )

                                        Glide.with(this@MainActivity)
                                            .load(metaData.image)
                                            .error(R.mipmap.ic_launcher)
                                            .apply(RequestOptions.bitmapTransform(multi)).into(it.binding.linkImageView)

                                        it.binding.linkImageView.visibility = View.VISIBLE
                                    } else {
                                        it.binding.linkImageView.visibility = View.GONE
                                    }
                                    it.binding.linkTitleTextView.text = metaData.title
                                    it.binding.linkDescriptionTextView.text = metaData.description
                                    it.binding.linkUrlTextView.text = Uri.parse(metaData.url).host
                                }

                                override fun onError(error: LinkResolverError) {
                                    LogUtils.e(error.message)
                                }

                            }, it.adapterPosition).resolve()

                    }
                    .onRecycle {
                        it.binding.linkTitleTextView.text = ""
                        it.binding.linkDescriptionTextView.text = ""
                        it.binding.linkUrlTextView.text = ""
                        it.binding.linkImageView.setImageDrawable(null)
                    }
            )
            .into(recyclerView)
    }
}
