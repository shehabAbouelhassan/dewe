package com.eatuitive.nutrition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eatuitive.nutrition.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.eatuitive.nutrition.adapters.AdapterComments;
import com.eatuitive.nutrition.models.ModelComment;

/* @Created by shehab September --- November 2020*/

public class PostDetailActivity extends AppCompatActivity {

    ProgressDialog pd;

    //to get detail of user and post
    String myUid, hisUid, myEmail, myName, myDp, postId, pLikes, hisDp, hisName, pImage;

    //views
    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTitleTv, pTimeTv, pDescriptionTv, pLikesTv, pCommentsTv;
    ImageButton moreBtn;
    Button likeBtn, shareBtn;

    LinearLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComment> commentList;
    AdapterComments adapterComments;

    //add comments views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;

    ActionBar actionBar;

    boolean mProcessLike = false;
    boolean mProcessComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Actionbar and its properties
        actionBar = getSupportActionBar();
        actionBar.setTitle("Post Detail");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //to get id of post using intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        //init views
        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameIV);
        pTitleTv = findViewById(R.id.pTitleTv);
        pTimeTv = findViewById(R.id.pTimeIV);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);

        loadPostInfo();

        checkUserStatus();

        loadUsersInfo();

        setLikes();

        loadComments();

        //set subtitle of actionbar
        actionBar.setSubtitle("SignedIn as: " + myEmail);

        //send comment button click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likePost();
            }
        });

        //more button click handle
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share button click handle
                showMoreOptions();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pTitle = pTitleTv.getText().toString().trim();
                String pDescription = pDescriptionTv.getText().toString().trim();

                BitmapDrawable bitmapDrawable = (BitmapDrawable)pImageIv.getDrawable();
                if (bitmapDrawable == null) {

                    shareTextOnly(pTitle, pDescription);
                }
                else{

                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescription, bitmap);
                }

            }
        });

        //click like count to start PostLikeActivity, and pass the post id
        pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostLikedByActivity.class);
                intent.putExtra("postId", postId);
               startActivity(intent);
            }
        });


    }

    private void addToHisNotifications(String hisUid, String pId, String notification){
        //timestamp for time and notification id
        String timestamp = ""+ System.currentTimeMillis();

        //data to put in notification in firebase
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", myUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added successfully


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {

        String shareBody = pTitle +"\n"+ pDescription;

        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        startActivity(Intent.createChooser(sIntent, "Share via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_images.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.eatuitive.nutrition.fileprovider", file);

        } catch (Exception e) {
            Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String pTitle, String pDescription) {
        //concatenate title and description to share
        String shareBody = pTitle +"\n"+ pDescription;

        //share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");//in case you share via email
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);//text to share
        startActivity(Intent.createChooser(sIntent, "Share via"));//message to show in share dialog
    }

    private void loadComments() {
        //layout(Linear) for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comments list
        commentList = new ArrayList<>();

        //path of the post, to get it's comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    commentList.add(modelComment);
                    // pass myUid and postId as parameter of constructor of comment Adapter
                    //setup adapter
                    adapterComments = new AdapterComments(getApplicationContext(), commentList, myUid, postId);
                    //set adapter
                    recyclerView.setAdapter(adapterComments);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions() {
        //creating popup menu currently having option Delete, we
        PopupMenu popupMenu = new PopupMenu(this, moreBtn, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if(hisUid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == 0){

                    beginDelete();

                }else if (id == 1) {
                    //Edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);

                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete() {
        //post can be with/without image

        if(pImage.equals("noImage")){
            //post without image
            deleteWithoutImage();
        }else{
            //post with image
            deleteWithImage();
        }
    }

    private void deleteWithImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");

        /*Steps:
        *1)Delete image using url
        *2)Delete from database using post id  */

        StorageReference picRed = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRed.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();//remove values from firebase where pid matches
                                }
                                //deleated
                                Toast.makeText(PostDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed, can't go further
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Deleting...");
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(PostDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        //when the details of post is loading, also check if current user has liked it or not
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postId).hasChild(myUid)){
                    //user has liked this post
                    /*To indicate that post is liked by this(signed-in) user
                    change drawable left icon  of like button
                    change text of like button from "like" to "liked"
                     */

                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked,0, 0, 0);
                    likeBtn.setText("Liked");
                }else{
                    //user has  not  liked this post
                    /*To indicate that post is liked by this(signed-in) user
                    change drawable left icon  of like button
                    change text of like button from "like" to "liked"
                     */
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black,0, 0, 0);
                    likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        //get total number of likes for the post, whose like button clicked
        //if currently signed_in user as not liked it before
        //increase value by1, otherwise decrease it by 1

        mProcessLike = true;
        //get id of the post clicked
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike) {

                    if (dataSnapshot.child(postId).hasChild(myUid)) {
                        //already liked, so remove like
                        postsRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;


                    } else {
                        // not liked, like it
                        postsRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 1));
                        likesRef.child(postId).child(myUid).setValue("Likes");
                        mProcessLike = false;

                        addToHisNotifications(""+hisUid, ""+postId, "Liked your post.");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding comment...");

        //get data from comment edit text
        final String comment  = commentEt.getText().toString().trim();

        //validate
        if(TextUtils.isEmpty(comment)){
            //no value is entered
            Toast.makeText(this, "Add a comment...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());

        //each post will have a child "comments" that will contain comments of that post

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashMap
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //added
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "comment added", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();

                        addToHisNotifications(""+hisUid, ""+postId, "Commented on your post.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed, not added
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateCommentCount() {
        //whenever user adds comment increase the comment count as we did for like count

        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessComment) {
                    String comments = ""+ dataSnapshot.child("pComments").getValue();
                    int newCommentBal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentBal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUsersInfo() {
        //get current user info
        Query query = FirebaseDatabase.getInstance().getReference("Users");
        query.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    myName = ""+ ds.child("name").getValue();
                    myDp = ""+ds.child("image").getValue();

                    //set data
                    try {
                        //if image is received then set
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_default).into(cAvatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();

        }else{
            // user not signed in, go to main activity
            startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            finish();
        }
    }

    private void loadPostInfo() {
        //get post using th id of the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //keep checking the posts until get th required post
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get data
                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDescription = ""+ds.child("pDescr").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    String hisUid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String commentCount = ""+ds.child("pComments").getValue();

                    //convert timestamp to dd/mm/yyy hh:mm am/pm
                    Calendar calender = Calendar.getInstance(Locale.getDefault());
                    calender.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime =  DateFormat.format("dd/MM/yyyy hh:mm aa", calender).toString();

                    //set data
                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescription);
                    pLikesTv.setText(pLikes + " Likes");
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount + " Comments");
                    pCommentsTv.setText(commentCount + " Commentssen");

                    uNameTv.setText(hisName);

                    //set image of the user who posted
                    //if there is no image i.e pImage.equals("noImage") then hide ImageView
                    if(pImage.equals("noImage")){
                        //hide imageView
                        pImageIv.setVisibility(View.GONE);
                    }else{
                        //show imageview
                        pImageIv.setVisibility(View.VISIBLE);

                        try {
                            Picasso.get().load(pImage).into(pImageIv);
                        }
                        catch (Exception e){

                        }
                    }

                    try {

                        //set user image in comment part
                        Picasso.get().load(hisDp).placeholder(R.drawable.ic_default).into(uPictureIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default).into(uPictureIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //hide some menu items

        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //get item id
        int id = item.getItemId();
        if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
