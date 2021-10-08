package com.example.dankmemes2.Adapters

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.dankmemes2.DataClasses.Meme
import com.example.dankmemes2.Fragments.MainFragment
import com.example.dankmemes2.R
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MemeListAdapter(val activity: Activity, val context: Context) : RecyclerView.Adapter<MemeListAdapter.MemeViewHolder>() {

    private val items: ArrayList<Meme> = ArrayList()
    val mainFragment = MainFragment()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meme, parent, false)
        val viewHolder = MemeViewHolder(view)
        return viewHolder
    }


    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val currentImage = items[position]

        holder.upsTV.text = currentImage.ups
        holder.titleTV.text = currentImage.title
        holder.authorTV.text = currentImage.author
        holder.subredditTV.text = currentImage.subreddit
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide
            .with(holder.itemView.context)
            .load(currentImage.url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    holder.photoView.setImageResource(R.drawable.load_failed)
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .placeholder(R.drawable.loading)
            .into(holder.photoView)
        holder.saveBT.setOnClickListener { downloadFromUrl(currentImage.url, currentImage.title, false) }
        holder.shareBT.setOnClickListener { shareMeme(currentImage.title, holder.photoView) }
        holder.starrBT.setOnClickListener {
            if (saveToFirestore(items[position])) { holder.starrBT.visibility = View.GONE }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun clear() { items.clear() }

    fun appendMeme(toAppendMemes: ArrayList<Meme>) {
        items.addAll(toAppendMemes)
        notifyDataSetChanged()
    }

    fun appendSingleMeme(updatedMeme:Meme) {
        items.add(updatedMeme)
        notifyDataSetChanged()
    }

    fun shareMeme(title: String, imageView: ImageView) {

        try {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val cachePath = File(context.cacheDir, "imageview")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.jpg") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()
        }  catch ( e:IOException ) {
            e.printStackTrace()
        }

        val imagePath = File(context.cacheDir, "imageview")
        val newFile = File(imagePath, "image.jpg")
        val contentUri = FileProvider.getUriForFile(
            this.context,
            "com.example.dankmemes2.fileprovider",
            newFile
        )

        if (contentUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            shareIntent.type = "image/jpeg"
            context.startActivity(Intent.createChooser(shareIntent,"Choose an app"))
        }
    }

    fun downloadFromUrl(url: String, title: String, silent: Boolean): String  {
        val request = DownloadManager.Request(url.toUri())
            .setTitle("Dank Memes")
            .setDescription("Downloading Meme")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                File.separator + "DankMemes" + File.separator + "${System.currentTimeMillis()}${url.subSequence(url.length-4, url.length)}")
            .setAllowedOverMetered(true)

        val fileName = "$title - DankMemes${url.subSequence(url.length-4, url.length)}"


        val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = try {
            manager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!silent) {
            Toast.makeText(context, "Image is being saved", Toast.LENGTH_SHORT).show()
        }

        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == downloadId && !silent) {
                    Toast.makeText(context, "Meme Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return fileName
    }

    fun saveToFirestore(meme: Meme): Boolean {
        var onSuccess: Boolean = false
        val db = FirebaseFirestore.getInstance()
        val memeToGo: MutableMap<String, Any> = HashMap()
        memeToGo["url"] = meme.url
        memeToGo["preview"] = meme.preview
        memeToGo["title"] = meme.title
        memeToGo["subreddit"] = meme.subreddit
        memeToGo["author"] = meme.author
        memeToGo["ups"] = meme.ups

        db.collection("starrMemes")
            .document(meme.url.substring(18))
            .set(memeToGo)
            .addOnSuccessListener {
                Toast.makeText(context, "Added to super Dank DB", Toast.LENGTH_SHORT).show()
                onSuccess = true
            }
            .addOnFailureListener{
                Toast.makeText(context, "Tell the idiot who made this to update his DB access keys", Toast.LENGTH_LONG).show()
                onSuccess = false
            }
        return onSuccess
    }

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: ImageView = itemView.findViewById(R.id.imageView)
        val upsTV: TextView = itemView.findViewById(R.id.upsTV)
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val authorTV: TextView = itemView.findViewById(R.id.authorTV)
        val subredditTV: TextView = itemView.findViewById(R.id.subredditTV)
        val saveBT: Button = itemView.findViewById(R.id.saveBT)
        val shareBT: Button = itemView.findViewById(R.id.shareBT)
        val starrBT: Button = itemView.findViewById(R.id.starrBT)
    }

}
