package com.eatuitive.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.eatuitive.nutrition.R;

/* @Created by shehab September --- November 2020*/


public class MainActivity extends AppCompatActivity {

    //views
    Button mRegisterBtn, mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);

        //handle register button click
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start Register Activity
                startActivity(new Intent(com.eatuitive.nutrition.MainActivity.this, RegisterActivity.class));
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(com.eatuitive.nutrition.MainActivity.this, LoginActivity.class));
            }
        });
    }
}


/*In this part(01) we will do the followings
01-Add internet permission (required for firebase)
02-Add Register and Login Buttons in MainActivity
03-Create RegisterActivity
04-Create Firebase Project and connect app with that project
05-Check google-services.json file to make sure app is connected with firebase
06-User Registration using Email & Password
07-Create ProfileActivity
08-Go to Profile Activity After Register/Login


In this Part(02):
01-Make ProfileAcivity Launcher
02-On app start Check if user signed in stay in ProfileAcivity otherwise go to MainActivity
03-Create Login Activity
04-Login User with Email/Password
05-After LoggingIn go to ProfileAcivity
06-Add options menu for adding Logout Option
07-After LoggingOut go to MainActivity


In this Part(03):
01- Add Recover Password option in LoginActivity
02- Clicking Forgot password will show a dialog containing
       TextView(as label)
       EditText(input email)
       Button(will send recover password instructions) to your email
03- Little Improvements


In this Part(04):
Add Google Sign-In feature
 Requirements:
 Enable Google sign-in from Firebase Authentication
 Add Project support email
 Add SHA-1 Certificate
 Add Google Sign-In library


In this Part(05):
 * 01- Save registered user's info(name, email, uid, phone, image)
 *     in firebase realtime database.
 *     Requirements:
 *     i)  Add Firebase Realtime database
 *     ii) Change firebase realtime database rules
 * 02- Add Bottom Navigation in Profile Activity having three menus
 *     i)   Home
 *     ii)  Profile (User info like name, email, uid, phone, image)
 *     iii) All Users*/

/*In this Part(06):
 * Design user profile
 * Get user info from firebase
 * Show user info

In this Part(07):
        --Edit Profile:
        1) Name
        2) Phone
        3) Profile Picture
        4) Cover Photo
        --Requirements:
        1) Camera & Storage Permissions (to pick image from camera or gallery)
        2) Firebase Storage libraries (to upload & receive profile/cover photo)
        --UI Update:
        1) Add ImageView (for cover photo)
        2) Add FloatingActionButton to show dialog containing options to Edit Profile
        3) Add Default Image for Profile Picture*/


