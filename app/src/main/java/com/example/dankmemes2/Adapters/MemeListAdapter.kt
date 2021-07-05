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
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.dankmemes2.Fragments.MainFragment
import com.example.dankmemes2.DataClasses.Meme
import com.example.dankmemes2.R
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream

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
        holder.titleTV.text = if (currentImage.title.length < 30) currentImage.title else (currentImage.title.subSequence(0, 25).toString()+"...")
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
                    holder.imageView.setImageResource(R.drawable.load_failed)
                    return false
                }
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .placeholder(R.drawable.loading)
            .into(holder.imageView)
        holder.saveBT.setOnClickListener { downloadFromUrl(currentImage.url, currentImage.title, false) }
        holder.shareBT.setOnClickListener { shareMeme(currentImage.title, holder.imageView) }
        holder.starrBT.setOnClickListener { saveToFirestore(items[position]) }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun clear() { items.clear() }

    fun updateMeme(updatedMemes: ArrayList<Meme>) {
        items.addAll(updatedMemes)
        notifyDataSetChanged()
    }

    fun shareMeme(title: String, imageView: ImageView) {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport){
                    if (p0.areAllPermissionsGranted()) {
                        val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap

                        val sdCard = Environment.getExternalStorageDirectory().toString()
                        val dir = File(sdCard + "/SharedDankMemes")
                        dir.mkdirs()
                        val fileName = String.format("%d.jpg", System.currentTimeMillis())
                        val outFile = File(dir, fileName)
                        val outStream = FileOutputStream(outFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                        outStream.flush()
                        outStream.close()

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/jpeg"
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outFile.toString()))
                        val chooser = Intent.createChooser(intent, "🤣")
                        context.startActivity(chooser)
                    }
                    else {
                        Toast.makeText(context, "Storage permission required", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
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

    fun saveToFirestore(meme: Meme) {
        val db = FirebaseFirestore.getInstance()
        val memeToGo: MutableMap<String, Any> = HashMap()
        memeToGo["url"] = meme.url
        memeToGo["ups"] = meme.ups
        memeToGo["title"] = meme.title
        memeToGo["subreddit"] = meme.subreddit
        memeToGo["author"] = meme.author

        db.collection("starrMemes")
            .add(memeToGo)
            .addOnSuccessListener {
                Toast.makeText(context, "Added to super Dank DB", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context, "Tell the idiot who made this to update his DB access keys", Toast.LENGTH_LONG).show()
            }
    }

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val upsTV: TextView = itemView.findViewById(R.id.upsTV)
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val authorTV: TextView = itemView.findViewById(R.id.authorTV)
        val subredditTV: TextView = itemView.findViewById(R.id.subredditTV)
        val saveBT: Button = itemView.findViewById(R.id.saveBT)
        val shareBT: Button = itemView.findViewById(R.id.shareBT)
        val starrBT: Button = itemView.findViewById(R.id.starrBT)

    }

}
