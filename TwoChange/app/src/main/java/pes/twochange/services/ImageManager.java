package pes.twochange.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import pes.twochange.R;

public class ImageManager {

    private static final ImageManager ourInstance = new ImageManager();

    public static ImageManager getInstance() {
        return ourInstance;
    }

    private ImageManager() {
    }

    public void putImageIntoView(String completePath, final Context context, final ImageView imageView) {
        getDownloadUrl(
                completePath,
                new UrlResponse() {
                    @Override
                    public void onSuccess(String url) {
                        Picasso.with(context).load(url).placeholder(R.drawable.progress_animation)
                                .error(R.mipmap.placeholder).into(imageView);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // Give the path of the image (for example: test/tom.jpg) and a callback
    // onSuccess(String url) gives the URL to download the image
    // onFailure(String errorMessage) gives the message of the exception if there's an error
    private void getDownloadUrl(String completePath, final UrlResponse urlResponse) {
        FirebaseStorage.getInstance().getReference().child(completePath)
                .getDownloadUrl()
                .addOnSuccessListener(
                        new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlResponse.onSuccess(uri.toString());
                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                urlResponse.onFailure(e.getMessage());
                            }
                        }
                );
    }

    interface UrlResponse {
        void onSuccess(String url);
        void onFailure(String errorMessage);
    }

    private void storeImage(String completePath, Bitmap bitmap) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child(completePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageReference.putBytes(data);
    }

    public void storeImage(String completePath, Uri fileUri) {
//        Bitmap bitmap = compressBitmap(fileUri, context);
//        storeImage(completePath, bitmap);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child(completePath);
        imageReference.putFile(fileUri);
    }

    public void storeImage(String completePath, Uri fileUri, Context context) {
//        Bitmap bitmap = compressBitmap(fileUri, context);
//        storeImage(completePath, bitmap);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child(completePath);
        imageReference.putFile(fileUri);
    }

//    private Bitmap compressBitmap(Uri fileUri, Context context) {
//        Bitmap bitmapImage = BitmapFactory.decodeFile(fileUri.getPath());
//        InputStream imageStream = null;
//        try {
//            imageStream = context.getContentResolver().openInputStream(
//                    fileUri);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        try {
//            stream.close();
//            stream = null;
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        return bmp;
//        Bitmap bitmapImage = BitmapFactory.decodeFile(fileUri.getPath());
//        int nh = (int) (bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()));
//        return Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
//    }

}