/*In this Part(08):
        -Show Users from Firebase Realtime database in RecyclerView
        We will show following info of each user:
        1) Profile Picture: show in CircularImageView[library]
        2) Name
        3) Email

        Add CardView Library
        Add RecyclerView Library
        Add CircularImageView Library

        Design Row(=row_user.xml) for RecyclerView



 Search(Case Insensitive) User from User List
1) By Name
2) By Email

firstly Add SearchView in menu_main.xml
Secondly Move menu from Dashboard activity to each fragment

In this Part(10):
»Design Chat Activity [Create new Empty Activity]
»Toolbar will contain receiver icon, name and status like online/offline.
»Add new Fragment For Chats List
»Add this fragment to BottomNavigation

In this Part(11):
»Show Receiver profile picture and name in toolbar
»Send message to any user

First Add Firebase auth with logout option in chatActivity too


In this Part(12):
Show sent messages
-Design different layouts for sender and receiver
-I'll use custom background for sender and receiver
-Receiver Layout(on left) will contain profile image, message and time
-Sender Layout(on right) will contain message and time


In this Part(13):
-Show Online/LastSeen status of user in toolbar of chat activity
 When user Register, add one more key named onlineStatus having value online
 Create method that will set the value on onlineStatus either online or Last Seen at...
 Call this method in onStart with value online
 Call this method in onPause with value as timestamp
 Get the value if it is online simply show otherwise convert timestamp to time

In this Part(14):
 * Show typing status of user in toolbar of chat activity
 * When user Register, add one more key named typingTo having value receiver's UID
 * Add textChange listener to edit text
 * EditText not empty means the user is typing something
 *   so set its value like receiver's uid
 * EditText empty means the user is not typing
 *   so set its value like noOne


*In this Part(15):
 * -Delete Message
 *   Who can delete what?
 *   1) Sender can delete only his own message  OR
 *   2) Sender can delete his and receiver's message
 *   After clicking delete what will happen?
 *   1) Remove message  OR
 *   2) Update value of message to "This message was deleted..."

 In this Part(16):
1) Send message notification using FCM (Firebase Cloud Messaging).
2) When the user sends a message, the receiver will receive the notification containing the message.
3) When the user clicks the notification it will open the chat with that person.



Publish a post to firebase.
 *   Post will contain user name, email, uid, dp, time of publishing, title description, image, etc
 *   User can publish a post with and without image
 *   Create AddPostActivity
 *   Add another option in action bar to go to AddPostActivity
 *   Image can be imported from gallery or taken from the camera

 In this Part(18)
 Show and Search Posts:
Show All posts in RecyclerView and search in SearchView by title/description of the post.
 Search is not case sensitive. You can even search for words in between phrase/paragraph.

 In this Part(19)
 -> Show user specific posts
   SigningIn user's post will be displayed in profileFragment
   Other user's post will be displayed in thereProfileActivity

   Changes in User's List:
   1) Click any user to display dialog having two options
       1)Chat: Go to chat activity to chat with that person
       2)Profile: See profile of that person
   Changes in PostList
   1) Click top of any post to show profile of the user of post
   Changes in ChatActivity
   1) Hide AddPost icon from toolbar


Delete Posts(20):
=>Delete Post:
Create Pop Menu
When UserClicks 3-dot button in any post, it will show a pop up menu having option Delete
The delete option will be shown only to the posts of current user
When delete button is clicked that post will be deleted

Logic:
Will compare current user's uid with the uid in posts
where both uids mathces, means that post is of current user

will be implemented in AdapterPosts

Part(21)
==>Edit Post:
 Add Option "Edit" in Popup Menu
 We will create another activity for editing post, rather we will AddPostActivity  to edit post.
 Clicking "Edit" will start AddPostActivity with a key "editPost".
 Whenever AddPostActivity starts it checks if it has intent string extra "editPost" then will
 do Edit Post task otherwise Add New Post tasks

 Part(22)
 ==> Like Post:
 Add "like Post" feature in this app
 Add another key (pLikes) in each post, that contains the total number of likes
 Add button for Liked post
 How database(of likes) will look like??
   -Likes
    -PostID
      UID (of signed in user):Liked
       UID (of signed in user):Liked
        UID (of signed in user):Liked
     ++PostID
     ++PotID
     ++PostID



 Part(23):
 => Add comment to any post
 => For this we will open the post in anew activity,
    where comments list, add comment will be available



    In this Part(24):
-Show Comments
-Will use recyclerview to show comments
-Each comment will contain
 Name of user (who posted the comment)
 Profile Picture of user (who posted the comment)
 The actual content of the comment

 In this Part(25):
-Delete Comments
-User could delete only his own comment
   We have saved the UID of the user(who added comment) in comment
   So the UID of the comment is equal to the UID of current user, it means
   This comment is by currently signed-in user, so he can delete this comment
   on this basis.
-Click comment to show delete that comment dialog.
User can delete only his own comment on any post.



In this Part(26):
-Show ChatList
 User Name,
 User Profile Image,
  Last Message,
   Online/Offline status


In this Part(27):
Share posts
  It will requires FileProvider class to share  images
  Creating folder "xml" and resource file paths.xml to define the path to be used
  for file sharing



  -Use Volley instead of Retrofit for FCM(Chat Notifications)


  In this Part(29):
1) Solve Issue: null Likes null Comments:
 Cause: we haven't added (any value for ) "pLikes", "pComments" while adding post.
 Solution: Add an initial value of "pLikes" and "pComments" to "0".

2) Send Image/Photo in chat
   Change chat row and ChatActivity UI
   Check storage/camera permission
   Pick image from camera/gallery
   Upload to firebase
   Add another field in ModelChat i.e "type"
     When message is text, then this "type" will  have value "text"
     When message is image, then this "type" will have value "image"



In this Part(30):
Post Notifications (using FCM Topic Messaging).
We will create a Settings screen where the user can enable/disable post notifications.
 When a user clicks the notification it will open the details of that post.
==> Required Libraries
Volley  {Already added}
Firebase cloud messaging {Already added}



n this part(31) we will do the followings
01- Block User/Unblock User
User will be able block/unblock any user from UserList and in Chat
Blocked/Unblocked icon will be also be displayed to show current status
The block user will not be able to send message(to the receiver who have blocked)
02- Update Libraries


In this part(32) we will do the followings
01- Implement Firebase Offline Capabilities
02- Receive Data from other apps, For example, if you share text or image from other apps, this app will also be able to receive and share that data within the app as Post.
 just as an app can send data to other apps, it can also receive data from other apps as well
  >For example when user select text/image in other apps and presses share button, our app will also
  be shown in the list of apps that can receive data

  >Data will be received(Text ot Image) and Publish as Post.

  ##//adding firebase offline feature

   Application class
   --This class is primarily used for initialization of global state before the first Activity is displayed

   Few acceptable uses of a custom application class:
   Specialized tasks that need to run before the creation of first activity.
   Global initialization that needs to be shared across all components(crash reporting, persistence).
   Static methods for easy access access to static immutable data such as shared network client object.

 **since we have to add firebase offline feature before using anywhere, that's why we are doing in a class that extends
 with Application class.##

 In this part(33) we will do the followings
01- Add Notification Screen tab in Bottom menu . All notifications:
       When someone likes/comments your post.
02- Click notification to open the post
03- Long click to delete notification


In this part(34)
1) Show the list of users who liked the post
    Click likes count of a Post to show list of users who liked that post






 */

