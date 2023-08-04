package com.empire.strivefurniture.ui.itemPages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empire.strivefurniture.R
import com.empire.strivefurniture.adapters.ImageAdapter
import com.empire.strivefurniture.databinding.FragmentAddItemBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.models.User
import com.empire.strivefurniture.utils.FirebaseUtils.furnitureItemDatabasePaths
import com.empire.strivefurniture.utils.FirebaseUtils.furnitureItemStoragePaths
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddItemFragment : Fragment() {
    private lateinit var binding: FragmentAddItemBinding
    private var user: User? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var imageUri: Uri? = null
    private val args: AddItemFragmentArgs by navArgs()
    private var imageDownloadUrl: String = ""

    //Image Picker variables
    private val selectedImagesList = ArrayList<Uri>()
    private val PICK_IMAGES_REQUEST_CODE = 123
    var position = 0 //Current position of the selected Image

    private lateinit var nextImage: ImageView
    private lateinit var previousImage: ImageView
    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var showSelectedImages: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(layoutInflater)
        initViews()

        /**
         * Initializing the variables of the Image display for the selected images
         */
        nextImage = binding.nextImage
        previousImage  = binding.previousImage
        imageSwitcher = binding.imageSwitcher
        showSelectedImages = binding.showSelectedId

        imageSwitcher.setFactory {
            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView
        }


        showSelectedImages.visibility = View.GONE

        /**
         * Set on next and previous btns click to navigate btn previous and next images selected
         */
        nextImage.setOnClickListener {
            if (position < selectedImagesList.size-1){
                position++
                imageSwitcher.setImageURI(selectedImagesList[position])
            }
            else{
                position = 0
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }

        previousImage.setOnClickListener {
            if (position > 0){
                position--
                imageSwitcher.setImageURI(selectedImagesList[position])
            }
            else{
                position = selectedImagesList.size
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }


        return binding.root
    }

    /**
     * on the result after selecting the Images
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) { // Multiple images selected
                val clipData = data.clipData!!
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImagesList.add(imageUri)
                }
                imageSwitcher.setImageURI(selectedImagesList[0])
                position = 0

            } else if (data?.data != null) { // Single image selected
                val imageUri = data.data!!
                selectedImagesList.add(imageUri)
                //set image to image switcher
                imageSwitcher.setImageURI(selectedImagesList[0])
                position = 0
            }
            //Check if there is any selected Image(s) and the show the switcher
            if (selectedImagesList.size > 0){
                showSelectedImages.visibility = View.VISIBLE
            }
        }
    }

    private fun initViews() {
        binding.apply {
            if (args.isEditing) {
                name.setText(args.item!!.name)
                price.setText(args.item!!.price)
                description.setText(args.item!!.description)

                uploadButton.text = "Update"
                toolbar.title = "Update item"
//                Glide.with(requireActivity()).load(args.item!!.photo).into(image)
            }

            uploadButton.setOnClickListener {
                progressIndicator.visibility = View.VISIBLE
                uploadButton.isEnabled = false
                if (checkFields()) uploadItem()
            }

            /**
             * Open a multiple image picker on clicking the image card of Id @image
              */
            imageCard.setOnClickListener {
                openImagePicker()
            }
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    /**
     * The function @openImagePicker below open the gallery for multiple image picking/selecting
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST_CODE)
    }

//    var getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        if (uri != null) {
//            imageUri = uri
//            Glide.with(requireContext()).load(imageUri).into(binding.image)
//        }
//    }

    private fun uploadItem() {
        Toast.makeText(requireContext(), "Uploading... \n Please wait a moment.", Toast.LENGTH_LONG)
            .show()
        Log.d(TAG, "isEditing? ${args.isEditing} ")
        val itemId =
            if (!args.isEditing) "${currentUser?.uid}${System.currentTimeMillis()}" else args.item!!.id

        binding.apply {
            val name = name.text.toString()
            val price = price.text.toString()
            val description = description.text.toString()

            if (user != null) {
                Log.d(TAG, "User is not null")
                if (imageUri != null) {
                    Log.d(TAG, "ImageUri ==> $imageUri")
                    furnitureItemStoragePaths.child(itemId).putFile(imageUri!!)
                        .addOnSuccessListener {
                            it.storage.downloadUrl.addOnSuccessListener {
                                imageDownloadUrl = it.toString()
                                Log.d(TAG, "ImageDownloadUrl ==> $imageDownloadUrl ")

                                //adding item

                                if (imageDownloadUrl.isNotEmpty() || args.item?.photo != null) {

                                    val item = FurnitureItem(
                                        id = itemId,
                                        name = name,
                                        price = price,
                                        location = user!!.location,
                                        seller = user!!.id,
                                        description = description,
                                        photo = imageDownloadUrl.ifEmpty { args.item!!.photo },
                                        contact = user!!.phoneNumber,
                                        sellerName = user!!.name,
                                        uploadTime = if (args.isEditing) args.item!!.uploadTime else System.currentTimeMillis()
                                            .toString()
                                    )

                                    furnitureItemDatabasePaths.child(itemId)
                                        .setValue(item).addOnSuccessListener {
                                            progressIndicator.visibility = View.GONE
                                            Log.d(TAG, "Item upload successful : $item")
                                            uploadButton.isEnabled = true

                                            Toast.makeText(
                                                requireActivity(),
                                                "Item upload successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(
                                                R.id.action_addItemFragment_to_homeFragment
                                            )
                                        }.addOnFailureListener {
                                            progressIndicator.visibility = View.GONE
                                            uploadButton.isEnabled = true
                                            Log.d(TAG, "Upload failed: ${it.message}")
                                            Toast.makeText(
                                                requireContext(),
                                                "Item upload failed. Please check your network and try again.",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                } else Log.d(
                                    TAG,
                                    "ImageDownloadUrl ==> $imageDownloadUrl or ItemPhoto ==> ${args.item?.photo}"
                                )
                            }
                        }
                } else Log.d(TAG, "ImageUri is null")

            } else Log.d(TAG, "User is null")

        }
    }

    private fun checkFields(): Boolean {
        /*binding.bookTitleTv.error = null
        binding.bookAuthorTv.error = null
        binding.bookDescrTv.error = null
        binding.bookDateTv.error = null
        binding.linkTv.error = null*/

        /*if (TextUtils.isEmpty(binding.bookTitleTv.text.toString())) {
            binding.bookTitleTv.setText("")
            binding.bookTitleV.error = "Book title can't be empty"
            return false
        }
        if (TextUtils.isEmpty(binding.bookAuthorTv.text.toString())) {
            binding.bookAuthorTv.setText("")
            binding.bookAuthorV.error = "Book author cant be empty"
            return false
        }
        if (TextUtils.isEmpty(binding.bookDescrTv.text.toString())) {
            binding.bookDescrTv.setText("")
            binding.bookDescrV.error = "Description cant be empty"
            return false
        }
        if (TextUtils.isEmpty(binding.linkTv.text.toString())) {
            binding.linkTv.setText("")
            binding.linkV.error = "Link cant be empty"
            return false
        }*/
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseDatabase.getInstance().getReference("Users").child(currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                    Log.d(TAG, "User is : $user")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Database error, failed to get user: ${error.message}")
                }
            })
    }

    companion object {
        const val TAG = "AddItemFragment"
    }
}